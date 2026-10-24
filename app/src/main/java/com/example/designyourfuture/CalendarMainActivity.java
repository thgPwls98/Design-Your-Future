package com.example.designyourfuture;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.designyourfuture.adapter.CalendarAdapter; // 올바른 import 경로로 수정
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CalendarMainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private RecyclerView appointmentsRecyclerView;
    private CalendarAdapter calendarAdapter;
    private String selectedDate; // 선택된 날짜

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_main);

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // RecyclerView 설정
        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        calendarAdapter = new CalendarAdapter();
        appointmentsRecyclerView.setAdapter(calendarAdapter);

        // CalendarView 설정
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format("%d-%02d-%02d", year, (month + 1), dayOfMonth); // 날짜 형식 설정
            loadAppointments(selectedDate); // 선택된 날짜의 일정 로드
        });

        // FloatingActionButton 클릭 리스너
        FloatingActionButton addEventButton = findViewById(R.id.add_event_button);
        addEventButton.setOnClickListener(v -> showAddEventDialog());

        // 기본 날짜 로드 (예: 오늘 날짜로 시작)
        selectedDate = getCurrentDate();
        loadAppointments(selectedDate); // 오늘 날짜의 일정 로드
    }

    // 일정 추가 다이얼로그 표시
    private void showAddEventDialog() {
        EditText eventContentEditText = new EditText(this);
        eventContentEditText.setHint("일정을 입력하세요.");

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("일정 추가")
                .setView(eventContentEditText)
                .setPositiveButton("추가", (dialog, which) -> {
                    String eventContent = eventContentEditText.getText().toString().trim();
                    if (!eventContent.isEmpty()) {
                        addEventToFirebase(eventContent); // Firebase에 일정 추가
                    } else {
                        Toast.makeText(this, "일정 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // Firebase에 일정 추가
    private void addEventToFirebase(String eventContent) {
        String userId = auth.getCurrentUser().getUid();
        String readDay = selectedDate;

        // Firestore에서 해당 날짜 문서를 참조
        firestore.collection("users")
                .document(userId)
                .collection("일정")
                .document(readDay)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // 문서가 존재할 경우, 기존의 일정 목록을 가져와서 새로운 일정 추가
                    if (documentSnapshot.exists()) {
                        // 기존 일정 목록을 가져옴
                        List<String> events = (List<String>) documentSnapshot.get("내용");
                        if (events == null) {
                            events = new ArrayList<>();
                        }
                        // 새로운 일정 추가
                        events.add(eventContent);

                        // 기존 문서에 업데이트 (새로운 일정 추가)
                        Map<String, Object> update = new HashMap<>();
                        update.put("내용", events);

                        firestore.collection("users")
                                .document(userId)
                                .collection("일정")
                                .document(readDay)
                                .update(update)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(CalendarMainActivity.this, "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                    loadAppointments(selectedDate); // 일정을 다시 로드
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(CalendarMainActivity.this, "일정 추가 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // 문서가 존재하지 않으면 새로 생성
                        List<String> events = new ArrayList<>();
                        events.add(eventContent);

                        Map<String, Object> event = new HashMap<>();
                        event.put("내용", events);

                        firestore.collection("users")
                                .document(userId)
                                .collection("일정")
                                .document(readDay)
                                .set(event)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(CalendarMainActivity.this, "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                    loadAppointments(selectedDate); // 일정을 다시 로드
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(CalendarMainActivity.this, "일정 추가 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CalendarMainActivity.this, "일정 추가 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // 일정 로드
    private void loadAppointments(String date) {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .collection("일정")
                .document(date)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    calendarAdapter.clear(); // 기존 리스트 비우기
                    if (documentSnapshot.exists()) {
                        List<String> events = (List<String>) documentSnapshot.get("내용");
                        if (events != null) {
                            // 모든 일정을 RecyclerView에 추가
                            for (String eventContent : events) {
                                calendarAdapter.addAppointment(new Appointment(date, eventContent));
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CalendarMainActivity.this, "일정 로딩 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // 현재 날짜 가져오기
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 월은 0부터 시작하므로 1을 더함
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%d-%02d-%02d", year, month, day); // "YYYY-MM-DD" 형식
    }

    // Appointment 클래스
    public static class Appointment {
        private String date;
        private String content;

        public Appointment(String date, String content) {
            this.date = date;
            this.content = content;
        }

        public String getDate() {
            return date;
        }

        public String getContent() {
            return content;
        }
    }
}
