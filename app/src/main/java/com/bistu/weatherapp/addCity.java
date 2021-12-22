package com.bistu.weatherapp;


import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.bistu.weatherapp.CacheUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;



public class addCity extends AppCompatActivity {
        private static final String TAG = "MainActivity";
        private EditText mEt;
        private Button mQuery;
        private TextView mWeatherInfo;
        private Button mList;
        private Button mCare;
        WeatherInfo.LivesBean curLive;
        private Button Citylist;
        private  int flag=1;
        private  Button reload;
        public void setFlag(){
            this.flag=0;
        }
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.addcity);
            initViews();

            Citylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(addCity.this,SearchCityActivity.class);
                    startActivityForResult(intent, 3);
                }
            });
            mQuery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeatherInfo.setText("");
                    curLive=null;
//                    String str="[{\"name\":\"东城区\",\"code\":\"110101\"},\n" +
//                            "{\"name\":\"西城区\",\"code\":\"110102\"},\n" +
//                            "{\"name\":\"朝阳区\",\"code\":\"110105\"},\n" +
//                            "{\"name\":\"丰台区\",\"code\":\"110106\"},\n" +
//                            "{\"name\":\"石景山区\",\"code\":\"110107\"},\n" +
//                            "{\"name\":\"海淀区\",\"code\":\"110108\"},\n" +
//                            "{\"name\":\"门头沟区\",\"code\":\"110109\"},\n" +
//                            "{\"name\":\"房山区\",\"code\":\"110111\"},\n" +
//                            "{\"name\":\"通州区\",\"code\":\"110112\"},\n" +
//                            "{\"name\":\"顺义区\",\"code\":\"110113\"},\n" +
//                            "{\"name\":\"昌平区\",\"code\":\"110114\"},\n" +
//                            "{\"name\":\"大兴区\",\"code\":\"110115\"},\n" +
//                            "{\"name\":\"怀柔区\",\"code\":\"110116\"},\n" +
//                            "{\"name\":\"平谷区\",\"code\":\"110117\"},\n" +
//                            "{\"name\":\"密云区\",\"code\":\"110118\"},\n" +
//                            "{\"name\":\"延庆区\",\"code\":\"110119\"}]";
//                    CacheUtil.get(getApplicationContext()).put("city",str);
                    flag=1;
                    getWeather(mEt.getText().toString());
                }
            });
            reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mWeatherInfo.setText("");
                    curLive=null;
                    flag=0;
                    getWeather(mEt.getText().toString());
                }

            });
            mCare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                    if(citeyDataList!=null) {
                        for (int i = 0; i < citeyDataList.size(); i++) {
                            if (citeyDataList.get(i).getCode().equals(curLive.getAdcode())) {
                                pos = i;
                                break;
                            }
                        }
                    }

                    if(pos!=-1){
                        citeyDataList.remove(pos);
                        Log.i(TAG, "onClick: ---");
                    }else {
                        CityData citeyData=new CityData();
                        citeyData.setName(curLive.getProvince()+" "+curLive.getCity());
                        citeyData.setCode(curLive.getAdcode());
                        citeyDataList.add(citeyData);

                    }
                    CacheUtil.get(getApplication()).put("care",JSON.toJSONString(citeyDataList));
                    checkCare();

                    Intent data = new Intent();
                    setResult(2, data);
                    finish();
                }
            });

            mList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String care = CacheUtil.get(getApplication()).getString("care", "");
                    List<CityData> citeyDataList =JSON.parseArray(care,CityData.class);

                    if(citeyDataList==null||citeyDataList.isEmpty()){
                        Toast.makeText(getApplication(),"请先添加关注",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String[] d=new String[citeyDataList.size()];
                    int i=0;
                    for(CityData citeyData:citeyDataList){
                        d[i]=citeyData.getName();
                        i++;
                    }
                    new AlertDialog.Builder(com.bistu.weatherapp.addCity.this).setTitle("选择城市").setItems(d, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mEt.setText(citeyDataList.get(which).getCode());
                            getWeather(mEt.getText().toString());
                        }
                    }).create().show();


                }
            });



        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        public void checkCare(){
            String care = CacheUtil.get(getApplication()).getString("care", "");
            List<CityData> citeyDataList =JSON.parseArray(care,CityData.class);
            if(citeyDataList!=null) {
                if (!citeyDataList.isEmpty()) {
                    int pos = -1;
                    for (int i = 0; i < citeyDataList.size(); i++) {
                        if (citeyDataList.get(i).getCode().equals(curLive.getAdcode())) {
                            pos = i;
                            break;
                        }
                    }
                    if (pos != -1) {
                        mCare.setText("取消关注 ");
                    } else {
                        mCare.setText("关注");
                    }
                }else {
                    mCare.setText("关注");
                }
            }else {
                mCare.setText("关注");
            }

        }

        public void getWeather(String code)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        String string = CacheUtil.get(getApplicationContext()).getString(code, "");

                        if (!TextUtils.isEmpty(string)&&flag==1)
                        {

                            WeatherInfo weatherInfo = JSON.parseObject(string, WeatherInfo.class);
                            WeatherInfo.LivesBean livesBean = weatherInfo.getLives().get(0);
                            curLive=livesBean;
                            if (System.currentTimeMillis() - simpleDateFormat.parse(livesBean.getReporttime()).getTime() < 60 * 60 * 1000l)
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        checkCare();
                                        mWeatherInfo.setText("天气：" + livesBean.getWeather() + "\n地区：" +
                                                livesBean.getProvince() + " " + livesBean.getCity() + "\n"
                                                + "温度：" + livesBean.getTemperature() + "\n湿度:" + livesBean.getHumidity() + "\n" + "风向：" + livesBean.getWinddirection() + " " + livesBean.getWindpower() + " \n" +
                                                "更新时间：" + livesBean.getReporttime()
                                        );
                                    }
                                });
                                return;
                            }


                        }

                        String post = RequestUtil.requestHttps("https://restapi.amap.com/v3/weather/weatherInfo", "POST", "key=55a3ed26fc8c8a4e1fc20f860b868962&city=" + code);
                        WeatherInfo weatherInfo;
                        try
                        {


                            weatherInfo = JSON.parseObject(post, WeatherInfo.class);

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Toast.makeText(getApplicationContext(), "没有查找到城市编码", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        if (weatherInfo.getStatus().equals("1"))
                        {
                            if( weatherInfo.getLives()==null||weatherInfo.getLives().size()==0)
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        Toast.makeText(getApplicationContext(), "没有查找到城市编码", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;

                            }

                            WeatherInfo.LivesBean livesBean = weatherInfo.getLives().get(0);
                            CacheUtil.get(getApplicationContext()).put(code, post);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    curLive=livesBean;
                                    checkCare();
                                    mWeatherInfo.setText("天气：" + livesBean.getWeather() + "\n地区：" +
                                            livesBean.getProvince() + " " + livesBean.getCity() + "\n"
                                            + "温度：" + livesBean.getTemperature() + "\n湿度:" + livesBean.getHumidity() + "\n" + "风向：" + livesBean.getWinddirection() + " " + livesBean.getWindpower() + " \n" +
                                            "更新时间：" + livesBean.getReporttime()
                                    );
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "没有查找到城市编码", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }

                }
            }).start();


        }

        private void initViews() {
            mEt = findViewById(R.id.et);
            mQuery = findViewById(R.id.query);
            mWeatherInfo = findViewById(R.id.weather_info);
            mList = findViewById(R.id.list);
            mCare = findViewById(R.id.care);
            Citylist=findViewById(R.id.Citylist);
            reload=findViewById(R.id.reload);
        }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && resultCode == 4) {

            //Bundle 是一个容器,专门给Intent传递消息的.内部的数据结构是KEY VALUE的键值对存在的
            //this.getIntent这个的意思是当前的Activity或者Service获取上一个给他传递的值
            // getExtras是得到Bundle这个容器里面的值
            String code = data.getStringExtra("citycode");
            mEt.setText(code);
        }
    }
    }


