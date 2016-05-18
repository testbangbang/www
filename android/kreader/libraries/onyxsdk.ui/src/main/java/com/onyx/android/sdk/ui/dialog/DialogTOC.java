package com.onyx.android.sdk.ui.dialog;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.reader.TOCItem;
import com.onyx.android.sdk.ui.GridViewTOC;
import com.onyx.android.sdk.ui.data.GridViewTOCAdapter;

public class DialogTOC extends DialogBaseOnyx
{
    public interface onGoToPageListener
    {
        public void onGoToPage(TOCItem item);
    }

    private onGoToPageListener mOnGoToPageListener = new onGoToPageListener()
    {
        
        @Override
        public void onGoToPage(TOCItem item)
        {
            //do nothing
        }
    };

    public void setOnGoToPageListener(onGoToPageListener l)
    {
        mOnGoToPageListener = l;
    }
    
    private ArrayList<TOCItem> mTocItems = null;

    public DialogTOC(Context context, ArrayList<TOCItem> tocItems)
    {
        super(context, R.style.full_screen_dialog);

        setContentView(R.layout.dialog_toc);
        
        mTocItems = tocItems;

        Button button_exit = (Button) findViewById(R.id.button_exit);
        GridViewTOC gridViewTOC = (GridViewTOC) findViewById(R.id.gridview_toc);

        button_exit.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                DialogTOC.this.dismiss();
            }
        });

        gridViewTOC.getGridView().setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                DialogTOC.this.dismiss();
                TOCItem item = (TOCItem) view.getTag();
                mOnGoToPageListener.onGoToPage(item);
            }
        });

        if(mTocItems == null) {
            ArrayList<TOCItem> TOCItems = new ArrayList<TOCItem>();
            for (int i = 0; i < 100; i++) {
                TOCItem item = new TOCItem("title"+i, i, "tag"+i);
                TOCItems.add(item);
            }
            mTocItems = TOCItems;
        }

        GridViewTOCAdapter adapter = new GridViewTOCAdapter(context, gridViewTOC.getGridView(), mTocItems);
        gridViewTOC.getGridView().setAdapter(adapter);
    }

}
