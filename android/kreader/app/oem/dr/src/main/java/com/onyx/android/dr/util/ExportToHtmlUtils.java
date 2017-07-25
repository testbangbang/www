package com.onyx.android.dr.util;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.InfromalEssayEntity;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Created by huxiaomao on 2017/1/12.
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

    public static void exportNewWordToHtml(List<String> newWordTitle, String title, List<NewWordNoteBookEntity> dataList) {
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
            sb.append(TimeUtils.getCurrentMonth(currentTime));
            sb.append("</th>");
            sb.append("<th>");
            sb.append(TimeUtils.getWeekOfMonth(currentTime));
            sb.append("</th>");
            sb.append("<th>");
            sb.append(TimeUtils.getCurrentDay(currentTime));
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.newWord);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.dictionaryLookup);
            sb.append("</th>");
            sb.append("<th>");
            sb.append(bean.readingMatter);
            sb.append("</th>");
            sb.append("</tr>");
        }
        sb.append("</table>");

        PrintStream printStream = null;
        try {
            String fileName = Environment.getExternalStorageDirectory() + Constants.NEW_WORD_HTML;
            File file = new File(fileName);
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            printStream = new PrintStream(new FileOutputStream(file));
            printStream.println(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void exportInfromalEssayToHtml(List<String> htmlTitle, String title, List<InfromalEssayEntity> dataList) {
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
            InfromalEssayEntity bean = dataList.get(i);
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
            String fileName = Environment.getExternalStorageDirectory() + Constants.NEW_WORD_HTML;
            File file = new File(fileName);
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            printStream = new PrintStream(new FileOutputStream(file));
            printStream.println(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
