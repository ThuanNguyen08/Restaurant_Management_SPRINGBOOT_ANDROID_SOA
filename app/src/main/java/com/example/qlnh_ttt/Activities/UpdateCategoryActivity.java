package com.example.qlnh_ttt.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlnh_ttt.R;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateCategoryActivity extends AppCompatActivity {

    private EditText edtCategoryName;
    private Button btnUpdateCategory;
    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_danhmuc_layout);

        edtCategoryName = findViewById(R.id.edtCategoryName);
        btnUpdateCategory = findViewById(R.id.btnUpdateCategory);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        categoryId = intent.getIntExtra("category_id", -1);
        String categoryName = intent.getStringExtra("category_name");
        edtCategoryName.setText(categoryName);

        // Sự kiện nút cập nhật
        btnUpdateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCategory();
            }
        });
    }

    private void updateCategory() {
        String newCategoryName = edtCategoryName.getText().toString().trim();

        if (newCategoryName.isEmpty()) {
            Toast.makeText(this, "Tên danh mục không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                URL url = new URL("http://172.16.1.2:8083/api/v1/dmFood/add");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonBody = "{\"categoryName\":\"" + newCategoryName + "\"}";

//                JSONObject newCategory = new JSONObject();
//                newCategory.put("categoryName", newCategoryName);


                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(jsonBody);
                writer.flush();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        Toast.makeText(UpdateCategoryActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    throw new Exception("Server returned code: " + responseCode);
                }

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(UpdateCategoryActivity.this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
