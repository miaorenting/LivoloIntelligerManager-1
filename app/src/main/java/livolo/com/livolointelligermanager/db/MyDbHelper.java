package livolo.com.livolointelligermanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mayn on 2018/4/20.
 * 创建数据库管理器
 */

public class MyDbHelper {

    private static MyDbHelper myDbHelper; // MyDbHelper实例对象
    private DatabaseHelper dbHelper; // SQLiteOpenHelper实例对象
    private SQLiteDatabase mDb; // 数据库实例对象
    private static final String DATABASE_NAME = "livolo_intelliger_db"; // 数据库名
    private static final int DATABASE_VERSION = 1; // 数据库版本号

    /**
     * 房间数据库
     */
    private static final String SQLROOM = "create table "
            + RoomDao.TABLE_NAME + " ( "
            + RoomDao.id + " integer primary key autoincrement ,"
            + RoomDao.room_id + " text ,"
            + RoomDao.picture_index + " integer ,"
            + RoomDao.home_id + " text ,"
            + RoomDao.room_name + " text )";

    /**
     * 开关数据库
     */
    private static final String SQLSWITCH = "create table "
            + DeviceDao.TABLE_NAME + " ( "
            + DeviceDao.id + " integer primary key autoincrement ,"
            + DeviceDao.room_id + " text ,"
            + DeviceDao.picture_index + " integer ,"
            + DeviceDao.switch_type + " integer ,"
            + DeviceDao.switch_id + " text ,"
            + DeviceDao.gateway_id + " text ,"
            + DeviceDao.switch_name + " text )";

    /**
     * 按键数据库
     */
    private static final String SQLBUTTON = "create table "
            + ButtonDao.TABLE_NAME + " ( "
            + ButtonDao.id + " integer primary key autoincrement ,"
            + ButtonDao.button_id + " text ,"
            + ButtonDao.button_name + " text ,"
            + ButtonDao.button_type + " integer ,"
            + ButtonDao.button_status + " integer ,"
            + ButtonDao.switch_id + " text ,"
            + ButtonDao.gateway_id + " text )";

    private MyDbHelper(Context ctx) {
        dbHelper = new DatabaseHelper(ctx);
    }

    public static synchronized void destory() {
        if (myDbHelper != null) {
            myDbHelper.close();
            myDbHelper = null;
        }
    }

    public static synchronized MyDbHelper getInstance(Context conetxt) {
        if (myDbHelper == null)
            synchronized (MyDbHelper.class) {
                myDbHelper = new MyDbHelper(conetxt);
            }
        return myDbHelper;
    }

    private class DatabaseHelper extends SQLiteOpenHelper { // 数据库辅助类
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        /**
         * 第一次创建数据库时需要执行的操作创建的新表
         */
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQLROOM);
            db.execSQL(SQLSWITCH);
            db.execSQL(SQLBUTTON);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(" drop table if exists " + RoomDao.TABLE_NAME);
            db.execSQL(" drop table if exists " + DeviceDao.TABLE_NAME);
            db.execSQL(" drop table if exists " + ButtonDao.TABLE_NAME);
            onCreate(db);
        }
    }

    // 获取db实例
    public SQLiteDatabase getDb() {
        try {
            mDb = dbHelper.getWritableDatabase();
            return mDb;
        } catch (IllegalStateException ie) {
            ie.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        mDb = null;
        dbHelper.close();
        dbHelper = null;
    }

}
