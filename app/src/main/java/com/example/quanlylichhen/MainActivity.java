package com.example.quanlylichhen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalCount;
    private TextView tvReminderCount;
    private RecyclerView rvAppointments;
    private TextView tvEmpty;
    private Button btnAddAppointment;

    private DatabaseHelper databaseHelper;
    private AppointmentAdapter adapter;
    private ArrayList<Appointment> appointmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Áp dụng insets để bố cục không bị che bởi status bar / navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Ánh xạ View
        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvReminderCount = findViewById(R.id.tvReminderCount);
        rvAppointments = findViewById(R.id.rvAppointments);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnAddAppointment = findViewById(R.id.btnAddAppointment);

        // Khởi tạo RecyclerView và Adapter
        appointmentList = new ArrayList<>();
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentAdapter(this, appointmentList);
        rvAppointments.setAdapter(adapter);

        // Xử lý nút "+ Thêm lịch hẹn"
        btnAddAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddAppointmentActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Nạp lại dữ liệu mỗi khi màn hình quay trở lại
        loadData();
    }

    /**
     * Nạp lại dữ liệu thống kê và danh sách lịch hẹn từ SQLite
     */
    public void loadData() {
        // Cập nhật thống kê
        int total = databaseHelper.countAll();
        int reminders = databaseHelper.countReminders();
        tvTotalCount.setText(String.valueOf(total));
        tvReminderCount.setText(String.valueOf(reminders));

        // Lấy danh sách lịch hẹn từ database
        appointmentList = databaseHelper.getAllAppointments();
        adapter.updateList(appointmentList);

        // Hiển thị thông báo khi danh sách trống
        if (appointmentList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvAppointments.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvAppointments.setVisibility(View.VISIBLE);
        }
    }
}
