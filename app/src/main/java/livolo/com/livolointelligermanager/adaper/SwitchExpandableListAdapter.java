package livolo.com.livolointelligermanager.adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.mode.RoomDetail;
import livolo.com.livolointelligermanager.mode.DeviceDetail;

/**
 * Created by mayn on 2018/4/12.
 */

public class SwitchExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<RoomDetail> list;
    private LayoutInflater mInflater;

    public SwitchExpandableListAdapter(Context context,List<RoomDetail> list){
        this.context = context;
        this.list = list;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return list.get(i).getSwitch_list()==null?0:list.get(i).getSwitch_list().size();
    }

    @Override
    public Object getGroup(int i) {
        return list.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return list.get(i).getSwitch_list()==null?null:list.get(i).getSwitch_list().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i*100000+i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        GroupHolder holder = null;
        if (view == null){
            holder = new GroupHolder();
            view = mInflater.inflate(R.layout.item_group_textview,null);
            holder.roomName = view.findViewById(R.id.text);
            holder.roomName.setBackgroundResource(R.color.lightblue);
            view.setTag(holder);
        }else{
            holder = (GroupHolder) view.getTag();
        }
        holder.roomName.setText(list.get(i).getRoom_name());
        if (list.get(i).getSwitch_list()==null){
            holder.roomName.setVisibility(View.GONE);
        }else{
            holder.roomName.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildHolder holder = null;
        if (view == null){
            view = mInflater.inflate(R.layout.item_switch,null);
            holder = new ChildHolder();
            holder.icon = view.findViewById(R.id.switch_btn);
            holder.btnName = view.findViewById(R.id.switch_name);
            view.setTag(holder);
        }else{
            holder = (ChildHolder) view.getTag();
        }
        if (list.get(i).getSwitch_list()!=null){
            DeviceDetail detail = list.get(i).getSwitch_list().get(i1);
            Glide.with(context).load(R.mipmap.switch_icon_1).into(holder.icon);
            holder.btnName.setText(detail.getSwitch_name());
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    class GroupHolder{
        private TextView roomName;
    }
    class ChildHolder{
        private ImageView icon;
        private TextView btnName;
    }

}
