package livolo.com.livolointelligermanager.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/11/10.
 */

public class MD5Tool {
    /**MD5大写加密*/
    public static String getMD5Large(String str){
        return getMD5(str).toUpperCase();
    }
    /**MD5小写加密*/
    public static String getMD5Small(String str){
        return getMD5(str).toLowerCase();
    }
    /**MD5加密*/
    public static String getMD5(String info){
        try{
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();
            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++)
            {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                }
                else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }
            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

}
