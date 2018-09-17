package livolo.com.livolointelligermanager.http;

import android.os.Handler;
import android.util.Log;

import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.mode.DeviceDetail;
import livolo.com.livolointelligermanager.util.MD5Tool;

/**
 * Created by Administrator on 2016/8/31.
 */
public class HttpTools {

    private HttpPost mHttpPost;
    private HttpGet mHttpGet;

    public HttpTools() {
        this.mHttpGet = new HttpGet();
        this.mHttpPost = new HttpPost();
    }

    public void getLoginCode(String username, Handler mHandler) {
        String[] keys = {"user_name"};
        String[] values = {username};
        mHttpGet.doGet(keys, values, mHandler, "app/user/login/verify_code", Constants.GET_LOGIN_CODE_SUCCESS, Constants.GET_LOGIN_CODE_FALSE, false);
    }

    /** 国内
     * password	String	是	密码（MD5加密）
     region_code	String	是	国际区号
     phone	String	是	手机号
     verify_code	String	是	验证码
     language	String	是	语言，cn:中文，en:外文
     nick_name	String	否	姓名
     resident_area	String	是	常驻地区
     */
    public void register(String password, String regionCode, String phone, String language, String header, String code,String nikename,String area, Handler mHandler) {
        String[] keys = {"password", "region_code", "phone", "language", "head_image_str", "verify_code","nick_name","region_id"};
        Object[] values = {MD5Tool.getMD5(password), regionCode, phone, language, header,code,nikename,area};
        mHttpPost.doPost(keys, values, mHandler, "app/user/login/register", Constants.REGISTER_SUCCESS, Constants.REGISTER_FALSE, false);
    }

    /** 国外
     nick_name	String	否	姓名
     email	String	是	邮箱
     password	String	是	密码（MD5加密）
     language	String	是	语言，cn:中文，en:外文
     resident_area	String	是	常驻地区
     */
    public void registerForaign(String nickname, String email, String password, String language, String header,String area, Handler mHandler) {
        String[] keys = {"nick_name", "password", "email", "head_image_str", "language","resident_area"};
        Object[] values = {nickname, MD5Tool.getMD5(password), email, header, language,area};
        mHttpPost.doPost(keys, values, mHandler, "app/user/login/register", Constants.REGISTER_SUCCESS, Constants.REGISTER_FALSE, false);
    }

    /**
     * 获取验证码 [GET]/app/user/login/phone/verify_code
     *phone	String	是	手机号
     region_code	String	是	国际区号
     type	Integer	是	0：用户注册，1：密码修改
     */
    public void getRegisterCode(String phone, String code,int type, Handler mHandler) {
        String[] keys = {"phone", "region_code","type"};
        String[] values = {phone, code,type+""};
        mHttpGet.doGet(keys, values, mHandler, "app/user/login/phone/verify_code", Constants.GET_CODE_BY_PHONE_SUCCESS, Constants.GET_CODE_BY_PHONE_FALSE, true);
    }

    /**
     * 用户登录
     * user_name	String	是	用户名
     * password	String	是	密码（MD5加密）
     */
    public void login(String username, String password, Handler mHandler) {
        String[] keys = {"user_name", "password"};
        Object[] values = {username, MD5Tool.getMD5(password)};
        mHttpPost.doPost(keys, values, mHandler, "app/user/login", Constants.LOGIN_SUCCESS, Constants.LOGIN_FALSE, false);
    }

    /**
     * 获取用户信息
     */
    public void getUserInfo(Handler mHandler) {
        String[] keys = {"user_id"};
        String[] values = {Constants.UserID};
        mHttpGet.doGet(keys, values, mHandler, "app/user", Constants.GET_USERINFO_SUCCESS, Constants.GET_USERINFO_FALSE, true);
    }

