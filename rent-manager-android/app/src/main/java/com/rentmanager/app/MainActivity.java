package com.rentmanager.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

private static final String HOME_URL =
"https://script.google.com/macros/s/AKfycbya8DK9ahttciYue5H8E68l9LJ4igvqiSz7cF3xPzBAckcxDpZMN0GBFIhH0q_wgx2s/exec";

private WebView webView;
private SwipeRefreshLayout refresh;
private TextView offline;

@SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_main);

webView = findViewById(R.id.webview);
refresh = findViewById(R.id.refresh);
offline = findViewById(R.id.offline);

// Keep website login cookies enabled.
CookieManager cookieManager = CookieManager.getInstance();
cookieManager.setAcceptCookie(true);
cookieManager.setAcceptThirdPartyCookies(webView, true);

WebSettings s = webView.getSettings();
s.setJavaScriptEnabled(true);
s.setDomStorageEnabled(true);
s.setDatabaseEnabled(true);
s.setSupportZoom(false);
s.setBuiltInZoomControls(false);
s.setDisplayZoomControls(false);
s.setMediaPlaybackRequiresUserGesture(false);
s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
s.setUserAgentString(
s.getUserAgentString() + " RentManagerAndroid/1.0"
);

webView.setWebChromeClient(new WebChromeClient());
webView.setBackgroundColor(Color.WHITE);

webView.setWebViewClient(new WebViewClient() {

@Override
public boolean shouldOverrideUrlLoading(
WebView view,
WebResourceRequest request) {
return handleExternalUrl(request.getUrl().toString());
}

@Override
public boolean shouldOverrideUrlLoading(
WebView view,
String url) {
return handleExternalUrl(url);
}

private boolean handleExternalUrl(String url) {
Uri uri = Uri.parse(url);
String scheme = uri.getScheme();

if (scheme == null) {
return false;
}

boolean isWhatsApp =
scheme.equalsIgnoreCase("whatsapp")
|| url.startsWith("https://wa.me/")
|| url.startsWith("https://api.whatsapp.com/send")
|| url.startsWith("http://wa.me/");

if (!isWhatsApp) {
return false;
}

try {
Intent intent = new Intent(Intent.ACTION_VIEW, uri);
startActivity(intent);
return true;
} catch (Exception e) {
if (scheme.equalsIgnoreCase("whatsapp")) {
String phone = uri.getQueryParameter("phone");
String message = uri.getQueryParameter("text");

if (phone != null && !phone.isEmpty()) {
Uri.Builder builder = Uri.parse("https://wa.me/")
.buildUpon()
.appendPath(phone);

if (message != null) {
builder.appendQueryParameter("text", message);
}

try {
startActivity(new Intent(
Intent.ACTION_VIEW, builder.build()));
} catch (Exception ex) {
android.widget.Toast.makeText(
MainActivity.this,
"WhatsApp link open nahi ho paya.",
android.widget.Toast.LENGTH_LONG
).show();
}
}
return true;
}
return true;
}
}

@Override
public void onPageFinished(WebView view, String url) {
refresh.setRefreshing(false);
offline.setVisibility(View.GONE);
webView.setVisibility(View.VISIBLE);
}

@Override
public void onReceivedError(
WebView view,
WebResourceRequest request,
WebResourceError error) {
if (request.isForMainFrame()) {
refresh.setRefreshing(false);
showOffline();
}
}
});


// IMPORTANT: only allow pull-to-refresh when WebView is
// already at the very top. Scrolling within the page should
// not accidentally trigger a reload.
refresh.setOnChildScrollUpCallback(
(parent, child) -> webView.canScrollVertically(-1)
);

refresh.setOnRefreshListener(() -> {
if (hasInternet()) {
webView.reload();
} else {
refresh.setRefreshing(false);
showOffline();
}
});

loadHome();

getOnBackPressedDispatcher().addCallback(
this,
new OnBackPressedCallback(true) {
@Override
public void handleOnBackPressed() {
if (webView.canGoBack()) {
webView.goBack();
} else {
finish();
}
}
}
);
}

private void loadHome() {
if (hasInternet()) {
offline.setVisibility(View.GONE);
webView.setVisibility(View.VISIBLE);
webView.loadUrl(HOME_URL);
} else {
showOffline();
}
}

private void showOffline() {
webView.setVisibility(View.GONE);
offline.setVisibility(View.VISIBLE);
}

private boolean hasInternet() {
ConnectivityManager cm =
(ConnectivityManager) getSystemService(
Context.CONNECTIVITY_SERVICE
);

if (cm == null) {
return false;
}

android.net.Network n = cm.getActiveNetwork();

if (n == null) {
return false;
}

NetworkCapabilities c = cm.getNetworkCapabilities(n);

return c != null
&& (c.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
|| c.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
|| c.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
}
}




