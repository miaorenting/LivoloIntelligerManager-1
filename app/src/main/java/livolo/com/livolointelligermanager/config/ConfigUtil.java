package livolo.com.livolointelligermanager.config;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.db.ButtonDao;
import livolo.com.livolointelligermanager.db.RoomDao;
import livolo.com.livolointelligermanager.db.DeviceDao;
import livolo.com.livolointelligermanager.mode.ButtonDetail;
import livolo.com.livolointelligermanager.mode.RoomDetail;
import livolo.com.livolointelligermanager.mode.DeviceDetail;

/**
 * Created by mayn on 2018/4/7.
 */

public class ConfigUtil {
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 校验邮箱
     *
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    /**
     * 获取 app的版本号
     */
    public static String getVersion() {
        PackageManager manager = SysApplication.getInstance().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(SysApplication.getInstance().getPackageName(), 0);
            return info.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(SysApplication.getInstance(), R.string.app_is_new, Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }
    }

    /**
     *设置UserId 上次登录的UserID */
    public static String getUserID() {
        return PreferenceManager.getDefaultSharedPreferences(
                SysApplication.getInstance()).getString("UserID", "");
    }

    public static void setUserID(String value) {
        PreferenceManager.getDefaultSharedPreferences(SysApplication.getInstance())
                .edit().putString("UserID", value).commit();
    }

    /**
     *设置HomeID 上次登录的homeid */
    public static String getHomeID() {
        return PreferenceManager.getDefaultSharedPreferences(
                SysApplication.getInstance()).getString("HomeID", "");
    }

    public static void setHomeID(String value) {
        PreferenceManager.getDefaultSharedPreferences(SysApplication.getInstance())
                .edit().putString("HomeID", value).commit();
    }

    /**
     *设置HomeName 上次登录的homeName */
    public static String getHomeName() {
        return PreferenceManager.getDefaultSharedPreferences(
                SysApplication.getInstance()).getString("HomeName", "");
    }

    public static void setHomeName(String value) {
        PreferenceManager.getDefaultSharedPreferences(SysApplication.getInstance())
                .edit().putString("HomeName", value).commit();
    }

    /**头像缓存*/
    public static String getHeaderStr() {
        return PreferenceManager.getDefaultSharedPreferences(
                SysApplication.getInstance()).getString("HeaderStr", "");
    }

    public static void setHeaderStr(String value) {
        PreferenceManager.getDefaultSharedPreferences(SysApplication.getInstance())
                .edit().putString("HeaderStr", value).commit();
    }

    /**用户名缓存*/
    public static String getUserName() {
        return PreferenceManager.getDefaultSharedPreferences(
                SysApplication.getInstance()).getString("HeaderStr", "");
    }

    public static void setUserName(String value) {
        PreferenceManager.getDefaultSharedPreferences(SysApplication.getInstance())
                .edit().putString("HeaderStr", value).commit();
    }

    /**
     *设置已经下载完毕的apk文件 version*/
    public static String getDownLoadApkVersion() {
        return PreferenceManager.getDefaultSharedPreferences(
                SysApplication.context).getString("apkVersion", "");
    }

    public static void setDownLoadApkVersion(String value) {
        PreferenceManager.getDefaultSharedPreferences(SysApplication.context)
                .edit().putString("apkVersion", value).commit();
    }

    public static int getRoomBlueIcon(int type) {
        int iconNum = 0;
        switch (type) {
            case 0:
                iconNum = R.mipmap.room_blue_0;
                break;
            case 1:
                iconNum = R.mipmap.room_blue_1;
                break;
            case 2:
                iconNum = R.mipmap.room_blue_2;
                break;
            case 3:
                iconNum = R.mipmap.room_blue_3;
                break;
            case 4:
                iconNum = R.mipmap.room_blue_4;
                break;
            case 5:
                iconNum = R.mipmap.room_blue_5;
                break;
            case 6:
                iconNum = R.mipmap.room_blue_6;
                break;
            case 7:
                iconNum = R.mipmap.room_blue_7;
                break;
            case 8:
                iconNum = R.mipmap.room_blue_8;
                break;
            case 9:
                iconNum = R.mipmap.room_blue_9;
                break;
            case 10:
                iconNum = R.mipmap.room_blue_10;
                break;
            case 11:
                iconNum = R.mipmap.room_blue_11;
                break;
            case 12:
                iconNum = R.mipmap.room_blue_12;
                break;
            case 13:
                iconNum = R.mipmap.room_blue_13;
                break;
            case 14:
                iconNum = R.mipmap.room_blue_14;
                break;
            default:
                iconNum = R.mipmap.room_blue_0;
                break;
        }
        return iconNum;
    }

