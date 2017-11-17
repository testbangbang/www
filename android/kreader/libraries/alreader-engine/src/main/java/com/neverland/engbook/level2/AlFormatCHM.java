package com.neverland.engbook.level2;


import android.util.Log;

import com.neverland.engbook.allstyles.AlCSSHtml;
import com.neverland.engbook.allstyles.AlOneCSS;
import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFileZipEntry;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlFilesCHM;
import com.neverland.engbook.level1.AlOneZIPRecord;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.util.AlParProperty;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;
import com.neverland.engbook.util.InternalFunc;

import java.util.ArrayList;

public class AlFormatCHM extends AlFormatBaseHTML {

    public static boolean isCHM(AlFiles a) {
        if (a.getIdentStr().contentEquals("chm"))
            return true;
        return false;
    }

    //private String		    currentFile = null;
    private boolean         needUnpackAfterAllRead = false;
    //private boolean	        isCSSStyle = false;
    private int             level = 0;

    private int             defCP = 1251;

    public AlFormatCHM() {
        currentFile = null;
        cssStyles = new AlCSSHtml();
    }

    private char[] dataForEntity = null;

    @Override
    public void initState(AlBookOptions bookOptions, AlFiles myParent, AlPreferenceOptions pref, AlStylesOptions stl) {
        xml_mode = false;
        ident = "CHM";

        aFiles = myParent;

        if ((bookOptions.formatOptions & AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG) != 0)
            needUnpackAfterAllRead = true;

        preference = pref;
        styles = stl;

        noUseCover = bookOptions.noUseCover;
        size = 0;

        autoCodePage = true;
        defCP = ((AlFilesCHM)aFiles).chmCodePage;
        setCP(defCP);
        dataForEntity = data_cp;

        allState.state_parser = STATE_XML_SKIP;
        allState.clearSkipped();

        cssStyles.init(this, TAL_CODE_PAGES.CP65001, AlCSSHtml.CSSHTML_SET_HTML);
        if ((bookOptions.formatOptions & AlFiles.BOOKOPTIONS_DISABLE_CSS) != 0)
            cssStyles.disableExternal = true;

        parser(0, -1);


    }

    @Override
    protected void setCP(int newcp) {
        use_cpR0 = AlUnicode.int2cp(newcp);
        data_cp = AlUnicode.getDataCP(defCP);
    }

    @Override
    public boolean isNeedAttribute(int atr) {
        switch (atr) {
            case AlFormatTag.TAG_NUMBER:
            case AlFormatTag.TAG_CONTENT_TYPE:
            case AlFormatTag.TAG_TITLE:
            case AlFormatTag.TAG_NUMFILES:
            case AlFormatTag.TAG_VALUE:
            case AlFormatTag.TAG_TYPE:
            case AlFormatTag.TAG_REL:
                return true;
        }
        return super.isNeedAttribute(atr);
    }

    private String pair_name = null;
    private String pair_files = null;


    private void setDefPair() {
        pair_name = null;
        pair_files = null;
    }

    private void addPair() {
        //OneContent c = new OneContent();
        if (pair_files != null) {
            if (pair_files.contains("#"))
                return;

            String fullName = AlFiles.getAbsoluteName(currentFile, pair_files);

            int num_file = aFiles.getExternalFileNum(fullName);
            if (num_file != AlFiles.LEVEL1_FILE_NOT_FOUND) {
                ArrayList<AlFileZipEntry> fl = aFiles.getFileList();
                if (fl.get(num_file).flag == 1 && fl.get(num_file).position == -1) {
                    ((AlFilesCHM) aFiles).addFilesToRecord(num_file, AlOneZIPRecord.SPECIAL_NONE);
                    stop_posUsed = aFiles.getSize();
                }
            }
        }
       /* c.fileNameShort = name;
        c.start_pos = level;

        if (content == null)
            content = new ArrayList<AlFiles.OneContent>();

        content.add(c);*/
    }

