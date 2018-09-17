package livolo.com.livolointelligermanager.mode;


import java.io.Serializable;
import java.util.List;

/**
 * Created by mayn on 2018/4/4.
 */

public class SenceDetail implements Serializable {

    private String scene_id;
    private String scene_name;
    private String picture_url;
    private String home_id;
    private int picture_index;
    private List<ButtonDetail> button_list;
    private boolean isSelected = false;

    public String getScene_id() {
        return scene_id;
    }

    public void setScene_id(String scene_id) {
        this.scene_id = scene_id;
    }

    public String getScene_name() {
        return scene_name;
    }

    public void setScene_name(String scene_name) {
        this.scene_name = scene_name;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public String getHome_id() {
        return home_id;
    }

    public void setHome_id(String home_id) {
        this.home_id = home_id;
    }

    public int getPicture_index() {
        return picture_index;
    }

    public void setPicture_index(int picture_index) {
        this.picture_index = picture_index;
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
}
