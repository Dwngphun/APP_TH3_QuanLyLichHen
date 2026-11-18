package com.example.quanlylichhen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private TextView tvError;
    private Button btnLogin;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Ánh xạ các view
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tvError = findViewById(R.id.tvError);
        btnLogin = findViewById(R.id.btnLogin);

        // Xử lý sự kiện nút Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // 1. Kiểm tra nếu tên đăng nhập hoặc mật khẩu bỏ trống
        if (username.isEmpty() || password.isEmpty()) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu");
            return;
        }

        // 2. Kiểm tra thông tin đăng nhập từ CSDL
        boolean isLoginSuccess = databaseHelper.checkLogin(username, password);

        // 3. Xử lý kết quả kiểm tra
        if (isLoginSuccess) {
            tvError.setVisibility(View.GONE);
            // Chuyển sang MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            // Gọi finish() để khi bấm Back không quay lại màn hình Login
            finish();
        } else {
            // Hiển thị thông báo lỗi
            tvError.setVisibility(View.VISIBLE);
            tvError.setText("Sai tên đăng nhập hoặc mật khẩu");
        }
    }
}
