package livolo.com.livolointelligermanager.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import livolo.com.livolointelligermanager.config.SysApplication;
import livolo.com.livolointelligermanager.mode.RoomDetail;

/**
 * Created by mayn on 2018/4/20.
 */

public class RoomDao {

    public static String TABLE_NAME = "roomdetail_table";
    public static String id = "_id";//本地数据库id编号
    public static String room_id = "room_id";//房间编号
    public static String picture_index = "picture_index";//图片类型，编号
    public static String room_name = "room_name";//科室名称
    public static String home_id = "home_id";//备注

    private MyDbHelper DB;
    private SQLiteDatabase db;

    public RoomDao() {
        this.DB = MyDbHelper.getInstance(SysApplication.getInstance());
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
     * 插入数据 单条
     *
     * @param info
     */
    public synchronized long insert(RoomDetail info) {
        long result = 0;
        if (info != null) {
            delete(info.getRoom_id());
            db = DB.getDb();
            ContentValues cv = new ContentValues();
            cv.put(room_id, info.getRoom_id() + "");
            cv.put(home_id, info.getHome_id() + "");
            cv.put(room_name, info.getRoom_name());
            cv.put(picture_index, info.getPicture_index());
            result = db.insert(TABLE_NAME, null, cv);
            db.close();
        }
        return result;
    }

    /**
     * 插入数据 数组
     *
     * @param list
     */
    public synchronized long insert(List<RoomDetail> list) {
        int len = list.size();
        long result = 0;
        if (len != 0) {
            deleteAll();
            db = DB.getDb();
            for (int i = 0; i < len; i++) {
                ContentValues cv = new ContentValues();
                RoomDetail info = list.get(i);
                cv.put(room_id, info.getRoom_id() + "");
                cv.put(home_id, info.getHome_id() + "");
                cv.put(room_name, info.getRoom_name());
                cv.put(picture_index, info.getPicture_index());
                result = db.insert(TABLE_NAME, null, cv);
            }
            db.close();
        }
        return result;
    }

    /**
     * 获取房间列表
     */
    public List<RoomDetail> getRoomList(String homeid) {
        db = DB.getDb();
        if (db != null) {
            Cursor cursor = null;
            try {
                cursor = db.query(TABLE_NAME, null, home_id + " = ? ",
                        new String[]{homeid}, null, null, null);
                List<RoomDetail> list = new ArrayList<RoomDetail>();
                if (cursor != null && cursor.getCount() > 0) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        RoomDetail detail = new RoomDetail();
                        detail.setRoom_id(cursor.getString(cursor.getColumnIndex(room_id)));
                        detail.setPicture_index(Integer.parseInt(cursor.getString(cursor.getColumnIndex(picture_index))));
                        detail.setRoom_name(cursor.getString(cursor.getColumnIndex(room_name)));
                        detail.setHome_id(cursor.getString(cursor.getColumnIndex(home_id)));
                        detail.setSwitch_list(null);
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
     * 修改房间参数
     */
    public int updateDoctorInfo(RoomDetail info) {
        db = DB.getDb();
        int result = 0;
        if (db != null) {
            ContentValues cv = new ContentValues();
            cv.put(room_name, info.getRoom_name());
            cv.put(picture_index, info.getPicture_index());
            result = db.update(TABLE_NAME, cv, room_id + " = ? ", new String[]{info.getRoom_id()});
            db.close();
        }
        return result;
    }

    //删除信息
    public synchronized boolean delete(String room_id) {
        db = DB.getDb();
        isDbLocked();
        if (db != null) {
            boolean result = db.delete(TABLE_NAME, this.room_id + " = ? ", new String[]{id}) > 0;
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
        }
        db.close();
    }
}
