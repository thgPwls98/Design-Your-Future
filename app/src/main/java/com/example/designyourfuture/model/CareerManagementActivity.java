package com.example.designyourfuture.model;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.designyourfuture.R;
import com.example.designyourfuture.adapter.CareerAdapter;


public class CareerManagementActivity extends AppCompatActivity {

    private EditText careerInput;
    private Button addCareerButton;
    private RecyclerView careerList;
    private CareerAdapter adapter;
    private ArrayList<String> careerItems;

    private FirebaseAuth auth; // Firebase 인증 객체
    private FirebaseFirestore firestore; // Firestore 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_career_management);

        // 시스템 창 여백 처리
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // View 초기화
        careerInput = findViewById(R.id.career_input);
        addCareerButton = findViewById(R.id.add_career_button);
        careerList = findViewById(R.id.career_list);

        // RecyclerView 설정
        careerItems = new ArrayList<>();
        adapter = new CareerAdapter(careerItems);
        careerList.setLayoutManager(new LinearLayoutManager(this));
        careerList.setAdapter(adapter);

        // "+" 버튼 클릭 이벤트
        addCareerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = careerInput.getText().toString().trim();
                if (!inputText.isEmpty()) {
                    careerItems.add(inputText); // RecyclerView에 추가
                    adapter.notifyDataSetChanged(); // UI 업데이트
                    saveCareerToFirestore(inputText); // Firestore에 저장
                    careerInput.setText(""); // 입력 필드 초기화
                } else {
                    Toast.makeText(CareerManagementActivity.this, "경력 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveCareerToFirestore(String careerTitle) {
        // 현재 로그인한 사용자 UID 가져오기
        String uid = auth.getCurrentUser().getUid();

        // 저장할 데이터 생성
        Map<String, Object> careerData = new HashMap<>();
        careerData.put("title", careerTitle);
        careerData.put("timestamp", System.currentTimeMillis());

        // Firestore에 데이터 저장
        firestore.collection("users")
                .document(uid) // 사용자의 고유 ID를 기준으로 문서 지정
                .collection("careers") // "careers" 하위 컬렉션
                .add(careerData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "경력 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "경력 저장에 실패했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
