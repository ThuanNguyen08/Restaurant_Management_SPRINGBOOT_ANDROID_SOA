package com.example.qlnh_ttt.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qlnh_ttt.AccoutType.AccoutType;
import com.example.qlnh_ttt.Adapters.OrderFoodAdapter;
import com.example.qlnh_ttt.Entities.DmFood;
import com.example.qlnh_ttt.Entities.Food;
import com.example.qlnh_ttt.Entities.BillDetail;
import com.example.qlnh_ttt.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
    private Integer billId = null;
    private Integer existingBillId = null;

    private Spinner spinnerDmFood;
    private List<DmFood> danhMucList;
    private ArrayAdapter<String> categoryAdapter;

    private static final long SEARCH_DELAY = 300;

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

        checkExistingBill();
    }


    // Thêm method để load danh mục
    private void loadCategories() {
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
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    JSONArray jsonArray = new JSONArray(response.toString());
                    List<DmFood> categories = new ArrayList<>();
                    List<String> categoryNames = new ArrayList<>();
                    categoryNames.add("Tất cả");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        DmFood danhMuc = new DmFood();
                        danhMuc.setDmFoodId(obj.getInt("dmFoodId"));
                        danhMuc.setCategoryName(obj.getString("categoryName"));
                        categories.add(danhMuc);
                        categoryNames.add(danhMuc.getCategoryName());
                    }

                    runOnUiThread(() -> {
                        danhMucList.clear();
                        danhMucList.addAll(categories);
                        categoryAdapter.clear();
                        categoryAdapter.addAll(categoryNames);
                        categoryAdapter.notifyDataSetChanged();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading categories: " + e.getMessage());
            }
        }).start();
    }
    private void initViews() {
        txtTableInfo = findViewById(R.id.txtTableInfo);
        rvFoodOrder = findViewById(R.id.rvFoodOrder);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        spinnerDmFood = findViewById(R.id.spinnerDmFood);
        foodList = new ArrayList<>();
        orderItems = new ArrayList<>();
        danhMucList = new ArrayList<>();

        // Khởi tạo adapter cho Spinner
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDmFood.setAdapter(categoryAdapter);
        loadCategories();


        // Xử lý sự kiện khi chọn danh mục
        spinnerDmFood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    // Nếu chọn "Tất cả"
                    loadFoodMenu();
                } else {
                    // Lấy danh mục được chọn và load món ăn theo danh mục
                    DmFood selectedCategory = danhMucList.get(position - 1);
                    loadFoodsByCategory(selectedCategory.getDmFoodId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });




    }

    private void loadFoodsByCategory(int categoryId) {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                URL url = new URL("http://172.16.1.2:8083/api/v1/food/category/" + categoryId);
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
                }
                else if(responseCode == HttpURLConnection.HTTP_NOT_FOUND){
                    runOnUiThread(() -> {
                        foodList.clear();
                        updateAdapterWithOrderItems();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading foods by category: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(OrderFoodActivity.this, "Lỗi tải danh sách món ăn", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    private void getIntentData() {
        tableId = getIntent().getIntExtra("table_id", -1);
        String tableName = getIntent().getStringExtra("table_name");
        txtTableInfo.setText(tableName);
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

                        // Load bill đã có
                        loadExistingOrderItems();
                    }
                }

                loadFoodMenu();

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
        if (billId == null) {
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


                    for (BillDetail item : orderItems) {
                        item.setBillID(billId);
                    }


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


    private void submitOrderItems() {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");
                int successCount = 0;

                for (BillDetail item : orderItems) {
                    // Lấy bill trước
                    URL checkUrl = new URL("http://172.16.1.2:8086/api/v1/billdetails/bill/" + item.getBillID() + "/" + item.getFoodID());
                    HttpURLConnection checkConn = (HttpURLConnection) checkUrl.openConnection();
                    checkConn.setRequestMethod("GET");
                    checkConn.setRequestProperty("Authorization", "Bearer " + token);

                    int responseCode = checkConn.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // món ăn đã gọi, nên update thêm
                        URL updateUrl = new URL("http://172.16.1.2:8086/api/v1/billdetails/" + item.getBillID() + "/" + item.getFoodID());
                        HttpURLConnection updateConn = (HttpURLConnection) updateUrl.openConnection();
                        updateConn.setRequestMethod("PUT");
                        updateConn.setRequestProperty("Authorization", "Bearer " + token);
                        updateConn.setRequestProperty("Content-Type", "application/json");
                        updateConn.setDoOutput(true);

                        // Chỉ gửi số lượng mới
//                        String newQuantity = String.valueOf(item.getQuantity());
//                        updateConn.getOutputStream().write(newQuantity.getBytes());

                        JSONObject updateData = new JSONObject();
                        updateData.put("newQuantity", item.getQuantity());
                        String jsonInputString = updateData.toString();

                        try (OutputStream os = updateConn.getOutputStream()) {
                            byte[] input = jsonInputString.getBytes("utf-8");
                            os.write(input, 0, input.length);
                        }

                        if (updateConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            successCount++;
                        }
                        updateConn.disconnect();
                    } else if(responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                        // Món ăn chưa gọi, nên phải gọi mới
                        URL createUrl = new URL("http://172.16.1.2:8086/api/v1/billdetails");
                        HttpURLConnection createConn = (HttpURLConnection) createUrl.openConnection();
                        createConn.setRequestMethod("POST");
                        createConn.setRequestProperty("Authorization", "Bearer " + token);
                        createConn.setRequestProperty("Content-Type", "application/json");
                        createConn.setDoOutput(true);

                        JSONObject billDetailData = new JSONObject();
                        billDetailData.put("billID", item.getBillID());
                        billDetailData.put("foodID", item.getFoodID());
                        billDetailData.put("quantity", item.getQuantity());
                        billDetailData.put("price", item.getPrice());

                        createConn.getOutputStream().write(billDetailData.toString().getBytes());

                        if (createConn.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                            successCount++;
                        }
                        createConn.disconnect();
                    }
                    checkConn.disconnect();
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
                runOnUiThread(() -> {
                    Toast.makeText(OrderFoodActivity.this, "Lỗi khi cập nhật món", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }



}