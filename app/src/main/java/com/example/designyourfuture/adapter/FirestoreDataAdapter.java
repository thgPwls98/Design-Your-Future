package com.example.designyourfuture.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.designyourfuture.R;
import com.example.designyourfuture.model.FirestoreDataModel;

import java.util.List;

public class FirestoreDataAdapter extends RecyclerView.Adapter<FirestoreDataAdapter.ViewHolder> {

    private List<FirestoreDataModel> dataList;
    private Context context;

    public FirestoreDataAdapter(List<FirestoreDataModel> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_firestore_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirestoreDataModel data = dataList.get(position);
        holder.titleTextView.setText(data.getTitle());
        holder.periodTextView.setText(data.getPeriod());
        holder.hostTextView.setText(data.getHost());
        holder.statusTextView.setText(data.getStatus());

        // 링크를 저장하고 클릭 리스너 설정
        final String link = data.getLink();
        holder.itemView.setOnClickListener(v -> {
            if (link != null && !link.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                context.startActivity(intent);  // 외부 브라우저로 링크 열기
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, periodTextView, hostTextView, statusTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.item_title);
            periodTextView = itemView.findViewById(R.id.item_period);
            hostTextView = itemView.findViewById(R.id.item_host);
            statusTextView = itemView.findViewById(R.id.item_status);
        }
    }
}
