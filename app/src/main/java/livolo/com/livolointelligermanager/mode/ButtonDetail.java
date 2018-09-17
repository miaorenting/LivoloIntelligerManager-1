package livolo.com.livolointelligermanager.mode;

import java.io.Serializable;

/**
 * Created by mayn on 2018/4/6.
 */

public class ButtonDetail implements Serializable{

    private String button_id;
    private String button_name;
    private int button_type;
    private int button_status;
    private String switch_id;
    private String gateway_id;
    private boolean isSelected = false;

    public String getButton_id() {
        return button_id;
    }

    public void setButton_id(String button_id) {
        this.button_id = button_id;
    }

    public String getButton_name() {
        return button_name;
    }

    public void setButton_name(String button_name) {
        this.button_name = button_name;
    }

    public int getButton_type() {
        return button_type;
    }

    public void setButton_type(int button_type) {
        this.button_type = button_type;
    }

    public int getButton_status() {
        return button_status;
    }

    public void setButton_status(int button_status) {
        this.button_status = button_status;
    }

    public String getSwitch_id() {
        return switch_id;
    }

    public void setSwitch_id(String switch_id) {
        this.switch_id = switch_id;
    }

    public String getGateway_id() {
        return gateway_id;
    }

    public void setGateway_id(String gateway_id) {
        this.gateway_id = gateway_id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
