package livolo.com.livolointelligermanager.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.adaper.AreaAdapter;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.AreaDetail;
import livolo.com.livolointelligermanager.util.DialogUtil;
import livolo.com.livolointelligermanager.util.EditLimitUtil;
import livolo.com.livolointelligermanager.util.ImageUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.view.ActionSheetDialog;
import livolo.com.livolointelligermanager.view.MultiShapeView;

/**
 * Created by mayn on 2018/6/14.
 */

public class RegisterForForeignActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {

    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    private final static int TimerResult = 1000;

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_right)
    TextView topRight;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.header)
    MultiShapeView header;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.email_code)
    EditText emailCode;
    @BindView(R.id.code_right)
    TextView codeRight;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.next_btn)
    Button nextBtn;
    @BindView(R.id.area_spinner)
    Spinner areaSpinner;

    private int PHOTO_REQUEST_GALLERY = 1001;
    private int PHOTO_REQUEST_CAMERA = 1002;
    private String headerStr;
    private Handler mHandler;
    private HttpTools mHttp;
    private SweetAlertDialog mDialog;
    private String areaCode;
    private Timer timer;
    private List<AreaDetail> areas;
    private int timeCount = 120;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_foreign);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.midblue);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        mHttp = new HttpTools();
        mHttp.getAreas(mHandler);
        initLayout();
    }

    private void initLayout() {
        topTitle.setText(R.string.register);
        backBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        header.setOnClickListener(this);
        codeRight.setOnClickListener(this);

        EditLimitUtil.setEditLimit(username,20,this);
        EditLimitUtil.setEditLimit(email,50,this);
        EditLimitUtil.setEditLimit(password,20,this);
    }

    private void setSprinnerLayout(){
        AreaAdapter areaAdapter = new AreaAdapter(this,areas);
        areaSpinner.setAdapter(areaAdapter);
        areaSpinner.setSelection(0);
        areaCode = areas.get(0).getRegion_id();
        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                areaCode = areas.get(i).getRegion_id();
                Constants.URL = "http://"+areas.get(i).getIp()+":8080";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * 校验邮箱
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.next_btn:
                if (!(EditLimitUtil.isLimit(email,50,this) && EditLimitUtil.isLimit(username,20,this))){
                    break;
                }

                final String emailText = email.getText().toString();
                if (TextUtils.isEmpty(emailText)) {
                    DialogUtil.createEmptyMsgDialog(this, R.string.email_cannot_be_empty);
                    return;
                }
                if (!isEmail(emailText)) {
                    DialogUtil.createEmptyMsgDialog(this, R.string.email_format_error);
                    return;
                }
                final String pwdText = password.getText().toString();
                if (TextUtils.isEmpty(pwdText)) {
                    DialogUtil.createEmptyMsgDialog(this, R.string.pwd_cannot_be_empty);
                    return;
                }
                mDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getResources().getString(R.string.be_need_commit))
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                mDialog.setTitleText(getResources().getString(R.string.committing)).showCancelButton(false).changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                mHttp.registerForaign(username.getText().toString(),emailText,pwdText,"en",headerStr,areaCode,mHandler);//(username.getText().toString(), emailText, pwdText, "en", headerStr, mHandler);
                            }
                        })
                        .setCancelButton(R.string.cancel, null);
                mDialog.show();

                break;
            case R.id.header:
                new ActionSheetDialog(this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem(getResources().getString(R.string.take_photo), ActionSheetDialog.SheetItemColor.Blue
                                , new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        getImageFromCamera();
                                    }
                                })
                        .addSheetItem(getResources().getString(R.string.photo_album), ActionSheetDialog.SheetItemColor.Blue
                                , new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        getImageFromAlbum();
                                    }
                                }).show();
                break;

            case R.id.code_right:
                String emailStr = email.getText().toString();
                if (TextUtils.isEmpty(emailStr)) {
                    DialogUtil.createEmptyMsgDialog(this, R.string.email_cannot_be_empty);
                    break;
                }
                if (!ConfigUtil.isEmail(emailStr)) {
                    DialogUtil.createEmptyMsgDialog(this, R.string.email_format_error);
                    break;
                }
                mHttp.sendEmailCode(emailStr, mHandler);
                break;
        }
    }

    /**
     * 从相册获取图片
     */
    private void getImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /**
     * 从照相机获取图片
     */
    private void getImageFromCamera() {
        String state = Environment.getExternalStorageState(); //拿到sdcard是否可用的状态码
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //文件夹
            startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
        } else {
            //提示请确认sd卡在
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText(getResources().getString(R.string.sd_unable)).setConfirmClickListener(null).setConfirmText("确定").show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            if (requestCode == PHOTO_REQUEST_GALLERY) {
                ContentResolver resolver = getContentResolver();
                //照片的原始资源地址
                Uri originalUri = data.getData();
                //使用ContentProvider通过URI获取原始图片
                bitmap = ImageUtil.getSmallBitmap(ImageUtil.getRealPathFromURI(this, originalUri), 200, 200);
                headerStr = ImageUtil.bitmapToBase64(bitmap);
            } else if (requestCode == PHOTO_REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                bitmap = (Bitmap) bundle.get("data");
                headerStr = ImageUtil.bitmapToBase64(bitmap);
            }
            Glide.with(this).load(bitmap).apply(new RequestOptions().error(R.mipmap.header_black)).into(header);
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case Constants.REGISTER_SUCCESS:
                mDialog.showCancelButton(false)
                        .setTitleText(getResources().getString(R.string.register_success))
                        .setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                ConfigUtil.setUserName(email.getText().toString());
                                finish();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                ConfigUtil.setUserName(email.getText().toString());
                break;
            case Constants.REGISTER_FALSE:
                mDialog.setTitleText(message.obj.toString()).showCancelButton(false).setConfirmButton(R.string.sure, null).changeAlertType(SweetAlertDialog.ERROR_TYPE);
                break;
            case TimerResult:
                if (timeCount <= 0) {
                    stopTimer();
                    codeRight.setText(R.string.code_btn);
                } else {
                    codeRight.setText(timeCount + "");
                }
                break;
            case Constants.GET_AREA_SUCCESS:
                //{"ip":"47.98.102.223","region_id":"86","region_name":"中国"},
                areas = new Gson().fromJson(message.obj.toString(), new TypeToken<List<AreaDetail>>() {
                }.getType());
                setSprinnerLayout();
                break;
            case Constants.GET_AREA_FALSE:
                SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                dialog.setTitleText(message.obj.toString());
                dialog.setConfirmButton(R.string.sure, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                    }
                });
                dialog.show();
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        if (mDialog != null) {
            mDialog.cancel();
        }
    }

    private void startTimer() {
        timer = new Timer();
        timeCount = 120;
        codeRight.setClickable(false);
        codeRight.setBackgroundResource(R.drawable.text_grav_bg);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeCount--;
                mHandler.sendEmptyMessage(TimerResult);
            }
        }, 0, 1000);
    }

    // 停止定时器
    private void stopTimer() {
        codeRight.setClickable(true);
        codeRight.setBackgroundResource(R.drawable.click_bg);
        if (timer != null) {
            timer.cancel();
            // 一定设置为null，否则定时器不会被回收
            timer = null;
        }
    }
}
