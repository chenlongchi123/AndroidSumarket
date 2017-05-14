package com.chen.supermarketmanagement;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ManageGoods2Fragment extends Fragment {

    Button btnSelect;
    Button btnMymap;
    Button btnSell;
    Button btnSellHistory;
    public ManageGoods2Fragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_manage_goods2, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        btnSelect=(Button)getActivity().findViewById(R.id.btnSelect);
        btnMymap=(Button)getActivity().findViewById(R.id.btnMymap);
        btnSell=(Button)getActivity().findViewById(R.id.btnSell);
        btnSellHistory=(Button)getActivity().findViewById(R.id.btnSellHistory);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getActivity(), SelectGoodsActivity.class);
                startActivity(intent);
            }
        });
        btnMymap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getActivity(), MyMapActivity.class);
                startActivity(intent);
            }
        });
        btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getActivity(), SellGoodsActivity.class);
                startActivity(intent);
            }
        });

        btnSellHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getActivity(), SellHistoryActivity.class);
                startActivity(intent);
            }
        });

    }


}
