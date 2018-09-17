package livolo.com.livolointelligermanager.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

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
 * Created by mayn on 2018/4/27.
 */

public class ChangePasswordByCodeActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {
    private final static int TimerResult = 1000;

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_right)
    TextView topRight;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.phone_number)
    EditText phoneNumber;
    @BindView(R.id.phone_code)
    EditText phoneCode;
    @BindView(R.id.code_right)
    TextView codeRight;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.commit_btn)
    Button commitBtn;

    private Handler mHandler;
    private SweetAlertDialog dialog;
    private HttpTools mHttp;
    private String teleCode = "86";
    private Timer timer;
    private int timeCount = 120;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passage_code);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.midblue);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        mHttp = new HttpTools();
        initLayout();
    }

    private void initLayout() {
        topTitle.setText(R.string.password_change);
        backBtn.setOnClickListener(this);
        commitBtn.setOnClickListener(this);
        codeRight.setOnClickListener(this);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
                R.array.countries, android.R.layout.simple_spinner_item);
        // 设置Spinner中每一项的样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 设置Spinner数据来源适配器
        spinner.setAdapter(adapter);
        spinner.setSelection(38);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String str = adapterView.getItemAtPosition(i).toString();
                teleCode = str;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        EditLimitUtil.setEditLimit(password,20,this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.code_right:
                if (TextUtils.isEmpty(phoneNumber.getText().toString()) && TextUtils.isEmpty(teleCode)) {
                    DialogUtil.createEmptyMsgDialog(this, R.string.phone_cannot_be_empty);
                } else {
                    mHttp.getRegisterCode(phoneNumber.getText().toString(), teleCode,1, mHandler);
                }
                break;
            case R.id.commit_btn:
                if (!EditLimitUtil.isLimit(password,20,this)){
                    return;
                }

                if (TextUtils.isEmpty(teleCode)){
                    DialogUtil.createEmptyMsgDialog(this,R.string.region_cannot_be_empty);
                    return;
                }
                String phone = phoneNumber.getText().toString();
                if (TextUtils.isEmpty(phone)){
                    DialogUtil.createEmptyMsgDialog(this,R.string.phone_cannot_be_empty);
                    return;
                }
                String code = phoneCode.getText().toString();
                if (TextUtils.isEmpty(code)){
                    DialogUtil.createEmptyMsgDialog(this,R.string.code_cannot_be_empty);
                    return;
                }
                String pwd = password.getText().toString();
                if (TextUtils.isEmpty(pwd)){
                    DialogUtil.createEmptyMsgDialog(this,R.string.input_password_login);
                    return;
                }
                dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText(getResources().getString(R.string.committing));
                dialog.show();
                mHttp.updatePassword(teleCode,phone,code,pwd,mHandler);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case Constants.UPDATE_PASSWORD_SUCCESS:
                dialog.setTitleText(getResources().getString(R.string.update_success))
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                dialog.cancel();
                                finish();
                            }
                        })
                        .showCancelButton(false)
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                break;
            case Constants.UPDATE_PASSWORD_FALSE:
                stopTimer();
                if (dialog!=null){
                    dialog.cancel();
                }
                DialogUtil.createEmptyMsgDialog(this,message.obj.toString());
                break;
            case Constants.GET_CODE_BY_PHONE_SUCCESS:
                Log.e("---------", "---------------------code.success");
                //发送成功
                startTimer();//发送成功后开始计时
                break;
            case Constants.GET_CODE_BY_PHONE_FALSE:
                //发送失败
                DialogUtil.createEmptyMsgDialog(this, message.obj.toString());
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

    private void startTimer() {
        timer = new Timer();
        timeCount = 120;
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
}
