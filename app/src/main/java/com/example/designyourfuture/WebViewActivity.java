package com.example.designyourfuture;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // WebView 초기화
        webView = findViewById(R.id.webview);

        // JavaScript 활성화
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        // "url" 키로 전달된 URL 값을 가져옵니다.
        String link = getIntent().getStringExtra("url"); // "link" -> "url"로 변경

        if (link != null && !link.isEmpty()) {
            // "http://" 또는 "https://"가 없으면 추가
            if (!link.startsWith("http://") && !link.startsWith("https://")) {
                link = "http://" + link; // http:// 또는 https://가 없으면 추가
            }
            Log.d("WebView", "Loading URL: " + link);
            webView.loadUrl(link);  // URL을 WebView에 로드합니다.
        } else {
            Log.e("WebView", "Invalid URL: " + link);
        }

        // WebViewClient 설정하여 외부 브라우저가 아닌 WebView 내에서 페이지가 열리도록 설정
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();  // SSL 인증서 오류 무시 (보안상 위험할 수 있으니 주의)
            }
        });
    }
}
