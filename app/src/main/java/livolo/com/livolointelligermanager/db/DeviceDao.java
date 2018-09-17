package livolo.com.livolointelligermanager.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import livolo.com.livolointelligermanager.config.SysApplication;
import livolo.com.livolointelligermanager.mode.DeviceDetail;

/**
 * Created by mayn on 2018/4/20.
 */

public class DeviceDao {

    public static String TABLE_NAME = "device_table";
    public static String id = "_id";//本地数据库id编号
    public static String switch_id = "switch_id";//房间编号
    public static String switch_name = "switch_name";
    public static String picture_index = "picture_index";//图片类型，编号
    public static String gateway_id = "gateway_id";//科室名称
    public static String room_id = "room_id";//备注
    public static String switch_type = "switch_type";//类型0为控制开关

    private MyDbHelper DB;
    private SQLiteDatabase db;

    public DeviceDao() {
        this.DB = MyDbHelper.getInstance(SysApplication.context);
    }

    /**
     * 检测当前数据库是否被锁定
     */
    public void isDbLocked() {
        while (db.isDbLockedByOtherThreads() || db.isDbLockedByCurrentThread()) {
            Log.e("InspectionInfoCache", "db被锁定");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 插入单条数据
     *
     * @param info
     */
    public synchronized long insert(DeviceDetail info) {
        long result = 0;
        if (info != null) {
            delete(info.getSwitch_id());
            db = DB.getDb();
            ContentValues cv = new ContentValues();
            cv.put(room_id, info.getRoom_id() + "");
            cv.put(switch_id, info.getSwitch_id() + "");
            cv.put(switch_name, info.getSwitch_name());
            cv.put(gateway_id, info.getGateway_id());
            cv.put(picture_index, info.getPicture_index());
            cv.put(switch_type,info.getSwitch_type());
            result = db.insert(TABLE_NAME, null, cv);
            db.close();
        }
        return result;
    }

    /**
     * 插入数据，数组
     * @param list
     */
    public synchronized long insert(List<DeviceDetail> list) {
        int len = list.size();
        long result = 0;
        if (len != 0) {
            for (int i = 0; i < len; i++) {
                ContentValues cv = new ContentValues();
                DeviceDetail info = list.get(i);
                delete(info.getSwitch_id());
                db = DB.getDb();
                cv.put(room_id, info.getRoom_id() + "");
                cv.put(switch_id, info.getSwitch_id() + "");
                cv.put(switch_name, info.getSwitch_name());
                cv.put(gateway_id, info.getGateway_id());
                cv.put(picture_index, info.getPicture_index());
                cv.put(switch_type,info.getSwitch_type());
                result = db.insert(TABLE_NAME, null, cv);
                db.close();
            }

        }
        return result;
    }


    /**
     * 获取房间列表
     */
    public List<DeviceDetail> getSwitchsByRoomID(String roomid) {
        db = DB.getDb();
        if (db != null) {
            Cursor cursor = null;
            try {
                cursor = db.query(TABLE_NAME, null, room_id + " = ? ",
                        new String[]{roomid}, null, null, null);
                List<DeviceDetail> list = new ArrayList<>();
                if (cursor != null && cursor.getCount() > 0) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        DeviceDetail detail = new DeviceDetail();
                        detail.setRoom_id(cursor.getString(cursor.getColumnIndex(room_id)));
                        detail.setPicture_index(cursor.getInt(cursor.getColumnIndex(picture_index)));
                        detail.setSwitch_name(cursor.getString(cursor.getColumnIndex(switch_name)));
                        detail.setSwitch_id(cursor.getString(cursor.getColumnIndex(switch_id)));
                        detail.setGateway_id(cursor.getString(cursor.getColumnIndex(gateway_id)));
                        detail.setSwitch_type(cursor.getInt(cursor.getColumnIndex(switch_type)));
                        detail.setButton_list(null);
                        list.add(detail);
                    }
                    return list;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
                if (db != null)
                    db.close();
            }
        }
        return null;
    }

    /**
     * 修改数据库中开关信息
     */
    public synchronized int updateSwitch(String switchID,String roomID,String switchName,String gatewayID) {
        db = DB.getDb();
        int result = 0;
        if (db != null) {
            ContentValues cv = new ContentValues();
            if (!TextUtils.isEmpty(roomID)){
                cv.put(room_id, roomID);
            }
            if (!TextUtils.isEmpty(switchName)){
                cv.put(switch_name, switchName);
            }
            if (!TextUtils.isEmpty(gatewayID)){
                cv.put(gateway_id, gatewayID);
            }
            result = db.update(TABLE_NAME, cv, switch_id + " = ? ", new String[]{switchID});
            db.close();
        }
        return result;
    }

    public synchronized void deleteAll() {
        db = DB.getDb();
        isDbLocked();
        if (db != null) {
            db.execSQL("delete from " + TABLE_NAME);
            db.close();
        }
    }

    //删除信息
    public synchronized boolean delete(String switch_id) {
        db = DB.getDb();
        isDbLocked();
        if (db != null) {
            boolean result = db.delete(TABLE_NAME, this.switch_id + " = ? ", new String[]{switch_id}) > 0;
            db.close();
            return result;
        } else {
            return false;
        }
    }

}
