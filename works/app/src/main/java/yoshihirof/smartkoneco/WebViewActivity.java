package yoshihirof.smartkoneco;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static yoshihirof.smartkoneco.R.id.webView;

public class WebViewActivity extends AppCompatActivity {
    /* アプリ上でウェブサイトを見るためのプログラム(主にシラバス) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent intent = this.getIntent();
        String text = intent.getStringExtra("URI");
        WebView myWebView = (WebView)this.findViewById(webView);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(false);
        myWebView.loadUrl(text);
    }
}
