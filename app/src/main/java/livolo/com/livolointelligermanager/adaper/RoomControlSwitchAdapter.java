package livolo.com.livolointelligermanager.adaper;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.mode.DeviceDetail;

/**
 * Created by mayn on 2018/6/15.
 */

public class RoomControlSwitchAdapter extends BaseAdapter {

    private Context context;
    private List<DeviceDetail> devices;
    private LayoutInflater mInflater;

    public RoomControlSwitchAdapter(Context context,List<DeviceDetail> list){
        this.context = context;
        this.devices = list;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int i) {
        return devices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = mInflater.inflate(R.layout.item_room_switch_btn,null);



        return view;
    }
}
