package com.example.qlnh_ttt.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.qlnh_ttt.Entities.DmFood;
import com.example.qlnh_ttt.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddFoodActivity extends AppCompatActivity {
    private static final String TAG = "UploadImageActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private int dmFoodID;
    private ImageView imageView;
    private Button btnChooseImage, btnUpload;
    private ProgressBar progressBar;
    private EditText edtFoodName, edtPrice, edtDmFoodID;
    private Uri selectedImageUri;

    // Thêm các biến mới vào đầu class
    private Spinner spinnerDmFood;
    private List<DmFood> danhMucList;
    private ArrayAdapter<String> adapter;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        imageView.setImageBitmap(bitmap);
                        btnUpload.setEnabled(true);
                    } catch (IOException e) {
                        Log.e(TAG, "Error loading image: " + e.getMessage());
                        Toast.makeText(this, "Lỗi khi tải ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.them_monan_layout);

        initializeViews();
        setupListeners();
        checkPermissions();
    }

    private void initializeViews() {
        imageView = findViewById(R.id.imageView);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnUpload = findViewById(R.id.btnUpload);
        progressBar = findViewById(R.id.progressBar);
        edtFoodName = findViewById(R.id.edtFoodName);
        edtPrice = findViewById(R.id.edtPrice);
        spinnerDmFood = findViewById(R.id.spinnerDmFood);
//        edtDmFoodID = findViewById(R.id.edtDmFoodID);
        btnUpload.setEnabled(false);


        // Khởi tạo adapter cho Spinner
        danhMucList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDmFood.setAdapter(adapter);

        // Gọi API để lấy danh sách danh mục
        fetchCategories();
    }

    private void setupListeners() {
        btnChooseImage.setOnClickListener(v -> openImagePicker());
        btnUpload.setOnClickListener(v -> uploadImage());
    }

    private void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cấp quyền truy cập", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vui lòng cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadImage() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh trước", Toast.LENGTH_SHORT).show();
            return;
        }

        String foodName = edtFoodName.getText().toString().trim();
        String price = edtPrice.getText().toString().trim();

        // Lấy ID danh mục từ item được chọn trong Spinner
        int selectedPosition = spinnerDmFood.getSelectedItemPosition();
        if (selectedPosition == -1 || danhMucList.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
            return;
        }
        dmFoodID = danhMucList.get(selectedPosition).getDmFoodId();


        if (foodName.isEmpty() || price.isEmpty() ) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

//        progressBar.setVisibility(View.VISIBLE);
        btnUpload.setEnabled(false);

        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                if (token.isEmpty()) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                        goToLogin();
                    });
                    return;
                }

                // Chuyển ảnh thành base64 string
                byte[] imageBytes = convertImageToBytes(selectedImageUri);
                String base64Image = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);

                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("foodName", foodName);
                jsonRequest.put("dmFoodID", dmFoodID);
                jsonRequest.put("price", price);
                jsonRequest.put("avtFood", base64Image);  // Gửi dạng base64 string

                URL url = new URL("http://172.16.1.2:8083/api/v1/food/add");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonRequest.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddFoodActivity.this,
                                "Thêm món ăn thành công",
                                Toast.LENGTH_SHORT).show();
                        clearForm();
                    });
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddFoodActivity.this,
                                "Phiên đăng nhập hết hạn",
                                Toast.LENGTH_SHORT).show();
                        goToLogin();
                    });
                } else {
                    throw new IOException("Server returned code: " + responseCode);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error uploading food: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(AddFoodActivity.this,
                            "Lỗi: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            } finally {
                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                });
            }
        }).start();

    }

    // Convert Image sang Byte
    private byte[] convertImageToBytes(Uri imageUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    private void clearForm() {
        edtFoodName.setText("");
        edtPrice.setText("");
//        edtDmFoodID.setText("");
        imageView.setImageDrawable(null);  // Thay thế dòng setImageResource
        selectedImageUri = null;
        btnUpload.setEnabled(false);
    }


    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return uri.getPath();
    }

    private void goToLogin() {
        Intent intent = new Intent(AddFoodActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }



    // Thêm method mới để lấy danh mục từ API
    private void fetchCategories() {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("auth_token", "");

                URL url = new URL("http://172.16.1.2:8083/api/v1/dmFood");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Đọc response
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Parse JSON
                    JSONArray jsonArray = new JSONArray(response.toString());
                    List<DmFood> categories = new ArrayList<>();
                    List<String> categoryNames = new ArrayList<>();

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
                        adapter.clear();
                        adapter.addAll(categoryNames);
                        adapter.notifyDataSetChanged();
                    });
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddFoodActivity.this,
                                "Phiên đăng nhập hết hạn",
                                Toast.LENGTH_SHORT).show();
                        goToLogin();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching categories: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(AddFoodActivity.this,
                            "Lỗi khi tải danh mục: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}