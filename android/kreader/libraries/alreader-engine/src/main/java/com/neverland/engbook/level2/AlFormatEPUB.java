package com.neverland.engbook.level2;

import android.util.Log;

import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlOneContent;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlFilesEPUB;
import com.neverland.engbook.level1.AlOneZIPRecord;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;
import com.neverland.engbook.util.InternalFunc;

import java.util.ArrayList;
import java.util.HashMap;

public class AlFormatEPUB extends AlAXML {

    public static boolean isEPUB(AlFiles a) {
        //noinspection RedundantIfStatement
        if (a.getIdentStr().contentEquals("epub"))
            return true;
        return false;
    }

    private static final String CONTAINER_MEDIATYPE = "application/oebps-package+xml";
    private static final String META_NAME_COVER = "cover";
    private static final String META_NAME_IMAGE = "image/";
    private static final String META_NAME_XMLHTML = "application/xhtml+xml";
    private static final String META_NAME_TOC = "application/x-dtbncx+xml";
    protected static final String EPUB_FOOTNOTEMARK = "footnote";


    private String		    currentFile = null;
    private int			    currentNum = UNKNOWN_FILE_SOURCE_NUM;

    private boolean			isGenre = false;
    private boolean			isAuthor = false;
    private boolean			isBookTitle = false;
    private boolean			isTOC = false;

    private int			    toc_state = 0;
    private String		    toc_point = null;
    private int 			toc_section = -1;
    private final StringBuilder	toc_text = new StringBuilder();
    private String		    toc_base;
    private final ArrayList<AlOneEPUBTOC>		listTOC = new ArrayList<>();
    private static final int		TOC_NAVMAP = 0x01;
    private static final int		TOC_NAVPOINT = 0x02;
    private static final int		TOC_NAVLABEL = 0x04;
    private static final int		TOC_TEXT = 0x08;

    private final HashMap<String, AlOneEPUBIDItem>	mapId = new HashMap<>();
    private final ArrayList<AlOneEPUBItem>			listFiles = new ArrayList<>();

    class AlOneEPUBIDItem {
        String	    href = null;
        String	    type = null;
    }

    class AlOneEPUBItem {
        String	    file = null;
        String	    type = null;
        boolean		note = false;
    }

    class AlOneEPUBTOC {
        String	    href = null;
        String	    name = null;
        int		    section = 0;
    }

    private boolean needUnpackAfterAllRead = false;


    @Override
    public void initState(AlBookOptions bookOptions, AlFiles myParent, AlPreferenceOptions pref, AlStylesOptions stl) {
        allState.isOpened = true;

        xml_mode = true;
        ident = "EPUB";

        aFiles = myParent;

        if ((bookOptions.formatOptions & AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG) != 0)
            needUnpackAfterAllRead = true;

        preference = pref;
        styles = stl;

        noUseCover = bookOptions.noUseCover;
        size = 0;

        autoCodePage = false;
        setCP(TAL_CODE_PAGES.CP65001);

        allState.state_parser = STATE_XML_SKIP;
        allState.state_skipped_flag = true;

        parser(0, -1);
        newParagraph();

        allState.isOpened = false;
    }

    @Override
    void doSpecialGetParagraph(long iType, int addon, long level, long[] stk, int[] cpl) {
        paragraph = iType;
        allState.state_parser = 0;
        //int cp = ((addon & 0x80000000) != 0) ? -1 : addon & 0x0000ffff;

        int num = addon;//(addon >> 16) & UNKNOWN_FILE_SOURCE_NUM;
        if (num != currentNum) {
            if (num == UNKNOWN_FILE_SOURCE_NUM) {
                currentFile = null;
            } else {
                String s = aFiles.getExternalAbsoluteFileName(num);
                if (s != null) {
                    currentFile = s;
                } else {
                    currentFile = null;
                    num = UNKNOWN_FILE_SOURCE_NUM;
                }
            }
            currentNum = num;
        }

        //if (cp != use_cpR0)
        //    setCP(cp);

        paragraph_level = (int) (level & LEVEL2_MASK_FOR_LEVEL);
        paragraph_tag = (int) ((level >> 31) & 0xffffffff);

        /*allState.state_skipped_flag = (addon & LEVEL2_FRM_ADDON_SKIPPEDTEXT) != 0;
        allState.state_code_flag = (addon & LEVEL2_FRM_ADDON_CODETEXT) != 0;*/

        allState.state_skipped_flag = (level & LEVEL2_FRM_ADDON_SKIPPEDTEXT) != 0;
        allState.state_code_flag = (level & LEVEL2_FRM_ADDON_CODETEXT) != 0;

        //allState.state_special_flag0 = (addon & LEVEL2_FRM_ADDON_SPECIALTEXT) != 0;
    }

