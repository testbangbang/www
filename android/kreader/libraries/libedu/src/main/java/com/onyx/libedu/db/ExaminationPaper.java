package com.onyx.libedu.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by li on 2017/7/28.
 */

public class ExaminationPaper {
    private static final String TAG = ExaminationPaper.class.getSimpleName();
    public static final String DB_TABLE_NAME = "library_examination_paper";
    public static final Uri CONTENT_URI = Uri.parse("content://" + OnyxCmsCenter.PROVIDER_AUTHORITY + "/" + DB_TABLE_NAME);
    private static final String TAG_ID = "id";

    public static class Column implements BaseColumns {
        public static final String PAPER_ID = "paperid";
        public static final String ACCOUNT = "account";
        public static final String CONTENT = "content";
        public static final String VERSION = "version";
        public static final String BOOK_ID = "bookid";
        public static final String SCORE = "score";
        public static final String CORRECT_COUNT = "correctCount";
        public static final String ERROR_COUNT = "errorCount";
        public static final String TIMES = "times";
        public static final String MODIFY_TIME = "modifyTime";
        public static final String LIMITS = "limits";

        private static boolean sColumnIndexesInitialized = false;
        private static int sColumnPaperID = -1;
        private static int sColumnAccount = -1;
        private static int sColumnContent = -1;
        private static int sColumnVersion = -1;
        private static int sColumnBookId = -1;

        private static String COLUMN_ARRAY[] = new String[]{PAPER_ID, ACCOUNT, CONTENT, VERSION, BOOK_ID, SCORE,
                CORRECT_COUNT, ERROR_COUNT, TIMES, MODIFY_TIME, LIMITS};

        private static HashMap<String, Integer> columnMap = new HashMap<String, Integer>();

        public static ContentValues createColumnDate(ExaminationPaper paper) {
            ContentValues values = new ContentValues();
            values.put(PAPER_ID, paper.paperId);
            values.put(ACCOUNT, paper.account);
            values.put(CONTENT, paper.content);
            values.put(VERSION, paper.version);
            values.put(BOOK_ID, paper.bookId);
            values.put(SCORE, paper.score);
            values.put(CORRECT_COUNT, paper.correctCount);
            values.put(ERROR_COUNT, paper.errorCount);
            values.put(TIMES, paper.times);
            values.put(MODIFY_TIME, paper.modifyTime);
            values.put(LIMITS, paper.limits);
            return values;
        }

        public static ExaminationPaper readerColumnDate(Cursor c) {
            if (columnMap.isEmpty()) {
                for (String col : COLUMN_ARRAY) {
                    int columnIndex = c.getColumnIndex(col);
                    if (columnIndex == -1) {
                        Log.w(TAG, "get column index failed: " + col);
                        columnMap.clear();
                        return null;
                    }

                    columnMap.put(col, columnIndex);
                }
            }

            String paperid = c.getString(columnMap.get(PAPER_ID));
            String account = c.getString(columnMap.get(ACCOUNT));
            String content = c.getString(columnMap.get(CONTENT));
            String version = c.getString(columnMap.get(VERSION));
            String bookid = c.getString(columnMap.get(BOOK_ID));
            String score = c.getString(columnMap.get(SCORE));
            String correctCount = c.getString(columnMap.get(CORRECT_COUNT));
            String errorCount = c.getString(columnMap.get(ERROR_COUNT));
            String times = c.getString(columnMap.get(TIMES));
            long modifyTime = c.getLong(columnMap.get(MODIFY_TIME));
            String limits = c.getString(columnMap.get(LIMITS));

            ExaminationPaper examinationPaper = new ExaminationPaper();
            examinationPaper.paperId = paperid;
            examinationPaper.account = account;
            examinationPaper.content = content;
            examinationPaper.version = version;
            examinationPaper.bookId = bookid;
            examinationPaper.score = score;
            examinationPaper.correctCount = correctCount;
            examinationPaper.errorCount = errorCount;
            examinationPaper.times = times;
            examinationPaper.modifyTime = modifyTime;
            examinationPaper.limits = limits;
            return examinationPaper;
        }

    }

    public String paperId = null;
    public String account = null;
    public String content = null;
    public String version = null;
    public String bookId = null;
    public String score = null;
    public String correctCount = null;
    public String errorCount = null;
    public String times = null;
    public long modifyTime = 0;
    public String limits = null;

    public static List<ExaminationPaper> getExaminationPaperByBookId(Context context, String bookId) {
        Cursor c = null;
        List<ExaminationPaper> paperList = new ArrayList<>();
        try {
            c = context.getContentResolver().query(CONTENT_URI, null, Column.BOOK_ID + "= ?", new String[]{bookId}, null);
            if (c == null) {
                Log.w(TAG, "getExaminationPaperByBookId: get examination failed");
                return null;
            }
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ExaminationPaper examinationPaper = Column.readerColumnDate(c);
                paperList.add(examinationPaper);
            }
            return paperList;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static ExaminationPaper getExaminationPaperByPaperId(Context context, String paperId) {
        Cursor c = null;
        ExaminationPaper paper = null;

        try {
            c = context.getContentResolver().query(CONTENT_URI, null, Column.PAPER_ID + "= ?", new String[]{paperId}, null);
            if (c == null) {
                Log.w(TAG, "getExaminationPaperByPaperId: get examination failed by paper id");
                return null;
            }
            if (c.moveToFirst()) {
                paper = Column.readerColumnDate(c);
            }
            return paper;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static boolean insertExaminationPaper(Context context, ExaminationPaper paper) {
        ContentValues columnDate = Column.createColumnDate(paper);
        if (columnDate == null) {
            Log.w(TAG, "insertExaminationPaper: create column data failed");
            return false;
        }
        Uri result = context.getContentResolver().insert(CONTENT_URI, columnDate);
        if (result == null) {
            return false;
        }

        String id = result.getLastPathSegment();
        if (id == null) {
            return false;
        }
        return true;
    }

    public static boolean updateExaminationPaper(Context context, ExaminationPaper paper) {
        ContentValues columnDate = Column.createColumnDate(paper);
        int count = context.getContentResolver().update(CONTENT_URI, columnDate, Column.PAPER_ID + "= ? AND " +
                Column.MODIFY_TIME + "= ?", new String[]{paper.paperId, paper.modifyTime + ""});
        if (count <= 0) {
            return false;
        }

        assert (count == 1);
        return true;
    }

    public static boolean deletePaper(Context context, ExaminationPaper paper) {
        int delete = context.getContentResolver().delete(CONTENT_URI, Column.PAPER_ID + "= ? AND " + Column.TIMES + "= ?",
                new String[]{paper.paperId, paper.times});
        if(delete <= 0) {
            return false;
        }
        return true;
    }
}
