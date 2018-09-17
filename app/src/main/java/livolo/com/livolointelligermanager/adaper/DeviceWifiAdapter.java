package livolo.com.livolointelligermanager.adaper;

import android.net.wifi.ScanResult;
import android.widget.ImageView;
import android.widget.TextView;

import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;

import livolo.com.livolointelligermanager.R;

/**
 * Created by mayn on 2018/6/1.
 */

public class DeviceWifiAdapter extends BaseRecyclerAdapter<ScanResult> {

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item_gateway;
    }

    @Override
    public void setUpData(CommonHolder holder, int position, int viewType, ScanResult data) {
        TextView name = getView(holder, R.id.name);
        ImageView icon = getView(holder,R.id.icon);
        name.setText(data.SSID);
        icon.setBackgroundResource(R.mipmap.gateway_item);
    }
}
