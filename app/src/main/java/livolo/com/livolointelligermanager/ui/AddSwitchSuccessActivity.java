package livolo.com.livolointelligermanager.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import livolo.com.livolointelligermanager.db.DeviceDao;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.RoomDetail;
import livolo.com.livolointelligermanager.mode.DeviceDetail;
import livolo.com.livolointelligermanager.receiver.BrodcaseTool;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.EditLimitUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.view.FlowLayoutView;

/**
 * Created by mayn on 2018/4/12.
 */

public class AddSwitchSuccessActivity extends BaseActivity implements Handler.Callback {

    public static final int ADDROOM = 1004;

    @BindView(R.id.flowlayout)
    FlowLayoutView flowlayout;
    @BindView(R.id.switch_name)
    EditText switchName;
    @BindView(R.id.complate_btn)
    Button complateBtn;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;

    private LayoutInflater mInflater;
    private List<RoomDetail> mRooms = new ArrayList<>();
    private List<TextView> mViews = new ArrayList<>();
    private HttpTools mHttp;
    private String mSwitchID;
    private String mSwitchName;
    private int curRoomIndex = 0;
    private Handler mHandler;
    private SweetAlertDialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addswitch_success);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.midblue);
        ButterKnife.bind(this);
        mInflater = LayoutInflater.from(this);
        mSwitchID = getIntent().getStringExtra("switchid");
        mSwitchName = getIntent().getStringExtra("switchname");
        mHttp = new HttpTools();
        mHandler = new Handler(this);

        dialog = new SweetAlertDialog(AddSwitchSuccessActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                .setContentText(getResources().getString(R.string.wait_update));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        mHttp.getdefaultName(Constants.HomeID, 2, "switch", mHandler);

        /**添加广播*/
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BRODCASE_REFRESH_AFTERDB);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRooms();
        initLayout(true);
    }

    private void initLayout(boolean b) {
        backBtn.setVisibility(View.INVISIBLE);
        topTitle.setText(R.string.addswitch_success_title);
        getRooms();
        if (!TextUtils.isEmpty(mSwitchID) && !TextUtils.isEmpty(mSwitchName)) {
            switchName.setText(mSwitchName);
            complateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(switchName.getText().toString())){
                        DialogUtil.createEmptyMsgDialog(AddSwitchSuccessActivity.this,R.string.devicename_cannot_be_empty);
                    }else{
                        if (EditLimitUtil.isLimit(switchName,20,AddSwitchSuccessActivity.this))
                        dialog = new SweetAlertDialog(AddSwitchSuccessActivity.this,SweetAlertDialog.PROGRESS_TYPE)
                                .setTitleText(getResources().getString(R.string.committing));
                        dialog.show();
                        RoomDetail room = mRooms.get(curRoomIndex);
                        mHttp.updateSwitch(room.getRoom_id(), mSwitchID, switchName.getText().toString(), 0, mHandler);
                        DeviceDetail detail = new DeviceDetail();
                        detail.setSwitch_name(switchName.getText().toString());
                        detail.setPicture_index(0);
                        detail.setRoom_id(room.getRoom_id());
                        detail.setGateway_id("");
                        detail.setSwitch_id(mSwitchID);
                        new DeviceDao().insert(detail);
                    }
                }
            });
        }
        mViews.clear();
        flowlayout.removeAllViews();
        /** 找到搜索标签的控件 */
        for (int i = 0; i < mRooms.size(); i++) {
            addView(mRooms.get(i).getRoom_name(), false);
        }
        addView(" + ", true);
        if (b){
            curRoomIndex = 0;
        }else{
            if (mViews!=null && mViews.size()>2){
                curRoomIndex = mViews.size()-2;
            }else{
                curRoomIndex = 0;
            }
        }
        if (mViews.size()>1){
            mViews.get(curRoomIndex).setBackgroundResource(R.drawable.border_light_blue);
        }
        setTextViewClick();
        EditLimitUtil.setEditLimit(switchName,20,this);
    }

    private void getRooms() {
        List<RoomDetail> tempRooms = ConfigUtil.getRoomListData();
        if (tempRooms != null && tempRooms.size() > 0) {
            mRooms.clear();
            for (int i = 0; i < tempRooms.size(); i++) {
                if (!tempRooms.get(i).getRoom_id().equals("00000000000000000000000000000000")) {
                    mRooms.add(tempRooms.get(i));
                }
            }
        }
    }

    private void addView(String str, boolean b) {
        TextView tv = (TextView) mInflater.inflate(
                R.layout.item_flowlayout, flowlayout, false);
        tv.setText(str);
        flowlayout.addView(tv);//添加到父View
        mViews.add(tv);

    }

    private void setTextViewClick() {
        for (int i = 0; i < mViews.size(); i++) {
            if (i == mViews.size() - 1) {
                mViews.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        addViewByDialog();
                        Intent intent = new Intent();
                        intent.setClass(AddSwitchSuccessActivity.this,ChoseRoomIconActivity.class);
                        intent.putExtra("singleadd",1);
                        startActivityForResult(intent,ADDROOM);
                    }
                });
            } else {
                //点击事件
                final int finalI = i;
                mViews.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTextViewBg();
                        curRoomIndex = finalI;
                        v.setBackgroundResource(R.drawable.border_light_blue);
                    }
                });
            }
        }
    }

    private void setTextViewBg() {
        for (int i = 0; i < mViews.size(); i++) {
            mViews.get(i).setBackgroundResource(R.drawable.text_bg);
            curRoomIndex = i;
        }
    }

    //接收回调广播
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("---------------",action);
            if (action.equals(Constants.BRODCASE_REFRESH_AFTERDB)) {
                getRooms();
                initLayout(false);
            }
        }
    };

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case Constants.UPDATE_SWITCH_SUCCESS:
                dialog.setTitleText(getResources().getString(R.string.addswitch_success_title))
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                BrodcaseTool.sendMainDataRefresh(AddSwitchSuccessActivity.this);
                                dialog.cancel();
                                finish();
                            }
                        }).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                break;
            case Constants.UPDATE_SWITCH_FALSE:
                dialog.setTitleText(message.obj.toString())
                        .setConfirmButton(R.string.sure,null)
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                break;
            case Constants.GET_DEFAULT_NAME_SUCCESS:
                if (dialog!=null){
                    dialog.cancel();
                }
                try {
                    JSONObject obj = new JSONObject(message.obj.toString());
                    String name = obj.getString("default_name");
                    switchName.setText(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.GET_DEFAULT_NAME_FALSE:
                if (dialog!=null){
                    dialog.cancel();
                }
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
