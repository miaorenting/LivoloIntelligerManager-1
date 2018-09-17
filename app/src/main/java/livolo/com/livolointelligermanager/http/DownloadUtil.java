package livolo.com.livolointelligermanager.http;

import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import livolo.com.livolointelligermanager.config.Constants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/11.
 */

public class DownloadUtil {

    private static DownloadUtil downloadUtil;
    private final OkHttpClient okHttpClient;

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    private DownloadUtil() {
        okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

    }


    /**
     * @param url      下载连接
     * @param saveDir  储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public void download(final String url, final long size, final String saveDir, final OnDownloadListener listener, final long startPoints) {
        Request request = new Request.Builder()
                .url(url)
                .header("RANGE", "bytes=" + startPoints + "-" + size)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                listener.onDownloadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
//                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = isExistDir(saveDir);
                RandomAccessFile savedFile = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(savePath, Constants.fileName);
                    savedFile = new RandomAccessFile(file, "rw");
                    //全局可读可写
                    String[] command = {"chmod", "777", file.getPath()};
                    ProcessBuilder builder = new ProcessBuilder(command);
                    try {
                        builder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    savedFile.seek(startPoints);//跳过已经下载的字节
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        savedFile.write(buf, 0, len);
                        sum += len;
                        // 下载中
                        listener.onDownloading(sum);
                    }
                    savedFile.close();
                    // 下载完成
                    listener.onDownloadSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (savedFile != null)
                            savedFile.close();
//                        if (fos != null)
//                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    @NonNull
    public static String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess();

        /**
         * @param progress 下载进度
         */
        void onDownloading(long progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }
}
