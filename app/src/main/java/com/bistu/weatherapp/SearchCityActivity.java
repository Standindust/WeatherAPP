package com.bistu.weatherapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.HashMap;
import java.util.Map;

public class SearchCityActivity extends Activity implements
        OnItemClickListener {

    private Context mContext;
    private ListView listview;
    private SimpleAdapter simp_adapter;
    private List<Map<String, Object>> dataList;
    //android6.0之后要动态获取权限

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);
        listview = findViewById(R.id.listview);
        dataList = new ArrayList<>();
        mContext = this;
        // 清空数据库表中内容
        //dbread.execSQL("delete from note");
        RefreshNotesList();
        listview.setOnItemClickListener(this);
    }

    public void RefreshNotesList() {

        int size = dataList.size();
        if (size > 0) {
            dataList.removeAll(dataList);
            simp_adapter.notifyDataSetChanged();
            listview.setAdapter(simp_adapter);
        }

        simp_adapter = new SimpleAdapter(this, getData(), R.layout.item_hotcity,
                new String[] { "tv_citymane" }, new int[] {
                R.id.cityitem});
        listview.setAdapter(simp_adapter);
    }

    private List<Map<String, Object>> getData() {
        String city = CacheUtil.get(getApplication()).getString("city", "");
        List<CityList> citeyDataList = JSON.parseArray(city,CityList.class);
        if(citeyDataList==null){
            return dataList;
        }
        for (int i = 0; i < citeyDataList.size(); i++) {

            Map<String, Object> map = new HashMap<String, Object>();
            String name = citeyDataList.get(i).getName();
            String citycode = citeyDataList.get(i).getCode();
            map.put("tv_citymane", name);
            map.put("tv_code", citycode);

            dataList.add(map);
        }

        return dataList;

    }

    // 点击某一个item
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        Map<String,String> itemMap = (Map<String, String>) listview.getItemAtPosition(arg2);
        String id = itemMap.get("tv_code");
        Intent myIntent = new Intent();
        myIntent.putExtra("citycode",id);
        //通过intent对象返回结果 setResult
        setResult(4, myIntent);

        finish();


    }


}
