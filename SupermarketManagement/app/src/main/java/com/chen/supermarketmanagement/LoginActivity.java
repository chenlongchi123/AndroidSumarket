package com.chen.supermarketmanagement;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import android.text.Html;
import android.text.TextUtils;

import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;


import com.chen.supermarketmanagement.bean.Goods;
import com.chen.supermarketmanagement.bean.Othercost;
import com.chen.supermarketmanagement.bean.User;
import com.chen.supermarketmanagement.utils.CONSTANT;
import com.chen.supermarketmanagement.utils.NetUtils;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.data;


public class LoginActivity extends AppCompatActivity {
    Button btnLogin;

    EditText etAccount;
    EditText etPassword;
    CheckBox ckBox;
    TextView tvRegister;
    ProgressDialog pDialog=null;
    List<Map<String, Object>> data = null;


    Handler handler=new Handler(){
        //回调方法
        public void handleMessage(Message msg){
            //关闭对话框
            if (pDialog!=null){
                pDialog.dismiss();
            }
            switch (msg.what){
                case 1:

                    int result= msg.arg1;
                    if (result==0){
                        etAccount.selectAll();
                        etAccount.setError("用户名或密码错误");
                        etAccount.requestFocus();
                    }else {
                        SharedPreferences pref=getSharedPreferences("user2", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=pref.edit();
                        //记录jessionid
                        //记录用户名或密码
                        if (ckBox.isChecked()){
                            editor.putString("username1",etAccount.getText().toString());
                            editor.putString("password1",etPassword.getText().toString());
                        }
                        else{
                            //清空以前的登录信息
                            editor.remove("username1");
                            editor.remove("password1");
                        }
                        editor.commit();//最后要提交
                        data = new ArrayList<Map<String, Object>>();
                        data.clear();
                        List<User> lists=(List<User>) msg.obj;
                        for(int i=0;i<lists.size();i++) {
                            User user1 = lists.get(i);
                            Map<String, Object> row = new HashMap<String, Object>();
                            row.put("name", "用户名:" + user1.getName());
                            row.put("password", "密码:" + user1.getPassword());
                            row.put("email", "邮箱:" + user1.getEmail());
                            row.put("status", "权限:" + user1.getStatus());
                            row.put("id", "id:" + user1.getId());
                            data.add(row);
                        }
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        // 传递数据
                        intent.putExtra("row",(Serializable) data.get(0)); // Map
                        startActivity(intent);
                        //关闭
                        LoginActivity.this.finish();
                    }
                    break;
                case 2:
                    Toast.makeText(LoginActivity.this,"服务器错误，请重试",Toast.LENGTH_SHORT).show();
                    break;
            }

        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin=(Button)findViewById(R.id.Login);
        tvRegister=(TextView) findViewById(R.id.tvRegister);
        etAccount=(EditText) findViewById(R.id.etAccount);
        etPassword=(EditText) findViewById(R.id.etPassword1);
        ckBox=(CheckBox)findViewById(R.id.ckBox);
//忘记密码链接

        tvRegister.setText(Html.fromHtml("<a href=\"http://16593r4j74.imwork.net:31959/SupermarketManagement\">注册账号？</a>"));
        tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
        //绑定监听器
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etAccount.getText().toString())){
                    etAccount.setError("请输入用户名");
                    etAccount.requestFocus();
                }
                else if(TextUtils.isEmpty(etPassword.getText().toString())){
                    etPassword.setError("请输入密码");
                    etPassword.requestFocus();//移动光标到EditText处
                }
                else {
                    if(!NetUtils.check(LoginActivity.this)){
                        Toast.makeText(LoginActivity.this,getString(R.string.network_check),Toast.LENGTH_SHORT).show();
                        return;//后续代码不可用
                    }
                    //进度对话框

                    pDialog=ProgressDialog.show(LoginActivity.this,null,"正在连接...",false,true);
                    new Thread(new Runnable(){
                        @Override
                        public void  run(){
                            Message msg=handler.obtainMessage();

                            //访问服务器端，验证用户名、密码
                            //发送请求

                            HttpPost post=new  HttpPost(CONSTANT.HOST + "/LoginServlet");
                            //设置参数
                            List<BasicNameValuePair> params=new ArrayList<BasicNameValuePair>();
                            params.add(new BasicNameValuePair("name",etAccount.getText().toString()));
                            params.add(new BasicNameValuePair("password",etPassword.getText().toString()));

                            UrlEncodedFormEntity entity;

                            try{
                                entity=new UrlEncodedFormEntity(params,"UTF-8");
                                post.setEntity(entity);

                                //超时设置
                                DefaultHttpClient client=new DefaultHttpClient();
                                client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,CONSTANT.REQUEST_TIMEOUT);
                                client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,CONSTANT.SO_TIMEOUT);
                                HttpResponse response=client.execute(post);
                                if(response.getStatusLine().getStatusCode()==200){
                                    Log.v("hehe","ok");

                             /*       //解析数据
                                    StringBuilder builder = new StringBuilder();
                                    HttpEntity httpEntity = response.getEntity();//得到一个http实体
                                    InputStream inputStream = httpEntity.getContent();//得到内容
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                                    String str;
                                    //打印返回的内容（可以看到内容是xml）
                                    while ((str = reader.readLine()) != null)
                                    {
//                                        Log.v("hehe", str);
                                        builder.append(str);
                                       if (str.startsWith("<!")){
                                           msg.what=1;
                                           msg.arg1=0;
                                       }else if (str.startsWith("<?")) {
                                           msg.what=1;
                                           msg.arg1=1;
                                       }
                                    }*/
                                    List<User> lists=null;
                                    User user=null;
                                    try {

                                        XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
                                        //获取XmlPullParser实例
                                        XmlPullParser pullParser=factory.newPullParser();
                                        HttpEntity httpEntity = response.getEntity();//得到一个http实体
                                        InputStream inputStream = httpEntity.getContent();//得到内容
                                        pullParser.setInput(inputStream, "UTF-8");
                                        //开始
                                        int eventType=pullParser.getEventType();
                                        while(eventType!=XmlPullParser.END_DOCUMENT){
                                            String nodeName=pullParser.getName();
                                            switch (eventType) {
                                                //文档开始
                                                case XmlPullParser.START_DOCUMENT:
                                                    lists=new ArrayList<User>();
                                                    break;

                                                //开始节点
                                                case XmlPullParser.START_TAG:
                                                    if("user".equals(nodeName)){
                                                        user=new User();
                                                        user.setId(Integer.parseInt(pullParser.getAttributeValue(0)));
                                                    }else if("name".equals(nodeName)){
                                                        user.setName(pullParser.nextText());
                                                    }else if("password".equals(nodeName)){
                                                        user.setPassword(pullParser.nextText());
                                                    }else if("email".equals(nodeName)){
                                                        user.setEmail(pullParser.nextText());
                                                    }else if("status".equals(nodeName)){
                                                        user.setStatus(pullParser.nextText());
                                                    }
                                                    break;
                                                //结束节点
                                                case XmlPullParser.END_TAG:
                                                    if("user".equals(nodeName)){
                                                        lists.add(user);
                                                        user=null;
                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }
                                            // 手动的触发下一个事件
                                            eventType=pullParser.next();
                                        }
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                        msg.arg1=0;
                                    }

                                    // 发送消息
                                    msg.arg1=1;
                                    msg.obj = lists;
                                    msg.what = 1;
                                }else {
                                    msg.what=2;
                                }
                                //关闭连接
                                client.getConnectionManager().shutdown();
                                handler.sendMessage(msg);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (ClientProtocolException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        };
                    }).start();

                }
            }
        });
    }



}
