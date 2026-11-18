package com.example.quanlylichhen;

public class Appointment {
    private int id;
    private String title;
    private String location;
    private String date; // định dạng yyyy-MM-dd
    private String time; // định dạng HH:mm
    private boolean reminder;

    // Constructor mặc định (không tham số)
    public Appointment() {
    }

    // Constructor đầy đủ tham số (bao gồm id khi lấy từ SQLite)
    public Appointment(int id, String title, String location, String date, String time, boolean reminder) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
    }

    // Constructor không có id (dùng khi thêm mới lịch hẹn vào SQLite)
    public Appointment(String title, String location, String date, String time, boolean reminder) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isReminder() {
        return reminder;
    }

    public boolean getReminder() {
        return reminder;
    }

    public void setReminder(boolean reminder) {
        this.reminder = reminder;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", reminder=" + reminder +
                '}';
    }
}
