package com.neverland.engbook.level2;

import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlFilesMOBI;
import com.neverland.engbook.util.AlPreferenceOptions;

public class AlScanMOBI extends AlScan {

    @Override
    public void initState(AlBookOptions bookOptions, AlFiles myParent, AlPreferenceOptions pref) {
        super.initState(bookOptions, myParent, pref);

        if (((AlFilesMOBI)aFiles).getCover() != -1)
            coverName = Integer.toString(((AlFilesMOBI)aFiles).getCover());

        bookTitle = ((AlFilesMOBI)aFiles).getTitle();
        bookGenres.addAll(((AlFilesMOBI)aFiles).getGanres());
        bookAuthors.addAll(((AlFilesMOBI) aFiles).getAuthors());

        size = 1;
    }
}
