package livolo.com.livolointelligermanager.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.littlejie.circleprogress.CircleProgress;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.GatewayDetail;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;

/**
 * Created by mayn on 2018/4/12.
 */

public class AddSwitchActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {

    public static final int ADD_SWITCH_FAILED = 1000;

    @BindView(R.id.circle_progress)
    CircleProgress circleProgress;
    @BindView(R.id.start_btn)
    Button startBtn;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;

    private boolean isStarted = false;
    private Handler mHandler;
    private HttpTools mHttp;
    private Timer mTimer;
    private int mTotleTime = 60;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addswitch);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.midblue);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        mHttp = new HttpTools();
        initLayout();
        mHttp.getGatewayList(Constants.HomeID,mHandler);

    }

    private void initLayout() {
        backBtn.setOnClickListener(this);
        startBtn.setOnClickListener(this);
        topTitle.setText(R.string.addswitch_title);
        circleProgress.setValue(0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BRODCASE_ADD_SWITCH);
        filter.addAction(Constants.BRODCASE_ADD_SWITCH_FAILED);
        registerReceiver(receiver, filter);
    }

    //接收回调广播
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.BRODCASE_ADD_SWITCH)) {
                circleProgress.setValue(100);
                stop();
                startBtn.setText(R.string.add_success);
                String switchId = intent.getStringExtra("switchid");
                String switchName = intent.getStringExtra("switchname");

                Intent intentSuccess = new Intent();
                intentSuccess.setClass(AddSwitchActivity.this, AddSwitchSuccessActivity.class);
                intentSuccess.putExtra("switchid",switchId);
                intentSuccess.putExtra("switchname",switchName);
                startActivity(intentSuccess);
                finish();
            }else if (action.equals(Constants.BRODCASE_ADD_SWITCH_FAILED)){
                String msg = intent.getStringExtra("resultMsg");
                stop();
                Message message = mHandler.obtainMessage();
                message.what = ADD_SWITCH_FAILED;
                message.obj = msg;
                message.sendToTarget();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        unregisterReceiver(receiver);
    }

    private void start() {
        mTotleTime = 60;
        isStarted = true;
        mHttp.addSwitchBtn(Constants.HomeID, mHandler);
        setTimer();
    }

    private void stop() {
        isStarted = false;
        if (mTimer != null) {
            mTimer.cancel();
        }
        circleProgress.setValue(0);
    }

    private void setTimer() {
        mTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mTotleTime > 0 && isStarted) {
                            mTotleTime--;
                            circleProgress.setValue((100 * (60 - mTotleTime)) / 60);
                        } else {
                            startBtn.setText(R.string.add_time_out);
                            stop();
                        }
                    }
                });
            }
        };
        mTimer.schedule(task, 0, 1000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;

            case R.id.start_btn:
                startBtnAction();
                break;
        }
    }

    private void  startBtnAction(){
        if (!isStarted) {
            start();
            startBtn.setText(R.string.cancel);
        } else {
            stop();
            startBtn.setText(R.string.add_again);
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case Constants.GET_ADDSWITCH_STATUS_SUCCESS:
                try {
                    JSONObject object = new JSONObject(message.obj.toString());
                    if (!object.get("device_id").equals("")) {
                        circleProgress.setValue(100);
                        stop();
                        startBtn.setText(R.string.add_success);
                        Intent intent = new Intent();
                        intent.setClass(this, AddSwitchSuccessActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("--------", "-------未添加--------");
                    }
                } catch (JSONException e) {
//                    e.printStackTrace();
                }

                break;
            case Constants.GET_ADDSWITCH_STATUS_FALSE:
                Log.e("--------", "-------获取失败！------");
                stop();
                DialogUtil.createEmptyMsgDialog(this,R.string.add_false);

                break;
            case ADD_SWITCH_FAILED:
                new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(message.obj.toString())
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
                                finish();
                            }
                        })
                        .showCancelButton(false)
                        .show();
                break;
            case Constants.GET_GATEWAY_LIST_SUCCESS:
                List<GatewayDetail> list = new Gson().fromJson(message.obj.toString(),new TypeToken<List<GatewayDetail>>(){}.getType());
                if (list!=null && list.size()>0){
                    startBtnAction();
                }else{
                    DialogUtil.createEmptyMsgDialog(this,R.string.add_gateway_first);
                }
                break;
            case Constants.GET_GATEWAY_LIST_FALSE:
                DialogUtil.createEmptyMsgDialog(this,message.obj.toString());
                break;
        }
        return false;
    }
}
