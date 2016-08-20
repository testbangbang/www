package com.onyx.android.edu.db.dataprovider;

import com.onyx.android.edu.db.model.Chapter;
import com.onyx.android.edu.db.model.Chapter_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/6/29.
 */
public class ChapterDataProvider {

    private static final String TAG = "ChapterDataProvider";

    public static List<Chapter> getChapterList(final List<Long> chapterIds){
        List<Chapter> chapterList = new ArrayList<>();
        int size = chapterIds.size();
        Select select = new Select();
        for (int i = 0; i < size; i++) {
            String str = String.valueOf(chapterIds.get(i));
            Long id = Long.valueOf(str);
            Chapter chapter = select.from(Chapter.class).where(Chapter_Table.uniqueId.eq(id)).querySingle();
            if (chapter != null){
                chapterList.add(chapter);
            }
        }
        return chapterList;
    }
}
