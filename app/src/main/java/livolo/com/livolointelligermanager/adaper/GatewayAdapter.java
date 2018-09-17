package livolo.com.livolointelligermanager.adaper;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.mode.GatewayDetail;

/**
 * Created by mayn on 2018/4/28.
 */

public class GatewayAdapter extends BaseRecyclerAdapter<GatewayDetail> {
    private Context context;

    public GatewayAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item_gateway;
    }

    @Override
    public void setUpData(CommonHolder holder, int position, int viewType, GatewayDetail data) {
        TextView name = getView(holder,R.id.name);
        ImageView icon = getView(holder,R.id.icon);
        ImageView warnIcon = getView(holder,R.id.full_icon);
        warnIcon.setVisibility(data.getFull_flag()==1? View.VISIBLE: View.GONE);
        Glide.with(context).load(R.mipmap.gateway_item).into(icon);
        name.setText(data.getGateway_name());
    }
}
