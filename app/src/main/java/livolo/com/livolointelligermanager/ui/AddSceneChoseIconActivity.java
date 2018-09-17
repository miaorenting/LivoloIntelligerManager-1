package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
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
import livolo.com.livolointelligermanager.mode.SenceDetail;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.EditLimitUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;

/**
 * Created by mayn on 2018/6/13.
 */

public class AddSceneChoseIconActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_right)
    TextView topRight;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.sence_icon)
    ImageView senceIcon;
    @BindView(R.id.sence_name)
    EditText senceName;
    @BindView(R.id.next_btn)
    Button nextBtn;
    @BindView(R.id.gridView_icon)
    GridView gridViewIcon;

    private int actionType = 0;//0表示正常添加情景，1表示修改情景时修改的名字和图标index
    private static int[] icons = {R.mipmap.sence_blue_movie, R.mipmap.sence_blue_outhome, R.mipmap.sence_blue_party, R.mipmap.sence_blue_sleep};
    private static int[] names = {R.string.scene_0, R.string.scene_1, R.string.scene_2, R.string.scene_3};
    private int curIcon = 0;
    private ChoseRoomIconAdapter adapter;
    private SweetAlertDialog mDialog;
    private HttpTools mHttp;
    private Handler mHandler;

    private static Activity activity;

    public static Activity getInstance() {
        return activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scene_chose_icon);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.midblue);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        mHttp = new HttpTools();
        activity = this;
        actionType = getIntent().getIntExtra("singleadd", 0);
        if (actionType == 1) {
            curIcon = getIntent().getIntExtra("sceneIcon", 0);
            String name = getIntent().getStringExtra("sceneName");
            if (!TextUtils.isEmpty(name)) {
                senceName.setText(name);
            }
            topTitle.setText(R.string.sence_manager);
        } else {
            topTitle.setText(R.string.add_sence);
            senceName.setText(names[curIcon]);
        }
        //初始化页面的图标和名字
        Glide.with(this).load(icons[curIcon]).into(senceIcon);
        adapter = new ChoseRoomIconAdapter(this, icons, names);
        gridViewIcon.setNumColumns(4);
        gridViewIcon.setAdapter(adapter);
        gridViewIcon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                curIcon = i;
                senceName.setText(names[i]);
                Glide.with(AddSceneChoseIconActivity.this).load(icons[i]).into(senceIcon);
//                mDialog = new SweetAlertDialog(AddSceneChoseIconActivity.this, SweetAlertDialog.PROGRESS_TYPE)
//                        .setContentText(getResources().getString(R.string.wait_update));
//                mDialog.setCanceledOnTouchOutside(false);
//                mDialog.show();
                mHttp.getdefaultName(Constants.HomeID, 4, getResources().getString(names[i]), mHandler);
            }
        });
        nextBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        EditLimitUtil.setEditLimit(senceName,20,this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.next_btn:
                if (TextUtils.isEmpty(senceName.getText().toString())) {
                    DialogUtil.createEmptyMsgDialog(this, R.string.input_scene_name);
                } else {
                    if (EditLimitUtil.isLimit(senceName,20,this)){
                        if (actionType == 0) {
                            SenceDetail detail = new SenceDetail();
                            detail.setPicture_index(curIcon);
                            detail.setScene_name(senceName.getText().toString());
                            Intent intent = new Intent();
                            intent.setClass(this, AddSceneActivity.class);
                            intent.putExtra("sence", detail);
                            startActivity(intent);
                        } else {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("sceneIcon", curIcon);
                            resultIntent.putExtra("sceneName", senceName.getText().toString());
                            setResult(1, resultIntent);
                            finish();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case Constants.GET_DEFAULT_NAME_SUCCESS:
                if (mDialog!=null){
                    mDialog.cancel();
                }
                try {
                    JSONObject obj = new JSONObject(message.obj.toString());
                    String name = obj.getString("default_name");
                    senceName.setText(name);
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
