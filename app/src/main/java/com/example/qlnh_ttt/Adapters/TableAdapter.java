package com.example.qlnh_ttt.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qlnh_ttt.Activities.FoodListActivity;
import com.example.qlnh_ttt.Activities.OrderFoodActivity;
import com.example.qlnh_ttt.Activities.PaymentActivity;
import com.example.qlnh_ttt.Entities.Table;
import com.example.qlnh_ttt.R;

import java.util.ArrayList;

public class TableAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Table> tableList;

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

        });

        view.setOnClickListener(v -> {
            String trangthai;
            if(table.getStatus().equalsIgnoreCase("OCCUPIED")) {
                trangthai = " đang có khách";
            } else {
                trangthai = " đang trống";
            }
            Toast.makeText(context, "Bàn "+ table.getId() + trangthai, Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(context, FoodListActivity.class);
            /*intent.putExtra("table_id", table.getId());
            intent.putExtra("table_name", table.getName());
            intent.putExtra("table_status", table.getStatus());*/
//            context.startActivity(intent);
        });

        return view;
    }
}