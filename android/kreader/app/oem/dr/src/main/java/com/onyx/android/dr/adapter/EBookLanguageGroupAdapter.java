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
import com.onyx.android.dr.event.EBookListEvent;
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
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.avos.avoscloud.Messages.OpType.result;
import static com.onyx.android.sdk.data.model.v2.EduAccount_Table.groups;
import static com.onyx.android.sdk.data.model.v2.EduAccount_Table.library;

/**
 * Created by hehai on 17-7-11.
 */

public class EBookLanguageGroupAdapter extends PageRecyclerView.PageAdapter<EBookLanguageGroupAdapter.GroupItemViewHolder> implements View.OnClickListener {
    private Context context;
    private Map<String, List<Metadata>> map;
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.bookshelf_group_row);
    private int col = DRApplication.getInstance().getResources().getInteger(R.integer.bookshelf_group_col);
    private LibraryDataHolder dataHolder;
    private Object[] language;
    private String libraryName;
    private int item_row = DRApplication.getInstance().getResources().getInteger(R.integer.bookshelf_group_item_row);
    private int item_col = DRApplication.getInstance().getResources().getInteger(R.integer.bookshelf_group_item_col);

    public EBookLanguageGroupAdapter(Context context) {
        this.context = context;
    }

    public void setMap(String libraryName, Map<String, List<Metadata>> map) {
        language = map.keySet().toArray();
        this.libraryName = libraryName;
        this.map = map;
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
        return map == null ? 0 : map.size();
    }

    @Override
    public EBookLanguageGroupAdapter.GroupItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(context, R.layout.item_bookshelf_group, null);
        return new GroupItemViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(final EBookLanguageGroupAdapter.GroupItemViewHolder holder, int position) {
        List<Metadata> list = map.get(language[position]);
        holder.groupName.setText((String) language[position]);
        final EBookListAdapter listAdapter = new EBookListAdapter(context, getDataHolder());
        listAdapter.setRowAndCol(item_row, item_col);
        holder.pageRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        holder.pageRecycler.addItemDecoration(dividerItemDecoration);
        holder.pageRecycler.setAdapter(listAdapter);
        QueryResult<Metadata> result = new QueryResult<>();
        result.list = list;
        result.count = list.size();
        Map<String, CloseableReference<Bitmap>> bitmaps = DataManagerHelper.loadCloudThumbnailBitmapsWithCache(context, DRApplication.getCloudStore().getCloudManager(), result.list);
        listAdapter.updateContentView(getLibraryDataModel(result, bitmaps));
        holder.nextButton.setOnClickListener(this);
        holder.nextButton.setTag(language[position]);
        if (result.list == null || result.list.size() <= 0) {
            holder.rootView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        String language = (String) v.getTag();
        EBookListEvent event = new EBookListEvent(language);
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
            dataHolder = new LibraryDataHolder(context);
            dataHolder.setCloudManager(DRApplication.getCloudStore().getCloudManager());
        }
        return dataHolder;
    }
}
