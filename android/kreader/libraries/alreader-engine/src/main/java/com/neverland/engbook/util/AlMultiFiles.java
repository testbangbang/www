package com.neverland.engbook.util;

import java.util.ArrayList;

public class AlMultiFiles {
    public static final int		LEVEL_FOR_MULTI = 32;

    public boolean					modePart;
    public boolean					isMultiFiles;
    public ArrayList<AlOneMultiFile>	collect = new ArrayList<>();

    public int						savedPosition;
    public long						queryWaitingPosition;
    public long						queryRealPosition;

    public int						firstMultiFile;
    public int						firstMultiFileReal;
    public int						counterPart;

    public long						correctionPos;

    public void addFile(int l1s, int l2s) {
        collect.add(AlOneMultiFile.add(l1s, l2s));
    }

    public AlMultiFiles() {
        correctionPos = 0;
        isMultiFiles = false;
        collect.clear();
        firstMultiFile = -1;
        queryRealPosition = queryWaitingPosition = 0;
        savedPosition = counterPart = 0;
        modePart = false;
    }
}
