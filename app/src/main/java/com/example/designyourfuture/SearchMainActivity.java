package com.example.designyourfuture;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.designyourfuture.adapter.SearchResultAdapter;
import com.example.designyourfuture.model.SearchResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.DividerItemDecoration;

public class SearchMainActivity extends AppCompatActivity {
    private EditText searchEditText;
    private ImageButton searchButton;
    private RecyclerView searchResultsRecyclerView;
    private SearchResultAdapter adapter;
    private List<SearchResult> searchResultsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_main);

        // UI 요소 초기화
        searchEditText = findViewById(R.id.search_edit_text);
        searchResultsRecyclerView = findViewById(R.id.search_results_recyclerview);
        searchButton = findViewById(R.id.search_button);

        // RecyclerView 설정
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsList = new ArrayList<>();
        adapter = new SearchResultAdapter(searchResultsList);
        searchResultsRecyclerView.setAdapter(adapter);

        // DividerItemDecoration 추가
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        searchResultsRecyclerView.addItemDecoration(divider);

        // Intent로 전달된 키워드 받기
        String keyword = getIntent().getStringExtra("SEARCH_KEYWORD");

        // 키워드가 있을 경우 EditText에 설정
        if (keyword != null && !keyword.isEmpty()) {
            searchEditText.setText(keyword);
            searchAndCrawl(keyword);  // 자동으로 검색 수행
        }

        // Search 버튼 클릭 리스너 설정
        searchButton.setOnClickListener(v -> {
            String keywordInput = searchEditText.getText().toString().trim();

            if (!keywordInput.isEmpty()) {
                searchAndCrawl(keywordInput);  // 서버에 키워드 전송
            } else {
                Toast.makeText(SearchMainActivity.this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchAndCrawl(String searchKeyword) {
        String url = "https://10.22.9.159:5000/search_and_crawl";

        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("keyword", searchKeyword);

            // JsonObjectRequest 생성 (Volley를 사용)
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonRequest,
                    response -> {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(SearchMainActivity.this, message, Toast.LENGTH_SHORT).show();

                            // 크롤링 완료 후 Firestore에서 데이터를 가져오는 기능 추가
                            getDataFromFirestore();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(SearchMainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
            );

            // 타임아웃 설정 및 재시도 정책
            request.setRetryPolicy(new DefaultRetryPolicy(
                    30000,  // 30초 타임아웃
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,  // 최대 재시도 횟수
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));  // 재시도 간격

            // Volley 요청 큐에 추가
            Volley.newRequestQueue(this).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Firestore에서 데이터를 가져오는 기능
    private void getDataFromFirestore() {
        String url = "https://10.22.9.159:5000/get_data"; // Flask 서버 엔드포인트

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        List<SearchResult> searchResults = new ArrayList<>();

                        // 데이터 추가
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataItem = dataArray.getJSONObject(i);
                            String title = dataItem.getString("title");
                            String host = dataItem.getString("host");
                            String period = dataItem.getString("period");
                            String status = dataItem.getString("status");
                            String link = dataItem.getString("link");

                            searchResults.add(new SearchResult(title, host, period, status, link));
                        }

                        // 어댑터에 데이터 추가
                        searchResultsList.clear();
                        searchResultsList.addAll(searchResults);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SearchMainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(SearchMainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}