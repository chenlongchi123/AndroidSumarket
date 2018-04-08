package com.chen.supermarketmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.chen.supermarketmanagement.bean.Goods;
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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.R.attr.data;

public class EditGoodsActivity extends AppCompatActivity {
    EditText etName;
    EditText etPrice;
    EditText etPrice2;
    EditText etPnum;
    EditText etDescription;
    Button btnEditGoods;
    Button btnDeleteGoods;
    Spinner spType;
    ProgressDialog pDialog = null;
    String action = "";
    Goods goods=new Goods();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_goods);
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
        btnEditGoods=(Button) findViewById(R.id.btnEditGoods);
        btnEditGoods.getBackground().setAlpha(230);//0~255透明度值
        btnDeleteGoods=(Button) findViewById(R.id.btnDeleteGoods);
        btnDeleteGoods.getBackground().setAlpha(230);//0~255透明度值
        spType=(Spinner) findViewById(R.id.spType);
        spType.getBackground().setAlpha(200);//0~255透明度值
        Intent intent=getIntent();
        Map<String,Object> good=(Map<String,Object>)intent.getSerializableExtra("row");
        String name=(String)good.get("name");
        String price=(String)good.get("price");
        String price2=(String)good.get("price2");
        String num=(String)good.get("num");
        String type=(String)good.get("type");
        String description=(String)good.get("description");
        String id=(String)good.get("id");
        goods.setId(Integer.parseInt(id.split(":")[1]));
        etName.setText(name.split(":")[1]);
        etPrice.setText(price.split(":")[1]);
        etPrice2.setText(price2.split(":")[1]);
        etPnum.setText(num.split(":")[1]);
        int i=0;
        if("日用百货".equals(type.split(":")[1])){
            i=0;
        }else if("电子产品".equals(type.split(":")[1])){
            i=1;
        }else if("床上用品".equals(type.split(":")[1])){
            i=2;
        }
        spType.setSelection(i);
        etDescription.setText(description.split(":")[1]);
        btnEditGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1.数据保存到服务器上
                if (!NetUtils.check(EditGoodsActivity.this)) {
                    Toast.makeText(EditGoodsActivity.this,
                            getString(R.string.network_check),
                            Toast.LENGTH_SHORT).show();
                    return; // 后续代码不执行
                }

                // 进度对话框

                pDialog=ProgressDialog.show(EditGoodsActivity.this,null,"正在加载",false,true);

                // 开始线程
                action = "update";
                editThread.start();

            }
        });
        btnDeleteGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1.数据保存到服务器上
                if (!NetUtils.check(EditGoodsActivity.this)) {
                    Toast.makeText(EditGoodsActivity.this,
                            getString(R.string.network_check),
                            Toast.LENGTH_SHORT).show();
                    return; // 后续代码不执行
                }

                // 进度对话框

                pDialog=ProgressDialog.show(EditGoodsActivity.this,null,"正在加载",false,true);

                // 开始线程
                action = "delete";
                deleteThread.start();

            }
        });
    }
    Thread editThread = new Thread() {
        public void run() {
            // 获取message
            Message msg = handler.obtainMessage();

            HttpPost post = new HttpPost(CONSTANT.HOST + "/EditServlet");

            // 发送请求
            DefaultHttpClient client = new DefaultHttpClient();

            try {
                // 请求参数
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                String id1=String.valueOf(goods.getId());
                params.add(new BasicNameValuePair("id",id1));
                params.add(new BasicNameValuePair("name", etName.getText().toString().trim()));
                params.add(new BasicNameValuePair("price", etPrice.getText().toString().trim()));
                params.add(new BasicNameValuePair("pnum", etPnum.getText().toString().trim()));
                params.add(new BasicNameValuePair("type",spType.getSelectedItem().toString().trim()));
                params.add(new BasicNameValuePair("description", etDescription.getText().toString().trim()));
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
    Thread deleteThread = new Thread() {
        public void run() {
            // 获取message
            Message msg = handler.obtainMessage();

            HttpPost post = new HttpPost(CONSTANT.HOST + "/DeleteServlet");

            // 发送请求
            DefaultHttpClient client = new DefaultHttpClient();

            try {
                // 请求参数
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                String id1=String.valueOf(goods.getId());
                params.add(new BasicNameValuePair("id",id1));
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
                    String info = "修改";
                    if ("delete".equals(action)) {
                        info = "删除";
                    }

                    if (result==1) {

                        Toast.makeText(EditGoodsActivity.this, info + "成功",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (result==-1) {
                        Toast.makeText(EditGoodsActivity.this, info + "失败，请重试",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    Toast.makeText(EditGoodsActivity.this, "服务器错误，请重试",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(EditGoodsActivity.this, "请重新登录",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        };
    };
}
