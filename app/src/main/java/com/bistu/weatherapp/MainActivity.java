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

public class MainActivity extends Activity implements OnScrollListener,
        OnItemClickListener, OnItemLongClickListener {

    private Context mContext;
    private ListView listview;
    private SimpleAdapter simp_adapter;
    private List<Map<String, Object>> dataList;
    private Button addNote;

    //android6.0之后要动态获取权限
    private void checkPermission() {
        // Storage Permissions
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(MainActivity.this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        checkPermission();
        listview = findViewById(R.id.listview);
        dataList = new ArrayList<>();

        addNote = findViewById(R.id.btn_add);

        mContext = this;
        
        addNote.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this,addCity.class);
                startActivityForResult(intent, 1);

            }
        });

        // 清空数据库表中内容
        //dbread.execSQL("delete from note");
        RefreshNotesList();

        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(this);
        listview.setOnScrollListener(this);
    }

    public void RefreshNotesList() {

        int size = dataList.size();
        if (size > 0) {
            dataList.removeAll(dataList);
            simp_adapter.notifyDataSetChanged();
            listview.setAdapter(simp_adapter);
        }

        simp_adapter = new SimpleAdapter(this, getData(), R.layout.item,
                new String[] { "tv_citymane","tv_code" }, new int[] {
                R.id.tv_city,R.id.tv_code});
        listview.setAdapter(simp_adapter);
    }

    private List<Map<String, Object>> getData() {
        String care = CacheUtil.get(getApplication()).getString("care", "");
        List<CityData> citeyDataList = JSON.parseArray(care,CityData.class);
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

    @Override
    public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    // 滑动listview监听事件
    @Override
    public void onScrollStateChanged(AbsListView arg0, int arg1) {
        // TODO Auto-generated method stub
        switch (arg1) {
            case SCROLL_STATE_FLING:
                Log.i("main", "");
            case SCROLL_STATE_IDLE:
                Log.i("main", "");
            case SCROLL_STATE_TOUCH_SCROLL:
                Log.i("main", "");
        }
    }

    // 点击某一个item
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        Map<String,String> itemMap = (Map<String, String>) listview.getItemAtPosition(arg2);
        String id = itemMap.get("tv_code");
            Intent myIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("citycode",id);
            myIntent.putExtras(bundle);
            myIntent.setClass(MainActivity.this, Detail.class);
            startActivityForResult(myIntent, 1);


    }

    @Override
    // 接受上一个页面返回的数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            RefreshNotesList();
        }
    }

    // 长按item的点击事件
    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
        final int n=arg2;
        Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除该地区吗？");
        builder.setMessage("确认删除吗？");
        Map<String,String> itemMap = (Map<String, String>) listview.getItemAtPosition(arg2);
        String id = itemMap.get("tv_code");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                    String string = CacheUtil.get(getApplicationContext()).getString(id, "");
                    WeatherInfo.LivesBean curLive;
                    WeatherInfo weatherInfo = JSON.parseObject(string, WeatherInfo.class);
                    WeatherInfo.LivesBean livesBean = weatherInfo.getLives().get(0);
                    curLive=livesBean;
                    if(curLive==null){
                        Toast.makeText(getApplication(),"请先查找到对应的天气信息",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String care = CacheUtil.get(getApplication()).getString("care", "");
                    List<CityData> citeyDataList = JSON.parseArray(care,CityData.class);
                    if(citeyDataList==null){
                        citeyDataList=new ArrayList<>();
                    }
                    int pos=-1;
                    for (int i = 0; i < citeyDataList.size(); i++) {
                        if (citeyDataList.get(i).getCode().equals(curLive.getAdcode())) {
                            pos = i;
                            break;
                        }
                    }


                    citeyDataList.remove(pos);
                    CacheUtil.get(getApplication()).put("care",JSON.toJSONString(citeyDataList));
                    RefreshNotesList();


                }
            });




        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create();
        builder.show();
        return true;
    }

}
