package com.chen.supermarketmanagement;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏模式
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //去除标题行
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);
        //自动登录
        SharedPreferences pref=getSharedPreferences("user",0);
        String username=pref.getString("name","");
        String password=pref.getString("password","");
        if(TextUtils.isEmpty(username)||TextUtils.isEmpty(password)){
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent intent=new Intent();
                    intent.setClass(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();// 结束本Activity
                }
            }, 3000);// 设置执行时间
        }
        else {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();// 结束本Activity
                }
            }, 3000);// 设置执行时间

        }


    }
}
