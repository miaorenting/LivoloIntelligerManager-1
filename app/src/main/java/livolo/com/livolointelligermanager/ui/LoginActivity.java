package livolo.com.livolointelligermanager.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.service.UpdateService;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.EditLimitUtil;
import livolo.com.livolointelligermanager.util.MD5Tool;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.view.MultiShapeView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mayn on 2018/4/2.
 */

public class LoginActivity extends BaseActivity implements Handler.Callback, View.OnClickListener {

    private static int BASIC_PERMISSION_REQUEST_CODE = 0x00099;

    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.register)
    TextView register;
    @BindView(R.id.forget_pwd)
    TextView forgetPwd;
    @BindView(R.id.header)
    MultiShapeView header;

    private Handler mHandler;
    private SweetAlertDialog dialog;
    private HttpTools mHttp;
    private static Activity activity;
    /**
     * 管理权限问题
     */
    private final String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.INTERNET,
            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            Manifest.permission.REQUEST_INSTALL_PACKAGES
    };


    /**
     * 请求权限
     *
     * @param permissions 请求的权限
     * @param requestCode 请求权限的请求码
     */
    public void requestPermission(String[] permissions, int requestCode) {
        this.BASIC_PERMISSION_REQUEST_CODE = requestCode;
        if (checkPermissions(permissions)) {
            permissionSuccess(BASIC_PERMISSION_REQUEST_CODE);
        } else {
            List<String> needPermissions = getDeniedPermissions(permissions);
            ActivityCompat.requestPermissions(this, needPermissions.toArray(new String[needPermissions.size()]), BASIC_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * 检测所有的权限是否都已授权
     * @param permissions
     * @return
     */
    private boolean checkPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     */
    private List<String> getDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                needRequestPermissionList.add(permission);
            }
        }
        return needRequestPermissionList;
    }


    /**
     * 系统请求权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode ==  BASIC_PERMISSION_REQUEST_CODE) {
            if (verifyPermissions(grantResults)) {
                permissionSuccess(BASIC_PERMISSION_REQUEST_CODE);
            } else {
                permissionFail(BASIC_PERMISSION_REQUEST_CODE);
//                showTipsDialog();
            }
        }
    }

    /**
     * 确认所有的权限是否都已授权
     *
     * @param grantResults
     * @return
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 启动当前应用设置页面
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    /**
     * 获取权限成功
     *
     * @param requestCode
     */
    public void permissionSuccess(int requestCode) {
        Log.e("---权限获取---", "获取权限成功=" + requestCode);

    }

    /**
     * 权限获取失败
     * @param requestCode
     */
    public void permissionFail(int requestCode) {
        Log.e("----权限获取---", "获取权限失败=" + requestCode);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        activity = this;
        if (getLanguage().equals("zh")) {
            Constants.URL = "http://cn.appnew.livolo.com:8080";
        }else{
            Constants.URL = "http://eu.appnew.livolo.com:8080";
        }
        Log.e("----url----",Constants.URL);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.login_bg);
        //检查权限
//        checkPermission();
        requestPermission(permissions,BASIC_PERMISSION_REQUEST_CODE);
        mHttp = new HttpTools();
        mHandler = new Handler(this);
        register.setOnClickListener(this);
        forgetPwd.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        if (username.getText().toString().equals(ConfigUtil.getUserName())) {
            Glide.with(this).load(Constants.URL + ConfigUtil.getHeaderStr()).into(header);
        } else {
            Glide.with(this).load(R.mipmap.header_black).into(header);
        }
