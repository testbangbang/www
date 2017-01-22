package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.unicode.AlUnicode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AlFilesCBZ extends AlFilesZIPRecord {

    private class AlCBZFiles {
        String realName = null;
        String sortName = null;

        AlCBZFiles(String name, String ext) {
            realName = name + ext;
            sortName = AlUnicode.getStringForAlphabeticSort(name, 6);
        }
    }

    private class ComparatorAlCBZFiles implements Comparator {
        public int compare(Object  arg0, Object  arg1) {
            return ((AlCBZFiles)arg0).sortName.compareTo(((AlCBZFiles)arg1).sortName);
        }
    }

    private static final String LEVEL1_CBZ_IMAGE_EXT_ZIP = ".cbz";
    private static final String LEVEL1_CBZ_IMAGE_EXT_RAR = ".cbr";
    private static final String LEVEL1_CBZ_IMAGE_EXT1 = ".jpg";
    private static final String LEVEL1_CBZ_IMAGE_EXT2 = ".jpeg";
    private static final String LEVEL1_CBZ_IMAGE_EXT3 = ".png";

    @Override
    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
	    /*addon_support = true;*/
        super.initState(file, myParent, fList);

        fileName = null;

        ident = "cbzr";

        recordList.clear();

        size = 0;
        ArrayList<AlCBZFiles> imageList = new ArrayList<>();
        ArrayList<AlFileZipEntry> parentList = myParent.getFileList();

        boolean need = false;
        for (int i = 0; i < parentList.size(); i++) {
            int tmp = parentList.get(i).name.lastIndexOf('.');
            if (tmp == -1) {
                continue;
            }

            String fext = parentList.get(i).name.substring(tmp, parentList.get(i).name.length()).toLowerCase();

            if (LEVEL1_CBZ_IMAGE_EXT1.contentEquals(fext)) {need = true;} else
            if (LEVEL1_CBZ_IMAGE_EXT2.contentEquals(fext)) {need = true;} else
            if (LEVEL1_CBZ_IMAGE_EXT3.contentEquals(fext)) {need = true;}

            if (need) {
                AlCBZFiles a = new AlCBZFiles(parentList.get(i).name.substring(0, tmp),
                        parentList.get(i).name.substring(tmp, parentList.get(i).name.length()));
                imageList.add(a);
            }
        }

        Collections.sort(imageList, new ComparatorAlCBZFiles());

        for (int i = 0; i < imageList.size(); i++) {
            addFilesToRecord(imageList.get(i).realName, AlOneZIPRecord.SPECIAL_IMAGE);
        }

        return TAL_RESULT.OK;
    }

    public static EngBookMyType.TAL_FILE_TYPE isCBZFile(AlFiles a, String ext, boolean verifyRAR) {
        if (verifyRAR) {
            if (a == null || ext == null || !LEVEL1_CBZ_IMAGE_EXT_RAR.contentEquals(ext))
                return EngBookMyType.TAL_FILE_TYPE.TXT;
        } else {
            if (a == null || ext == null || !LEVEL1_CBZ_IMAGE_EXT_ZIP.contentEquals(ext))
                return EngBookMyType.TAL_FILE_TYPE.TXT;
        }

        ArrayList<AlFileZipEntry> parentList = a.getFileList();

        if (parentList == null)
            return EngBookMyType.TAL_FILE_TYPE.TXT;

        int cnt = 0;
        for (int i = 0; i < parentList.size(); i++) {
            int tmp = parentList.get(i).name.lastIndexOf('.');
            if (tmp == -1) {
                continue;
            }

            String fext = parentList.get(i).name.substring(tmp, parentList.get(i).name.length()).toLowerCase();

            if (LEVEL1_CBZ_IMAGE_EXT1.contentEquals(fext)) cnt++;
            if (LEVEL1_CBZ_IMAGE_EXT2.contentEquals(fext)) cnt++;
            if (LEVEL1_CBZ_IMAGE_EXT3.contentEquals(fext)) cnt++;

            if (cnt > 0)
                break;
        }

        return cnt > 0 ? EngBookMyType.TAL_FILE_TYPE.CBZ : EngBookMyType.TAL_FILE_TYPE.TXT;
    }
}
