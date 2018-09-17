package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fog.fog2sdk.MiCODevice;
import io.fogcloud.fog_mdns.helper.SearchDeviceCallBack;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.util.WIFITool;
import livolo.com.livolointelligermanager.util.WifiSupport;

/**
 * Created by mayn on 2018/4/10.
 */

public class WorkWifiChoseActivity extends BaseActivity implements View.OnClickListener {

    private static final int WIFICIPHER_NOPASS = 0;
    private static final int WIFICIPHER_WEP = 1;
    private static final int WIFICIPHER_WPA = 2;

    @BindView(R.id.wifi_name)
    TextView wifiName;
    @BindView(R.id.wifi_password)
    EditText wifiPassword;
    @BindView(R.id.next_btn)
    Button nextBtn;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.checkbox)
    ImageView checkbox;

    private String ssid;
    private WifiManager mWifiManager;
    private List<ScanResult> wifiList = new ArrayList<ScanResult>();
    private WifiReceiver mWifiReceiver;
    private boolean isAgree = true;
    //设备搜索
    private MiCODevice micodev;

    private static Activity activity;

    public static Activity getInstance() {
        return activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workwifi);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.midblue);
        this.activity = this;
        ButterKnife.bind(this);
        topTitle.setText(R.string.wifichose_title);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        openWifi();
        wifiRegister();
        backBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        //测试 默认密码
        wifiPassword.setText("123456789A");
        checkbox.setOnClickListener(this);
    }

    /**
     * 检查wifi是否开启 如未开启，则打开wifi
     */
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        } else {
            mWifiManager.startScan();
        }
    }

    /**
     * 注册wifi广播，监听wifi状态
     * WIFI_STATE_CHANGED_ACTION：反映WiFi 功能所对应的状态，包括
     * WIFI_STATE_DISABLED(Wifi 功能已被关闭)、
     * WIFI_STATE_DISABLING(Wifi 功能正在关闭中)、
     * WIFI_STATE_ENABLED(Wifi 功能已被打开)、
     * WIFI_STATE_ENABLING(Wifi 功能正在打开中)、
     * WIFI_STATE_UNKNOWN(Wifi 功能状态未知)。
     * SUPPLICANT_STATE_CHANGED_ACTION：表示WPAS 的状态发生了变化。
     * NETWORK_STATE_CHANGED_ACTION：表示WIFI 连接状态发生变化，其携带的信息一般是NetworkInfo 对象。
     */
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiReceiver);
    }

    private void changeCheckbox() {
        if (isAgree) {
            isAgree = false;
            checkbox.setBackgroundResource(R.mipmap.select_out);
        } else {
            isAgree = true;
            checkbox.setBackgroundResource(R.mipmap.select_blue);
        }
    }

    private void setSpinner() {
        String tempSSID = mWifiManager.getConnectionInfo().getSSID();
//        Log.e("-----------temp","--------------------------"+tempSSID);
        //去掉部分机型的ssid前面的双引号
        if(tempSSID.length()>2 && tempSSID.charAt(0) == '"'&& tempSSID.charAt(tempSSID.length() -1) == '"'){
            ssid = tempSSID.substring(1,tempSSID.length()-1);
        }else{
            ssid = tempSSID;
        }
        wifiName.setText(ssid);
//        ssid = spinners.get(0);
    }

    //接收：
    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            wifiList.clear();
            wifiList.addAll(mWifiManager.getScanResults());
            setSpinner();
            String action = intent.getAction();
            // / Wifi 状态变化
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                //获取scan后的结果
//                Log.e("-----scan_result-----","-----list.size:"+wifiList.size());
            } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {

            } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {//wifi的状态变化
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                Log.e("WIFI状态", "wifiState:" + wifiState);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        Log.e("WIFI状态", "wifiState:WIFI_STATE_DISABLED (Wifi 功能已被关闭)、");
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        Log.e("WIFI状态", "wifiState:WIFI_STATE_DISABLING (Wifi 功能正在关闭中)");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        Log.e("WIFI状态", "wifiState:WIFI_STATE_ENABLED (Wifi 功能已被打开)");
                        mWifiManager.startScan();//开启扫描
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        Log.e("WIFI状态", "wifiState:WIFI_STATE_ENABLING (Wifi 功能正在打开中)");
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        Log.e("WIFI状态", "wifiState:WIFI_STATE_UNKNOWN (Wifi 功能状态未知)");
                        break;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();

                break;
            case R.id.next_btn:
                Intent intent = new Intent(this, DeviceChoseActivity.class);
//                Intent intent = new Intent(this, AddGatewayProgress.class);
                intent.putExtra("wifiname", ssid);
                intent.putExtra("password", wifiPassword.getText().toString().trim());
                startActivity(intent);
                break;
            case R.id.checkbox:
                changeCheckbox();
                break;
        }
    }

}
