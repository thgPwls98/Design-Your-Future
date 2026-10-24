package com.example.designyourfuture;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupMainActivity extends AppCompatActivity {

    private FirebaseAuth auth;  // Firebase 인증 객체
    private FirebaseFirestore firestore;  // Firestore 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();  // Firebase 인스턴스 가져오기
        firestore = FirebaseFirestore.getInstance();

        Button registerButton = findViewById(R.id.signup_button);  // 회원가입 버튼 객체 생성

        registerButton.setOnClickListener(view -> registerUser());  // 클릭 시 registerUser 함수 호출

    }
    private void registerUser() {
        EditText usernameEditText = findViewById(R.id.signup_username);
        EditText emailEditText = findViewById(R.id.signup_email); // ui에 추가해줘ㅠㅠ
        EditText passwordEditText = findViewById(R.id.signup_password);

        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // 입력값 검증
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase 권한으로 email, password를 생성
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Firestore에 사용자 세부 정보 저장
                saveUserData(username, email);
                Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();  // 메인 액티비티로 이동
            } else {
                // 회원가입 실패 시 메시지 표시
                Toast.makeText(this, "회원가입 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData(String username, String email) {
        Map<String, Object> user = new HashMap<>();  // HashMap 생성
        user.put("username", username);
        user.put("email", email);

        firestore.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference ->
                        Log.d("RegisterActivity", "문서 추가 성공: " + documentReference.getId())
                )
                .addOnFailureListener(e ->
                        Log.e("RegisterActivity", "문서 추가 실패", e)
                );
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);  // 메인 액티비티로 이동
        startActivity(intent);
        finish();  // 현재 액티비티 종료
    }

}