package com.chen.supermarketmanagement;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.supermarketmanagement.bean.User;
import com.chen.supermarketmanagement.utils.CONSTANT;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFragment extends Fragment {
    Button btnMymap;
    Button btnExit;
    TextView tvName1;
    TextView tvPassword;
    public ImageView imageView;
    public ImageView imageView2;


    public Animation animation1;
    public Animation animation2;

    public TextView text;


    public boolean juage = true;


    public int images[] = new int[] { R.drawable.hailang, R.drawable.hainiao,
            R.drawable.feiji, R.drawable.haiou, R.drawable.haious };


    public int count = 0;


    public Handler handler = new Handler();


    public Runnable runnable = new Runnable() {


        @Override
        public void run() {
// TODO Auto-generated method stub
            AnimationSet animationSet1 = new AnimationSet(true);
            AnimationSet animationSet2 = new AnimationSet(true);
            imageView2.setVisibility(0);
            TranslateAnimation ta = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                    -1f, Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f);
            ta.setDuration(2000);
            animationSet1.addAnimation(ta);
            animationSet1.setFillAfter(true);
            ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                    0f, Animation.RELATIVE_TO_SELF, 0f);
            ta.setDuration(2000);
            animationSet2.addAnimation(ta);
            animationSet2.setFillAfter(true);
//iamgeView 出去  imageView2 进来
            imageView.startAnimation(animationSet1);
            imageView2.startAnimation(animationSet2);
            imageView.setBackgroundResource(images[count % 5]);
            count++;
            imageView2.setBackgroundResource(images[count % 5]);

            text.setText(String.valueOf(count));
            if (juage)
                handler.postDelayed(runnable, 6000);
            Log.i("handler", "handler");
        }
    };





    public void onPause() {
        juage = false;
        super.onPause();
    }
    public MyFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        imageView = (ImageView) getActivity().findViewById(R.id.imageView);
        imageView2 = (ImageView)getActivity().findViewById(R.id.imageView2);
        text=(TextView)getActivity().findViewById(R.id.text);
        text.setText(String.valueOf(count));
//将iamgeView先隐藏，然后显示
        imageView2.setVisibility(4);
        handler.postDelayed(runnable, 2000);
        btnExit=(Button)getActivity().findViewById(R.id.btnExit);
        btnExit.getBackground().setAlpha(230);//0~255透明度值
        tvName1=(TextView)getActivity().findViewById(R.id.tvName1);
        tvPassword=(TextView)getActivity().findViewById(R.id.tvPassword);
        btnMymap=(Button)getActivity().findViewById(R.id.btnMymap);
        btnMymap.getBackground().setAlpha(230);//0~255透明度值
        SharedPreferences read = getActivity().getSharedPreferences("user2", Context.MODE_PRIVATE);
        String name = read.getString("username1", "");
        String pwd = read.getString("password1", "");
        tvName1.setText(name.toString());
        tvPassword.setText(pwd.toString());
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), LoginActivity.class);
                startActivity(intent);

            }

        });
        btnMymap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getActivity(), MyMapActivity.class);
                startActivity(intent);
            }
        });
    }
}




