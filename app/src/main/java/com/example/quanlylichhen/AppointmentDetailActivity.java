package com.example.quanlylichhen;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AppointmentDetailActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvDetailTitle;
    private TextView tvDetailLocation;
    private TextView tvDetailDateTime;
    private TextView tvDetailReminderStatus;
    private SwitchCompat switchDetailReminder;
    private Button btnBackAction;
    private Button btnDeleteAppointment;

    private DatabaseHelper databaseHelper;
    private int appointmentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointment_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);

        // Ánh xạ View
        btnBack = findViewById(R.id.btnBack);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailLocation = findViewById(R.id.tvDetailLocation);
        tvDetailDateTime = findViewById(R.id.tvDetailDateTime);
        tvDetailReminderStatus = findViewById(R.id.tvDetailReminderStatus);
        switchDetailReminder = findViewById(R.id.switchDetailReminder);
        btnBackAction = findViewById(R.id.btnBackAction);
        btnDeleteAppointment = findViewById(R.id.btnDeleteAppointment);

        // Lấy ID lịch hẹn truyền từ Intent
        appointmentId = getIntent().getIntExtra("appointment_id", -1);
        if (appointmentId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin lịch hẹn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Đổ dữ liệu lịch hẹn lên giao diện
        loadAppointmentDetails();

        // Xử lý sự kiện nút quay lại (mũi tên tiêu đề và nút bên dưới)
        btnBack.setOnClickListener(v -> finish());
        btnBackAction.setOnClickListener(v -> finish());

        // Xử lý sự kiện nút xóa lịch hẹn
        btnDeleteAppointment.setOnClickListener(v -> handleDeleteAppointment());
    }

    private void loadAppointmentDetails() {
        Appointment appointment = databaseHelper.getAppointmentById(appointmentId);
        if (appointment == null) {
            Toast.makeText(this, "Lịch hẹn không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tên lịch hẹn
        tvDetailTitle.setText(appointment.getTitle());

        // Địa điểm
        tvDetailLocation.setText(appointment.getLocation());

        // Thời gian: chuyển đổi date sang dd/MM/yyyy và ghép với time
        String formattedDateTime = formatDateTime(appointment.getDate(), appointment.getTime());
        tvDetailDateTime.setText(formattedDateTime);

        // Trạng thái nhắc nhở ban đầu
        boolean isReminder = appointment.isReminder();
        switchDetailReminder.setChecked(isReminder);
        tvDetailReminderStatus.setText(isReminder ? "Đang bật" : "Đang tắt");

        // Lắng nghe thay đổi bật/tắt nhắc nhở
        switchDetailReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            databaseHelper.updateReminder(appointmentId, isChecked);
            tvDetailReminderStatus.setText(isChecked ? "Đang bật" : "Đang tắt");
        });
    }

    private void handleDeleteAppointment() {
        int rows = databaseHelper.deleteAppointment(appointmentId);
        if (rows > 0) {
            Toast.makeText(this, "Đã xóa lịch hẹn", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Không thể xóa lịch hẹn", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    /**
     * Chuyển đổi định dạng ngày yyyy-MM-dd sang dd/MM/yyyy và ghép với HH:mm
     */
    private String formatDateTime(String dbDate, String dbTime) {
        if (dbDate == null) dbDate = "";
        if (dbTime == null) dbTime = "";

        String displayDate = dbDate;
        String[] parts = dbDate.split("-");
        if (parts.length == 3) {
            displayDate = parts[2] + "/" + parts[1] + "/" + parts[0];
        }

        return displayDate + " · " + dbTime;
    }
}
