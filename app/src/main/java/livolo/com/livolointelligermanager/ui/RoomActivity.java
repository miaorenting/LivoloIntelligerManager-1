package livolo.com.livolointelligermanager.ui;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import iammert.com.huelib.HueSeekBar;
import iammert.com.huelib.ProgressListener;
import iammert.com.huelib.VerticalAnimationListener;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.db.ButtonDao;
import livolo.com.livolointelligermanager.db.DeviceDao;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.ButtonDetail;
import livolo.com.livolointelligermanager.mode.DeviceDetail;
import livolo.com.livolointelligermanager.mode.RoomDetail;
import livolo.com.livolointelligermanager.receiver.BrodcaseTool;
import livolo.com.livolointelligermanager.util.BtnStatusUtil;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.view.SwitchButton;

import static livolo.com.livolointelligermanager.ui.AddGatewayByFragmentActivity.context;

/**
 * Created by mayn on 2018/4/6.
 * 房间 查看开关并控制开关
 */

public class RoomActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {

    @BindView(R.id.back_btn)
    ImageButton backBtn;
    @BindView(R.id.room_name)
    TextView roomName;
    @BindView(R.id.switch_button)
    CheckedTextView totalSwitchBtn;
    @BindView(R.id.layout_views)
    LinearLayout layoutViews;

