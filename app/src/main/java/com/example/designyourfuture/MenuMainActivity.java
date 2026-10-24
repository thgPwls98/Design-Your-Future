package com.example.designyourfuture;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.designyourfuture.model.CareerManagementActivity;

public class MenuMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menu_main);

        // 시스템 바에 맞춰 padding 설정
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 캘린더 버튼 설정
        Button calendarButton = findViewById(R.id.calendar_button);
        calendarButton.setOnClickListener(v -> {
            // CalendarMainActivity로 이동
            Intent intent = new Intent(MenuMainActivity.this, CalendarMainActivity.class);
            startActivity(intent);
        });

        // 경력 관리 버튼 설정
        Button careerManagementButton = findViewById(R.id.career_management_button);
        careerManagementButton.setOnClickListener(v -> {
            // CareerManagementActivity로 이동
            Intent intent = new Intent(MenuMainActivity.this, CareerManagementActivity.class);
            startActivity(intent);
        });
    }
}
