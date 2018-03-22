package com.onyx.jdread.shop.utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.ResourceReleaser;
import com.facebook.common.util.UriUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogVipNoticeBinding;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.reader.ui.view.HTMLReaderWebView;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.BaseSubjectViewModel;
import com.onyx.jdread.shop.model.DialogBookInfoViewModel;
import com.onyx.jdread.shop.model.MainConfigEndViewModel;
import com.onyx.jdread.shop.model.SubjectType;
import com.onyx.jdread.shop.view.BookInfoDialog;
import com.onyx.jdread.util.TimeUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by jackdeng on 2018/1/18.
 */

public class ViewHelper {

    public static boolean isShowBookDetailView(BookDetailResultBean resultBean) {
        boolean show = false;
        if (resultBean != null && resultBean.data != null && Constants.RESULT_CODE_SUCCESS.equals(String.valueOf(resultBean.result_code))) {
            show = true;
        }
        return show;
    }

    public static String formatYueDou(float price) {
        if (price > 0) {
            return new DecimalFormat("0").format(price);
        } else {
            return "0";
        }
    }

    public static String getYueDouPrice(BookDetailResultBean.DetailBean bookDetailBean) {
        if (bookDetailBean != null) {
            if (bookDetailBean.jd_price > 0) {
                if (isNetBook(bookDetailBean.book_type)) {
                    return String.format(ResManager.getString(R.string.net_book_price_and_status), formatYueDou(bookDetailBean.jd_price),
                            ViewHelper.getNetBookStatus(bookDetailBean.netStatus));
                } else {
                    return String.format(ResManager.getString(R.string.book_detail_yuedou_price), formatYueDou(bookDetailBean.jd_price));
                }
            }
        }
        return String.format(ResManager.getString(R.string.book_detail_yuedou_price), "0");
    }

    public static String formatRMB(float price) {
        return String.valueOf(new DecimalFormat("0.00").format(price));
    }

    public static boolean isCanNowRead(BookDetailResultBean.DetailBean detailBean) {
        boolean canNowRead = false;
        if (detailBean != null) {
            canNowRead = detailBean.can_try;
        }
        return canNowRead;
    }

    public static int calculateTotalPages(List<BaseSubjectViewModel> subjectList, int recycleViewHeight) {
        int totalPage = 1;
        if (subjectList != null) {
            List<BaseSubjectViewModel> tempList = new ArrayList<>();
            tempList.addAll(subjectList);
            int itemSpace = ResManager.getInteger(R.integer.custom_recycle_view_space);
            int itemHeight = 0;
            for (int i = 0; i < tempList.size(); i++) {
                BaseSubjectViewModel subjectViewModel = tempList.get(i);
                int subjectType = subjectViewModel.getSubjectType();
                switch (subjectType) {
                    case SubjectType.TYPE_TOP_FUNCTION:
                        itemHeight = itemHeight + Constants.SHOP_VIEW_TOP_FUNCTION_HEIGHT;
                        break;
                    case SubjectType.TYPE_BANNER:
                        itemHeight = itemHeight + Constants.SHOP_VIEW_BANNER_HEIGHT;
                        break;
                    case SubjectType.TYPE_TITLE:
                        itemHeight = itemHeight + Constants.SHOP_VIEW_TITLE_HEIGHT;
                        break;
                    case SubjectType.TYPE_COVER:
                        itemHeight = itemHeight + Constants.SHOP_VIEW_SUBJECT_HEIGHT;
                        break;
                    case SubjectType.TYPE_END:
                        MainConfigEndViewModel endViewModel = (MainConfigEndViewModel) subjectViewModel;
                        if (endViewModel.showEmptyView.get()) {
                            itemHeight = itemHeight + Constants.SHOP_VIEW_END_VIEW_HIGH_HEIGHT;
                        } else {
                            itemHeight = itemHeight + Constants.SHOP_VIEW_END_VIEW_HEIGHT;
                        }
                        break;
                    case SubjectType.TYPE_VIP_USER:
                        itemHeight = itemHeight + Constants.SHOP_VIEW_VIP_INFO_VIEW_HEIGHT;
                        break;
                }
                itemHeight = itemHeight + itemSpace;
                if (itemHeight > recycleViewHeight) {
                    tempList.add(i, subjectViewModel);
                    itemHeight = 0;
                    totalPage++;
                }
            }
        }
        return totalPage;
    }

