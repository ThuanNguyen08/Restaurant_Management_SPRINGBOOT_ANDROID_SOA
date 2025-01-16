package com.example.qlnh_ttt.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlnh_ttt.Entities.InfoUser;
import com.example.qlnh_ttt.R;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UpdateAccountActivity extends AppCompatActivity {
    private EditText etFullName, etEmail, etPhone;
    private RadioGroup rgSex;
    private Button btnSave;
    private ImageView imgvBack;
    private InfoUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_account_layout);

        etFullName = findViewById(R.id.et_fullname);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        rgSex = findViewById(R.id.rg_sex);
        btnSave = findViewById(R.id.btn_save);
        imgvBack = findViewById(R.id.imgv_back);

        currentUser = new InfoUser(
                getIntent().getIntExtra("userInfoId", 0),
                getIntent().getIntExtra("accountId", 0),
                getIntent().getStringExtra("fullName"),
                getIntent().getStringExtra("sex"),
                getIntent().getStringExtra("email"),
                getIntent().getStringExtra("phoneNumber")
        );

        populateUserData();

        // Set click listeners
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });

        imgvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void populateUserData() {
        etFullName.setText(currentUser.getFullName());
        etEmail.setText(currentUser.getEmail());
        etPhone.setText(currentUser.getPhoneNumber());

        if (currentUser.getSex().equalsIgnoreCase("Nam")) {
            ((RadioButton) findViewById(R.id.rb_male)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.rb_female)).setChecked(true);
        }
    }

    private void updateUserInfo() {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                URL url = new URL("http://172.16.1.2:8081/api/v1/infoUser/update/" + currentUser.getUserInfoId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("fullName", etFullName.getText().toString());
                jsonBody.put("sex", ((RadioButton) findViewById(rgSex.getCheckedRadioButtonId())).getText().toString());
                jsonBody.put("email", etEmail.getText().toString());
                jsonBody.put("phoneNumber", etPhone.getText().toString());

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("updated", true);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
                    });
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}