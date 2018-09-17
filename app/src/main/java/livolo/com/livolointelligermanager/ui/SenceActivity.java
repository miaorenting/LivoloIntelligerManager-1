package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;
import com.zcolin.gui.zrecyclerview.ZRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.adaper.TextRecycleAdapter;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.ButtonDetail;
import livolo.com.livolointelligermanager.mode.SenceDetail;
import livolo.com.livolointelligermanager.util.StatusBarUtils;

/**
 * Created by mayn on 2018/4/6.
 */

public class SenceActivity extends BaseActivity implements View.OnClickListener,Handler.Callback{

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.top_right)
    TextView topRight;
    @BindView(R.id.sence_icon)
    ImageView senceIcon;
    @BindView(R.id.sence_recycleview)
    ZRecyclerView senceRecycleview;

    private TextRecycleAdapter mAdapter;
    private List<ButtonDetail> list =  new ArrayList<>();
    private SenceDetail senceDetail;
    private HttpTools mHttp;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sence);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.midblue);
        ButterKnife.bind(this);
        senceDetail = (SenceDetail) getIntent().getSerializableExtra("sence");
        mHttp = new HttpTools();
        mHandler = new Handler(this);
        initlayout();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (senceDetail!=null){
            mHttp.getScene(senceDetail.getScene_id(),mHandler);
        }
    }

    private void initlayout() {
        backBtn.setOnClickListener(this);
        topRight.setOnClickListener(this);
        topRight.setText(R.string.update);
        if (senceDetail!=null && !TextUtils.isEmpty(senceDetail.getScene_name())){
            topTitle.setText(senceDetail.getScene_name());
            if (!TextUtils.isEmpty(senceDetail.getPicture_url())){
                Glide.with(this).load(Constants.URL+senceDetail.getPicture_url()).into(senceIcon);
            }else{
                Glide.with(this).load(ConfigUtil.getSenceBlueIcon(senceDetail.getPicture_index())).into(senceIcon);
            }
        }
        mAdapter = new TextRecycleAdapter();
        mAdapter.setDatas(list);
        senceRecycleview.setAdapter(mAdapter);
        senceRecycleview.setIsLoadMoreEnabled(false);
        senceRecycleview.setIsRefreshEnabled(false);
        senceRecycleview.addDefaultItemDecoration();
        senceRecycleview.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener<String>() {
            @Override
            public boolean onItemLongClick(View covertView, final int position, String data) {
                new SweetAlertDialog(SenceActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getResources().getString(R.string.be_need_delete))
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                //TODO 删除操作

                                sweetAlertDialog.cancel();
                            }
                        })
                        .setCancelButton(R.string.cancel, null)
                        .show();
                return false;
            }
        });
//        mHttp.getScene(senceDetail.getScene_id(),mHandler);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.top_right:
                Intent intent = new Intent();
                intent.setClass(this,AddSceneActivity.class);
                intent.putExtra("sence",senceDetail);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case Constants.GET_SCENE_SUCCESS:
                senceDetail = new Gson().fromJson(message.obj.toString(),SenceDetail.class);
                topTitle.setText(senceDetail.getScene_name());
                if (!TextUtils.isEmpty(senceDetail.getPicture_url())){
                    Glide.with(this).load(Constants.URL+senceDetail.getPicture_url()).into(senceIcon);
                }else{
                    Glide.with(this).load(ConfigUtil.getSenceBlueIcon(senceDetail.getPicture_index())).into(senceIcon);
                }
                if (senceDetail.getButton_list()!=null){
                    mAdapter.clearDatas();
                    mAdapter.addDatas(senceDetail.getButton_list());
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case Constants.GET_SCENE_FALSE:
                Toast.makeText(this, getResources().getString(R.string.get_scene_error), Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }
}
