package livolo.com.livolointelligermanager.config;

/**
 * Created by mayn on 2018/4/2.
 */

public class Constants {
    public static String URL = "http://47.98.102.223:8080/";
    public static String mqttHost = "";
    public static String HomeID = "";
    public static String UserID = "";
    public static String Token = "";
    public static final String FileNamePath = "downloadFile";
    public static String fileName = "LivoloAPK.apk";

    public static String BRODCASE_TOTALBTN_REFRESH = "action_mian_totlebtn_refresh";//仅仅刷新主界面总开关
    public static String BRODCASE_MAIN_ADAPTER = "action_mian_adapter";//刷新主界面的房间列表
    public static String BRODCASE_MAIN_REFRESH = "action_mian_refresh";//刷新主界面的总开关和房间列表
    public static String BRODCASE_MAIN_REFRESH_ALL_DATA = "action_main_refresh_all_data";//重新从服务端获取数据，并刷新界面
    public static String BRODCASE_REFRESH_ALL_ACTIVITY = "action_refresh_all_activity";//刷新说有界面
    public static String BRODCASE_MAIN_SENCE = "action_mian_sence";//刷新主界面的情景列表
    public static String BRODCASE_ADD_GATEWAY = "action_add_gateway_result";//添加网关 mqtt推送添加结果
    public static String BRODCASE_ADD_SWITCH = "action_add_switch_result";//添加开关 mqtt推送添加结果
    public static String BRODCASE_ADD_SWITCH_FAILED = "action_add_switch_failed";//添加开关失败 mqtt推送
    public static String BRODCASE_REFRESH_AFTERDB = "action_refresh_after_db_refresh";//数据库更新完毕后更新其他页面的数据
    public static String BRODCASE_SYSEXIT = "action_sys_exit";//通知程序出错，退出程序

