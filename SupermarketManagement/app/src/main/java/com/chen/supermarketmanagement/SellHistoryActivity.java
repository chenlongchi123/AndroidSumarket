package com.chen.supermarketmanagement;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.supermarketmanagement.bean.Goods;
import com.chen.supermarketmanagement.utils.CONSTANT;
import com.chen.supermarketmanagement.utils.NetUtils;
import com.google.gson.JsonSyntaxException;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellHistoryActivity extends AppCompatActivity {
    TextView start_time;
    TextView end_time;
    SimpleAdapter adapter = null;
    ListView lvSellHistory=null;
    ProgressDialog pDialog = null;
    List<Map<String, Object>> data = null;
    Button seebtn;
    List<BasicNameValuePair> params;
    boolean Onclick=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_history);
        lvSellHistory=(ListView)findViewById(R.id.lvSellHistory);
        start_time = (TextView)findViewById(R.id.start_time);
        end_time = (TextView)findViewById(R.id.end_time);
        seebtn=(Button)findViewById(R.id.seebtn);
        seebtn.getBackground().setAlpha(230);//0~255透明度值
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
                SellHistoryActivity.this, c3.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_WEEKDAY
                        | DateUtils.FORMAT_ABBREV_WEEKDAY);

        start_time.setText(oldYear + "-" + (oldMonthOfYear + 1)
                + "-" + oldDayOfMonth + " " + weekDay);
        end_time.setText(oldYear + "-" + (oldMonthOfYear + 1)
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

                new DatePickerDialog(SellHistoryActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        Calendar c2 = Calendar.getInstance();
                        c2.set(year, monthOfYear, dayOfMonth);
                        String weekDay = DateUtils.formatDateTime(
                                SellHistoryActivity.this, c2.getTimeInMillis(),
                                DateUtils.FORMAT_SHOW_WEEKDAY
                                        | DateUtils.FORMAT_ABBREV_WEEKDAY);

                        start_time.setText(year + "-" + (monthOfYear + 1)
                                + "-" + dayOfMonth + " " + weekDay);
                    }
                }, oldYear, oldMonthOfYear, oldDayOfMonth).show();

            }
        });
        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldDateFrom = end_time.getText().toString();
                int oldYear = Integer.parseInt(oldDateFrom.split(" ")[0]
                        .split("-")[0]);
                int oldMonthOfYear = Integer.parseInt(oldDateFrom.split(" ")[0]
                        .split("-")[1]) - 1;
                int oldDayOfMonth = Integer.parseInt(oldDateFrom.split(" ")[0]
                        .split("-")[2]);

                new DatePickerDialog(SellHistoryActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        Calendar c2 = Calendar.getInstance();
                        c2.set(year, monthOfYear, dayOfMonth);
                        String weekDay = DateUtils.formatDateTime(
                                SellHistoryActivity.this, c2.getTimeInMillis(),
                                DateUtils.FORMAT_SHOW_WEEKDAY
                                        | DateUtils.FORMAT_ABBREV_WEEKDAY);

                        end_time.setText(year + "-" + (monthOfYear + 1)
                                + "-" + dayOfMonth + " " + weekDay);
                    }
                }, oldYear, oldMonthOfYear, oldDayOfMonth).show();

            }
        });



        // 数据
        data = new ArrayList<Map<String, Object>>();

        // 适配器
        // context: 上下文
        // data: 数据
        // resource: 每一行的布局方式
        // from: Map中的key
        // to: 布局中的组件id
        adapter = new SimpleAdapter(this, data, R.layout.activity_item_sell_history,
                new String[] { "name", "price", "odate","type"}, new int[] {R.id.tvName, R.id.tvPrice, R.id.tvOdate,R.id.tvType });
        // 绑定
        lvSellHistory.setAdapter(adapter);

        seebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvSellHistory.setAdapter(null);
                new Thread(myable).start();
                adapter = new SimpleAdapter(SellHistoryActivity.this, data, R.layout.activity_item_sell_history,
                        new String[] { "name", "price", "odate","type"}, new int[] {R.id.tvName, R.id.tvPrice, R.id.tvOdate,R.id.tvType });
                // 绑定
                lvSellHistory.setAdapter(adapter);
            }
        });

    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (!NetUtils.check(SellHistoryActivity.this)) {
            Toast.makeText(SellHistoryActivity.this,
                    getString(R.string.network_check), Toast.LENGTH_SHORT)
                    .show();
            return; // 后续代码不执行
        }

        // 进度对话框
        pDialog=ProgressDialog.show(SellHistoryActivity.this,null,"正在连接...",false,true);

        new Thread(myable1).start();
    }
    Runnable myable1 = new Runnable() {

        @Override
        public void run() {
            // 获取message
            Message msg = handler.obtainMessage();

            HttpPost post = new HttpPost(CONSTANT.HOST + "/SellHistoryServlet");
            // 发送请求
            DefaultHttpClient client = new DefaultHttpClient();
            try {

                // 超时设置
                client.getParams().setParameter(
                        CoreConnectionPNames.CONNECTION_TIMEOUT,
                        CONSTANT.REQUEST_TIMEOUT);
                client.getParams().setParameter(
                        CoreConnectionPNames.SO_TIMEOUT,
                        CONSTANT.SO_TIMEOUT);
                HttpResponse response = client.execute(post);
             // 处理结果
                if (response.getStatusLine().getStatusCode() == 200) {

//                        String json = EntityUtils.toString(response.getEntity());
//                        Log.v("hehhehe",json.toString());
                    List<Goods> lists=null;
                    Goods goods=null;
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
                                    lists=new ArrayList<Goods>();
                                    break;

                                //开始节点
                                case XmlPullParser.START_TAG:
                                    if("user".equals(nodeName)){
                                        goods=new Goods();
                                        goods.setId(Integer.parseInt(pullParser.getAttributeValue(0)));
                                    }else if("name".equals(nodeName)){
                                        goods.setName(pullParser.nextText());
                                    }else if("price".equals(nodeName)){
                                        goods.setPrice(Double.parseDouble(pullParser.nextText().trim()));
                                    }else if("odate".equals(nodeName)){
                                        goods.setOdate(pullParser.nextText());
                                    }else if("type".equals(nodeName)){
                                        goods.setType(pullParser.nextText());
                                    }
                                    break;
                                //结束节点
                                case XmlPullParser.END_TAG:
                                    if("user".equals(nodeName)){
                                        lists.add(goods);
                                        goods=null;
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
                    }

                    // 发送消息
                    msg.obj = lists;
                    msg.what = 1;

                } else {
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

        }
    };
    Runnable myable = new Runnable() {

        @Override
        public void run() {
        // 获取message
        Message msg = handler.obtainMessage();

        // 发送请求
        DefaultHttpClient client = new DefaultHttpClient();
        try {
            HttpPost post1=new  HttpPost(CONSTANT.HOST + "/SellHistoryByTime");
            //设置参数
            params=new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("startTime",start_time.getText().toString()));
            params.add(new BasicNameValuePair("endTime",end_time.getText().toString()));
            Onclick = true;

            //sellThread.interrupt();
            // 超时设置
            client.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT,
                    CONSTANT.REQUEST_TIMEOUT);
            client.getParams().setParameter(
                    CoreConnectionPNames.SO_TIMEOUT,
                    CONSTANT.SO_TIMEOUT);

            UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"UTF-8");
            post1.setEntity(entity);
            HttpResponse response= client.execute(post1);


            // 处理结果
            if (response.getStatusLine().getStatusCode() == 200) {

//                        String json = EntityUtils.toString(response.getEntity());
//                        Log.v("hehhehe",json.toString());
                List<Goods> lists=null;
                Goods goods=null;

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
                                lists=new ArrayList<Goods>();
                                break;

                            //开始节点
                            case XmlPullParser.START_TAG:
                                if("user".equals(nodeName)){
                                    goods=new Goods();
                                    goods.setId(Integer.parseInt(pullParser.getAttributeValue(0)));
                                }else if("name".equals(nodeName)){
                                    goods.setName(pullParser.nextText());
                                    Log.e("hehe",goods.getName().toString());
                                }else if("price".equals(nodeName)){
                                    goods.setPrice(Double.parseDouble(pullParser.nextText().trim()));
                                }else if("odate".equals(nodeName)){
                                    goods.setOdate(pullParser.nextText());
                                }else if("type".equals(nodeName)){
                                    goods.setType(pullParser.nextText());
                                }
                                break;
                            //结束节点
                            case XmlPullParser.END_TAG:
                                if("user".equals(nodeName)){
                                    lists.add(goods);
                                    goods=null;
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
                }

                // 发送消息
                msg.obj = lists;
                msg.what = 1;

            } else {
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

    }
    };
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // 关闭对话框
            if (pDialog != null) {
                pDialog.dismiss();
            }

            // 清空data
            data.clear();

            switch (msg.what) {
                case 1:
                    // Passenger[] => data
                    List<Goods> lists=(List<Goods>) msg.obj;
                    for(int i=0;i<lists.size();i++){
                        Goods goods1=lists.get(i);
                        Map<String, Object> row = new HashMap<String, Object>();
                        row.put("name",  "货物名称:"+goods1.getName());
                        row.put("price", "价格:"+goods1.getPrice() );
                        row.put("odate", "日期:"+goods1.getOdate() );
                        row.put("type", "类型:"+goods1.getType() );
                        row.put("id","id:"+goods1.getId());
                        data.add(row);
                    }
                    adapter.notifyDataSetChanged();

                    break;
                case 2:
                    Toast.makeText(SellHistoryActivity.this, "服务器错误，请重试",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(SellHistoryActivity.this, "请重新登录",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        };
    };
}
