package livolo.com.livolointelligermanager.adaper;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.db.DeviceDao;
import livolo.com.livolointelligermanager.mode.DeviceDetail;
import livolo.com.livolointelligermanager.mode.RoomDetail;
import livolo.com.livolointelligermanager.view.SwitchButton;

/**
 * Created by mayn on 2018/4/13.
 */

public class RoomAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private Context context;
    private List<RoomDetail> list;
    private boolean isShowCheckBox = false;//是否展示复选框
    private DeviceDao mDeviceDao;

    public RoomAdapter(Context context,List<RoomDetail> list,boolean b){
        this.list = list;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.isShowCheckBox = b;
        this.mDeviceDao = new DeviceDao();
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Holder holder;
        if (view == null){
            view = mInflater.inflate(R.layout.item_room,null);
            holder = new Holder();
            holder.icon = view.findViewById(R.id.item_icon);
            holder.roomName = view.findViewById(R.id.item_room_name);
            holder.infoText = view.findViewById(R.id.item_room_info);
            holder.checkBox = view.findViewById(R.id.checkbox);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }
        final RoomDetail detail = list.get(i);
        Glide.with(context).load(ConfigUtil.getRoomBlackIcon(detail.getPicture_index())).into(holder.icon);
        holder.roomName.setText(detail.getRoom_name());
        List<DeviceDetail> tempList = mDeviceDao.getSwitchsByRoomID(detail.getRoom_id());
        if (tempList!=null){
            holder.infoText.setText(context.getResources().getString(R.string.device_number)+ tempList.size());
        }else{
            holder.infoText.setText(context.getResources().getString(R.string.device_number)+0);
        }
        holder.checkBox.setBackgroundResource(R.mipmap.select_out);
        if (isShowCheckBox){
            holder.checkBox.setVisibility(View.VISIBLE);
        }else{
            holder.checkBox.setVisibility(View.GONE);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detail.isSelected()){
                    detail.setSelected(false);
                    holder.checkBox.setBackgroundResource(R.mipmap.select_out);
                }else{
                    detail.setSelected(true);
                    holder.checkBox.setBackgroundResource(R.mipmap.select_blue);
                }
            }
        });
        return view;
    }

    public void setCheckBox(boolean b){
        this.isShowCheckBox = b;
    }

    class Holder{
        private ImageView icon;
        private TextView roomName;
        private TextView infoText;
        private ImageView checkBox;
    }
}
