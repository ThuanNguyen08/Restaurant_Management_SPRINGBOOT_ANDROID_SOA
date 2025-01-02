package com.example.qlnh_ttt.AccoutType;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AccoutType {

        private static final String CHECK_TYPE_URL = "http://172.16.1.2:8080/api/v1/auth/get-accountType";

        public static boolean isUser(Context context) {
            try {
                SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                URL url = new URL(CHECK_TYPE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                // Đọc response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String accountType = in.readLine();
                in.close();

                return "user".equals(accountType);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

}