    @Override
    public void formatAddonInt() {
        pariType = paragraph;
        /*parAddon = use_cpR0 & 0x8000ffff;
        parAddon |= currentNum << 16;*/

        parAddon = currentNum;

        parLevel = paragraph_level | (((long)paragraph_tag) << 31);

        /*if (allState.state_skipped_flag)
            parAddon += LEVEL2_FRM_ADDON_SKIPPEDTEXT;
        if (allState.state_code_flag)
            parAddon += LEVEL2_FRM_ADDON_CODETEXT;*/

        if (allState.state_skipped_flag)
            parLevel |= LEVEL2_FRM_ADDON_SKIPPEDTEXT;
        if (allState.state_code_flag)
            parLevel |= LEVEL2_FRM_ADDON_CODETEXT;

        /*if (allState.state_special_flag0)
        parAddon += LEVEL2_FRM_ADDON_SPECIALTEXT;*/
    }

    @Override
    public boolean isNeedAttribute(int atr) {
        switch (atr) {
            case AlFormatTag.TAG_NUMBER:
            case AlFormatTag.TAG_CONTENT_TYPE:
            case AlFormatTag.TAG_TITLE:
            case AlFormatTag.TAG_NUMFILES:
            case AlFormatTag.TAG_NOTE:
            case AlFormatTag.TAG_TOC:
            case AlFormatTag.TAG_CONTENT:
            case AlFormatTag.TAG_FULL_PATH:
            case AlFormatTag.TAG_MEDIA_TYPE:
                return true;
        }
        return super.isNeedAttribute(atr);
    }

    @Override
    protected void prepareCustom() {
        if (listTOC.size() > 0) {

            int	pos, parNum, startFind = 0;

            for (int i = 0; i < listTOC.size(); i++) {
                AlOneEPUBTOC a = listTOC.get(i);

                StringBuilder link = new StringBuilder();
                String href = a.href;
                pos = href.indexOf('#');
                if (pos != -1)
                    href = href.substring(0, pos);
                link.append(AlFiles.getAbsoluteName(toc_base, href));
                if (pos != -1)
                    link.append(a.href.substring(pos));

                for (int j = startFind; j < lnk.size(); j++) {
                    if (lnk.get(j).name.contentEquals(link)) {
                        pos = lnk.get(j).positionS;
                        startFind = ++j;

                        parNum = findParagraphByPos(pos);
                        if (par.get(parNum).start == pos)
                            par.get(parNum).iType |= AlStyles.PAR_BREAKPAGE;

                        break;
                    }
                }

                addContent(AlOneContent.add(a.name, pos, a.section));
            }
        }

        coverIsFirstImage = false;
        if (coverMETAIID != null) {
            coverName = coverMETAIID;
        } else
        if (coverITEM_hrefImage != null) {
            coverName = coverITEM_hrefImage;
        } else

        /*if (coverITEM_idImage != null) {
            coverName = coverITEM_idImage;
        } else
        if (coverITEM_idXML != null) {
            coverName = coverITEM_idXML;
        } else
        if (coverITEM_hrefXML != null) {
            coverName = coverITEM_hrefXML;
        } else*/

        if (coverFIRST != null) {
            coverIsFirstImage = true;
            coverName = coverFIRST;
        }

        super.prepareCustom();
    }

    private boolean coverIsFirstImage = false;
    private String	coverMETAIID = null;
    private String	coverITEM_hrefImage = null;
    private String	coverITEM_idImage = null;
    private String	coverITEM_idXML = null;
    private String	coverITEM_hrefXML = null;
    private String	coverFIRST = null;

