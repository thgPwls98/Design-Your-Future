package com.example.designyourfuture.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.designyourfuture.R;
import com.example.designyourfuture.model.SearchResult;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    private List<SearchResult> resultList;

    public SearchResultAdapter(List<SearchResult> resultList) {
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        SearchResult result = resultList.get(position);
        holder.titleText.setText(result.getTitle());
        holder.hostText.setText(result.getHost());
        holder.periodText.setText(result.getPeriod());
        holder.statusText.setText(result.getStatus());
        //holder.linkText.setText(result.getLink());

        // 링크 클릭 리스너 설정 (예: 외부 브라우저로 링크 열기)
        holder.itemView.setOnClickListener(v -> {
            String link = result.getLink();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public static class SearchResultViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, hostText, periodText, statusText ;

        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.title_text);
            hostText = itemView.findViewById(R.id.host_text);
            periodText = itemView.findViewById(R.id.period_text);
            statusText = itemView.findViewById(R.id.status_text);
            //linkText = itemView.findViewById(R.id.link_text);
        }
    }
}
