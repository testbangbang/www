package com.onyx.android.sdk.reader.dataprovider;

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
 * Created by li on 2017/7/29.
 */

public class PaperQuestionAndAnswer {
    private static final String TAG = PaperQuestionAndAnswer.class.getSimpleName();
    public static final String DB_TABLE_NAME = "library_paper_question_answer";
    public static final Uri CONTENT_URI = Uri.parse("content://" + OnyxCmsCenter.PROVIDER_AUTHORITY + "/" + DB_TABLE_NAME);
    private static final String TAG_ID = "id";

    public static class Column implements BaseColumns {
        public static final String BOOK_ID = "bookid";
        public static final String PAPER_ID = "paperid";
        public static final String REQUESTION_ID = "requestionId";
        public static final String QUESTION = "question";
        public static final String OPTION1 = "option1";
        public static final String OPTION2 = "option2";
        public static final String OPTION3 = "option3";
        public static final String OPTION4 = "option4";
        public static final String ANSWER = "answer";
        public static final String USER_ANSWER = "userAnswer";
        public static final String GET_SCORE = "getScore";
        public static final String TIMES = "times";

        private static String ARRAY_COLUMN[] = new String[]{BOOK_ID, PAPER_ID, REQUESTION_ID, QUESTION,
                OPTION1, OPTION2, OPTION3, OPTION4, ANSWER, USER_ANSWER, GET_SCORE, TIMES};
        private static HashMap<String, Integer> columnMap = new HashMap<String, Integer>();

        public static ContentValues createValuesFromObject(PaperQuestionAndAnswer paper) {
            ContentValues values = new ContentValues();
            values.put(BOOK_ID, paper.bookId);
            values.put(PAPER_ID, paper.paperId);
            values.put(REQUESTION_ID, paper.requestionId);
            values.put(QUESTION, paper.question);
            values.put(OPTION1, paper.option1);
            values.put(OPTION2, paper.option2);
            values.put(OPTION3, paper.option3);
            values.put(OPTION4, paper.option4);
            values.put(ANSWER, paper.answer);
            values.put(USER_ANSWER, paper.userAnswer);
            values.put(GET_SCORE, paper.getScore);
            values.put(TIMES, paper.times);
            return values;
        }

        public static PaperQuestionAndAnswer readColumnData(Cursor c) {
            if (columnMap.isEmpty()) {
                for (String column : ARRAY_COLUMN) {
                    int columnIndex = c.getColumnIndex(column);
                    if (columnIndex == -1) {
                        Log.w(TAG, "readColumnData: get column index failed");
                        return null;
                    }
                    columnMap.put(column, columnIndex);
                }
            }

            String bookId = c.getString(columnMap.get(BOOK_ID));
            String paperId = c.getString(columnMap.get(PAPER_ID));
            String requestionId = c.getString(columnMap.get(REQUESTION_ID));
            String question = c.getString(columnMap.get(QUESTION));
            String option1 = c.getString(columnMap.get(OPTION1));
            String option2 = c.getString(columnMap.get(OPTION2));
            String option3 = c.getString(columnMap.get(OPTION3));
            String option4 = c.getString(columnMap.get(OPTION4));
            String answer = c.getString(columnMap.get(ANSWER));
            String userAnswer = c.getString(columnMap.get(USER_ANSWER));
            String getScore = c.getString(columnMap.get(GET_SCORE));
            String times = c.getString(columnMap.get(TIMES));

            PaperQuestionAndAnswer paperQuestionAndAnswer = new PaperQuestionAndAnswer();
            paperQuestionAndAnswer.bookId = bookId;
            paperQuestionAndAnswer.paperId = paperId;
            paperQuestionAndAnswer.requestionId = requestionId;
            paperQuestionAndAnswer.question = question;
            paperQuestionAndAnswer.option1 = option1;
            paperQuestionAndAnswer.option2 = option2;
            paperQuestionAndAnswer.option3 = option3;
            paperQuestionAndAnswer.option4 = option4;
            paperQuestionAndAnswer.answer = answer;
            paperQuestionAndAnswer.userAnswer = userAnswer;
            paperQuestionAndAnswer.getScore = getScore;
            paperQuestionAndAnswer.times = times;

            return paperQuestionAndAnswer;
        }
    }

    public String bookId = null;
    public String paperId = null;
    public String requestionId = null;
    public String question = null;
    public String option1 = null;
    public String option2 = null;
    public String option3 = null;
    public String option4 = null;
    public String answer = null;
    public String userAnswer = null;
    public String getScore = null;
    public String times = null;

