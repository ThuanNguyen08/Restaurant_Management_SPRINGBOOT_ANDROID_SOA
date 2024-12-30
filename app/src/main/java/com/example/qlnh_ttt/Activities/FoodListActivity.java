package com.example.qlnh_ttt.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlnh_ttt.Adapters.FoodListAdapter;
import com.example.qlnh_ttt.Entities.Food;
import com.example.qlnh_ttt.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FoodListActivity extends AppCompatActivity {
    private RecyclerView rvFoodList;
    private FoodListAdapter adapter;
    private Button btnAddFood;
    private List<Food> foodList;
    private static final String TAG = "FoodListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        initViews();
        setupRecyclerView();
        loadFoodList();

        btnAddFood.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddFoodActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        rvFoodList = findViewById(R.id.rvFoodList);
        btnAddFood = findViewById(R.id.btnAddFood);
        foodList = new ArrayList<Food>();
    }

    private void setupRecyclerView() {
        adapter = new FoodListAdapter(this, foodList);
        rvFoodList.setLayoutManager(new LinearLayoutManager(this));
        rvFoodList.setAdapter(adapter);
    }

    private void loadFoodList() {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                URL url = new URL("http://172.16.1.2:8083/api/v1/food");
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
                    List<Food> newFoodList = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Food food = new Food();
                        food.setFoodID(jsonObject.getInt("foodID"));
                        food.setFoodName(jsonObject.getString("foodName"));
                        food.setDmFoodID(jsonObject.getInt("dmFoodID"));
                        food.setPrice(jsonObject.getString("price"));

                        if (!jsonObject.isNull("avtFood")) {
                            String base64Image = jsonObject.getString("avtFood");
                            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                            food.setAvtFood(imageBytes);
                        }

                        newFoodList.add(food);
                    }

                    runOnUiThread(() -> {
                        foodList.clear();
                        foodList.addAll(newFoodList);
                        adapter.notifyDataSetChanged();
                    });

                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                        // Chuyển về màn hình đăng nhập
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    throw new IOException("Server returned code: " + responseCode);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error loading food list: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi tải danh sách món ăn", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFoodList(); // Reload danh sách khi quay lại màn hình
    }
}