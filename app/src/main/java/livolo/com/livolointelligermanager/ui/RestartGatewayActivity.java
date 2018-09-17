package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.util.WindowManagerUtil;

/**
 * Created by mayn on 2018/5/28.
 */

public class RestartGatewayActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.top_right)
    TextView topRight;
    @BindView(R.id.image_operation)
    ImageView imageOperation;
    @BindView(R.id.next_btn)
    Button nextBtn;

    private int width = 0;

    private static Activity activity;

    public static Activity getInstance() {
        return activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restart_gateway);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.midblue);
        ButterKnife.bind(this);
        activity = this;
        initLayout();
    }

    private void initLayout(){
        width = WindowManagerUtil.getWindowWidth(this);
        ViewGroup.LayoutParams lp = imageOperation.getLayoutParams();
        lp.width = width*35/100;
        lp.height = width*48/100;
        imageOperation.setLayoutParams(lp);
        topTitle.setText(R.string.reset_gateway);
        backBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.next_btn:
                Intent intent = new Intent(this,WorkWifiChoseActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
