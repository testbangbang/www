package com.neverland.engbook.level2;

import com.neverland.engbook.allstyles.AlCSSHtml;
import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlOneContent;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlFilesMOBI;
import com.neverland.engbook.util.AlOneLink;
import com.neverland.engbook.util.AlParProperty;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;
import com.neverland.engbook.util.Base32Hex;
import com.neverland.engbook.util.InternalFunc;

import java.util.ArrayList;

import static com.neverland.engbook.forpublic.EngBookMyType.AL_ROOT_RIGHTPATH_STR;

public class AlFormatMOBI extends AlFormatBaseHTML {

    public static boolean isMOBI(AlFiles a) {
        //noinspection RedundantIfStatement
        if (a.getIdentStr().startsWith("mobi"))
            return true;
        return false;
    }

    private int         firstInsertCover = -1;

    private ArrayList<AlFilesMOBI.MOBITOC> level1TOC = null;

    private class MOBIFootnote {
        int start;
        int stop;
    }

    private int			footstart = -1;
    private final ArrayList<MOBIFootnote> footnotes = new ArrayList<>();

    public AlFormatMOBI() {
        firstInsertCover = -1;
        currentFile = AL_ROOT_RIGHTPATH_STR + '_';
        cssStyles = new AlCSSHtml();
    }

    @Override
    public void initState(AlBookOptions bookOptions, AlFiles myParent, AlPreferenceOptions pref, AlStylesOptions stl) {
        xml_mode = true;
        ident = "MOBI";

        aFiles = myParent;

        if ((bookOptions.formatOptions & AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG) != 0)
            aFiles.needUnpackData();

        preference = pref;
        styles = stl;

        noUseCover = bookOptions.noUseCover;
        size = 0;

        autoCodePage = false;
        setCP(aFiles.getCodePage());

        if (((AlFilesMOBI)aFiles).getCover() != -1)
            coverName = Integer.toString(((AlFilesMOBI)aFiles).getCover());

        bookTitle = ((AlFilesMOBI)aFiles).getTitle();
        bookGenres.addAll(((AlFilesMOBI)aFiles).getGanres());
        bookAuthors.addAll(((AlFilesMOBI)aFiles).getAuthors());
        level1TOC = ((AlFilesMOBI)aFiles).getTOC();

        footstart = -1;

        allState.clearSkipped();

        cssStyles.init(this, TAL_CODE_PAGES.CP65001, AlCSSHtml.CSSHTML_SET_MOBI);
        if ((bookOptions.formatOptions & AlFiles.BOOKOPTIONS_DISABLE_CSS) != 0)
            cssStyles.disableExternal = true;

        firstInsertCover = -1;
        allState.state_parser = 0;

        parser(0, -1);
    }

    @Override
    protected boolean isNeedAttribute(int atr) {
        switch (atr) {
            case AlFormatTag.TAG_ALIGN:
            case AlFormatTag.TAG_NUMFILES:
            case AlFormatTag.TAG_REALFILE:
            case AlFormatTag.TAG_NOTE:
            case AlFormatTag.TAG_CHARSET:
            case AlFormatTag.TAG_CONTENT:
            case AlFormatTag.TAG_FILEPOS:
            case AlFormatTag.TAG_RECINDEX:
            case AlFormatTag.TAG_FILE_AS:
            case AlFormatTag.TAG_ROLE:
            case AlFormatTag.TAG_VALUE:
            case AlFormatTag.TAG_REL:
                return true;
        }
        return super.isNeedAttribute(atr);
    }

