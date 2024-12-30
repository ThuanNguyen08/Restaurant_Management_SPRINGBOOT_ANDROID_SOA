package com.example.qlnh_ttt.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qlnh_ttt.Adapters.OrderFoodAdapter;
import com.example.qlnh_ttt.Entities.Food;
import com.example.qlnh_ttt.Entities.BillDetail;
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

public class OrderFoodActivity extends AppCompatActivity {
    private RecyclerView rvFoodOrder;
    private OrderFoodAdapter foodOrderAdapter;
    private ArrayList<Food> foodList;
    private ArrayList<BillDetail> orderItems;
    private TextView txtTableInfo;
    private Button btnConfirmOrder;
    private static final String TAG = "OrderFoodActivity";
    private int tableId;
    private int billId;
    private Integer existingBillId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_layout);

        //ánh xạ các đối tượng giao diện
        initViews();

        // lấy dữ liệu từ intent
        getIntentData();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup confirm button
        setupConfirmButton();

        // Load dữ liệu lên hóa đơn
        loadDataSequence();
    }

    private void initViews() {
        txtTableInfo = findViewById(R.id.txtTableInfo);
        rvFoodOrder = findViewById(R.id.rvFoodOrder);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        foodList = new ArrayList<>();
        orderItems = new ArrayList<>();
    }

    private void getIntentData() {
        tableId = getIntent().getIntExtra("table_id", -1);
        String tableName = getIntent().getStringExtra("table_name");
        txtTableInfo.setText("Bàn " + tableName);
    }

    private void setupRecyclerView() {
        foodOrderAdapter = new OrderFoodAdapter(this, foodList, (food, quantity) -> updateOrderItem(food, quantity));
        rvFoodOrder.setLayoutManager(new LinearLayoutManager(this));
        rvFoodOrder.setAdapter(foodOrderAdapter);
    }

    private void updateOrderItem(Food food, int quantity) {
        BillDetail existingItem = null;
        for (BillDetail item : orderItems) {
            if (item.getFoodID() == food.getFoodID()) {
                existingItem = item;
                break;
            }
        }

        if (quantity > 0) {
            if (existingItem == null) {
                BillDetail newItem = new BillDetail();
                newItem.setBillID(billId);
                newItem.setFoodID(food.getFoodID());
                newItem.setQuantity(quantity);
                newItem.setPrice(Integer.parseInt(food.getPrice()));
                orderItems.add(newItem);
            } else {
                existingItem.setQuantity(quantity);
            }
        } else if (existingItem != null) {
            orderItems.remove(existingItem);
        }
    }

    //gọi phương kiểm tra nếu billid tồn tại thì load những món đã order lên trước.
    private void loadDataSequence() {
        new Thread(() -> {
            try {
                checkExistingBill();
                runOnUiThread(this::loadFoodMenu);
            } catch (Exception e) {
                Log.e(TAG, "Error loading data: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void loadFoodMenu() {
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
                        updateAdapterWithOrderItems();
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

    private void updateAdapterWithOrderItems() {
        for (Food food : foodList) {
            for (BillDetail item : orderItems) {
                if (food.getFoodID() == item.getFoodID()) {
                    foodOrderAdapter.setQuantity(food, item.getQuantity());
                    break;
                }
            }
        }
        foodOrderAdapter.notifyDataSetChanged();
    }


    private void setupConfirmButton() {
        btnConfirmOrder.setOnClickListener(v -> {
            if (orderItems.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn món", Toast.LENGTH_SHORT).show();
                return;
            }
            submitOrder();
        });
    }

    private void submitOrder() {
        if (billId == 0) {
            createNewBill();
        } else {
            submitOrderItems();
        }
    }

    private void createNewBill() {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                URL url = new URL("http://172.16.1.2:8085/api/v1/bills");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Create JSON payload for new bill
                JSONObject billData = new JSONObject();
                billData.put("tableID", tableId);
                billData.put("status", "UNPAID");

                // Write JSON data to connection
                conn.getOutputStream().write(billData.toString().getBytes());

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    billId = jsonResponse.getInt("billID");
                    existingBillId = billId;

                    for (BillDetail item : orderItems) {
                        item.setBillID(billId);
                    }

                    runOnUiThread(this::submitOrderItems);

                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    runOnUiThread(() -> {
                        Toast.makeText(OrderFoodActivity.this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(OrderFoodActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    throw new IOException("Server returned code: " + responseCode);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error creating bill: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(OrderFoodActivity.this, "Lỗi tạo hóa đơn mới", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }


    // cập nhật món ăn cho bàn
    private void submitOrderItems() {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                int successCount = 0;

                for (BillDetail item : orderItems) {
                    URL url = new URL("http://172.16.1.2:8086/api/v1/billdetails/" + item.getBillID() + "/" + item.getFoodID());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    // Gửi newQuantity trong body
                    String newQuantity = String.valueOf(item.getQuantity());
                    conn.getOutputStream().write(newQuantity.getBytes());

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        successCount++;
                    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        runOnUiThread(() -> {
                            Toast.makeText(OrderFoodActivity.this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OrderFoodActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        });
                        return;
                    } else {
                        throw new IOException("Server trả về mã lỗi: " + responseCode + " cho món: " + item.getFoodID());
                    }

                    conn.disconnect();
                }

                final int finalSuccessCount = successCount;
                runOnUiThread(() -> {
                    if (finalSuccessCount == orderItems.size()) {
                        Toast.makeText(OrderFoodActivity.this, "Cập nhật món thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(OrderFoodActivity.this,
                                "Chỉ cập nhật được " + finalSuccessCount + "/" + orderItems.size() + " món",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi cập nhật món: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(OrderFoodActivity.this, "Lỗi khi cập nhật món", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    // kiểm tra bill đã tồn tại chưa
    private void checkExistingBill() {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                // Gọi API để kiểm tra bill hiện tại của bàn
                URL url = new URL("http://172.16.1.2:8085/api/v1/bills/table/" + tableId);
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

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (!jsonResponse.isNull("billID")) {
                        existingBillId = jsonResponse.getInt("billID");
                        billId = existingBillId;

                        // Load existing order items
                        loadExistingOrderItems();
                    }
                }

                // Load food menu after checking bill
                runOnUiThread(this::loadFoodMenu);

            } catch (Exception e) {
                Log.e(TAG, "Error checking existing bill: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi kiểm tra hóa đơn hiện tại", Toast.LENGTH_SHORT).show();
                    loadFoodMenu(); // Still load menu even if check fails
                });
            }
        }).start();
    }

    private void loadExistingOrderItems() {
        if (existingBillId == null) return;

        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                URL url = new URL("http://172.16.1.2:8086/api/v1/billdetails/bill/" + existingBillId);
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
                    List<BillDetail> existingItems = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        BillDetail item = new BillDetail();
                        item.setBillID(jsonObject.getInt("billID"));
                        item.setFoodID(jsonObject.getInt("foodID"));
                        item.setQuantity(jsonObject.getInt("quantity"));
                        item.setPrice(jsonObject.getInt("price"));
                        existingItems.add(item);
                    }

                    runOnUiThread(() -> {
                        orderItems.clear();
                        orderItems.addAll(existingItems);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading existing orders: " + e.getMessage());
            }
        }).start();
    }
}