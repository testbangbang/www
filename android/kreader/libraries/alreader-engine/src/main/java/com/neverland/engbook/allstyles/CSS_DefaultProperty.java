package com.neverland.engbook.allstyles;

import com.neverland.engbook.forpublic.AlEngineOptions;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level2.AlFormatTag;
import com.neverland.engbook.util.AlDeafultTextParameters;
import com.neverland.engbook.util.AlOneXMLAttrClass;
import com.neverland.engbook.util.AlParProperty;
import com.neverland.engbook.util.AlStyles;

import java.io.UnsupportedEncodingException;

public class CSS_DefaultProperty extends AlCSSHtml {

    public static String DEFAULT_CSS_ALL =
    "first-letter {font-family:fantasy; font-size:1.44rem; font-weight:bold}" +
    "p {text-indent:2em;}" +
    "p.notes {font-size:0.83rem;}" +
    "p.left {text-indent:0em; text-align:left; margin-left:0; margin-right:0;}" +
    "p.right {text-indent:0em; text-align:right; margin-left:0; margin-right:0;}" +
    "p.center {text-indent:0em; text-align:center; margin-left:0; margin-right:0;}" +
    "title {font-family:fantasy; text-indent:0; text-shadow:1; font-size:1.44rem; text-align:center; margin-top:2%; margin-bottom:3%; margin-left:10%; margin-right:10%; hyphens:none;}" +
    "subtitle {font-size:1.2rem; text-align:center; text-indent:0; margin-top:1%; margin-bottom:1%; margin-left:5%; margin-right:5%;}" +
    "image {text-indent:0;}";

    public void init(AlDeafultTextParameters defp) {
        supportLevel = AlEngineOptions.CSS_SUPPORT_ALL;

        AlSetCSS a = new AlSetCSS(this);
        a.name = "::default";
        allSets.add(a);

        try {
            parse(0, DEFAULT_CSS_ALL.getBytes("UTF-8"), -1, 0, TAL_CODE_PAGES.CP65001, null, -1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        long sz;

        defp.p_prop = AlParProperty.SL2_INDENT_DEFAULTEM;
        clear();
        if (apply1OneClassX4CSSDefault(AlFormatTag.TAG_P, 0x00, dpar)) {
            defp.p_par = dpar.paragraph;
            defp.p_prop = dpar.prop & AlParProperty.SL2_INDENT_MASK;
        }

        clear();
        if (apply1OneClassX4CSSDefault(AlFormatTag.TAG_P, AlFormatTag.TAG_NOTES, dpar)) {
            sz = (int)(dpar.fontSize0);
            if (sz > 511)
                sz = 511;
            defp.notes_par = (dpar.paragraph | (sz << AlStyles.SL_SIZE_SHIFT)) & MASK_FOR_FLETTER;;
            defp.notes_par = dpar.prop;
        }

        clear();
        if (apply1OneClassX4CSSDefault(AlFormatTag.TAG_FIRST_LETTER, 0x00, dpar)) {
            sz = (int)(dpar.fontSize0);
            if (sz > 511)
                sz = 511;
            defp.flet_par = (dpar.paragraph | (sz << AlStyles.SL_SIZE_SHIFT)) & MASK_FOR_FLETTER;;
            defp.flet_prop = dpar.prop;
        }

        clear();
        if (apply1OneClassX4CSSDefault(AlFormatTag.TAG_TITLE, 0x00, dpar)) {

        }

        clear();
        if (apply1OneClassX4CSSDefault(AlFormatTag.TAG_SUBTITLE, 0x00, dpar)) {

        }

        allSets.clear();
    }

    private final AlOneXMLAttrClass   cls = new AlOneXMLAttrClass();
    private final DefCSSPar           dpar = new DefCSSPar();

    private void clear() {
        cls.clear();
        dpar.clearValues();
    }

    public static final long MASK_FOR_FLETTER = AlStyles.STYLE_ITALIC | AlStyles.STYLE_BOLD | AlStyles.SL_SIZE_MASK | AlStyles.SL_FONT_MASK | AlStyles.SL_SHADOW;
}
