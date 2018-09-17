package livolo.com.livolointelligermanager.util;

import java.util.List;

import livolo.com.livolointelligermanager.mode.ButtonDetail;
import livolo.com.livolointelligermanager.mode.DeviceDetail;

/**
 * Created by mayn on 2018/4/19.
 */

public class BtnStatusUtil {

    /**
     * 获取房间总开关状态 true表示有开关为开启状态，false 表示所有开关关闭状态 */
    public static boolean getRoomBtnStatus(List<DeviceDetail> list) {
        if (list!=null){
            for (int i = 0; i < list.size(); i++) {
                DeviceDetail detail = list.get(i);
                if (detail != null && detail.getButton_list() != null) {
                    for (int m = 0; m < detail.getButton_list().size(); m++) {
                        ButtonDetail btn = detail.getButton_list().get(m);
                        if (btn.getButton_status() > 0) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取开关总开关状态 true表示有按键为开启状态，false 表示所有按键关闭状态 */
    public static boolean getSwitchBtnStatus(List<ButtonDetail> list) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ButtonDetail detail = list.get(i);
                if (detail.getButton_status() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

}
