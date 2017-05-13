package com.chen.supermarketmanagement;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.chen.supermarketmanagement.utils.CONSTANT;
import com.chen.supermarketmanagement.utils.NetUtils;
import com.google.gson.JsonSyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SellGoodsActivity extends AppCompatActivity {
    private TextView start_time;
    EditText  etId;
    Button btnSellGoods;
    ProgressDialog pDialog = null;
    String action = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_goods);
        start_time = (TextView)findViewById(R.id.start_time);
        btnSellGoods=(Button)findViewById(R.id.btnSellGoods);
        etId=(EditText)findViewById(R.id.etId);
        //获取当前日期
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String time_now = dateFormat.format(now);
//        start_time.setText(time_now);

        int oldYear = Integer.parseInt(time_now.split(" ")[0]
                .split("-")[0]);
        int oldMonthOfYear = Integer.parseInt(time_now.split(" ")[0]
                .split("-")[1]) - 1;
        int oldDayOfMonth = Integer.parseInt(time_now.split(" ")[0]
                .split("-")[2]);

        Calendar c3 = Calendar.getInstance();
        c3.set(oldYear, oldMonthOfYear, oldDayOfMonth);
        String weekDay = DateUtils.formatDateTime(
                SellGoodsActivity.this, c3.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_WEEKDAY
                        | DateUtils.FORMAT_ABBREV_WEEKDAY);

        start_time.setText(oldYear + "-" + (oldMonthOfYear + 1)
                + "-" + oldDayOfMonth + " " + weekDay);
        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldDateFrom = start_time.getText().toString();
                int oldYear = Integer.parseInt(oldDateFrom.split(" ")[0]
                        .split("-")[0]);
                int oldMonthOfYear = Integer.parseInt(oldDateFrom.split(" ")[0]
                        .split("-")[1]) - 1;
                int oldDayOfMonth = Integer.parseInt(oldDateFrom.split(" ")[0]
                        .split("-")[2]);

                new DatePickerDialog(SellGoodsActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        Calendar c2 = Calendar.getInstance();
                        c2.set(year, monthOfYear, dayOfMonth);
                        String weekDay = DateUtils.formatDateTime(
                                SellGoodsActivity.this, c2.getTimeInMillis(),
                                DateUtils.FORMAT_SHOW_WEEKDAY
                                        | DateUtils.FORMAT_ABBREV_WEEKDAY);

                        start_time.setText(year + "-" + (monthOfYear + 1)
                                + "-" + dayOfMonth + " " + weekDay);
                    }
                }, oldYear, oldMonthOfYear, oldDayOfMonth).show();

            }
        });
        viewInit();
    }
    void viewInit() {
        btnSellGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etId.getText().toString())) {

                    etId.setError("请输入商品ID");
                    etId.requestFocus();//移动光标到EditText处
                } else {
                    if (!NetUtils.check(SellGoodsActivity.this)) {
                        Toast.makeText(SellGoodsActivity.this,
                                getString(R.string.network_check),
                                Toast.LENGTH_SHORT).show();
                        return; // 后续代码不执行
                    }

                    // 进度对话框

                    pDialog = ProgressDialog.show(SellGoodsActivity.this, null, "正在加载", false, true);

                    // 开始线程
                    action = "sellGoods";
                    sellThread.start();
                }

            }
        });
    }
    Thread sellThread = new Thread() {
        public void run() {
            // 获取message
            Message msg = handler.obtainMessage();

            HttpPost post = new HttpPost(CONSTANT.HOST + "/SellGoodsServlet");

            // 发送请求
            DefaultHttpClient client = new DefaultHttpClient();

            try {
                // 请求参数
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("id",  etId.getText().toString()));
                params.add(new BasicNameValuePair("odate", start_time.getText().toString()));
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
                        Toast.makeText(SellGoodsActivity.this, "出售成功",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (result==-1) {
                        Toast.makeText(SellGoodsActivity.this, "出售失败，请重试",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    Toast.makeText(SellGoodsActivity.this, "服务器错误，请重试",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(SellGoodsActivity.this, "请重新登录",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        };
    };
}
