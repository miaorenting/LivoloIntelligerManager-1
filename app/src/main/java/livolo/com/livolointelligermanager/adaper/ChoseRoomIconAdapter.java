package livolo.com.livolointelligermanager.adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import livolo.com.livolointelligermanager.R;

/**
 * Created by mayn on 2018/5/23.
 */

public class ChoseRoomIconAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private int[] roomIcons;
    private int[] roomNames;

    public ChoseRoomIconAdapter(Context context,int[] icons,int[] names){
        this.context = context;
        this.roomIcons = icons;
        this.roomNames = names;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return roomIcons.length;
    }

    @Override
    public Object getItem(int i) {
        return roomIcons[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder = null;
        if (view == null){
            holder = new Holder();
            view = mInflater.inflate(R.layout.item_roomicon,null);
            holder.icon = view.findViewById(R.id.icon);
            holder.name = view.findViewById(R.id.name);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }
        Glide.with(context).load(roomIcons[i]).into(holder.icon);
        holder.name.setText(roomNames[i]);

        return view;
    }


    class Holder{
        private TextView name;
        private ImageView icon;
    }
}