    @Override
    protected void prepareCustom() {
		if (lnk != null && lnk.size() > 0) {
            for (int i = 0; i < lnk.size(); i++) {
                AlOneLink ap = lnk.get(i);

                if (ap.iType == 1)
                    continue;

                ap.positionE = ap.positionS;

                try {
                    ap.positionS = findParagraphPositionBySourcePos(0, par0.size(), ap.positionS);
                } catch (Exception e) {
                    ap.positionS = 0;
                    e.printStackTrace();
                    return;
                }

                for (int j = 0; j < footnotes.size(); j++) {
                    MOBIFootnote f = footnotes.get(j);
                    if (ap.positionS >= f.start && ap.positionS < f.stop) {
                        ap.positionS = f.start;
                        ap.positionE = f.stop;
                        ap.iType = 1;
                    } else
                    if (ap.positionS > f.stop) {
                        break;
                    }
                }
            }
        }

        int numPar;
        if (level1TOC != null) {
            for (int i = 0; i < level1TOC.size(); i++) {
                AlFilesMOBI.MOBITOC a = level1TOC.get(i);

                if (a.pos < 0 && a.fid >= 0)
                    a.pos = ((AlFilesMOBI)aFiles).getFIDPosition(a.fid, a.off);

                if (a.pos >= 0) {
                    a.real = findParagraphPositionBySourcePos(0, par0.size(), a.pos);//findParagraphBySourcePos1(0, par.size(), (*level1TOC)[i].pos);
                    addContent(AlOneContent.add(a.label, a.real, a.level));

                    numPar = findParagraphByPos(a.real);
                    par0.get(numPar).prop |= AlParProperty.SL2_BREAK_BEFORE;
                }
            }
        }

        super.prepareCustom();
    }

    /*@Override
    public void addTestContent(String s, int level) {
        if (s == null)
            return;
        s = s.trim();
        if (s.length() == 0)
            return;

        if ((paragraph & AlStyles.PAR_NOTE) == 0)
            addContent(AlOneContent.add(s, content_start, level));
    }*/

    @Override
    public  void setSpecialText(boolean flag) {
        if (flag) {
            allState.state_special_flag = (level1TOC == null);
            specialBuff.clear();
        } else {
            if (specialBuff.isContent) {
                addTestContent(specialBuff.buff.toString(), allState.section_count);
                specialBuff.isContent = false;
            } else
            if (specialBuff.isCSSStyle) {
                cssStyles.parseBuffer(specialBuff.buff, currentFile);
                specialBuff.isCSSStyle = false;
            }
            allState.state_special_flag = false;
        }
    }

    protected  boolean addNotes() {
        String s = null;

        StringBuilder param = tag.getATTRValue(AlFormatTag.TAG_HREF);

        int val;
        if (param != null && param.indexOf("kindle:pos:fid:") != -1) {
            val = param.indexOf("kindle:pos:fid:");
            String sFID = param.substring(val + 15, val + 15 + 4);
            String sOFF = param.substring(val + 24, val + 24 + 10);

            int fid, off;
            fid = (int) Base32Hex.decode2int(sFID, false);
            off = (int) Base32Hex.decode2int(sOFF, false);

            val = ((AlFilesMOBI)aFiles).getFIDPosition(fid, off);

            if (val >= 0) {
                sFID = ':' + sFID + sOFF;

                addCharFromTag((char)AlStyles.CHAR_LINK_S, false);
                addTextFromTag(sFID, false);
                addCharFromTag((char) AlStyles.CHAR_LINK_E, false);

                addtestLink(sFID, val, 0);
            }

            return true;
        }

        if (param != null) {
            addCharFromTag((char)AlStyles.CHAR_LINK_S, false);
            addTextFromTag(param.toString(), false);
            addCharFromTag((char) AlStyles.CHAR_LINK_E, false);
            return true;
        }

        param = tag.getATTRValue(AlFormatTag.TAG_FILEPOS);
        if (param != null) {

            try {
                val = Integer.parseInt(param.toString());
            } catch (Exception e) {
                val = -1;
            }

            if (val >= 0) {
                addCharFromTag((char)AlStyles.CHAR_LINK_S, false);
                addTextFromTag(param.toString(), false);
                addCharFromTag((char) AlStyles.CHAR_LINK_E, false);

                addtestLink(param.toString(), val, 0);

                return true;
            }
        }

        return false;
    }