    public static List<PaperQuestionAndAnswer> getAnswerPaperByTimes(Context context, String paperId, String times) {
        Cursor c = null;
        ArrayList<PaperQuestionAndAnswer> list = new ArrayList<>();
        try {
            c = context.getContentResolver().query(CONTENT_URI, null, Column.PAPER_ID + "= ? AND " +
                    Column.TIMES + "= ?", new String[]{paperId, times}, null);
            if (c == null) {
                Log.w(TAG, "getPaperQuestionAndAnswerByBookId: get paper failed");
                return null;
            }

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                PaperQuestionAndAnswer paperQuestionAndAnswer = Column.readColumnData(c);
                list.add(paperQuestionAndAnswer);
            }
            return list;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static boolean deleteAnswerPaperByTimes(Context context, String paperId, String times) {
        int delete = context.getContentResolver().delete(CONTENT_URI, Column.PAPER_ID + "= ? AND " + Column.TIMES +
                "= ?", new String[]{paperId, times});
        if(delete <= 0) {
            return false;
        }
        return true;
    }

    public static List<PaperQuestionAndAnswer> getPaperQuestionAndAnswerByBookId(Context context, String bookId) {
        Cursor c = null;
        ArrayList<PaperQuestionAndAnswer> list = new ArrayList<>();
        try {
            c = context.getContentResolver().query(CONTENT_URI, null, Column.BOOK_ID + "= ?", new String[]{bookId}, null);
            if (c == null) {
                Log.w(TAG, "getPaperQuestionAndAnswerByBookId: get paper failed");
                return null;
            }

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                PaperQuestionAndAnswer paperQuestionAndAnswer = Column.readColumnData(c);
                boolean flag = false;
                if (list != null && list.size() > 0) {
                    for (PaperQuestionAndAnswer paper : list) {
                        if (paperQuestionAndAnswer.requestionId.equals(paper.requestionId)) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) {
                    continue;
                }
                list.add(paperQuestionAndAnswer);
            }
            return list;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static boolean updateAnswerById(Context context, String bookId, long requestionId, String userAnswer, String score) {
        PaperQuestionAndAnswer paperQuestionAndAnswer = null;
        Cursor c = null;
        try {
            c = context.getContentResolver().query(CONTENT_URI, null, Column.BOOK_ID + " = ? AND " +
                            Column.REQUESTION_ID + "= ?",
                    new String[]{bookId, requestionId + ""}, null);
            if (c == null) {
                Log.d(TAG, "updatePaperQuestionAndAnswerById: query by requestionId failed");
                return false;
            }

            if (c.moveToFirst()) {
                paperQuestionAndAnswer = Column.readColumnData(c);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        if (paperQuestionAndAnswer == null) {
            Log.d(TAG, "updateAnswerById: get paper failed");
            return false;
        }
        paperQuestionAndAnswer.userAnswer = userAnswer;
        paperQuestionAndAnswer.getScore = score;
        int count = context.getContentResolver().update(CONTENT_URI, Column.createValuesFromObject(paperQuestionAndAnswer),
                Column.BOOK_ID + "= ? AND " + Column.REQUESTION_ID + " = ?",
                new String[]{paperQuestionAndAnswer.bookId, paperQuestionAndAnswer.requestionId});
        if (count <= 0) {
            return false;
        }

        return true;
    }

    public static List<PaperQuestionAndAnswer> getAnswerPaperById(Context context, String bookId, long requestionId) {
        List<PaperQuestionAndAnswer> list = new ArrayList<>();
        Cursor c = null;
        try {
            c = context.getContentResolver().query(CONTENT_URI, null, Column.BOOK_ID + " = ? AND " +
                            Column.REQUESTION_ID + "= ?",
                    new String[]{bookId, requestionId + ""}, null);
            if (c == null) {
                Log.d(TAG, "updatePaperQuestionAndAnswerById: query by requestionId failed");
                return null;
            }

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                PaperQuestionAndAnswer paperQuestionAndAnswer = Column.readColumnData(c);
                list.add(paperQuestionAndAnswer);
            }
            return list;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static boolean insertPaperQuestionAndAnswer(Context context, PaperQuestionAndAnswer paper) {
        ContentValues values = Column.createValuesFromObject(paper);
        Uri result = context.getContentResolver().insert(CONTENT_URI, values);
        if (result == null) {
            return false;
        }

        String id = result.getLastPathSegment();
        if (id == null) {
            return false;
        }
        return true;
    }

    public static boolean updatePaperQuestionAndAnswer(Context context, PaperQuestionAndAnswer paper) {
        ContentValues values = Column.createValuesFromObject(paper);
        int count = context.getContentResolver().update(CONTENT_URI, values, Column.BOOK_ID + "= ? AND " +
                        Column.REQUESTION_ID + " = ? AND " + Column.TIMES + "= ?",
                new String[]{paper.bookId, paper.requestionId, paper.times});
        if (count < 0) {
            return false;
        }
        return true;
    }
}
