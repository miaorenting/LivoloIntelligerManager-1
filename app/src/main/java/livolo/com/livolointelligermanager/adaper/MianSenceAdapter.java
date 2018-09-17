package livolo.com.livolointelligermanager.adaper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.config.ConfigUtil;
import livolo.com.livolointelligermanager.config.Constants;
import livolo.com.livolointelligermanager.http.HttpTools;
import livolo.com.livolointelligermanager.mode.SenceDetail;
import livolo.com.livolointelligermanager.ui.AddSceneActivity;
import livolo.com.livolointelligermanager.ui.MainActivity;
import livolo.com.livolointelligermanager.ui.SenceActivity;


/**
 * Created by mayn on 2018/4/4.
 */

public class MianSenceAdapter extends RecyclerView.Adapter<MianSenceAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<SenceDetail> mDatas;
    private Context context;
    private int width = 0;
    private HttpTools mHttp;
    private Handler mHandler;

    public MianSenceAdapter(Context context, List<SenceDetail> datats,int width,HttpTools mHttp,Handler mhandler) {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
        mHandler = mhandler;
        this.mHttp = mHttp;
        this.width = width;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_main_sence, parent, false);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(width,(width*115/100));
        view.setLayoutParams(lp);
        ViewHolder holder = new ViewHolder(view);
        holder.mImg = view.findViewById(R.id.icon);
        LinearLayout.LayoutParams imageLp = new LinearLayout.LayoutParams(width*6/10,width*6/10);
        holder.mImg.setLayoutParams(imageLp);
        holder.mTxt = view.findViewById(R.id.name);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final SenceDetail detail = mDatas.get(position);
        if (TextUtils.isEmpty(detail.getPicture_url())) {
            int res = ConfigUtil.getSenceBlueIcon(detail.getPicture_index());
            Glide.with(context).load(res).into(holder.mImg);
        } else {
            Glide.with(context).load(Constants.URL+detail.getPicture_url()).into(holder.mImg);
        }
        holder.mTxt.setText(detail.getScene_name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,detail.getScene_name()+context.getResources().getString(R.string.scene_open),Toast.LENGTH_SHORT).show();
                mHttp.controlSwicht("",0,detail.getScene_id(),3,0,0,0,mHandler);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                senceDetail = (SenceDetail) getIntent().getSerializableExtra("sence");
                Intent intent = new Intent();
                intent.setClass(context,AddSceneActivity.class);
                intent.putExtra("sence",detail);
                context.startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }
        ImageView mImg;
        TextView mTxt;
    }

}
