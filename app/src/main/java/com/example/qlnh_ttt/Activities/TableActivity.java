package com.example.qlnh_ttt.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qlnh_ttt.Adapters.TableAdapter;
import com.example.qlnh_ttt.Entities.Table;
import com.example.qlnh_ttt.R;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TableActivity extends AppCompatActivity {
    private boolean isLoading = false;
    private GridView gvDisplayTable;
    private Button btnAddTable;
    private ArrayList<Table> tableList;
    private TableAdapter adapter;
    private static final String TAG = "TableActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewtable_layout);

        gvDisplayTable = findViewById(R.id.gvDisplayTable);
        btnAddTable = findViewById(R.id.btnAddTable);
        tableList = new ArrayList<>();

        btnAddTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TableActivity.this, AddTableActivity.class);
                startActivity(intent);
            }
        });

        loadTables();
    }



    private void loadTables() {
        if (isLoading) return;
        isLoading = true;
        new Thread(() -> {

            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                if (token.isEmpty()) {
                    runOnUiThread(() -> {
                        Toast.makeText(TableActivity.this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                        goToLogin();
                    });
                    return;
                }

                URL url = new URL("http://172.16.1.2:8084/api/v1/tables");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = connection.getResponseCode();
                Log.d("TableActivity", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    tableList.clear();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    Log.d("TableActivity", "API Response: " + response.toString());

                    JSONArray jsonArray = new JSONArray(response.toString());


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int tableID = jsonObject.getInt("tableID");
                        String tableName = jsonObject.getString("tableName");
                        String status = jsonObject.getString("status");

                        Table table = new Table(tableID, tableName, status);
                        tableList.add(table);
                        Log.d("TableActivity", "Added Table - ID: " + tableID + ", Name: " + tableName + ", Status: " + status);
                    }

                    Log.d("TableActivity", "Total Tables: " + tableList.size());

                    runOnUiThread(() -> {
                        if (!tableList.isEmpty()) {
//                            gvDisplayTable.setAdapter(null);
                            adapter = new TableAdapter(TableActivity.this, tableList);
                            gvDisplayTable.setAdapter(adapter);
                            isLoading = false;
                        } else {
                            Toast.makeText(TableActivity.this, "Không có bàn nào", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    runOnUiThread(() -> {
                        Toast.makeText(TableActivity.this, "Phiên làm việc hết hạn", Toast.LENGTH_SHORT).show();
                        goToLogin();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(TableActivity.this, "Lỗi tải dữ liệu: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                    isLoading = false;
                }

                connection.disconnect();

            } catch (Exception e) {
                Log.e("TableActivity", "Error loading tables: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(TableActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
                isLoading = false;
            }
        }).start();

    }

    public void goToLogin() {
        Intent intent = new Intent(TableActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tableList.clear();
        loadTables(); // Reload tables when returning to this screen
    }

}