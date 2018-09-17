package livolo.com.livolointelligermanager.mode;

/**
 * Created by mayn on 2018/7/7.
 */

public class UserInfoDetail {

    private String nick_name;
    private String phone;
    private String email;
    private String resident_area;
    private String head_image_url;
    private int picture_index;

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getResident_area() {
        return resident_area;
    }

    public void setResident_area(String resident_area) {
        this.resident_area = resident_area;
    }

    public String getHead_image_url() {
        return head_image_url;
    }

    public void setHead_image_url(String head_image_url) {
        this.head_image_url = head_image_url;
    }

    public int getPicture_index() {
        return picture_index;
    }

    public void setPicture_index(int picture_index) {
        this.picture_index = picture_index;
    }
}