    /**
     * 密码修改
     * user_id	String	是	用户ID
     * old_password	String	是	旧密码（MD5加密）
     * password	String	是	新密码（MD5加密）
     */
    public void updatePasswordByOld(String oldPwd, String newPwd, Handler mHandler) {
        String[] keys = {"user_id", "old_password", "password"};
        Object[] values = {Constants.UserID, MD5Tool.getMD5(oldPwd), MD5Tool.getMD5(newPwd)};
        mHttpPost.doPost(keys, values, mHandler, "app/user/password/update", Constants.UPDATE_PASSWORD_SUCCESS, Constants.UPDATE_PASSWORD_FALSE, true);
    }

    /**
     * 根据验证码 密码修改 /app/user/login/password/login_phone
     * region_code String 国际区号
     * phone	String	是	手机号
     * verify_code	String	是	验证码
     * password String 密码修改
     */
    public void updatePassword(String teleCode, String phone, String code, String password, Handler mHandler) {
        String[] keys = {"region_code", "phone", "verify_code", "password"};
        Object[] values = {teleCode, phone, code, MD5Tool.getMD5(password)};
        mHttpPost.doPost(keys, values, mHandler, "app/user/login/password/login_phone", Constants.UPDATE_PASSWORD_SUCCESS, Constants.UPDATE_PASSWORD_FALSE, true);
    }

    /**
     * 修改密码获取验证码
     * phone	String	是	手机号
     */
    public void getVerifyCode(String phone, Handler mHandler) {
        String[] keys = {"phone"};
        String[] values = {phone};
        mHttpGet.doGet(keys, values, mHandler, "app/user/password/verify_code", Constants.GET_CODE_BY_PHONE_SUCCESS, Constants.GET_CODE_BY_PHONE_FALSE, true);
    }

    /**
     * 密码修改（手机号和验证码登录）
     * phone	String	是	手机号
     * verify_code	String	是	验证码
     */
    public void updateByPhone(String phone, String code, Handler mHandler) {
        String[] keys = {"phone", "verify_code"};
        Object[] values = {phone, code};
        mHttpPost.doPost(keys, values, mHandler, "app/user/password/login", Constants.GET_CODE_BY_PHONE_SUCCESS, Constants.GET_CODE_BY_PHONE_FALSE, true);
    }

    /**
     * 获取家庭列表
     * [GET]/app/home/list
     */
    public void getHomelist(String userid, Handler mHandler) {
        String[] keys = {"user_id"};
        String[] values = {userid};
        mHttpGet.doGet(keys, values, mHandler, "app/home/list", Constants.GET_HOMELIST_SUCCESS, Constants.GET_HOMELIST_FALSE, true);
    }

    /**
     * 新增家庭
     * home_name	String	是	家庭名称
     * home_address	String	是	家庭地址
     */
    public void addHome(String homename, String address, Handler mHandler) {
        String[] keys = {"home_name", "home_address"};
        Object[] values = {homename, address};
        mHttpPost.doPost(keys, values, mHandler, "app/home", Constants.ADD_HOME_SUCCESS, Constants.ADD_HOME_FALSE, true);
    }

    /**
     * 修改家庭信息
     * home_id	String	是	家庭ID
     * home_name	String	是	家庭名称
     * home_address	String	是	家庭地址
     */
    public void updateHome(String homeid, String homename, String address, Handler mHandler) {
        String[] keys = {"home_id", "home_name", "home_address"};
        Object[] values = {homeid, homename, address};
        mHttpPost.doPost(keys, values, mHandler, "app/home/update", Constants.UPDATE_HOME_SUCCESS, Constants.UPDATE_HOME_FALSE, true);
    }

    /**
     * 删除家庭
     * home_id	String	是	家庭ID
     * home_name	String	是	家庭名称
     * home_address	String	是	家庭地址
     */
    public void deleteHome(String homeid, String homename, String address, Handler mHandler) {
        String[] keys = {"home_id", "home_name", "home_address"};
        Object[] values = {homeid, homename, address};
        mHttpPost.doPost(keys, values, mHandler, "app/home/delete", Constants.DELETE_HOME_SUCCESS, Constants.DELETE_HOME_FALSE, true);
    }

