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

public class OtherCostActivity extends AppCompatActivity {
    private TextView time;
    EditText etUse;
    EditText etMoney;
    Button btnAddCost;
    ProgressDialog pDialog = null;
    String action = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_cost);
        time = (TextView)findViewById(R.id.time);
        btnAddCost=(Button)findViewById(R.id.btnAddCost);
        btnAddCost.getBackground().setAlpha(230);//0~255透明度值
        etUse=(EditText)findViewById(R.id.etUse);
        etUse.getBackground().setAlpha(100);//0~255透明度值
        etMoney=(EditText)findViewById(R.id.etMoney);
        etMoney.getBackground().setAlpha(100);//0~255透明度值
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
                OtherCostActivity.this, c3.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_WEEKDAY
                        | DateUtils.FORMAT_ABBREV_WEEKDAY);

        time.setText(oldYear + "-" + (oldMonthOfYear + 1)
                + "-" + oldDayOfMonth + " " + weekDay);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldDateFrom = time.getText().toString();
                int oldYear = Integer.parseInt(oldDateFrom.split(" ")[0]
                        .split("-")[0]);
                int oldMonthOfYear = Integer.parseInt(oldDateFrom.split(" ")[0]
                        .split("-")[1]) - 1;
                int oldDayOfMonth = Integer.parseInt(oldDateFrom.split(" ")[0]
                        .split("-")[2]);

                new DatePickerDialog(OtherCostActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        Calendar c2 = Calendar.getInstance();
                        c2.set(year, monthOfYear, dayOfMonth);
                        String weekDay = DateUtils.formatDateTime(
                                OtherCostActivity.this, c2.getTimeInMillis(),
                                DateUtils.FORMAT_SHOW_WEEKDAY
                                        | DateUtils.FORMAT_ABBREV_WEEKDAY);

                        time.setText(year + "-" + (monthOfYear + 1)
                                + "-" + dayOfMonth + " " + weekDay);
                    }
                }, oldYear, oldMonthOfYear, oldDayOfMonth).show();

            }
        });
        viewInit();
    }
    void viewInit() {
        btnAddCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etUse.getText().toString())) {

                    etUse.setError("请输入使用用途");
                    etUse.requestFocus();//移动光标到EditText处
                }else if (TextUtils.isEmpty(etUse.getText().toString())) {

                    etMoney.setError("请输入使用金额");
                    etMoney.requestFocus();//移动光标到EditText处
                } else {
                    if (!NetUtils.check(OtherCostActivity.this)) {
                        Toast.makeText(OtherCostActivity.this,
                                getString(R.string.network_check),
                                Toast.LENGTH_SHORT).show();
                        return; // 后续代码不执行
                    }

                    // 进度对话框

                    pDialog = ProgressDialog.show(OtherCostActivity.this, null, "正在加载", false, true);

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

            HttpPost post = new HttpPost(CONSTANT.HOST + "/OtherCostServlet");

            // 发送请求
            DefaultHttpClient client = new DefaultHttpClient();

            try {
                // 请求参数
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("oname",  etUse.getText().toString()));
                params.add(new BasicNameValuePair("money",  etMoney.getText().toString()));
                params.add(new BasicNameValuePair("odate", time.getText().toString()));
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
                        Toast.makeText(OtherCostActivity.this, "添加成功",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (result==-1) {
                        Toast.makeText(OtherCostActivity.this, "添加失败，请重试",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    Toast.makeText(OtherCostActivity.this, "服务器错误，请重试",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(OtherCostActivity.this, "请重新登录",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        };
    };
}
