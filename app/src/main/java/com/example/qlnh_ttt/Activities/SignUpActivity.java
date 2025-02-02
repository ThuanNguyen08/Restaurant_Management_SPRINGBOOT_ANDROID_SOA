package com.example.qlnh_ttt.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qlnh_ttt.R;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {

    private EditText etUsername, etPassword1, etPassword2;
    private Button btnSignUp;
    private Spinner spAccountType;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        // ánh xạ tới các biến giao diện
        etUsername = findViewById(R.id.etUsername);
        spAccountType = findViewById(R.id.spAccountType);
        etPassword1 = findViewById(R.id.etPassword1);
        etPassword2 = findViewById(R.id.etPassword2);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvLogin);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"user", "admin"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAccountType.setAdapter(adapter);

        // Xử lý sự kiện đăng ký
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        // Chuyển đến LoginActivity
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
    }

    private void signUp() {
        String username = etUsername.getText().toString().trim();
        String accountType = spAccountType.getSelectedItem().toString();
        String password1 = etPassword1.getText().toString().trim();
        String password2 = etPassword2.getText().toString().trim();

        // Kiểm tra thông tin nhập
        if (username.isEmpty() || accountType.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
            if (username.isEmpty()) etUsername.setError("Thiếu Username");
            if (password1.isEmpty()) etPassword1.setError("Nhập Password");
            if (password2.isEmpty()) etPassword2.setError("Nhập lại password để xác nhận");
            return;
        }

        if (!password1.equals(password2)) {
            etPassword2.setError("Password không hợp lệ");
            return;
        }

        // Tạo dữ liệu json
        String jsonBody = "{\"username\":\"" + username + "\",\"accountType\":\"" + accountType + "\",\"password\":\"" + password1 + "\"}";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://172.16.1.2:8080/api/v1/register"); // Sử dụng 10.0.2.2 cho emulator
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // Gửi dữ liệu JSON
                    OutputStream os = connection.getOutputStream();
                    os.write(jsonBody.getBytes());
                    os.flush();

                    //Lấy response trả về
                    int responseCode = connection.getResponseCode();
                    Log.d("SignUp", "Response Code: " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        createUserInfo(username);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignUpActivity.this, "Đăng kí thành công!", Toast.LENGTH_SHORT).show();
                                goToLogin();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignUpActivity.this, "Đăng kí thất bại, hãy thử lại.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (Exception e) {
                    Log.e("SignUp", "Error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void createUserInfo(String username) {
        new Thread(() -> {
            try {
                URL url = new URL("http://172.16.1.2:8080/api/v1/infoUser/add");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String jsonBody = "{\"username\":\"" + username + "\"}";
                OutputStream os = connection.getOutputStream();
                os.write(jsonBody.getBytes());
                os.flush();

                int responseCode = connection.getResponseCode();
                Log.d("CreateUserInfo", "Response Code: " + responseCode);
            } catch (Exception e) {
                Log.e("CreateUserInfo", "Error: " + e.getMessage());
            }
        }).start();
    }


    private void goToLogin() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}