    private String getFlowPart(String param) {
        String s = param.substring(12);

        int i = s.indexOf('?');
        if (i > 1)
            s = s.substring(0, i);

        try {
            int n = (int) Base32Hex.decode2int(s, false);
            if (n > 0) {
                s = ((AlFilesMOBI)aFiles).getFlowString(n);
                i = s.indexOf("=\"kindle:embed:");
                if (i != -1) {
                    s = s.substring(i + 2);
                    i = s.indexOf("\"");
                    if (i != -1)
                        s = s.substring(0, i);
                }
                return s;
                /*int flow_size = ((AlFilesMOBI)aFiles).getFlowPartSize(n);
                byte[] flow = ((AlFilesMOBI)aFiles).getFlowPart(n);
                if (flow != null && flow_size > 0) {
                    if (use_cpR0 == TAL_CODE_PAGES.CP1252) {
                        s = AlUnicode.ANSIbuffer2Ustring(flow, flow_size);
                    } else {
                        s = AlUnicode.UTFbuffer2Ustring(flow, flow_size);
                    }
                    flow_size = s.indexOf("=\"kindle:embed:");
                    if (flow_size != -1) {
                        s = s.substring(flow_size + 2);
                        flow_size = s.indexOf("\"");
                        if (flow_size != -1)
                            s = s.substring(0, flow_size);
                    }
                    return s;
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected boolean addImages() {
        StringBuilder param = tag.getATTRValue(AlFormatTag.TAG_SRC);

        if (param != null) {
            String s = param.toString();

            if (s.startsWith("kindle:flow:")) {
                s = getFlowPart(s);
                if (s == null || !s.startsWith("kindle:embed:"))
                    return false;
            }

            if (/*s != null && */s.startsWith("kindle:embed:")) {
                s = s.substring(13);

                int i = s.indexOf('?');
                if (i > 1)
                    s = s.substring(0, i);

                try {
                    int n = (int) Base32Hex.decode2int(s, false) - 1;
                    if (n >= 0) {
                        param.setLength(0);
                        param.append(Integer.toString(n));
                    } else {
                        param = null;
                    }
                } catch (Exception e) {
                    param = null;
                }
            }
        }

        if (param == null) {
            param = tag.getATTRValue(AlFormatTag.TAG_RECINDEX);
            if (param != null) {
                int n = InternalFunc.str2int(param, 10) - 1;
                if (n >= 0) {
                    param.setLength(0);
                    param.append(Integer.toString(n));
                } else {
                    param = null;
                }
            }
        }

        if (param != null) {
            boolean maybeCover = size == 0 && coverName == null;

            if (maybeCover)
                setParagraphStyle(AlStyles.SL_COVER);

            addCharFromTag((char)AlStyles.CHAR_IMAGE_S, false);
            addTextFromTag(param.toString(), false);
            addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);

            if (maybeCover)
                clearParagraphStyle(AlStyles.SL_COVER);

            return false;
        }

