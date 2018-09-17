package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.adaper.RoomAdapter;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.db.RoomDao;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.RoomDetail;
import livolo.com.livolointelligermanager.receiver.BrodcaseTool;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.view.ActionSheetDialog;

/**
 * Created by mayn on 2018/4/13.
 * 房间管理 查看房间列表并操作增删改查
 */

public class RoomManagerActivity extends BaseActivity implements View.OnClickListener ,Handler.Callback{

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.top_right)
    TextView topRight;
    @BindView(R.id.room_listview)
    ListView roomListview;
    @BindView(R.id.cancel_btn)
    TextView cancelBtn;

    private RoomAdapter mAdapter;
    private List<RoomDetail> list = new ArrayList<>();
    private boolean isNeedShow = false;//是否展示多选框
    private RoomDao mDao;
    private HttpTools mHttp;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_manager);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.midblue);
        ButterKnife.bind(this);
        mDao = new RoomDao();
        mHttp = new HttpTools();
        mHandler = new Handler(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BRODCASE_REFRESH_AFTERDB);
        registerReceiver(receiver, filter);
        initLayout();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    //接收回调广播
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.BRODCASE_REFRESH_AFTERDB)){
                list.clear();
                List<RoomDetail> tempList = mDao.getRoomList(Constants.HomeID);
                if (tempList!=null){
                    list.addAll(mDao.getRoomList(Constants.HomeID));
                    for (int i = 0;i<list.size();i++){
                        if (list.get(i).getRoom_id().equals("00000000000000000000000000000000")){
                            list.remove(i);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        list.clear();
        List<RoomDetail> tempList = mDao.getRoomList(Constants.HomeID);
        if (tempList!=null){
            list.addAll(mDao.getRoomList(Constants.HomeID));
            for (int i = 0;i<list.size();i++){
                if (list.get(i).getRoom_id().equals("00000000000000000000000000000000")){
                    list.remove(i);
                }
            }
            mAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    private void initLayout() {
        topRight.setText(R.string.add);
        topTitle.setText(R.string.room_manager);
        backBtn.setOnClickListener(this);
        topRight.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        cancelBtn.setVisibility(View.GONE);
        list.addAll(mDao.getRoomList(Constants.HomeID));
        //http 获取房间列表
        mAdapter = new RoomAdapter(this, list, isNeedShow);
        roomListview.setAdapter(mAdapter);

        roomListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isNeedShow) {
                    Intent intent = new Intent();
                    intent.setClass(RoomManagerActivity.this, AddRoomActivity.class);
                    intent.putExtra("room", list.get(i));
                    startActivity(intent);
                }
            }
        });
        roomListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if (!isNeedShow) {
                    new ActionSheetDialog(RoomManagerActivity.this).builder()
                            .setCancelable(true)
                            .addSheetItem(getResources().getString(R.string.delete), ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {

                                    String[] roomids = new String[1];
                                    roomids[0] = list.get(i).getRoom_id();
                                    list.remove(i);
                                    mHttp.deleteRooms(roomids,mHandler);
                                    mAdapter.notifyDataSetChanged();
                                }
                            })
                            .addSheetItem(getResources().getString(R.string.delete_selected), ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    //显示复选框
                                    rightTitleSet();
                                }
                            }).show();
                }
                return true;
            }
        });
    }

    private void rightTitleSet() {
        isNeedShow = !isNeedShow;
        mAdapter.setCheckBox(isNeedShow);
        mAdapter.notifyDataSetChanged();
        if (isNeedShow) {
            topRight.setText(R.string.cancel);
            cancelBtn.setVisibility(View.VISIBLE);
        } else {
            topRight.setText(R.string.add);
            cancelBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.top_right:
                if (isNeedShow) {
                    //取消
                    rightTitleSet();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(RoomManagerActivity.this, ChoseRoomIconActivity.class);
//                    intent.setClass(this, AddRoomActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.cancel_btn:
                /**批量删除操作*/
                List<String> tempList = new ArrayList<>();
                if (list!=null){
                    for (int i = 0;i<list.size();i++){
                        if (list.get(i).isSelected()){
                            tempList.add(list.get(i).getRoom_id());
                            Log.e("------","----------");
                        }
                    }
                    for (int i = 0;i<list.size();i++){
                        if (list.get(i).isSelected()){
                            list.remove(i);
                            i = -1;//重新过滤，不然会越过不少item
                        }
                    }
                }
                if (tempList!=null && tempList.size()>0){
                    String[] roomids = new String[tempList.size()];
                    for (int i = 0;i<tempList.size();i++){
                        roomids[i] = tempList.get(i);
                    }
                    mHttp.deleteRooms(roomids,mHandler);
                }
                /**批量删除后，复原原界面*/
                rightTitleSet();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (isNeedShow){
                isNeedShow = !isNeedShow;
                mAdapter.setCheckBox(isNeedShow);
                mAdapter.notifyDataSetChanged();
                topRight.setText(R.string.add);
                cancelBtn.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case Constants.DELETE_ROOM_SUCCESS:
                Log.e("","");
                BrodcaseTool.sendMainDataRefresh(this);
                break;
            case Constants.DELETE_ROOM_FALSE:
                DialogUtil.createEmptyMsgDialog(this,getResources().getString(R.string.token_time_out));
                break;
        }
        return false;
    }
}
