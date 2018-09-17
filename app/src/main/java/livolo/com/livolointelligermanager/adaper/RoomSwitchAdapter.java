package livolo.com.livolointelligermanager.adaper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.db.ButtonDao;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.ButtonDetail;
import livolo.com.livolointelligermanager.mode.DeviceDetail;
import livolo.com.livolointelligermanager.ui.RoomActivity;
import livolo.com.livolointelligermanager.util.BtnStatusUtil;
import livolo.com.livolointelligermanager.view.SwitchButton;

/**
 * Created by mayn on 2018/4/6.
 */

public class RoomSwitchAdapter extends BaseRecyclerAdapter<DeviceDetail> {
    private RoomActivity context;
    private ButtonDao mBtnDao;
    private Handler mHandler;
    private HttpTools mHttp;

    public RoomSwitchAdapter(RoomActivity context,Handler mHandler){
        this.context = context;
        this.mBtnDao = new ButtonDao();
        this.mHandler = mHandler;
        this.mHttp = new HttpTools();
    }
    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item_room_switch_btn;
    }

    @Override
    public void setUpData(CommonHolder holder, int position, int viewType, final DeviceDetail data) {
        TextView btn1 = getView(holder,R.id.btn_1);
        TextView btn2 = getView(holder,R.id.btn_2);
        btn2.setVisibility(View.GONE);
        TextView btn3 = getView(holder,R.id.btn_3);
        btn3.setVisibility(View.GONE);
        final List<TextView> tempList = new ArrayList<>();
        tempList.add(btn1);
        tempList.add(btn2);
        tempList.add(btn3);
        TextView switchName = getView(holder,R.id.switch_name);
        final SwitchButton switchButton = getView(holder,R.id.switch_control);
        switchName.setText(data.getSwitch_name());
        final List<ButtonDetail> list = data.getButton_list();
        switchButton.setChecked(BtnStatusUtil.getSwitchBtnStatus(list));//开关总按键状态
//        switchButton.toggle();
        for (int i =0;i<list.size();i++){
            final TextView nameView = tempList.get(i);
            nameView.setVisibility(View.VISIBLE);
            nameView.setText(list.get(i).getButton_name());
            if (list.get(i).getButton_status()==0){
                nameView.setBackgroundResource(R.mipmap.btn_on);
            }else{
                nameView.setBackgroundResource(R.mipmap.btn_off);
            }
            final int finalI = i;
            nameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ButtonDetail detail = list.get(finalI);
                    changeBtnStatus(detail,nameView);
                    switchButton.setChecked(BtnStatusUtil.getSwitchBtnStatus(list));//开关总按键状态
                }
            });
            nameView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {
                    final ButtonDetail detail = list.get(finalI);
                    final View layout = LayoutInflater.from(context).inflate(R.layout.dialog_addroom, null);
                    new AlertDialog.Builder(context).setTitle(R.string.input_switch_name).setView(layout)
                            .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    TextView et = layout.findViewById(R.id.etname);
                                    String name = et.getText().toString();
                                    if (!TextUtils.isEmpty(name)){
                                        detail.setButton_name(name);
                                        mBtnDao.updateBtn(detail);
                                        nameView.setText(name);
                                        mHttp.updateButton(detail.getButton_id(),name,mHandler);
                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancel, null).show();


                    return false;
                }
            });
        }
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = switchButton.isChecked();
                int status = (!b)?100:0;
                for (int i = 0;i<list.size();i++){
                    list.get(i).setButton_status(status);
                    mBtnDao.updateBtn(list.get(i));
                    tempList.get(i).setBackgroundResource((!b)?R.mipmap.btn_off:R.mipmap.btn_on);
                    /**发送开关指令*/
                    mHttp.controlSwicht(data.getGateway_id(),status,data.getSwitch_id(),4,0,0,0,mHandler);
                }
                switchButton.setChecked(!b);
                context.refreshBtnStatus();
            }
        });
    }

    private void changeBtnStatus(ButtonDetail detail,View view){
        if (detail.getButton_status()==0){
            view.setBackgroundResource(R.mipmap.btn_off);
            detail.setButton_status(100);
            if (view.getId() == R.id.btn_1){
                mHttp.controlSwicht(detail.getGateway_id(),100,detail.getButton_id(),0,0,0,0,mHandler);
            }
        }else{
            view.setBackgroundResource(R.mipmap.btn_on);
            detail.setButton_status(0);
            if (view.getId() == R.id.btn_1){
                mHttp.controlSwicht(detail.getGateway_id(),0,detail.getButton_id(),0,0,0,0,mHandler);
            }
        }
        mBtnDao.updateBtn(detail);
        context.refreshBtnStatus();
    }

}
