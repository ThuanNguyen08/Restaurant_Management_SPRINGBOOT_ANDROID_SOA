package com.example.qlnh_ttt.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import com.example.qlnh_ttt.R;

public class HomeActivity extends AppCompatActivity {

    private RelativeLayout layoutThongKe, layoutXemBan, layoutXemMenu, layoutXemDanhMuc;
    private TextView  txtViewAllStatistic;
    private ImageView btnLogout;
    private RecyclerView rcvDonTrongNgay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trangchu_layout);

        // Ánh xạ các view
        layoutThongKe = findViewById(R.id.layout_displayhome_ThongKe);
        layoutXemBan = findViewById(R.id.layout_displayhome_XemBan);
        layoutXemMenu = findViewById(R.id.layout_displayhome_XemMenu);
        layoutXemDanhMuc = findViewById(R.id.layout_displayhome_XemDanhMuc);
        txtViewAllStatistic = findViewById(R.id.txt_displayhome_ViewAllStatistic);
        rcvDonTrongNgay = findViewById(R.id.rcv_displayhome_DonTrongNgay);
        btnLogout = findViewById(R.id.btn_logout);

        //sự kiện click cho nút logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // Sự kiện click cho từng layout
        layoutThongKe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang màn hình thống kê
                Intent intent = new Intent(HomeActivity.this, StatisticActivity.class);
                startActivity(intent);
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

        txtViewAllStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang màn hình chi tiết thống kê
                Intent intent = new Intent(HomeActivity.this, StatisticActivity.class);
                startActivity(intent);
            }
        });


        // Thiết lập RecyclerView
        setupRecyclerViews();
    }

    private void logout() {
        //xóa thông tin user
        getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().clear().apply();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void setupRecyclerViews() {

//        // Dữ liệu mẫu cho RecyclerView Đơn Trong Ngày
//        ArrayList<String> donTrongNgayList = new ArrayList<>();
//        donTrongNgayList.add("Đơn 1: 500,000đ");
//        donTrongNgayList.add("Đơn 2: 300,000đ");
//        donTrongNgayList.add("Đơn 3: 700,000đ");
//
//        RecyclerViewAdapter donTrongNgayAdapter = new RecyclerViewAdapter(donTrongNgayList);
//        rcvDonTrongNgay.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        rcvDonTrongNgay.setAdapter(donTrongNgayAdapter);
    }
}