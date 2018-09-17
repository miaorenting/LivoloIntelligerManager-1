package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.service.UpdateService;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.view.ActionSheetDialog;

/**
 * Created by mayn on 2018/4/16.
 */

public class SetManagerActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.updata_version)
    TextView updataVersion;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.update_pwd)
    TextView updatePwd;
//    @BindView(R.id.update_phone)
//    TextView updatePhone;
    @BindView(R.id.about)
    TextView about;
    @BindView(R.id.delete_user)
    TextView deleteUser;

    private Handler mHandler;
    private HttpTools mHttp;
    public static Activity activity;
    private SweetAlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.midblue);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        mHttp = new HttpTools();
        activity = this;
        initLayout();
    }

    private void initLayout() {
        topTitle.setText(R.string.set);
        backBtn.setOnClickListener(this);
        updataVersion.setOnClickListener(this);
        location.setOnClickListener(this);
        about.setOnClickListener(this);
        updatePwd.setOnClickListener(this);
//        updatePhone.setOnClickListener(this);
        deleteUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.updata_version:
                new HttpTools().getServiceAppVersion(mHandler);
                break;
            case R.id.location:
                ActionSheetDialog actionSheetDialog = new ActionSheetDialog(this).builder().setTitle("地区选择")
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(true);
                String[] array = getResources().getStringArray(R.array.areas);
                for (int i = 0; i < array.length; i++) {
                    actionSheetDialog.addSheetItem(array[i], ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                        @Override
                        public void onClick(int which) {
//                            mHttp.deleteUser(mHandler);
                        }
                    });
                }
                actionSheetDialog.show();
                break;
            case R.id.update_pwd:
                Intent intent = new Intent();
                intent.setClass(this, ChangePasswordActivity.class);
                startActivity(intent);

                break;
            case R.id.delete_user:
                dialog = new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getResources().getString(R.string.delete_user))
                        .setContentText(getResources().getString(R.string.delete_user_warn))
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                mHttp.deleteUser(mHandler);
                            }
                        })
                        .setCancelButton(R.string.cancel,null);
                dialog.show();

                break;
            case R.id.about:
                Intent intentAbout = new Intent();
                intentAbout.setClass(this, AboutActivity.class);
                startActivity(intentAbout);
                break;
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case Constants.GET_SERVICE_APP_VERSION_SUCCESS:
                try {
                    JSONObject object = new JSONObject(message.obj.toString());
                    String versionCode = object.getString("file_version");
                    final String url = object.getString("file_address");
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
                                        service.setClass(SetManagerActivity.this, UpdateService.class);
                                        service.putExtra("url", url);
                                        service.putExtra("isMain", false);
                                        startService(service);
                                        sweetAlertDialog.cancel();
                                    }
                                })
                                .setCancelButton(R.string.update_app_later, null)
                                .show();
                    } else {
                        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText(getResources().getString(R.string.app_is_new))
                                .setConfirmButton(R.string.sure, null)
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.GET_SERVICE_APP_VERSION_FALSE:
                DialogUtil.createEmptyMsgDialog(this, R.string.cannot_get_app_version);
                break;
            case Constants.DELETE_USER_SUCCESS:
                dialog.setTitleText(getResources().getString(R.string.delete_user_success))
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                dialog.cancel();
                                if (MainActivity.getInstance()!=null){
                                    MainActivity.getInstance().finish();
                                }
                                Intent intent = new Intent(SetManagerActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .showCancelButton(false)
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                break;
        }
        return false;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
