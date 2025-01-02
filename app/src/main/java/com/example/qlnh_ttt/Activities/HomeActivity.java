package com.example.qlnh_ttt.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;
import java.time.LocalDate;

import com.example.qlnh_ttt.Adapters.BillAdapter;
import com.example.qlnh_ttt.Entities.Bill;
import com.example.qlnh_ttt.R;

import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    private RelativeLayout layoutThongKe, layoutXemBan, layoutXemMenu, layoutXemDanhMuc;
    private TextView  txtViewAllStatistic;
    private ImageView btnLogout;
    private RecyclerView rcvDonTrongNgay;

    private TextView txtTotalRevenue, txtBillCount;
    private BillAdapter billAdapter;
    private List<Bill> todayBills = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trangchu_layout);

        // Ánh xạ các view
        layoutXemBan = findViewById(R.id.layout_displayhome_XemBan);
        layoutXemMenu = findViewById(R.id.layout_displayhome_XemMenu);
        layoutXemDanhMuc = findViewById(R.id.layout_displayhome_XemDanhMuc);
        rcvDonTrongNgay = findViewById(R.id.rcv_displayhome_DonTrongNgay);
        btnLogout = findViewById(R.id.btn_logout);

        txtTotalRevenue = findViewById(R.id.txt_total_revenue);
        txtBillCount = findViewById(R.id.txt_bill_count);


        //sự kiện click cho nút logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


        layoutXemBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang màn hình danh sách bàn
                Intent intent = new Intent(HomeActivity.this, TableActivity.class);
                startActivity(intent);
            }
        });

        layoutXemMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang màn hình menu
                Intent intent = new Intent(HomeActivity.this, FoodListActivity.class);
                startActivity(intent);
            }
        });

        layoutXemDanhMuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chuyển sang màn hình danh muc
                Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });



        // Thiết lập RecyclerView
        setupRecyclerViews();

        loadTodayBills();
    }

    private void logout() {
        //xóa thông tin user
        getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().clear().apply();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void setupRecyclerViews() {

        billAdapter = new BillAdapter(todayBills);

        rcvDonTrongNgay.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcvDonTrongNgay.setAdapter(billAdapter);
    }

    private void loadTodayBills() {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                URL url = new URL("http://172.16.1.2:8085/api/v1/bills");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    JSONArray jsonArray = new JSONArray(response.toString());
                    List<Bill> allBills = new ArrayList<>();
                    LocalDate today = LocalDate.now();
                    int totalRevenue = 0;
                    int billCount = 0;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject billJson = jsonArray.getJSONObject(i);

                        // Chuyển đổi kiểu ngày
                        LocalDateTime billDate = LocalDateTime.parse(
                                billJson.getString("billDate"),
                                DateTimeFormatter.ISO_DATE_TIME
                        );

                        // Create Bill object
                        Bill bill = new Bill(
                                billJson.getInt("billID"),
                                billDate,
                                billJson.getInt("userInfoID"),
                                billJson.getInt("tableID"),
                                billJson.getString("status"),
                                billJson.getInt("totalAmount")
                        );

                        // bill đã được thanh toán thì mới thêm vào list
                        if (billDate.toLocalDate().equals(today) &&
                                bill.getStatus().equals("PAID")) {
                            allBills.add(bill);
                            totalRevenue += bill.getTotalAmount();
                            billCount++;
                        }
                    }

                    // Update UI on main thread
                    final int finalTotalRevenue = totalRevenue;
                    final int finalBillCount = billCount;
                    final List<Bill> finalBills = allBills;

                    new Handler(Looper.getMainLooper()).post(() -> {
                        // Update RecyclerView
                        todayBills.clear();
                        todayBills.addAll(finalBills);
                        billAdapter.notifyDataSetChanged();

                        // Update statistics
                        updateStatistics(finalTotalRevenue, finalBillCount);
                    });

//                    runOnUiThread(() -> {
//                        todayBills.clear();
//                        todayBills.addAll(finalBills);
//                        billAdapter.notifyDataSetChanged();
//                        updateStatistics(finalTotalRevenue, finalBillCount);
//                    });
                }
                conn.disconnect();

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(HomeActivity.this,"Ngày hôm nay chưa có hóa đơn nào",Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void updateStatistics(int totalRevenue, int billCount) {
        // Format currency in VND
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedRevenue = formatter.format(totalRevenue);

        // Update UI elements
        txtTotalRevenue.setText(formattedRevenue);
        txtBillCount.setText("Số đơn: " + billCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTodayBills();
    }
}