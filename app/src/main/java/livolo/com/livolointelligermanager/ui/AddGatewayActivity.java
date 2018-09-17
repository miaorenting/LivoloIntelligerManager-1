package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.util.StatusBarUtils;

/**
 * Created by mayn on 2018/4/9.
 */

public class AddGatewayActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.other_status)
    TextView otherStatus;
    @BindView(R.id.next_btn)
    Button nextBtn;
    @BindView(R.id.checkbox)
    ImageView checkbox;

    private static Activity activity;
    private boolean isAgree = true;


    public static Activity getInstance() {
        return activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addgateway);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.midblue);
        activity = this;
        ButterKnife.bind(this);
        initLayout();
    }

    private void initLayout() {
        topTitle.setText(R.string.addgateway_title);
        backBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        //下划线并加上抗锯齿
        otherStatus.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        otherStatus.getPaint().setAntiAlias(true);//抗锯齿
        otherStatus.setOnClickListener(this);
        checkbox.setOnClickListener(this);
    }

    private void changeCheckbox(){
        if (isAgree){
            isAgree = false;
            checkbox.setBackgroundResource(R.mipmap.select_out);
        }else{
            isAgree = true;
            checkbox.setBackgroundResource(R.mipmap.select_blue);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();

                break;
            case R.id.next_btn:
                Intent intent = new Intent();
                intent.setClass(this, WorkWifiChoseActivity.class);
                startActivity(intent);
                break;
            case R.id.checkbox:
                changeCheckbox();
                break;
            case R.id.other_status:
                Intent resetIntent = new Intent(this,RestartGatewayActivity.class);
                startActivity(resetIntent);
                break;
        }
    }
}
