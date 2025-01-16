package com.example.qlnh_ttt.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlnh_ttt.Entities.Food;
import com.example.qlnh_ttt.R;
import com.example.qlnh_ttt.Adapters.PaymentDetailAdapter;
import com.example.qlnh_ttt.Entities.BillDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    private TextView txtTableName;
    private TextView txtTotalAmount;
    private Button btnConfirmPayment;
    private RecyclerView rvPaymentDetails;
    private PaymentDetailAdapter adapter;
    private ArrayList<BillDetail> billDetails;
    private Map<Integer, Food> foodMap;
    private int billId;
    private int tableId;
    private static final String TAG = "PaymentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_layout);

        initViews();
        getIntentData();
        setupRecyclerView();
        loadBillDetails();
        setupConfirmButton();
    }

    private void initViews() {
        txtTableName = findViewById(R.id.txtTableName);
        txtTotalAmount = findViewById(R.id.txtTotalAmount);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        rvPaymentDetails = findViewById(R.id.rvPaymentDetails);
        billDetails = new ArrayList<>();
        foodMap = new HashMap<>();
    }

    private void getIntentData() {
        tableId = getIntent().getIntExtra("table_id", -1);
        String tableName = getIntent().getStringExtra("table_name");
        txtTableName.setText(tableName);
    }

    private void setupRecyclerView() {
        adapter = new PaymentDetailAdapter(this, billDetails);
        rvPaymentDetails.setLayoutManager(new LinearLayoutManager(this));
        rvPaymentDetails.setAdapter(adapter);
    }

    private void loadBillDetails() {
        new Thread(() -> {
            try {
                // lấy idbill khi đã có id bàn lấy từ intent
                getBillIdForTable();

                if (billId > 0) {
                    loadFoodList();
                    // gọi api để truyềnn idbill lấy thông tin của bill
                    loadBillDetailsFromApi();
                    // Update the total amount
                    updateTotalAmount();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading bill details: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(this, "Lỗi tải thông tin hóa đơn", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void loadFoodList() throws Exception {
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
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Food food = new Food();
                food.setFoodID(jsonObject.getInt("foodID"));
                food.setFoodName(jsonObject.getString("foodName"));
                food.setPrice(jsonObject.getString("price"));
                foodMap.put(food.getFoodID(), food);
            }
        }
    }

    private void getBillIdForTable() throws Exception {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", "");

        URL url = new URL("http://172.16.1.2:8086/api/v1/bills/table/" + tableId);
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
            billId = jsonResponse.getInt("billID");
        }
    }

    private void loadBillDetailsFromApi() throws Exception {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", "");

        URL url = new URL("http://172.16.1.2:8086/api/v1/billdetails/bill/" + billId);
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
            ArrayList<BillDetail> newBillDetails = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                BillDetail detail = new BillDetail();
                detail.setBillID(jsonObject.getInt("billID"));
                detail.setFoodID(jsonObject.getInt("foodID"));
                detail.setQuantity(jsonObject.getInt("quantity"));
                detail.setPrice(jsonObject.getInt("price"));
                newBillDetails.add(detail);
            }

            runOnUiThread(() -> {
                billDetails.clear();
                billDetails.addAll(newBillDetails);
                adapter.updateData(billDetails, foodMap);
            });
        }
    }

    private void updateTotalAmount() throws Exception {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", "");

        URL url = new URL("http://172.16.1.2:8086/api/v1/bills/total/" + billId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
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
            int totalAmount = jsonResponse.getInt("totalAmount");

            NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            String formattedAmount = formatter.format(totalAmount) + " VNĐ";

            runOnUiThread(() -> txtTotalAmount.setText(formattedAmount));
        }
    }

    private void setupConfirmButton() {
        btnConfirmPayment.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    String token = sharedPreferences.getString("auth_token", "");

                    URL url = new URL("http://172.16.1.2:8086/api/v1/bills/pay/" + billId);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Authorization", "Bearer " + token);

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Lỗi thanh toán", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error confirming payment: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(this, "Lỗi thanh toán", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }
}