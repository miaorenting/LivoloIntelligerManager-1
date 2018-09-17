package livolo.com.livolointelligermanager.adaper;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;

import java.util.List;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.db.ButtonDao;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.RoomDetail;
import livolo.com.livolointelligermanager.mode.DeviceDetail;
import livolo.com.livolointelligermanager.receiver.BrodcaseTool;
import livolo.com.livolointelligermanager.util.BtnStatusUtil;
import livolo.com.livolointelligermanager.view.SwitchButton;

/**
 * Created by mayn on 2018/4/6.
 */

public class MainRoomAdapter extends BaseRecyclerAdapter<RoomDetail> {
    private Context context;
    private ButtonDao mBtnDao;
    private HttpTools mHttp;
    private Handler mHander;

    public MainRoomAdapter(Context context, Handler mHander) {
        this.context = context;
        this.mHander = mHander;
        this.mBtnDao = new ButtonDao();
        this.mHttp = new HttpTools();
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item_main_room;
    }

    @Override
    public void setUpData(CommonHolder holder, int position, int viewType, final RoomDetail data) {
        ImageView icon = getView(holder, R.id.room_icon);
        TextView name = getView(holder, R.id.room_name);
        final CheckedTextView btn = getView(holder, R.id.room_switch);
        int res = ConfigUtil.getRoomBlueIcon(data.getPicture_index());
        Glide.with(context).load(res).into(icon);
        name.setText(data.getRoom_name());
        boolean btnStatus = BtnStatusUtil.getRoomBtnStatus(data.getSwitch_list());
        btn.setChecked(btnStatus);
        if (btnStatus) {
            btn.setBackgroundResource(R.mipmap.switch_btn_on);
        }else{
            btn.setBackgroundResource(R.mipmap.switch_btn_off);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = btn.isChecked();
                int status = (!b ? 100 : 0);
                List<DeviceDetail> switchs = data.getSwitch_list();
                if (switchs != null) {
                    for (int i = 0; i < switchs.size(); i++) {
                        mBtnDao.updateBtnStatus(switchs.get(i).getSwitch_id(), status);
                    }
                    BrodcaseTool.sendMainBtnRefresh(context);
                }
                //提交网络请求，如果失败了，则交由主页刷新全部按键状态
                mHttp.controlSwicht("", status, data.getRoom_id(), 1,0,0,0, mHander);
                btn.setChecked(!b);
                if (!b){
                    btn.setBackgroundResource(R.mipmap.switch_btn_on);
                }else{
                    btn.setBackgroundResource(R.mipmap.switch_btn_off);
                }
            }
        });
    }
}