        return false;
    }

    @Override
    public boolean externPrepareTAG() {
        StringBuilder param;

        switch (tag.tag) {
            case AlFormatTag.TAG_SCRIPT:
                if (tag.closed) {
                    allState.decSkipped();
                    if (allState.skipped_flag > 0)
                        allState.state_parser = STATE_XML_SKIP;
                } else if (!tag.ended) {
                    allState.incSkipped();
                    allState.state_parser = STATE_XML_SKIP;
                } else {

                }
                return true;
            case AlFormatTag.TAG_LINK:
                if (tag.closed) {

                } else if (!tag.ended) {

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
            case AlFormatTag.TAG_STYLE:
                if (tag.closed) {
                    setSpecialText(false);
                } else if (!tag.ended) {
                    specialBuff.isCSSStyle = true;
                    setSpecialText(true);
                } else {

                }
                return true;
            case AlFormatTag.TAG_TITLE:
                if (tag.closed) {

                } else if (!tag.ended) {

                } else {

                }
                return true;
            case AlFormatTag.TAG_A:
                if (tag.closed) {
                    if ((styleStack.buffer[styleStack.position].paragraph & AlStyles.STYLE_LINK) !=0)
                    clearTextStyle(AlStyles.STYLE_LINK);
                } else
                if (!tag.ended) {

                    param = tag.getATTRValue(AlFormatTag.TAG_NAME);
                    if (param != null) {
                        addtestLink(param.toString());
                    }

                    if (addNotes())
                        setTextStyle(AlStyles.STYLE_LINK);
                } else {

                }
                return true;
            case AlFormatTag.TAG_HEAD:
                if (tag.closed) {
                    allState.decSkipped();
                    clearStateStyle(AlStateLevel2.PAR_DESCRIPTION1);
                    newParagraph();
                } else if (!tag.ended) {
                    newParagraph();
                    setStateStyle(AlStateLevel2.PAR_DESCRIPTION1);
                    allState.incSkipped();
                } else {

                }
                return true;
            case AlFormatTag.TAG_BODY:
                if (tag.closed) {
                    clearStateStyle(AlStateLevel2.PAR_NOTE);
                    newParagraph();
                    if (level1TOC == null)
                        setPropStyle(AlParProperty.SL2_BREAK_BEFORE);
                } else if (!tag.ended) {
                    allState.clearSkipped();
                    newParagraph();
                    cssStyles.enable = false;
                    cssStyles.fixWorkSet();
                } else {

                }
                return true;

            case AlFormatTag.TAG_H1:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);
                    newParagraph();
                    newEmptyTextParagraph();
                    setSpecialText(false);
                } else if (!tag.ended) {
                    newParagraph();
                    newEmptyTextParagraph();
                    if (level1TOC == null)
                        setPropStyle(AlParProperty.SL2_BREAK_BEFORE);
                    setParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);
                    allState.section_count = 0;
                    specialBuff.isContent = true;
                    allState.content_start = size;
                    setSpecialText(true);
                } else {

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
                    clearParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);
                    newParagraph();
                    newEmptyTextParagraph();
                    setSpecialText(false);
                } else
                if (!tag.ended) {
                    newParagraph();
                    newEmptyTextParagraph();
                    setParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);// | AlStyles.PAR_BREAKPAGE);
                    allState.section_count = 1;
                    specialBuff.isContent = true;
                    allState.content_start = size;
                    setSpecialText(true);
                } else {
                    newParagraph();
                    newEmptyTextParagraph();
                }
                return true;
            case AlFormatTag.TAG_ASIDE:
                if (tag.closed) {
                    if (footstart != -1) {
                        MOBIFootnote f = new MOBIFootnote();
                        f.start = footstart;
                        f.stop = size;
                        footnotes.add(f);
                    }
                    footstart = -1;

                    if (preference.onlyPopupFootnote)
                        if ((styleStack.buffer[styleStack.position].paragraph & AlStyles.STYLE_HIDDEN) != 0)
                    clearTextStyle(AlStyles.STYLE_HIDDEN);
                } else
                if (!tag.ended) {

                    StringBuilder tp = tag.getATTRValue(AlFormatTag.TAG_TYPE);

                    if (tp != null && tp.toString().startsWith(AlFormatEPUB.EPUB_FOOTNOTEMARK)) {
                        footstart = size;
                        if (preference.onlyPopupFootnote)
                            setTextStyle(AlStyles.STYLE_HIDDEN);
                    }
                }
                return true;

            case AlFormatTag.TAG_HTML:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                    cssStyles.disableWorkSet();
                    cssStyles.enable = true;

                    currentFile = AL_ROOT_RIGHTPATH_STR + '\u0002' + Integer.toString(allState.start_position_par);

                    if (coverName != null) {

                        if (firstInsertCover == -1) {
                            firstInsertCover = allState.start_position;
                            allState.clearSkipped();
                            newParagraph();
                            setParagraphStyle(AlStyles.SL_COVER);
                            addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
                            addCharFromTag(LEVEL2_COVERTOTEXT, false);
                            addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);
                            newParagraph();
                            allState.restoreSkipped();
                            setStateStyle(AlStateLevel2.PAR_DESCRIPTION1);
                            clearParagraphStyle(AlStyles.SL_COVER);
                        }
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_IMG:
                if (tag.closed) {

                } else {
                    addImages();
                }
                return true;
            case AlFormatTag.TAG_PAGEBREAK:
                if (tag.closed) {

                } else if (!tag.ended) {
                    newParagraph();
                    setPropStyle(AlParProperty.SL2_BREAK_BEFORE);
                }
                return true;
        }

        return super.externPrepareTAG ();
    }

}
