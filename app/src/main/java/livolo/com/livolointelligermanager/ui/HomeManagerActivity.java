package livolo.com.livolointelligermanager.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.littlejie.circleprogress.utils.Constant;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.adaper.HomeManagerAdapter;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.HomeDetail;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.util.WindowManagerUtil;
import livolo.com.livolointelligermanager.view.ActionSheetDialog;

/**
 * Created by mayn on 2018/6/12.
 */

public class HomeManagerActivity extends BaseActivity implements Handler.Callback, View.OnClickListener {

    private static final int REQUEST_CODE = 1100;

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.top_right)
    TextView topRight;
    @BindView(R.id.listview)
    ListView listview;

    private Handler mHandler;
    private HttpTools mHttp;
    private List<HomeDetail> list = new ArrayList<>();
    private HomeManagerAdapter mAdapter;
    private SweetAlertDialog dialog;
    private int width = 0;
    private String unbindHomeId;
    private boolean needrefresh = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homemanager);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.midblue);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        mHttp = new HttpTools();
        width = WindowManagerUtil.getWindowWidth(this);
        setScanRQLayout();
        initLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needrefresh) {
            mHttp.getHomelist(Constants.UserID, mHandler);
        } else {
            needrefresh = true;
        }
    }

    private void initLayout() {
        backBtn.setOnClickListener(this);
        topTitle.setText(R.string.set_home);
        topRight.setText(R.string.add);
        topRight.setOnClickListener(this);
        mAdapter = new HomeManagerAdapter(this, list);
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new ActionSheetDialog(HomeManagerActivity.this).builder()
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem(getResources().getString(R.string.show_bind), ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Bitmap mBitmap = CodeUtils.createImage(list.get(i).getHome_id(), width * 3 / 5, width * 3 / 5, BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon));
                                View layout = LayoutInflater.from(HomeManagerActivity.this).inflate(R.layout.layout_rq_image, null);
                                ImageView image = layout.findViewById(R.id.rq_image);
                                image.setImageBitmap(mBitmap);
                                final AlertDialog alertDialog = new AlertDialog.Builder(HomeManagerActivity.this, R.style.AlertDialogRqImage).setView(layout).show();
                            }
                        })
                        .addSheetItem(getResources().getString(R.string.look_update), ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Intent intent = new Intent();
                                intent.setClass(HomeManagerActivity.this, AddHomeActivity.class);
                                intent.putExtra("home", list.get(i));
                                startActivity(intent);
                            }
                        })
                        .addSheetItem(getResources().getString(R.string.unbind), ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                dialog = new SweetAlertDialog(HomeManagerActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText(getResources().getString(R.string.need_unbind_home) + "?")
                                        .setConfirmButton(getResources().getString(R.string.sure), new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                dialog.showCancelButton(false)
                                                        .setTitleText(getResources().getString(R.string.committing))
                                                        .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                                if (ConfigUtil.getHomeID().equals(unbindHomeId)) {
                                                    ConfigUtil.setHomeID("");
                                                    ConfigUtil.setHomeName("");
                                                }
                                                unbindHomeId = list.get(i).getHome_id();
                                                mHttp.unbindHome(list.get(i).getHome_id(), Constants.UserID, mHandler);
                                                list.remove(i);
                                            }
                                        })
                                        .setCancelButton(R.string.cancel, null);
                                dialog.show();

                            }
                        })
                        .show();
            }
        });
    }

    /**  */
    public void setScanRQLayout() {
        /**
         * 执行扫面Fragment的初始化操作
         */
        CaptureFragment captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
    }

    @Override
    public boolean handleMessage(Message message) {
        if (dialog != null) {
            dialog.cancel();
        }
        switch (message.what) {
            case Constants.GET_HOMELIST_SUCCESS:
                final List<HomeDetail> tempList = new Gson().fromJson(message.obj.toString(), new TypeToken<List<HomeDetail>>() {
                }.getType());
                if (tempList != null) {
                    list.clear();
                    list.addAll(tempList);
                }
                mAdapter.notifyDataSetChanged();
                break;
            case Constants.GET_HOMELIST_FALSE:
                dialog.setTitleText(getResources().getString(R.string.get_homelist_failed))
                        .showCancelButton(false)
                        .setConfirmButton(R.string.sure, null)
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                break;
            case Constants.BIND_HOME_SUCCESS:
                mHttp.getHomelist(Constants.UserID, mHandler);
                break;
            case Constants.BIND_HOME_FALSE:
                dialog.setTitleText(getResources().getString(R.string.add_false))
                        .setConfirmButton(R.string.sure, null)
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                break;
            case Constants.UNBIND_HOME_SUCCESS:
                mHttp.getHomelist(Constants.UserID, mHandler);
                if (unbindHomeId.equals(Constants.HomeID)) {
                    Constants.HomeID = "";
                    ConfigUtil.setHomeID("");
                }
                if (list != null && list.size() > 0) {
                    Constants.HomeID = list.get(0).getHome_id();
                    ConfigUtil.setHomeID(list.get(0).getHome_id());
                }
                break;
            case Constants.UNBIND_HOME_FALSE:
                DialogUtil.createEmptyMsgDialog(this, R.string.unbind_failed);
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.top_right:
                new ActionSheetDialog(HomeManagerActivity.this).builder()
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem(getResources().getString(R.string.addhome_by_create), ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Intent intent = new Intent();
                                intent.setClass(HomeManagerActivity.this, AddHomeActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addSheetItem(getResources().getString(R.string.addhome_by_rq), ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Intent intent = new Intent(HomeManagerActivity.this, CaptureActivity.class);
                                startActivityForResult(intent, REQUEST_CODE);
                            }
                        })
                        .show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        needrefresh = false;
        /* 处理二维码扫描结果 */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    final String result = bundle.getString(CodeUtils.RESULT_STRING);
                    if (TextUtils.isEmpty(result)) {
                        Toast.makeText(this, R.string.rq_empty, Toast.LENGTH_SHORT).show();
                    } else {
                        dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText(getResources().getString(R.string.request_bind_home))
                                .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        dialog.setTitleText(getResources().getString(R.string.committing))
                                                .showCancelButton(false)
                                                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                        mHttp.bindHome(result, mHandler);
                                    }
                                })
                                .setCancelButton(R.string.cancel, null);
                        dialog.show();
                    }

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(this, R.string.scan_rq_failed, Toast.LENGTH_LONG).show();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            HomeManagerActivity.this.setResult(RESULT_OK, resultIntent);
            HomeManagerActivity.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            HomeManagerActivity.this.setResult(RESULT_OK, resultIntent);
            HomeManagerActivity.this.finish();
        }
    };
}
