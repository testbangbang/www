package com.onyx.android.dr.util;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.onyx.android.dr.bean.AnnotationStatisticsBean;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.data.database.ReadingRateEntity;
import com.onyx.android.dr.event.ExportHtmlFailedEvent;
import com.onyx.android.dr.event.ExportHtmlSuccessEvent;
import com.onyx.android.dr.reader.data.ReadSummaryEntity;
import com.onyx.android.sdk.data.model.CreateInformalEssayBean;
import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.List;


/**
 * Created by zhouzhiming on 2017/8/3.
 */
public class ExportToHtmlUtils {

    @NonNull
    private static StringBuilder getTitleStringBuilder(String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<title>");
        sb.append(title);
        sb.append("</title>");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
        sb.append("<style type=\"text/css\">");
        sb.append("TABLE{border-collapse:collapse;border-left:solid 1 #000000; border-top:solid 1 #000000;padding:5px;}");
        sb.append("TH{border-right:solid 1 #000000;border-bottom:solid 1 #000000;}");
        sb.append("TD{font:normal;border-right:solid 1 #000000;border-bottom:solid 1 #000000;}");
        sb.append("</style></head>");
        sb.append("<body bgcolor=\"#FFF8DC\">");
        sb.append("<div align=\"center\">");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append(title);
        sb.append("<br/><br/>");
        return sb;
    }

