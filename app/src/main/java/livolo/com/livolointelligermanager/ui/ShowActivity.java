package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.util.StatusBarUtils;

/**
 * Created by mayn on 2018/4/2.
 */

public class ShowActivity extends BaseActivity implements Handler.Callback{

    private Handler mHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_show);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.deepblue);
        mHandler = new Handler(this);
        Message msg = mHandler.obtainMessage();
        mHandler.sendEmptyMessageDelayed(0,1000);
    }

    @Override
    public boolean handleMessage(Message message) {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
        return false;
    }
}
