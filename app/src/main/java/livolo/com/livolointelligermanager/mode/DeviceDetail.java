package livolo.com.livolointelligermanager.mode;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mayn on 2018/4/6.
 */

public class DeviceDetail implements Serializable {

    private String switch_id;
    private int picture_index;//开关类型，单连还是二连还是三联...
    private String switch_name;
    private String gateway_id;
    private String room_id;//所属房间id
    private int switch_type;
    private boolean isSelected = false;
    private List<ButtonDetail> button_list;

    public String getSwitch_id() {
        return switch_id;
    }

    public void setSwitch_id(String switch_id) {
        this.switch_id = switch_id;
    }

    public int getPicture_index() {
        return picture_index;
    }

    public void setPicture_index(int picture_index) {
        this.picture_index = picture_index;
    }

    public String getSwitch_name() {
        return switch_name;
    }

    public void setSwitch_name(String switch_name) {
        this.switch_name = switch_name;
    }

    public String getGateway_id() {
        return gateway_id;
    }

    public void setGateway_id(String gateway_id) {
        this.gateway_id = gateway_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public List<ButtonDetail> getButton_list() {
        return button_list;
    }

    public void setButton_list(List<ButtonDetail> button_list) {
        this.button_list = button_list;
    }

    public int getSwitch_type() {
        return switch_type;
    }

    public void setSwitch_type(int switch_type) {
        this.switch_type = switch_type;
    }
}
