package com.onyx.android.sdk.ui;

import android.content.Context;
import android.widget.TableLayout;
import com.onyx.android.sdk.data.GAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 3/16/14
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class OnyxTableViewSample extends TableLayout{

    public abstract class OnyxTableViewCallback {
        public void onPageChanged(int oldPage, int newPage) {
        }

        public void onItemClicked(OnyxTableItemView view) {
        }

        public void onItemLongPressed(OnyxTableItemView view) {
        }

        public void onItemFocusChanged(OnyxTableItemView oldItem, OnyxTableItemView newItem) {
        }
    }

    private GAdapter data;
    private OnyxTableViewCallback callback;
    private int subviewResourceId;

    public OnyxTableViewSample(Context context) {
        super(context);
    }

    public OnyxTableViewSample(Context context, final GAdapter adapter, int resourceId, final OnyxTableViewCallback callback) {
        super(context);
        setAdapter(data);
        setCallback(callback);
        subviewResourceId = resourceId;
        initialize();
    }

    /**
     * Create list view by using specified adapter.
     * @param context The parent context.
     * @param adapter The data adapter.
     * @param resourceId The sub view resource id. if no resource id is found in adapter, it tries to load resource according to resource id.
     * if < 0 the default layout resource will be used.
     * @return The created table view.
     */
    static public OnyxTableViewSample thumbnailTableView(Context context, final GAdapter adapter, int resourceId, final OnyxTableViewCallback callback) {
        // fixed rows and columns.
        OnyxTableViewSample tableView = new OnyxTableViewSample(context, adapter, resourceId, callback);
        return tableView;
    }

    /**
     * Create list view by using specified adapter.
     * @param context The parent context.
     * @param adapter The data adapter.
     * @param resourceId The sub view resource id. if < 0 the default layout resource will be used.
     * @return The created table view.
     */
    static public OnyxTableViewSample listTableView(Context context, final GAdapter adapter, int resourceId, final OnyxTableViewCallback callback) {
        // rows are fixed by item height
        OnyxTableViewSample tableView = new OnyxTableViewSample(context, adapter, resourceId, callback);
        return tableView;
    }

    /**
     * Create list view by using specified adapter.
     * @param context The parent context.
     * @param adapter The data adapter.
     * @param resourceId The sub view resource id. if < 0 the default layout resource will be used.
     * @return The created table view.
     */
    static public OnyxTableViewSample detailsTableView(Context context, final GAdapter adapter, int resourceId, final OnyxTableViewCallback callback) {
        // rows are fixed by item height
        OnyxTableViewSample tableView = new OnyxTableViewSample(context, adapter, resourceId, callback);
        return tableView;
    }

    /**
     * Create list view by using specified adapter.
     * @param context The parent context.
     * @param adapter The data adapter.
     * @param resourceId The sub view resource id. if < 0 the default layout resource will be used.
     * @return The created table view.
     */
    static public OnyxTableViewSample listSelectionTableView(Context context, final GAdapter adapter, int resourceId, final OnyxTableViewCallback callback) {
        OnyxTableViewSample tableView = new OnyxTableViewSample(context, adapter, resourceId, callback);
        return tableView;
    }

    /**
     * Create horizontal toolbar by using specified adapter. All the items use the same layout resource.
     * @param context The parent context.
     * @param adapter The data adapter.
     * @param resourceId The sub view resource id. if < 0 the default layout resource will be used.
     * @return The created table view.
     */
    static public OnyxTableViewSample toolbarTableView(Context context, final GAdapter adapter, int resourceId, final OnyxTableViewCallback callback) {
        // one row and fixed columns.
        OnyxTableViewSample tableView = new OnyxTableViewSample(context, adapter, resourceId, callback);
        return tableView;
    }

    /**
     * Create horizon toolbar by using specified adapter. The items width may be different. Caller should
     * pass layout resource id in the GObject.
     * @param context The parent context.
     * @param adapter The data adapter.
     * @return The created table view.
     */
    static public OnyxTableViewSample stretchableToolbarTableView(Context context, final GAdapter adapter, final OnyxTableViewCallback callback) {
        // one row and fixed columns.
        OnyxTableViewSample tableView = new OnyxTableViewSample(context, adapter, -1, callback);
        return tableView;
    }

    public void setCallback(final OnyxTableViewCallback cb) {
        callback = cb;
    }

    public void setAdapter(final GAdapter adapter) {
        data = adapter;
    }

    public void setupSubViews() {
        int rows = getRows();
    }

    public int getRows() {
        return 1;
    }

    private void initialize() {

    }


}
