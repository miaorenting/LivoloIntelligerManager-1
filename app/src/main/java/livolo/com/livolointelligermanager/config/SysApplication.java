package livolo.com.livolointelligermanager.config;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.tencent.bugly.crashreport.CrashReport;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import livolo.com.livolointelligermanager.ui.MainActivity;

/**
 * Created by mayn on 2018/4/2.
 */

public class SysApplication extends Application{

    public static Context context;
    public static Context getInstance(){
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //bugly 软件监控
        CrashReport.initCrashReport(getApplicationContext(), "334dd2a3e7", false);
        //二维码生成器和扫描器
        ZXingLibrary.initDisplayOpinion(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //解决64k方法数限制
        MultiDex.install(this);
    }

    public static void closeAllActivity(){
        if (MainActivity.getInstance()!=null){
            MainActivity.getInstance().finish();
        }
    }

//    attachBaseContext

}
