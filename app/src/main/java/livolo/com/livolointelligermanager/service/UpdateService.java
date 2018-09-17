package livolo.com.livolointelligermanager.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.config.SysApplication;
import livolo.com.livolointelligermanager.http.DownloadUtil;
import livolo.com.livolointelligermanager.ui.LoginActivity;
import livolo.com.livolointelligermanager.ui.MainActivity;
import livolo.com.livolointelligermanager.ui.SetManagerActivity;

/**
 * Created by Administrator on 2017/1/4.
 */

public class UpdateService extends Service implements Handler.Callback {

    private static final int START_LOAD = 1000;
    private static final int SET_PROGRESS = 1001;
    private static final int COLS_PROGRESS = 1002;

//    private long loadSize = 0;
    private String versionCode;//表示当前需要下载的apk的版本号
    private String url;
    private long total = 0;

    private int curProsses = 0;
    private NotificationManager manager;
    private Notification.Builder builder;
    private Handler mHandle;

//    private boolean isDownFaile = true;//是否下载失败 true表示下载失败 false表示没有下载失败
    private boolean isMain = true;//service.putExtra("isMain",false);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandle = new Handler(this);
        url = intent.getStringExtra("url");
        isMain = intent.getBooleanExtra("isMain", true);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        setNotice();
        startLoad();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 添加通知栏：下载通知
     */
    private void setNotice() {
        builder = new Notification.Builder(this);
        //初始化NotifactionManager
        builder.setContentTitle(getResources().getString(R.string.app_name))
                .setSmallIcon(R.mipmap.app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon))
                .setContentText(getResources().getString(R.string.app_name));
        builder.setProgress(100, 0, false);
        manager.notify(0, builder.build());
        startLoad();
    }

    private void startLoad() {
        if (fileIsExists()) {
            //删除原来的文件//TODO
            deleteApk();
        }
//        if (!isDownFaile) {
//            installApk();
//        } else {
            /**开始下载*/
            getContentLength(url);
//            downloadApk(url, 0);
//        }
    }

    /**
     * 开始安装
     */
    private void installApk() {

        String fileName = Environment.getExternalStorageDirectory() + "/downloadFile/" + Constants.fileName;
        File file = new File(fileName);
//        Log.e()
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(SysApplication.getInstance(),
                    "livolo.com.livolointelligermanager.fileProvider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        if (SysApplication.getInstance().getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            SysApplication.getInstance().startActivity(intent);
        }
    }

    /**
     * 获取下载长度
     * @param downloadUrl
     * @return
     */
    public void getContentLength(final String downloadUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlcon = null;
                try {
                    URL url = new URL(downloadUrl);
                    urlcon = (HttpURLConnection) url.openConnection();
                    int result = urlcon.getContentLength();
                    total = result;
                    mHandle.sendEmptyMessage(START_LOAD);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void downloadApk(final String url, final long startPosition) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadUtil.get().download(url, total, "downloadFile", new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess() {
                        //下载完成
//                        builder.setContentText("下载完成！");
//                        manager.notify(0, builder.build());

                        /**保存已经下载的apk文件版本*/
                        ConfigUtil.setDownLoadApkVersion(versionCode);
                        /***/
                        setNoticeAndInstall();
                    }

                    @Override
                    public void onDownloading(long result) {
                        //下载进度
//                        loadSize = result;
                        int progress = (int) (result * 100 / total);
                        if (progress > curProsses) {
                            curProsses = progress;
                            Log.e("downloadFile", "下载进度值:" + progress);
                            mHandle.sendEmptyMessage(SET_PROGRESS);
                        }
                    }

                    @Override
                    public void onDownloadFailed() {
                        //下载失败
//                        if (isDownFaile) {
//                            downloadApk(url, loadSize);
                            Log.e("downloadFile", "下载失败！loadSize:");
//                            builder.setContentText("下载失败！");
//                            manager.notify(0, builder.build());
                            SetManagerActivity.updateUpdatePropress(curProsses, false);
//                        }
                    }
                }, 0);
            }
        }).start();

    }

    private void setNoticeAndInstall() {
        installApk();
    }


    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case START_LOAD:
                downloadApk(url, 0);
                if (isMain) {
                    LoginActivity.startUpdatePropress();
                } else {
                    SetManagerActivity.startUpdatePropress();
                }
                break;
            case SET_PROGRESS:
                if (isMain) {
                    LoginActivity.updateUpdatePropress(curProsses, true);
                } else {
                    SetManagerActivity.updateUpdatePropress(curProsses, true);
                }
                break;
            case COLS_PROGRESS:
                if (isMain) {
                    LoginActivity.updateUpdatePropress(curProsses, false);
                } else {
                    SetManagerActivity.updateUpdatePropress(curProsses, false);
                }
                break;
        }

        return false;
    }

    /**
     * 检查 文件夹中是否已经存在apk
     */
    public static boolean fileIsExists() {
        String fileName = Environment.getExternalStorageDirectory() + "/downloadFile/" + Constants.fileName;
        try {
            File f = new File(fileName);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 删除单个文件 apk
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteApk() {
        String fileName = Environment.getExternalStorageDirectory() + "/downloadFile/" + Constants.fileName;
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("delete------","删除单个文件" + fileName + "成功！");
                return true;
            } else {
                Log.e("delete------","删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            Log.e("delete------","删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }


}
