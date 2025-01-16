package com.example.qlnh_ttt.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlnh_ttt.Adapters.AccountAdapter;
import com.example.qlnh_ttt.Entities.InfoUser;
import com.example.qlnh_ttt.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AccountManagementActivity extends AppCompatActivity {
    private RecyclerView rvAccounts;
    private AccountAdapter accountAdapter;
    private List<InfoUser> accountList = new ArrayList<>();
    private boolean isAdmin = false;
    private ImageView imgvBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_management_layout);

        rvAccounts = findViewById(R.id.rv_accounts);
        imgvBack = findViewById(R.id.imgv_back);

        imgvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
        checkAccountType();
    }

    public void back() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);// xóa các activity ko tái xử dụng
        startActivity(intent);
        finish(); // Kết thúc Activity hiện tại
    }

    private void checkAccountType() {
        new Thread(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            String token = sharedPreferences.getString("auth_token", "");

            try {
                URL url = new URL("http://172.16.1.2:8080/api/v1/auth/get-accountType");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String accountType = reader.readLine();
                    isAdmin = accountType.equals("admin");

                    runOnUiThread(() -> {
                        if (isAdmin) {
                            loadAllAccounts();
                        } else {
                            loadPersonalAccount();
                        }
                    });
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Lỗi khi kiểm tra loại tài khoản", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void loadAllAccounts() {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                URL url = new URL("http://172.16.1.2:8081/api/v1/infoUser");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    JSONArray jsonArray = new JSONArray(response.toString());
                    List<InfoUser> accounts = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject accountJson = jsonArray.getJSONObject(i);
                        InfoUser account = new InfoUser(
                                accountJson.getInt("userInfoId"),
                                accountJson.getInt("accountId"),
                                accountJson.getString("fullName"),
                                accountJson.getString("sex"),
                                accountJson.getString("email"),
                                accountJson.getString("phoneNumber")

                        );
                        accounts.add(account);
                    }

                    runOnUiThread(() -> {
                        accountList.clear();
                        accountList.addAll(accounts);
                        setupRecyclerView();
                    });
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Lỗi khi tải danh sách tài khoản", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void loadPersonalAccount() {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                URL url = new URL("http://172.16.1.2:8081/api/v1/infoUser/info");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject accountJson = new JSONObject(response.toString());
                    InfoUser account = new InfoUser(
                            accountJson.getInt("userInfoId"),
                            accountJson.getInt("accountId"),
                            accountJson.getString("fullName"),
                            accountJson.getString("sex"),
                            accountJson.getString("email"),
                            accountJson.getString("phoneNumber")
                    );

                    runOnUiThread(() -> {
                        accountList.clear();
                        accountList.add(account);
                        setupRecyclerView();
                    });
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Lỗi khi tải thông tin tài khoản", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void setupRecyclerView() {
        accountAdapter = new AccountAdapter(this,accountList, isAdmin, new AccountAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(InfoUser account, int position) {
                // Hiện dialog xác nhận xóa
                new AlertDialog.Builder(AccountManagementActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa user này?")
                        .setPositiveButton("Xóa", (dialog, which) -> deleteAccount(account, position))
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        rvAccounts.setLayoutManager(new LinearLayoutManager(this));
        rvAccounts.setAdapter(accountAdapter);
    }

    private void deleteAccount(InfoUser account, int position) {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                URL url = new URL("http://172.16.1.2:8080/api/v1/delete/" + account.getAccountId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        accountAdapter.removeItem(position);
                        Toast.makeText(this, "Đã xóa tài khoản thành công", Toast.LENGTH_SHORT).show();
                    });
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this,
                        "Lỗi khi xóa tài khoản",
                        Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accountList != null) {
            checkAccountType();
        }
    }
}
