package com.example.qlnh_ttt.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qlnh_ttt.AccoutType.AccoutType;
import com.example.qlnh_ttt.Activities.FoodListActivity;
import com.example.qlnh_ttt.Activities.OrderFoodActivity;
import com.example.qlnh_ttt.Activities.PaymentActivity;
import com.example.qlnh_ttt.Activities.TableActivity;
import com.example.qlnh_ttt.Entities.Table;
import com.example.qlnh_ttt.R;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TableAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Table> tableList;
    private Activity activity;
    public TableAdapter(Context context, ArrayList<Table> tableList) {
        this.context = context;
        this.tableList = tableList;
    }

    @Override
    public int getCount() {
        return tableList.size();
    }

    @Override
    public Object getItem(int position) {
        return tableList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.viewdetailtable_layout, parent, false);
        }

        Table table = tableList.get(position);

        TextView txtTableName = view.findViewById(R.id.txt_customtable_TenBanAn);
        ImageView imgTable = view.findViewById(R.id.img_customtable_BanAn);
        ImageView imgOrder = view.findViewById(R.id.img_GoiMon);
        ImageView imgPay = view.findViewById(R.id.img_ThanhToan);
        ImageView imgDelete = view.findViewById(R.id.img_XoaBan);

        // Check role trong thread riêng
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            boolean isUser = AccoutType.isUser(context);
            handler.post(() -> {
                if(isUser) {
                    imgDelete.setVisibility(View.GONE);
                } else {
                    imgDelete.setVisibility(View.VISIBLE);
                }
            });
        }).start();
        txtTableName.setText(table.getName());



        // set ảnh của bàn với trạng thái status
        if(table.getStatus().equalsIgnoreCase("OCCUPIED")) {
            imgTable.setImageResource(R.drawable.img_table_occupied);
        } else {
            imgTable.setImageResource(R.drawable.img_table_empty);
        }

        imgOrder.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderFoodActivity.class);
            intent.putExtra("table_id", table.getId());
            intent.putExtra("table_name", table.getName());
            intent.putExtra("table_status", table.getStatus());
            context.startActivity(intent);
        });

        imgPay.setOnClickListener(v -> {
            Intent intent = new Intent(context, PaymentActivity.class);
            intent.putExtra("table_id", table.getId());
            intent.putExtra("table_name", table.getName());
            context.startActivity(intent);
        });

        imgDelete.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa " + table.getName() + "?")
                    .setPositiveButton("Xóa", (dialog, id) -> {
                        deleteTable(table.getId(), position);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        view.setOnClickListener(v -> {
            String trangthai;
            if(table.getStatus().equalsIgnoreCase("OCCUPIED")) {
                trangthai = " đang có khách";
            } else {
                trangthai = " đang trống";
            }
            Toast.makeText(context,table.getName() + trangthai, Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(context, FoodListActivity.class);
            /*intent.putExtra("table_id", table.getId());
            intent.putExtra("table_name", table.getName());
            intent.putExtra("table_status", table.getStatus());*/
//            context.startActivity(intent);
        });

        return view;
    }

    private void deleteTable(int tableId, int position) {
        new Thread(() -> {
            try {
                String token = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                        .getString("auth_token", "");

                URL url = new URL("http://172.16.1.2:8084/api/v1/tables/" + tableId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    ((TableActivity) context).runOnUiThread(() -> {
                        tableList.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Xóa bàn thành công", Toast.LENGTH_SHORT).show();
                    });
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    ((TableActivity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "Thời gian đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                        ((TableActivity) context).goToLogin();
                    });
                } else {
                    ((TableActivity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Không thể xóa bàn đang có người ngồi" , Toast.LENGTH_SHORT).show()
                    );
                }
                conn.disconnect();
            } catch (Exception e) {
                ((TableActivity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }




}