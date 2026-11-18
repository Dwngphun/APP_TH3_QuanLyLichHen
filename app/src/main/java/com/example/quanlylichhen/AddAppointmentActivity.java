package com.example.quanlylichhen;

import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText edtTitle;
    private EditText edtLocation;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private SwitchCompat switchReminder;
    private Button btnSaveAppointment;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_appointment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);

        // Ánh xạ các thành phần giao diện
        btnBack = findViewById(R.id.btnBack);
        edtTitle = findViewById(R.id.edtTitle);
        edtLocation = findViewById(R.id.edtLocation);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        switchReminder = findViewById(R.id.switchReminder);
        btnSaveAppointment = findViewById(R.id.btnSaveAppointment);

        // Thiết lập chế độ 24 giờ cho TimePicker
        timePicker.setIs24HourView(true);

        // Xử lý nút quay lại trên thanh tiêu đề
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút "Lưu lịch hẹn"
        btnSaveAppointment.setOnClickListener(v -> handleSaveAppointment());
    }

    private void handleSaveAppointment() {
        String title = edtTitle.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();

        // 1. Kiểm tra tiêu đề và địa điểm không được bỏ trống
        if (title.isEmpty()) {
            edtTitle.setError("Vui lòng nhập tên lịch hẹn");
            edtTitle.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            edtLocation.setError("Vui lòng nhập địa điểm");
            edtLocation.requestFocus();
            return;
        }

        // 2. Lấy thông tin ngày/tháng/năm và giờ/phút
        int year = datePicker.getYear();
        int month = datePicker.getMonth(); // 0-based (0 -> 11)
        int day = datePicker.getDayOfMonth();

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // 3. Kiểm tra thời điểm hẹn phải sau thời điểm hiện tại của hệ thống
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(Calendar.YEAR, year);
        selectedCalendar.set(Calendar.MONTH, month);
        selectedCalendar.set(Calendar.DAY_OF_MONTH, day);
        selectedCalendar.set(Calendar.HOUR_OF_DAY, hour);
        selectedCalendar.set(Calendar.MINUTE, minute);
        selectedCalendar.set(Calendar.SECOND, 0);
        selectedCalendar.set(Calendar.MILLISECOND, 0);

        Calendar currentCalendar = Calendar.getInstance();

        if (!selectedCalendar.after(currentCalendar)) {
            Toast.makeText(this, "Thời gian hẹn phải sau thời điểm hiện tại", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Định dạng ngày yyyy-MM-dd và giờ HH:mm theo Locale.US
        String dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
        String timeStr = String.format(Locale.US, "%02d:%02d", hour, minute);
        boolean isReminder = switchReminder.isChecked();

        // 5. Thêm vào CSDL SQLite
        Appointment appointment = new Appointment(title, location, dateStr, timeStr, isReminder);
        long insertId = databaseHelper.insertAppointment(appointment);

        if (insertId != -1) {
            Toast.makeText(this, "Thêm lịch hẹn thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Có lỗi xảy ra khi lưu lịch hẹn", Toast.LENGTH_SHORT).show();
        }
    }
}
