package livolo.com.livolointelligermanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.adaper.AddRoomItemAdapter;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.DeviceDetail;
import livolo.com.livolointelligermanager.mode.RoomDetail;
import livolo.com.livolointelligermanager.receiver.BrodcaseTool;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.EditLimitUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.view.MyListView;

/**
 * Created by mayn on 2018/4/13.
 * 添加房间、修改房间
 */

public class AddRoomActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.top_right)
    TextView topRight;
    @BindView(R.id.room_icon)
    ImageView roomIcon;
    @BindView(R.id.room_name)
    EditText roomName;
    @BindView(R.id.unbind_listview)
    MyListView unbindListview;
    @BindView(R.id.bind_title)
    TextView bindTitle;
    @BindView(R.id.bind_listview)
    MyListView bindListview;

    private RoomDetail roomDetail;
    private AddRoomItemAdapter mUnbindAdapter;
    private AddRoomItemAdapter mBindAdapter;
    private List<DeviceDetail> mSwitchList = new ArrayList<>();//表示已添加的房间
    private List<DeviceDetail> mUnSwitchList = new ArrayList<>();//表示未添加的房间
    private List<DeviceDetail> mAddSwitchs = new ArrayList<>();
    private Map<String, String> mMap;
    private HttpTools mHttp;
    private Handler mHandler;
    private SweetAlertDialog dialog;
    private int curPic = 0;
    private String curRoomID = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addroom);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.midblue);
        ButterKnife.bind(this);
        mHttp = new HttpTools();
        mHandler = new Handler(this);
        roomDetail = (RoomDetail) getIntent().getSerializableExtra("room");
        mMap = new HashMap<>();
        initLayout();
    }

    private void initLayout() {
        backBtn.setOnClickListener(this);
        topRight.setText(R.string.save);
        topRight.setOnClickListener(this);
        bindTitle.setOnClickListener(this);
        getSwitchList(ConfigUtil.getRoomListData());
        curPic = roomDetail.getPicture_index();
        Glide.with(this).load(ConfigUtil.getRoomBlackIcon(curPic)).into(roomIcon);
        roomName.setText(roomDetail.getRoom_name());
        if (TextUtils.isEmpty(roomDetail.getRoom_id())) {//创建房间模式
            curRoomID = "";
            getSwitchList(ConfigUtil.getRoomListData());
            topTitle.setText(R.string.addroom_title);
            mUnbindAdapter = new AddRoomItemAdapter(this, "", mUnSwitchList, mMap);
        } else {//修改房间模式
            curRoomID = roomDetail.getRoom_id();
            getSwitchList(ConfigUtil.getRoomListData());
            topTitle.setText(R.string.room_manager);
            mUnbindAdapter = new AddRoomItemAdapter(this, roomDetail.getRoom_id(), mUnSwitchList, mMap);
            roomIcon.setOnClickListener(this);
            roomName.setOnClickListener(this);
        }
        unbindListview.setAdapter(mUnbindAdapter);
        mUnbindAdapter.notifyDataSetChanged();
        mBindAdapter = new AddRoomItemAdapter(this,"",mSwitchList,mMap);
        bindListview.setAdapter(mBindAdapter);
        mBindAdapter.notifyDataSetChanged();
        EditLimitUtil.setEditLimit(roomName,20,this);
    }

    private void getSwitchList(List<RoomDetail> list) {
        if (list != null && list.size() > 0) {
            mUnSwitchList.clear();
            for (RoomDetail detail : list) {//筛选出本房间的开关
                if (detail.getRoom_id().equals(curRoomID) && detail.getSwitch_list() != null) {
                    mUnSwitchList.addAll(detail.getSwitch_list());
                }
            }

            for (RoomDetail detail : list) {//筛选出未纳入房间的开关
                if (detail.getRoom_id().equals("00000000000000000000000000000000") && detail.getSwitch_list() != null) {
                    mUnSwitchList.addAll(detail.getSwitch_list());
                }
            }
            mSwitchList.clear();
            for (RoomDetail detail : list) {//剩余已经被纳入其他房间的开关
                if (!detail.getRoom_id().equals(curRoomID) && !detail.getRoom_id().equals("00000000000000000000000000000000") && detail.getSwitch_list() != null) {
                    mSwitchList.addAll(detail.getSwitch_list());
                }
            }
            for (int i = 0; i < list.size(); i++) {
                mMap.put(list.get(i).getRoom_id(), list.get(i).getRoom_name());
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.top_right:
                if (roomName.getText().toString().equals("")) {
                    dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getResources().getString(R.string.room_is_name_cannot_be_null))
                            .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.cancel();
                                }
                            });
                    dialog.show();
                } else {
                    if (EditLimitUtil.isLimit(roomName,20,this)){
                        dialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText(getResources().getString(R.string.be_need_save))
                                .setConfirmButton(R.string.yes, new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        dialog.showCancelButton(false).changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                        for (int i = 0; i < mUnSwitchList.size(); i++) {
                                            if (mUnSwitchList.get(i).isSelected()) {
                                                mAddSwitchs.add(mUnSwitchList.get(i));
                                            }
                                        }
                                        if (!TextUtils.isEmpty(roomDetail.getRoom_id())) {
                                            mHttp.updateRoom(roomDetail.getRoom_id(), roomName.getText().toString(), curPic, mAddSwitchs, mHandler);
                                        } else {
                                            mHttp.addRoom(Constants.HomeID, roomName.getText().toString(), curPic, mAddSwitchs, mHandler);
                                        }
                                    }
                                })
                                .setCancelButton(R.string.no, new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        dialog.cancel();
                                    }
                                });
                        dialog.show();
                    }
                }
                break;
            case R.id.room_icon:
                changRoomIconAndName();
                break;
            case R.id.room_name:
                changRoomIconAndName();
                break;
            case R.id.bind_title:
                if (bindListview.getVisibility() == View.VISIBLE){
                    bindListview.setVisibility(View.GONE);
                }else{
                    bindListview.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void changRoomIconAndName() {
        Intent intent = new Intent();
        intent.setClass(this, ChoseRoomIconActivity.class);
        intent.putExtra("roomIcon", roomDetail.getPicture_index());
        intent.putExtra("roomName", roomDetail.getRoom_name());
        intent.putExtra("singleadd", 2);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            String name = data.getStringExtra("roomName");
            int iconNum = data.getIntExtra("roomIcon", 0);
            roomName.setText(name);
            curPic = iconNum;
            Glide.with(this).load(ConfigUtil.getRoomBlackIcon(curPic)).into(roomIcon);
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case Constants.ADDROOM_SUCCESS:
                dialog.setTitleText(getResources().getString(R.string.add_success)).showCancelButton(false)
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                if (ChoseRoomIconActivity.getInstance() != null) {
                                    ChoseRoomIconActivity.getInstance().finish();
                                }
                                dialog.dismiss();
                                BrodcaseTool.sendMainDataRefresh(AddRoomActivity.this);
                                finish();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                break;
            case Constants.ADDROOM_FALSE:
                if (dialog != null) {
                    dialog.cancel();
                }
                DialogUtil.createEorreMsgDialog(this, message.obj.toString());
                break;
            case Constants.UPDATE_ROOM_SUCCESS:
                dialog.setTitleText(getResources().getString(R.string.update_success)).showCancelButton(false)
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                dialog.cancel();
                                BrodcaseTool.sendMainDataRefresh(AddRoomActivity.this);
                                finish();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                break;
            case Constants.UPDATE_ROOM_FALSE:
                if (dialog != null) {
                    dialog.cancel();
                }
                DialogUtil.createEorreMsgDialog(this, message.obj.toString());
                break;
        }
        return false;
    }
}
