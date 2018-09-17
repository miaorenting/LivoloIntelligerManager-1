package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fog.fog2sdk.MiCODevice;
import io.fogcloud.easylink.helper.EasyLinkCallBack;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.util.StatusBarUtils;

/**
 * Created by mayn on 2018/5/5.
 */

public class AddGatewayByFragmentActivity extends BaseActivity {

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.top_right)
    TextView topRight;
    @BindView(R.id.viewpage)
    ViewPager viewpage;

    public static Context context;
    private MiCODevice micodev;//
    private String wifiSSID;
    private String wifiPWD;
    private String deviceSSID;


    public static Context getContext(){
        return context;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gateway);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.midblue);
        ButterKnife.bind(this);
        context = this;
        micodev = new MiCODevice(this);
        initLayout();
    }

    private void initLayout(){
        wifiSSID = micodev.getSSID();
        Log.e("--------------------","--------------------------------------ssid:" + wifiSSID);

    }

    private void startEasyLink() {
//        totalTime = 60;
//        isComplate = false;
        Log.e("-------------",wifiSSID+"----------------"+wifiPWD);
        micodev.startEasyLink(wifiSSID, wifiPWD, true, 60000, 20, "", "", new EasyLinkCallBack() {
            @Override
            public void onSuccess(int code,String message) {
                Log.e("-----start_success----", message);
            }

            @Override
            public void onFailure(int code, String message) {
                Log.e("-----start_false----", message);
            }
        });
    }

    private void stopEasyLink() {
        micodev.stopEasyLink(new EasyLinkCallBack() {
            @Override
            public void onSuccess(int code, String message) {
                Log.e("-----stop_success----", message);
            }

            @Override
            public void onFailure(int code, String message) {
                Log.d("-----stop_false----", message);
            }
        });
    }

}
