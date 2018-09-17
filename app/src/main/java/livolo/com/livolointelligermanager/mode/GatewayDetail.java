package livolo.com.livolointelligermanager.mode;

import java.io.Serializable;

/**
 * Created by mayn on 2018/4/28.
 */

public class GatewayDetail implements Serializable {

    /**gateway_id	String	是	网关ID
     gateway_name	String	是	网关名称
     picture_index	Integer	是	图片编号*/
    private String gateway_id;
    private String gateway_name;
    private int picture_index;
    private int full_flag;

    public String getGateway_id() {
        return gateway_id;
    }

    public void setGateway_id(String gateway_id) {
        this.gateway_id = gateway_id;
    }

    public String getGateway_name() {
        return gateway_name;
    }

    public void setGateway_name(String gateway_name) {
        this.gateway_name = gateway_name;
    }

    public int getPicture_index() {
        return picture_index;
    }

    public void setPicture_index(int picture_index) {
        this.picture_index = picture_index;
    }

    public int getFull_flag() {
        return full_flag;
    }

    public void setFull_flag(int full_flag) {
        this.full_flag = full_flag;
    }
}
