package livolo.com.livolointelligermanager.adaper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.db.ButtonDao;
import livolo.com.livolointelligermanager.mode.RoomDetail;
import livolo.com.livolointelligermanager.mode.DeviceDetail;
import livolo.com.livolointelligermanager.receiver.BrodcaseTool;
import livolo.com.livolointelligermanager.util.BtnStatusUtil;
import livolo.com.livolointelligermanager.view.SwitchButton;

/**
 * Created by mayn on 2018/5/8.
 */

public class MainRoomGridViewAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInfalter;
    private List<RoomDetail> list;
    private ButtonDao mBtnDao;

    public MainRoomGridViewAdapter(Context context, List<RoomDetail> list){
        this.context = context;
        this.mInfalter = LayoutInflater.from(context);
        this.list = list;
        this.mBtnDao = new ButtonDao();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Holder holder = null;
        if (view == null){
            view = mInfalter.inflate(R.layout.item_main_room,null);
            holder = new Holder();
            holder.icon = view.findViewById(R.id.room_icon);
            holder.name = view.findViewById(R.id.room_name);
            holder.btn = view.findViewById(R.id.room_switch);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }
        final RoomDetail detail = list.get(i);
        holder.name.setText(detail.getRoom_name());
        int res = ConfigUtil.getRoomBlueIcon(detail.getPicture_index());
        Glide.with(context).load(res).into(holder.icon);
        boolean isopen = BtnStatusUtil.getRoomBtnStatus(detail.getSwitch_list());
        holder.btn.setChecked(isopen);
        setBg(holder.btn,isopen);
        final CheckedTextView btn = holder.btn;
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = btn.isChecked();
                int status = (!b ? 100 : 0);
                Log.e("-------------------","------------------------"+status);
                List<DeviceDetail> switchs = detail.getSwitch_list();
                if (switchs != null) {
                    for (int i = 0; i < switchs.size(); i++) {
                        mBtnDao.updateBtnStatus(switchs.get(i).getSwitch_id(), status);
                        //发送开关通知
                    }
                }
                btn.setChecked(!b);
                setBg(btn,!b);
                BrodcaseTool.sendMainBtnRefresh(context);
            }
        });
        return view;
    }

    class Holder{
        private ImageView icon;
        private TextView name;
        private CheckedTextView btn;
    }

    private void setBg(CheckedTextView view,boolean b){
        if (b){
            view.setBackgroundResource(R.mipmap.switch_btn_on);
        }else{
            view.setBackgroundResource(R.mipmap.switch_btn_off);
        }
    }

}
