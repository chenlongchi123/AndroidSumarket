package com.chen.supermarketmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.supermarketmanagement.bean.Cost;
import com.chen.supermarketmanagement.bean.Goods;
import com.chen.supermarketmanagement.bean.Othercost;
import com.chen.supermarketmanagement.utils.CONSTANT;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MingXiActivity extends AppCompatActivity {
    ListView lvSeeOther=null;
    ListView lvSellHistory=null;
    List<Map<String, Object>> data = null;
    List<Map<String, Object>> data1 = null;
    SimpleAdapter adapter = null;
    List<BasicNameValuePair> params;
    ProgressDialog pDialog = null;

    String start_time;
    String end_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ming_xi);
        Intent intent=getIntent();
        Map<String,Object> good=(HashMap<String,Object>)intent.getSerializableExtra("row");
        start_time=(String)good.get("start_time");
        end_time=(String)good.get("end_time");
        lvSeeOther=(ListView)findViewById(R.id.lvSeeOther);
        lvSellHistory=(ListView)findViewById(R.id.lvSellHistory);
        new Thread(myable).start();
        new Thread(myable1).start();
        // 数据
        data = new ArrayList<Map<String, Object>>();

        // 适配器
        // context: 上下文
        // data: 数据
        // resource: 每一行的布局方式
        // from: Map中的key
        // to: 布局中的组件id
        adapter = new SimpleAdapter(this, data, R.layout.activity_item_see_other,
                new String[] { "oname", "money", "odate"}, new int[] {R.id.tvUseWhere, R.id.tvUseMoney, R.id.tvUseDate});
        // 绑定
        lvSeeOther.setAdapter(adapter);
        data1 = new ArrayList<Map<String, Object>>();

        // 适配器
        // context: 上下文
        // data: 数据
        // resource: 每一行的布局方式
        // from: Map中的key
        // to: 布局中的组件id
        adapter = new SimpleAdapter(this, data1, R.layout.activity_item_sell_history,
                new String[] { "name", "price", "odate","type"}, new int[] {R.id.tvName, R.id.tvPrice, R.id.tvOdate,R.id.tvType });
        // 绑定
        lvSellHistory.setAdapter(adapter);

    }
    Runnable myable = new Runnable() {

        @Override
        public void run() {
            // 获取message
            Message msg = handler.obtainMessage();

            // 发送请求
            DefaultHttpClient client = new DefaultHttpClient();
            try {
                HttpPost post1=new  HttpPost(CONSTANT.HOST + "/SeeOtherByTime");
                //设置参数
                params=new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("startTime",start_time));
                params.add(new BasicNameValuePair("endTime",end_time));

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
                    List<Othercost> lists=null;
                    Othercost othercost=null;

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
                                    lists=new ArrayList<Othercost>();
                                    break;

                                //开始节点
                                case XmlPullParser.START_TAG:
                                    if("user".equals(nodeName)){
                                        othercost=new Othercost();
                                        othercost.setId(Integer.parseInt(pullParser.getAttributeValue(0)));
                                    }else if("oname".equals(nodeName)){
                                        othercost.setOname(pullParser.nextText());
                                    }else if("money".equals(nodeName)){
                                        othercost.setMoney(Double.parseDouble(pullParser.nextText().trim()));
                                    }else if("odate".equals(nodeName)){
                                        othercost.setOdate(pullParser.nextText());
                                    }
                                    break;
                                //结束节点
                                case XmlPullParser.END_TAG:
                                    if("user".equals(nodeName)){
                                        lists.add(othercost);
                                        othercost=null;
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
                    List<Othercost> lists=(List<Othercost>) msg.obj;
                    for(int i=0;i<lists.size();i++){
                        Othercost othercost1=lists.get(i);
                        Map<String, Object> row = new HashMap<String, Object>();
                        row.put("oname",  "货物名称:"+othercost1.getOname());
                        row.put("money", "价格:"+othercost1.getMoney() );
                        row.put("odate", "日期:"+othercost1.getOdate() );
                        data.add(row);
                    }
                    adapter.notifyDataSetChanged();

                    break;
                case 2:
                    Toast.makeText(MingXiActivity.this, "服务器错误，请重试",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(MingXiActivity.this, "请重新登录",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        };
    };
    Runnable myable1 = new Runnable() {

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
                params.add(new BasicNameValuePair("startTime",start_time));
                params.add(new BasicNameValuePair("endTime",end_time));


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
            handler1.sendMessage(msg);

        }
    };
    Handler handler1 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // 关闭对话框
            if (pDialog != null) {
                pDialog.dismiss();
            }

            // 清空data
            data1.clear();

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
                        data1.add(row);
                    }
                    adapter.notifyDataSetChanged();

                    break;
                case 2:
                    Toast.makeText(MingXiActivity.this, "服务器错误，请重试",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(MingXiActivity.this, "请重新登录",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        };
    };
}
