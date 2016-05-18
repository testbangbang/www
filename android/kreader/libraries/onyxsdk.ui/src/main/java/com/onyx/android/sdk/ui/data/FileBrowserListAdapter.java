/**
 * 
 */
package com.onyx.android.sdk.ui.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.data.util.FileUtil;
import com.onyx.android.sdk.ui.OnyxGridView;

/**
 * @author dxwts
 * 
 */
public class FileBrowserListAdapter extends OnyxPagedAdapter
{

    private ArrayList<File> mItems = new ArrayList<File>();
    private String mHostPath;
    private LayoutInflater mInflater = null;
    private String mParentPath;

    public FileBrowserListAdapter(Context context, OnyxGridView gridView)
    {
        super(gridView);
        mInflater = LayoutInflater.from(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public ArrayList<File> getItems()
    {
        return mItems;
    }

    public void fillItems(File[] files, String path)
    {
        mHostPath = path;
        mItems.clear();
        for (File i : files) {
            if (!i.isHidden()) {
                mItems.add(i);
            }
        }
        this.sortByName();
        this.getPaginator().initializePageData(mItems.size(),
                this.getPaginator().getPageSize());

    }

    public String getHostPath()
    {
        return mHostPath;
    }

    public String getParentPath()
    {
        File file = new File(mHostPath);
        mParentPath = file.getParent();
        return mParentPath;
    }

    public void sortByName()
    {
        final Comparator<File> comp_name = new Comparator<File>()
        {

            @Override
            public int compare(File o1, File o2)
            {
                if (((File) o1).isDirectory()
                        && !((File) o2).isDirectory()) {
                    return -1;
                }
                else if (!((File) o1).isDirectory()
                        && ((File) o2).isDirectory()) {
                    return 1;
                }
                else if (((File) o1).isDirectory()
                        && ((File) o2).isDirectory()) {
                    return o1.getName()
                            .compareToIgnoreCase(o2.getName());
                }

                return o1.getName()
                        .compareToIgnoreCase(o2.getName());
            }
        };
        Collections.sort(mItems, comp_name);
        this.notifyDataSetChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View ret_view = null;

        int idx = this.getPaginator().getItemIndex(position, this.getPaginator().getPageIndex());
        File item_data = this.getItems().get(idx);

        ret_view = mInflater.inflate(R.layout.dialog_file_item, null);
        TextView textview_detail_item_name = (TextView) ret_view.findViewById(R.id.textview_detail_gridview_item_name);

        ImageView imageview_detail_item = (ImageView) ret_view.findViewById(R.id.imageview_detail_gridview_item_cover);

        RelativeLayout.LayoutParams imageview_params = new RelativeLayout.LayoutParams(this.getPageLayout()
                .getItemCurrentHeight(),
                this.getPageLayout().getItemCurrentHeight());
        imageview_detail_item.setLayoutParams(imageview_params);

        ret_view.setTag(item_data);

        int iconId = 0;
        if (item_data.isFile()) {
            if (FileUtil.getFileExtension(item_data) != "") {
                iconId = ret_view.getResources().getIdentifier(FileUtil.getFileExtension(item_data), "drawable",
                        ret_view.getContext().getPackageName());
                if (iconId == 0) {
                    iconId = ret_view.getResources().getIdentifier("unknown_document", "drawable",
                            ret_view.getContext().getPackageName());
                }
            }
            else {
                iconId = ret_view.getResources().getIdentifier("unknown_document", "drawable",
                        ret_view.getContext().getPackageName());
            }
        }
        else if (item_data.isDirectory()) {
            iconId = ret_view.getResources().getIdentifier("dir", "drawable",
                    ret_view.getContext().getPackageName());
        }
        else {
            iconId = ret_view.getResources().getIdentifier("unknown_document", "drawable",
                    ret_view.getContext().getPackageName());
        }

        imageview_detail_item.setImageResource(iconId);

        textview_detail_item_name.setText(item_data.getName());

        // warning!
        // repeatedly calling setLayoutParams() will cause a strange bug that
        // makes TextView's content disappearing,
        // having no clue about it
        if (ret_view.getLayoutParams() == null) {
            OnyxGridView.LayoutParams params = new OnyxGridView.LayoutParams(
                    this.getPageLayout().getItemCurrentWidth(),
                    this.getPageLayout().getItemCurrentHeight());
            ret_view.setLayoutParams(params);
        }
        else {
            ret_view.getLayoutParams().width = this.getPageLayout().getItemCurrentWidth();
            ret_view.getLayoutParams().height = this.getPageLayout().getItemCurrentHeight();
        }
        
        return ret_view;
    }
}
