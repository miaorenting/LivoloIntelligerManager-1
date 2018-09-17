package livolo.com.livolointelligermanager.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import livolo.com.livolointelligermanager.R;
import livolo.com.livolointelligermanager.ui.MainActivity;

/**
 * Created by mayn on 2018/5/5.
 */

public class AddGatewayRemindFragment extends Fragment {

    private View rootView = null;// 缓存Fragment view

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_addgateway, null);
            //TODO

        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        container = (ViewGroup) rootView.getParent();
        if (container != null) {
            container.removeView(rootView);
        }
        return rootView;
    }
}