    public static final int LOGIN_SUCCESS = 0;
    public static final int LOGIN_FALSE = LOGIN_SUCCESS+1;
    public static final int GET_LOGIN_CODE_SUCCESS = LOGIN_FALSE+1;
    public static final int GET_LOGIN_CODE_FALSE = GET_LOGIN_CODE_SUCCESS+1;
    public static final int ADD_GATEWAY_SUCCESS = GET_LOGIN_CODE_FALSE+1;
    public static final int ADD_GATEWAY_FALSE = ADD_GATEWAY_SUCCESS+1;
    public static final int ADD_SWITCH_SUCCESS = ADD_GATEWAY_FALSE+1;
    public static final int ADD_SWITCH_FALSE = ADD_SWITCH_SUCCESS+1;
    public static final int CONTROL_SWITCH_SUCCESS = ADD_SWITCH_FALSE+1;
    public static final int CONTROL_SWITCH_FALSE = CONTROL_SWITCH_SUCCESS+1;
    public static final int GET_ADDSWITCH_STATUS_SUCCESS = CONTROL_SWITCH_FALSE+1;
    public static final int GET_ADDSWITCH_STATUS_FALSE =GET_ADDSWITCH_STATUS_SUCCESS+1;
    public static final int GET_RESULT_FOR_SWITCH = GET_ADDSWITCH_STATUS_FALSE +1;
    public static final int GET_USERINFO_SUCCESS = GET_RESULT_FOR_SWITCH+1;
    public static final int GET_USERINFO_FALSE = GET_USERINFO_SUCCESS+1;
    public static final int UPDATE_PASSWORD_SUCCESS = GET_USERINFO_FALSE+1;
    public static final int UPDATE_PASSWORD_FALSE = UPDATE_PASSWORD_SUCCESS+1;
    public static final int GET_CODE_BY_PHONE_SUCCESS = UPDATE_PASSWORD_FALSE+1;
    public static final int GET_CODE_BY_PHONE_FALSE = GET_CODE_BY_PHONE_SUCCESS+1;
    public static final int GET_HOMELIST_SUCCESS = GET_CODE_BY_PHONE_FALSE+1;
    public static final int GET_HOMELIST_FALSE = GET_HOMELIST_SUCCESS+1;
    public static final int ADD_HOME_SUCCESS = GET_HOMELIST_FALSE+1;
    public static final int ADD_HOME_FALSE = ADD_HOME_SUCCESS+1;
    public static final int UPDATE_HOME_SUCCESS = ADD_HOME_FALSE+1;
    public static final int UPDATE_HOME_FALSE = UPDATE_HOME_SUCCESS+1;
    public static final int DELETE_HOME_SUCCESS = UPDATE_HOME_FALSE+1;
    public static final int DELETE_HOME_FALSE = DELETE_HOME_SUCCESS+1;
    public static final int BIND_HOME_SUCCESS = DELETE_HOME_FALSE+1;
    public static final int BIND_HOME_FALSE = BIND_HOME_SUCCESS+1;
    public static final int UNBIND_HOME_SUCCESS = BIND_HOME_FALSE+1;
    public static final int UNBIND_HOME_FALSE = UNBIND_HOME_SUCCESS+1;
    public static final int GET_HOME_SWITCH_BTN_SUCCESS = UNBIND_HOME_FALSE+1;
    public static final int GET_HOME_SWITCH_BTN_FALSE = GET_HOME_SWITCH_BTN_SUCCESS+1;
    public static final int GET_GATEWAY_LIST_SUCCESS = GET_HOME_SWITCH_BTN_FALSE+1;
    public static final int GET_GATEWAY_LIST_FALSE = GET_GATEWAY_LIST_SUCCESS+1;
    public static final int GET_SENCE_LIST_SUCCESS = GET_GATEWAY_LIST_FALSE+1;
    public static final int GET_SENCE_LIST_FALSE = GET_SENCE_LIST_SUCCESS+1;
    public static final int GET_NETWORD_RESULT = GET_SENCE_LIST_FALSE+1;
    public static final int UPDATE_BUTTON_NAME_SUCCESS = GET_NETWORD_RESULT+1;
    public static final int UPDATE_BUTTON_NAME_FALSE = UPDATE_BUTTON_NAME_SUCCESS+1;
    public static final int ADD_SCENE_SUCCESS = UPDATE_BUTTON_NAME_FALSE+1;
    public static final int ADD_SCENE_FALSE = ADD_SCENE_SUCCESS+1;
    public static final int GET_SCENE_SUCCESS = ADD_SCENE_FALSE+1;
    public static final int GET_SCENE_FALSE = GET_SCENE_SUCCESS+1;
    public static final int UPDATE_SCENE_SUCCESS = GET_SCENE_FALSE+1;
    public static final int UPDATE_SCENE_FALSE = UPDATE_SCENE_SUCCESS+1;
    public static final int REGISTER_SUCCESS = UPDATE_SCENE_FALSE+1;
    public static final int REGISTER_FALSE = REGISTER_SUCCESS+1;
    public static final int ADDROOM_SUCCESS = REGISTER_FALSE+1;
    public static final int ADDROOM_FALSE = ADDROOM_SUCCESS+1;
    public static final int UPDATE_ROOM_SUCCESS = ADDROOM_FALSE+1;
    public static final int UPDATE_ROOM_FALSE = UPDATE_ROOM_SUCCESS+1;
    public static final int DELETE_ROOM_SUCCESS = UPDATE_ROOM_FALSE+1;
    public static final int DELETE_ROOM_FALSE = DELETE_ROOM_SUCCESS+1;
    public static final int DELETE_GATEWAY_SUCCESS = DELETE_ROOM_FALSE+1;
    public static final int DELETE_GATEWAY_FALSE = DELETE_GATEWAY_SUCCESS+1;
    public static final int UPDATE_SWITCH_SUCCESS = DELETE_GATEWAY_FALSE+1;
    public static final int UPDATE_SWITCH_FALSE = UPDATE_SWITCH_SUCCESS+1;
    public static final int DELETE_SWITCH_SUCCESS = UPDATE_SWITCH_FALSE+1;
    public static final int DELETE_SWITCH_FALSE = DELETE_SWITCH_SUCCESS+1;
    public static final int DELETE_SCENE_SUCCESS = DELETE_SWITCH_FALSE+1;
    public static final int DELETE_SCENE_FALSE = DELETE_SCENE_SUCCESS+1;
    public static final int GET_SERVICE_APP_VERSION_SUCCESS = DELETE_SCENE_FALSE+1;
    public static final int GET_SERVICE_APP_VERSION_FALSE = GET_SERVICE_APP_VERSION_SUCCESS+1;
    public static final int SEND_EMAIL_CODE_SUCCESS = GET_SERVICE_APP_VERSION_FALSE+1;
    public static final int SEND_EMAIL_CODE_FALSE = SEND_EMAIL_CODE_SUCCESS+1;
    public static final int CONTROL_SWITCH_BY_SEEK_SUCCESS = SEND_EMAIL_CODE_FALSE+1;
    public static final int CONTROL_SWITCH_BY_SEEK_FALSE = CONTROL_SWITCH_BY_SEEK_SUCCESS+1;
    public static final int GET_AREA_SUCCESS = CONTROL_SWITCH_BY_SEEK_FALSE+1;
    public static final int GET_AREA_FALSE = GET_AREA_SUCCESS+1;
    public static final int GET_DEFAULT_NAME_SUCCESS = GET_AREA_FALSE+1;
    public static final int GET_DEFAULT_NAME_FALSE = GET_DEFAULT_NAME_SUCCESS+1;
    public static final int DELETE_USER_SUCCESS = GET_DEFAULT_NAME_FALSE+1;
    public static final int DELETE_USER_FALSE = DELETE_USER_SUCCESS+1;
    public static final int GET_IP_AND_PORT_SUCCESS = DELETE_USER_FALSE+1;
    public static final int GET_IP_AND_PORT_FALSE = GET_IP_AND_PORT_SUCCESS+1;

}
