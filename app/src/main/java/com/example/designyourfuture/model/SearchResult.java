package com.example.designyourfuture.model;

public class SearchResult {
    private String title;
    private String host;
    private String period;
    private String status;
    private String link;

    // 생성자
    public SearchResult(String title, String host, String period, String status, String link) {
        this.title = title;
        this.host = host;
        this.period = period;
        this.status = status;
        this.link = link;
    }

    // getter 메서드
    public String getTitle() {
        return title;
    }

    public String getHost() {
        return host;
    }

    public String getPeriod() {
        return period;
    }

    public String getStatus() {
        return status;
    }

    public String getLink() {
        return link;
    }
}
