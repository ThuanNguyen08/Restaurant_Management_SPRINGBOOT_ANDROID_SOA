package com.example.qlnh_ttt.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qlnh_ttt.R;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UpdateFoodActivity extends AppCompatActivity {
    private EditText etFoodName, etFoodPrice, etFoodCategory;
    private Button btnSave, btnCancel;
    private ImageView imgFood;
    private int foodId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_food_layout);

        initializeViews();
        loadFoodData();
        setupListeners();
    }

    private void initializeViews() {
        etFoodName = findViewById(R.id.etFoodName);
        etFoodPrice = findViewById(R.id.etFoodPrice);
        etFoodCategory = findViewById(R.id.etFoodCategory);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        imgFood = findViewById(R.id.imgFood);

        foodId = getIntent().getIntExtra("food_id", -1);
    }

    private void loadFoodData() {
        String foodName = getIntent().getStringExtra("food_name");
        double foodPrice = getIntent().getDoubleExtra("food_price", 0);
        int foodCategory = getIntent().getIntExtra("food_category", 0);

        etFoodName.setText(foodName);
        etFoodPrice.setText(String.valueOf(foodPrice));
        etFoodCategory.setText(String.valueOf(foodCategory));
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> updateFood());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void updateFood() {
        String name = etFoodName.getText().toString();
        String price = etFoodPrice.getText().toString();
        String category = etFoodCategory.getText().toString();

        if (name.isEmpty() || price.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                URL url = new URL("http://172.16.1.2:8083/api/v1/food/update/" + foodId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);

                JSONObject foodData = new JSONObject();
                foodData.put("foodName", name);
                foodData.put("price", Double.parseDouble(price));
                foodData.put("dmFoodID", Integer.parseInt(category));

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = foodData.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Thời gian đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    });
                } else {
                    throw new Exception("Server returned code: " + responseCode);
                }
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}
