package com.neverland.engbook.level2;

import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlOneContent;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlFilesEPUB;
import com.neverland.engbook.level1.AlOneZIPRecord;
import com.neverland.engbook.level1.JEBFilesEPUB;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;
import com.neverland.engbook.util.InternalFunc;

import java.util.ArrayList;
import java.util.HashMap;

public class JEBFormatEPUB extends AlFormatEPUB {

    public static boolean isJEB(AlFiles a) {
        //noinspection RedundantIfStatement
        if (a.getIdentStr().contentEquals("jeb"))
            return true;
        return false;
    }

}