//        mHttp.getServiceAppVersion(mHandler);//获取版本号
        setEditTextLimit();
    }

    private void setEditTextLimit(){
        EditLimitUtil.setEditLimit(username,20,this);
        EditLimitUtil.setEditLimit(password,20,this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        username.setText(ConfigUtil.getUserName());
        mHttp.getServiceAppVersion(mHandler);//获取版本号
    }

    private void getLoginCode() {
        String userName = username.getText().toString().trim();
        String pwd = password.getText().toString().trim();
        if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(userName)) {
            mHttp.getLoginCode(userName, mHandler);
        } else {
            DialogUtil.createEmptyMsgDialog(this, R.string.login_warn);
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case Constants.GET_IP_AND_PORT_SUCCESS:
                try {
                    JSONObject object = new JSONObject(message.obj.toString());
                    String ip = object.getString("ip");
                    String port = object.getString("port");
                    Constants.URL = "http://"+ip+":"+port;
                    Constants.mqttHost = "tcp://"+ip+":1883";
                    getLoginCode();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.GET_IP_AND_PORT_FALSE:

                DialogUtil.createEmptyMsgDialog(this,R.string.failed_connect_service);
                break;
            case Constants.GET_LOGIN_CODE_SUCCESS:
                try {
                    JSONObject json = new JSONObject(message.obj.toString());
                    String userName = username.getText().toString().trim();
                    String pwd = password.getText().toString().trim();
                    ConfigUtil.setUserName(userName);
                    mHttp.login(userName, MD5Tool.getMD5(pwd) + json.get("verify_code"), mHandler);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.GET_LOGIN_CODE_FALSE:
                dialog.setTitleText(message.obj.toString())
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                dialog.cancel();
                            }
                        })
                        .setContentText(message.obj.toString())
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                break;
            case Constants.LOGIN_SUCCESS:
                try {
                    JSONObject json = new JSONObject(message.obj.toString());
                    Constants.UserID = json.get("user_id").toString();
                    Constants.Token = json.get("token").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                dialog.cancel();
                finish();
                break;
            case Constants.LOGIN_FALSE:
                dialog.setTitleText(getResources().getString(R.string.error))
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                dialog.cancel();
                            }
                        })
                        .setContentText(message.obj.toString())
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                break;
            case Constants.GET_SERVICE_APP_VERSION_SUCCESS:
                try {
                    JSONObject object = new JSONObject(message.obj.toString());
                    String versionCode = object.getString("file_version");
                    final String appUrl = object.getString("file_address");
                    String size = object.getString("file_size");
                    String updateInfo = object.getString("update_content");
                    final int versionApk = Integer.parseInt(ConfigUtil.getVersion());
                    if (versionApk < Double.valueOf(versionCode)) {
                        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText(getResources().getString(R.string.update_appversion))
                                .setContentText(updateInfo)
                                .setConfirmButton(R.string.update_app_now, new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Intent service = new Intent();
                                        service.setClass(LoginActivity.this, UpdateService.class);
                                        service.putExtra("url", appUrl);
                                        service.putExtra("isMain",true);
                                        startService(service);
                                        sweetAlertDialog.cancel();
                                    }
                                })
                                .setCancelButton(R.string.update_app_later,null)
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.GET_SERVICE_APP_VERSION_FALSE:
                DialogUtil.createEmptyMsgDialog(this,message.obj.toString());
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                Constants.URL = "https://appnew.livolo.com/";
                dialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText(getResources().getString(R.string.logining));
                dialog.show();
                mHttp.getUserIPAndPort(username.getText().toString(),mHandler);
                break;
            case R.id.register:
                Intent registerIntent = new Intent();
                if (getLanguage().equals("zh")) {
                    registerIntent.setClass(this, RegisterActivity.class);
                } else {
                    registerIntent.setClass(this, RegisterForForeignActivity.class);
                }
                startActivity(registerIntent);
                break;
            case R.id.forget_pwd:
                Intent forgetIntent = new Intent();
                if (getLanguage().equals("zh")) {
                    forgetIntent = new Intent(this, ChangePasswordByCodeActivity.class);
                }else{
                    forgetIntent = new Intent(this, ChangPasswordByEmailActivity.class);
                }
                startActivity(forgetIntent);
                break;
        }
    }

    public String getLanguage() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        Log.e("--------------language", locale.getLanguage());
        return locale.getLanguage();
    }
    private static TextView proText;
    private static ProgressBar progress;
    private static AlertDialog progressDialog;

    public static void startUpdatePropress(){
        View view = View .inflate(activity, R.layout.layout_progress, null);
        proText = view.findViewById(R.id.progress_text);
        progress = view.findViewById(R.id.progress);
        progressDialog = new AlertDialog.Builder(activity, R.style.AlertDialogRqImage).setView(view).show();
        proText.setText(activity.getResources().getString(R.string.app_loading)+": 0%");
    }

    public static void updateUpdatePropress(int proValue,boolean b){
        if (b){
            progress.setProgress(proValue);
            proText.setText(activity.getResources().getString(R.string.app_loading)+": "+proValue+"%");
            if (proValue>=100){
                progressDialog.dismiss();
            }
        }else{
            proText.setText(activity.getResources().getString(R.string.loading_failed));
//            Toast.makeText(activity,R.string.loading_failed,Toast.LENGTH_LONG).show();
        }
    }
}
