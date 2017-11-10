package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.BookReportListAdapter;
import com.onyx.android.dr.adapter.InformalEssayAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.BookReportData;
import com.onyx.android.dr.data.InformalEssayData;
import com.onyx.android.dr.interfaces.BookReportView;
import com.onyx.android.dr.request.cloud.AddCommentRequest;
import com.onyx.android.dr.request.cloud.AddInformalCommentRequest;
import com.onyx.android.dr.request.cloud.BringOutBookReportRequest;
import com.onyx.android.dr.request.cloud.CreateBookReportRequest;
import com.onyx.android.dr.request.cloud.CreateInformalEssayRequest;
import com.onyx.android.dr.request.cloud.DeleteBookReportRequest;
import com.onyx.android.dr.request.cloud.DeleteInformalEssayRequest;
import com.onyx.android.dr.request.cloud.GetBookReportListRequest;
import com.onyx.android.dr.request.cloud.GetBookReportRequest;
import com.onyx.android.dr.request.cloud.GetSharedImpressionRequest;
import com.onyx.android.dr.request.cloud.GetSharedInformalRequest;
import com.onyx.android.dr.request.cloud.RequestGetInformalEssay;
import com.onyx.android.dr.request.local.InformalEssayExport;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.CreateInformalEssayBean;
import com.onyx.android.sdk.data.model.InformalEssayBean;
import com.onyx.android.sdk.data.model.v2.AddCommentRequestBean;
import com.onyx.android.sdk.data.model.v2.CloudMetadataCollection;
import com.onyx.android.sdk.data.model.v2.CreateBookReportRequestBean;
import com.onyx.android.sdk.data.model.v2.CreateBookReportResult;
import com.onyx.android.sdk.data.model.v2.GetBookReportList;
import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;
import com.onyx.android.sdk.data.model.v2.GetBookReportListRequestBean;
import com.onyx.android.sdk.data.model.v2.GetSharedImpressionResult;
import com.onyx.android.sdk.data.model.v2.GetSharedInformalResult;
import com.onyx.android.sdk.data.request.data.db.GetBookLibraryIdRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.onyx.android.dr.R.string.please_select_export_data;

/**
 * Created by li on 2017/9/19.
 */

public class BookReportPresenter {
    private final InformalEssayData infromalEssayData;
    private BookReportView bookReportView;
    private BookReportData bookReportData;
    private List<GetBookReportListBean> data;
    private List<CreateInformalEssayBean> informalEssayData;
    private List<Boolean> listCheck;
    private List<Boolean> informalEssayListCheck;
    private String offset = "0";
    private String limit = "200";
    private String sortBy = "createdAt";
    private String order = "-1";

    public BookReportPresenter(BookReportView bookReportView) {
        this.bookReportView = bookReportView;
        bookReportData = new BookReportData();
        infromalEssayData = new InformalEssayData();
    }

