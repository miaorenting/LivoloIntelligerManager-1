package livolo.com.livolointelligermanager.mode;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mayn on 2018/4/12.
 */

public class HomeDetail implements Serializable {

    /**{home_address=七都岛湖中墅2号, home_name=温州, home_id=fcd744abc0894b10847fe929fe6a82bc}*/
    private String home_id;
    private String home_name;
    private String home_address;

    public String getHome_id() {
        return home_id;
    }

    public void setHome_id(String home_id) {
        this.home_id = home_id;
    }

    public String getHome_name() {
        return home_name;
    }

    public void setHome_name(String home_name) {
        this.home_name = home_name;
    }

    public String getHome_address() {
        return home_address;
    }

    public void setHome_address(String home_address) {
        this.home_address = home_address;
    }
}
