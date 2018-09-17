package livolo.com.livolointelligermanager.mode;

/**
 * Created by mayn on 2018/7/5.
 */

public class AreaDetail {

    /**region_id	String	是	地区ID
     region_name	String	是	地区名称

     "ip":"47.98.102.223","region_id":"86","region_name":"中国"*/


    private String ip;
    private String region_id;
    private String region_name;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public String getRegion_name() {
        return region_name;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }
}
