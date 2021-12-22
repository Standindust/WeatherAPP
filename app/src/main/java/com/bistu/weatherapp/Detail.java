package com.bistu.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;


public class Detail extends AppCompatActivity {
    WeatherInfo.LivesBean curLive;

    private TextView weather;
    private TextView place;
    private TextView temp;
    private TextView wet;
    private TextView time;
    private TextView windpower;
    private TextView winddirection;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xiangxi);
        initViews();
        Bundle myBundle = this.getIntent().getExtras();
        //Bundle 是一个容器,专门给Intent传递消息的.内部的数据结构是KEY VALUE的键值对存在的
        //this.getIntent这个的意思是当前的Activity或者Service获取上一个给他传递的值
        // getExtras是得到Bundle这个容器里面的值
        String code = myBundle.getString("citycode");
        String string = CacheUtil.get(getApplicationContext()).getString(code, "");
        WeatherInfo weatherInfo = JSON.parseObject(string, WeatherInfo.class);
        WeatherInfo.LivesBean livesBean = weatherInfo.getLives().get(0);
        curLive=livesBean;
        weather.setText(curLive.getWeather());
        place.setText(curLive.getProvince()+curLive.getCity());
        wet.setText(curLive.getHumidity());
        time.setText(curLive.getReporttime());
        temp.setText(curLive.getTemperature());
        windpower.setText(curLive.getWindpower());
        winddirection.setText(curLive.getWinddirection());

        }


    private void initViews () {
        weather=findViewById(R.id.weather);
        place=findViewById(R.id.place);
        wet=findViewById(R.id.wet);
        temp=findViewById(R.id.temp);
        time=findViewById(R.id.time);
        winddirection=findViewById(R.id.winddirection);
        windpower=findViewById(R.id.windpower);
    }
}