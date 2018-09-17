package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.EditLimitUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;

/**
 * Created by mayn on 2018/4/16.
 */

public class FeedbackActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.top_right)
    TextView topRight;
    @BindView(R.id.feed_1)
    TextView feed1;
    @BindView(R.id.feed_2)
    TextView feed2;
    @BindView(R.id.feed_3)
    TextView feed3;
    @BindView(R.id.feed_4)
    TextView feed4;
    @BindView(R.id.feed_content)
    EditText feedContent;

    private TextView[] texts = new TextView[4];
    private HttpTools mHttp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.midblue);
        ButterKnife.bind(this);
        mHttp = new HttpTools();
        topRight.setText(R.string.commit);
        topTitle.setText(R.string.feedback);

        backBtn.setOnClickListener(this);
        topRight.setOnClickListener(this);
        feed1.setOnClickListener(this);
        feed2.setOnClickListener(this);
        feed3.setOnClickListener(this);
        feed4.setOnClickListener(this);
        texts[0] = feed1;
        texts[1] = feed2;
        texts[2] = feed3;
        texts[3] = feed4;
        setTextBorder(0);
        EditLimitUtil.setEditLimit(feedContent,1000,this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.top_right:
                new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getResources().getString(R.string.be_need_commit))
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
//                                mHttp.
                                finish();//暂时测试
                            }
                        })
                        .setCancelButton(R.string.cancel, null).show();
                break;
            case R.id.feed_1:
                setTextBorder(0);
                break;
            case R.id.feed_2:
                setTextBorder(1);
                break;
            case R.id.feed_3:
                setTextBorder(2);
                break;
            case R.id.feed_4:
                setTextBorder(3);
                break;
        }
    }

    private void setTextBorder(int index){
        for (int i = 0;i<texts.length;i++){
            if (index == i){
                texts[i].setBackgroundResource(R.drawable.blue_button_background);
            }else{
                texts[i].setBackgroundResource(R.drawable.border_grav);
            }
        }
    }
}
