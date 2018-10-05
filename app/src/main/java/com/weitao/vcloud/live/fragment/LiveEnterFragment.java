package com.weitao.vcloud.live.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weitao.vcloud.live.R;
import com.weitao.vcloud.live.activity.EnterAudienceActivity;
import com.weitao.vcloud.live.activity.EnterLiveActivity;
import com.weitao.vcloud.live.base.BaseFragment;

/**
 * Created by zhukkun on 3/6/17.
 */
public class LiveEnterFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live_enter, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(R.id.live_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnterLiveActivity.start(getContext());
            }
        });

        findView(R.id.audience_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnterAudienceActivity.start(getContext());
            }
        });
    }

}