    private void setSpecialText(boolean flag) {
        if (flag) {
            allState.state_special_flag0 = true;
            state_specialBuff0.setLength(0);
            state_specialBuff0.ensureCapacity(256);
        } else {
            if (isTOC) {
                toc_text.setLength(0);
                toc_text.append(state_specialBuff0.toString().trim());
                isTOC = false;
            } else
            if (isAuthor) {
                if (allState.isOpened)
                    bookAuthors.add(state_specialBuff0.toString().trim());
                isAuthor = false;
            }
            else
            if (isGenre) {
                if (allState.isOpened)
                    bookGenres.add(state_specialBuff0.toString().trim());
                isGenre = false;
            }
            else
            if (isBookTitle) {
                if (allState.isOpened)
                    bookTitle = state_specialBuff0.toString().trim();
                isBookTitle = false;
            }

            allState.state_special_flag0 = false;
        }
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

            addCharFromTag((char)AlStyles.CHAR_LINK_S, false);
            addTextFromTag(s, false);
            addCharFromTag((char)AlStyles.CHAR_LINK_E, false);
            return true;
        }

        if (allState.isOpened) {
            param = tag.getATTRValue(AlFormatTag.TAG_NAME);
            StringBuilder tp = tag.getATTRValue(AlFormatTag.TAG_TYPE);
            if (param != null) {
                addtestLink(param.toString(), (tp != null) && (tp.toString().startsWith(EPUB_FOOTNOTEMARK)));
            }
        }
        return false;
    }

    void addtestLink(String s, boolean isFootnote) {
        if (allState.isOpened) {
            StringBuilder link = new StringBuilder();// = AlFiles::getAbsoluteName(currentFile, s);
            if (currentFile != null && currentFile.length() > 0) {
                link.append(currentFile);
                link.append('#');
            }
            link.append(s);
            if (!isFootnote) {
                super.addtestLink(link.toString());
            } else {
                super.addtestLink(link.toString(), 1);
            }
        }
    }

    private int			realSkipCoverPos = -1;

    private boolean addImages() {
        StringBuilder param = tag.getATTRValue(AlFormatTag.TAG_SRC);
        if (param == null)
            param = tag.getATTRValue(AlFormatTag.TAG_HREF);

        if (param != null) {
            String name = AlFiles.getAbsoluteName(currentFile, param.toString());

            int num_file = aFiles.getExternalFileNum(name);
            if (num_file != AlFiles.LEVEL1_FILE_NOT_FOUND)
                name = aFiles.getExternalAbsoluteFileName(num_file);


            boolean needInsert = true;

            boolean hasCover = false;
            if (allState.isOpened) {
                if (realSkipCoverPos == -1 && size == 0 && noUseCover) {
                    realSkipCoverPos = tag.start_pos;
                    needInsert = false;
                }

                hasCover = coverMETAIID != null || coverITEM_hrefImage != null
                //|| coverITEM_idImage != null || coverITEM_idXML != null || coverITEM_hrefXML != null
                ;
            } else {
                needInsert = realSkipCoverPos != tag.start_pos;
                hasCover = coverName.length() > 0;
            }

            if (needInsert) {
                if (!hasCover && coverFIRST == null)
                    setParagraphStyle(AlStyles.PAR_COVER);

                addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
                addTextFromTag(name, false);
                addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);

                if (!hasCover && coverFIRST == null)
                    clearParagraphStyle(AlStyles.PAR_COVER);
            }
            if (coverFIRST  == null) {
                coverFIRST = name;
            }
        }

        return false;
    }

    private void addFiles2Read(String fname, int sp) {
        if (allState.isOpened) {
            String addFiles = AlFiles.getAbsoluteName(currentFile, fname);
            ((AlFilesEPUB)aFiles).addFilesToRecord(addFiles, sp);
            if (dinamicSize)
                stop_posUsed = aFiles.getSize();
        }
    }

    @Override
    protected boolean externPrepareTAG() {
        StringBuilder param;


        if (allState.isOpened &&
                (paragraph & (AlStyles.PAR_DESCRIPTION1 | AlStyles.PAR_DESCRIPTION2 | AlStyles.PAR_DESCRIPTION3 | AlStyles.PAR_DESCRIPTION4)) == 0) {
            param = tag.getATTRValue(AlFormatTag.TAG_ID);
            StringBuilder tp = tag.getATTRValue(AlFormatTag.TAG_TYPE);
            if (param != null)
                addtestLink(param.toString(), (tp != null) && (tp.toString().startsWith(EPUB_FOOTNOTEMARK)));
        }


        switch (tag.tag) {
// styles
            case AlFormatTag.TAG_B:
            case AlFormatTag.TAG_STRONG:
                if (tag.closed) {
                    clearTextStyle(AlStyles.STYLE_BOLD);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_BOLD);
                } else {

                }
                return true;
            case AlFormatTag.TAG_SUP:
                if (tag.closed) {
                    clearTextStyle(AlStyles.STYLE_SUP);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_SUP);
                } else {

                }
                return true;
            case AlFormatTag.TAG_SUB:
                if (tag.closed) {
                    clearTextStyle(AlStyles.STYLE_SUB);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_SUB);
                } else {

                }
                return true;
            case AlFormatTag.TAG_I:
            case AlFormatTag.TAG_EM:
            case AlFormatTag.TAG_DFM:
                if (tag.closed) {
                    clearTextStyle(AlStyles.STYLE_ITALIC);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_ITALIC);
                } else {

                }
                return true;
            case AlFormatTag.TAG_UL:
            case AlFormatTag.TAG_OL:
                //case AlFormatTag.TAG_LI:
                if (tag.closed) {
                    decULNumber();
                } else
                if (!tag.ended) {
                    incULNumber();
                } else {

                }
                return true;
            case AlFormatTag.TAG_LI:
                newParagraph();
                return true;
            case AlFormatTag.TAG_U:
            case AlFormatTag.TAG_S:
            case AlFormatTag.TAG_INS:
            case AlFormatTag.TAG_UNDERLINE:
                if (tag.closed) {
                    clearTextStyle(AlStyles.STYLE_UNDER);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_UNDER);
                } else {

                }
                return true;
			case AlFormatTag.TAG_CODE:
                if (tag.closed) {
                    clearTextStyle(AlStyles.STYLE_CODE);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_CODE);
                } else {

                }
                return true;
            case AlFormatTag.TAG_STRIKE:
            case AlFormatTag.TAG_DEL:
            case AlFormatTag.TAG_STRIKETHROUGH:
                if (tag.closed) {
                    clearTextStyle(AlStyles.STYLE_STRIKE);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_STRIKE);
                } else {

                }
                return true;
            case AlFormatTag.TAG_A:
                if (tag.closed) {
                    if ((paragraph & AlStyles.STYLE_LINK) != 0)
                        clearTextStyle(AlStyles.STYLE_LINK);
                } else
                if (!tag.ended) {
                    if (addNotes())
                        setTextStyle(AlStyles.STYLE_LINK);
                } else {

                }
                return true;
            case AlFormatTag.TAG_ASIDE:
                if (tag.closed) {
                    closeOpenNotes();
                    if (preference.onlyPopupFootnote)
                        if ((paragraph & AlStyles.STYLE_HIDDEN) != 0)
                            clearTextStyle(AlStyles.STYLE_HIDDEN);
                } else
                if (!tag.ended) {
                    StringBuilder tp = tag.getATTRValue(AlFormatTag.TAG_TYPE);
                    if (preference.onlyPopupFootnote && tp != null && tp.toString().startsWith(EPUB_FOOTNOTEMARK))
                        setParagraphStyle(AlStyles.STYLE_HIDDEN);
                }
                newParagraph();
                return true;
