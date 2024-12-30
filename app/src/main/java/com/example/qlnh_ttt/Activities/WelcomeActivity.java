package com.example.qlnh_ttt.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qlnh_ttt.R;

public class WelcomeActivity extends AppCompatActivity {

    private Button btnLogin, btnSignUp;
    private TextView textViewTitle, textViewSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);

        // Khởi tạo các view
        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_signup);

        // Thiết lập sự kiện click cho các nút
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLoginFromWel();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSignUpFromWel();
            }
        });
    }

    // Mở activity đăng nhập
    public void callLoginFromWel() {
        // Giả sử có một Activity đăng nhập
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    // Mở activity đăng ký
    public void callSignUpFromWel() {
        // Giả sử có một Activity đăng ký
        Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}