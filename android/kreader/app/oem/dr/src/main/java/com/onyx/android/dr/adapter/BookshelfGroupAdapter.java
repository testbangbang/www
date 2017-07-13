package com.onyx.android.dr.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.event.ToBookshelfV2Event;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.CloudDataProvider;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehai on 17-7-11.
 */

public class BookshelfGroupAdapter extends PageRecyclerView.PageAdapter<BookshelfGroupAdapter.GroupItemViewHolder> implements View.OnClickListener {
    private Context context;
    private List<QueryArgs> groups;
    private int row = 3;
    private int col = 1;
    private LibraryDataHolder dataHolder;
    private int mode;

    public BookshelfGroupAdapter(Context context) {
        this.context = context;
    }

    public void setGroups(int mode, List<QueryArgs> groups) {
        this.mode = mode;
        this.groups = groups;
    }

    @Override
    public int getRowCount() {
        return row;
    }

    @Override
    public int getColumnCount() {
        return col;
    }

    @Override
    public int getDataCount() {
        return groups == null ? 0 : groups.size();
    }

    @Override
    public BookshelfGroupAdapter.GroupItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(context, R.layout.item_bookshelf_group, null);
        return new GroupItemViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(BookshelfGroupAdapter.GroupItemViewHolder holder, int position) {
        QueryArgs queryArgs = groups.get(position);
        CloudDataProvider localDataProvider = new CloudDataProvider(DRApplication.getCloudStore().getCloudConf());
        QueryResult<Metadata> queryResult = localDataProvider.findMetadataResultByQueryArgs(context, queryArgs);
        holder.groupName.setText(queryArgs.query);
        Map<String, CloseableReference<Bitmap>> bitmaps = DataManagerHelper.loadCloudThumbnailBitmapsWithCache(context, DRApplication.getCloudStore().getCloudManager(), queryResult.list);
        BookListAdapter listAdapter = new BookListAdapter(context, getDataHolder());
        holder.pageRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        holder.pageRecycler.addItemDecoration(dividerItemDecoration);
        holder.pageRecycler.setAdapter(listAdapter);
        listAdapter.updateContentView(getLibraryDataModel(queryResult, bitmaps));
        holder.nextButton.setOnClickListener(this);
        holder.nextButton.setTag(position);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int position = (int) v.getTag();
        ToBookshelfV2Event event = new ToBookshelfV2Event();
        QueryArgs queryArgs = groups.get(position);
        event.setTitle(queryArgs.query);
        event.setArgs(queryArgs);
        EventBus.getDefault().post(event);
    }

    static class GroupItemViewHolder extends PageRecyclerView.ViewHolder {
        @Bind(R.id.group_name)
        TextView groupName;
        @Bind(R.id.next_button)
        ImageView nextButton;
        @Bind(R.id.page_recycler)
        PageRecyclerView pageRecycler;

        GroupItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private LibraryDataModel getLibraryDataModel(QueryResult<Metadata> result, Map<String, CloseableReference<Bitmap>> map) {
        return LibraryViewInfo.buildLibraryDataModel(result, map);
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(context);
            dataHolder.setCloudManager(DRApplication.getCloudStore().getCloudManager());
        }
        return dataHolder;
    }
}
