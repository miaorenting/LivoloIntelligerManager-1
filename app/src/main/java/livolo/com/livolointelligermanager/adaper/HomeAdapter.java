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
 * Created by mayn on 2018/4/24.
 */

public class HomeAdapter extends BaseAdapter {

    private Context context;
    private List<HomeDetail> list;
    private LayoutInflater mInflater;

    public HomeAdapter(Context context, List<HomeDetail> list){
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
            view = mInflater.inflate(R.layout.item_textview,null);
            holder.text =view.findViewById(R.id.text);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }
        holder.text.setText(list.get(i).getHome_name());

        return view;
    }

    class Holder{
        private TextView text;
    }
}
