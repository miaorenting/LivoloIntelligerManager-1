package livolo.com.livolointelligermanager.adaper;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.mode.SenceDetail;

/**
 * Created by mayn on 2018/4/18.
 */

public class SenceAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context context;
    private List<SenceDetail> list;
    private boolean isShowCheckBox = false;//是否展示复选框
    private boolean isAllItemEnable = true;

    public SenceAdapter(Context context, List<SenceDetail> list, boolean b){
        this.list = list;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.isShowCheckBox = b;
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
            view = mInflater.inflate(R.layout.item_sence,null);
            holder = new Holder();
            holder.icon = view.findViewById(R.id.item_icon);
            holder.roomName = view.findViewById(R.id.item_room_name);
            holder.checkBox = view.findViewById(R.id.checkbox);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }
        final SenceDetail detail = list.get(i);
        if (TextUtils.isEmpty(detail.getPicture_url())) {
            int res = ConfigUtil.getSenceBlueIcon(detail.getPicture_index());
            Glide.with(context).load(res).into(holder.icon);
        } else {
            Glide.with(context).load(Constants.URL+detail.getPicture_url()).into(holder.icon);
        }
        holder.roomName.setText(detail.getScene_name());
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

    public void setIsEnabledClick(boolean b){
        isAllItemEnable = b;
        notifyDataSetChanged();
    }

    class Holder{
        private ImageView icon;
        private TextView roomName;
        private ImageView checkBox;
    }
}
