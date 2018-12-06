package com.zxd.notconfused.utils;

import com.zxd.notconfused.R;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class mUtils {


    //使用okhttp请求数据
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }



    public static int getWeatherImage(String str){

        if(str.equals("晴")){
            return R.drawable.q;
        }
        if(str.equals("多云")){
            return R.drawable.duoyun;
        }
        if(str.equals("阴")){
            return R.drawable.y;
        }
        if(str.equals("阵雨")){
            return R.drawable.zy;
        }
        if(str.equals("雷阵雨并伴有冰雹")){
            return R.drawable.lzybbb;
        }
        if(str.equals("雨夹雪")){
            return R.drawable.yjx;
        }
        if(str.equals("小雨")){
            return R.drawable.xy;
        }
        if(str.equals("中雨")){
            return R.drawable.zhongy;
        }
        if(str.equals("暴雨")){
            return R.drawable.by;
        }
        if(str.equals("大暴雨")){
            return R.drawable.dby;
        }
        if(str.equals("特大暴雨")){
            return R.drawable.tdby;
        }
        if(str.equals("阵雪")){
            return R.drawable.zy;
        }
        if(str.equals("中雪")){
            return R.drawable.zhongx;
        }
        if(str.equals("大雪")){
            return R.drawable.dx;
        }
        if(str.equals("暴雪")){
            return R.drawable.bx;
        }
        if(str.equals("雾")){
            return R.drawable.w;
        }
        if(str.equals("沙尘暴")){
            return R.drawable.scb;
        }
        if(str.equals("小雨-中雨")){
            return R.drawable.xy_zy;
        }
        if(str.equals("中雨-大雨")){
            return R.drawable.zy_dy;
        }
        if(str.equals("大雨-暴雨")){
            return R.drawable.dy_by;
        }
        if(str.equals("大暴雨-特大暴雨")){
            return R.drawable.dy_by;
        }
        if(str.equals("小雪-中雪")){
            return R.drawable.xx_zx;
        }
        if(str.equals("中雪-大雪")){
            return R.drawable.zx_dx;
        }
        if(str.equals("大雪-暴雪")){
            return R.drawable.dx_bx;
        }
        if(str.equals("扬沙")){
            return R.drawable.ys;
        }
        if(str.equals("强沙尘暴")){
            return R.drawable.qscb;
        }
        if(str.equals("飑")){
            return R.drawable.b;
        }
        if(str.equals("龙卷风")){
            return R.drawable.ljf;
        }
        if(str.equals("轻霾")){
            return R.drawable.qm;
        }
        if(str.equals("霾")){
            return R.drawable.m;
        }

        return R.drawable.q;
    }
}
