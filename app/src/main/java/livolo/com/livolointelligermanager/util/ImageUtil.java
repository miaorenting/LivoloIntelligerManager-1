package livolo.com.livolointelligermanager.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

import livolo.com.livolointelligermanager.config.SysApplication;

/**
 * Created by Administrator on 2016/11/7.
 */

public class ImageUtil {

    /**base64转为bitmap */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**get Bitmap from picUrl*/
    public static Bitmap getBitmapFromUrl(String url){

        Bitmap bitmap = null;
        try {

            bitmap = Glide.with(SysApplication.getInstance()).asBitmap().load(url).into(20,20).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 图片转成string
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap){
        if (bitmap!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] appicon = baos.toByteArray();// 转为byte数组
            return Base64Tool.encode(appicon);
        }else{
            return "";
        }

    }
    /**
     * 图像drawable 转 bitmap*/
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     * @param filePath
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;  //只返回图片的大小信息
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

//    public static void glideImageHeader(Context context, String url, ImageView view, RequestOptions options){
//        if (TextUtils.isEmpty(url)){
//            Glide.with(context).load(R.mipmap.imageload_empty).into(view);
//        }else{
//            if (options == null){
//                options = new RequestOptions();
//            }
//            options.placeholder(R.mipmap.imageload_error);
//            Glide.with(context).load(url).apply(options).into(view);
//        }
//    }


}
