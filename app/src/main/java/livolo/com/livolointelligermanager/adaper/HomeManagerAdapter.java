package livolo.com.livolointelligermanager.adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.mode.HomeDetail;

/**
 * Created by mayn on 2018/6/12.
 */

public class HomeManagerAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<HomeDetail> list;

    public HomeManagerAdapter(Context context, List<HomeDetail> list){
        this.context = context;
        this.list = list;
        this.mInflater = LayoutInflater.from(context);
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
        Holder holder = null;
        if (view == null){
            holder = new Holder();
            view = mInflater.inflate(R.layout.item_homemanager,null);
            holder.name = view.findViewById(R.id.home_name);
            holder.address = view.findViewById(R.id.home_address);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }
        holder.name.setText(list.get(i).getHome_name());
        holder.address.setText(list.get(i).getHome_address());
        return view;
    }

    class Holder{
        private TextView name;
        private TextView address;
    }

}
