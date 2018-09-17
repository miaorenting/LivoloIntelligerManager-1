package livolo.com.livolointelligermanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;
import com.zcolin.gui.zrecyclerview.ZRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import livolo.com.livolointelligermanager.receiver.BrodcaseTool;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.EditLimitUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;

/**
 * Created by mayn on 2018/5/12.
 */

public class AddSceneActivity extends BaseActivity implements View.OnClickListener,Handler.Callback{

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.top_right)
    TextView topRight;
    @BindView(R.id.sence_icon)
    ImageView senceIcon;
    @BindView(R.id.sence_name)
    EditText senceName;
    @BindView(R.id.sence_recycleview)
    ZRecyclerView senceRecycleview;

    private TextRecycleAdapter mAdapter;
    private List<ButtonDetail> list = new ArrayList<>();
    private SenceDetail senceDetail;
    private HttpTools mHttp;
    private Handler mHandler;
    private SweetAlertDialog dialog;

    private int[] icons = {R.mipmap.sence_blue_movie,R.mipmap.sence_blue_outhome,R.mipmap.sence_blue_party,R.mipmap.sence_blue_sleep};
    private int curIcon = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scene);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.midblue);
        ButterKnife.bind(this);
        senceDetail = (SenceDetail) getIntent().getSerializableExtra("sence");
        mHttp = new HttpTools();
        mHandler = new Handler(this);
        initlayout();
    }

    private void initlayout() {
        backBtn.setOnClickListener(this);
        topRight.setText(R.string.commit);
        topRight.setOnClickListener(this);
        if (TextUtils.isEmpty(senceDetail.getScene_id())) {
            topTitle.setText(R.string.add_sence);
            senceName.setText(senceDetail.getScene_name());
        } else {
            senceName.setOnClickListener(this);
            senceIcon.setOnClickListener(this);
            topTitle.setText(R.string.sence_manager);
            List<ButtonDetail> tempList = senceDetail.getButton_list();
            if (tempList!=null && tempList.size()>0){
                list.addAll(tempList);
            }
        }
        curIcon = senceDetail.getPicture_index();
        senceName.setText(senceDetail.getScene_name());
        Glide.with(this).load(icons[curIcon]).into(senceIcon);
        mAdapter = new TextRecycleAdapter();
        addLastAdd();
        mAdapter.setDatas(list);
        senceRecycleview.setAdapter(mAdapter);
        senceRecycleview.setIsLoadMoreEnabled(false);
        senceRecycleview.setIsRefreshEnabled(false);
        senceRecycleview.addDefaultItemDecoration();
        senceRecycleview.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<ButtonDetail>() {
            @Override
            public void onItemClick(View covertView, int position, ButtonDetail data) {
                if (position == (list.size() - 1)) {
                    Intent intent = new Intent();
                    intent.setClass(AddSceneActivity.this, SwitchManagerActivity.class);
                    intent.putExtra("isManager", false);
                    startActivityForResult(intent, Constants.GET_RESULT_FOR_SWITCH);
                }
            }
        });
        senceRecycleview.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener<ButtonDetail>() {
            @Override
            public boolean onItemLongClick(View covertView, final int position, ButtonDetail data) {
                new SweetAlertDialog(AddSceneActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getResources().getString(R.string.be_need_delete))
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                deleteButton(position);
                                sweetAlertDialog.cancel();
                            }
                        })
                        .setCancelButton(R.string.cancel, null)
                        .show();
                return false;
            }
        });
        EditLimitUtil.setEditLimit(senceName,20,this);

    }

    public void deleteButton(int position) {
        list.remove(position);
        mAdapter.clearDatas();
        mAdapter.addDatas(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data==null){
            return;
        }
        if (requestCode == Constants.GET_RESULT_FOR_SWITCH) {
            list.remove(list.size() - 1);
            list.add(((ButtonDetail) data.getSerializableExtra("btn")));
            addLastAdd();
            mAdapter.clearDatas();
            mAdapter.addDatas(list);
            mAdapter.notifyDataSetChanged();
        }else if (resultCode ==1){
            String name = data.getStringExtra("sceneName");
            int iconNum = data.getIntExtra("sceneIcon",0);
            senceName.setText(name);
            curIcon = iconNum;
            Glide.with(this).load(ConfigUtil.getSenceBlueIcon(curIcon)).into(senceIcon);
        }
    }

    private void addLastAdd(){
        //最后一行添加“添加 + ”
        ButtonDetail addTemp = new ButtonDetail();
        addTemp.setButton_name(" + ");
        list.add(addTemp);
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case Constants.ADD_SCENE_SUCCESS:
                setSuccessDialog(R.string.add_scene_success);
                BrodcaseTool.sendMainDataRefresh(this);
                break;
            case Constants.ADD_SCENE_FALSE:
                if (dialog!=null){
                    dialog.cancel();
                }
                DialogUtil.createEorreMsgDialog(this,message.obj.toString());
                break;
            case Constants.UPDATE_SCENE_SUCCESS://提价修改请求的成功与失败
                setSuccessDialog(R.string.update_scene_success);
                break;
            case Constants.UPDATE_SCENE_FALSE:
                if (dialog!=null){
                    dialog.cancel();
                }
                DialogUtil.createEorreMsgDialog(this,message.obj.toString());
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.top_right:
                if (EditLimitUtil.isLimit(senceName,20,this)){
                    dialog = new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getResources().getString(R.string.be_need_commit))
                            .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    //提交添加请求
                                    JSONArray array = new JSONArray();
                                    if (list!=null){
                                        for (int i = 0;i<list.size()-1;i++){
                                            JSONObject obj = new JSONObject();
                                            try {
                                                obj.put("button_id",list.get(i).getButton_id());
                                                obj.put("button_status",list.get(i).getButton_status());
                                                array.put(obj);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    //提交修改或者添加请求
                                    if (TextUtils.isEmpty(senceDetail.getScene_id())){
                                        mHttp.addSence(Constants.HomeID,senceName.getText().toString().trim(),curIcon,array,"",mHandler);
                                    }else{
                                        //提交修改请求
                                        mHttp.updateScene(senceDetail.getScene_id(),senceName.getText().toString(),curIcon,array,"",mHandler);
                                    }
                                    dialog.setTitleText(getResources().getString(R.string.committing))
                                            .showCancelButton(false)
                                            .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                }
                            })
                            .setCancelButton(R.string.cancel, null);
                    dialog.show();
                }
                break;
            case R.id.sence_icon:
                changSceneIconAndName();
                break;
            case R.id.sence_name:
                changSceneIconAndName();
                break;
        }
    }

    private void changSceneIconAndName(){
        Intent intent = new Intent();
        intent.setClass(this,AddSceneChoseIconActivity.class);
        intent.putExtra("sceneIcon",senceDetail.getPicture_index());
        intent.putExtra("sceneName",senceDetail.getScene_name());
        intent.putExtra("singleadd",1);
        startActivityForResult(intent,0);
    }

    private void setSuccessDialog(int title){
        dialog.setTitleText(getResources().getString(title))
                .setConfirmButton(getResources().getString(R.string.sure), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if (AddSceneChoseIconActivity.getInstance()!=null){
                            AddSceneChoseIconActivity.getInstance().finish();
                        }
                        finish();
                        dialog.cancel();
                    }
                })
                .showCancelButton(false)
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
    }

}
