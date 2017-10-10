package com.onyx.android.dr.util;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.data.database.ReadingRateEntity;
import com.onyx.android.dr.event.ExportHtmlFailedEvent;
import com.onyx.android.dr.event.ExportHtmlSuccessEvent;
import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
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
            EventBus.getDefault().post(new ExportHtmlSuccessEvent());
        } catch (Exception e) {
            EventBus.getDefault().post(new ExportHtmlFailedEvent());
            e.printStackTrace();
        }finally {
            FileUtils.closeQuietly(printStream);
        }
    }

    public static void exportInfromalEssayToHtml(Context context, List<String> htmlTitle, String title, List<InformalEssayEntity> dataList) {
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
            InformalEssayEntity bean = dataList.get(i);
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
            EventBus.getDefault().post(new ExportHtmlSuccessEvent());
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
            String time = DateTimeUtil.formatDate(bean.time, DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM);
            sb.append("<th>");
            sb.append(time);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.bookName);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.timeHorizon);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.language);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.readSummaryPiece);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.readerResponsePiece);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.readerResponseNumber);
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
            EventBus.getDefault().post(new ExportHtmlSuccessEvent());
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
            EventBus.getDefault().post(new ExportHtmlSuccessEvent());
        } catch (Exception e) {
            EventBus.getDefault().post(new ExportHtmlFailedEvent());
            e.printStackTrace();
        }finally {
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
            EventBus.getDefault().post(new ExportHtmlSuccessEvent());
        } catch (Exception e) {
            EventBus.getDefault().post(new ExportHtmlFailedEvent());
            e.printStackTrace();
        }finally {
            FileUtils.closeQuietly(printStream);
        }
    }

    public static void exportBookReportToHtml(String title, List<String> titleList, GetBookReportListBean bookReportListBean) {
        StringBuilder sb = getTitleStringBuilder(title);
        sb.append("<table border=\"1\"><tr>");
        for (int i = 0; i < titleList.size(); i++) {
            sb.append("<th>");
            sb.append(titleList.get(i));
            sb.append("</th>");
        }
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<th>");
        sb.append(bookReportListBean.updatedAt);
        sb.append("</th>");
        sb.append("<th>");
        sb.append(bookReportListBean.name);
        sb.append("</th>");
        sb.append("<th>");
        sb.append("000");
        sb.append("</th>");
        sb.append("<th>");
        sb.append(bookReportListBean.content);
        sb.append("</th>");
        sb.append("<th>");
        sb.append(bookReportListBean.content == null ? 0 :bookReportListBean.content.length());
        sb.append("</th>");
        sb.append("</tr>");
        sb.append("</table>");

        PrintStream printStream = null;
        try {
            createCatalogue();
            File file = new File(Environment.getExternalStorageDirectory(), Constants.MY_NOTES_FOLDER + File.separator +
            bookReportListBean.name + TimeUtils.getCurrentMillTimeInString() + Constants.UNIT);
            file.createNewFile();
            printStream = new PrintStream(new FileOutputStream(file));
            printStream.println(sb);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(printStream);
        }
    }
}