    public static int getRoomBlackIcon(int type) {
        int iconNum = 0;
        switch (type) {
            case 0:
                iconNum = R.mipmap.room_black_0;
                break;
            case 1:
                iconNum = R.mipmap.room_black_1;
                break;
            case 2:
                iconNum = R.mipmap.room_black_2;
                break;
            case 3:
                iconNum = R.mipmap.room_black_3;
                break;
            case 4:
                iconNum = R.mipmap.room_black_4;
                break;
            case 5:
                iconNum = R.mipmap.room_black_5;
                break;
            case 6:
                iconNum = R.mipmap.room_black_6;
                break;
            case 7:
                iconNum = R.mipmap.room_black_7;
                break;
            case 8:
                iconNum = R.mipmap.room_black_8;
                break;
            case 9:
                iconNum = R.mipmap.room_black_9;
                break;
            case 10:
                iconNum = R.mipmap.room_black_10;
                break;
            case 11:
                iconNum = R.mipmap.room_black_11;
                break;
            case 12:
                iconNum = R.mipmap.room_black_12;
                break;
            case 13:
                iconNum = R.mipmap.room_black_13;
                break;
            case 14:
                iconNum = R.mipmap.room_black_14;
                break;
            default:
                iconNum = R.mipmap.room_black_0;
                break;
        }
        return iconNum;
    }

    public static int getSenceBlueIcon(int index) {
        int res = 0;
        switch (index) {
            case 0:
                res = R.mipmap.sence_blue_movie;
                break;
            case 1:
                res = R.mipmap.sence_blue_outhome;
                break;
            case 2:
                res = R.mipmap.sence_blue_party;
                break;
            case 3:
                res = R.mipmap.sence_blue_sleep;
                break;
            default:
                res = R.mipmap.sence_blue_outhome;//其他情景模式
                break;
        }
        return res;
    }

    // 房间列表 包括开关和按键
    public static List<RoomDetail> getRoomListData() {
        List<RoomDetail> list;
        DeviceDao mDeviceDao = new DeviceDao();
        ButtonDao mBtnDao = new ButtonDao();
        /**从本地数据库获取所有开关信息*/
        list = new RoomDao().getRoomList(Constants.HomeID);
        if (list!=null && list.size()>0){
            for (int i = 0;i<list.size();i++){
                list.get(i).setSwitch_list(mDeviceDao.getSwitchsByRoomID(list.get(i).getRoom_id()));
                List<DeviceDetail> switchs = list.get(i).getSwitch_list();
                if (switchs!=null && switchs.size()>0){
                    for (int m=0;m<switchs.size();m++){
                        switchs.get(m).setButton_list(mBtnDao.getButtonList(switchs.get(m).getSwitch_id()));
                    }
                }
            }
        }else{
            list = new ArrayList<>();
        }
        return list;
    }

    //获取房间列表 包括开关和按键
    public static List<RoomDetail> getJustRooms(){
        List<RoomDetail> list = null;
        /**从本地数据库获取所有开关信息*/
        list = new RoomDao().getRoomList(Constants.HomeID);
        return list;
    }

    /**补全从服务器获取的数据*/
    public static List<RoomDetail> setRoomDetails(List<RoomDetail> list){
        if (list!=null && list.size()>0){
            for (int i = 0;i<list.size();i++){
                RoomDetail room = list.get(i);
                room.setHome_id(Constants.HomeID);
                List<DeviceDetail> switchs = room.getSwitch_list();
                if (switchs!=null && switchs.size()>0){
                    for (int m = 0;m<switchs.size();m++){
                        switchs.get(m).setRoom_id(room.getRoom_id());
                        List<ButtonDetail> btns = switchs.get(m).getButton_list();
                        if (btns!=null && btns.size()>0){
                            for (int n = 0;n<btns.size();n++){
                                btns.get(n).setGateway_id(switchs.get(m).getGateway_id());
                                btns.get(n).setSwitch_id(switchs.get(m).getSwitch_id());
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    public static String getSSIDStr(String tempSSID) {
        String ssid;
        if (tempSSID.length() > 2 && tempSSID.charAt(0) == '"' && tempSSID.charAt(tempSSID.length() - 1) == '"') {
            ssid = tempSSID.substring(1, tempSSID.length() - 1);
        } else {
            ssid = tempSSID;
        }
        return ssid;
    }

    public static String getStringValue(String code){
      String str = "";
        switch (code){
            case "add_room":
                str = "";
                break;
        }

      return str;
    }

}
