package com.entrolabs.rbsk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.entrolabs.rbsk.ApiKey.AuthKey;
import com.entrolabs.rbsk.ApiKey.VolleyCallback;
import com.entrolabs.rbsk.Common.Constants;
import com.entrolabs.rbsk.Common.Helper;
import com.entrolabs.rbsk.Common.SessionManager;
import com.entrolabs.rbsk.URL.UrlBase;
import com.entrolabs.rbsk.databinding.ActivityLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarException;

import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    SessionManager sessionManager;
    private ActivityLoginBinding binding;
    Dialog dialog;
    private String user_name= "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        }
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        InitViews();

    }

    private void InitViews() {
        sessionManager = new SessionManager(LoginActivity.this);
        binding.TvSignin.setOnClickListener(this);

    }
    boolean validate() {
        int error = 0;
        String errorTxt = "error", freeze_date = "";

        if (binding.EtUserName.getText().toString().trim().length() == 0) {
            errorTxt = "Username number Cannot be empty";
            error++;
        } else if (binding.EtPassword.getText().toString().trim().length() == 0) {
            errorTxt = "Password Cannot be empty";
            error++;
        }

        if (error > 0) {
            error = 0;
            Helper.t(getApplicationContext(), String.valueOf(errorTxt));
            return false;
        }
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            startActivity(new Intent(LoginActivity.this, SplashActivity.class));
        }
        return false;
    }

    public boolean methodRequiresPermission(String[] perms, int permission) {

        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            return true;
        } else {

            Helper.l("Requesting permissions");
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Need these permissions",
                    permission, perms);
            return false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        // Some permissions have been granted

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.TvSignin:
                String username = binding.EtUserName.getText().toString().trim().replaceAll("\\s", "");
                String password = binding.EtPassword.getText().toString().trim().replaceAll("\\s", "");
       boolean res = validate();
       if(res){
           if (Helper.isNetworkAvailable(getApplicationContext())) {
               if (methodRequiresPermission(Helper.perms, 111) == true) {
                   Map<String, String> params = new LinkedHashMap<String, String>();

                   params.put("login", "true");
                   params.put("username", username);
                   params.put("password", password);
                   GetDATA(Constants.LOGIN, params,dialog);
               }
               else{
                   Helper.t(getApplicationContext(), "some permissions are missing");
               }
           } else {
               Helper.t(getApplicationContext(), "need internet connection");
           }

       }break;



        }

    }

    private void GetDATA(int login, Map<String, String> params, Dialog dialog) {
        if(Helper.isNetworkAvailable(LoginActivity.this)){
            AuthKey.VolleyCallBack(new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    if(login == 1001){
                        sessionManager.createLoginSession(result);
                        finish();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    }

                }

                @Override
                public void onFailure(JSONObject apierror) {
                    try{
                        Helper.t(getApplicationContext(),apierror.getString("error"));

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(String message) {
                    Helper.t(getApplicationContext(), message);
                }

                @Override
                public void onException(String message) {
                    Helper.t(getApplicationContext(), message);
                }

                @Override
                public void onLogout(String message) {
                    sessionManager.logoutUser();
                    finish();
                    startActivity(new Intent(LoginActivity.this, LoginActivity.class));

                }
            }, UrlBase.BASE_URL,params,LoginActivity.this,"show");

        }else{
            Helper.t(getApplicationContext(),"Need Internet Connection");
        }
    }
}