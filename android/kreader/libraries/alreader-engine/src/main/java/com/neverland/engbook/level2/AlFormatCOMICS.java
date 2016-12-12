package com.neverland.engbook.level2;


import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.util.AlOneImage;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;

public class AlFormatCOMICS extends AlAXML {

    public static boolean isCOMICS(AlFiles a) {
        if (a.getIdentStr().contentEquals("cbzr"))
            return true;
        return false;
    }

    private static final int ACBF_TEST_BUF_LENGTH = 1024;
    private static final String ACBF_TEST_STR_1 = "<acbf";

    public static boolean isACBF(AlFiles a) {
        char[] buf_uc = new char[ACBF_TEST_BUF_LENGTH];
        String s;

        if (getTestBuffer(a, TAL_CODE_PAGES.CP1251, buf_uc, ACBF_TEST_BUF_LENGTH, true)) {
            s = String.copyValueOf(buf_uc);
            if (s.contains(ACBF_TEST_STR_1))
                return true;
        }

        if (getTestBuffer(a, TAL_CODE_PAGES.CP1200, buf_uc, ACBF_TEST_BUF_LENGTH, true)) {
            s = String.copyValueOf(buf_uc);
            if (s.contains(ACBF_TEST_STR_1))
                return true;
        }

        if (getTestBuffer(a, TAL_CODE_PAGES.CP1201, buf_uc, ACBF_TEST_BUF_LENGTH, true)) {
            s = String.copyValueOf(buf_uc);
            if (s.contains(ACBF_TEST_STR_1))
                return true;
        }

        return false;
    }

    public AlFormatCOMICS() {
        image_start = -1;
        image_stop = -1;
        image_name = null;
    }

    @Override
    public void initState(AlBookOptions bookOptions, AlFiles myParent, AlPreferenceOptions pref, AlStylesOptions stl) {
        allState.isOpened = true;

        isTextFormat = false;
        xml_mode = true;
        ident = "COMICS";

        aFiles = myParent;

        if ((bookOptions.formatOptions & AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG) != 0)
            aFiles.needUnpackData();

        preference = pref;
        styles = stl;

        size = 0;

        autoCodePage = false;
        setCP(TAL_CODE_PAGES.CP65001);

        allState.state_parser = STATE_XML_TEXT;
        allState.state_skipped_flag = true;

        parser(0, aFiles.getSize());
        newParagraph();

        allState.isOpened = false;
    }

    private int numPage = 0;

    @Override
    public int getPageStart(int pos) {
        return par.get(pos).start;
    }

    @Override
    public int getCountPages() {
        return par.size();
    }

    private int image_start;
    private int image_stop;
    private String image_name;

    private void addtestImage() {
        if (allState.isOpened) {
            if (image_start > 0) {
                image_stop = tag.start_pos;
                im.add(AlOneImage.add(image_name, image_start, image_stop, AlOneImage.IMG_BASE64));
            }
        }
        image_start = -1;
    }

    private void testImage() {
        image_start = -1;
        if (allState.isOpened) {
            StringBuilder s1 = tag.getATTRValue(AlFormatTag.TAG_ID);
            if (s1 != null) {
                image_name = s1.toString();
                image_start = allState.start_position;
            }
        }
    }

    @Override
    public boolean externPrepareTAG() {
        switch (tag.tag) {
            case  AlFormatTag.TAG_IMAGE:
                StringBuilder param = tag.getATTRValue(AlFormatTag.TAG_SRC);

                if (param == null)
                    param = tag.getATTRValue(AlFormatTag.TAG_HREF);

                if (param != null) {
                    allState.state_skipped_flag = false;

                    if (allState.isOpened) {
                        newParagraph();
                        setParagraphStyle(AlStyles.PAR_COVER);

                        if (param.charAt(0) == '#') {
                            numPage++;
                        } else {
                            im.add(AlOneImage.addLowQuality(param.toString(), numPage++, size, AlOneImage.IMG_MEMO));
                        }
                    }

                    addCharFromTag((char)AlStyles.CHAR_IMAGE_S, false);
                    addTextFromTag(param, false);
                    addCharFromTag((char)AlStyles.CHAR_IMAGE_E, false);

                    allState.state_skipped_flag = true;
                }
                return true;

            case AlFormatTag.TAG_BINARY:
                if (tag.closed) {
                    addtestImage();
                } else
                if (!tag.ended) {
                    allState.state_parser = STATE_XML_SKIP;
                    testImage();
                } else {

                }
                return true;
        }

        return false;
    }
}
