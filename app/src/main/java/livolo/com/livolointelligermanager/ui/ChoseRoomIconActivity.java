package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.adaper.ChoseRoomIconAdapter;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.RoomDetail;
import livolo.com.livolointelligermanager.receiver.BrodcaseTool;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.EditLimitUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;

/**
 * Created by mayn on 2018/5/23.
 */

public class ChoseRoomIconActivity extends BaseActivity implements View.OnClickListener,Handler.Callback{

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
    @BindView(R.id.next_btn)
    Button nextBtn;
    @BindView(R.id.gridView_icon)
    GridView gridViewIcon;

//    private boolean b = true;//true 表示正常添加房间，false 开关添加时新添加房间无开关列表
    private int actionType = 0;//0表示正常添加房间，1表示添加开关时新添加房间（无开关列表），2表示修改房间时修改房间的名字和列表
    private int curIcon = 0;
    private ChoseRoomIconAdapter adapter;
    private HttpTools mHttp;
    private Handler mHandler;
    private SweetAlertDialog mDialog;

    private static int[] icons = {R.mipmap.room_black_0,R.mipmap.room_black_1,R.mipmap.room_black_2,R.mipmap.room_black_3,R.mipmap.room_black_4,
            R.mipmap.room_black_5,R.mipmap.room_black_6,R.mipmap.room_black_7,R.mipmap.room_black_8,R.mipmap.room_black_9,
            R.mipmap.room_black_10,R.mipmap.room_black_11,R.mipmap.room_black_12,R.mipmap.room_black_13,R.mipmap.room_black_14};
    private static int[] rooms = {R.string.roomname_0,R.string.roomname_1,R.string.roomname_2,R.string.roomname_3,R.string.roomname_4,
            R.string.roomname_5,R.string.roomname_6,R.string.roomname_7,R.string.roomname_8,R.string.roomname_9,
            R.string.roomname_10,R.string.roomname_11,R.string.roomname_12,R.string.roomname_13,R.string.roomname_14};

    private static Activity activity;

    public static Activity getInstance() {
        return activity;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choseroomicon);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.midblue);
        ButterKnife.bind(this);
        actionType = getIntent().getIntExtra("singleadd",0);
        mHttp = new HttpTools();
        mHandler = new Handler(this);
        activity = this;
        backBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        if (actionType == 2){
            curIcon = getIntent().getIntExtra("roomIcon",0);
            String name = getIntent().getStringExtra("roomName");
            if (!TextUtils.isEmpty(name)){
                roomName.setText(name);
            }
            topTitle.setText(R.string.room_manager);
        }else{
            topTitle.setText(R.string.addroom_title);
            roomName.setText(rooms[curIcon]);
        }
        //初始化页面的图标和名字
        Glide.with(this).load(icons[curIcon]).into(roomIcon);
        adapter = new ChoseRoomIconAdapter(this,icons,rooms);
        gridViewIcon.setNumColumns(4);
        gridViewIcon.setAdapter(adapter);
        gridViewIcon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                curIcon = i;
                roomName.setText(rooms[i]);
                Glide.with(ChoseRoomIconActivity.this).load(icons[i]).into(roomIcon);
//                mDialog = new SweetAlertDialog(ChoseRoomIconActivity.this,SweetAlertDialog.PROGRESS_TYPE)
//                        .setContentText(getResources().getString(R.string.wait_update));
//                mDialog.setCanceledOnTouchOutside(false);
//                mDialog.show();
                mHttp.getdefaultName(Constants.HomeID,3,getResources().getString(rooms[i]),mHandler);
            }
        });
        EditLimitUtil.setEditLimit(roomName,20,this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.next_btn:
                if (TextUtils.isEmpty(roomName.getText().toString())){
                    DialogUtil.createEmptyMsgDialog(this,R.string.room_is_name_cannot_be_null);
                    break;
                }
                if (!EditLimitUtil.isLimit(roomName,20,this)){
                    break;
                }
                if (actionType == 0){
                    RoomDetail roomDetail = new RoomDetail();
                    roomDetail.setRoom_name(roomName.getText().toString());
                    roomDetail.setPicture_index(curIcon);
                    Intent intent = new Intent(this,AddRoomActivity.class);
                    intent.putExtra("room",roomDetail);
                    startActivity(intent);
                }else if (actionType == 1){
                    //直接添加房间 ---> 服务端返回添加成功 ---> 关闭界面 --->
                    mHttp.addRoom(Constants.HomeID,roomName.getText().toString(),curIcon,null,mHandler);
                }else if (actionType == 2){
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("roomIcon",curIcon);
                    resultIntent.putExtra("roomName",roomName.getText().toString());
                    setResult(1,resultIntent);
                    finish();
                }
                break;
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case Constants.ADDROOM_SUCCESS:
                Toast.makeText(this,R.string.add_success,Toast.LENGTH_SHORT).show();
                BrodcaseTool.sendMainDataRefresh(this);
                finish();
                break;
            case Constants.ADDROOM_FALSE:
                Toast.makeText(this,R.string.add_false,Toast.LENGTH_SHORT).show();
                finish();
                break;

            case Constants.GET_DEFAULT_NAME_SUCCESS:
                if (mDialog!=null){
                    mDialog.cancel();
                }
                try {
                    JSONObject obj = new JSONObject(message.obj.toString());
                    String name = obj.getString("default_name");
                    roomName.setText(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.GET_DEFAULT_NAME_FALSE:
                if (mDialog!=null){
                    mDialog.cancel();
                }
                break;
        }
        return false;
    }
}
