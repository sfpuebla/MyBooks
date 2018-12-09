package com.edu.uoc.mybooks;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {

    private Activity activity = null;

    public MyWebViewClient(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {

        // Debe comprobar si los campos tienen datos y mostrar una alerta
        // avisando al usuario que la compra se ha realizado correctamente

        // En caso contrario avisar de que debe cumplimentar todos los campos


        /*
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
        return true;
        */


        return false;
    }

}



