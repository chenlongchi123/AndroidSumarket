package com.chen.supermarketmanagement;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chen.supermarketmanagement.bean.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends FragmentActivity {
    ViewPager pager = null;
    TextView tvTicket = null;
    TextView tvMy = null;
    User user=new User();
    //存放Fragment
    private ArrayList<Fragment> fragmentArrayList;
    //管理Fragment
    private FragmentManager fragmentManager;
    @SuppressWarnings("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=getIntent();
        Map<String,Object> good=(HashMap<String,Object>)intent.getSerializableExtra("row");
        String name=(String)good.get("name");
        String password=(String)good.get("password");
        String email=(String)good.get("email");
        String status=(String)good.get("status");
        String id=(String)good.get("id");
        user.setId(Integer.parseInt(id.split(":")[1]));
        user.setEmail(email.split(":")[1]);
        user.setName(name.split(":")[1]);
        user.setPassword(password.split(":")[1]);
        user.setStatus(status.split(":")[1]);
        bindViews();
        InitFragment();
//        //导航条
//        final android.support.v7.app.ActionBar bar = getSupportActionBar();
//        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);//设计模式


//  获取ViewPager
        pager = (ViewPager) findViewById(R.id.pager);
        //绑定
        pager.setAdapter(new MFragmentPagerAdapter(fragmentManager, fragmentArrayList));
        pager.setCurrentItem(0);
        //事件处理
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageSelected(int arg0) {
//                bar.setSelectedNavigationItem(arg0);
                switch (arg0) {
                    case 0:
                        pager.setCurrentItem(0);
                        tvTicket.setBackgroundResource(R.drawable.cab_background_top_mainbar);
                        tvMy.setBackgroundResource(R.color.colorPrimary);
                        break;
                    case 1:
                        pager.setCurrentItem(1);
                        tvTicket.setBackgroundResource(R.color.colorAccent);
                        tvMy.setBackgroundResource(R.drawable.cab_background_top_mainbar);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    private void bindViews() {
        tvMy = (TextView) findViewById(R.id.tvMy);
        tvTicket = (TextView) findViewById(R.id.tvTicket);
        tvTicket.setOnClickListener(new MyOnClickListener());
        tvMy.setOnClickListener(new MyOnClickListener());
    }

    //重置所有文本的选中状态
    private void setSelected() {
        tvTicket.setSelected(false);
        tvMy.setSelected(false);
    }

    /**
     * 初始化Fragment，并添加到ArrayList中
     */
    private void InitFragment(){
        fragmentArrayList = new ArrayList<Fragment>();
        Log.v("hehe",user.getStatus());
        if(user.getStatus().trim().equals("a")){
            fragmentArrayList.add(new ManageGoodsFragment());
            Log.v("hehe","11111111111111111111111");
        }else {
            fragmentArrayList.add(new ManageGoods2Fragment());
        }

        fragmentArrayList.add(new MyFragment());

        fragmentManager = getSupportFragmentManager();

    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.tvTicket:
                    pager.setCurrentItem(0);
                    tvTicket.setBackgroundResource(R.drawable.cab_background_top_mainbar);
                    tvMy.setBackgroundResource(R.color.colorPrimary);
                    break;
                case R.id.tvMy:
                    pager.setCurrentItem(1);
                    tvTicket.setBackgroundResource(R.color.colorAccent);
                    tvMy.setBackgroundResource(R.drawable.cab_background_top_mainbar);
                    break;
            }
        }

    }
}