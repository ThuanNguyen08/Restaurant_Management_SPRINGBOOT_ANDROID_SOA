package com.example.qlnh_ttt.Activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlnh_ttt.R;

public class ItemCategoryActivity extends AppCompatActivity {
    private TextView txtCategoryName;
    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_category_item);

        txtCategoryName = findViewById(R.id.txtCategoryName);

        // Lấy dữ liệu từ Intent
        String categoryName = getIntent().getStringExtra("category_name");
        categoryId = getIntent().getIntExtra("category_id", -1);

        if (categoryName != null) {
            txtCategoryName.setText(categoryName);
        }

        // Thêm xử lý sự kiện khi click vào category
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Thêm code xử lý sự kiện click vào đây
    }
}