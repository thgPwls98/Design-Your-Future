package com.example.designyourfuture;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowInsetsController;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.designyourfuture.adapter.FirestoreDataAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirestoreDataAdapter adapter;
    private EditText searchEditText;
    private List<com.example.designyourfuture.model.FirestoreDataModel> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 시스템 바 설정 (API 30 이상에서만 지원됨)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                WindowInsetsController insetsController = getWindow().getInsetsController();
                if (insetsController != null) {
                    // 'BEHAVIOR_SHOW_BARS_BY_SWIPE' 대신 적절한 상수로 수정
                    insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
            } catch (Exception e) {
                Log.e("WindowInsets", "Error adjusting system bars behavior", e);
            }
        }

        setContentView(R.layout.activity_home);

        // RecyclerView 설정
        recyclerView = findViewById(R.id.results_recycler_view);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataList = new ArrayList<>();
        adapter = new FirestoreDataAdapter(dataList, this);
        recyclerView.setAdapter(adapter);

        // Firestore 데이터 로드
        fetchFirestoreData();

        // Menu 버튼 클릭 리스너 설정
        findViewById(R.id.menu_button).setOnClickListener(v -> {
            // MenuMainActivity로 이동
            Intent intent = new Intent(HomeActivity.this, MenuMainActivity.class);
            startActivity(intent);
        });

        // 검색 EditText와 버튼 설정
        searchEditText = findViewById(R.id.search_edit_text);
        ImageButton searchButton = findViewById(R.id.search_button);

        searchButton.setOnClickListener(v -> {
            // 입력된 키워드를 가져옴
            String keyword = searchEditText.getText().toString().trim();
            if (keyword.isEmpty()) {
                Toast.makeText(HomeActivity.this, "검색어를 입력하세요!", Toast.LENGTH_SHORT).show();
            } else {
                // 검색어를 SearchMainActivity로 넘기기
                Intent intent = new Intent(HomeActivity.this, SearchMainActivity.class);
                intent.putExtra("SEARCH_KEYWORD", keyword);  // 키워드를 Intent에 추가
                startActivity(intent);
            }
        });

        // 시스템 바 인셋 처리
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchFirestoreData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("New activity in recent times") // Firestore 컬렉션 이름
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataList.clear(); // 기존 데이터 초기화
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            com.example.designyourfuture.model.FirestoreDataModel data = document
                                    .toObject(com.example.designyourfuture.model.FirestoreDataModel.class);
                            dataList.add(data);
                        }
                        adapter.notifyDataSetChanged(); // RecyclerView 업데이트
                    } else {
                        // 데이터 로드 실패 시 처리
                        Toast.makeText(HomeActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }
    private void searchFirestoreData(String keyword) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("New activity in recent times")
                .whereEqualTo("keywordField", keyword) // "keywordField"는 필드명으로, 실제 컬렉션에서 검색하려는 필드로 변경
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataList.clear(); // 기존 데이터 초기화
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            com.example.designyourfuture.model.FirestoreDataModel data = document
                                    .toObject(com.example.designyourfuture.model.FirestoreDataModel.class);
                            dataList.add(data);
                        }
                        adapter.notifyDataSetChanged(); // RecyclerView 업데이트
                    } else {
                        // 검색 실패 시 처리
                        Toast.makeText(HomeActivity.this, "검색 실패", Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }
}
