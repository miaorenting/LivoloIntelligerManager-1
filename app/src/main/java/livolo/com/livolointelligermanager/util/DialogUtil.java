package livolo.com.livolointelligermanager.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import cn.pedant.SweetAlert.Constants;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.SysApplication;
import livolo.com.livolointelligermanager.ui.LoginActivity;
import livolo.com.livolointelligermanager.ui.MainActivity;

/**
 * Created by mayn on 2018/4/23.
 */

public class DialogUtil {

    public static void createEmptyMsgDialog(Context context, int resStr) {
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(context.getResources().getString(resStr))
                .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void createEorreMsgDialog(Context context, String resStr) {
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(resStr)
                .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void createEmptyMsgDialog(Context context, String resStr) {
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(resStr)
                .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void addEditTextByDialog(Context context, int layoutTitle, final View.OnClickListener listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.dialog_addroom, null);
        View titleView = inflater.inflate(layoutTitle, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.AlertDialog).setCustomTitle(titleView).setView(layout).show();
        final TextView et = layout.findViewById(R.id.etname);
        Button sureBtn = layout.findViewById(R.id.btn_sure);
        Button cancelBtn = layout.findViewById(R.id.btn_cancal);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        sureBtn.setOnClickListener(listener);
    }

    public static void createExitDialog(int res,Context context) {
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(context.getResources().getString(res))
                .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        System.exit(0);
                        sweetAlertDialog.cancel();
                    }
                });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
