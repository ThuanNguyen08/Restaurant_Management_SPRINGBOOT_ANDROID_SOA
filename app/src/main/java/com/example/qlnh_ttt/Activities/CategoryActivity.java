package com.example.qlnh_ttt.Activities;

import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlnh_ttt.AccoutType.AccoutType;
import com.example.qlnh_ttt.Adapters.CategoryAdapter;
import com.example.qlnh_ttt.Entities.DmFood;
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

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView rvCategory;
    private Button btnAddDanhMuc, btnAddThucDon;
    private ArrayList<DmFood> categoryList;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewcategory_layout);
        categoryList = new ArrayList<>();
        CheckAccount();
        initViews();
        setupRecyclerView();
        loadCategoryList();

        // Sự kiện nhấn "Thêm danh mục"
        btnAddDanhMuc.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this, AddCategoryActivity.class);
            startActivity(intent);
        });


        // Sự kiện nhấn "Thêm thực đơn"
        btnAddThucDon.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this,AddFoodActivity.class);
            startActivity(intent);
        });
    }

    // ánh xạ các phần tử view
    private void initViews() {
        rvCategory = findViewById(R.id.rvCategory);
        btnAddDanhMuc = findViewById(R.id.btnAddDanhMuc);
        btnAddThucDon = findViewById(R.id.btnAddThucDon);

    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter(this, categoryList, new CategoryAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(DmFood category, int position) {
                // Hiện dialog xác nhận xóa
                new AlertDialog.Builder(CategoryActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa danh mục này?")
                        .setPositiveButton("Xóa", (dialog, which) -> deleteCategory(category, position))
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        rvCategory.setLayoutManager(new LinearLayoutManager(this));
        rvCategory.setAdapter(categoryAdapter);
    }


    private void loadCategoryList() {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                URL url = new URL("http://172.16.1.2:8082/api/v1/dmFood");
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
                    List<DmFood> newCategoryList = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        DmFood dmFood = new DmFood();
                        dmFood.setDmFoodId(jsonObject.getInt("dmFoodId"));
                        dmFood.setCategoryName(jsonObject.getString("categoryName"));

                        newCategoryList.add(dmFood);
                    }

                    runOnUiThread(() -> {
                        categoryList.clear();
                        categoryList.addAll(newCategoryList);
                        categoryAdapter.notifyDataSetChanged();
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
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi tải danh sách món ăn", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }


    private void deleteCategory(DmFood category, int position) {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                URL url = new URL("http://172.16.1.2:8082/api/v1/dmFood/" + category.getDmFoodId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    runOnUiThread(() -> {
                        categoryAdapter.removeItem(position);
                        Toast.makeText(CategoryActivity.this,
                                "Đã xóa danh mục thành công", Toast.LENGTH_SHORT).show();
                    });
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    throw new IOException("Server returned code: " + responseCode);
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(CategoryActivity.this,
                            "Lỗi khi xóa danh mục: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (categoryList != null) {
            loadCategoryList();
        }
    }

    public void CheckAccount(){
        new Thread(() -> {
            boolean isUser = AccoutType.isUser(CategoryActivity.this);

            runOnUiThread(() -> {
                if(isUser){
                    btnAddDanhMuc.setVisibility(View.GONE);
                    btnAddThucDon.setVisibility(View.GONE);
                } else {
                    btnAddDanhMuc.setVisibility(View.VISIBLE);
                    btnAddThucDon.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }

}