    public void getImpressionsList() {
        GetBookReportListRequestBean requestBean = new GetBookReportListRequestBean();
        requestBean.offset = offset;
        requestBean.limit = limit;
        requestBean.order = order;
        requestBean.sortBy = sortBy;
        final String libraryId = DRPreferenceManager.loadLibraryParentId(DRApplication.getInstance(), "");
        final GetBookReportListRequest rq = new GetBookReportListRequest(requestBean);
        bookReportData.getImpressionsList(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetBookReportList bookReportList = rq.getBookReportList();
                ArrayList<Boolean> checkList = rq.getCheckList();
                if (data == null) {
                    data = new ArrayList<>();
                    listCheck = new ArrayList<>();
                } else {
                    data.clear();
                    listCheck.clear();
                }
                if (bookReportList != null && bookReportList.list != null && bookReportList.list.size() > 0) {
                    data.addAll(bookReportList.list);
                    listCheck.addAll(checkList);
                }
                getSharedImpressions(libraryId);
            }
        });
    }

    public void getInformalEssay(String param) {
        final String libraryId = DRPreferenceManager.loadLibraryParentId(DRApplication.getInstance(), "");
        final RequestGetInformalEssay req = new RequestGetInformalEssay(infromalEssayData, param);
        infromalEssayData.getInformalEssay(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<CreateInformalEssayBean> group = req.getGroup();
                ArrayList<Boolean> checkList = req.getCheckList();
                if (informalEssayData == null) {
                    informalEssayData = new ArrayList<>();
                    informalEssayListCheck = new ArrayList<>();
                } else {
                    informalEssayData.clear();
                    informalEssayListCheck.clear();
                }
                if (group != null && group.size() > 0) {
                    informalEssayData.addAll(group);
                    informalEssayListCheck.addAll(checkList);
                }
                getSharedInformal(libraryId);
            }
        });
    }

    public void getSharedImpressions(String libraryId) {
        GetBookReportListRequestBean requestBean = new GetBookReportListRequestBean();
        requestBean.offset = offset;
        requestBean.limit = limit;
        requestBean.order = order;
        requestBean.sortBy = sortBy;
        final GetSharedImpressionRequest rq = new GetSharedImpressionRequest(libraryId, requestBean);
        bookReportData.getSharedImpressions(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetSharedImpressionResult result = rq.getResult();
                ArrayList<Boolean> checkList = rq.getCheckList();
                if(result != null && result.list != null && result.list.size() > 0) {
                    data.addAll(result.list);
                    listCheck.addAll(checkList);
                }
                bookReportView.setBookReportList(data, listCheck);
            }
        });
    }

    public void getSharedInformal(String libraryId) {
        GetBookReportListRequestBean requestBean = new GetBookReportListRequestBean();
        requestBean.offset = offset;
        requestBean.limit = limit;
        requestBean.order = order;
        requestBean.sortBy = sortBy;
        final GetSharedInformalRequest rq = new GetSharedInformalRequest(libraryId, requestBean);
        bookReportData.getSharedInformal(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetSharedInformalResult result = rq.getResult();
                ArrayList<Boolean> checkList = rq.getCheckList();
                if(result != null && result.list != null && result.list.size() > 0) {
                    informalEssayData.addAll(result.list);
                    informalEssayListCheck.addAll(checkList);
                }
                bookReportView.setInformalEssayData(informalEssayData, informalEssayListCheck);
                bookReportView.setBookReportList(data, listCheck);
            }
        });
    }

    public void remoteAdapterData(List<Boolean> listCheck, BookReportListAdapter adapter, List<GetBookReportListBean> list) {
        List<GetBookReportListBean> exportList = getData(listCheck, list);
        if (exportList == null || exportList.isEmpty()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(please_select_export_data));
            return;
        }
        int length = listCheck.size();
        for (int i = length - 1; i >= 0; i--) {
            if (listCheck.get(i)) {
                //delete basedata data
                GetBookReportListBean bean = list.get(i);
                deleteImpression(bean._id);
                list.remove(i);
                listCheck.remove(i);
                adapter.notifyItemRemoved(i);
            }
        }
        adapter.notifyItemRangeChanged(0, list.size());
    }

    public void remoteInformalAdapterData(List<Boolean> listCheck, InformalEssayAdapter informalEssayAdapter, List<CreateInformalEssayBean> list) {
        List<CreateInformalEssayBean> exportList = getInformalData(listCheck, list);
        if (exportList == null || exportList.isEmpty()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(please_select_export_data));
            return;
        }
        int length = listCheck.size();
        for (int i = length - 1; i >= 0; i--) {
            if (listCheck.get(i)) {
                //delete basedata data
                CreateInformalEssayBean bean = list.get(i);
                deleteInformalEssay(bean._id);
                list.remove(i);
                listCheck.remove(i);
                informalEssayAdapter.notifyItemRemoved(i);
            }
        }
        informalEssayAdapter.notifyItemRangeChanged(0, list.size());
    }

    public void deleteInformalEssay(String id) {
        final DeleteInformalEssayRequest rq = new DeleteInformalEssayRequest(id);
        infromalEssayData.deleteCloudInformalEssay(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                bookReportView.setDeleteResult();
            }
        });
    }

    public void deleteImpression(String id) {
        final DeleteBookReportRequest rq = new DeleteBookReportRequest(id);
        bookReportData.deleteImpression(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                bookReportView.setDeleteResult();
            }
        });
    }

    public ArrayList<String> getHtmlTitleData() {
        ArrayList<String> htmlTitle = bookReportData.getHtmlTitle(DRApplication.getInstance());
        return htmlTitle;
    }

    public ArrayList<String> getInformalHtmlTitleData() {
        ArrayList<String> htmlTitle = infromalEssayData.getHtmlTitle(DRApplication.getInstance());
        return htmlTitle;
    }

    public void exportDataToHtml(List<Boolean> listCheck, ArrayList<String> dataList, List<GetBookReportListBean> list) {
        List<GetBookReportListBean> exportNewWordList = getData(listCheck, list);
        if (exportNewWordList == null || exportNewWordList.isEmpty()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(please_select_export_data));
            return;
        }
        String title = DRApplication.getInstance().getResources().getString(R.string.reader_response);
        final BringOutBookReportRequest req = new BringOutBookReportRequest(title, dataList, exportNewWordList);
        bookReportData.bringOutReport(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void exportDataToHtml(final Context context, List<Boolean> listCheck, ArrayList<String> dataList, List<CreateInformalEssayBean> list) {
        List<CreateInformalEssayBean> exportNewWordList = getInformalData(listCheck, list);
        if (exportNewWordList == null || exportNewWordList.isEmpty()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(please_select_export_data));
            return;
        }
        final InformalEssayExport req = new InformalEssayExport(context, dataList, exportNewWordList);
        infromalEssayData.exportInformalEssay(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    private List<CreateInformalEssayBean> getInformalData(List<Boolean> listCheck, List<CreateInformalEssayBean> list) {
        List<CreateInformalEssayBean> exportList = new ArrayList<>();
        for (int i = 0, j = list.size(); i < j; i++) {
            Boolean aBoolean = listCheck.get(i);
            if (aBoolean) {
                CreateInformalEssayBean bean = list.get(i);
                if (!exportList.contains(bean)) {
                    exportList.add(bean);
                }
            }
        }
        return exportList;
    }

    public void shareReadingRate(List<Boolean> listCheck, List<GetBookReportListBean> list) {
        List<GetBookReportListBean> exportList = getData(listCheck, list);
        if (exportList == null || exportList.isEmpty()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(R.string.no_relevant_data));
            return;
        }
        int length = exportList.size();
        String[] array = new String[]{};
        for (int i = length - 1; i >= 0; i--) {
            if (listCheck.get(i)) {
                GetBookReportListBean bean = exportList.get(i);
                array = Arrays.copyOf(array, array.length + 1);
                array[array.length - 1] = bean._id;
            }
        }
        DRPreferenceManager.saveShareType(DRApplication.getInstance(), Constants.READER_RESPONSE);
        ActivityManager.startShareBookReportActivity(DRApplication.getInstance(), "", array);
    }

    public void shareInformalEssay(List<Boolean> listCheck, List<CreateInformalEssayBean> list) {
        List<CreateInformalEssayBean> exportList = getInformalData(listCheck, list);
        if (exportList == null || exportList.isEmpty()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(please_select_export_data));
            return;
        }
        int length = exportList.size();
        String[] array = new String[]{};
        for (int i = length - 1; i >= 0; i--) {
            if (listCheck.get(i)) {
                CreateInformalEssayBean bean = exportList.get(i);
                array = Arrays.copyOf(array, array.length + 1);
                array[array.length - 1] = bean._id;
            }
        }
        DRPreferenceManager.saveShareType(DRApplication.getInstance(), Constants.INFORMAL_ESSAY);
        ActivityManager.startShareBookReportActivity(DRApplication.getInstance(), "", array);
    }

    public void createImpression(String bookId, String bookName, String title, String content, String currentPage) {
        CreateBookReportRequestBean requestBean = new CreateBookReportRequestBean();
        requestBean.setName(bookName);
        requestBean.setTitle(title);
        requestBean.setBook(bookId);
        requestBean.setContent(content);
        requestBean.setPageNumber(currentPage);
        final CreateBookReportRequest rq = new CreateBookReportRequest(requestBean);
        bookReportData.createImpression(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                CreateBookReportResult createBookReportResult = rq.getCreateBookReportResult();
                bookReportView.saveBookReportData(createBookReportResult);
            }
        });
    }

    public void getImpression(String id) {
        final GetBookReportRequest rq = new GetBookReportRequest(id);
        bookReportData.getImpression(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                CreateBookReportResult result = rq.getResult();
                if (result != null) {
                    bookReportView.getBookReport(result);
                }
            }
        });
    }

    public void addComment(String bookId, String top, String left, String content) {
        AddCommentRequestBean requestBean = new AddCommentRequestBean();
        requestBean.top = top;
        requestBean.left = left;
        requestBean.content = content;

        final AddCommentRequest rq = new AddCommentRequest(bookId, requestBean);
        bookReportData.addComment(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                CreateBookReportResult result = rq.getResult();
                bookReportView.addCommentResult(result);
            }
        });
    }

    public void addInformalComment(String bookId, String top, String left, String content) {
        AddCommentRequestBean requestBean = new AddCommentRequestBean();
        requestBean.top = top;
        requestBean.left = left;
        requestBean.content = content;

        final AddInformalCommentRequest rq = new AddInformalCommentRequest(bookId, requestBean);
        bookReportData.addInformalComment(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                CreateInformalEssayBean result = rq.getResult();
                bookReportView.addInformalCommentResult(result);
            }
        });
    }

    public void getLibraryId(final String bookId) {
        final GetBookLibraryIdRequest rq = new GetBookLibraryIdRequest(bookId);
        bookReportData.getLibraryId(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                CloudMetadataCollection metadataCollection = rq.getMetadataCollection();
                if (metadataCollection != null) {
                    String libraryId = metadataCollection.getLibraryUniqueId();
                    bookReportView.setLibraryId(bookId, libraryId);
                }
            }
        });
    }

    private List<GetBookReportListBean> getData(List<Boolean> listCheck, List<GetBookReportListBean> list) {
        List<GetBookReportListBean> exportList = new ArrayList<>();
        for (int i = 0, j = list.size(); i < j; i++) {
            Boolean aBoolean = listCheck.get(i);
            if (aBoolean) {
                GetBookReportListBean bean = list.get(i);
                if (!exportList.contains(bean)) {
                    exportList.add(bean);
                }
            }
        }
        return exportList;
    }

    public void createInformalEssay(InformalEssayBean param) {
        final CreateInformalEssayRequest req = new CreateInformalEssayRequest(param);
        infromalEssayData.createInformalEssay(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                bookReportView.createInformalEssay(req.getGroup() != null);
            }
        });
    }
}
