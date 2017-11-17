package com.neverland.engbook.allstyles;

import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlRandomAccessFile;
import com.neverland.engbook.level2.AlFormat;
import com.neverland.engbook.level2.AlFormatTag;
import com.neverland.engbook.util.AlOneXMLAttrClass;
import com.neverland.engbook.util.AlStyleStack;
import com.neverland.engbook.util.AlStyles;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class AlCSSStyles {

    public static String	DEFAULT_CSS_ALL =
            "p:first-letter {font-family:fantasy; font-size:3rem;} p:notes {font-size:0.83rem; font-stretch:condensed;}";

    public boolean		isKindle = false;

    public AlCSSStyles() {

    }

    public void debugStyles(AlRandomAccessFile df) {

        String ustr;
        byte[] bb = null;

        for (int i = 0; i < allSets.size(); i++) {
            ustr = "\n\r\n\rCSS file " + Integer.toString(i) + ' ' + allSets.get(i).name;
            try {
                bb = ustr.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            df.write(bb);

            for (int j = 0; j < allSets.get(i).usingSet.size(); j++) {
                ustr = "\n\r use " + Integer.toString(allSets.get(i).usingSet.get(j));
                try {
                    bb = ustr.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                df.write(bb);
            }

            for (int j = 0; j < allSets.get(i).setTAG.size(); j++) {
                ustr = ("\n\r") + allSets.get(i).setTAG.get(j).outString();
                try {
                    bb = ustr.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                df.write(bb);
            }

            for (int j = 0; j < allSets.get(i).setCLASS.size(); j++) {
                ustr = ("\n\r") + allSets.get(i).setCLASS.get(j).outString();
                try {
                    bb = ustr.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                df.write(bb);
            }

            for (int j = 0; j < allSets.get(i).setTAGCLASS.size(); j++) {
                ustr = ("\n\r") + allSets.get(i).setTAGCLASS.get(j).outString();
                try {
                    bb = ustr.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                df.write(bb);
            }
        }

        ustr = ("\n\r");
        try {
            bb = ustr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        df.write(bb);

        for (int i = 0; i < collection.size(); i++) {
            ustr = "\n\rCollect#" + Integer.toString(i);

            for (int j = 0; j < collection.get(i).s.size(); j++) {
                ustr += " " + Integer.toString(collection.get(i).s.get(j));
            }

            try {
                bb = ustr.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            df.write(bb);
        }
    }

    public boolean parseFile(String nameCSSFile, String nameCurrentFile, int cp, int setNum) {
        if (!enable)
            return false;

        int current = 0;
        byte[] data = null;
        int sz = 0;

        String name = AlFiles.getAbsoluteName(nameCurrentFile, nameCSSFile);

        for (int i = 1; i < allSets.size(); i++) {
            if (allSets.get(i).name.contentEquals(name)) {
                if (allSets.get(i).usingSet.size() > 0 || allSets.get(i).setTAG.size() > 0 || allSets.get(i).setCLASS.size() > 0 || allSets.get(i).setTAGCLASS.size() > 0) {
                    if (setNum > 0) {
                        allSets.get(setNum).usingSet.add(i);
                    } else {
                        addAllUsing2WorkSet(i);
                    }
                }
                return true;
            }
        }

        AlSetCSS a = new AlSetCSS(this);
        a.name = name;
        allSets.add(a);
        current = allSets.size() - 1;

        if (format == null)
            return false;

        int fnum = format.aFiles.getExternalFileNum(name);
        if (fnum != AlFiles.LEVEL1_FILE_NOT_FOUND) {
            sz = format.aFiles.getExternalFileSize(fnum);
            if (sz > 0) {
                data = new byte[sz + 4];
                if (data != null) {
                    if (!format.aFiles.fillBufFromExternalFile(fnum, 0, data, 0, sz)) {
                        sz = 0;
                    }
                }
            }
        }

        if (data != null && sz > 0) {
            parse(current, data, sz, 0, cp4f, name, current);

            /*if (allSets.get(current).usingSet.size() > 0 || allSets.get(current).setTAG.size() > 0 ||
                    allSets.get(current).setCLASS.size() > 0 || allSets.get(current).setTAGCLASS.size() > 0) {
                if (setNum > 0) {
                    allSets.get(setNum).usingSet.add(current);
                } else {
                    addAllUsing2WorkSet(current);
                }
            }*/
            if (allSets.get(current).usingSet.size() > 0 || allSets.get(current).setTAG.size() > 0 || allSets.get(current).setCLASS.size() > 0 || allSets.get(current).setTAGCLASS.size() > 0) {

                if (disableExternal) {
                    for (int j = 0; j < allSets.get(current).setTAG.size(); j++) {
                        allSets.get(current).setTAG.get(j).val.m1 &= AlOneCSS.ENABLE_MASK1_RESTRICTION;
                        allSets.get(current).setTAG.get(j).val.v1 &= AlOneCSS.ENABLE_MASK1_RESTRICTION;
                    }
                    for (int j = 0; j < allSets.get(current).setCLASS.size(); j++) {
                        allSets.get(current).setCLASS.get(j).val.m1 &= AlOneCSS.ENABLE_MASK1_RESTRICTION;
                        allSets.get(current).setCLASS.get(j).val.v1 &= AlOneCSS.ENABLE_MASK1_RESTRICTION;
                    }
                    for (int j = 0; j < allSets.get(current).setTAGCLASS.size(); j++) {
                        allSets.get(current).setTAGCLASS.get(j).val.m1 &= AlOneCSS.ENABLE_MASK1_RESTRICTION;
                        allSets.get(current).setTAGCLASS.get(j).val.v1 &= AlOneCSS.ENABLE_MASK1_RESTRICTION;
                    }
                }

                if (setNum > 0) {
                    allSets.get(setNum).usingSet.add(current);
                } else {
                    addAllUsing2WorkSet(current);
                }
            }
        }

        data = null;

        return true;
    }

    protected void addAllUsing2WorkSet(int num) {
        for (int i = 0; i < allSets.get(num).usingSet.size(); i++) {
            addAllUsing2WorkSet(allSets.get(num).usingSet.get(i));
        }
        currentSet.s.add(num);
    }

    public boolean parseTemp(StringBuilder data, AlStyleStack stack) {
        byte[] bt = null;
        try {
            bt = data.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return parseTemp(bt, bt.length, stack);
    }

    public boolean parseTemp(byte[] data, int len, AlStyleStack stack) {
        AlOneCSSPair res = parse(-1, data, len, 100, TAL_CODE_PAGES.CP65001, null, -1);
        if (res.m0 != 0 || res.m1 != 0) {

            if (disableExternal) {
                res.m1 &= AlOneCSS.ENABLE_MASK1_RESTRICTION;
                res.v1 &= AlOneCSS.ENABLE_MASK1_RESTRICTION;
            }

            AlSetCSS.applyValue(res, stack);
            return true;
        }
        return false;
    }


    public boolean parseBuffer(StringBuilder data, String nameCurrentFile) {
        byte[] bt = null;
        try {
            bt = data.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (bt != null)
            return parseBuffer(bt, bt.length, TAL_CODE_PAGES.CP65001, nameCurrentFile);
        return false;
    }

    public boolean parseBuffer(byte[] data, int len, int cp, String nameCurrentFile) {
        if (!enable)
            return false;

        String name = nameCurrentFile + ".local.css\u0001";

        int current = 0;
        for (int i = 1; i < allSets.size(); i++) {
            if (allSets.get(i).name == name) {
                current = i;
                break;
            }
        }

        if (current == 0) {
            AlSetCSS a = new AlSetCSS(this);
            a.name = name;
            allSets.add(a);
            current = allSets.size() - 1;
        }

        parse(current, data, len, 0, cp, name, current);

        if (allSets.get(current).usingSet.size() > 0 || allSets.get(current).setTAG.size() > 0 || allSets.get(current).setCLASS.size() > 0 || allSets.get(current).setTAGCLASS.size() > 0) {
            if (disableExternal) {
                for (int j = 0; j < allSets.get(current).setTAG.size(); j++) {
                    allSets.get(current).setTAG.get(j).val.m1 &= AlOneCSS.ENABLE_MASK1_RESTRICTION;
                    allSets.get(current).setTAG.get(j).val.v1 &= AlOneCSS.ENABLE_MASK1_RESTRICTION;
                }
                for (int j = 0; j < allSets.get(current).setCLASS.size(); j++) {
                    allSets.get(current).setCLASS.get(j).val.m1 &= AlOneCSS.ENABLE_MASK1_RESTRICTION;
                    allSets.get(current).setCLASS.get(j).val.v1 &= AlOneCSS.ENABLE_MASK1_RESTRICTION;
                }
                for (int j = 0; j < allSets.get(current).setTAGCLASS.size(); j++) {
                    allSets.get(current).setTAGCLASS.get(j).val.m1 &= AlOneCSS.ENABLE_MASK1_RESTRICTION;
                    allSets.get(current).setTAGCLASS.get(j).val.v1 &= AlOneCSS.ENABLE_MASK1_RESTRICTION;
                }
            }
            addAllUsing2WorkSet(current);
        }

        return true;
    }

    public void disableWorkSet() {
        //css_map.clear();
        currentSet = new AlOneWorkSet();
    }

    public int fixWorkSet() {
        if (currentSet.s.size() == 0)
            return -1;

        boolean equal = collection.size() > 0 && collection.get(collection.size() - 1).s.size() == currentSet.s.size();
        if (equal) {
            for (int i = 0; i < currentSet.s.size(); i++) {
                if (currentSet.s.get(i) != collection.get(collection.size() - 1).s.get(i)) {
                    equal = false;
                    break;
                }
            }
        }

        if (!equal)
            collection.add(currentSet);

        return collection.size() - 1;
    }

    public void enableWorkSet(int num) {
        if (num == lastUsedSet)
            return;

        currentSet = new AlOneWorkSet();
        /*#if CSS_OPIMIZE_MAP
        currentSet.m.clear();
        #endif*/

        if (num >= 0 && num < collection.size()) {

            for (int i = 0; i < collection.get(num).s.size(); i++) {
                currentSet.s.add(collection.get(num).s.get(i));
            }
        }

        lastUsedSet = num;
    }

    public boolean apply1ClassX(int tag, AlOneXMLAttrClass cls, AlStyleStack stack) {
        if (cls.count == 0x00) {
            return apply1OneClassX(tag, 0x00, stack);
        } else {
            boolean res = false;
            for (int i = 0; i < cls.count; i++)
                res |= apply1OneClassX(tag, cls.hash[i], stack);
            return res;
        }
    };

    public boolean apply1OneClassX(int tag, long clsX, AlStyleStack stack) {
        /*long mKey = tag;
        if (clsX != 0)
            mKey = (long)tag ^ clsX;

        AlOneCSSPair m = css_map.get(mKey);
        if (m != null) {
            if (m.m0 == 0 && m.m1 == 0)
                return false;
            AlSetCSS.applyValue(m, stack);
            return true;
        }

        ////////////////////////////
        m = new AlOneCSSPair();
        long res = allSets.get(0).apply1(tag, clsX, m);
        for (int i = 0; i < currentSet.s.size(); i++) {
            res |= allSets.get(currentSet.s.get(i)).apply1(tag, clsX, m);
        }

        css_map.put(mKey, m);

        if (res != 0) {
            AlSetCSS.applyValue(m, stack);
            return true;
        }

        return false;
*/

        ////////////////////////////
        internalPair.clear();
        long res = allSets.get(0).apply1(tag, clsX, internalPair);
        for (int i = 0; i < currentSet.s.size(); i++) {
            res |= allSets.get(currentSet.s.get(i)).apply1(tag, clsX, internalPair);
        }

        if (res != 0) {
            AlSetCSS.applyValue(internalPair, stack);
            return true;
        }

        return false;
    };

    private AlOneCSSPair			internalPair = new AlOneCSSPair();

    //AlOneMapKey				    mKey = new AlOneMapKey();
    //public HashMap<Long, AlOneCSSPair> css_map = new HashMap<>();

    public boolean				enable = false;
    public boolean				supportFontSize = false;

    public boolean		        disableExternal = false;

    protected AlFormat			format = null;
    protected int               cp4f = TAL_CODE_PAGES.CP65001;
    protected ArrayList<AlSetCSS> allSets = new ArrayList<>();

    protected int				lastUsedSet;
    protected AlOneWorkSet		currentSet = new AlOneWorkSet();
    protected ArrayList<AlOneWorkSet>collection = new ArrayList<>();

    public abstract void init(AlFormat f, int cp4files, int useSet);
    protected abstract AlOneCSSPair		parse(int usesets, byte[] data, int len, int state, int cp, String cFile, int setNum);
}
