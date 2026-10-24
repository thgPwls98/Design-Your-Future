package com.example.designyourfuture.model;

public class FirestoreDataModel {
    private String title;
    private String period;
    private String host;
    private String status;
    private String link;  // 링크를 저장할 필드명 변경

    // 기본 생성자 추가
    public FirestoreDataModel() {
        // Firebase에서 객체를 변환할 때 사용되는 기본 생성자
    }

    // 기존의 생성자 유지
    public FirestoreDataModel(String title, String period, String host, String status, String link) {
        this.title = title;
        this.period = period;
        this.host = host;
        this.status = status;
        this.link = link;  // link로 초기화
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLink() {
        return link;  // link를 반환하는 메소드
    }

    public void setLink(String link) {
        this.link = link;
    }
}
