package livolo.com.livolointelligermanager.http;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.config.SysApplication;
import livolo.com.livolointelligermanager.mode.ResultDetail;
import livolo.com.livolointelligermanager.receiver.BrodcaseTool;
import livolo.com.livolointelligermanager.util.DialogUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mayn on 2018/4/7.
 */

public class HttpGet {

    public HttpGet() {
    }

    // 在子线程发起网络请求
    public void doGet(String[] keys, String[] values, final Handler mHandler, String url, final int resultCode, final int falseCode, boolean needHeader) {
        // 创建请求客户端
        OkHttpClient okHttpClient = new OkHttpClient();
        /**拼接参数*/
        StringBuilder tempParams = new StringBuilder();
        if (keys!=null){
            if (keys.length == values.length) {
                for (int i = 0; i < keys.length; i++) {
                    if (i > 0) {
                        tempParams.append("&");
                    }
                    try {
                        tempParams.append(String.format("%s=%s", keys[i], URLEncoder.encode(values[i], "utf-8")));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } finally {

                    }
                }
            } else {
                Log.e("----doGet----", "请求参数有误keys和values长度不一致！");
            }
        }
        // 创建请求参数
        Request request = null;
        if (needHeader) {
            request = new Request.Builder().url(Constants.URL +"/"+ url + "?" + tempParams.toString())
                    .addHeader("user_id", Constants.UserID).addHeader("token", Constants.Token).get().build();
        } else {
            request = new Request.Builder().url(Constants.URL + "/"+url + "?" + tempParams.toString()).get().build();
        }
        Log.e("-----doget----", "getRequest:" + Constants.URL + url + "?" + tempParams.toString());
        // 创建请求对象
        Call call = okHttpClient.newCall(request);
        // 发起异步的请求
        call.enqueue(new Callback() {
            @Override
            // 请求发生异常
            public void onFailure(Call call, IOException e) {
                Log.e("------", "----ERROR----");
                Message msg = mHandler.obtainMessage();
                msg.what = falseCode;
                msg.obj = SysApplication.getInstance().getResources().getString(R.string.cannot_connect_service);
                mHandler.sendMessage(msg);
            }

            @Override
            // 获取到服务器数据。注意：即使是 404 等错误状态也是获取到服务器数据
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Log.e("--------", "getResult:" + result);
                    analyze(result, resultCode, falseCode, mHandler);
                }
            }
        });

    }

    private void analyze(String json, int resultCode, int falseCode, Handler mHandler) {
        Message msg = mHandler.obtainMessage();
        try {
            JSONObject object = new JSONObject(json);
            String resultActionCode = object.get("result_code").toString().trim();
            if (resultActionCode.equals("000")) {
                msg.what = resultCode;
                msg.obj = object.get("data").toString();
            } else if (resultActionCode.equals("111")) {
                msg.what = falseCode;
                msg.obj = object.get("result_msg").toString();
            } else if (resultActionCode.equals("112")) {
                msg.what = falseCode;
                msg.obj = object.get("result_msg").toString();
                BrodcaseTool.sendSysExit(SysApplication.getInstance(), R.string.login_again);
                //关闭程序，重新启动程序
                return;
            } else {
                msg.what = falseCode;
                msg.obj = object.get("result_msg").toString();
            }
            mHandler.sendMessage(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
