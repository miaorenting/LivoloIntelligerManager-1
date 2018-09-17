package livolo.com.livolointelligermanager.adaper;

import android.widget.TextView;

import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.SysApplication;
import livolo.com.livolointelligermanager.mode.ButtonDetail;

/**
 * Created by mayn on 2018/4/6.
 */

public class TextRecycleAdapter extends BaseRecyclerAdapter<ButtonDetail> {

    public TextRecycleAdapter() {
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item_textview;
    }

    @Override
    public void setUpData(CommonHolder holder, int position, int viewType, ButtonDetail data) {
        TextView text = getView(holder, R.id.text);
        if (!data.getButton_name().trim().equals("+")){
            text.setText(data.getButton_name()
                    + " ("
                    + (data.getButton_status() == 0 ? SysApplication.getInstance().getResources().getString(R.string.close) : SysApplication.getInstance().getResources().getString(R.string.open))
                    + ")");
        }else{
            text.setText(data.getButton_name());
        }
    }
}
