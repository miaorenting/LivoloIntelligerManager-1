package livolo.com.livolointelligermanager.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;

import livolo.com.livolointelligermanager.config.Constants;

/**
 * Created by Administrator on 2017/8/12.
 */

public class DownLoadService extends Service {

    private DownloadManager downloadManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("url");

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        IntentFilter filter = new IntentFilter();
        //下载完成的时候获得一个系统通知（notification）,注册一个广播接受者来接收ACTION_DOWNLOAD_COMPLETE广播
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        //ACTION_NOTIFICATION_CLICKED 当用户从通知栏点击了一个下载项目或者从Downloads app点击可一个
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        registerReceiver(receiver, filter);


        download(url);
        return super.onStartCommand(intent, flags, startId);
    }
    /**DownloadManager.Request用来请求一个下载，DownloadManager.Query用来查询下载信息
     * downloadId用于后面查询下载信息*/
    public void download(String url){
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        //TODO wifi时直接下载，非wifi时提示并选择是否继续下载
//        request.addRequestHeader(String header, String value)//添加请求下载的网络链接的http头
        //定制Download Manager Notifications的样式
        request.setTitle("下载标题");
        request.setDescription("下载描述内容");
        //表示下载完成后显示通知栏
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);//是否只在wifi情况下下载
        request.setMimeType("application/cn.trinea.download.file");

        //在外部存储中指定一个任意的保存位置的方法
        String fileName = Environment.getExternalStorageDirectory() + "/downloadFile/" + Constants.fileName;
        request.setDestinationUri(Uri.fromFile(new File(fileName)));

        //指定存储文件的路径是应用在外部存储中的专用文件夹的方法
        File folder = new File(Constants.FileNamePath);
        if (!(folder.exists() && folder.isDirectory())){
            folder.mkdirs();
        }
//        request.setDestinationInExternalFilesDir(this,Environment.DIRECTORY_DOWNLOADS, Constants.fileName);

        long downloadId = downloadManager.enqueue(request);//执行下载，downloadId用于后面查询下载信息

    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            Log.e("-----download-----",intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)?"ACTION_DOWNLOAD_COMPLETE":"ACTION_NOTIFICATION_CLICKED");

//            if (myDownloadReference == reference) {
//
//            }
        }
    };
}
