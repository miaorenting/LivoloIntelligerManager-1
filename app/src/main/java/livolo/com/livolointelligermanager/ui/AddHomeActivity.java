package livolo.com.livolointelligermanager.ui;

import android.graphics.Bitmap;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.HomeDetail;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.EditLimitUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;

/**
 * Created by mayn on 2018/5/25.
 */

public class AddHomeActivity extends BaseActivity implements Handler.Callback {

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.top_right)
    TextView topRight;
    @BindView(R.id.home_name)
    EditText homeName;
    @BindView(R.id.home_address)
    EditText homeAddress;
    private HomeDetail mHomeDetail;

    private HttpTools mHttp;
    private Handler mHandler;
    private SweetAlertDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addhome);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.midblue);
        ButterKnife.bind(this);
        mHomeDetail = (HomeDetail) getIntent().getSerializableExtra("home");
        mHttp = new HttpTools();
        mHandler = new Handler(this);

        if (mHomeDetail == null) {
            topTitle.setText(R.string.addhome_by_create);
        } else {
            topTitle.setText(R.string.set_home);
            homeName.setText(mHomeDetail.getHome_name());
            homeAddress.setText(mHomeDetail.getHome_address());
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        topRight.setText(R.string.commit);
        topRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(homeName.getText().toString())) {
                    DialogUtil.createEmptyMsgDialog(AddHomeActivity.this, R.string.input_homename);
                } else {
                    if (EditLimitUtil.isLimit(homeName,20,AddHomeActivity.this)
                            && EditLimitUtil.isLimit(homeAddress,200,AddHomeActivity.this)){
                        mDialog = new SweetAlertDialog(AddHomeActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText(getResources().getString(R.string.be_need_commit))
                                .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        mDialog.setTitleText(getResources().getString(R.string.committing))
                                                .showCancelButton(false)
                                                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                        if (mHomeDetail == null) {
                                            mHttp.addHome(homeName.getText().toString(), homeAddress.getText().toString(), mHandler);
                                        } else {
                                            mHttp.updateHome(mHomeDetail.getHome_id(), homeName.getText().toString(), homeAddress.getText().toString(), mHandler);
                                            if (Constants.HomeID.equals(mHomeDetail.getHome_id())){
                                                ConfigUtil.setHomeName(homeName.getText().toString());
                                            }
                                        }

                                    }
                                })
                                .setCancelButton(R.string.cancel, null);
                        mDialog.show();
                    }
                }
            }
        });
        EditLimitUtil.setEditLimit(homeName,20,this);
        EditLimitUtil.setEditLimit(homeAddress,200,this);
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case Constants.ADD_HOME_SUCCESS:
                String strcode = "R.string"+"add_success";
                mDialog.setTitleText(getResources().getString(R.string.add_success))
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                mDialog.cancel();
                                finish();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                break;
            case Constants.ADD_HOME_FALSE:
                if (mDialog != null) {
                    mDialog.cancel();
                }
                DialogUtil.createEorreMsgDialog(this, message.obj.toString());
                break;
            case Constants.UPDATE_HOME_SUCCESS:
                mDialog.setTitleText(getResources().getString(R.string.update_success))
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                ConfigUtil.setHomeName(homeName.getText().toString());
                                mDialog.cancel();
                                finish();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                break;
            case Constants.UPDATE_HOME_FALSE:
                if (mDialog != null) {
                    mDialog.cancel();
                }
                DialogUtil.createEorreMsgDialog(this, message.obj.toString());
                break;
        }
        return false;
    }
}
