package com.example.qlnh_ttt.Activities;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlnh_ttt.Adapters.RevenueAdapter;
import com.example.qlnh_ttt.Entities.Bill;
import com.example.qlnh_ttt.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RevenueActivity extends AppCompatActivity {

    private TextView tvStartDate, tvEndDate, txtTotalRevenue, txtBillCount;
    private Button btnSearch;
    private RecyclerView rvBillList;
    private RevenueAdapter revenueAdapter;
    private List<Bill> billList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doanhthu_layout);

        // Ánh xạ các view
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnSearch = findViewById(R.id.btnSearch);
        rvBillList = findViewById(R.id.rvBillList);
        txtTotalRevenue = findViewById(R.id.txt_total_revenue);
        txtBillCount = findViewById(R.id.txt_bill_count);

        // Thiết lập RecyclerView
        revenueAdapter = new RevenueAdapter(billList);
        rvBillList.setLayoutManager(new LinearLayoutManager(this));
        rvBillList.setAdapter(revenueAdapter);

        // Sự kiện chọn ngày bắt đầu
        tvStartDate.setOnClickListener(view -> showDatePickerDialog(tvStartDate));

        // Sự kiện chọn ngày kết thúc
        tvEndDate.setOnClickListener(view -> showDatePickerDialog(tvEndDate));

        // Sự kiện nút tìm kiếm
        btnSearch.setOnClickListener(view -> searchBills());

        loadAllBills();
    }

    private void loadAllBills() {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");


                URL url = new URL("http://172.16.1.2:8086/api/v1/bills");
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
                    List<Bill> todayBills = new ArrayList<>();
//                    LocalDate today = LocalDate.now();
                    int totalRevenue = 0;
                    int billCount = 0;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject billJson = jsonArray.getJSONObject(i);

                        //format chuỗi string ngày kiểu datetime
                        LocalDateTime billDate = LocalDateTime.parse(
                                billJson.getString("billDate"),
                                DateTimeFormatter.ISO_DATE_TIME
                        );

                        Bill bill = new Bill(
                                billJson.getInt("billID"),
                                billDate,
                                billJson.getInt("userInfoID"),
                                billJson.getInt("tableID"),
                                billJson.getString("status"),
                                billJson.getInt("totalAmount")
                        );

//                        if (billDate.toLocalDate().equals(today) && bill.getStatus().equalsIgnoreCase("PAID")) {
                        todayBills.add(bill);
                        totalRevenue += bill.getTotalAmount();
                        billCount++;
//                        }
                    }

                    final int finalTotalRevenue = totalRevenue;
                    final int finalBillCount = billCount;

                    // Cập nhật giao diện trên UI thread
                    new Handler(Looper.getMainLooper()).post(() -> {
                        billList.clear();
                        billList.addAll(todayBills);
                        revenueAdapter.notifyDataSetChanged();
                        // Update statistics
                        updateStatistics(finalTotalRevenue, finalBillCount);

                        if (todayBills.isEmpty()) {
                            Toast.makeText(this, "Ngày hôm nay chưa có hóa đơn nào.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    throw new IOException("Mã phản hồi: " + responseCode);
                }

                conn.disconnect();

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(RevenueActivity.this, "Lỗi khi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void updateStatistics(int totalRevenue, int billCount) {
        // Format currency in VND
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedRevenue = formatter.format(totalRevenue);

        // Update UI elements
        txtTotalRevenue.setText(formattedRevenue);
        txtBillCount.setText("Số đơn: " + billCount);
    }

    private void showDatePickerDialog(TextView targetView) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
            targetView.setText(date);
        }, LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth());
        datePickerDialog.show();
    }

    private void searchBills() {
        String startDateStr = tvStartDate.getText().toString();
        String endDateStr = tvEndDate.getText().toString();

        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn cả ngày bắt đầu và ngày kết thúc!", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalDateTime startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ISO_LOCAL_DATE).atTime(23, 59, 59);

        // Lọc danh sách hóa đơn theo ngày
        List<Bill> filteredBills = new ArrayList<>();
        int totalRevenue = 0;
        int billCount = 0;

        for (Bill bill : billList) {
            if (bill.getBillDate().isAfter(startDate) && bill.getBillDate().isBefore(endDate)) {
                filteredBills.add(bill);
                totalRevenue += bill.getTotalAmount();
                billCount++;
            }
        }

        if (filteredBills.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy hóa đơn trong khoảng thời gian này!", Toast.LENGTH_SHORT).show();
        }

        // Cập nhật RecyclerView
        revenueAdapter.updateData(filteredBills);

        // Cập nhật thống kê
        updateStatistics(totalRevenue, billCount);
    }
}
