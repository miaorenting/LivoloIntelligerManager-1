package livolo.com.livolointelligermanager.receiver;

import android.content.Context;
import android.content.Intent;

import livolo.com.livolointelligermanager.config.Constants;

/**
 * Created by mayn on 2018/4/25.
 */

public class BrodcaseTool {

    /**仅仅刷新主界面总开关*/
    public static void sendMainBtnRefresh(Context context){
        Intent intent = new Intent();
        intent.setAction(Constants.BRODCASE_TOTALBTN_REFRESH);//
        context.sendBroadcast(intent);
    }
    /**刷新主界面的列表*/
    public static void sendMainAdapterRefresh(Context context){
        Intent intent = new Intent();
        intent.setAction(Constants.BRODCASE_MAIN_ADAPTER);
        context.sendBroadcast(intent);
    }
    /** 从服务端获取数据 刷新本地缓存数据 */
    public static void sendMainDataRefresh(Context context){
        Intent intent = new Intent();
        intent.setAction(Constants.BRODCASE_MAIN_REFRESH_ALL_DATA);
        context.sendBroadcast(intent);
    }

    /**通知各个活动的Activity 刷新界面数据并刷新界面*/
    public static void sendAllActivityRefresh(Context context){
        Intent intent = new Intent();
        intent.setAction(Constants.BRODCASE_REFRESH_ALL_ACTIVITY);
        context.sendBroadcast(intent);
    }
    //添加网关 mqtt返回信息 播报
    public static void sendAddGateway(Context context){
        Intent intent = new Intent();
        intent.setAction(Constants.BRODCASE_ADD_GATEWAY);
        context.sendBroadcast(intent);
    }
    //添加开关 mqtt返回信息 播报
    public static void sendAddSwitch(Context context, String switchId,String switchName){
        Intent intent = new Intent();
        intent.setAction(Constants.BRODCASE_ADD_SWITCH);
        intent.putExtra("switchid",switchId);
        intent.putExtra("switchname",switchName);
        context.sendBroadcast(intent);
    }
    //添加开关失败 mqtt 返回信息
    public static void sendAddSwitchFailed(Context context,String msg){
        Intent intent = new Intent();
        intent.setAction(Constants.BRODCASE_ADD_SWITCH_FAILED);
        intent.putExtra("resultMsg",msg);
        context.sendBroadcast(intent);
    }

    public static void sendRefreshAfterDB(Context context){
        Intent intent = new Intent();
        intent.setAction(Constants.BRODCASE_REFRESH_AFTERDB);
        context.sendBroadcast(intent);
    }

    public static void sendSysExit(Context context,int msg){
        Intent intent = new Intent();
        intent.setAction(Constants.BRODCASE_SYSEXIT);
        intent.putExtra("msg",msg);
        context.sendBroadcast(intent);
    }

}
