package livolo.com.livolointelligermanager.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.util.StatusBarUtils;

/**
 * Created by mayn on 2018/5/16.
 */

public class SwitchChangeActivity extends BaseActivity implements Handler.Callback {

    @BindView(R.id.picker)
    ColorPicker picker;
    @BindView(R.id.svbar)
    SVBar svbar;
    @BindView(R.id.send_text)
    TextView sendText;
//    @BindView(R.id.opacitybar)
//    OpacityBar opacitybar;
//    @BindView(R.id.saturationbar)
//    SaturationBar saturationbar;
//    @BindView(R.id.valuebar)
//    ValueBar valuebar;

    private HttpTools mHttp;
    private Handler mHandler;

    private long oldTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changebtn);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.midblue);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        mHttp = new HttpTools();
        picker.addSVBar(svbar);//色调
//        picker.addSaturationBar(saturationbar);//饱和度
//        picker.addValueBar(valuebar);//亮度
        //To get the color
        picker.getColor();
        //to turn of showing the old color
        picker.setShowOldCenterColor(false);
        //To set the old selected color u can do it like this
        picker.setOldCenterColor(picker.getColor());
        // adds listener to the colorpicker which is implemented
        //in the activity
        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                if (System.currentTimeMillis() - oldTime > 200) {
                    oldTime = System.currentTimeMillis();
                    int[] colors = getArgb(color);
                    Log.e("-----------", colors[0] + "----------" + colors[1] + "----------" + colors[2] + "----------" + colors[3]);
                    sendText.setText("15164643168743---"+100+"---15164643168743---type:"+5+"---R:"+colors[1]+"---G:"+colors[2]+" ----B:"+ colors[3]);
                    mHttp.controlSwicht("15164643168743", 100, "15164643168743", 5, colors[1], colors[2], colors[3], mHandler);
                }

            }
        });

    }

    public int[] getArgb(int color) {
        final int a = (color >>> 24);
        final int r = (color >> 16) & 0xFF;
        final int g = (color >> 8) & 0xFF;
        final int b = (color) & 0xFF;
        return new int[]{a, r, g, b};
    }

    @Override
    public boolean handleMessage(Message message) {
        Log.e("", "--------------" + message.obj.toString());
        return false;
    }
}