    /**
     * 绑定家庭
     * home_id	String	是	家庭ID
     * user_id	String	是	用户ID
     */
    public void bindHome(String homeid, Handler mHandler) {
        String[] keys = {"home_id", "user_id"};
        Object[] values = {homeid, Constants.UserID};
        mHttpPost.doPost(keys, values, mHandler, "app/home/bind", Constants.BIND_HOME_SUCCESS, Constants.BIND_HOME_FALSE, true);
    }

    /**
     * 解除绑定
     * home_id	String	是	家庭ID
     * user_id	String	是	用户ID
     */
    public void unbindHome(String homeid, String userid, Handler mHandler) {
        String[] keys = {"home_id", "user_id"};
        Object[] values = {homeid, userid};
        mHttpPost.doPost(keys, values, mHandler, "app/home/unbind", Constants.UNBIND_HOME_SUCCESS, Constants.UNBIND_HOME_FALSE, true);
    }

    /**
     * 获取房间列表
     * home_id	String	是	家庭ID
     */
    public void getHomeSwitchBtn(String homeid, Handler mHandler) {
        String[] keys = {"home_id"};
        String[] values = {homeid};
        mHttpGet.doGet(keys, values, mHandler, "app/room/list", Constants.GET_HOME_SWITCH_BTN_SUCCESS, Constants.GET_HOME_SWITCH_BTN_FALSE, true);
    }

    /**
     * 获取网关列表
     * gateway/list
     * home_id	String	是	家庭ID
     */
    public void getGatewayList(String homeid, Handler mHandler) {
        String[] keys = {"home_id"};
        String[] values = {homeid};
        mHttpGet.doGet(keys, values, mHandler, "app/gateway/list", Constants.GET_GATEWAY_LIST_SUCCESS, Constants.GET_GATEWAY_LIST_FALSE, true);
    }

