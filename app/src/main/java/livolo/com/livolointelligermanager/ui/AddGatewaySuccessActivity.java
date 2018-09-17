package livolo.com.livolointelligermanager.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.service.MQTTService;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.EditLimitUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.util.WifiSupport;

/**
 * Created by mayn on 2018/4/11.
 */

public class AddGatewaySuccessActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {

    private static final int CLICKABLE = 1000;
    private static final int checkSSID = 1100;

    @BindView(R.id.gateway_name)
    EditText gatewayName;
    @BindView(R.id.complate_btn)
    Button complateBtn;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;

    private LayoutInflater mInflater;
    private List<String> mNames;
    private List<TextView> mViews = new ArrayList<>();

    private String mGatewayName;
    private String mGatewayId;
    private String wifiName;
    private String wifiPassword;

    private HttpTools mHttp;
    private Handler mHandler;
    private SweetAlertDialog dialog;
    private WifiReceiver mWifiReceiver;
    private WifiManager mWifiManager;
    private SweetAlertDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addgateway_success);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.midblue);
        ButterKnife.bind(this);
        mInflater = LayoutInflater.from(this);
        mHttp = new HttpTools();
        mHandler = new Handler(this);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mGatewayName = getIntent().getStringExtra("gateway_name");
        mGatewayId = getIntent().getStringExtra("gateway_mac");
        wifiName = getIntent().getStringExtra("wifiname");
        wifiPassword = getIntent().getStringExtra("wifipassword");
        if (!TextUtils.isEmpty(mGatewayName)) {
            gatewayName.setText(mGatewayName);
        }
        wifiRegister();
        complateBtn.setClickable(false);
        mHandler.sendEmptyMessageDelayed(CLICKABLE, 2000);
        complateBtn.setOnClickListener(this);
        backBtn.setVisibility(View.INVISIBLE);
        topTitle.setText(R.string.addgateway_success_title);
        mDialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE).setContentText(getResources().getString(R.string.connecting));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        WifiConfiguration wifiConfiguration = WifiSupport.createWifiConfig(wifiName, wifiPassword, WifiSupport.WifiCipherType.WIFICIPHER_WPA);
        WifiSupport.addNetWork(wifiConfiguration, this);
        EditLimitUtil.setEditLimit(gatewayName,20,this);

    }

    private void wifiRegister() {
        IntentFilter filter = new IntentFilter();
        // filter.addAction(WifiManager.ERROR_AUTHENTICATING);
        filter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // 测试wifi验证密码错误问题
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mWifiReceiver = new WifiReceiver();
        registerReceiver(mWifiReceiver, filter);
    }

    //接收：
    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            List<ScanResult> results = mWifiManager.getScanResults();
            String tempSSID = mWifiManager.getConnectionInfo().getSSID();
            // / Wifi 状态变化
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {

                //获取scan后的结果
            } else if (WifiManager.ACTION_PICK_WIFI_NETWORK.equals(action)) {

            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                //wifi连接上与否
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    Log.e("----------wifi", "-------------------------wifi断开");
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    Log.e("----------wifi", "-------------------------wifi,链接：" + tempSSID);
                    //获取当前wifi名称
                    if (ConfigUtil.getSSIDStr(tempSSID).equals(wifiName)) {
                        Log.e("---------------", "--------------------Log----------------");
                        mHandler.sendEmptyMessageDelayed(checkSSID, 2000);
                    }else{

                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.complate_btn:
                dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText(getResources().getString(R.string.committing));
                dialog.show();
                if (EditLimitUtil.isLimit(gatewayName,20,this)){
                    mHandler.sendEmptyMessageDelayed(1005, 1000);
                }

                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (dialog != null && dialog.isShowing()) {
                dialog.cancel();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case Constants.ADD_GATEWAY_SUCCESS:
                dialog.setTitleText(message.obj.toString())
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                //启动MQTT服务器
                                try {
                                    MQTTService.client.disconnect();
                                } catch (MqttException e) {
//                                    e.printStackTrace();
                                }
                                Intent service = new Intent(AddGatewaySuccessActivity.this, MQTTService.class);
                                startService(service);
                                dialog.cancel();
                                finish();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                break;
            case Constants.ADD_GATEWAY_FALSE:
                dialog.setTitleText(message.obj.toString())
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                dialog.cancel();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                break;
            case CLICKABLE:
                complateBtn.setClickable(true);
                break;
            case 1005:
                mHttp.addGateway(mGatewayId, gatewayName.getText().toString(), 0, Constants.HomeID, mHandler);
                break;
            case Constants.GET_DEFAULT_NAME_SUCCESS:
                //{"default_name":"gateway"}
                if (mDialog!=null){
                    mDialog.cancel();
                }
                try {
                    JSONObject obj = new JSONObject(message.obj.toString());
                    mGatewayName = obj.getString("default_name");
                    gatewayName.setText(mGatewayName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.GET_DEFAULT_NAME_FALSE:
                if (mDialog!=null){
                    mDialog.cancel();
                }
                break;
            case checkSSID:
                mHttp.getdefaultName(Constants.HomeID, 1,"LIVOLO GATEWAY", mHandler);
                break;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null && !this.isFinishing()) {
            dialog.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.cancel();
        }
        unregisterReceiver(mWifiReceiver);
    }
}
