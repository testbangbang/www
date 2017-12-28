package com.onyx.jdread;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.FragmentHelpBinding;

/**
 * Created by hehai on 17-12-28.
 */

public class HelpFragment extends BaseFragment {

    private FragmentHelpBinding helpBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helpBinding = FragmentHelpBinding.inflate(inflater, container, false);
        HelpModel helpModel = new HelpModel();
        helpBinding.helpTitle.setTitleModel(helpModel.titleBarModel);
        helpBinding.setHelpModel(helpModel);
        return helpBinding.getRoot();
    }
}
