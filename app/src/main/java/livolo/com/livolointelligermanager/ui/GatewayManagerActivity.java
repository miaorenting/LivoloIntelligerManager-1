package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;
import com.zcolin.gui.zrecyclerview.ZRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.adaper.GatewayAdapter;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.GatewayDetail;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;

/**
 * Created by mayn on 2018/4/9.
 */

public class GatewayManagerActivity extends BaseActivity implements View.OnClickListener ,Handler.Callback{

    @BindView(R.id.gateways_view)
    ZRecyclerView gatewaysView;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.top_right)
    TextView topRight;

    private HttpTools mHttp;
    private Handler mHandler;
    private GatewayAdapter adapter;
    private List<GatewayDetail> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gatewaymanager);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.midblue);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        mHttp = new HttpTools();
        initLayout();
        }

    @Override
    protected void onResume() {
        super.onResume();
        mHttp.getGatewayList(Constants.HomeID,mHandler);
    }

    private void initLayout() {
        backBtn.setOnClickListener(this);
        topRight.setOnClickListener(this);
        topTitle.setText(R.string.gatewaymanager_title);
        topRight.setText(R.string.add);
        adapter = new GatewayAdapter(this);
        adapter.setDatas(list);
        gatewaysView.setAdapter(adapter);
        gatewaysView.setIsRefreshEnabled(false);
        gatewaysView.setNoMore(false);
        gatewaysView.addDefaultItemDecoration();
        gatewaysView.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener<GatewayDetail>() {
            @Override
            public boolean onItemLongClick(View covertView, final int position, GatewayDetail data) {
                new SweetAlertDialog(GatewayManagerActivity.this,SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getResources().getString(R.string.be_need_delete))
                        .setContentText(getResources().getString(R.string.delete_gateway_warn))
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                mHttp.deleteGateway(list.get(position).getGateway_id(),mHandler);
                                list.remove(position);
                                adapter.clearDatas();
                                adapter.addDatas(list);
                                adapter.notifyDataSetChanged();
                                sweetAlertDialog.cancel();
                            }
                        })
                        .setCancelButton(R.string.cancel, null).show();
                return false;
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
                intent.setClass(this, AddGatewayActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case Constants.GET_GATEWAY_LIST_SUCCESS:
                list = new Gson().fromJson(message.obj.toString(),new TypeToken<List<GatewayDetail>>(){}.getType());
                adapter.clearDatas();
                adapter.addDatas(list);
                adapter.notifyDataSetChanged();
                break;
            case Constants.GET_GATEWAY_LIST_FALSE:
                DialogUtil.createEmptyMsgDialog(this,message.obj.toString());
                break;
            case Constants.DELETE_GATEWAY_SUCCESS:
                mHttp.getGatewayList(Constants.HomeID,mHandler);
                break;
            case Constants.DELETE_GATEWAY_FALSE:
                DialogUtil.createEmptyMsgDialog(this,message.obj.toString());
                break;
        }
        return false;
    }
}
