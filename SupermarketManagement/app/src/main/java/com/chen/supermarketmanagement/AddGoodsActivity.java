package com.chen.supermarketmanagement;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.chen.supermarketmanagement.utils.CONSTANT;
import com.chen.supermarketmanagement.utils.NetUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AddGoodsActivity extends AppCompatActivity {
    EditText etName;
    EditText etPrice;
    EditText etPrice2;
    EditText etPnum;
    EditText etDescription;
    Button btnAddGoods;
    Spinner spType;
    ProgressDialog pDialog = null;
    String action = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods);
        etName=(EditText)findViewById(R.id.etName);
        etName.getBackground().setAlpha(100);//0~255透明度值
        etPrice=(EditText)findViewById(R.id.etPrice);
        etPrice.getBackground().setAlpha(100);//0~255透明度值
        etPrice2=(EditText)findViewById(R.id.etPrice2);
        etPrice2.getBackground().setAlpha(100);//0~255透明度值
        etPnum=(EditText)findViewById(R.id.etPnum);
        etPnum.getBackground().setAlpha(100);//0~255透明度值
        etDescription=(EditText)findViewById(R.id.etDescription);
        etDescription.getBackground().setAlpha(100);//0~255透明度值
        btnAddGoods=(Button) findViewById(R.id.btnAddGoods);
        btnAddGoods.getBackground().setAlpha(230);//0~255透明度值
        spType=(Spinner) findViewById(R.id.spType);
        spType.getBackground().setAlpha(200);//0~255透明度值
        viewInit();
    }
    void viewInit() {
        btnAddGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etName.getText().toString())) {

                    etName.setError("请输入商品名称");
                    etName.requestFocus();//移动光标到EditText处
                } else if (TextUtils.isEmpty(etPrice.getText().toString())) {
                    etPrice.setError("请输入商品价格");
                    etPrice.requestFocus();
                } else if (TextUtils.isEmpty(etPnum.getText().toString())) {
                    etPnum.setError("请输入库存");
                    etPnum.requestFocus();//移动光标到EditText处
                } else {
                    if (!NetUtils.check(AddGoodsActivity.this)) {
                        Toast.makeText(AddGoodsActivity.this,
                                getString(R.string.network_check),
                                Toast.LENGTH_SHORT).show();
                        return; // 后续代码不执行
                    }

                    // 进度对话框

                    pDialog = ProgressDialog.show(AddGoodsActivity.this, null, "正在加载", false, true);

                    // 开始线程
                    action = "update";
                    addThread.start();
                }

            }
        });
    }
    Thread addThread = new Thread() {
        public void run() {
            // 获取message
            Message msg = handler.obtainMessage();

            HttpPost post = new HttpPost(CONSTANT.HOST + "/AddGoodsServlet");

            // 发送请求
            DefaultHttpClient client = new DefaultHttpClient();

            try {
                // 请求参数
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("name",  etName.getText().toString()));
                params.add(new BasicNameValuePair("price", etPrice.getText().toString()));
                params.add(new BasicNameValuePair("pnum",etPnum.getText().toString() ));
                params.add(new BasicNameValuePair("type", spType.getSelectedItem().toString()));
                params.add(new BasicNameValuePair("description", etDescription.getText().toString()));
                params.add(new BasicNameValuePair("price2", etPrice2.getText().toString()));
                params.add(new BasicNameValuePair("action", action));

                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
                post.setEntity(entity);

                // 超时设置
                client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONSTANT.REQUEST_TIMEOUT);
                client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, CONSTANT.SO_TIMEOUT);
                HttpResponse response = client.execute(post);

                // 处理结果
                if (response.getStatusLine().getStatusCode() == 200) {
                    // 发送消息
                    msg.arg1=1;
                    msg.what = 1;
                } else {
                    msg.arg1=-1;
                    msg.what = 2;
                }

                client.getConnectionManager().shutdown();

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                msg.what = 2;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                msg.what = 2;
            } catch (IOException e) {
                e.printStackTrace();
                msg.what = 2;
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                msg.what = 2;
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                msg.what = 3; // 重新登录
            }

            // 发送消息
            handler.sendMessage(msg);

        };
    };
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // 关闭对话框
            if (pDialog != null) {
                pDialog.dismiss();
            }

            switch (msg.what) {
                case 1:
                    int result = msg.arg1;
                    if (result==1) {
                        Toast.makeText(AddGoodsActivity.this, "添加成功",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (result==-1) {
                        Toast.makeText(AddGoodsActivity.this, "添加失败，请重试",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    Toast.makeText(AddGoodsActivity.this, "服务器错误，请重试",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(AddGoodsActivity.this, "请重新登录",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        };
    };
}