// paragraph
            case AlFormatTag.TAG_DIV:
            case AlFormatTag.TAG_DT:
            case AlFormatTag.TAG_DD:
            case AlFormatTag.TAG_P:
                if (tag.closed) {
                    newParagraph();
                } else
                if (!tag.ended) {
                    newParagraph();
                } else {
                    newParagraph();
                    newEmptyTextParagraph();
                }
                return true;
            case AlFormatTag.TAG_HR:
            case AlFormatTag.TAG_BR:
                if (tag.closed) {

                } else
                if (!tag.ended) {
                    newParagraph();
                } else {
                    newParagraph();
                }
                return true;
            case AlFormatTag.TAG_PRE:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_PRE);
                    newParagraph();
                    allState.state_code_flag = false;
                } else
                if (!tag.ended) {
                    newParagraph();
                    setParagraphStyle(AlStyles.PAR_PRE);
                    allState.state_code_flag = true;
                } else {

                }
                return true;
            case AlFormatTag.TAG_TT:
                if (tag.closed) {
                    clearTextStyle(AlStyles.STYLE_CODE);
                } else
                if (!tag.ended) {
                    setTextStyle(AlStyles.STYLE_CODE);
                } else {

                }
                return true;
            case AlFormatTag.TAG_BLOCKQUOTE:
            case AlFormatTag.TAG_CITE:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_CITE);
                    newParagraph();
                    newEmptyTextParagraph();
                } else
                if (!tag.ended) {
                    newParagraph();
                    newEmptyTextParagraph();
                    setParagraphStyle(AlStyles.PAR_CITE);
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
                    clearParagraphStyle(AlStyles.PAR_SUBTITLE);
                    newParagraph();
                    newEmptyStyleParagraph();
                } else
                if (!tag.ended) {
                    newParagraph();
                    newEmptyStyleParagraph();
                    setParagraphStyle(AlStyles.PAR_SUBTITLE);
                } else {
                    newParagraph();
                    newEmptyStyleParagraph();
                }
                return true;
            case AlFormatTag.TAG_H1:
                if (tag.closed) {
                    clearParagraphStyle(AlStyles.PAR_TITLE);
                    newParagraph();
                    newEmptyStyleParagraph();
                } else
                if (!tag.ended) {
                    newParagraph();
                    newEmptyStyleParagraph();
                    setParagraphStyle(AlStyles.PAR_TITLE);
                    isFirstParagraph = true;
                } else {

                }
                return true;
            case AlFormatTag.TAG_EMPTY_LINE:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {
                    newParagraph();
                    newEmptyTextParagraph();
                }
                return true;
