package com.onyx.android.edu.base;

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by ming on 16/6/24.
 * 全局类
 */
public class Global {

    private static Global ourInstance;

    public static Global getInstance() {
        synchronized (Global.class){
            if (ourInstance == null){
                ourInstance = new Global();
            }
        }
        return ourInstance;
    }

    public static Context mContext;

    public void init(Context context){
        mContext = context;
//        initDataProvider(context);
    }

    private void initDataProvider(final Context context) {
        FlowConfig.Builder builder = new FlowConfig.Builder(context);
        FlowManager.init(builder.build());
    }

    public static Context getContext(){
        return mContext;
    }

    public void loadTestData(){
    }

}
