package com.example.designyourfuture.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.designyourfuture.CalendarMainActivity;
import com.example.designyourfuture.R;

import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.AppointmentViewHolder> {

    private final List<CalendarMainActivity.Appointment> appointments = new ArrayList<>();

    // 일정 추가
    public void addAppointment(CalendarMainActivity.Appointment appointment) {
        appointments.add(appointment);
        notifyItemInserted(appointments.size() - 1);
    }

    // 기존 데이터 지우기
    public void clear() {
        appointments.clear();
        notifyDataSetChanged();
    }

    @Override
    public AppointmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppointmentViewHolder holder, int position) {
        CalendarMainActivity.Appointment appointment = appointments.get(position);
        holder.dateTextView.setText(appointment.getDate());
        holder.contentTextView.setText(appointment.getContent());
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView contentTextView;

        public AppointmentViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
        }
    }
}
