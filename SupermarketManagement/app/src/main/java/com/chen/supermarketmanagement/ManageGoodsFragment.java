package com.chen.supermarketmanagement;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ManageGoodsFragment extends Fragment {
    Button btnAdd;
    Button btnSelect;
    Button btnMymap;
    Button btnOther;
    Button btnSell;
    Button btnSellHistory;
    Button btnSeeCost;
    Button btnSeeOther;
    public ManageGoodsFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_manage_goods, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        btnAdd=(Button)getActivity().findViewById(R.id.btnAdd);
        btnSelect=(Button)getActivity().findViewById(R.id.btnSelect);
        btnMymap=(Button)getActivity().findViewById(R.id.btnMymap);
        btnSell=(Button)getActivity().findViewById(R.id.btnSell);
        btnOther=(Button)getActivity().findViewById(R.id.btnOther);
        btnSellHistory=(Button)getActivity().findViewById(R.id.btnSellHistory);
        btnSeeCost=(Button)getActivity().findViewById(R.id.btnSeeCost);
        btnSeeOther=(Button)getActivity().findViewById(R.id.btnSeeOther);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getActivity(), AddGoodsActivity.class);
                startActivity(intent);
            }
        });
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
        btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getActivity(), OtherCostActivity.class);
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
        btnSeeCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getActivity(), SeeCostActivity.class);
                startActivity(intent);
            }
        });
        btnSeeOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getActivity(), SeeOtherActivity.class);
                startActivity(intent);
            }
        });
    }


}
