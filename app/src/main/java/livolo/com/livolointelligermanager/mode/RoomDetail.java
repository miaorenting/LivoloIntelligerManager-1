package livolo.com.livolointelligermanager.mode;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mayn on 2018/4/6.
 */

public class RoomDetail implements Serializable {

    private String room_id;
    private int picture_index;
    private String room_name;
    private String home_id;//所属homeid
    private boolean isSelected = false;
    private boolean isOpen = false;
    private List<DeviceDetail> switch_list;

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public int getPicture_index() {
        return picture_index;
    }

    public void setPicture_index(int picture_index) {
        this.picture_index = picture_index;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getHome_id() {
        return home_id;
    }

    public void setHome_id(String home_id) {
        this.home_id = home_id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public List<DeviceDetail> getSwitch_list() {
        return switch_list;
    }

    public void setSwitch_list(List<DeviceDetail> switch_list) {
        this.switch_list = switch_list;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
