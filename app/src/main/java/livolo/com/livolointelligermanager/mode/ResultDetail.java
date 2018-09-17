package livolo.com.livolointelligermanager.mode;

/**
 * Created by mayn on 2018/4/18.
 */

public class ResultDetail {

    private String result_code;
    private String result_msg;
    private Object data;

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getResult_msg() {
        return result_msg;
    }

    public void setResult_msg(String result_msg) {
        this.result_msg = result_msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
