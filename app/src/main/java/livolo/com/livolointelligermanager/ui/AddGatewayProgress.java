package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.littlejie.circleprogress.CircleProgress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.fog.fog2sdk.MiCODevice;
import io.fogcloud.easylink.helper.EasyLinkCallBack;
import io.fogcloud.fog_mdns.helper.SearchDeviceCallBack;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.receiver.TCPReceive;
import livolo.com.livolointelligermanager.util.IPUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.util.TCPTool;
import livolo.com.livolointelligermanager.util.WifiSupport;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mayn on 2018/4/10.
 */

public class AddGatewayProgress extends BaseActivity implements Handler.Callback, View.OnClickListener {
    private final static int[] COLORS = new int[]{Color.GREEN, Color.YELLOW, Color.RED};
    private final static int RUN_TIMER = 1000;
    private final static int SEND_SUCCESS_UDP = 1001;
    private final static int SEND_SUCCESS_TCP = 1002;
    private final static int SNED_HTTP = 1003;

    @BindView(R.id.circle_progress)
    CircleProgress circleProgress;
    @BindView(R.id.text_status)
    TextView textStatus;
    @BindView(R.id.next_btn)
    Button nextBtn;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;

    private WifiManager mWifiManager;
    private Handler mHandler;
    private int totalTime = 30;
    private boolean canRunProgress = true;
    private Timer timer;
    private MiCODevice micodev;
    private String textPoint = "";
    private String wifiSSID;
    private String wifiPWD;
    private String deviceSSID;

    private String mGatewayName;
    private String mGatewayID;

    private static Activity activity;

    public static Activity getInstance() {
        return activity;
    }

    private boolean isSendType = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addgateway_progress);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.midblue);
        activity = this;
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        topTitle.setText(R.string.addgateway_progress_title);
        wifiSSID = getIntent().getStringExtra("wifiname");
        wifiPWD = getIntent().getStringExtra("password");
        deviceSSID = getIntent().getStringExtra("device");
        circleProgress.setGradientColors(COLORS);
        circleProgress.setValue(0);
        micodev = new MiCODevice(this);
        setTimer();
        nextBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        Log.e("----------------","--------------------------------------oncreate");

        mHandler.sendEmptyMessageDelayed(SEND_SUCCESS_TCP, 2000);
    }

    private void setTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                if (totalTime > 0 && canRunProgress) {
                    totalTime--;

                    Message msg = mHandler.obtainMessage();
                    msg.what = RUN_TIMER;
                    msg.obj = (100 * (30 - totalTime)) / 30;
                    msg.sendToTarget();
                } else {
                    stop(0, getResources().getString(R.string.start_set_gateway));
                }
//                    }
//                });
            }
        };
        timer.schedule(task, 0, 1000);
    }

    private void destroyTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void stop(int value, String str) {
        destroyTimer();
        canRunProgress = false;
        circleProgress.setValue(value);
        nextBtn.setText(str);
        textStatus.setText("");
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case RUN_TIMER:
                int value = Integer.parseInt(message.obj.toString());
                if (value == 100) {
                    circleProgress.setValue(99);
                    textStatus.setText(R.string.set_gateway_failed);
//                    mTCPReceive.closed();
                    stop(0, getResources().getString(R.string.set_gateway_failed));
                    Toast.makeText(this, R.string.add_gateway_false, Toast.LENGTH_SHORT).show();
                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(message.obj.toString())
                            .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.cancel();
                                    finish();
                                }
                            }).show();
                } else {
                    circleProgress.setValue(value);
                    if (textPoint.length() < 6) {
                        textPoint = textPoint + ".";
                    } else {
                        textPoint = "";
                    }
                    textStatus.setText(getResources().getString(R.string.connecting_text) + textPoint);
                }
                break;
            case Constants.GET_NETWORD_RESULT:
                stop(100, getResources().getString(R.string.set_gateway_success));
                nextBtn.setClickable(false);
                try {
                    JSONObject obj = new JSONObject(message.obj.toString());
                    JSONObject json = obj.getJSONObject("device_info");
                    mGatewayName = json.getString("name");//
                    mGatewayID = json.getString("ID");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessageDelayed(SEND_SUCCESS_UDP, 1000);
                break;
            case SEND_SUCCESS_UDP:
                WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                myChangeWIFI(mWifiManager, wifiSSID);
                if (AddGatewayActivity.getInstance() != null) {
                    AddGatewayActivity.getInstance().finish();
                }
                if (DeviceChoseActivity.getInstance() != null) {
                    DeviceChoseActivity.getInstance().finish();
                }
                if (WorkWifiChoseActivity.getInstance() != null) {
                    WorkWifiChoseActivity.getInstance().finish();
                }
                if (RestartGatewayActivity.getInstance() != null) {
                    RestartGatewayActivity.getInstance().finish();
                }
                Intent intent = new Intent();
                intent.setClass(this, AddGatewaySuccessActivity.class);
                intent.putExtra("gateway_name", mGatewayName);
                intent.putExtra("gateway_mac", mGatewayID);
                intent.putExtra("wifiname", wifiSSID);
                intent.putExtra("wifipassword", wifiPWD);
                startActivity(intent);
                Log.e("-------------","--------------------------------------------------------------------finish!!!");
                finish();
                break;
            case SEND_SUCCESS_TCP:
                sendHttpMsg();
                break;
            case SNED_HTTP:
                sendHttpMsg();
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_btn:
                if (canRunProgress) {
                    canRunProgress = false;
                    nextBtn.setText(R.string.start_set_gateway);
                    textStatus.setText("");
//                    stopEasyLink();
                    circleProgress.setValue(0);
                } else {
                    canRunProgress = true;
                    setTimer();
                    nextBtn.setText(R.string.cancel);
                    mHandler.sendEmptyMessageDelayed(SEND_SUCCESS_TCP, 2000);
//                    startEasyLink();
                }
                myChangeWIFI(mWifiManager, wifiSSID);
                finish();
                break;
            case R.id.back_btn:
                finish();
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyTimer();
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private void sendHttpMsg() {
        //创建okHttpClient对象
        final OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建一个Request
        String[] keys = {"SSID", "PASSWORD", "DHCP","SERVICEIP"};
        Object[] values = {wifiSSID, wifiPWD, true, Constants.URL};
        final String reqStr = getJsonString(keys, values);
        Log.e("-----post----", "------------------dopost:" + reqStr);
        RequestBody requestBody = RequestBody.create(JSON, reqStr);
        final Request request;
        request = new Request.Builder()
                .url("http://10.10.10.1:8000/config-write-uap")
                .post(requestBody)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessageDelayed(SNED_HTTP, 2000);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.e("--------gateway-----",result);
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    mGatewayID = object.getString("gid");
                    String type = object.getString("type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mGatewayName = "my_gateway_name";
                mHandler.sendEmptyMessageDelayed(SEND_SUCCESS_UDP, 2000);
            }
        });
    }

    /**
     * 组装获取Http的body
     */
    public String getJsonString(String[] keys, Object[] values) {
        if (keys.length == values.length) {
            JSONObject json = new JSONObject();
            try {
                for (int i = 0; i < keys.length; i++) {
                    json.put(keys[i], values[i]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json.toString();
        } else {
            Log.e("Error_Http_Json", "keys和values数量无法对应");
            return null;
        }
    }

    private void myChangeWIFI(WifiManager mWifiManager, String ssid) {
        WifiConfiguration wificonfig = WifiSupport.isExsits(ssid, this);
        mWifiManager.enableNetwork(wificonfig.networkId, true);
    }
}