    @NonNull
    private static void createCatalogue() {
        String filePath = Environment.getExternalStorageDirectory() + Constants.MY_NOTES_FOLDER;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void exportNewWordToHtml(Context context, List<String> newWordTitle, String title, List<NewWordNoteBookEntity> dataList) {
        StringBuilder sb = getTitleStringBuilder(title);
        sb.append("<table border=\"1\"><tr>");
        for (int i = 0; i < newWordTitle.size(); i++) {
            sb.append("<th>");
            sb.append(newWordTitle.get(i));
            sb.append("</th>");
        }
        sb.append("</tr>");
        for (int i = 0; i < dataList.size(); i++) {
            sb.append("<tr>");
            NewWordNoteBookEntity bean = dataList.get(i);
            long currentTime = bean.currentTime;
            sb.append("<th>");
            sb.append(i + 1);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.newWord);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.paraphrase);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.dictionaryLookup);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.readingMatter);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(TimeUtils.getDate(currentTime));
            sb.append("</th>");
            sb.append("</tr>");
        }
        sb.append("</table>");

        PrintStream printStream = null;
        try {
            createCatalogue();
            String time = TimeUtils.getNewTime(System.currentTimeMillis());
            File file = new File(Environment.getExternalStorageDirectory() + Constants.NEW_WORD_HTML + "_" + time + Constants.UNIT);
            file.createNewFile();
            printStream = new PrintStream(new FileOutputStream(file));
            printStream.println(sb.toString());
            EventBus.getDefault().post(new ExportHtmlSuccessEvent(file.toString()));
        } catch (Exception e) {
            EventBus.getDefault().post(new ExportHtmlFailedEvent());
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(printStream);
        }
    }

    public static void exportInfromalEssayToHtml(Context context, List<String> htmlTitle, String title, List<CreateInformalEssayBean> dataList) {
        StringBuilder sb = getTitleStringBuilder(title);
        sb.append("<table border=\"1\"><tr>");
        for (int i = 0; i < htmlTitle.size(); i++) {
            sb.append("<th>");
            sb.append(htmlTitle.get(i));
            sb.append("</th>");
        }
        sb.append("</tr>");
        for (int i = 0; i < dataList.size(); i++) {
            sb.append("<tr>");
            CreateInformalEssayBean bean = dataList.get(i);
            long currentTime = bean.currentTime;
            sb.append("<th>");
            sb.append(TimeUtils.getDate(currentTime));
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.title);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.wordNumber);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.content);
            sb.append("</th>");
            sb.append("</tr>");
        }
        sb.append("</table>");

        PrintStream printStream = null;
        try {
            createCatalogue();
            String time = TimeUtils.getNewTime(System.currentTimeMillis());
            File file = new File(Environment.getExternalStorageDirectory() + Constants.INFORMAL_ESSAY_HTML + "_" + time + Constants.UNIT);
            file.createNewFile();
            printStream = new PrintStream(new FileOutputStream(file));
            printStream.println(sb.toString());
            EventBus.getDefault().post(new ExportHtmlSuccessEvent(file.toString()));
        } catch (Exception e) {
            EventBus.getDefault().post(new ExportHtmlFailedEvent());
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(printStream);
        }
    }

    public static void exportReadingRateToHtml(Context context, List<String> htmlTitle, String title, List<ReadingRateEntity> dataList) {
        StringBuilder sb = getTitleStringBuilder(title);
        sb.append("<table border=\"1\"><tr>");
        for (int i = 0; i < htmlTitle.size(); i++) {
            sb.append("<th>");
            sb.append(htmlTitle.get(i));
            sb.append("</th>");
        }
        sb.append("</tr>");
        for (int i = 0; i < dataList.size(); i++) {
            sb.append("<tr>");
            ReadingRateEntity bean = dataList.get(i);
            sb.append("<th>");
            sb.append(bean.recordDate);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.name);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.readTimeLong);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.language);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.summaryCount);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.impressionCount);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.impressionWordsCount);
            sb.append("</th>");
            sb.append("</tr>");
        }
        sb.append("</table>");

        PrintStream printStream = null;
        try {
            createCatalogue();
            String time = TimeUtils.getNewTime(System.currentTimeMillis());
            File file = new File(Environment.getExternalStorageDirectory() + Constants.READING_RATE_HTML + "_" + time + Constants.UNIT);
            file.createNewFile();
            printStream = new PrintStream(new FileOutputStream(file));
            printStream.println(sb.toString());
            EventBus.getDefault().post(new ExportHtmlSuccessEvent(file.toString()));
        } catch (Exception e) {
            EventBus.getDefault().post(new ExportHtmlFailedEvent());
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(printStream);
        }
    }

    public static void exportGoodSentenceToHtml(Context context, List<String> htmlTitle, String title, List<GoodSentenceNoteEntity> dataList) {
        StringBuilder sb = getTitleStringBuilder(title);
        sb.append("<table border=\"1\"><tr>");
        for (int i = 0; i < htmlTitle.size(); i++) {
            sb.append("<th>");
            sb.append(htmlTitle.get(i));
            sb.append("</th>");
        }
        sb.append("</tr>");
        for (int i = 0; i < dataList.size(); i++) {
            sb.append("<tr>");
            GoodSentenceNoteEntity bean = dataList.get(i);
            long currentTime = bean.currentTime;
            sb.append("<th>");
            sb.append(i + 1);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.details);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.readingMatter);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.pageNumber);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(TimeUtils.getDate(currentTime));
            sb.append("</th>");
            sb.append("</tr>");
        }
        sb.append("</table>");

        PrintStream printStream = null;
        try {
            createCatalogue();
            String time = TimeUtils.getNewTime(System.currentTimeMillis());
            File file = new File(Environment.getExternalStorageDirectory() + Constants.GOOD_SENTENCE_HTML + "_" + time + Constants.UNIT);
            file.createNewFile();
            printStream = new PrintStream(new FileOutputStream(file));
            printStream.println(sb.toString());
            EventBus.getDefault().post(new ExportHtmlSuccessEvent(file.toString()));
        } catch (Exception e) {
            EventBus.getDefault().post(new ExportHtmlFailedEvent());
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(printStream);
        }
    }

    public static void exportMemorandumToHtml(Context context, List<String> newWordTitle, String title, List<MemorandumEntity> dataList) {
        StringBuilder sb = getTitleStringBuilder(title);
        sb.append("<table border=\"1\"><tr>");
        for (int i = 0; i < newWordTitle.size(); i++) {
            sb.append("<th>");
            sb.append(newWordTitle.get(i));
            sb.append("</th>");
        }
        sb.append("</tr>");
        for (int i = 0; i < dataList.size(); i++) {
            sb.append("<tr>");
            MemorandumEntity bean = dataList.get(i);
            long currentTime = bean.currentTime;
            sb.append("<th>");
            sb.append(TimeUtils.getDate(currentTime));
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.timeQuantum);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.matter);
            sb.append("</th>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        PrintStream printStream = null;
        try {
            createCatalogue();
            String time = TimeUtils.getNewTime(System.currentTimeMillis());
            File file = new File(Environment.getExternalStorageDirectory() + Constants.MEMORANDUM_HTML + "_" + time + Constants.UNIT);
            file.createNewFile();
            printStream = new PrintStream(new FileOutputStream(file));
            printStream.println(sb.toString());
            EventBus.getDefault().post(new ExportHtmlSuccessEvent(file.toString()));
        } catch (Exception e) {
            EventBus.getDefault().post(new ExportHtmlFailedEvent());
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(printStream);
        }
    }

    public static void exportBookReportToHtml(String title, List<String> titleList, List<GetBookReportListBean> dataList) {
        StringBuilder sb = getTitleStringBuilder(title);
        sb.append("<table border=\"1\"><tr>");
        for (int i = 0; i < titleList.size(); i++) {
            sb.append("<th>");
            sb.append(titleList.get(i));
            sb.append("</th>");
        }
        sb.append("</tr>");
        for (int i = 0; i < dataList.size(); i++) {
            sb.append("<tr>");
            GetBookReportListBean bean = dataList.get(i);
            String time = DateTimeUtil.formatDate(bean.updatedAt, TimeUtils.DATE_FORMAT_DATE);
            sb.append("<th>");
            sb.append(time);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.title);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.pageNumber);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.content);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.content == null ? 0 : bean.content.length());
            sb.append("</th>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        PrintStream printStream = null;
        try {
            createCatalogue();
            String time = TimeUtils.getNewTime(System.currentTimeMillis());
            File file = new File(Environment.getExternalStorageDirectory() + Constants.READER_RESPONSE_HTML + "_" + time + Constants.UNIT);
            file.createNewFile();
            printStream = new PrintStream(new FileOutputStream(file));
            printStream.println(sb);
            EventBus.getDefault().post(new ExportHtmlSuccessEvent(file.toString()));
        } catch (Exception e) {
            EventBus.getDefault().post(new ExportHtmlFailedEvent());
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(printStream);
        }
    }

    public static void exportAnnotationDataToHtml(String title, List<String> titleList, List<AnnotationStatisticsBean> dataList) {
        StringBuilder sb = getTitleStringBuilder(title);
        sb.append("<table border=\"1\"><tr>");
        for (int i = 0; i < titleList.size(); i++) {
            sb.append("<th>");
            sb.append(titleList.get(i));
            sb.append("</th>");
        }
        sb.append("</tr>");
        for (int i = 0; i < dataList.size(); i++) {
            sb.append("<tr>");
            AnnotationStatisticsBean bean = dataList.get(i);
            String time = "";
            try {
                time = TimeUtils.formatDate(bean.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sb.append("<th>");
            sb.append(time);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.getBook().getName());
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.getLibrary().getName());
            sb.append("</th>");
            sb.append("<th>");
            sb.append(String.valueOf(bean.getCount()));
            sb.append("</th>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        PrintStream printStream = null;
        try {
            createCatalogue();
            String time = TimeUtils.getNewTime(System.currentTimeMillis());
            File file = new File(Environment.getExternalStorageDirectory() + Constants.ANNOTATION_HTML + "_" + time + Constants.UNIT);
            file.createNewFile();
            printStream = new PrintStream(new FileOutputStream(file));
            printStream.println(sb);
            EventBus.getDefault().post(new ExportHtmlSuccessEvent(file.toString()));
        } catch (Exception e) {
            EventBus.getDefault().post(new ExportHtmlFailedEvent());
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(printStream);
        }
    }

    public static void exportSummaryDataToHtml(String title, List<String> titleList, List<ReadSummaryEntity> dataList) {
        StringBuilder sb = getTitleStringBuilder(title);
        sb.append("<table border=\"1\"><tr>");
        for (int i = 0; i < titleList.size(); i++) {
            sb.append("<th>");
            sb.append(titleList.get(i));
            sb.append("</th>");
        }
        sb.append("</tr>");
        for (int i = 0; i < dataList.size(); i++) {
            sb.append("<tr>");
            ReadSummaryEntity bean = dataList.get(i);
            sb.append("<th>");
            sb.append(bean.time);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.bookName);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.pageNumber);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.summary);
            sb.append("</th>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        PrintStream printStream = null;
        try {
            createCatalogue();
            String time = TimeUtils.getNewTime(System.currentTimeMillis());
            File file = new File(Environment.getExternalStorageDirectory() + Constants.SUMMARY_HTML + "_" + time + Constants.UNIT);
            file.createNewFile();
            printStream = new PrintStream(new FileOutputStream(file));
            printStream.println(sb);
            EventBus.getDefault().post(new ExportHtmlSuccessEvent(file.toString()));
        } catch (Exception e) {
            EventBus.getDefault().post(new ExportHtmlFailedEvent());
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(printStream);
        }
    }
}
