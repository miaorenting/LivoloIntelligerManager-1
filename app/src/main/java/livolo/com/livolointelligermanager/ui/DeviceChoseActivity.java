package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;
import com.zcolin.gui.zrecyclerview.ZRecyclerView;

import org.w3c.dom.Text;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.adaper.DeviceWifiAdapter;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.util.WifiSupport;
import livolo.com.livolointelligermanager.view.WheelPicker;
import livolo.com.livolointelligermanager.view.WheelView;

/**
 * Created by mayn on 2018/4/10.
 */

public class DeviceChoseActivity extends BaseActivity implements View.OnClickListener ,Handler.Callback{

    private static final int checkSSID = 1000;

    @BindView(R.id.next_btn)
    Button nextBtn;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.wifi_picker)
    ZRecyclerView wifiPicker;

    private WifiManager mWifiManager;
    private List<ScanResult> wifiList = new ArrayList<ScanResult>();
    private ScanResult scanResult;
    private WifiReceiver mWifiReceiver;
    private String mWifiName;
    private String mPassword;
    private DeviceWifiAdapter adapter;
    private int index = 0;
    private Handler mHandler;
    private static Activity activity;
    private SweetAlertDialog dialog;

    public static Activity getInstance() {
        return activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_chose);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.midblue);
        activity = this;
        ButterKnife.bind(this);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiName = getIntent().getStringExtra("wifiname");
        mPassword = getIntent().getStringExtra("password");
        adapter = new DeviceWifiAdapter();
        mHandler = new Handler(this);
        adapter.setDatas(wifiList);
        wifiPicker.setAdapter(adapter);
        wifiPicker.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<ScanResult>() {
            @Override
            public void onItemClick(View covertView, int position, ScanResult data) {
                index = 0;
                scanResult = data;
                if (data.SSID.contains("LIVO_GW_")) {
                    addNetWIFI(data.SSID, "12345678", data);
                }
                dialog = new SweetAlertDialog(DeviceChoseActivity.this,SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText(getResources().getString(R.string.connecting));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                myChangeWIFI(mWifiManager, data.SSID);
            }
        });
        wifiPicker.setIsRefreshEnabled(false);
//        wifiPicker.setNoMore(false);
        initLayout();
        openWifi();
        wifiRegister();
    }

    private void initLayout() {
        backBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        topTitle.setText(R.string.devicechose_title);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();

                break;
            case R.id.next_btn:
//                index = 0;
//                mDeviceSSID = wifiList.get(curDevice).SSID;
//                if (mDeviceSSID.equals("LIVO_GW_1D3AD0")) {
//                    addNetWIFI(mDeviceSSID, "12345678", wifiList.get(curDevice));
//                }
                break;
        }
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

    private void setSpinner() {
        adapter.clearDatas();
        adapter.setDatas(wifiList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case checkSSID:
                if (ConfigUtil.getSSIDStr(mWifiManager.getConnectionInfo().getSSID()).equals(scanResult.SSID)){
                    dialog.cancel();
                    Intent intentAdd = new Intent();
                    intentAdd.setClass(DeviceChoseActivity.this, AddGatewayProgress.class);
                    intentAdd.putExtra("wifiname", mWifiName);
                    intentAdd.putExtra("password", mPassword);
                    intentAdd.putExtra("device", scanResult.SSID);
                    DeviceChoseActivity.this.startActivity(intentAdd);
                }else{
                    Toast.makeText(this,R.string.connect_again,Toast.LENGTH_SHORT).show();
                    myChangeWIFI(mWifiManager, scanResult.SSID);
                }
            break;
        }
        return false;
    }

    //接收：
    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String tempSSID = mWifiManager.getConnectionInfo().getSSID();
            wifiList.clear();
            List<ScanResult> results = mWifiManager.getScanResults();
            if (results != null) {
                for (int i = 0; i < results.size(); i++) {
                    if (!TextUtils.isEmpty(results.get(i).SSID) && results.get(i).SSID.contains("LIVO_GW_")) {
                        wifiList.add(results.get(i));
                    }
                }
            }
            setSpinner();
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
                    Log.e("----------wifi", "-------------------------wifi,链接："+tempSSID);
                    //获取当前wifi名称
                    if (scanResult!=null){
                        if (ConfigUtil.getSSIDStr(tempSSID).equals(scanResult.SSID)) {
                            Log.e("---------------","--------------------Log----------------");
                            mHandler.sendEmptyMessageDelayed(checkSSID,2000);
                        } else {
                            if (!TextUtils.isEmpty(scanResult.SSID)) {
                                if (index < 5) {
                                    if (scanResult.SSID.contains("LIVO_GW_")) {
                                        addNetWIFI(scanResult.SSID, "12345678", scanResult);
                                    }
                                    myChangeWIFI(mWifiManager, scanResult.SSID);
                                    index++;
                                } else {
                                    Toast.makeText(context, R.string.connect_again, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void addNetWIFI(String ssid, String password, ScanResult device) {
        WifiConfiguration wifiConfiguration = WifiSupport.createWifiConfig(ssid, password, WifiSupport.getWifiCipher(device.capabilities));
        WifiSupport.addNetWork(wifiConfiguration, this);
    }

    private void myChangeWIFI(WifiManager mWifiManager, String ssid) {
        WifiConfiguration wificonfig = WifiSupport.isExsits(ssid, this);
        mWifiManager.enableNetwork(wificonfig.networkId, true);
    }

}
