package com.example.qlnh_ttt.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qlnh_ttt.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvSignUp, resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        // Khởi tạo các view
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
        resultTextView = findViewById(R.id.resultTextView);

        // Sự kiện click cho nút Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                // Kiểm tra thông tin đăng nhập (Ví dụ đơn giản)
                if (username.isEmpty() || password.isEmpty()) {
                    // Kiểm tra xem các trường có rỗng không
                    if (username.isEmpty()) {
                        etUsername.setError("Thiếu Username");
                    }
                    if (password.isEmpty()) {
                        etPassword.setError("Thiếu Password");
                    }
                } else {
                    login(username, password);
                }
            }
        });

        // Sự kiện click để chuyển tới màn hình đăng ký
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUp();
            }
        });
    }

    private void login(String username, String password) {
        // Tạo JSON body để gửi yêu cầu đăng nhập
        String jsonBody = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Tạo URL và kết nối
                    URL url = new URL("http://172.16.1.2:8080/api/v1/login"); // Sử dụng 10.0.2.2 thay vì localhost cho emulator
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // Gửi body JSON
                    OutputStream os = connection.getOutputStream();
                    os.write(jsonBody.getBytes());
                    os.flush();

                    // Lấy mã trạng thái phản hồi
                    int responseCode = connection.getResponseCode();
                    Log.d("Login", "Response Code: " + responseCode);

                    // Đọc phản hồi từ server
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Kiểm tra mã phản hồi và hiển thị Token nếu thành công
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Giả sử response trả về Token Bearer
                        String token = response.toString();

                        if(!token.equals("Invalid credentials")){
                            // Lưu token vào SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("auth_token", token);
                            editor.apply();
                            //Chuyển đến HomeActivity
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish(); // Đóng LoginActivity

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    resultTextView.setText("Tên đăng nhập hoặc mật khẩu không đúng.");
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                resultTextView.setText("Đăng nhập thất bại: " + responseCode);
                            }
                        });
                    }

                } catch (Exception e) {
                    Log.e("Login", "Error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultTextView.setText("Error: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }


    // Chuyển đến màn hình đăng ký
    private void goToSignUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class); // Đảm bảo SignUpActivity đã được tạo
        startActivity(intent);
    }
}