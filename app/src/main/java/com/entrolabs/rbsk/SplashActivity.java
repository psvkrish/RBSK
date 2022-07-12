package com.entrolabs.rbsk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

import com.entrolabs.rbsk.Common.Helper;
import com.entrolabs.rbsk.Common.RootUtil;
import com.entrolabs.rbsk.Common.SessionManager;

public class SplashActivity extends AppCompatActivity {

    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        }
        setContentView(R.layout.activity_splash);
        InitViews();
    }

    private void InitViews() {
        sessionManager = new SessionManager(SplashActivity.this);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (RootUtil.isDeviceRooted()) {
                    Helper.t(getApplicationContext(), "Device is rooted cannot proceed further");
                } else {
                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String networkOperator = tm.getNetworkOperatorName();
                    if ("Android".equals(networkOperator)) {
                        // Emulator
                        Helper.t(getApplicationContext(), "Cannot run in this device");
                    } else {
                        // Device
                        if (sessionManager.isLoggedIn()) {

                            finish();
                            startActivity(new Intent(SplashActivity.this, MainActivity.class)
                                    .putExtra("sec_name", "")
                                    .putExtra("sec_code", ""));

                        } else {
                            finish();
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));

                        }
                    }

                }
            }
        }, 1000);

    }
}