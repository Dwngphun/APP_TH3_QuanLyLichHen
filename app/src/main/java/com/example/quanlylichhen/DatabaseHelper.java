package com.example.quanlylichhen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên CSDL và Version
    public static final String DATABASE_NAME = "appointments.db";
    public static final int DATABASE_VERSION = 1;

    // Bảng users và các cột
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_USERNAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";

    // Bảng appointments và các cột
    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String COLUMN_APPT_ID = "id";
    public static final String COLUMN_APPT_TITLE = "title";
    public static final String COLUMN_APPT_LOCATION = "location";
    public static final String COLUMN_APPT_DATE = "date";
    public static final String COLUMN_APPT_TIME = "time";
    public static final String COLUMN_APPT_REMINDER = "reminder";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users
        String createTableUsers = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COLUMN_USER_PASSWORD + " TEXT NOT NULL"
                + ")";

        // Tạo bảng appointments
        String createTableAppointments = "CREATE TABLE " + TABLE_APPOINTMENTS + " ("
                + COLUMN_APPT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_APPT_TITLE + " TEXT NOT NULL, "
                + COLUMN_APPT_LOCATION + " TEXT NOT NULL, "
                + COLUMN_APPT_DATE + " TEXT NOT NULL, "
                + COLUMN_APPT_TIME + " TEXT NOT NULL, "
                + COLUMN_APPT_REMINDER + " INTEGER NOT NULL"
                + ")";

        db.execSQL(createTableUsers);
        db.execSQL(createTableAppointments);

        // Chèn dữ liệu mẫu: bảng users (admin / 123456)
        ContentValues adminUser = new ContentValues();
        adminUser.put(COLUMN_USER_USERNAME, "admin");
        adminUser.put(COLUMN_USER_PASSWORD, "123456");
        db.insert(TABLE_USERS, null, adminUser);

        // Chèn dữ liệu mẫu: 3 lịch hẹn mẫu
        // 1. ("Họp nhóm đồ án", "Phòng B203", "2026-10-25", "09:00", 1)
        insertSampleAppointment(db, "Họp nhóm đồ án", "Phòng B203", "2026-10-25", "09:00", 1);
        // 2. ("Khám răng", "Nha khoa Minh Tâm", "2026-10-26", "14:30", 1)
        insertSampleAppointment(db, "Khám răng", "Nha khoa Minh Tâm", "2026-10-26", "14:30", 1);
        // 3. ("Đi cà phê với bạn", "Highland Coffee", "2026-10-27", "19:00", 0)
        insertSampleAppointment(db, "Đi cà phê với bạn", "Highland Coffee", "2026-10-27", "19:00", 0);
    }

    private void insertSampleAppointment(SQLiteDatabase db, String title, String location, String date, String time, int reminder) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_APPT_TITLE, title);
        values.put(COLUMN_APPT_LOCATION, location);
        values.put(COLUMN_APPT_DATE, date);
        values.put(COLUMN_APPT_TIME, time);
        values.put(COLUMN_APPT_REMINDER, reminder);
        db.insert(TABLE_APPOINTMENTS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        onCreate(db);
    }

    /**
     * Kiểm tra thông tin đăng nhập từ bảng users
     */
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USER_USERNAME + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
        Cursor cursor = null;
        boolean isValid = false;
        try {
            cursor = db.rawQuery(query, new String[]{username, password});
            if (cursor != null && cursor.moveToFirst()) {
                isValid = true;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isValid;
    }

    /**
     * Thêm user mới vào bảng users
     */
    public long insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, username);
        values.put(COLUMN_USER_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    /**
     * Thêm lịch hẹn mới vào bảng appointments và trả về id vừa thêm
     */
    public long insertAppointment(Appointment a) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_APPT_TITLE, a.getTitle());
        values.put(COLUMN_APPT_LOCATION, a.getLocation());
        values.put(COLUMN_APPT_DATE, a.getDate());
        values.put(COLUMN_APPT_TIME, a.getTime());
        values.put(COLUMN_APPT_REMINDER, a.isReminder() ? 1 : 0);
        return db.insert(TABLE_APPOINTMENTS, null, values);
    }

    /**
     * Lấy toàn bộ danh sách lịch hẹn, sắp xếp tăng dần theo date ASC, time ASC
     */
    public ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_APPOINTMENTS +
                " ORDER BY " + COLUMN_APPT_DATE + " ASC, " + COLUMN_APPT_TIME + " ASC";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APPT_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPT_TITLE));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPT_LOCATION));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPT_DATE));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPT_TIME));
                    int reminderInt = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APPT_REMINDER));
                    boolean reminder = (reminderInt == 1);

                    list.add(new Appointment(id, title, location, date, time, reminder));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * Lấy 1 lịch hẹn theo id
     */
    public Appointment getAppointmentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_APPOINTMENTS + " WHERE " + COLUMN_APPT_ID + " = ?";
        Cursor cursor = null;
        Appointment appointment = null;
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
            if (cursor != null && cursor.moveToFirst()) {
                int apptId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APPT_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPT_TITLE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPT_LOCATION));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPT_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPT_TIME));
                int reminderInt = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APPT_REMINDER));
                boolean reminder = (reminderInt == 1);

                appointment = new Appointment(apptId, title, location, date, time, reminder);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return appointment;
    }

    /**
     * Cập nhật cột reminder (1 hoặc 0) theo id
     */
    public int updateReminder(int id, boolean on) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_APPT_REMINDER, on ? 1 : 0);
        return db.update(TABLE_APPOINTMENTS, values, COLUMN_APPT_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Xóa lịch hẹn theo id
     */
    public int deleteAppointment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_APPOINTMENTS, COLUMN_APPT_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Trả về tổng số lịch hẹn (SELECT COUNT(*) FROM appointments)
     */
    public int countAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS;
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    /**
     * Trả về số lịch hẹn đang bật nhắc nhở (SELECT COUNT(*) FROM appointments WHERE reminder = 1)
     */
    public int countReminders() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS + " WHERE " + COLUMN_APPT_REMINDER + " = ?";
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery(query, new String[]{"1"});
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }
}
