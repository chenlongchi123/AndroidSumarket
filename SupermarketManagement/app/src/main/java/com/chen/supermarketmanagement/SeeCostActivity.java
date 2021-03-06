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

import com.chen.supermarketmanagement.bean.Cost;
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

public class SeeCostActivity extends AppCompatActivity {
    TextView start_time;
    TextView end_time;
    SimpleAdapter adapter = null;
    ListView lvSeeCost=null;
    ListView lvSeeCostBytime=null;
    ProgressDialog pDialog = null;
    List<Map<String, Object>> data = null;
    List<Map<String, Object>> data2=null;
    Button seebtn;
    List<BasicNameValuePair> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_cost);
        lvSeeCost=(ListView)findViewById(R.id.lvSeeCost);
        lvSeeCostBytime=(ListView)findViewById(R.id.lvSeeCostBytime);
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
                SeeCostActivity.this, c3.getTimeInMillis(),
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

                new DatePickerDialog(SeeCostActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        Calendar c2 = Calendar.getInstance();
                        c2.set(year, monthOfYear, dayOfMonth);
                        String weekDay = DateUtils.formatDateTime(
                                SeeCostActivity.this, c2.getTimeInMillis(),
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

                new DatePickerDialog(SeeCostActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        Calendar c2 = Calendar.getInstance();
                        c2.set(year, monthOfYear, dayOfMonth);
                        String weekDay = DateUtils.formatDateTime(
                                SeeCostActivity.this, c2.getTimeInMillis(),
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
        adapter = new SimpleAdapter(this, data, R.layout.activity_item_see_cost,
                new String[] { "csprice", "jjprice", "hfprice","profit"}, new int[] {R.id.tvCsprice, R.id.tvJjprice, R.id.tvHfprice,R.id.tvProfit });
        // 绑定
        lvSeeCost.setAdapter(adapter);
        seebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvSeeCostBytime.setAdapter(null);
                new Thread(myable).start();
                adapter = new SimpleAdapter(SeeCostActivity.this, data, R.layout.activity_item_see_cost,
                        new String[] { "csprice", "jjprice", "hfprice","profit"}, new int[] {R.id.tvCsprice, R.id.tvJjprice, R.id.tvHfprice,R.id.tvProfit });
                // 绑定
                lvSeeCostBytime.setAdapter(adapter);
            }
        });

    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (!NetUtils.check(SeeCostActivity.this)) {
            Toast.makeText(SeeCostActivity.this,
                    getString(R.string.network_check), Toast.LENGTH_SHORT)
                    .show();
            return; // 后续代码不执行
        }

        // 进度对话框
        pDialog=ProgressDialog.show(SeeCostActivity.this,null,"正在连接...",false,true);
        new Thread(myable1).start();

    }

    Runnable myable1 = new Runnable()  {
        public void run() {
            // 获取message
            Message msg = handler.obtainMessage();
            data2 = new ArrayList<Map<String, Object>>();
            data2.clear();
            Map<String, Object> row = new HashMap<String, Object>();
            row.put("start_time", "0000-00-00");
            row.put("end_time",  end_time.getText().toString());

            data2.add(row);
            lvSeeCostBytime.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent=new Intent(SeeCostActivity.this,MingXiActivity.class);
                    // 传递数据
                    intent.putExtra("row",(Serializable) data2.get(position)); // Map
                    startActivity(intent);
                    //关闭
                    SeeCostActivity.this.finish();
                }
            });
            lvSeeCost.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent=new Intent(SeeCostActivity.this,MingXiActivity.class);
                    // 传递数据
                    intent.putExtra("row",(Serializable) data2.get(position)); // Map
                    startActivity(intent);
                    //关闭
                    SeeCostActivity.this.finish();
                }
            });

            HttpPost post = new HttpPost(CONSTANT.HOST + "/SeeBillServlet");
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
                    List<Cost> lists=null;
                    Cost cost=null;
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
                                    lists=new ArrayList<Cost>();
                                    break;

                                //开始节点
                                case XmlPullParser.START_TAG:
                                    if("user".equals(nodeName)){
                                        cost=new Cost();
                                    }else if("jjprice".equals(nodeName)){
                                        cost.setJjprice(Double.parseDouble(pullParser.nextText().trim()));
                                    }else if("csprice".equals(nodeName)){
                                        cost.setCsprice(Double.parseDouble(pullParser.nextText().trim()));
                                    }else if("hfprice".equals(nodeName)){
                                        cost.setHfprice(Double.parseDouble(pullParser.nextText().trim()));
                                    }
                                    break;
                                //结束节点
                                case XmlPullParser.END_TAG:
                                    if("user".equals(nodeName)){
                                        lists.add(cost);
                                        cost=null;
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
                HttpPost post1=new  HttpPost(CONSTANT.HOST + "/SeeBillByTime");
                data2 = new ArrayList<Map<String, Object>>();
                data2.clear();
                Map<String, Object> row = new HashMap<String, Object>();
                row.put("start_time",  start_time.getText().toString());
                row.put("end_time",  end_time.getText().toString());

                data2.add(row);
                lvSeeCostBytime.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent intent=new Intent(SeeCostActivity.this,MingXiActivity.class);
                        // 传递数据
                        intent.putExtra("row",(Serializable) data2.get(position)); // Map
                        startActivity(intent);
                        //关闭
                        SeeCostActivity.this.finish();
                    }
                });
                lvSeeCost.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent intent=new Intent(SeeCostActivity.this,MingXiActivity.class);
                        // 传递数据
                        intent.putExtra("row",(Serializable) data2.get(position)); // Map
                        startActivity(intent);
                        //关闭
                        SeeCostActivity.this.finish();
                    }
                });
                //设置参数
                params=new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("startTime",start_time.getText().toString()));
                params.add(new BasicNameValuePair("endTime",end_time.getText().toString()));


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
                    List<Cost> lists=null;
                    Cost cost=null;
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
                                    lists=new ArrayList<Cost>();
                                    break;

                                //开始节点
                                case XmlPullParser.START_TAG:
                                    if("user".equals(nodeName)){
                                        cost=new Cost();
                                    }else if("jjprice".equals(nodeName)){
                                        cost.setJjprice(Double.parseDouble(pullParser.nextText().trim()));
                                    }else if("csprice".equals(nodeName)){
                                        cost.setCsprice(Double.parseDouble(pullParser.nextText().trim()));
                                    }else if("hfprice".equals(nodeName)){
                                        cost.setHfprice(Double.parseDouble(pullParser.nextText().trim()));
                                    }
                                    break;
                                //结束节点
                                case XmlPullParser.END_TAG:
                                    if("user".equals(nodeName)){
                                        lists.add(cost);
                                        cost=null;
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
                    List<Cost> lists=(List<Cost>) msg.obj;
                    for(int i=0;i<lists.size();i++){
                        Cost cost=lists.get(i);
                        Map<String, Object> row = new HashMap<String, Object>();
                        Double profit=cost.getCsprice()-cost.getJjprice()-cost.getHfprice();
                        row.put("csprice",  "出售货物总收入:"+cost.getCsprice());
                        row.put("jjprice", "出售货物总进价:"+cost.getJjprice() );
                        row.put("hfprice", "其他总支出:"+cost.getHfprice() );
                        row.put("profit", "收益:"+profit );
                        data.add(row);
                    }
                    adapter.notifyDataSetChanged();

                    break;
                case 2:
                    Toast.makeText(SeeCostActivity.this, "服务器错误，请重试",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(SeeCostActivity.this, "请重新登录",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        };
    };
}
