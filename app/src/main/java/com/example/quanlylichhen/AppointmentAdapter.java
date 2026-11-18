package com.example.quanlylichhen;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<Appointment> list;

    public AppointmentAdapter(Context context, ArrayList<Appointment> list) {
        this.context = context;
        this.list = list;
    }

    public void updateList(ArrayList<Appointment> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = list.get(position);

        // 1. Tên lịch hẹn (chữ đậm, cỡ lớn)
        holder.tvItemTitle.setText(appointment.getTitle());

        // 2. Dòng "Địa điểm: [tên địa điểm]" (màu xám)
        holder.tvItemLocation.setText("Địa điểm: " + appointment.getLocation());

        // 3. Chuyển đổi ngày từ yyyy-MM-dd sang dd/MM/yyyy và hiển thị dd/MM/yyyy · HH:mm
        String formattedDateTime = formatDateTime(appointment.getDate(), appointment.getTime());
        holder.tvItemDateTime.setText(formattedDateTime);

        // 4. Badge trạng thái nhắc nhở
        if (appointment.isReminder()) {
            holder.tvItemReminder.setText("Có nhắc");
            holder.tvItemReminder.setBackgroundResource(R.drawable.bg_badge_reminder_on);
            holder.tvItemReminder.setTextColor(ContextCompat.getColor(context, R.color.badge_reminder_on_text));
        } else {
            holder.tvItemReminder.setText("Không nhắc");
            holder.tvItemReminder.setBackgroundResource(R.drawable.bg_badge_reminder_off);
            holder.tvItemReminder.setTextColor(ContextCompat.getColor(context, R.color.badge_reminder_off_text));
        }

        // 5. Khi nhấn vào item, mở AppointmentDetailActivity và truyền appointment_id
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AppointmentDetailActivity.class);
            intent.putExtra("appointment_id", appointment.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    /**
     * Hàm hỗ trợ chuyển chuỗi ngày yyyy-MM-dd thành dd/MM/yyyy và ghép giờ HH:mm
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemTitle;
        TextView tvItemLocation;
        TextView tvItemDateTime;
        TextView tvItemReminder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemLocation = itemView.findViewById(R.id.tvItemLocation);
            tvItemDateTime = itemView.findViewById(R.id.tvItemDateTime);
            tvItemReminder = itemView.findViewById(R.id.tvItemReminder);
        }
    }
}
