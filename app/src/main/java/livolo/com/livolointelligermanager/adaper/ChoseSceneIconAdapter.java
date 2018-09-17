package livolo.com.livolointelligermanager.adaper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import livolo.com.livolointelligermanager.R;


/**
 * Created by mayn on 2018/5/14.
 */

public class ChoseSceneIconAdapter extends BaseAdapter {

    private Context context;
    private int[] icons;
    private LayoutInflater inflater;

    public ChoseSceneIconAdapter(Context context,int[] icons){
        this.context = context;
        this.icons = icons;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return icons.length;
    }

    @Override
    public Object getItem(int i) {
        return icons[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder = null;
        if (view == null ){
            holder = new Holder();
            view = inflater.inflate(R.layout.item_imageview,null);
            holder.icon = view.findViewById(R.id.icon_image);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }
        Log.e("-----------icon","-----------------------------"+icons[i]);
        Glide.with(context).load(icons[i]).into(holder.icon);
        return view;
    }

    class Holder{
        private ImageView icon;
    }
}
