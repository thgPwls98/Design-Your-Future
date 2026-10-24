package com.example.designyourfuture.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CareerAdapter extends RecyclerView.Adapter<CareerAdapter.CareerViewHolder> {

    private final ArrayList<String> careerItems;

    // 생성자
    public CareerAdapter(ArrayList<String> careerItems) {
        this.careerItems = careerItems;
    }

    @NonNull
    @Override
    public CareerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 항목 레이아웃 설정 (기본 제공 레이아웃 simple_list_item_1 사용)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new CareerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CareerViewHolder holder, int position) {
        // 항목 데이터 설정
        holder.careerTitle.setText(careerItems.get(position));
    }

    @Override
    public int getItemCount() {
        // 항목 개수 반환
        return careerItems.size();
    }

    // ViewHolder 내부 클래스
    public static class CareerViewHolder extends RecyclerView.ViewHolder {
        TextView careerTitle;

        public CareerViewHolder(@NonNull View itemView) {
            super(itemView);
            careerTitle = itemView.findViewById(android.R.id.text1);
        }
    }
}
