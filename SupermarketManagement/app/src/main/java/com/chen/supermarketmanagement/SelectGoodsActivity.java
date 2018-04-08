package com.chen.supermarketmanagement;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.chen.supermarketmanagement.bean.Goods;
import com.chen.supermarketmanagement.utils.CONSTANT;
import com.chen.supermarketmanagement.utils.NetUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
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

public class SelectGoodsActivity extends AppCompatActivity {
    SimpleAdapter adapter = null;
    ListView lvSelectGoods=null;
    ProgressDialog pDialog = null;
    List<Map<String, Object>> data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_goods);
        lvSelectGoods=(ListView)findViewById(R.id.lvSelectGoods);
        // 数据
        data = new ArrayList<Map<String, Object>>();

        // 适配器
        // context: 上下文
        // data: 数据
        // resource: 每一行的布局方式
        // from: Map中的key
        // to: 布局中的组件id
        adapter = new SimpleAdapter(this, data, R.layout.activity_item_select_goods,
                new String[] { "name", "price", "num", "id","type"}, new int[] {R.id.tvName, R.id.tvPrice, R.id.tvNum,R.id.tvId,R.id.tvType });
        // 绑定
        lvSelectGoods.setAdapter(adapter);
        // 事件处理
        lvSelectGoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(SelectGoodsActivity.this,
                        EditGoodsActivity.class);
                // 传递数据
                intent.putExtra("row", (Serializable) data.get(position)); // Map
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (!NetUtils.check(SelectGoodsActivity.this)) {
            Toast.makeText(SelectGoodsActivity.this,
                    getString(R.string.network_check), Toast.LENGTH_SHORT)
                    .show();
            return; // 后续代码不执行
        }

        // 进度对话框
        pDialog=ProgressDialog.show(SelectGoodsActivity.this,null,"正在连接...",false,true);

        new Thread() {
            public void run() {
                // 获取message
                Message msg = handler.obtainMessage();

                HttpPost post = new HttpPost(CONSTANT.HOST
                        + "/ListServlet");
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
                                        }else if(nodeName!= null &&"pnum".equals(nodeName)){
                                            goods.setPnum(Integer.parseInt(pullParser.nextText().trim()));
                                        }else if("type".equals(nodeName)){
                                            goods.setType(pullParser.nextText());
                                        }else if("description".equals(nodeName)){
                                            goods.setDescription(pullParser.nextText());
                                        }else if("price2".equals(nodeName)){
                                            goods.setPrice2(Double.parseDouble(pullParser.nextText().trim()));
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

            };
        }.start();
    }

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
                        row.put("num", "数量:"+goods1.getPnum() );
                        row.put("type", "类型:"+goods1.getType() );
                        row.put("description","描述:"+goods1.getDescription());
                        row.put("id","id:"+goods1.getId());
                        row.put("price2", "进价:"+goods1.getPrice2() );
                        data.add(row);
                    }
                    adapter.notifyDataSetChanged();

                    break;
                case 2:
                    Toast.makeText(SelectGoodsActivity.this, "服务器错误，请重试",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(SelectGoodsActivity.this, "请重新登录",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        };
    };
}