    private List<DeviceDetail> list = new ArrayList<DeviceDetail>();
//    private List<View> views = new ArrayList<>();
    private RoomDetail detail;
    private DeviceDao mDeviceDao;
    private ButtonDao mBtnDao;
    private HttpTools mHttp;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.deepblue);
        ButterKnife.bind(this);
        detail = (RoomDetail) getIntent().getSerializableExtra("room");
        mDeviceDao = new DeviceDao();
        mBtnDao = new ButtonDao();
        mHttp = new HttpTools();
        mHandler = new Handler(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BRODCASE_REFRESH_AFTERDB);
        registerReceiver(receiver, filter);
        initLayout();
    }

    private void initLayout() {
        if (detail != null) {
            roomName.setText(detail.getRoom_name());
            list = mDeviceDao.getSwitchsByRoomID(detail.getRoom_id());
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setButton_list(mBtnDao.getButtonList(list.get(i).getSwitch_id()));
                }
            }
        }
        backBtn.setOnClickListener(this);
        totalSwitchBtn.setOnClickListener(this);
        refreshBtnStatus();//总开关
        layoutViews.removeAllViews();
        if (list != null) {
            for (DeviceDetail device : list) {
                addView(device);
            }
        }
    }

    private void addView(final DeviceDetail data) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_control_switch_btn, null);
        HueSeekBar seekbar = view.findViewById(R.id.hueSeekBar);
        final TextView btn1 = view.findViewById(R.id.btn_1);
        TextView btn2 = view.findViewById(R.id.btn_2);
        btn2.setVisibility(View.GONE);
        TextView btn3 = view.findViewById(R.id.btn_3);
        btn3.setVisibility(View.GONE);
        final List<TextView> tempList = new ArrayList<>();
        tempList.add(btn1);
        tempList.add(btn2);
        tempList.add(btn3);
        TextView switchName = view.findViewById(R.id.switch_name);
        switchName.setText(data.getSwitch_name());
        final List<ButtonDetail> btnlist = data.getButton_list();
        final CheckedTextView switchBtn = view.findViewById(R.id.switch_control);
        boolean curB = BtnStatusUtil.getSwitchBtnStatus(btnlist);
        switchBtn.setChecked(curB);
        setBtnBg(switchBtn,curB);
        for (int i = 0; i < btnlist.size(); i++) {
            final TextView nameView = tempList.get(i);
            nameView.setVisibility(View.VISIBLE);
            nameView.setText(btnlist.get(i).getButton_name());
            if (btnlist.get(i).getButton_status() == 0) {
                nameView.setBackgroundResource(R.mipmap.btn_on);
            } else {
                nameView.setBackgroundResource(R.mipmap.btn_off);
            }
            final int finalI = i;
            nameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ButtonDetail detail = btnlist.get(finalI);
                    changeBtnStatus(detail, nameView);
                    boolean b = BtnStatusUtil.getSwitchBtnStatus(btnlist);
                    switchBtn.setChecked(b);//开关总按键状态
                    setBtnBg(switchBtn,b);
                }
            });
            nameView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {
                    final ButtonDetail detail = btnlist.get(finalI);
                    final View layout = LayoutInflater.from(context).inflate(R.layout.dialog_addroom, null);
                    new AlertDialog.Builder(context).setTitle(R.string.input_switch_name).setView(layout)
                            .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    TextView et = layout.findViewById(R.id.etname);
                                    String name = et.getText().toString();
                                    if (!TextUtils.isEmpty(name)) {
                                        detail.setButton_name(name);
                                        mBtnDao.updateBtn(detail);
                                        nameView.setText(name);
                                        mHttp.updateButton(detail.getButton_id(), name, mHandler);
                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancel, null).show();
                    return false;
                }
            });
        }
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                clearMemory(RoomActivity.this);
                boolean b = switchBtn.isChecked();
                int status = (!b) ? 100 : 0;
                for (int i = 0; i < list.size(); i++) {
                    btnlist.get(i).setButton_status(status);
                    mBtnDao.updateBtn(btnlist.get(i));
                    tempList.get(i).setBackgroundResource((!b) ? R.mipmap.btn_off : R.mipmap.btn_on);
                    /**发送开关指令*/
                    mHttp.controlSwicht(data.getGateway_id(), status, data.getSwitch_id(), 4, 0, 0, 0, mHandler);
                }
                switchBtn.setChecked(!b);
                setBtnBg(switchBtn,!b);
                refreshBtnStatus();
            }
        });
        Log.e("--------------","--------------------curvalue:");
        if (data.getSwitch_type() == 0) {
            seekbar.setVisibility(View.VISIBLE);
        } else {
            seekbar.setVisibility(View.GONE);
        }
        final int[] curValue = {btnlist.get(0).getButton_status()};
        seekbar.setCurrentProgress(curValue[0]);//设置初始值
        seekbar.setProgressListener(new ProgressListener() {
            @Override
            public void onProgressChange(int progress) {
                if (progress!= curValue[0]){
                    curValue[0] = progress;
                    clearMemory(RoomActivity.this);
                    mHttp.controlSwichtBySeek(data.getGateway_id(), progress, data.getSwitch_id(), 5, 0, 0, 0, mHandler);
                    if (progress == 0) {
                        btn1.setBackgroundResource(R.mipmap.btn_on);
                        btnlist.get(0).setButton_status(0);
                        switchBtn.setChecked(false);
                        setBtnBg(switchBtn,false);
                    } else {
                        btn1.setBackgroundResource(R.mipmap.btn_off);
                        btnlist.get(0).setButton_status(progress);
                        switchBtn.setChecked(true);
                        setBtnBg(switchBtn,true);
                    }
                    mBtnDao.updateBtn(btnlist.get(0));
                    refreshBtnStatus();
                }
            }
        });

        seekbar.setVerticalAnimationListener(new VerticalAnimationListener() {
            @Override
            public void onAnimProgressChanged(int percentage) {
                if (percentage>60){

                }else{

                }
            }
        });
        layoutViews.addView(view);
    }

    //接收回调广播
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.BRODCASE_REFRESH_AFTERDB)) {
                initLayout();
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.switch_button:
                boolean b = totalSwitchBtn.isChecked();
                int status = (!b) ? 100 : 0;
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        List<ButtonDetail> btns = list.get(i).getButton_list();
                        for (int m = 0; m < btns.size(); m++) {
                            btns.get(m).setButton_status(status);
                            mBtnDao.updateBtn(btns.get(m));
                        }
                    }
                }
                //提交网络请求，如果失败了，则交由主页刷新全部按键状态
                mHttp.controlSwicht("", status, detail.getRoom_id(), 1, 0, 0, 0, mHandler);
                totalSwitchBtn.setChecked(!b);
                setBtnBg(totalSwitchBtn,!b);
                initLayout();
                break;
        }
    }

    public void refreshBtnStatus() {
        if (list != null) {
            boolean b = BtnStatusUtil.getRoomBtnStatus(list);
            totalSwitchBtn.setChecked(b);//设置房间总开关状态
            setBtnBg(totalSwitchBtn,b);
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case Constants.CONTROL_SWITCH_SUCCESS:

                break;
            case Constants.CONTROL_SWITCH_FALSE:
                BrodcaseTool.sendMainDataRefresh(this);
                break;
            case Constants.UPDATE_BUTTON_NAME_SUCCESS:

                break;
            case Constants.UPDATE_BUTTON_NAME_FALSE:
                BrodcaseTool.sendMainDataRefresh(this);
                break;
            case Constants.CONTROL_SWITCH_BY_SEEK_SUCCESS:

                break;
            case Constants.CONTROL_SWITCH_BY_SEEK_FALSE:
                DialogUtil.createEorreMsgDialog(this,getResources().getString(R.string.error));
                BrodcaseTool.sendMainDataRefresh(this);
                break;
        }
        return false;
    }

    private void changeBtnStatus(ButtonDetail detail, View view) {
        if (detail.getButton_status() == 0) {
            view.setBackgroundResource(R.mipmap.btn_off);
            detail.setButton_status(100);
            mHttp.controlSwicht(detail.getGateway_id(), 100, detail.getButton_id(), 0, 0, 0, 0, mHandler);
        } else {
            view.setBackgroundResource(R.mipmap.btn_on);
            detail.setButton_status(0);
            mHttp.controlSwicht(detail.getGateway_id(), 0, detail.getButton_id(), 0, 0, 0, 0, mHandler);
        }
        mBtnDao.updateBtn(detail);
        refreshBtnStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /**
     * 清理内存
     * @param context
     */
    public static void clearMemory(Context context) {
        ActivityManager activityManger = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = activityManger.getRunningAppProcesses();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ActivityManager.RunningAppProcessInfo apinfo = list.get(i);
                String[] pkgList = apinfo.pkgList;
                if (apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    for (int j = 0; j < pkgList.length; j++) {
                        /**清理不可用的内容空间**/
                        activityManger.killBackgroundProcesses(pkgList[j]);
                    }
                }
            }
        }
    }

    private void setBtnBg(CheckedTextView view,boolean b){
        if (b){
            view.setBackgroundResource(R.mipmap.switch_btn_on);
        }else{
            view.setBackgroundResource(R.mipmap.switch_btn_off);
        }
    }

}