// addon
            case AlFormatTag.TAG_IMAGE:
            case AlFormatTag.TAG_IMG:
                if (tag.closed) {

                } else
                if (!tag.ended) {
                    addImages();
                } else {
                    addImages();
                }
                return true;
            case AlFormatTag.TAG_BODY:
                if (tag.closed) {
                    allState.state_skipped_flag = true;
                } else
                if (!tag.ended) {
                    allState.state_skipped_flag = false;
                } else {

                }
                return true;
// manage
            case AlFormatTag.TAG_META:
                if (tag.closed) {

                } else {
                    if (allState.isOpened && (paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
                        if (allState.isOpened) {
                            param = tag.getATTRValue(AlFormatTag.TAG_NAME);
                            if (param != null && (META_NAME_COVER.contentEquals(param))) {
                                param = tag.getATTRValue(AlFormatTag.TAG_CONTENT);
                                if (param != null)
                                    coverMETAIID = param.toString();
                            }
                        }
                    }
                }
                return true;

            case AlFormatTag.TAG_ROOTFILE:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {
                    if (allState.isOpened && (paragraph & AlStyles.PAR_DESCRIPTION1) != 0) {
                        param = tag.getATTRValue(AlFormatTag.TAG_MEDIA_TYPE);
                        if (param != null && CONTAINER_MEDIATYPE.contentEquals(param)) {
                            param = tag.getATTRValue(AlFormatTag.TAG_FULL_PATH);
                            if (param != null) {
                                String val = AlFiles.getAbsoluteName(EngBookMyType.AL_ROOT_RIGHTPATH_STR, param.toString());
                                addFiles2Read(val, AlOneZIPRecord.SPECIAL_CONTENT);
                            }
                        }
                    }
                }
                return true;
            case AlFormatTag.TAG_ITEMREF:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {
                    if (allState.isOpened && (paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
                        param = tag.getATTRValue(AlFormatTag.TAG_IDREF);
                        if (param != null) {
                            AlOneEPUBIDItem it = mapId.get(param.toString());
                            if (it != null) {
                                AlOneEPUBItem a = new AlOneEPUBItem();
                                a.file = it.href;
                                a.type = it.type;
                                listFiles.add(a);
                            }
                        }
                    }
                }
                return true;
            case AlFormatTag.TAG_ITEM:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {
                    if (allState.isOpened && (paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
                        param = tag.getATTRValue(AlFormatTag.TAG_ID);
                        if (param != null) {
                            StringBuilder href = tag.getATTRValue(AlFormatTag.TAG_HREF);
                            StringBuilder type = tag.getATTRValue(AlFormatTag.TAG_MEDIA_TYPE);
                            if (href != null && type != null) {
                                String file = AlFiles.getAbsoluteName(currentFile, href.toString());
                                if (aFiles.getExternalFileNum(file) != AlFiles.LEVEL1_FILE_NOT_FOUND) {
                                    AlOneEPUBIDItem a = new AlOneEPUBIDItem();
                                    a.href = file;
                                    a.type = type.toString();
                                    mapId.put(param.toString(), a);

                                    if (param.indexOf(META_NAME_COVER) == 0) {
                                        if (a.type.startsWith("image/")) {
                                            coverITEM_idImage = file;
                                        } else
                                        if (a.type.contains(META_NAME_XMLHTML)) {
                                            coverITEM_idXML = file;
                                        }
                                    }

                                    if (href.indexOf(META_NAME_COVER) != -1) {
                                        if (a.type.startsWith("image/")) {
                                            coverITEM_hrefImage = file;
                                        } else
                                        if (a.type.startsWith(META_NAME_XMLHTML)) {
                                            coverITEM_hrefXML = file;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            case AlFormatTag.TAG_REFERENCE:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {
                    if (allState.isOpened && (paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
                       StringBuilder href = tag.getATTRValue(AlFormatTag.TAG_HREF);
                        param = tag.getATTRValue(AlFormatTag.TAG_TYPE);
                        if (param != null && href != null) {
                            String file;

                            /*if (META_NAME_COVER.contentEquals(param)) {
                                file = AlFiles.getAbsoluteName(currentFile, *href);
                                if (aFiles.getExternalFileNum(file) != AlFiles.LEVEL1_FILE_NOT_FOUND)
                                    coverREFERENCE_XML = file;
                            }*/

                            if ((param).indexOf("note") == 0) {
                                file = AlFiles.getAbsoluteName(currentFile, href.toString());
                                if (aFiles.getExternalFileNum(file) != AlFiles.LEVEL1_FILE_NOT_FOUND) {
                                    for (int i = 0; i < listFiles.size(); i++) {
                                        if (listFiles.get(i).file.contentEquals(file)) {
                                            listFiles.get(i).note = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            case AlFormatTag.TAG_CREATOR:
            case AlFormatTag.TAG_TITLE:
                if (tag.closed) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
				/*clearParagraphStyle(AlStyles::PAR_TITLE);
				newParagraph();
				newEmptyStyleParagraph();*/
                        if (allState.isOpened)
                            setSpecialText(false);
				/*allState.state_skipped_flag = true;*/
                    }
                } else
                if (!tag.ended) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
				/*allState.state_skipped_flag = false;
				newParagraph();
				newEmptyStyleParagraph();
				setParagraphStyle(AlStyles::PAR_TITLE);*/
                        if (allState.isOpened) {
                            isBookTitle = tag.tag == AlFormatTag.TAG_TITLE;
                            isAuthor = tag.tag == AlFormatTag.TAG_CREATOR;
                            setSpecialText(true);
                        }
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_GUIDE:
                if (tag.closed) {

                } else {

                }
                return true;
            case AlFormatTag.TAG_SPINE:
                if (tag.closed) {

                } else
                if (!tag.ended) {
                    if ((paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
                        param = tag.getATTRValue(AlFormatTag.TAG_TOC);
                        if (param != null) {
                            AlOneEPUBIDItem it = mapId.get(param.toString());
                            if (it != null) {
                                addFiles2Read(it.href, AlOneZIPRecord.SPECIAL_TOC);
                            }
                        }
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_DESCRIPTION:
		/*if (tag.closed) {
			if ((paragraph & AlStyles::PAR_DESCRIPTION2) != 0) {
				clearParagraphStyle(AlStyles::PAR_ANNOTATION);
				newParagraph();
				newEmptyStyleParagraph();
				allState.state_skipped_flag = true;
			}
		} else
		if (!tag.ended) {
			if ((paragraph & AlStyles::PAR_DESCRIPTION2) != 0) {
				allState.state_skipped_flag = false;
				newParagraph();
				newEmptyStyleParagraph();
				setParagraphStyle(AlStyles::PAR_ANNOTATION);
			}
		} else {

		}*/
                return true;
            case AlFormatTag.TAG_DATE:
		/*if (tag.closed) {
			if ((paragraph & AlStyles::PAR_DESCRIPTION2) != 0) {
				clearParagraphStyle(AlStyles::PAR_AUTHOR);
				newParagraph();
				newEmptyStyleParagraph();
				allState.state_skipped_flag = true;
			}
		} else
		if (!tag.ended) {
			if ((paragraph & AlStyles::PAR_DESCRIPTION2) != 0) {
				allState.state_skipped_flag = false;
				newParagraph();
				newEmptyStyleParagraph();
				setParagraphStyle(AlStyles::PAR_AUTHOR);
			}
		} else {

		}*/
                return true;
            case AlFormatTag.TAG_CONTENT:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {
                    if (allState.isOpened && (paragraph & AlStyles.PAR_DESCRIPTION3) != 0) {
                        if (toc_state == TOC_NAVMAP + TOC_NAVPOINT) {
                            toc_point = null;
                            param = tag.getATTRValue(AlFormatTag.TAG_SRC);
                            if (param != null)
                                toc_point = param.toString();

                            /*if (toc_point.length() > 0 && toc_text.length() > 0) {
                                AlOneEPUBTOC a = new AlOneEPUBTOC();
                                a.href = toc_point;
                                a.name = toc_text.toString();
                                a.section = toc_section;
                                toc_point = null;
                                toc_text.setLength(0);
                                listTOC.add(a);
                            }*/
                        }
                    }
                }
                return true;
            case AlFormatTag.TAG_NAVMAP:
                if (tag.closed) {
                    if (allState.isOpened && (paragraph & AlStyles.PAR_DESCRIPTION3) != 0) {
                        toc_state &= ~TOC_NAVMAP;
                    }
                } else
                if (!tag.ended) {
                    if (allState.isOpened && (paragraph & AlStyles.PAR_DESCRIPTION3) != 0) {
                        toc_state |= TOC_NAVMAP;
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_NAVPOINT:
                if (tag.closed) {
                    if (allState.isOpened && (paragraph & AlStyles.PAR_DESCRIPTION3) != 0) {
                        if (toc_state == TOC_NAVMAP + TOC_NAVPOINT) {

                            addTOCPoint();

                            if (toc_section > 0) {
                                toc_section--;
                            } else
                            if (toc_section == 0) {
                                toc_section--;
                                toc_state &= ~TOC_NAVPOINT;
                            }
                        }
                    }
                } else
                if (!tag.ended) {
                    if (allState.isOpened && (paragraph & AlStyles.PAR_DESCRIPTION3) != 0) {
                        if ((toc_state == TOC_NAVMAP) || (toc_state == TOC_NAVMAP + TOC_NAVPOINT)) {

                            if (toc_section == -1) {
                                toc_state |= TOC_NAVPOINT;
                            } else {
                                /*if (toc_point != null && toc_point.length() > 0 && toc_text.length() > 0) {
                                    AlOneEPUBTOC a = new AlOneEPUBTOC();
                                    a.href = toc_point;
                                    a.name = toc_text.toString();
                                    a.section = toc_section;
                                    toc_point = null;
                                    toc_text.setLength(0);
                                    listTOC.add(a);
                                }*/

                                addTOCPoint();
                            }
                            toc_section++;
                        }
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_NAVLABEL:
                if (tag.closed) {
                    if (allState.isOpened && (paragraph & AlStyles.PAR_DESCRIPTION3) != 0) {
                        if (toc_state == TOC_NAVMAP + TOC_NAVPOINT + TOC_NAVLABEL) {
                            toc_state &= ~TOC_NAVLABEL;
                        }
                    }
                } else
                if (!tag.ended) {
                    if (allState.isOpened && (paragraph & AlStyles.PAR_DESCRIPTION3) != 0) {
                        if (toc_state == TOC_NAVMAP + TOC_NAVPOINT) {
                            toc_state |= TOC_NAVLABEL;
                        }
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_TEXT:
                if (tag.closed) {
                    if (allState.isOpened && (paragraph & AlStyles.PAR_DESCRIPTION3) != 0) {
                        if (toc_state == TOC_NAVMAP + TOC_NAVPOINT + TOC_NAVLABEL + TOC_TEXT) {
                            toc_state &= ~TOC_TEXT;
                            setSpecialText(false);
                        }
                    }
                } else
                if (!tag.ended) {
                    if (allState.isOpened && (paragraph & AlStyles.PAR_DESCRIPTION3) != 0) {
                        if (toc_state == TOC_NAVMAP + TOC_NAVPOINT + TOC_NAVLABEL) {
                            toc_state |= TOC_TEXT;
                            isTOC = true;
                            setSpecialText(true);
                        }
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_EXTFILE:
                if (tag.closed) {
                    closeOpenNotes();

                    if (allState.isOpened && (paragraph & AlStyles.PAR_DESCRIPTION2) != 0) {
                        for (int i = 0; i < listFiles.size(); i++) {
                            addFiles2Read(listFiles.get(i).file,
                                    listFiles.get(i).note ? AlOneZIPRecord.SPECIAL_NOTE : AlOneZIPRecord.SPECIAL_NONE);
                        }

                        if (coverMETAIID != null) {
                            AlOneEPUBIDItem it = mapId.get(coverMETAIID);
                            if (it != null)
                                coverMETAIID = it.href;
                        }

                        if (needUnpackAfterAllRead)
                            aFiles.needUnpackData();
                        //acoverITEM_hrefXML = acoverITEM_idXML = acoverREFERENCE_XML = false;
                    }

                    if (allState.isOpened)
                        newParagraph();

                    if ((paragraph & (AlStyles.PAR_NOTE | AlStyles.PAR_DESCRIPTION1 | AlStyles.PAR_DESCRIPTION2 |
                            AlStyles.PAR_DESCRIPTION3 | AlStyles.PAR_DESCRIPTION4)) != 0)
                        setParagraphStyle(AlStyles.PAR_BREAKPAGE);

                    clearParagraphStyle(AlStyles.PAR_DESCRIPTION1 | AlStyles.PAR_DESCRIPTION2 |
                            AlStyles.PAR_DESCRIPTION3 | AlStyles.PAR_DESCRIPTION4 | AlStyles.PAR_NOTE);

                    currentNum = UNKNOWN_FILE_SOURCE_NUM;
                    currentFile = null;
                    allState.state_skipped_flag = false;
                } else
                if (!tag.ended) {
                    allState.state_skipped_flag = true;

                    param = tag.getATTRValue(AlFormatTag.TAG_NUMFILES);
                    if (param != null) {
                        currentNum = InternalFunc.str2int(param, 10);
                    }

                    param = tag.getATTRValue(AlFormatTag.TAG_ID);
                    if (param != null) {
                        currentFile = param.toString();
                    }

                    param = tag.getATTRValue(AlFormatTag.TAG_IDREF);
                    if (param != null) {
                        switch (InternalFunc.str2int(param, 10)) {
                        case AlOneZIPRecord.SPECIAL_FIRST:
                            setParagraphStyle(AlStyles.PAR_DESCRIPTION1);
                            break;
                        case AlOneZIPRecord.SPECIAL_CONTENT:
                            setParagraphStyle(AlStyles.PAR_DESCRIPTION2);

                            allState.state_skipped_flag = false;
                            if (allState.isOpened || coverName != null) {
                                setParagraphStyle(AlStyles.PAR_COVER);
                                if (noUseCover || coverIsFirstImage) {
                                    addCharFromTag((char)0xa0, false);
                                    addCharFromTag((char)0xa0, false);
                                    addCharFromTag((char)0xa0, false);
                                } else {
                                    addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
                                    addCharFromTag(LEVEL2_COVERTOTEXT, false);
                                    addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);
                                }
                                clearParagraphStyle(AlStyles.PAR_COVER);
                            } else {
                                addCharFromTag((char)0xa0, false);
                                addCharFromTag((char)0xa0, false);
                                addCharFromTag((char)0xa0, false);
                            }
                            allState.state_skipped_flag = true;
                            break;
                        case AlOneZIPRecord.SPECIAL_TOC:
                            toc_base = currentFile;
                            setParagraphStyle(AlStyles.PAR_DESCRIPTION3);
                            break;
                        case AlOneZIPRecord.SPECIAL_NOTE:
                            setParagraphStyle(AlStyles.PAR_NOTE | AlStyles.PAR_BREAKPAGE);
                            break;
                        }
                    }

                } else {

                }
                return true;
            case AlFormatTag.TAG_TABLE:
            case AlFormatTag.TAG_TH:
            case AlFormatTag.TAG_TD:
            case AlFormatTag.TAG_TR:
                return prepareTable();
        }

        return false;

    }

    private void addTOCPoint() {
        if (toc_point != null && toc_point.length() > 0 && toc_text.length() > 0) {
            AlOneEPUBTOC a = new AlOneEPUBTOC();
            a.href = toc_point;
            a.name = toc_text.toString();
            a.section = toc_section;
            toc_point = null;
            toc_text.setLength(0);
            listTOC.add(a);
        }
    }
}