    /**
     * 添加房间
     * [POST]/app/room
     * home_id	String	是	家庭ID
     * room_name	String	是	房间名称
     * picture_index	Integer	是	图片编号
     * switch_list	List	否	开关列表
     */
    public void addRoom(String homeid, String roomName, int picIndex, List<DeviceDetail> list, Handler mHandler) {
        String[] keys = {"home_id", "room_name", "picture_index", "switch_list"};
        JSONArray array = new JSONArray();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                JSONObject object = new JSONObject();
                try {
                    object.put("switch_id", list.get(i).getSwitch_id());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(object);
            }
        }
        Object[] values = {homeid, roomName, picIndex, array};
        mHttpPost.doPost(keys, values, mHandler, "app/room", Constants.ADDROOM_SUCCESS, Constants.ADDROOM_FALSE, true);
    }

    /**
     * 修改房间
     * /app/room/update
     * room_id	String	是	房间ID
     * room_name	String	是	房间名称
     * picture_index	Integer	是	图片编号
     * switch_list	List	否	开关列表
     */
    public void updateRoom(String roomid, String roomName, int picIndex, List<DeviceDetail> list, Handler mHandler) {
        String[] keys = {"room_id", "room_name", "picture_index", "switch_list"};
        JSONArray array = new JSONArray();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("switch_id", list.get(i).getSwitch_id());
                    array.put(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Object[] values = {roomid, roomName, picIndex, array};
        mHttpPost.doPost(keys, values, mHandler, "app/room/update", Constants.UPDATE_ROOM_SUCCESS, Constants.UPDATE_ROOM_FALSE, true);
    }

    /**
     * 删除房间 /app/room/delete
     * room_ids	String	是	房间ID
     */
    public void deleteRooms(String[] roomids, Handler mHandler) {
        String[] keys = {"room_ids"};
        JSONArray array = new JSONArray();
        for (int i = 0; i < roomids.length; i++) {
            array.put(roomids[i]);
        }
        Object[] values = {array};
        mHttpPost.doPost(keys, values, mHandler, "app/room/delete", Constants.DELETE_ROOM_SUCCESS, Constants.DELETE_ROOM_FALSE, true);
    }

    /**
     * 添加网关 ：/app/gateway
     * gateway_id	String	是	网关ID
     * gateway_name	String	是	网关名称
     * picture_index	Integer	是	网关图片
     * home_id	String	是	家庭ID
     */
    public void addGateway(String gatewayId, String name, int index, String homeid, Handler mHandler) {
        String[] keys = {"gateway_id", "gateway_name", "picture_index", "home_id"};
        Object[] values = {gatewayId, name, index, homeid};
        mHttpPost.doPost(keys, values, mHandler, "app/gateway", Constants.ADD_GATEWAY_SUCCESS, Constants.ADD_GATEWAY_FALSE, true);
    }

    /**
     * 删除网关 /app/gateway/delete
     * gateway_ids	String	是	网关ID数组
     */
    public void deleteGateway(String gatewayid, Handler mHandler) {
        String[] keys = {"gateway_ids"};
        JSONArray array = new JSONArray();
        array.put(gatewayid);
        Object[] values = {array};
        mHttpPost.doPost(keys, values, mHandler, "app/gateway/delete", Constants.ADD_GATEWAY_SUCCESS, Constants.ADD_GATEWAY_FALSE, true);
    }

    /**
     * 发送添加开关指令 /switch/ready_add
     * home_id : 用户家的id
     */
    public void addSwitchBtn(String homeid, Handler mHandler) {
        String[] keys = {"home_id"};
        Object[] values = {homeid};
        mHttpPost.doPost(keys, values, mHandler, "app/switch/ready_add", Constants.ADD_SWITCH_SUCCESS, Constants.ADD_SWITCH_FALSE, true);
    }

    /**
     * 操作开关 /app/switch/operate
     * gateway_id	String	否	网关ID
     * button_status	Integer	否	0：关，100：开
     * id	String	是	按钮ID/房间ID/家庭ID/场景ID
     * type	Integer	是	0：按钮ID，1：房间ID，2：家庭ID，3：场景ID
     */
    public void controlSwicht(String gatewayID, int status, String id, int type, int R, int G, int B, Handler mHandler) {
        String[] keys = {"gateway_id", "button_status", "id", "type", "R", "G", "B"};
        Object[] values = {gatewayID, status, id, type, R, G, B};
        mHttpPost.doPost(keys, values, mHandler, "app/switch/operate", Constants.CONTROL_SWITCH_SUCCESS, Constants.CONTROL_SWITCH_FALSE, true);
    }

    public void controlSwichtBySeek(String gatewayID, int status, String id, int type, int R, int G, int B, Handler mHandler) {
        String[] keys = {"gateway_id", "button_status", "id", "type", "R", "G", "B"};
        Object[] values = {gatewayID, status, id, type, R, G, B};
        try {
            mHttpPost.doPost(keys, values, mHandler, "app/switch/operate", Constants.CONTROL_SWITCH_BY_SEEK_SUCCESS, Constants.CONTROL_SWITCH_BY_SEEK_FALSE, true);
        } catch (OutOfMemoryError outOfMemoryError) {
            Log.e("---------------", "-----------------------------------OOM");
        } catch (Exception e) {
            Log.e("---------------", "----------------------------------eeeee");
        }
    }


    /**
     * 修改开关
     * room_id	String	否	房间ID
     * switch_id	String	是	开关ID
     * switch_name	String	是	开关名称
     * picture_index	Integer	是	图片编号
     */
    public void updateSwitch(String roomId, String switchId, String switchName, int picIndex, Handler mHandler) {
        String[] keys = {"room_id", "switch_id", "switch_name", "picture_index"};
        Object[] values = {roomId, switchId, switchName, picIndex};
        mHttpPost.doPost(keys, values, mHandler, "app/switch/update", Constants.UPDATE_SWITCH_SUCCESS, Constants.UPDATE_SWITCH_FALSE, true);
    }

    /**
     * 删除开关
     * switch_list	List	是	开关ID列表
     * switch_id	String	是	开关ID
     * gateway_id	String	是	网关ID
     */
    public void deleteSwitch(DeviceDetail mSwitch, Handler mHandler) {
//        if (switchList!=null && switchList.size()>0){
        String[] keys = {"switch_list"};
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        try {
            object.put("switch_id", mSwitch.getSwitch_id());
            object.put("gateway_id", mSwitch.getGateway_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        array.put(object);
        Object[] values = {array};
        mHttpPost.doPost(keys, values, mHandler, "app/switch/delete", Constants.DELETE_SWITCH_SUCCESS, Constants.DELETE_SWITCH_FALSE, true);
//        }
    }

    /**
     * 修改开关里的按钮
     * [POST]/app/switch/button/update
     * button_id	String	是	按钮ID
     * button_name	String	是	按钮名称
     */
    public void updateButton(String btnid, String name, Handler mHandler) {
        String[] keys = {"button_id", "button_name"};
        Object[] values = {btnid, name};
        mHttpPost.doPost(keys, values, mHandler, "app/switch/button/update", Constants.UPDATE_BUTTON_NAME_SUCCESS, Constants.UPDATE_BUTTON_NAME_FALSE, true);
    }

    /**
     * 获取情景列表 scene/list
     * home_id	String	是	家庭ID
     */
    public void getSenceList(String homeid, Handler mHandler) {
        String[] keys = {"home_id"};
        String[] values = {homeid};
        mHttpGet.doGet(keys, values, mHandler, "app/scene/list", Constants.GET_SENCE_LIST_SUCCESS, Constants.GET_SENCE_LIST_FALSE, true);
    }

    /**
     * 添加情景模式 /app/scene
     * home_id	String	是	家庭ID
     * scene_name	String	是	场景名称
     * picture_index	Integer	是	图片编号（图片编号和图片64编码二选一）
     * button_list	List	否	按钮列表
     * picture_str	String	是	图片64编码（图片编号和图片64编码二选一）
     */
    public void addSence(String homeID, String sceneName, int picIndex, JSONArray array, String picStr, Handler mHandler) {
        String[] keys = {"home_id", "scene_name", "picture_index", "button_list", "picture_str"};
        Object[] values = {homeID, sceneName, picIndex, array, picStr};
        mHttpPost.doPost(keys, values, mHandler, "app/scene", Constants.ADD_SCENE_SUCCESS, Constants.ADD_SCENE_FALSE, true);
    }

    /**
     * 获取场景模式
     * scene_id	String	是	场景ID
     */
    public void getScene(String sceneid, Handler mHandler) {
        String[] keys = {"scene_id"};
        String[] values = {sceneid};
        mHttpGet.doGet(keys, values, mHandler, "app/scene", Constants.GET_SCENE_SUCCESS, Constants.GET_SCENE_SUCCESS, true);
    }

    /**
     * 修改情景模式 /app/scene/update
     * scene_id	String	是	场景ID
     * scene_name	String	是	场景名称
     * picture_index	Integer	是	图片编号（图片编号和图片地址二选一）
     * button_list	List	否	按钮列表
     * picture_url	String	是	图片地址（图片编号和图片地址二选一）
     */
    public void updateScene(String sceneId, String sceneName, int picIndex, JSONArray array, String picStr, Handler mHandler) {
        String[] keys = {"scene_id", "scene_name", "picture_index", "button_list", "picture_str"};
        Object[] values = {sceneId, sceneName, picIndex, array, picStr};
        mHttpPost.doPost(keys, values, mHandler, "app/scene/update", Constants.UPDATE_SCENE_SUCCESS, Constants.UPDATE_SCENE_FALSE, true);
    }

    /**
     * 删除场景
     * [POST]/app/scene/delete
     * 请求报文参数
     * 参数名	类型	必填	说明
     * scene_id	String	是	场景ID
     */
    public void deleteScene(String sceneId, Handler mHandler) {
        String[] keys = {"scene_ids"};
        JSONArray array = new JSONArray();
        array.put(sceneId);
        Object[] values = {array};
        mHttpPost.doPost(keys, values, mHandler, "app/scene/delete", Constants.DELETE_SCENE_SUCCESS, Constants.DELETE_SCENE_FALSE, true);
    }

    /**
     * 获取服务端app的版本
     * 参数名	类型	必填	说明
     * type	Integer	是	0：APP，1：固件
     * [GET]/app/setting/file_version
     */
    public void getServiceAppVersion(Handler mHandler) {
        String[] keys = {"type"};
        String[] values = {"0"};
        mHttpGet.doGet(keys, values, mHandler, "app/setting/file_version", Constants.GET_SERVICE_APP_VERSION_SUCCESS, Constants.GET_SERVICE_APP_VERSION_FALSE, true);
    }

    /**
     * 发送邮箱验证码  /app/user/login/email/verify_code
     */
    public void sendEmailCode(String email, Handler mHandler) {
        String[] keys = {"email"};
        String[] values = {email};
        mHttpGet.doGet(keys, values, mHandler, "app/user/login/email/verify_code", Constants.SEND_EMAIL_CODE_SUCCESS, Constants.SEND_EMAIL_CODE_FALSE, true);
    }

    /**
     * 通过邮箱修改密码 /app/user/login/password/login_email
     * email string
     * verify_code String
     * password String MD5加密
     */
    public void updatePasswordByEmail(String email, String code, String password, Handler mHandler) {
        String[] keys = {"email", "verify_code", "password"};
        Object[] values = {email, code, MD5Tool.getMD5(password)};
        mHttpPost.doPost(keys, values, mHandler, "app/user/login/password/login_email", Constants.UPDATE_PASSWORD_SUCCESS, Constants.UPDATE_PASSWORD_FALSE, true);
    }

    public void getAreas(Handler mHandler){
        mHttpGet.doGet(null, null, mHandler, "app/setting/region/list", Constants.GET_AREA_SUCCESS, Constants.GET_AREA_SUCCESS, true);
    }

    /**[GET]/app/setting/last_default_name

     请求报文参数
     参数名	类型	必填	说明
     home_id	String	是	家庭ID
     name_type	Integer	是	名称类型，0:家庭名称,1:网关名称,2:开关名称,3:房间名称,4:场景名称
     */
    public void getdefaultName(String homeid,int nameType,String name,Handler mHandler){
        String[] keys = {"home_id","name_type","default_name"};
        String[] values = {homeid,nameType+"",name};
        mHttpGet.doGet(keys, values, mHandler, "app/setting/last_default_name", Constants.GET_DEFAULT_NAME_SUCCESS, Constants.GET_DEFAULT_NAME_FALSE, true);
    }
    /** /app/user/delete
     * 参数名	类型	必填	说明
     user_id	String	是	家庭ID
     * */
    public void deleteUser(Handler mHandler){
        String[] keys = {"user_id"};
        Object[] values = {Constants.UserID};
        mHttpPost.doPost(keys, values, mHandler, "app/user/delete", Constants.DELETE_USER_SUCCESS, Constants.DELETE_USER_FALSE, true);
    }

    /**获取用户常住地址的服务器ip*/
    public void getUserIPAndPort(String username,Handler mHandler){
        String[] keys = {"user_name"};
        String[] values = {username};
        mHttpGet.doGet(keys, values, mHandler, "app/user/login/query",
                Constants.GET_IP_AND_PORT_SUCCESS, Constants.GET_IP_AND_PORT_FALSE, true);
    }

}
