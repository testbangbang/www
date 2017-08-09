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
import com.onyx.android.dr.event.EBookChildLibraryEvent;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehai on 17-7-11.
 */

public class EBookGroupAdapter extends PageRecyclerView.PageAdapter<EBookGroupAdapter.GroupItemViewHolder> implements View.OnClickListener {
    private List<Library> groups;
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.bookshelf_group_row);
    private int col = DRApplication.getInstance().getResources().getInteger(R.integer.bookshelf_group_col);
    private LibraryDataHolder dataHolder;
    private int item_row = DRApplication.getInstance().getResources().getInteger(R.integer.bookshelf_group_item_row);
    private int item_col = DRApplication.getInstance().getResources().getInteger(R.integer.bookshelf_group_item_col);

    public EBookGroupAdapter() {

    }

    public void setGroups(List<Library> groups) {
        this.groups = groups;
        notifyDataSetChanged();
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
    public EBookGroupAdapter.GroupItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(parent.getContext(), R.layout.item_bookshelf_group, null);
        return new GroupItemViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(final EBookGroupAdapter.GroupItemViewHolder holder, int position) {
        Library library = groups.get(position);
        holder.groupName.setText(library.getName());
        final EBookListAdapter listAdapter = new EBookListAdapter(getDataHolder());
        listAdapter.setRowAndCol(item_row, item_col);
        holder.pageRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        holder.pageRecycler.addItemDecoration(dividerItemDecoration);
        holder.pageRecycler.setAdapter(listAdapter);
        QueryArgs queryArgs = new QueryArgs();
        queryArgs = QueryBuilder.generateMetadataInQueryArgs(queryArgs);
        queryArgs.libraryUniqueId = library.getIdString();
        queryArgs.recursive = true;
        queryArgs.limit = item_col;
        final CloudContentListRequest listRequest = new CloudContentListRequest(queryArgs);
        DRApplication.getCloudStore().submitRequestToSingle(DRApplication.getInstance(), listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                QueryResult<Metadata> result = listRequest.getProductResult();
                if (result != null && result.list != null) {
                    Map<String, CloseableReference<Bitmap>> bitmaps = DataManagerHelper.loadCloudThumbnailBitmapsWithCache(DRApplication.getInstance(), DRApplication.getCloudStore().getCloudManager(), result.list);
                    listAdapter.updateContentView(getLibraryDataModel(result, bitmaps));
                }
                if (result == null || result.list == null || result.list.size() <= 0) {
                    holder.rootView.setVisibility(View.GONE);
                }
            }
        });
        holder.nextButton.setOnClickListener(this);
        holder.nextButton.setTag(position);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int position = (int) v.getTag();
        EBookChildLibraryEvent event = new EBookChildLibraryEvent(groups.get(position));
        EventBus.getDefault().post(event);
    }

    static class GroupItemViewHolder extends PageRecyclerView.ViewHolder {
        @Bind(R.id.group_name)
        TextView groupName;
        @Bind(R.id.next_button)
        ImageView nextButton;
        @Bind(R.id.page_recycler)
        SinglePageRecyclerView pageRecycler;
        View rootView;

        GroupItemViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }

    private LibraryDataModel getLibraryDataModel(QueryResult<Metadata> result, Map<String, CloseableReference<Bitmap>> map) {
        return LibraryViewInfo.buildLibraryDataModel(result, map);
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(DRApplication.getInstance());
            dataHolder.setCloudManager(DRApplication.getCloudStore().getCloudManager());
        }
        return dataHolder;
    }
}
