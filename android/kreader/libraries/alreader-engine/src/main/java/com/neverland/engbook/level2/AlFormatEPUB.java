package com.neverland.engbook.level2;

import com.neverland.engbook.allstyles.AlCSSHtml;
import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlOneContent;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlFilesEPUB;
import com.neverland.engbook.level1.AlOneZIPRecord;
import com.neverland.engbook.util.AlMultiFiles;
import com.neverland.engbook.util.AlOneMultiFile;
import com.neverland.engbook.util.AlParProperty;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;
import com.neverland.engbook.util.InternalFunc;

import java.util.ArrayList;
import java.util.HashMap;

public class AlFormatEPUB extends AlFormatBaseHTML {

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

    private int			realSkipCoverPos = -1;


    private String	imageFIRST = null;
    private String	coverITEM_hrefImage = null;
    private String	coverITEM_idImage = null;
    private String	coverITEM_hrefXML = null;
    private boolean			acoverITEM_hrefXML;
    private String	coverITEM_idXML = null;
    private boolean			acoverITEM_idXML;
    private String	coverREFERENCE_XML = null;
    private boolean			acoverREFERENCE_XML;

    private String	coverMETAIID = null;


    public AlFormatEPUB() {
        cssStyles = new AlCSSHtml();
    }

    @Override
    public void initState(AlBookOptions bookOptions, AlFiles myParent, AlPreferenceOptions pref, AlStylesOptions stl) {
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
        allState.incSkipped();

        currentFile = null;
        active_file = UNKNOWN_FILE_SOURCE_NUM;
        toc_state = 0;
        toc_point = null;
        toc_section = -1;

        cssStyles.init(this, TAL_CODE_PAGES.CP65001, AlCSSHtml.CSSHTML_SET_EPUB);
        if ((bookOptions.formatOptions & AlFiles.BOOKOPTIONS_DISABLE_CSS) != 0)
            cssStyles.disableExternal = true;

        if (false) {
            multiFiles.modePart = false;
        } else {
            //
            multiFiles.savedPosition = bookOptions.readPosition;
            if ((bookOptions.formatOptions & AlFiles.LEVEL1_BOOKOPTIONS_NEED_MULTIFILE_FULL) != 0) {
                multiFiles.modePart = false;
            } else {
                multiFiles.isMultiFiles = false;
                if (bookOptions.readPositionAddon > 0 && bookOptions.readPosition > 0) {
                    // reading away after the start
                    multiFiles.queryWaitingPosition = bookOptions.readPositionAddon;
                    multiFiles.modePart = true;
                } else
                if (bookOptions.readPosition == 0 || bookOptions.readPositionAddon == 0) {
                    // first open of file or begin of file
                    multiFiles.queryWaitingPosition = 0;
                    multiFiles.modePart = true;
                } else {
                    // this error. disable part mode
                    multiFiles.queryWaitingPosition = 0;
                    multiFiles.modePart = false;
                }
            }
        }

        parser(0, -1);
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
            case AlFormatTag.TAG_REL:
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
                        if (par0.get(parNum).start == pos && a.section == 0)
                            par0.get(parNum).prop |= AlParProperty.SL2_BREAK_BEFORE;

                        break;
                    }
                }

                addContent(AlOneContent.add(a.name, pos, a.section));
            }
        }

        if (coverMETAIID != null) {
            coverName = coverMETAIID;
        } else
        if (coverITEM_hrefImage != null) {
            coverName = coverITEM_hrefImage;
        }

        if (noUseCover || coverName == null || imageFIRST == null || coverName.contentEquals(imageFIRST)) {
            if (imageFIRST != null && par0.size() > 1)
                par0.get(1).paragraph |= AlStyles.SL_COVER;
            removeCover();
        }

        if (imageFIRST != null) {
            coverName = imageFIRST;
        }

        super.prepareCustom();
    }

    @Override
    public void setSpecialText(boolean flag) {
        if (flag) {
            allState.state_special_flag = true;
            specialBuff.clear();
        } else {
            if (specialBuff.isTOC) {
                toc_text.setLength(0);
                toc_text.append(specialBuff.buff.toString().trim());
                specialBuff.isTOC = false;
            } else
            if (specialBuff.isAuthor) {
                bookAuthors.add(specialBuff.buff.toString().trim());
                specialBuff.isAuthor = false;
            } else
            if (specialBuff.isGenre) {
                bookGenres.add(specialBuff.buff.toString().trim());
                specialBuff.isGenre = false;
            } else
            if (specialBuff.isBookTitle) {
                bookTitle = specialBuff.buff.toString().trim();
                specialBuff.isBookTitle = false;
            } else
            if (specialBuff.isCSSStyle) {
                cssStyles.parseBuffer(specialBuff.buff, currentFile);
                specialBuff.isCSSStyle = false;
            }

            allState.state_special_flag = false;
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

        param = tag.getATTRValue(AlFormatTag.TAG_NAME);
        StringBuilder tp = tag.getATTRValue(AlFormatTag.TAG_TYPE);
        if (param != null)
            addtestLink(param.toString(), (tp != null) && (tp.toString().startsWith(EPUB_FOOTNOTEMARK)));

        return false;
    }

    void addtestLink(String s, boolean isFootnote) {

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



    private boolean addImages() {
        StringBuilder param = tag.getATTRValue(AlFormatTag.TAG_SRC);
        if (param == null)
            param = tag.getATTRValue(AlFormatTag.TAG_HREF);

        if (param != null) {
            String name = AlFiles.getAbsoluteName(currentFile, param.toString());

            int num_file = aFiles.getExternalFileNum(name);
            if (num_file != AlFiles.LEVEL1_FILE_NOT_FOUND)
                name = aFiles.getExternalAbsoluteFileName(num_file);

            if (par0.size() == 1 && imageFIRST == null)
                imageFIRST = name;

            if (allState.vector_image)
                allState.clearSkipped();

            addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
            addTextFromTag(name, false);
            addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);

            if (allState.vector_image)
                allState.restoreSkipped();

        }

        return false;
    }

    private int testFiles2Read(String fname, int sp) {
        String addFiles = AlFiles.getAbsoluteName(currentFile, fname);
        return ((AlFilesEPUB) aFiles).testFilesToRecord(addFiles, sp);
    }

    private void addFiles2Read(String fname, int sp) {
        String addFiles = AlFiles.getAbsoluteName(currentFile, fname);
        ((AlFilesEPUB) aFiles).addFilesToRecord(addFiles, sp);
        if (dinamicSize)
            stop_posUsed = aFiles.getSize();
    }

    private int removeFiles2Read(int num) {
        int res = ((AlFilesEPUB) aFiles).removeFilesFromRecord(num);
        if (dinamicSize)
            stop_posUsed = aFiles.getSize();
        return res;
    }

    @Override
    protected boolean externPrepareTAG() {
        StringBuilder param;

        if ((allState.description & (AlStateLevel2.PAR_DESCRIPTION1 | AlStateLevel2.PAR_DESCRIPTION2 | AlStateLevel2.PAR_DESCRIPTION3 | AlStateLevel2.PAR_DESCRIPTION4)) == 0) {
            param = tag.getATTRValue(AlFormatTag.TAG_ID);
            StringBuilder tp = tag.getATTRValue(AlFormatTag.TAG_TYPE);
            if (param != null)
                addtestLink(param.toString(), (tp != null) && (tp.toString().startsWith(EPUB_FOOTNOTEMARK)));
        }


        switch (tag.tag) {

            case AlFormatTag.TAG_A:
                if (tag.closed) {
                    if ((styleStack.buffer[styleStack.position].paragraph & AlStyles.STYLE_LINK) != 0)
                        clearTextStyle(AlStyles.STYLE_LINK);
                } else if (!tag.ended) {
                    if (addNotes())
                        setTextStyle(AlStyles.STYLE_LINK);
                } else {

                }
                return true;
            case AlFormatTag.TAG_ASIDE:
                if (tag.closed) {
                    closeOpenNotes();
                    if (preference.onlyPopupFootnote)
                        if ((styleStack.buffer[styleStack.position].paragraph & AlStyles.STYLE_HIDDEN) != 0)
                            clearTextStyle(AlStyles.STYLE_HIDDEN);
                } else if (!tag.ended) {
                    StringBuilder tp = tag.getATTRValue(AlFormatTag.TAG_TYPE);
                    if (preference.onlyPopupFootnote && tp != null && tp.toString().startsWith(EPUB_FOOTNOTEMARK))
                        setTextStyle(AlStyles.STYLE_HIDDEN);
                }
                //newParagraph();
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
            case AlFormatTag.TAG_H1:
                if (tag.closed) {
                    newParagraph();
                    newEmptyTextParagraph();
                } else if (!tag.ended) {
                    newParagraph();
                    newEmptyTextParagraph();
                    setParagraphStyle(AlStyles.SL_SPECIAL_PARAGRAPGH);
                    isFirstParagraph = true;
                } else {

                }
                return true;
            case AlFormatTag.TAG_IMAGE:
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

                } else if (!tag.ended) {
                    allState.clearSkipped();

                    newParagraph();
                    setPropStyle(AlParProperty.SL2_BREAK_BEFORE);

                    allState.decSkipped();

                    cssStyles.enable = false;
                    cssStyles.fixWorkSet();

                } else {

                }
                return true;
            case AlFormatTag.TAG_META:
                if (tag.closed) {

                } else {
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION2) != 0) {
                        param = tag.getATTRValue(AlFormatTag.TAG_NAME);
                        if (param != null && (META_NAME_COVER.contentEquals(param))) {
                            param = tag.getATTRValue(AlFormatTag.TAG_CONTENT);
                            if (param != null) {
                                coverMETAIID = param.toString();
                            }
                        }
                    }
                }
                return true;
            case AlFormatTag.TAG_CONTENT:
                if (tag.closed) {

                } else if (!tag.ended) {

                } else {
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION3) != 0) {
                        if (toc_state == TOC_NAVMAP + TOC_NAVPOINT) {
                            toc_point = null;
                            param = tag.getATTRValue(AlFormatTag.TAG_SRC);
                            if (param != null)
                                toc_point = param.toString();

                            if (toc_point.length() > 0 && toc_text.length() > 0) {
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
                }
                return true;
            case AlFormatTag.TAG_ROOTFILE:
                if (tag.closed) {

                } else if (!tag.ended) {

                } else {
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION1) != 0) {
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

                } else if (!tag.ended) {

                } else {
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION2) != 0) {
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

                } else if (!tag.ended) {

                } else {
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION2) != 0) {
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
                                        } else if (a.type.contains(META_NAME_XMLHTML)) {
                                            coverITEM_idXML = file;
                                        }
                                    }

                                    if (href.indexOf(META_NAME_COVER) != -1) {
                                        if (a.type.startsWith("image/")) {
                                            coverITEM_hrefImage = file;
                                        } else if (a.type.startsWith(META_NAME_XMLHTML)) {
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

                } else if (!tag.ended) {

                } else {
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION2) != 0) {
                        StringBuilder href = tag.getATTRValue(AlFormatTag.TAG_HREF);
                        param = tag.getATTRValue(AlFormatTag.TAG_TYPE);
                        if (param != null && href != null) {
                            String file;

                            if (META_NAME_COVER.contentEquals(param)) {
                                file = AlFiles.getAbsoluteName(currentFile, href.toString());
                                if (aFiles.getExternalFileNum(file) != AlFiles.LEVEL1_FILE_NOT_FOUND)
                                    coverREFERENCE_XML = file;
                            }

                            if (param.indexOf("note") == 0) {
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
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION2) != 0) {
                        setSpecialText(false);
                    }
                } else if (!tag.ended) {
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION2) != 0) {
                        specialBuff.isBookTitle = tag.tag == AlFormatTag.TAG_TITLE;
                        specialBuff.isAuthor = tag.tag == AlFormatTag.TAG_CREATOR;
                        setSpecialText(true);
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

                } else if (!tag.ended) {
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION2) != 0) {
                        param = tag.getATTRValue(AlFormatTag.TAG_TOC);
                        if (param != null) {
                            AlOneEPUBIDItem it = mapId.get(param.toString());
                            if (it != null)
                                addFiles2Read(it.href, AlOneZIPRecord.SPECIAL_TOC);
                        }
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_NAVMAP:
                if (tag.closed) {
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION3) != 0)
                        toc_state &= ~TOC_NAVMAP;
                } else if (!tag.ended) {
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION3) != 0)
                        toc_state |= TOC_NAVMAP;
                } else {

                }
                return true;
            case AlFormatTag.TAG_NAVPOINT:
                if (tag.closed) {
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION3) != 0) {
                        if (toc_state == TOC_NAVMAP + TOC_NAVPOINT) {
                            if (toc_section > 0) {
                                toc_section--;
                            } else if (toc_section == 0) {
                                toc_section--;
                                toc_state &= ~TOC_NAVPOINT;
                            }
                        }
                    }
                } else if (!tag.ended) {
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION3) != 0) {
                        if ((toc_state == TOC_NAVMAP) || (toc_state == TOC_NAVMAP + TOC_NAVPOINT)) {
                            toc_section++;
                            if (toc_section == 0) {
                                toc_state |= TOC_NAVPOINT;
                            } else {
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
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_NAVLABEL:
                if (tag.closed) {
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION3) != 0) {
                        if (toc_state == TOC_NAVMAP + TOC_NAVPOINT + TOC_NAVLABEL) {
                            toc_state &= ~TOC_NAVLABEL;
                        }
                    }
                } else if (!tag.ended) {
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION3) != 0) {
                        if (toc_state == TOC_NAVMAP + TOC_NAVPOINT) {
                            toc_state |= TOC_NAVLABEL;
                        }
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_TEXT:
                if (tag.closed) {
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION3) != 0) {
                        if (toc_state == TOC_NAVMAP + TOC_NAVPOINT + TOC_NAVLABEL + TOC_TEXT) {
                            toc_state &= ~TOC_TEXT;
                            setSpecialText(false);
                        }
                    }
                } else if (!tag.ended) {
                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION3) != 0) {
                        if (toc_state == TOC_NAVMAP + TOC_NAVPOINT + TOC_NAVLABEL) {
                            toc_state |= TOC_TEXT;
                            specialBuff.isTOC = true;
                            setSpecialText(true);
                        }
                    }
                } else {

                }
                return true;
            case AlFormatTag.TAG_EXTFILE:
                if (tag.closed) {

                    cssStyles.disableWorkSet();
                    cssStyles.enable = true;

                    closeOpenNotes();

                    if ((allState.description & AlStateLevel2.PAR_DESCRIPTION2) != 0) {
                        if (!multiFiles.modePart) {
                            for (int i = 0; i < listFiles.size(); i++)
                                addFiles2Read(listFiles.get(i).file, listFiles.get(i).note ? AlOneZIPRecord.SPECIAL_NOTE : AlOneZIPRecord.SPECIAL_NONE);
                        } else {

                            multiFiles.isMultiFiles = listFiles.size() > AlMultiFiles.LEVEL_FOR_MULTI * 5;

                            if (!multiFiles.isMultiFiles) {
                                for (int i = 0; i < listFiles.size(); i++)
                                    addFiles2Read(listFiles.get(i).file, listFiles.get(i).note ? AlOneZIPRecord.SPECIAL_NOTE : AlOneZIPRecord.SPECIAL_NONE);
                                multiFiles.modePart = false;
                            } else {
                                if (multiFiles.queryWaitingPosition == 0) {
                                    multiFiles.firstMultiFile = 0;
                                    int lastMultiFile = multiFiles.firstMultiFile;
                                    for (int i = 0; i < listFiles.size(); i++) {
                                        addFiles2Read(listFiles.get(i).file, listFiles.get(i).note ? AlOneZIPRecord.SPECIAL_NOTE : AlOneZIPRecord.SPECIAL_NONE);
                                        if (++lastMultiFile > multiFiles.firstMultiFile + AlMultiFiles.LEVEL_FOR_MULTI)
                                            break;
                                    }
                                    multiFiles.firstMultiFileReal = multiFiles.firstMultiFile;
                                    multiFiles.queryWaitingPosition = ((long) size) << 32L;
                                } else {

                                    int startLevel1Size = aFiles.getSize();
                                    multiFiles.firstMultiFile = -1;

                                    for (int i = 0; i < listFiles.size(); i++) {
                                        if ((multiFiles.queryWaitingPosition & 0x7fffffff) == startLevel1Size) {
                                            multiFiles.firstMultiFile = i;
                                            break;
                                        }

                                        startLevel1Size += testFiles2Read(listFiles.get(i).file, listFiles.get(i).note ?
                                                AlOneZIPRecord.SPECIAL_NOTE : AlOneZIPRecord.SPECIAL_NONE);
                                    }

                                    if (multiFiles.firstMultiFile == -1) {
                                        // error - read all files;
                                        for (int i = 0; i < listFiles.size(); i++)
                                            addFiles2Read(listFiles.get(i).file, listFiles.get(i).note ? AlOneZIPRecord.SPECIAL_NOTE : AlOneZIPRecord.SPECIAL_NONE);
                                        multiFiles.modePart = false;
                                    } else {
                                        int tmp = 0;
                                        // add first LEVEL_FOR_MULTI files
                                        for (int i = 0;
                                             i < Math.min(listFiles.size(), AlMultiFiles.LEVEL_FOR_MULTI); i++) {
                                            if (i == multiFiles.firstMultiFile)
                                                multiFiles.firstMultiFileReal = tmp;

                                            addFiles2Read(listFiles.get(i).file, listFiles.get(i).note ? AlOneZIPRecord.SPECIAL_NOTE : AlOneZIPRecord.SPECIAL_NONE);
                                            tmp++;
                                        }

                                        // add only need
                                        for (int i = Math.max(multiFiles.firstMultiFile, AlMultiFiles.LEVEL_FOR_MULTI + 1);
                                             i < Math.min(listFiles.size(), multiFiles.firstMultiFile + AlMultiFiles.LEVEL_FOR_MULTI); i++) {

                                            if (i == multiFiles.firstMultiFile)
                                                multiFiles.firstMultiFileReal = tmp;

                                            addFiles2Read(listFiles.get(i).file, listFiles.get(i).note ? AlOneZIPRecord.SPECIAL_NOTE : AlOneZIPRecord.SPECIAL_NONE);

                                            tmp++;
                                        }
                                    }

                                }
                            }
                        }

                        if (coverMETAIID != null) {
                            AlOneEPUBIDItem it = mapId.get(coverMETAIID);
                            coverMETAIID = null;
                            if (it != null)
                                coverMETAIID = it.href;
                        }

                        acoverITEM_hrefXML = acoverITEM_idXML = acoverREFERENCE_XML = false;

                        if (needUnpackAfterAllRead)
                            aFiles.needUnpackData();

                    }

                    newParagraph();

                    if ((allState.description & (AlStateLevel2.PAR_NOTE | AlStateLevel2.PAR_DESCRIPTION1 | AlStateLevel2.PAR_DESCRIPTION2 |
                            AlStateLevel2.PAR_DESCRIPTION3 | AlStateLevel2.PAR_DESCRIPTION4)) != 0)
                        setPropStyle(AlParProperty.SL2_BREAK_BEFORE);

                    allState.description &= ~(AlStateLevel2.PAR_DESCRIPTION1 | AlStateLevel2.PAR_DESCRIPTION2 |
                            AlStateLevel2.PAR_DESCRIPTION3 | AlStateLevel2.PAR_DESCRIPTION4 | AlStateLevel2.PAR_NOTE);


                    active_file = UNKNOWN_FILE_SOURCE_NUM;
                    currentFile = null;

                    allState.incSkipped();
                } else if (!tag.ended) {
                    styleStack.clear();

                    param = tag.getATTRValue(AlFormatTag.TAG_NUMFILES);
                    if (param != null)
                        active_file = InternalFunc.str2int(param, 10);

                    param = tag.getATTRValue(AlFormatTag.TAG_ID);
                    if (param != null)
                        currentFile = param.toString();

                    param = tag.getATTRValue(AlFormatTag.TAG_IDREF);
                    if (param != null) {
                        switch (InternalFunc.str2int(param, 10)) {
                            case AlOneZIPRecord.SPECIAL_NONE:
                                if (multiFiles.modePart) {
                                    if (multiFiles.counterPart == multiFiles.firstMultiFileReal)
                                        multiFiles.queryRealPosition = size;
                                    multiFiles.counterPart++;
                                } else {
                                    multiFiles.collect.add(AlOneMultiFile.add(allState.start_position_par, size));
                                }
                                break;
                            case AlOneZIPRecord.SPECIAL_FIRST:
                                allState.description |= AlStateLevel2.PAR_DESCRIPTION1;
                                break;
                            case AlOneZIPRecord.SPECIAL_CONTENT:
                                allState.description |= AlStateLevel2.PAR_DESCRIPTION2;

                                allState.clearSkipped();

                                setParagraphStyle(AlStyles.SL_COVER);
                                addCharFromTag((char) AlStyles.CHAR_IMAGE_S, false);
                                addCharFromTag(LEVEL2_COVERTOTEXT, false);
                                addCharFromTag((char) AlStyles.CHAR_IMAGE_E, false);
                                clearParagraphStyle(AlStyles.SL_COVER);

                                allState.restoreSkipped();
                                break;
                            case AlOneZIPRecord.SPECIAL_TOC:
                                toc_base = currentFile;
                                allState.description |= AlStateLevel2.PAR_DESCRIPTION3;
                                break;
                            case AlOneZIPRecord.SPECIAL_NOTE:
                                setStateStyle(AlStateLevel2.PAR_NOTE);
                                setPropStyle(AlParProperty.SL2_BREAK_BEFORE);
                                break;
                        }
                    }

                } else {

                }
                return true;
            case AlFormatTag.TAG_SVG:
                if (tag.closed) {
                    allState.decSkipped();
                    allState.vector_image = false;
                } else if (!tag.ended) {
                    allState.incSkipped();
                    allState.vector_image = true;
                } else {

                }
                return true;
        }


        return super.externPrepareTAG();

    }

    //private int countTextFiles = 0;

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
