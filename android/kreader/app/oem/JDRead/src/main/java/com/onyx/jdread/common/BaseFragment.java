package com.onyx.jdread.common;

import android.app.Fragment;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class BaseFragment extends Fragment {
    public boolean onKeyBack(){
        return false;
    }

    public boolean onKeyPageUp(){
        return false;
    }

    public boolean onKeyPageDown(){
        return false;
    }
}
