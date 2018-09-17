package livolo.com.livolointelligermanager.adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.mode.DeviceDetail;

/**
 * Created by mayn on 2018/4/13.
 */

public class AddRoomItemAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private String roomid;
    private List<DeviceDetail> list = new ArrayList<>();
    private Map<String,String> map;

    public AddRoomItemAdapter(Context context, String roomid, List<DeviceDetail> tempList, Map<String,String> map) {
        this.list = tempList;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.roomid = roomid;
        this.map = map;
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
        if (view == null) {
            view = mInflater.inflate(R.layout.item_addroom, null);
            holder = new Holder();
            holder.name = view.findViewById(R.id.item_room_name);
            holder.icon = view.findViewById(R.id.item_icon);
            holder.content = view.findViewById(R.id.item_room_info);
            holder.checkBox = view.findViewById(R.id.item_check);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        final DeviceDetail detail = list.get(i);
        holder.name.setText(detail.getSwitch_name());
        Glide.with(context).load(R.mipmap.switch_icon_1).into(holder.icon);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detail.getRoom_id().equals(roomid)) {
                    detail.setSelected(false);
                    detail.setRoom_id("00000000000000000000000000000000");
                    Glide.with(context).load(R.mipmap.select_out).into(holder.checkBox);
                } else {
                    detail.setRoom_id(roomid);
                    detail.setSelected(true);
                    Glide.with(context).load(R.mipmap.select_blue).into(holder.checkBox);
                }
            }
        });
        if (detail.getRoom_id().equals(roomid)) {
            Glide.with(context).load(R.mipmap.select_blue).into(holder.checkBox);
            detail.setSelected(true);
            holder.content.setText(R.string.belong_room);
            holder.checkBox.setClickable(true);
        } else {
            if (detail.getRoom_id().equals("00000000000000000000000000000000")) {
                Glide.with(context).load(R.mipmap.select_out).into(holder.checkBox);
                holder.content.setText(R.string.no_group);
                holder.checkBox.setClickable(true);
            } else {
                Glide.with(context).load(R.mipmap.select_grav).into(holder.checkBox);
                holder.content.setText(context.getResources().getString(R.string.belong)+map.get(detail.getRoom_id()));
                holder.checkBox.setClickable(false);
            }
            detail.setSelected(false);
        }
        return view;
    }

    class Holder {
        private ImageView icon;
        private TextView name;
        private TextView content;
        private ImageView checkBox;
    }
}
