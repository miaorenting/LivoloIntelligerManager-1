package livolo.com.livolointelligermanager.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import livolo.com.livolointelligermanager.config.SysApplication;
import livolo.com.livolointelligermanager.mode.ButtonDetail;

/**
 * Created by mayn on 2018/4/23.
 */

public class ButtonDao {

    public static String TABLE_NAME = "btndetail_table";
    public static String id = "_id";//本地数据库id编号
    public static String button_id = "button_id";//房间编号
    public static String button_name = "button_name";
    public static String button_type = "button_type";
    public static String button_status = "button_status";
    public static String switch_id = "switch_id";
    public static String gateway_id = "gateway_id";

    private MyDbHelper DB;
    private SQLiteDatabase db;

    public ButtonDao() {
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
    public synchronized long insert(ButtonDetail info) {
        long result = 0;
        if (info != null) {
            delete(info.getButton_id());
            db = DB.getDb();
            ContentValues cv = new ContentValues();
            cv.put(button_id, info.getButton_id());
            cv.put(button_name, info.getButton_name());
            cv.put(switch_id, info.getSwitch_id());
            cv.put(gateway_id, info.getGateway_id());
            cv.put(button_type, info.getButton_type());
            cv.put(button_status, info.getButton_status());
            result = db.insert(TABLE_NAME, null, cv);
        }
        db.close();
        return result;
    }

    /**
     * 插入数组数据
     *
     * @param list
     */
    public synchronized long insert(List<ButtonDetail> list) {
        long result = 0;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ContentValues cv = new ContentValues();
                ButtonDetail info = list.get(i);
                delete(info.getButton_id());
                db = DB.getDb();
                cv.put(button_id, info.getButton_id());
                cv.put(button_name, info.getButton_name());
                cv.put(switch_id, info.getSwitch_id());
                cv.put(gateway_id, info.getGateway_id());
                cv.put(button_type, info.getButton_type());
                cv.put(button_status, info.getButton_status());
                result = db.insert(TABLE_NAME, null, cv);
                db.close();
            }

        }
        return result;
    }

    /**
     * 根据开关id（switch_id）获取按键列表
     */
    public List<ButtonDetail> getButtonList(String switchid) {
        db = DB.getDb();
        if (db != null) {
            Cursor cursor = null;
            try {
                cursor = db.query(TABLE_NAME, null, switch_id + " = ? ",
                        new String[]{switchid}, null, null, null);
                List<ButtonDetail> list = new ArrayList<>();
                if (cursor != null && cursor.getCount() > 0) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        ButtonDetail detail = new ButtonDetail();
                        detail.setButton_id(cursor.getString(cursor.getColumnIndex(button_id)));
                        detail.setButton_status(Integer.parseInt(cursor.getString(cursor.getColumnIndex(button_status))));
                        detail.setButton_name(cursor.getString(cursor.getColumnIndex(button_name)));
                        detail.setSwitch_id(cursor.getString(cursor.getColumnIndex(switch_id)));
                        detail.setGateway_id(cursor.getString(cursor.getColumnIndex(gateway_id)));
                        detail.setButton_type(Integer.parseInt(cursor.getString(cursor.getColumnIndex(button_type))));
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
     * 修改按键参数
     */
    public int updateBtn(ButtonDetail info) {
        db = DB.getDb();
        int result = 0;
        if (db != null) {
            ContentValues cv = new ContentValues();
            cv.put(button_status,info.getButton_status());
            cv.put(gateway_id, info.getGateway_id());
            cv.put(button_name, info.getButton_name());
            result = db.update(TABLE_NAME, cv, button_id + " = ? ", new String[]{info.getButton_id()});
            cv.clear();
            db.close();
        }
        return result;
    }

    /***/

    public int updateBtnStatus(String switchId,int status){
        db = DB.getDb();
        int result = 0;
        if (db != null) {
            ContentValues cv = new ContentValues();
            cv.put(button_status, status);
            result = db.update(TABLE_NAME, cv, switch_id + " = ? ", new String[]{switchId});
            db.close();
        }
        return result;
    }

    /**
     * 根据
     */
    public synchronized boolean delete(String btnid) {
        db = DB.getDb();
        isDbLocked();
        if (db != null) {
            boolean result = db.delete(TABLE_NAME, this.button_id + " = ? ", new String[]{btnid}) > 0;
            db.close();
            return result;
        } else {
            return false;
        }
    }

    public synchronized void deleteAll() {
        db = DB.getDb();
        isDbLocked();
        if (db != null) {
            db.execSQL("delete from " + TABLE_NAME);
            db.close();
        }
    }
}
