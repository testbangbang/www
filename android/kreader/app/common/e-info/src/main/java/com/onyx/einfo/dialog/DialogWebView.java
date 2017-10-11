package com.onyx.einfo.dialog;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.einfo.R;
import com.onyx.einfo.custom.CustomWebView;

import java.util.ArrayList;
import java.util.List;

//import us.feras.mdv.MarkdownView;

import static android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK;

/**
 * Created by suicheng on 2016/11/1.
 */
public class DialogWebView extends OnyxAlertDialog {

    private CustomWebView markdownView;
    private TextView pageIndicatorView;
    private int currentPage = 1;
    private String dialogTitle;
    private List<String> htmlContent = new ArrayList<>();
    private OnLoadMoreListener loadMoreListener;
    private boolean enableFullScreen = true;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        loadMoreListener = listener;
    }

    public void enableFullScreen(boolean enable) {
        enableFullScreen = enable;
    }

    public void setData(List<String> data) {
        htmlContent = data;
        if (htmlContent.size() < currentPage) {
            currentPage = htmlContent.size();
        }
        onPageChange(0);
    }

    public void addData(List<String> data) {
        htmlContent.addAll(data);
        onPageChange(currentPage == 1 ? 0 : 1);
    }

    public DialogWebView() {
        super();
    }

    public DialogWebView(String dialogTitle, List<String> htmlContent) {
        this.dialogTitle = dialogTitle;
        this.htmlContent.addAll(htmlContent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Params params = new Params().setTittleString(dialogTitle)
                .setCanceledOnTouchOutside(false)
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_mardown_view)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        pageIndicatorView = pageIndicator;
                        markdownView = (CustomWebView) customView.findViewById(R.id.markdownView);
                        markdownView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
                        markdownView.setScrollbarFadingEnabled(false);
                        WebSettings settings = markdownView.getSettings();
                        settings.setJavaScriptEnabled(true);
                        settings.setSupportZoom(false);
                        settings.setCacheMode(LOAD_CACHE_ELSE_NETWORK);
                        settings.setDomStorageEnabled(true);
                        settings.setDatabaseEnabled(true);
                        settings.setAppCacheEnabled(true);

                        if (!enableFullScreen) {
                            ViewGroup.LayoutParams layoutParams = markdownView.getLayoutParams();
                            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            markdownView.setLayoutParams(layoutParams);
                            markdownView.setMaxHeight((int) customView.getContext().getResources()
                                    .getDimension(R.dimen.dialog_markdown_view_max_height));
                        }
                        if (htmlContent.size() > 0) {
                            onPageChange(0);
                        }
                    }
                })
                .setEnableFunctionPanel(false)
                .setEnableCloseButtonTopRight(true);
        if (enableFullScreen) {
            int[] screenSize = getScreenSize();
            params.setDialogWidth(screenSize[0]);
            params.setDialogHeight(screenSize[1]);
        }
        if (htmlContent.size() > 1) {
            params.setEnablePageIndicator(true)
                    .setEnablePageIndicatorPanel(true)
                    .setPrevPageAction(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onPageChange(-1);
                        }
                    })
                    .setNextPageAction(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            processLoadMoreListener();
                            onPageChange(1);
                        }
                    });
        }
        setParams(params);
        super.onCreate(savedInstanceState);
    }

    public void show(FragmentManager fm) {
        super.show(fm, this.getClass().getSimpleName());
    }

    private void loadData(int index) {
        String content = htmlContent.get(index);
        if (StringUtils.isNotBlank(content)) {
            if (URLUtil.isNetworkUrl(content)) {
                markdownView.loadUrl(content);
                return;
            }
        }
        markdownView.loadData(content, "text/html", "UTF-8");
    }

    private void onPageChange(int diff) {
        int dataCount = htmlContent.size();
        if (currentPage + diff <= dataCount && currentPage + diff > 0) {
            currentPage += diff;
            pageIndicatorView.setText(currentPage + "/" + dataCount);
            loadData(currentPage - 1);
        }
    }

    private void processLoadMoreListener() {
        if (loadMoreListener != null) {
            if (currentPage + 1 > htmlContent.size()) {
                loadMoreListener.onLoadMore();
            }
        }
    }

    private int[] getScreenSize() {
        int[] screenSize = new int[2];
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        screenSize[0] = dm.widthPixels;
        screenSize[1] = dm.heightPixels;
        return screenSize;
    }

    private String getCssStyleFile() {
        return "file:///android_asset/markdown_style_default.css";
    }
}
