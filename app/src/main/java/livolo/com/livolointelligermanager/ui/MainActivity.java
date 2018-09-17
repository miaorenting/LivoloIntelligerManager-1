package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;
import com.zcolin.gui.zrecyclerview.ZRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.fogcloud.fog_mqtt.helper.MQTTErrCode;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.adaper.HomeAdapter;
import livolo.com.livolointelligermanager.adaper.MainRoomAdapter;
import livolo.com.livolointelligermanager.adaper.MianSenceAdapter;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.db.ButtonDao;
import livolo.com.livolointelligermanager.db.DeviceDao;
import livolo.com.livolointelligermanager.db.RoomDao;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.DeviceDetail;
import livolo.com.livolointelligermanager.mode.HomeDetail;
import livolo.com.livolointelligermanager.mode.RoomDetail;
import livolo.com.livolointelligermanager.mode.SenceDetail;
import livolo.com.livolointelligermanager.mode.UserInfoDetail;
import livolo.com.livolointelligermanager.receiver.BrodcaseTool;
import livolo.com.livolointelligermanager.service.MQTTService;
import livolo.com.livolointelligermanager.service.UpdateService;
import livolo.com.livolointelligermanager.util.BtnStatusUtil;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.EditLimitUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.util.WindowManagerUtil;
import livolo.com.livolointelligermanager.view.MultiShapeView;

public class MainActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {

