package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.content.Intent;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.adaper.RoomAdapter;
import livolo.com.livolointelligermanager.adaper.SenceAdapter;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.RoomDetail;
import livolo.com.livolointelligermanager.mode.SenceDetail;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.view.ActionSheetDialog;

/**
 * Created by mayn on 2018/4/16.
 */

public class SenceManagerActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.top_right)
    TextView topRight;
    @BindView(R.id.room_listview)
    ListView senceListView;
    @BindView(R.id.cancel_btn)
    TextView cancelBtn;

    private SenceAdapter mAdapter;
    private List<SenceDetail> list = new ArrayList<>();
    private HttpTools mHttp;
    private Handler mHandler;
    private SweetAlertDialog dialog;
    private int deleteIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sence_manager);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.midblue);
        ButterKnife.bind(this);
        mHttp = new HttpTools();
        mHandler = new Handler(this);
        initLayout();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mHttp.getSenceList(Constants.HomeID, mHandler);
    }

    private void initLayout() {
        topRight.setText(R.string.add);
        topTitle.setText(R.string.sence_manager);
        backBtn.setOnClickListener(this);
        topRight.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        cancelBtn.setVisibility(View.GONE);
        //http 获取房间列表
        mAdapter = new SenceAdapter(this, list, false);
        senceListView.setAdapter(mAdapter);

        senceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.setClass(SenceManagerActivity.this, SenceActivity.class);
                intent.putExtra("sence", list.get(i));
                startActivity(intent);
            }
        });
        senceListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                deleteIndex = i;
                dialog = new SweetAlertDialog(SenceManagerActivity.this,SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getResources().getString(R.string.be_need_delete))
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                dialog.setTitleText(getResources().getString(R.string.committing))
                                        .showCancelButton(false)
                                        .showContentText(false)
                                        .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                mHttp.deleteScene(list.get(deleteIndex).getScene_id(),mHandler);
                            }
                        })
                        .setCancelButton(R.string.cancel,null);
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.top_right:
                Intent intent = new Intent();
//                intent.setClass(this, AddSceneActivity.class);
                intent.setClass(this, AddSceneChoseIconActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//
//        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case Constants.GET_SENCE_LIST_SUCCESS:
                Log.e("-------sence---------", message.obj.toString());
                List<SenceDetail> senceList = new Gson().fromJson(message.obj.toString(), new TypeToken<List<SenceDetail>>() {
                }.getType());
                list.clear();
                list.addAll(senceList);
                mAdapter.notifyDataSetChanged();
                break;
            case Constants.GET_SENCE_LIST_FALSE:
                DialogUtil.createEmptyMsgDialog(this, message.obj.toString());
                break;
            case Constants.DELETE_SCENE_SUCCESS:
                Log.e("-------------------","------------------------------------------");
                dialog.cancel();
                list.remove(deleteIndex);
                mAdapter.notifyDataSetChanged();
                break;
            case Constants.DELETE_SCENE_FALSE:
                dialog.setTitleText(getResources().getString(R.string.delete_false))
                        .setConfirmButton(R.string.sure,null)
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                break;
        }
        return false;
    }
}
