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
import android.widget.Button;
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

    Button btnExit;
    TextView tvName1;
    TextView tvPassword;
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
        btnExit=(Button)getActivity().findViewById(R.id.btnExit);
        tvName1=(TextView)getActivity().findViewById(R.id.tvName1);
        tvPassword=(TextView)getActivity().findViewById(R.id.tvPassword);
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
    }
}