package com.example.qlnh_ttt.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qlnh_ttt.R;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AddTableActivity extends AppCompatActivity {
    private EditText etTableName;
    private Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_table_layout);

        etTableName = findViewById(R.id.etTableName);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTable();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void saveTable() {
        String tableName = etTableName.getText().toString().trim();
        if (tableName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên bàn", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                URL url = new URL("http://172.16.1.2:8084/api/v1/tables");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);

                JSONObject jsonTable = new JSONObject();
                jsonTable.put("tableName", tableName);
                jsonTable.put("status", "EMPTY");

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonTable.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Thêm bàn thành công", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Lỗi: " + responseCode, Toast.LENGTH_SHORT).show()
                    );
                }
                conn.disconnect();
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}