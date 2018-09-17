package livolo.com.livolointelligermanager.ui;

import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.EditLimitUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;

/**
 * Created by mayn on 2018/6/26.
 */

public class ChangPasswordByEmailActivity extends BaseActivity implements View.OnClickListener,Handler.Callback{
    private final static int TimerResult = 1000;

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_right)
    TextView topRight;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.email_code)
    EditText emailCode;
    @BindView(R.id.code_right)
    TextView codeRight;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.commit_btn)
    Button commitBtn;

    private Handler mHandler;
    private SweetAlertDialog dialog;
    private HttpTools mHttp;
    private Timer timer;
    private int timeCount = 180;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.midblue);
        setContentView(R.layout.activity_chang_password_email);
        ButterKnife.bind(this);
        topTitle.setText(R.string.password_change);
        mHandler = new Handler(this);
        mHttp = new HttpTools();
        initLayout();
    }

    private void initLayout(){
        backBtn.setOnClickListener(this);
        codeRight.setOnClickListener(this);
        commitBtn.setOnClickListener(this);
        EditLimitUtil.setEditLimit(email,50,this);
        EditLimitUtil.setEditLimit(password,20,this);

    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case Constants.SEND_EMAIL_CODE_SUCCESS:
                startTimer();
                Toast.makeText(this,R.string.send_success,Toast.LENGTH_SHORT).show();
                break;
            case Constants.SEND_EMAIL_CODE_FALSE:
                Toast.makeText(this,R.string.send_failed,Toast.LENGTH_SHORT).show();
                break;
            case Constants.UPDATE_PASSWORD_SUCCESS:
                if (dialog!=null){
                    dialog.setTitleText(getResources().getString(R.string.update_success))
                            .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.cancel();
                                    finish();
                                }
                            })
                            .showCancelButton(false)
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                }

                break;
            case Constants.UPDATE_PASSWORD_FALSE:
                if (dialog!=null){
                    dialog.cancel();
                }
                DialogUtil.createEmptyMsgDialog(this,message.obj.toString());
                break;
            case TimerResult:
                if (timeCount <= 0) {
                    stopTimer();
                    codeRight.setText(R.string.code_btn);
                } else {
                    codeRight.setText(timeCount + "");
                }
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
            case R.id.code_right:
                String emailStr = email.getText().toString();
                if (TextUtils.isEmpty(emailStr)){
                    DialogUtil.createEmptyMsgDialog(this,R.string.email_cannot_be_empty);
                    break;
                }
                if (!ConfigUtil.isEmail(emailStr)){
                    DialogUtil.createEmptyMsgDialog(this,R.string.email_format_error);
                    break;
                }
                mHttp.sendEmailCode(emailStr,mHandler);
                break;
            case R.id.commit_btn:
                if (!EditLimitUtil.isLimit(password,20,this)){
                    return;
                }
                if (TextUtils.isEmpty(email.getText().toString())){
                    DialogUtil.createEmptyMsgDialog(this,R.string.email_cannot_be_empty);
                    break;
                }
                if (!ConfigUtil.isEmail(email.getText().toString())){
                    DialogUtil.createEmptyMsgDialog(this,R.string.email_format_error);
                    break;
                }
                if (TextUtils.isEmpty(emailCode.getText().toString())){
                    DialogUtil.createEmptyMsgDialog(this,R.string.code_cannot_be_empty);
                    break;
                }
                if (TextUtils.isEmpty(emailCode.getText().toString())){
                    DialogUtil.createEmptyMsgDialog(this,R.string.code_cannot_be_empty);
                    break;
                }
                if (TextUtils.isEmpty(password.getText().toString())){
                    DialogUtil.createEmptyMsgDialog(this,R.string.input_password_login);
                    break;
                }

                dialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE)
                        .showCancelButton(false)
                        .setTitleText(getResources().getString(R.string.committing));
                dialog.show();
                mHttp.updatePasswordByEmail(email.getText().toString(),emailCode.getText().toString(),password.getText().toString(),mHandler);
                break;
        }
    }

    private void startTimer() {
        timer = new Timer();
        timeCount = 180;
        codeRight.setClickable(false);
        codeRight.setBackgroundResource(R.drawable.text_grav_bg);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeCount--;
                mHandler.sendEmptyMessage(TimerResult);
            }
        }, 0, 1000);
    }

    // 停止定时器
    private void stopTimer() {
        codeRight.setClickable(true);
        codeRight.setBackgroundResource(R.drawable.click_bg);
        if (timer != null) {
            timer.cancel();
            // 一定设置为null，否则定时器不会被回收
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