    @BindView(R.id.switch_button)
    CheckedTextView switchButton;
    @BindView(R.id.setting)
    TableRow setting;
    @BindView(R.id.room_manager)
    TableRow roomManager;
    @BindView(R.id.gateway_manager)
    TableRow gatewayManager;
    @BindView(R.id.switch_manager)
    TableRow switchManager;
    @BindView(R.id.sence_manager)
    TableRow senceManager;
    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.side_menu_btn)
    ImageView sideMenuBtn;
    @BindView(R.id.user_home)
    TextView userHome;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.room_list)
    ZRecyclerView roomList;
    @BindView(R.id.side_menu)
    LinearLayout sideMenu;
    @BindView(R.id.main_face)
    LinearLayout mainFace;
    @BindView(R.id.feedback)
    TableRow feedback;
    @BindView(R.id.login_out)
    TableRow loginOut;
    @BindView(R.id.home_manager)
    TableRow homeManager;
    @BindView(R.id.person_header)
    MultiShapeView personHeader;

    private MianSenceAdapter mSenceAdapter;
    public List<SenceDetail> mSences = new ArrayList<SenceDetail>();
    private MainRoomAdapter mRoomAdapter;
    public List<RoomDetail> mRooms = new ArrayList<RoomDetail>();
    private Handler mHander;
    private HttpTools mHttp;

    private RoomDao mRoomDao;
    private DeviceDao mDeviceDao;
    private ButtonDao mButtonDao;
    private static Activity activity;

    private int width = 0;

    private Intent mqttService;

    public static Activity getInstance() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        StatusBarUtils.setWindowStatusBarColor(this, R.color.deepblue);
        ButterKnife.bind(this);
        mHander = new Handler(this);
        mHttp = new HttpTools();
        mRoomDao = new RoomDao();
        mDeviceDao = new DeviceDao();
        mButtonDao = new ButtonDao();
        mHttp.getUserInfo(mHander);
        /**获取屏幕的高度和宽度*/
        width = WindowManagerUtil.getWindowWidth(this);
        /**添加广播*/
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BRODCASE_MAIN_REFRESH);
        filter.addAction(Constants.BRODCASE_TOTALBTN_REFRESH);
        filter.addAction(Constants.BRODCASE_MAIN_REFRESH_ALL_DATA);
        registerReceiver(receiver, filter);

        //如果上次登录的userid与本次的一样 则默认进入上次所选的homeid的家庭，如果homeid为空 则直接弹出home列表选择，如果上次和本次userid不同，则直接弹出home列表选择
        String oldUserID = ConfigUtil.getUserID();
        String oldHomeID = ConfigUtil.getHomeID();
        if (oldUserID.equals(Constants.UserID) && !TextUtils.isEmpty(oldHomeID)) {//上次与本次同意个userid
            Constants.HomeID = oldHomeID;
            mHttp.getHomeSwitchBtn(oldHomeID, mHander);
            mHttp.getSenceList(oldHomeID, mHander);
            userHome.setText(ConfigUtil.getHomeName());
            //启动MQTT服务器
            mqttService = new Intent(this, MQTTService.class);
            startService(mqttService);
        } else {
            ConfigUtil.setUserID(Constants.UserID);
            ConfigUtil.setHomeID("");
            Constants.HomeID = "";
            userHome.setText(R.string.add_home_first);
            mRoomDao.deleteAll();
            mDeviceDao.deleteAll();
            mButtonDao.deleteAll();
            //oldHomeID重新
//            mHttp.getHomelist(Constants.UserID, mHander);
        }
        initLayout();//初始化界面
    }

    private void initLayout() {
//        homePopwin.setVisibility(View.GONE);
        sideMenuBtn.setOnClickListener(this);
        setSildMune();
        //设置布局管理器
        getSences();
        getRooms();

        userHome.setOnClickListener(this);
        switchManager.setOnClickListener(this);
        roomManager.setOnClickListener(this);
        senceManager.setOnClickListener(this);
        setting.setOnClickListener(this);
        feedback.setOnClickListener(this);
        loginOut.setOnClickListener(this);
        switchButton.setOnClickListener(this);
        userHome.setOnClickListener(this);
        homeManager.setOnClickListener(this);
        personHeader.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRooms();
        refreshAdapter();
        refreshTotleBtn();
        if (TextUtils.isEmpty(Constants.HomeID)) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getResources().getString(R.string.home_chose))
                    .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            if (TextUtils.isEmpty(Constants.HomeID)) {
                                mHttp.getHomelist(Constants.UserID, mHander);
                            }
                            sweetAlertDialog.cancel();
                        }
                    }).show();
        } else {
            mHttp.getSenceList(Constants.HomeID, mHander);
        }
        userHome.setText(ConfigUtil.getHomeName());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if (mqttService!=null){
            stopService(mqttService);
            mqttService = null;
        }

    }

    /**
     * 情景列表 设置
     */
    private void getSences() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerview.setLayoutManager(linearLayoutManager);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) recyclerview.getLayoutParams();
        lp.setMargins(4 * width / 100, 0, 4 * width / 100, 0);
        recyclerview.setLayoutParams(lp);
        //设置适配器
        mSenceAdapter = new MianSenceAdapter(this, mSences, (int) (width * 2.3) / 10, mHttp, mHander);
        recyclerview.setAdapter(mSenceAdapter);
    }

    /**
     * 设置主页面房间数据
     */
    private void getRooms() {
        mRoomAdapter = new MainRoomAdapter(this, mHander);
        mRoomAdapter.setDatas(mRooms);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) recyclerview.getLayoutParams();
        lp.setMargins(4 * width / 100, 0, 4 * width / 100, 0);
        roomList.setLayoutParams(lp);
        roomList.setGridLayout(true, 2);
        roomList.setAdapter(mRoomAdapter);
        roomList.setEmptyView(this, R.layout.maim_room_empty);
        roomList.setOnPullLoadMoreListener(new ZRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                if (TextUtils.isEmpty(ConfigUtil.getHomeID())) {
                    roomList.setPullLoadMoreCompleted();
                } else {
                    roomList.setPullLoadMoreCompleted();
                    mHttp.getHomeSwitchBtn(ConfigUtil.getHomeID(), mHander);
                }
            }

            @Override
            public void onLoadMore() {
                roomList.setPullLoadMoreCompleted();
            }
        });
        roomList.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<RoomDetail>() {
            @Override
            public void onItemClick(View covertView, int position, RoomDetail data) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, RoomActivity.class);
                intent.putExtra("room", data);
                startActivity(intent);
            }
        });
    }

    /**
     * 侧滑菜单管理
     */
    private void setSildMune() {
        //侧滑菜单管理
        gatewayManager.setOnClickListener(this);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                //获取屏幕的宽高
                WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) mainFace.getLayoutParams();
                p.setMargins(sideMenu.getRight(), 0, -sideMenu.getRight(), 0);
                mainFace.requestLayout();
                mainFace.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (TextUtils.isEmpty(Constants.HomeID) && (view.getId() != R.id.user_home)) {
            return;
        }
        switch (view.getId()) {
            case R.id.side_menu_btn:
                showDrawerLayout();

                break;
            case R.id.gateway_manager:
                Intent intent = new Intent();
                intent.setClass(this, GatewayManagerActivity.class);
                startActivity(intent);
                drawer.closeDrawer(Gravity.LEFT);
                break;
            case R.id.switch_manager:
                Intent intentSwitch = new Intent(this, SwitchManagerActivity.class);
                intentSwitch.putExtra("isManager", true);
                startActivity(intentSwitch);
                drawer.closeDrawer(Gravity.LEFT);
                break;
            case R.id.room_manager:
                Intent roomManager = new Intent(this, RoomManagerActivity.class);
                startActivity(roomManager);
                drawer.closeDrawer(Gravity.LEFT);
                break;
            case R.id.sence_manager:
                Intent senceManager = new Intent();
                senceManager.setClass(this, SenceManagerActivity.class);
                startActivity(senceManager);
                drawer.closeDrawer(Gravity.LEFT);
                break;
            case R.id.setting:
                Intent setIntent = new Intent();
                setIntent.setClass(this, SetManagerActivity.class);
                startActivity(setIntent);
                drawer.closeDrawer(Gravity.LEFT);
                break;
            case R.id.feedback:
                drawer.closeDrawer(Gravity.LEFT);
                Intent feed = new Intent(this, FeedbackActivity.class);
                startActivity(feed);
                drawer.closeDrawer(Gravity.LEFT);
                break;
            case R.id.login_out:
                Intent loginOut = new Intent(this, LoginActivity.class);
                startActivity(loginOut);
                finish();
                break;
            case R.id.switch_button:
                boolean b = switchButton.isChecked();
                int status = (!b ? 100 : 0);
                if (mRooms != null) {
                    for (int i = 0; i < mRooms.size(); i++) {
                        List<DeviceDetail> switchs = mRooms.get(i).getSwitch_list();
                        if (switchs != null) {
                            for (int m = 0; m < switchs.size(); m++) {
                                mButtonDao.updateBtnStatus(switchs.get(m).getSwitch_id(), status);
                            }
                        }
                    }
                    mRooms = ConfigUtil.getRoomListData();
                    if (mRooms != null) {
                        for (int i = 0; i < mRooms.size(); i++) {
                            if (mRooms.get(i).getRoom_id().equals("00000000000000000000000000000000")) {
                                mRooms.remove(i);
                            }
                        }
                    }
                    refreshAdapter();
                }
                mHttp.controlSwicht("", status, Constants.HomeID, 2, 0, 0, 0, mHander);
                switchButton.setChecked(!b);
                if (!b) {
                    switchButton.setBackgroundResource(R.mipmap.switch_btn_on);
                } else {
                    switchButton.setBackgroundResource(R.mipmap.switch_btn_off);
                }
                break;
            case R.id.user_home:
//                Intent intentColor = new Intent();
//                intentColor.setClass(this,SwitchChangeActivity.class);
//                startActivity(intentColor);
                mHttp.getHomelist(Constants.UserID, mHander);//先获取homelist
                break;
            case R.id.home_manager:
                Intent intentHome = new Intent();
                intentHome.setClass(this, HomeManagerActivity.class);
                startActivity(intentHome);
                break;
            case R.id.person_header:
                Intent userIntent = new Intent(this,UserInfoActivity.class);
                startActivity(userIntent);
                break;
        }
    }

    /**
     * 存入数据库
     */
    private void insertDB(List<RoomDetail> list) {
        mDeviceDao.deleteAll();
        mButtonDao.deleteAll();
        if (list.size() > 0) {
            mRoomDao.insert(list);
            for (int i = 0; i < list.size(); i++) {
                RoomDetail room = list.get(i);
                List<DeviceDetail> switchs = room.getSwitch_list();
                if (switchs != null && switchs.size() > 0) {
                    mDeviceDao.insert(switchs);
                    for (int m = 0; m < switchs.size(); m++) {
                        DeviceDetail deviceDetail = switchs.get(m);
                        if (deviceDetail.getButton_list() != null && deviceDetail.getButton_list().size() > 0) {
                            mButtonDao.insert(deviceDetail.getButton_list());
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case Constants.GET_HOME_SWITCH_BTN_SUCCESS:
                List<RoomDetail> temprooms = new Gson().fromJson(message.obj.toString(), new TypeToken<List<RoomDetail>>() {
                }.getType());
                List<RoomDetail> rooms = ConfigUtil.setRoomDetails(temprooms);
                insertDB(rooms);
                refreshRooms();
                refreshAdapter();
                refreshTotleBtn();
                roomList.setPullLoadMoreCompleted();
                BrodcaseTool.sendRefreshAfterDB(this);
                break;
            case Constants.GET_HOME_SWITCH_BTN_FALSE:
                DialogUtil.createEmptyMsgDialog(this, message.obj.toString());
                break;
            case Constants.GET_HOMELIST_SUCCESS:
                final List<HomeDetail> list = new Gson().fromJson(message.obj.toString(), new TypeToken<List<HomeDetail>>() {
                }.getType());
                if (list == null || list.size() == 0) {
                    //提示
                    new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText(getResources().getString(R.string.add_home_first))
                            .showCancelButton(false)
                            .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    addViewByDialog();
                                    sweetAlertDialog.cancel();
                                }
                            }).show();
                    break;
                }
                final HomeAdapter mHAdapter = new HomeAdapter(this, list);
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.dialog_homelist, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogRqImage).setView(layout).show();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCanceledOnTouchOutside(true);
                ListView homeListView = layout.findViewById(R.id.home_listview);
                ViewGroup.LayoutParams lp = homeListView.getLayoutParams();
                lp.width = width * 2 / 3;
                homeListView.setLayoutParams(lp);
                homeListView.setAdapter(mHAdapter);
                homeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        ConfigUtil.setHomeID(list.get(i).getHome_id());
                        ConfigUtil.setHomeName(list.get(i).getHome_name());
                        Constants.HomeID = list.get(i).getHome_id();
                        userHome.setText(list.get(i).getHome_name());
                        mHttp.getHomeSwitchBtn(list.get(i).getHome_id(), mHander);
                        mHttp.getSenceList(list.get(i).getHome_id(), mHander);
                        // 擦除老的mqtt，启动MQTT服务器
                        if (mqttService!=null){
                            stopService(mqttService);
                            mqttService = null;
                        }
                        mqttService = new Intent(MainActivity.this, MQTTService.class);
                        startService(mqttService);

                        alertDialog.dismiss();
                    }
                });

                break;
            case Constants.GET_HOMELIST_FALSE:

                break;
            case Constants.GET_SENCE_LIST_SUCCESS:
                List<SenceDetail> senceList = new Gson().fromJson(message.obj.toString(), new TypeToken<List<SenceDetail>>() {
                }.getType());
                mSences.clear();
                mSences.addAll(senceList);
                mSenceAdapter.notifyDataSetChanged();
                break;
            case Constants.GET_SENCE_LIST_FALSE:
                DialogUtil.createEmptyMsgDialog(this, message.obj.toString());
                break;
            case Constants.CONTROL_SWITCH_SUCCESS:
                //TODO
                break;
            case Constants.CONTROL_SWITCH_FALSE:
                //如果开关控制失败，则重新请求数据，刷新数据
                mHttp.getHomeSwitchBtn(Constants.HomeID, mHander);
                break;

            case Constants.ADD_HOME_SUCCESS:
                Toast.makeText(this, R.string.add_success, Toast.LENGTH_SHORT).show();
                mHttp.getHomelist(Constants.UserID, mHander);
                break;
            case Constants.ADD_HOME_FALSE:
                Toast.makeText(this, R.string.add_false, Toast.LENGTH_SHORT).show();
                break;
            case Constants.GET_SERVICE_APP_VERSION_SUCCESS:
                try {
                    JSONObject object = new JSONObject(message.obj.toString());
                    String versionCode = object.getString("file_version");
                    final String url = object.getString("file_address");
                    String size = object.getString("file_size");
                    final int versionApk = Integer.parseInt(ConfigUtil.getVersion());
                    if (versionApk < Double.valueOf(versionCode)) {
                        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText(getResources().getString(R.string.set_appversion))
                                .setConfirmButton(R.string.update_app_now, new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Intent service = new Intent();
                                        service.setClass(MainActivity.this, UpdateService.class);
                                        service.putExtra("url", url);
                                        service.putExtra("isMain", true);
                                        startService(service);
                                        sweetAlertDialog.cancel();
                                    }
                                })
                                .setCancelButton(R.string.update_app_later, null)
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.GET_SERVICE_APP_VERSION_FALSE:
//                DialogUtil.createEmptyMsgDialog(this,R.string.cannot_get_app_version);
                break;
            case Constants.GET_USERINFO_SUCCESS:
                UserInfoDetail user = new Gson().fromJson(message.obj.toString(),UserInfoDetail.class);
                if (user.getHead_image_url()!=null){
                    Glide.with(this).load(Constants.URL+user.getHead_image_url()).into(personHeader);
                }
                break;
            case Constants.GET_USERINFO_FALSE:

                break;
        }
        return false;
    }

    /**
     * 侧滑动作
     */
    private void showDrawerLayout() {
        if (!drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.openDrawer(Gravity.LEFT);
        } else {
            drawer.closeDrawer(Gravity.LEFT);
        }
    }

    /**
     * 刷新总开关
     */
    private void refreshTotleBtn() {
        //总开关状态变更
        boolean b = false;
        if (mRooms != null) {
            for (int i = 0; i < mRooms.size(); i++) {
                if (BtnStatusUtil.getRoomBtnStatus(mRooms.get(i).getSwitch_list())) {
                    b = true;
                    break;
                }
            }
        }
        switchButton.setChecked(b);
        if (b) {
            switchButton.setBackgroundResource(R.mipmap.switch_btn_on);
        } else {
            switchButton.setBackgroundResource(R.mipmap.switch_btn_off);
        }

    }

    /**
     * 刷新roomAdapter列表
     */
    private void refreshAdapter() {
        mRoomAdapter.clearDatas();
        mRoomAdapter.addDatas(mRooms);
//        roomList.refresh();
        mRoomAdapter.notifyDataSetChanged();
    }

    /**
     * 刷新场景模式列表
     */
    private void refreshSence() {

    }

    /**
     * 刷新刷新rooms 列表内容
     */
    private void refreshRooms() {
        mRooms = ConfigUtil.getRoomListData();
        if (mRooms != null) {
            for (int i = 0; i < mRooms.size(); i++) {
                if (mRooms.get(i).getRoom_id().equals("00000000000000000000000000000000")) {
                    mRooms.remove(i);
                }
            }
        }
    }

    //接收回调广播
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.BRODCASE_MAIN_REFRESH)) {
                refreshRooms();
                refreshAdapter();
                refreshTotleBtn();
            } else if (action.equals(Constants.BRODCASE_TOTALBTN_REFRESH)) {
                refreshRooms();
                refreshTotleBtn();
            } else if (action.equals(Constants.BRODCASE_MAIN_REFRESH_ALL_DATA)) {
                mHttp.getHomeSwitchBtn(Constants.HomeID, mHander);
                mHttp.getSenceList(Constants.HomeID, mHander);
            } else if (action.equals(Constants.BRODCASE_REFRESH_AFTERDB)) {
                refreshRooms();
                refreshAdapter();
                refreshTotleBtn();
            }
        }
    };
    //记录用户首次点击返回键的时间·
    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - firstTime > 1000) {
                Toast.makeText(MainActivity.this, R.string.click_double_back, Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void addViewByDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_addroom, null);
        View titleView = inflater.inflate(R.layout.dialog_add_home_title, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.AlertDialog).setCustomTitle(titleView).setView(layout).show();
        final EditText et = layout.findViewById(R.id.etname);
        EditLimitUtil.setEditLimit(et, 20, this);
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
                String homeName = et.getText().toString();
                if (TextUtils.isEmpty(homeName)) {
                    Toast.makeText(MainActivity.this, R.string.sure, Toast.LENGTH_SHORT).show();
                } else {
                    mHttp.addHome(homeName, "", mHander);
                    alertDialog.dismiss();
                }
            }
        });
    }

    private static TextView proText;
    private static ProgressBar progress;
    private static AlertDialog progressDialog;

    public static void startUpdatePropress() {
        View view = View.inflate(activity, R.layout.layout_progress, null);
        proText = view.findViewById(R.id.progress_text);
        progress = view.findViewById(R.id.progress);
        progressDialog = new AlertDialog.Builder(activity, R.style.AlertDialogRqImage).setView(view).show();
        proText.setText(activity.getResources().getString(R.string.app_loading) + ": 0%");
    }

    public static void updateUpdatePropress(int proValue, boolean b) {
        if (b) {
            progress.setProgress(proValue);
            proText.setText(activity.getResources().getString(R.string.app_loading) + ": " + proValue + "%");
            if (proValue >= 100) {
                progressDialog.dismiss();
            }
        } else {
            proText.setText(activity.getResources().getString(R.string.loading_failed));
//            Toast.makeText(activity,R.string.loading_failed,Toast.LENGTH_LONG).show();
        }
    }


}
