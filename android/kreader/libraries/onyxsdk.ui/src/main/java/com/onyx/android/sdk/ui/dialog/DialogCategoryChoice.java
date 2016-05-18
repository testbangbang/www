package com.onyx.android.sdk.ui.dialog;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.Inflater;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.device.DeviceInfo;
import com.onyx.android.sdk.ui.OnyxProgressBar;
import com.onyx.android.sdk.ui.dialog.DialogLoading.onFinishReaderListener;
import com.onyx.android.sdk.ui.view.tagview.FlowLayout;
import com.onyx.android.sdk.ui.view.tagview.TagAdapter;
import com.onyx.android.sdk.ui.view.tagview.TagFlowLayout;

public class DialogCategoryChoice extends DialogBaseOnyx{
	
	private static final String TAG = DialogCategoryChoice.class.getSimpleName();
    private PowerManager.WakeLock mWakeLock = null;

    private TagFlowLayout mFlowLayout;
    private LayoutInflater mInflater;
    
    private int currentSelect = -1;
    
    private String[] mKeys;
    
    private String[] mVals = new String[]{"100", "200", "300", "400", "500", "600", "700",
    									  "800", "900", "1000", "1100", "1200", "1300"};

    public interface OnTagClickListener{
        public void OnTagClick(int position, String category);
    }
    
    public interface OnTagSelectListener{
    	public void OnItemSelect(int select, String category);
    }
    
    private OnTagClickListener mOnTagClickListener = new OnTagClickListener()
    {
        @Override
        public void OnTagClick(int position, String category){
            //do nothing
        }
    };
    
    public void SetOnFinishReaderListener(OnTagClickListener l){
    	mOnTagClickListener = l;
    }
    
    private OnTagSelectListener mOnTagSelectListener = new OnTagSelectListener()
    {
        @Override
        public void OnItemSelect(int select, String category){
            //do nothing
        	currentSelect = select;
        }
    };
    
    public void SetOnTagSelectListener(OnTagSelectListener l){
    	mOnTagSelectListener = l;
    }
    
    public DialogCategoryChoice(Context context, String msg, Object object)
    {
        super(context);        
        mInflater = LayoutInflater.from(context);
        mKeys = context.getResources().getStringArray(R.array.book_category_name);
        
        setContentView(R.layout.dialog_category_single_choose);
        initKeyValues(object);
        ((TextView)findViewById(R.id.textView_title)).setText(msg);
        mFlowLayout = (TagFlowLayout) findViewById(R.id.id_flowlayout);
        
        mFlowLayout.setAdapter(new TagAdapter<String>(mKeys){
            @Override
            public View getView(FlowLayout parent, int position, String s)
            {
                TextView tv = (TextView) mInflater.inflate(R.layout.dialog_category_single_choose_tv,
                        mFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });

        mFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener(){
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent)
            {
                mOnTagClickListener.OnTagClick(position, mVals[position]);
                return true;
            }
        });


        mFlowLayout.setOnSelectListener(new TagFlowLayout.OnSelectListener(){
            @Override
            public void onSelected(Set<Integer> selectPosSet)
            {      	       	
                if(selectPosSet.size()>0){
                	int category = (Integer) selectPosSet.toArray()[0];
                	mOnTagSelectListener.OnItemSelect(category, mVals[category]);
                	currentSelect = category;
                }               	
            }
        });
        
        setCanceledOnTouchOutside(false);

        this.setOnShowListener(new OnShowListener(){
            @Override
            public void onShow(DialogInterface dialog)
            {
                if(mWakeLock == null) {
                    mWakeLock = DeviceInfo.currentDevice.newWakeLock(getContext(), TAG);
                    mWakeLock.acquire();
                } else {
                    mWakeLock.acquire();
                }
            }
        });

        this.setOnDismissListener(new OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                if(mWakeLock != null) {
                    mWakeLock.release();
                    mWakeLock = null;
                }
            }
        });

    }
    
    private void initKeyValues(Object object)
    {
    	if(object == null)
    		return;
    	
    	if(object instanceof Map){
    		Map<String, String> maps = (Map<String, String>) object;
    		
    		if(maps!=null && maps.size()>0){
            	mKeys = new String[maps.size()];
            	mVals = new String[maps.size()];
            	
            	int index = 0;
            	Set<Entry<String, String>>  keyvaluESets = maps.entrySet();
            	for(Entry<String, String> entry : keyvaluESets){
            		mKeys[index]   = entry.getKey();
            		mVals[index++] = entry.getValue();
            	}      		
            }
    	}else if(object instanceof String[]){
    		String[] strings = (String[]) object;
    		
    		if(strings!=null && strings.length>0 && strings.length%2 ==0 ){
    			for(int i=0;i<strings.length/2;i++){
    				mKeys[i]   = strings[i];
            		mVals[i]   = strings[i+strings.length/2];
    			}
    		}
    	}   	
    }
    
    public int currentSelect()
    {
    	return this.currentSelect;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.cancel();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
