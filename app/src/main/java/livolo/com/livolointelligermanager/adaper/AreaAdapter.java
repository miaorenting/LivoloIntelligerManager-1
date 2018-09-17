package livolo.com.livolointelligermanager.adaper;

import android.content.Context;
import android.net.sip.SipSession;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.mode.AreaDetail;

public class AreaAdapter extends BaseAdapter {
    private Context context;
    private List<AreaDetail> list;
    private LayoutInflater mInflater;

    public AreaAdapter(Context context, List<AreaDetail> list){
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Holder holder = null;
        if (view == null){
            holder = new Holder();
            view = mInflater.inflate(R.layout.item_area,null);
            holder.areaText = view.findViewById(R.id.area_name);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }
        holder.areaText.setText("< "+list.get(position).getRegion_name()+" >");
        return view;
    }

    class Holder{
        private TextView areaText;
    }

}