    public boolean addImages() {
        if (active_file == UNKNOWN_FILE_SOURCE_NUM)
            return false;

        StringBuilder s = tag.getATTRValue(AlFormatTag.TAG_SRC);

        if (s == null)
            s = tag.getATTRValue(AlFormatTag.TAG_HREF);

        if (s != null) {
            String name = AlFiles.getAbsoluteName(currentFile, s.toString());

            int num_file = aFiles.getExternalFileNum(name);
            if (num_file != AlFiles.LEVEL1_FILE_NOT_FOUND)
                name = aFiles.getExternalAbsoluteFileName(num_file);

            addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
            addTextFromTag(name, false);
            addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);

            return false;
        }

        return false;
    }

    @Override
    void addtestLink(String s) {

            StringBuilder link = new StringBuilder();
            if (currentFile != null && currentFile.length() > 0) {
                link.append(currentFile);
                link.append('#');
            }
            link.append(s);
            super.addtestLink(link.toString());

    }

    private boolean addNotes() {
        StringBuilder param = tag.getATTRValue(AlFormatTag.TAG_HREF);

        if (param != null) {

            String s = param.toString();
            if (param.indexOf(":") == -1) {
                if (param.charAt(0) != '#') {
                    s = AlFiles.getAbsoluteName(currentFile, param.toString());
                } else {
                    s = currentFile + param;
                }

                StringBuilder s2 = new StringBuilder(s);
                int pos = s.indexOf('#');
                if (pos > 0)
                    s2.delete(pos, s2.length());

                int num_file = aFiles.getExternalFileNum(s2.toString());
                if (num_file != AlFiles.LEVEL1_FILE_NOT_FOUND) {
                    s2.setLength(0);
                    s2.append(aFiles.getExternalAbsoluteFileName(num_file));
                }

                if (pos > 0)
                    s2.append(s.substring(pos));

                s = s2.toString();
            }

            addCharFromTag((char) AlStyles.CHAR_LINK_S, false);
            addTextFromTag(s, false);
            addCharFromTag((char) AlStyles.CHAR_LINK_E, false);
            return true;
        }

        return false;
    }

    @Override
    protected int getEntityCode(String val, int base) {
        int c = InternalFunc.str2int(val, base);
        if (c >= 0x80 && c <= 0xff)
            return dataForEntity[c - 0x80];
        return c;
    };

    @Override
    protected char findEntity(String key) {
        Character ch = super.findEntity(key);
        if (ch >= 0x80 && ch <= 0xff)
            return dataForEntity[ch - 0x80];
        return ch;
    }

    @Override
    public boolean externPrepareTAG() {
        StringBuilder param;

        if ((allState.description & (AlStateLevel2.PAR_DESCRIPTION1 | AlStateLevel2.PAR_DESCRIPTION2 | AlStateLevel2.PAR_DESCRIPTION3 | AlStateLevel2.PAR_DESCRIPTION4)) == 0) {
            param = tag.getATTRValue(AlFormatTag.TAG_ID);
            if (param != null)
                addtestLink(param.toString());
        }

        switch (tag.tag) {
            case AlFormatTag.TAG_H1:
                if (tag.closed) {
                    newParagraph();
                    newEmptyTextParagraph();
                    setSpecialText(false);
                } else
                if (!tag.ended) {
                    newParagraph();
                    newEmptyTextParagraph();
                    setParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);
                } else {
                    newParagraph();
                    newEmptyTextParagraph();
                }
                return true;
            case AlFormatTag.TAG_LINK:
                if (tag.closed) {

                } else {

                    StringBuilder tp = tag.getATTRValue(AlFormatTag.TAG_TYPE);
                    StringBuilder rl = tag.getATTRValue(AlFormatTag.TAG_REL);
                    if ((tp != null && "text/css".contentEquals(tp)) ||
                        (rl != null && "stylesheet".contentEquals(rl))) {
                        tp = tag.getATTRValue(AlFormatTag.TAG_HREF);
                        if (tp != null)
                            cssStyles.parseFile(tp.toString(), currentFile, TAL_CODE_PAGES.CP65001, 0);
                    }

                }
                return true;
            case AlFormatTag.TAG_H2:
            case AlFormatTag.TAG_H3:
            case AlFormatTag.TAG_H4:
            case AlFormatTag.TAG_H5:
            case AlFormatTag.TAG_H6:
            case AlFormatTag.TAG_H7:
            case AlFormatTag.TAG_H8:
            case AlFormatTag.TAG_H9:
                if (tag.closed) {
                    newParagraph();
                    newEmptyTextParagraph();
                } else if (!tag.ended) {
                    newParagraph();
                    newEmptyTextParagraph();
                    setParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);
                } else {
                    newParagraph();
                    newEmptyTextParagraph();
                }
                return true;
            case AlFormatTag.TAG_IMG:
                if (tag.closed) {

                } else if (!tag.ended) {
                    addImages();
                } else {
                    addImages();
                }
                return true;
            case AlFormatTag.TAG_BODY:
                if (tag.closed) {
                    clearStateStyle(AlStateLevel2.PAR_NOTE);
                    newParagraph();
                    setPropStyle(AlParProperty.SL2_BREAK_BEFORE);
                } else if (!tag.ended) {
                    allState.decSkipped();
                    newParagraph();
                    cssStyles.enable = false;
                    cssStyles.fixWorkSet();
                } else {

                }
                return true;
            case AlFormatTag.TAG_HEAD:
                if (tag.closed) {
                    newParagraph();
                } else if (!tag.ended) {
                    newParagraph();
                } else {

                }
                return true;
            case AlFormatTag.TAG_OL:
            case AlFormatTag.TAG_UL:
                if ((allState.description & AlStateLevel2.PAR_DESCRIPTION1) != 0) {
                    if (tag.closed) {
                        if (level > 0)
                            level--;
                    } else if (!tag.ended) {
                        level++;
                    } else {

                    }
                } else {
                    if (tag.closed) {
                        decULNumber();
                    } else if (!tag.ended) {
                        incULNumber();
                    } else {

                    }
                }
                return true;
            case AlFormatTag.TAG_LI:
                if ((allState.description & AlStateLevel2.PAR_DESCRIPTION1) != 0) {
                    if (tag.closed) {
                        if (level > 0)
                            level--;
                    } else if (!tag.ended) {
                        level++;
                    } else {

                    }
                } else {
                    newParagraph();
                }
                return true;
            case AlFormatTag.TAG_OBJECT:
                if ((allState.description & AlStateLevel2.PAR_DESCRIPTION1) != 0) {
                    if (tag.closed) {
                        addPair();
                        clearStateStyle(AlStateLevel2.PAR_DESCRIPTION4);
                    } else
                    if (!tag.ended) {
                        setStateStyle(AlStateLevel2.PAR_DESCRIPTION4);
                        setDefPair();
                    } else {

                    }
                }
                return true;
            case AlFormatTag.TAG_PARAM:
                if ((allState.description & AlStateLevel2.PAR_DESCRIPTION4) != 0) {
                    if (tag.closed) {

                    } else
                    if (!tag.ended) {
                        StringBuilder name = tag.getATTRValue(AlFormatTag.TAG_NAME);
                        StringBuilder value = tag.getATTRValue(AlFormatTag.TAG_VALUE);

                        if (name != null && value != null) {
                            String n = name.toString();
                            String v = value.toString();
                            if (n.equalsIgnoreCase("Name") && v != null && v.trim().length() > 0)
                                pair_name = v;
                            if (n.equalsIgnoreCase("Local") && v != null && v.trim().length() > 0 && !v.trim().toLowerCase().contains(":"))
                                pair_files = v;
                        }
                    } else {

                    }
                }
                return true;
            case AlFormatTag.TAG_FRAME:
            case AlFormatTag.TAG_IFRAME:
                //if ((paragraph & (AlStyles.PAR_DESCRIPTION2 | AlStyles.PAR_DESCRIPTION3)) != 0) {
                    if (tag.closed) {

                    } else
                    if (!tag.ended) {
                        //////////////////////////////////
                        StringBuilder n = tag.getATTRValue(AlFormatTag.TAG_SRC);
                        if (n != null && n.indexOf(":") == -1) {
                            pair_files = n.toString();
                            addPair();
                        }
                        //////////////////////////////////
                    } else {

                    }
                //}
                return true;
            case AlFormatTag.TAG_A:
                if (tag.closed) {
                    if ((styleStack.buffer[styleStack.position].paragraph & AlStyles.STYLE_LINK) != 0)
                        clearTextStyle(AlStyles.STYLE_LINK);
                } else if (!tag.ended) {

                    //////////////////////////////////
                    StringBuilder n = tag.getATTRValue(AlFormatTag.TAG_HREF);
                    if (n != null && n.indexOf(":") == -1) {
                        pair_files = n.toString();
                        addPair();
                    }
                    //////////////////////////////////

                    param = tag.getATTRValue(AlFormatTag.TAG_NAME);
                    if (param != null)
                        addtestLink(param.toString());


                    if (addNotes())
                        setTextStyle(AlStyles.STYLE_LINK);
                } else {

                }
                return true;
            case AlFormatTag.TAG_EXTFILE:
                if (tag.closed) {

                    clearULNumber();

                    cssStyles.disableWorkSet();
                    cssStyles.enable = true;

                    if ((styleStack.buffer[styleStack.position].paragraph & (AlStateLevel2.PAR_DESCRIPTION1/* | AlStyles.PAR_DESCRIPTION3*/)) != 0 ||
                            allState.start_position + 10 >= stop_posUsed) {
                        ArrayList<AlFileZipEntry> fl = aFiles.getFileList();
                        for (int i = 0; i < fl.size(); i++) {
                            if (fl.get(i).flag == 1 && fl.get(i).position == -1)
                                ((AlFilesCHM)aFiles).addFilesToRecord(i, AlOneZIPRecord.SPECIAL_NONE);
                        }
                        stop_posUsed = aFiles.getSize();
                    }

                    newParagraph();

                    setPropStyle(AlParProperty.SL2_BREAK_BEFORE);

                    clearStateStyle(AlStateLevel2.PAR_DESCRIPTION1 | AlStateLevel2.PAR_DESCRIPTION2 |
                            AlStateLevel2.PAR_DESCRIPTION3 | AlStateLevel2.PAR_DESCRIPTION4 | AlStateLevel2.PAR_NOTE);

                    active_file = UNKNOWN_FILE_SOURCE_NUM;
                    currentFile = null;
                    allState.decSkipped();
                } else
                if (!tag.ended) {
                    allState.incSkipped();

                    param = tag.getATTRValue(AlFormatTag.TAG_NUMFILES);
                    if (param != null) {
                        active_file = InternalFunc.str2int(param, 10);
                    }

                    param = tag.getATTRValue(AlFormatTag.TAG_ID);
                    if (param != null) {
                        currentFile = param.toString();
                    }

                    param = tag.getATTRValue(AlFormatTag.TAG_IDREF);
                    if (param != null) {
                        switch (InternalFunc.str2int(param, 10)) {
                            case AlOneZIPRecord.SPECIAL_CHM_HHC:
                                setStateStyle(AlStateLevel2.PAR_DESCRIPTION1);
                                level = 0;
                                break;
                            /*case AlOneZIPRecord.SPECIAL_CHM_INDEXSTART:
                                setParagraphStyle(AlStyles.PAR_DESCRIPTION2);
                                break;
                            case AlOneZIPRecord.SPECIAL_CHM_INDEXEND:
                                setParagraphStyle(AlStyles.PAR_DESCRIPTION3);
                                break;*/
                        }
                    }

                } else {

                }
                return true;


            case AlFormatTag.TAG_TABLE:
            case AlFormatTag.TAG_TR:
            case AlFormatTag.TAG_TH:
            case AlFormatTag.TAG_TD:
            case AlFormatTag.TAG_TBL:
            case AlFormatTag.TAG_TC:
            case AlFormatTag.TAG_GRIDSPAN:
                newParagraph();
                return true;
        }

        /*if ((paragraph & (AlStyles.PAR_NOTE | AlStyles.PAR_DESCRIPTION1 | AlStyles.PAR_DESCRIPTION2 |
                AlStyles.PAR_DESCRIPTION3 | AlStyles.PAR_DESCRIPTION4)) != 0)
            return false;*/

        return super.externPrepareTAG();
    }
}
