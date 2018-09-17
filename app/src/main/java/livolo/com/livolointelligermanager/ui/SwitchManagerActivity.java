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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.adaper.SwitchExpandableListAdapter;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.db.ButtonDao;
import livolo.com.livolointelligermanager.db.RoomDao;
import livolo.com.livolointelligermanager.db.DeviceDao;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.ButtonDetail;
import livolo.com.livolointelligermanager.mode.RoomDetail;
import livolo.com.livolointelligermanager.mode.DeviceDetail;
import livolo.com.livolointelligermanager.receiver.BrodcaseTool;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.view.ActionSheetDialog;

/**
 * Created by mayn on 2018/4/12.
 */

/**
 * 此页面有两个作用，一个是情景模式选择开关，一个是开关管理。
 * true 表示开关管理，可长按item修改开关名称或者删除开关，无点击应答
 * false 表示情景模式选择，点击有应答，长按无应答
 */

public class SwitchManagerActivity extends BaseActivity implements View.OnClickListener ,Handler.Callback{

    @BindView(R.id.switch_listview)
    ExpandableListView switchListview;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.top_right)
    TextView topRight;

    private SwitchExpandableListAdapter mAdapter;
    private List<RoomDetail> list;
    private RoomDao mRoomDao;
    private DeviceDao mDeviceDao;
    private ButtonDao mBtnDao;
    private HttpTools mHttp;
    private Handler mHandler;

    private int deletegroup;
    private int deleteindex;
    private int updategroup;
    private int updateindex;
    private String updateName;

    private boolean isManager = true;//true 表示开关管理，可长按item修改开关名称或者删除开关，无点击应答 ,false 表示情景模式选择，点击有应答，长按无应答

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.midblue);
        ButterKnife.bind(this);
        isManager = getIntent().getBooleanExtra("isManager", false);
        mRoomDao = new RoomDao();
        mDeviceDao = new DeviceDao();
        mBtnDao = new ButtonDao();
        mHttp = new HttpTools();
        mHandler = new Handler(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BRODCASE_REFRESH_AFTERDB);
        registerReceiver(receiver,filter);
        initLayout();

    }

    //接收回调广播
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.BRODCASE_REFRESH_AFTERDB)){
                refresh();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void refresh(){
        List<RoomDetail> tempList = ConfigUtil.getRoomListData();
        if (tempList!=null){
            list.clear();
            list.addAll(tempList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initLayout() {
        topTitle.setText(R.string.switch_manager_title);
        if (isManager) {
            topRight.setText(R.string.add);
        }
        backBtn.setOnClickListener(this);
        topRight.setOnClickListener(this);

        /**从本地数据库获取所有开关信息*/
        list = ConfigUtil.getRoomListData();
        mAdapter = new SwitchExpandableListAdapter(this, list);
        switchListview.setAdapter(mAdapter);
        //遍历所有group,将所有项设置成默认展开
        int groupCount = switchListview.getCount();
        for (int i = 0; i < groupCount; i++) {
            switchListview.expandGroup(i);
        }
        ;
        //group 点击无效
        switchListview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;//返回true,表示不可点击
            }
        });

        switchListview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                if (!isManager) {
                    selectFunc(i,i1);
                } else {
                    managerFunc(i, i1);
                }
                return false;
            }
        });
    }

    private void selectFunc(int groupIndex, int childIndex) {
        ActionSheetDialog dialog = new ActionSheetDialog(SwitchManagerActivity.this).builder()
                .setTitle("按键状态选择")
                .setCancelable(true);
        DeviceDetail selectSwitch = list.get(groupIndex).getSwitch_list().get(childIndex);
        if (selectSwitch != null && selectSwitch.getButton_list() != null) {
            for (int i = 0; i < selectSwitch.getButton_list().size(); i++) {
                final ButtonDetail btn = selectSwitch.getButton_list().get(i);
                dialog.addSheetItem(btn.getButton_name() + " " + getResources().getString(R.string.open), ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        btn.setButton_status(100);
                        setResult(btn);
                    }
                });
                dialog.addSheetItem(btn.getButton_name() + " " + getResources().getString(R.string.close), ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        btn.setButton_status(0);
                        setResult(btn);
                    }
                });
            }
        }
        dialog.show();
    }

    private void setResult(ButtonDetail detail) {
        Intent intent = new Intent();
        intent.putExtra("btn", detail);
        setResult(Constants.GET_RESULT_FOR_SWITCH, intent);
        finish();
    }

    private void managerFunc(final int groupIndex, final int childIndex) {
        new ActionSheetDialog(SwitchManagerActivity.this).builder()
                .setCancelable(true)
                .addSheetItem(getResources().getString(R.string.rename), ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        //更改本地数据库数据
                        rename(groupIndex, childIndex);
                    }
                })
                .addSheetItem(getResources().getString(R.string.delete), ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        deletegroup = groupIndex;
                        deleteindex = childIndex;
                        mHttp.deleteSwitch(list.get(groupIndex).getSwitch_list().get(childIndex),mHandler);
                    }
                }).show();
    }

    private void rename(final int groupIndex, final int childIndex) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_addroom, null);
        View titleView = inflater.inflate(R.layout.dialog_title, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.AlertDialog).setCustomTitle(titleView).setView(layout).show();
        final TextView et = layout.findViewById(R.id.etname);
        Button sureBtn = layout.findViewById(R.id.btn_sure);
        Button cancelBtn = layout.findViewById(R.id.btn_cancal);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    DeviceDetail mSwitch = list.get(groupIndex).getSwitch_list().get(childIndex);
                    updategroup = groupIndex;
                    updateindex = childIndex;
                    updateName = name;
                    mHttp.updateSwitch(mSwitch.getRoom_id(),mSwitch.getSwitch_id(),name,mSwitch.getPicture_index(),mHandler);

                } else {
                    Toast.makeText(SwitchManagerActivity.this, getResources().getString(R.string.rename_isempty), Toast.LENGTH_LONG).show();
                }
                    alertDialog.dismiss();
            }
        });
    }

    private void refresh(int position) {
        switchListview.collapseGroup(position);
        switchListview.expandGroup(position);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.top_right:
                Intent intent = new Intent();
                intent.setClass(this, AddSwitchActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case Constants.UPDATE_SWITCH_SUCCESS:
                mDeviceDao.updateSwitch(list.get(updategroup).getSwitch_list().get(updateindex).getSwitch_id(), "", updateName, "");
                list.clear();
                list.addAll(ConfigUtil.getRoomListData());
                refresh(updategroup);
                BrodcaseTool.sendMainDataRefresh(this);
                break;
            case Constants.UPDATE_SWITCH_FALSE:
                DialogUtil.createEmptyMsgDialog(this,message.obj.toString());
                break;
            case Constants.DELETE_SWITCH_SUCCESS:
                BrodcaseTool.sendMainDataRefresh(this);
                mDeviceDao.delete(list.get(deletegroup).getSwitch_list().get(deleteindex).getSwitch_id());
                list.clear();
                list.addAll(ConfigUtil.getRoomListData());
                refresh(deletegroup);
                break;
            case Constants.DELETE_SWITCH_FALSE:
                DialogUtil.createEmptyMsgDialog(this,message.obj.toString());
                break;
        }
        return false;
    }
}
