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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.UserInfoDetail;
import livolo.com.livolointelligermanager.util.ImageUtil;
import livolo.com.livolointelligermanager.util.StatusBarUtils;
import livolo.com.livolointelligermanager.view.ActionSheetDialog;
import livolo.com.livolointelligermanager.view.MultiShapeView;

/**
 * Created by mayn on 2018/7/7.
 */

public class UserInfoActivity extends BaseActivity implements Handler.Callback,View.OnClickListener{

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.top_right)
    TextView topRight;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.header)
    MultiShapeView header;
    @BindView(R.id.nikename)
    EditText nikename;
    @BindView(R.id.phone_number)
    TextView phoneNumber;
    @BindView(R.id.password)
    TextView password;

    private Handler mHandler;
    private HttpTools mHttp;
    private int PHOTO_REQUEST_GALLERY = 1001;
    private int PHOTO_REQUEST_CAMERA = 1002;
    private String headerStr ="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        ButterKnife.bind(this);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.midblue);
        mHandler = new Handler(this);
        mHttp = new HttpTools();
        mHttp.getUserInfo(mHandler);
        topTitle.setText(R.string.userinfo);
        backBtn.setOnClickListener(this);
        header.setOnClickListener(this);

    }


    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case Constants.GET_USERINFO_SUCCESS:
                //{"data":{"user_id":"5070215a84dd4cf28c27c41aafaf5f6b","phone":"18757705626","nick_name":"miao","resident_area":"中国"},"result_code":"000","result_msg":"success"}
                UserInfoDetail user = new Gson().fromJson(message.obj.toString(),UserInfoDetail.class);
                if (user.getNick_name()!=null){
                    nikename.setText(user.getNick_name());
                }
                if (user.getPhone()!=null){
                    phoneNumber.setText(user.getPhone());
                }
                if (user.getHead_image_url()!=null){
                    Glide.with(this).load(Constants.URL+user.getHead_image_url()).into(header);
                }
                break;
            case Constants.GET_USERINFO_FALSE:

                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.top_right:

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
}
