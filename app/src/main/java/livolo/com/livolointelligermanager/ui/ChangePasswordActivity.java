package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.EditLimitUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;

/**
 * Created by mayn on 2018/4/16.
 */

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener,Handler.Callback{

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.top_right)
    TextView topRight;
    @BindView(R.id.old_password)
    EditText oldPassword;
    @BindView(R.id.new_password_1)
    EditText newPassword1;
    @BindView(R.id.new_password_2)
    EditText newPassword2;
    @BindView(R.id.commit_btn)
    Button commitBtn;

    private Handler mHandler;
    private SweetAlertDialog dialog;
    private HttpTools mHttp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_pwd);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.midblue);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        mHttp = new HttpTools();
        initLayout();

    }

    private void initLayout() {
        backBtn.setOnClickListener(this);
        topTitle.setText(R.string.password_change);
        commitBtn.setOnClickListener(this);
        EditLimitUtil.setEditLimit(oldPassword,20,this);
        EditLimitUtil.setEditLimit(newPassword1,20,this);
        EditLimitUtil.setEditLimit(newPassword2,20,this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                finish();

                break;
            case R.id.commit_btn:
                if (EditLimitUtil.isLimit(oldPassword,20,this)
                        && EditLimitUtil.isLimit(newPassword1,20,this)
                        && EditLimitUtil.isLimit(newPassword2,20,this)){
                    String old = oldPassword.getText().toString().trim();
                    String new1 = newPassword1.getText().toString().trim();
                    String new2 = newPassword2.getText().toString().trim();
                    if (TextUtils.isEmpty(old)){
                        DialogUtil.createEmptyMsgDialog(this,R.string.be_right_input_password);
                        return;
                    }
                    if (TextUtils.isEmpty(new1)){
                        DialogUtil.createEmptyMsgDialog(this,R.string.be_right_input_password);
                        return;
                    }
                    if (TextUtils.isEmpty(new2)){
                        DialogUtil.createEmptyMsgDialog(this,R.string.be_right_input_password);
                        return;
                    }
                    if (!new1.equals(new2)){
                        DialogUtil.createEmptyMsgDialog(this,R.string.passwrod_not_same);
                        return;
                    }
                    dialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE)
                            .setTitleText(getResources().getString(R.string.committing));
                    dialog.show();
                    mHttp.updatePasswordByOld(old,new1,mHandler);
                }
                break;
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case Constants.UPDATE_PASSWORD_SUCCESS:
                dialog.setTitleText(getResources().getString(R.string.password_change))
                    .setContentText(getResources().getString(R.string.update_password_success))
                    .setConfirmButton(getResources().getString(R.string.sure), new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dialog.cancel();
                            finish();
                        }
                    })
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                break;
            case Constants.UPDATE_PASSWORD_FALSE:
                if (dialog!=null){
                    dialog.cancel();
                }
                DialogUtil.createEmptyMsgDialog(this,message.obj.toString());
                break;
        }
        return false;
    }
}
