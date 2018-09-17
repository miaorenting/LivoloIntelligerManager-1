package livolo.com.livolointelligermanager.http;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.crypto.spec.GCMParameterSpec;

import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.config.SysApplication;
import livolo.com.livolointelligermanager.mode.ResultDetail;
import livolo.com.livolointelligermanager.receiver.BrodcaseTool;
import livolo.com.livolointelligermanager.util.DialogUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mayn on 2018/4/7.
 */

public class HttpPost {

    public HttpPost(){}

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public void doPost(String[] keys,Object[] values, final Handler mHandler, String url, final int resultCode, final int falseCode,boolean needHeader){
        //创建okHttpClient对象
        final OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建一个Request
        final String reqStr = getJsonString(keys,values);
        Log.e("-----post----","action:"+url+"-------"+"dopost:"+reqStr);
        RequestBody requestBody = RequestBody.create(JSON, reqStr);
       final Request request;
        if (needHeader){
            request = new Request.Builder()
                    .url(Constants.URL+"/"+url)
                    .addHeader("user_id",Constants.UserID)
                    .addHeader("token",Constants.Token)
                    .addHeader("home_id",Constants.HomeID)
                    .post(requestBody)
                    .build();
        }else{
            request = new Request.Builder()
                    .url(Constants.URL+"/"+url)
                    .post(requestBody)
                    .build();
        }

        mOkHttpClient.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("-------------------","-----------------------ERROR-----------------------call:"+call.toString());
                if (falseCode != Constants.CONTROL_SWITCH_FALSE){
                    Message msg = mHandler.obtainMessage();
                    msg.what = falseCode;
                    msg.obj = SysApplication.getInstance().getResources().getString(R.string.cannot_connect_service);
                    mHandler.sendMessage(msg);
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.e("------","postResult:"+result);
                analyze(result,resultCode,falseCode,mHandler);
            }
        });
    }
    private void analyze(String json,int resultCode,int falseCode,Handler mHandler){
        Message msg = mHandler.obtainMessage();
        try {
            JSONObject object = new JSONObject(json);
            if (object.get("result_code").toString().equals("000")) {
                msg.what = resultCode;
                if (object.get("data").toString().equals("{\"status\":\"0\"}")) {
                    msg.obj = object.get("result_msg").toString();
                }else if (object.get("data").toString().equals("{\"status\":\"1\"}")){
                    msg.obj = object.get("result_msg").toString();
                }else{
                    msg.obj = object.get("data").toString();
                }
            } else if (object.get("result_code").toString().equals("111")){//访问错误
                msg.what = falseCode;
                msg.obj = object.get("result_msg").toString();
            }else if (object.get("result_code").toString().equals("112")){//登录超时
                msg.what = falseCode;
                msg.obj = object.get("result_msg").toString();
                BrodcaseTool.sendSysExit(SysApplication.getInstance(),R.string.login_again);
                return;
            }else{
                msg.what = falseCode;
                msg.obj = object.get("result_msg").toString();
            }
            mHandler.sendMessage(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /** 组装获取Http的body */
    public String getJsonString(String[] keys, Object[] values){
        if (keys.length == values.length){
            JSONObject json = new JSONObject();
            try {
                for (int i = 0; i < keys.length; i++){
                    json.put(keys[i],values[i]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json.toString();
        }else{
            Log.e("Error_Http_Json","keys和values数量无法对应");
            return null;
        }
    }


}
