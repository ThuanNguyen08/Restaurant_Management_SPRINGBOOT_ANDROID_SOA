package com.example.qlnh_ttt.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.example.qlnh_ttt.R;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddCategoryActivity extends AppCompatActivity {

    private EditText edtTenDanhMuc;
    private Button btnAddCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.them_danhmuc_layout);  // Đảm bảo sử dụng layout đúng

        // Ánh xạ các View
        edtTenDanhMuc = findViewById(R.id.edtNameDanhMuc);
        btnAddCategory = findViewById(R.id.btn_addcategory);

        // Xử lý sự kiện nhấn nút "Thêm Menu"
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });
    }

    private void addCategory(){
        String categoryName = edtTenDanhMuc.getText().toString().trim();

        // Kiểm tra nếu người dùng chưa nhập tên menu
        if (categoryName.isEmpty()) {
            // Hiển thị thông báo nếu tên menu không hợp lệ
            Toast.makeText(AddCategoryActivity.this, "Vui lòng nhập tên menu!", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {

            try {
                //lay token
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                //check token
                if (token.isEmpty()) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                        goToLogin();
                    });
                    return;
                }


                //json de post
                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("categoryName", categoryName);

                //ket noi api
                URL url = new URL("http://172.16.1.2:8083/api/v1/dmFood/add");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);

                //set kieu ki tu utf8
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonRequest.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                //lay trang thai response
                int responseCode = conn.getResponseCode();

                //check trang thai response
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddCategoryActivity.this,
                                "Thêm danh mục thành công",
                                Toast.LENGTH_SHORT).show();
                        clearForm();
                    });
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddCategoryActivity.this,
                                "Phiên đăng nhập hết hạn",
                                Toast.LENGTH_SHORT).show();
                        goToLogin();
                    });
                } else {
                    throw new IOException("Server returned code: " + responseCode);
                }

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(AddCategoryActivity.this,
                            "Lỗi: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();

    }


    private void clearForm(){
        edtTenDanhMuc.setText("");
    }

    private void goToLogin() {
        Intent intent = new Intent(AddCategoryActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}