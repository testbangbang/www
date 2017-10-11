package com.onyx.einfo.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.model.v2.PushNotification;
import com.onyx.android.sdk.data.request.cloud.v2.PushNotificationLoadRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.ui.dialog.DialogProgressHolder;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.einfo.InfoApp;
import com.onyx.einfo.R;
import com.onyx.einfo.dialog.DialogWebView;
import com.onyx.einfo.events.PushNotificationEvent;
import com.onyx.einfo.events.TabSwitchEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suicheng on 2017/9/14.
 */
public class MessageFragment extends Fragment {

    private static final SimpleDateFormat DATE_FORMAT_YY_MM_DD_HH_MM_SS = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault());

    @Bind(R.id.content_pageView)
    PageRecyclerView contentPageView;
    @Bind(R.id.radio_group)
    RadioGroup messageTypeGroup;

    private DialogWebView markdownDialog;
    private OnyxPageDividerItemDecoration itemDecoration;

    private DialogProgressHolder dialogHolder = new DialogProgressHolder();

    private List<PushNotification> notificationList = new ArrayList<>();

    private int pageCol = 1;
    private int pageRow = 8;
    private int queryLimit = 35;
    private String notificationType = PushNotification.Type.Text.toString().toLowerCase();

    private boolean isUserVisible = false;

    public static Fragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, view);
        initView((ViewGroup) view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshData();
    }

    private void initView(ViewGroup viewGroup) {
        initContentView();
        intiRadioGroupView();
    }

    private void initContentView() {
        contentPageView.setLayoutManager(new DisableScrollGridManager(getContext()));
        itemDecoration = new OnyxPageDividerItemDecoration(getContext(), OnyxPageDividerItemDecoration.VERTICAL);
        itemDecoration.setActualChildCount(pageRow);
        contentPageView.addItemDecoration(itemDecoration);
        contentPageView.setItemDecorationHeight(itemDecoration.getDivider().getIntrinsicHeight());
        PageRecyclerView.PageAdapter adapter = new PageRecyclerView.PageAdapter<PushViewHolder>() {
            @Override
            public int getRowCount() {
                return pageRow;
            }

            @Override
            public int getColumnCount() {
                return pageCol;
            }

            @Override
            public int getDataCount() {
                return getActualDataCount();
            }

            @Override
            public PushViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new PushViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_content_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(PushViewHolder holder, final int position) {
                holder.itemView.setTag(position);
                updateItemDecoration();
                if (position % pageRow == 0) {
                    holder.orderTextView.setText(getString(R.string.order_number));
                    holder.titleTextView.setText(getString(R.string.title));
                    holder.dateTextView.setText(getString(R.string.date));
                    holder.unReadView.setVisibility(View.INVISIBLE);
                } else {
                    int actualPosition = getActualPosition(position);
                    PushNotification notification = notificationList.get(actualPosition);
                    holder.orderTextView.setText(String.valueOf(actualPosition + 1));
                    holder.titleTextView.setText(notification.title);
                    Date date = notification.getCreatedAt() == null ? new Date() : notification.getCreatedAt();
                    holder.dateTextView.setText(DATE_FORMAT_YY_MM_DD_HH_MM_SS.format(date));
                    holder.unReadView.setVisibility(notification.isReaded ? View.INVISIBLE : View.VISIBLE);
                }
            }
        };
        contentPageView.setAdapter(adapter);
        contentPageView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                if (!contentPageView.getPaginator().hasNextPage()) {
                    loadMoreData();
                }
            }
        });
    }

    private void intiRadioGroupView() {
        messageTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                processMessageTypeSelect(checkedId);
            }
        });
    }

    private void processMessageTypeSelect(int id) {
        switch (id) {
            case R.id.radio_notification:
                notificationType = PushNotification.Type.Text.toString().toLowerCase();
                break;
            case R.id.radio_vote_result:
                notificationType = PushNotification.Type.Vote.toString().toLowerCase();
                break;
        }
        refreshData();
    }

    private void loadMoreData() {
        QueryArgs queryArgs = getQueryArgs();
        queryArgs.offset = CollectionUtils.getSize(notificationList);
        queryArgs.fetchPolicy = FetchPolicy.CLOUD_MEM_DB;
        loadData(queryArgs, false, null);
    }

    private void loadData(QueryArgs queryArgs, final boolean clear, final BaseCallback baseCallback) {
        final PushNotificationLoadRequest loadRequest = new PushNotificationLoadRequest(queryArgs);
        loadRequest.setClearBeforeSave(clear);
        PushNotification query = new PushNotification();
        query.type = notificationType;
        loadRequest.setQueryType(JSONObjectParseUtils.toJson(query));
        InfoApp.getCloudStore().submitRequest(getContext().getApplicationContext(), loadRequest,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        dialogHolder.dismissProgressDialog(request);
                        if (e != null) {
                            ToastUtils.showToast(getContext().getApplicationContext(), R.string.fetch_message_fail);
                            return;
                        }
                        if (clear) {
                            notificationList.clear();
                        }
                        notificationList.addAll(loadRequest.getNotificationList());
                        BaseCallback.invoke(baseCallback, request, e);
                        updateContentView();
                    }
                });
        dialogHolder.showProgressDialog(getContext(), loadRequest, R.string.refreshing, null);
    }

    private QueryArgs getQueryArgs() {
        QueryArgs queryArgs = new QueryArgs();
        queryArgs.fetchPolicy = FetchPolicy.CLOUD_MEM_DB;
        queryArgs.limit = queryLimit;
        queryArgs.offset = 0;
        queryArgs.sortBy = SortBy.CreationTime;
        queryArgs.order = SortOrder.Desc;
        return queryArgs;
    }

    @OnClick(R.id.refresh)
    public void refreshData() {
        QueryArgs args = getQueryArgs();
        loadData(args, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                gotoPage(0);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        dialogHolder.dismissAllProgressDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushNotificationEvent(PushNotificationEvent event) {
        PushNotification item = event.notification;
        if (StringUtils.isNotBlank(item.type)) {
            if (item.type.equals(notificationType)) {
                if (CollectionUtils.isNullOrEmpty(notificationList)) {
                    notificationList.add(item);
                } else {
                    notificationList.add(0, item);
                }
            }
        }
        updateContentView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onKeyEvent(KeyEvent event) {
        if (isUserVisible) {
            processKeyEvent(event);
        }
    }

    private void processKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return;
        }
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_PAGE_DOWN:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                nextTab();
                break;
            case KeyEvent.KEYCODE_PAGE_UP:
            case KeyEvent.KEYCODE_VOLUME_UP:
                prevTab();
                break;
            default:
                break;
        }
    }

    private void nextTab() {
        if (isUserVisible) {
            EventBus.getDefault().post(TabSwitchEvent.createNextTabSwitch());
        }
    }

    private void prevTab() {
        if (isUserVisible) {
            EventBus.getDefault().post(TabSwitchEvent.createPrevTabSwitch());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isUserVisible = isVisibleToUser;
    }

    private void updateItemDecoration() {
        GPaginator pageIndicator = contentPageView.getPaginator();
        if (pageIndicator.isLastPage()) {
            itemDecoration.setBlankCount(pageIndicator.itemsInCurrentPage() == pageRow ? 0 : getBlankCount());
        } else {
            itemDecoration.setBlankCount(0);
        }
    }

    private void updateContentView() {
        if (contentPageView == null) {
            return;
        }
        contentPageView.getAdapter().notifyDataSetChanged();
    }

    private void gotoPage(int page) {
        if (contentPageView == null) {
            return;
        }
        contentPageView.gotoPage(page);
    }

    private int getBlankCount() {
        return pageRow - getActualDataCount() % pageRow - 1;
    }

    private int getActualPosition(int oldPosition) {
        return oldPosition - oldPosition / pageRow - 1;
    }

    private int getActualDataCount() {
        int size = CollectionUtils.getSize(notificationList);
        if (size <= 0) {
            return 1;
        }
        int pages = size / pageRow;
        if (pages * pageRow < size) {
            pages++;
        }
        return size + pages;
    }

    private void showNotificationContentDialog(String title, List<String> messageList) {
        if (markdownDialog == null) {
            markdownDialog = new DialogWebView(StringUtils.isNullOrEmpty(title) ?
                    getString(R.string.message_content) : title, messageList);
            markdownDialog.setDialogEventsListener(new OnyxAlertDialog.DialogEventsListener() {
                @Override
                public void onCancel(OnyxAlertDialog dialog, DialogInterface dialogInterface) {
                }

                @Override
                public void onDismiss(OnyxAlertDialog dialog, DialogInterface dialogInterface) {
                    markdownDialog = null;
                }
            });
            markdownDialog.show(getActivity().getFragmentManager());
        } else {
            markdownDialog.addData(messageList);
        }
    }

    private void processItemClick(int position) {
        if (position % pageRow == 0) {
            return;
        }
        PushNotification notification = notificationList.get(getActualPosition(position));
        showNotificationContentDialog(notification);
        readPushNotification(notification);
        updateContentView();
    }

    private void showNotificationContentDialog(PushNotification item) {
        List<String> contentList = new ArrayList<>();
        String text = item.content;
        contentList.add(text);
        showNotificationContentDialog(item.title, contentList);
    }

    private void readPushNotification(PushNotification item) {
        item.isReaded = true;
        BaseData.asyncSave(item);
    }

    class PushViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.text_view_order)
        TextView orderTextView;
        @Bind(R.id.text_view_title)
        TextView titleTextView;
        @Bind(R.id.text_view_date)
        TextView dateTextView;
        @Bind(R.id.text_view_un_read)
        ImageView unReadView;

        public PushViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processItemClick((Integer) v.getTag());
                }
            });
            ButterKnife.bind(this, itemView);
        }
    }
}