    public static String getPayByCashUrl(Map<String, String> params) {
        String url = CloudApiContext.getJDBooxBaseUrl() + CloudApiContext.ReadBean.PAY_BY_CASH;
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            StringBuffer sb = null;
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
            url += sb.toString();
        }
        return url;
    }

    public static boolean dialogIsShowing(Dialog dialog) {
        return dialog != null && dialog.isShowing();
    }

    public static boolean dialogIsShowing(DialogFragment dialogFragment) {
        if (dialogFragment == null || dialogFragment.getDialog() == null) {
            return false;
        }
        return dialogIsShowing(dialogFragment.getDialog());
    }

    public static void dismissDialog(Dialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public static void dismissDialog(DialogFragment dialog) {
        if (dialogIsShowing(dialog)) {
            dialog.dismiss();
        }
    }

    public static String getNetBookStatus(int status) {
        String bookStatus = "";
        if (status == Constants.NET_BOOK_STATUS_DOWN) {
            bookStatus = ResManager.getString(R.string.net_book_status_down);
        } else {
            bookStatus = ResManager.getString(R.string.net_book_status_update);
        }
        return bookStatus;
    }

    public static boolean isNetBook(int book_type) {
        return book_type == Constants.BOOK_DETAIL_TYPE_NET;
    }

    public static String getNetBookUpdateTimeInfo(String modifiedTime) {
        int days = TimeUtils.daysBetweenDefault(new Date(Long.valueOf(modifiedTime)), Calendar.getInstance().getTime());
        if (days >= 1) {
            return String.format(ResManager.getString(R.string.net_book_day_befor_update),
                    days > 99 ? ResManager.getString(R.string.greater_than_ninety_nine) : String.valueOf(days));
        } else {
            int hours = TimeUtils.hoursBetweenInMillis(Long.valueOf(modifiedTime), Calendar.getInstance().getTimeInMillis());
            return String.format(ResManager.getString(R.string.net_book_hours_befor_update), hours);
        }
    }

    public static String friendlyShowCopyRightInfo(String info) {
        String noResults = ResManager.getString(R.string.no_results);
        return StringUtils.isNullOrEmpty(info) ? noResults : info;
    }

    public static String formatFileSize(float fileSize) {
        if (fileSize > 0) {
            return String.format(ResManager.getString(R.string.copyright_book_size_value), fileSize);
        } else {
            return friendlyShowCopyRightInfo("");
        }
    }

    public static BookInfoDialog showNoticeDialog(Context context, String title, String url, View.OnClickListener closeListener) {
        final DialogVipNoticeBinding infoBinding = DialogVipNoticeBinding.inflate(LayoutInflater.from(context), null, false);
        final DialogBookInfoViewModel infoViewModel = new DialogBookInfoViewModel();
        infoViewModel.title.set(title);
        infoBinding.setViewModel(infoViewModel);
        infoBinding.setListener(closeListener);
        final BookInfoDialog dialog = new BookInfoDialog(context.getApplicationContext(),R.style.CustomDialogStyle);
        dialog.setView(infoBinding.getRoot());
        infoBinding.infoWebView.setCallParentPageFinishedMethod(false);
        infoBinding.infoWebView.loadUrl(url);
        infoBinding.infoWebView.registerOnOnPageChangedListener(new HTMLReaderWebView.OnPageChangedListener() {
            @Override
            public void onPageChanged(int totalPage, int curPage) {
                infoViewModel.currentPage.set(curPage);
                infoViewModel.totalPage.set(totalPage);
            }
        });
        WebSettings settings = infoBinding.infoWebView.getSettings();
        settings.setSupportZoom(false);
        settings.setTextZoom(Constants.WEB_VIEW_TEXT_ZOOM);
        dialog.show();
        return dialog;
    }

    public static boolean isViewVisible(View view) {
        return view != null && view.getVisibility() == View.VISIBLE;
    }

    public static void ensureCoverUrl(ResultBookBean item) {
        if (StringUtils.isNotBlank(item.image_url) && !UriUtil.isNetworkUri(Uri.parse(item.image_url))) {
            item.image_url = CloudApiContext.DEFAULT_COVER_PRE_FIX + item.image_url;
        }
    }

    public static Bitmap loadCoverBitmap(String url, Context context) {
        try {
            return Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .transform(CutBitmapTransformation.getInstance(context))
                    .into(ViewTarget.SIZE_ORIGINAL, ViewTarget.SIZE_ORIGINAL)
                    .get();
        } catch (Exception e) {
            return null;
        }
    }

    public static void saveBitmapCover(List<ResultBookBean> data, Context context) {
        if (!CollectionUtils.isNullOrEmpty(data)) {
            for (ResultBookBean resultBean : data) {
                saveBitmapCover(resultBean, context);
            }
        }
    }

    public static void saveBitmapCover(ResultBookBean resultBean, Context context) {
        if (resultBean != null) {
            ensureCoverUrl(resultBean);
            CloseableReference<Bitmap> refBitmap = getRefBitmap(resultBean.image_url, context);
            if (refBitmap != null) {
                resultBean.coverBitmap.set(refBitmap);
            } else {
                resultBean.coverDefault.set(R.mipmap.ic_cloud_default_cover);
            }
        }
    }

    public static CloseableReference<Bitmap> getRefBitmap(String imageUrl, Context context) {
        return CloseableReference.of(ViewHelper.loadCoverBitmap(imageUrl, context), new ResourceReleaser<Bitmap>() {
            @Override
            public void release(Bitmap value) {
            }
        });
    }
}
