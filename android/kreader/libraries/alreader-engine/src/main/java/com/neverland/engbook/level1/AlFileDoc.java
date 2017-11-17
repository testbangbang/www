package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.EngBookMyType.TAL_FILE_TYPE;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.util.AlOneImage;
import com.neverland.engbook.util.AlStyles;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

public class AlFileDoc extends AlFileOle {

    private enum Version { DOS, WW2, WW6, WW8 }
    Version version;

    private static final int DOS_MAGIC = 0xBE31;
    private static final int WRI_MAGIC = 0xBE32;
    private static final int WW2_MAGIC_1 = 0xA59B;
    private static final int WW2_MAGIC_2 = 0xA5DB;
    private static final int WW6_MAGIC  = 0xA5DC;
    private static final int WW8_MAGIC  = 0xA5EC;

    public static TAL_FILE_TYPE isDOC(String fName, AlFiles a, ArrayList<AlFileZipEntry> fList, String ext) {

        if (ext != null && !ext.equalsIgnoreCase(".doc"))
            return TAL_FILE_TYPE.TXT;

        if (a.getSize() < 256)
            return TAL_FILE_TYPE.TXT;

        if (!isOleFile(a)) {
            a.read_pos = 0;
            int magic = a.getWord();
            if (magic != DOS_MAGIC &&
                    magic != WRI_MAGIC &&
                    magic != WW2_MAGIC_1 &&
                    magic != WW2_MAGIC_2)
                return TAL_FILE_TYPE.TXT;
        }

        return TAL_FILE_TYPE.DOC;
    }

    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
        super.initState(file, myParent, fList);

        int res = TAL_RESULT.OK;

        try {
            parent.read_pos = 0;
            int magic = parent.getWord();
            switch (magic) {

                case DOS_MAGIC:
                case WRI_MAGIC:
                    version = Version.DOS;
                    break;

                case WW2_MAGIC_1:
                case WW2_MAGIC_2:
                    version = Version.WW2;
                    break;

                default:
                    super.parse();
                    docStream = super.stream("WordDocument");
                    if (docStream < 0)
                        throw new IOException();
                    byte[] wtemp = new byte[2];
                    super.read(docStream, wtemp, 2);
                    magic = word(wtemp, 0);
                    switch (magic) {

                        case WW6_MAGIC:
                            version = Version.WW6;
                            break;

                        case WW8_MAGIC:
                            version = Version.WW8;
                            break;

                        default:
                            throw new IOException();
                    }
            }

            read_fib();

            readPiece();
            readBinTab();
            readHref();
            readStyles();
            super.kick();

            makePiece();
            makeBinTab();
            makeHref();
            makeStyles();

        } catch (RuntimeException e) {
            e.printStackTrace();
            res = TAL_RESULT.ERROR;
        } catch (IOException e) {
            e.printStackTrace();
            res = TAL_RESULT.ERROR;
        }

        ident = "doc";
        return res;
    }

    public boolean isUnicode() {
        return (fib.flags & 0x1000) != 0;
    }

    @Override
    public int getCodePage() {

        if (version == Version.DOS)
            return -1;

        switch (fib.lid & 0x3ff) {
            case 0x005: //CZECH
            case 0x00E: //HUNGARIAN
            case 0x015: //POLISH
            case 0x01A: //SERBIAN/CROATIAN
            case 0x01B: //SLOVAK
            case 0x024: //SLOVENIAN
                return 1250;

            case 0x002: //BULGARIAN
            case 0x019: //RUSSIAN
            case 0x022: //UKRAINIAN
            case 0x023: //BELARUSIAN
            case 0x02F: //MACEDONIAN
            case 0x03F: //KAZAK
                return 1251;

            case 0x008: //GREEK
                return 1253;

            case 0x01F: //TURKISH
                return 1254;

            case 0x00D: //HEBREW
                return 1255;

            case 0x001: //ARABIC
            case 0x020: //URDU
            case 0x021: //INDONESIAN
            case 0x029: //FARSI
                return 1256;

            case 0x025: //ESTONIAN
            case 0x026: //LATVIAN
            case 0x027: //LITHUANIAN
                return 1257;
        }

        //0x028: TAJIK
        //0x02B: ARMENIAN
        //0x02C: AZERI
        //0x037: GEORGIAN
        //0x040: KIRGHIZ
        //0x042: TURKMEN
        //0x043: UZBEK
        //0x044: TATAR

        //0x004: CHINESE
        //0x011: JAPANESE
        //0x012: KOREAN
        //0x01E: THAI
        //0x02A: VIETNAMESE
        //0x039: HINDI
        //0x03E: MALAY
        //0x041: SWAHILI
        //0x049: TAMIL
        //0x050: MONGOLIAN
        //0x051: TIBETAN
        //0x053: KHMER
        //0x054: LAOS
        //0x061: NEPALI
        //0x064: FILIPINO

        //0x003: CATALAN
        //0x006: DANISH
        //0x007: GERMAN
        //0x009: ENGLISH
        //0x00A: SPANISH
        //0x00B: FINNISH
        //0x00C: FRENCH
        //0x00F: ICELANDIC
        //0x010: ITALIAN
        //0x013: DUTCH
        //0x014: NORWEGIAN
        //0x016: PORTUGUESE
        //0x018: ROMANIAN
        //0x01C: ALBANIAN
        //0x01D: SWEDISH
        //0x02D: BASQUE
        //0x036: AFRIKAANS
        //0x03C: GAELIC
        //0x03D: YIDDISH

        return 1252;
    }

    @Override
    protected int getBuffer(int pos, byte[] dst, int cnt) {
        if (isUnicode()) pos >>= 1;
        //int len = dst.length;
        int res = 0;

        try {
            int n = findPiece(pos);
            while (cnt > 0) {
                if (piece.get(n).filePos == Integer.MAX_VALUE)
                    break;
                int charPos = piece.get(n-1).charPos;
                int offset = pos - charPos;
                int size = piece.get(n).charPos - charPos - offset;
                if (!isUnicode()) {
                    if (size > cnt) size = cnt;
                    super.add(docStream + piece.get(n).filePos + offset, dst, res, size, 0);
                    res += size;
                    cnt -= size;
                } else {
                    if (size > cnt / 2) size = cnt / 2;
                    if (piece.get(n).codepage == 1200) {
                        super.add(docStream + piece.get(n).filePos + offset*2, dst, res, size * 2, 0);
                    } else {
                        super.add(docStream + piece.get(n).filePos + offset, dst, res + size, size, 1);
                    }
                    res += size * 2;
                    cnt -= size * 2;
                }
                pos += size;
                n++;
            }

            for (Read rd = super.step(); rd != null; rd = super.step()) {
                if (rd.tag == 1) {
					/* конвертируем из cp1252 в utf16 */
                    char[] tabl = AlUnicode.getDataCP(1252);
                    int b = rd.pos;
                    int w = rd.pos - rd.len;
                    int l = rd.len;
                    for (int j = 0; j < l; j++) {
                        char c = (char)(rd.buf[b++] & 255);
                        if (c >= 128) c = tabl[c - 128];
                        rd.buf[w++] = (byte)(c & 255);
                        rd.buf[w++] = (byte)(c >> 8);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            for (; res < cnt; res++)
                dst[res] = 0x00;
        }

        return res;
    }

    public static class Format {
        public int start;
        public int limit;
        public int value;
        public int xnote;
        public int xdata;

        public static final int STYLE_ITALIC  = (int) AlStyles.STYLE_ITALIC;
        public static final int STYLE_BOLD	= (int) AlStyles.STYLE_BOLD;
        public static final int STYLE_SUP	 = (int) AlStyles.STYLE_SUP;
        public static final int STYLE_SUB	 = (int) AlStyles.STYLE_SUB;
        public static final int STYLE_UNDER   = (int) AlStyles.STYLE_UNDER;
        public static final int STYLE_STRIKE  = (int) AlStyles.STYLE_STRIKE;
        public static final int STYLE_HIDDEN  = 0x1000;
        public static final int STYLE_NEWPAR  = 0x2000;
        public static final int STYLE_OBJ	 = 0x4000;
        public static final int STYLE_OLE2	= 0x8000;

        public static final int SHIFT_LEVEL   = 16;
        public static final int MASK_LEVEL	= 15 << SHIFT_LEVEL;

        public static final int SHIFT_ALIGN   = 20;
        public static final int MASK_ALIGN	= 7 << SHIFT_ALIGN;
        public static final int NORMAL  = 0;
        public static final int LEFT	= 1;
        public static final int RIGHT   = 2;
        public static final int CENTER  = 3;
        public static final int JUSTIFY = 4;

        public static final int SHIFT_SPECIAL = 23;
        public static final int MASK_SPECIAL  = 7 << SHIFT_SPECIAL;
        public static final int SPECIAL = 1;
        public static final int FOOTREF = 2;
        public static final int FOOTTEXT= 3;
        public static final int ENDREF  = 4;
        public static final int ENDTEXT = 5;
        public static final int ANNREF  = 6;
        public static final int ANNTEXT = 7;

        public boolean newPar() { return (value & STYLE_NEWPAR) != 0; }
        public boolean bold()   { return (value & STYLE_BOLD  ) != 0; }
        public boolean italic() { return (value & STYLE_ITALIC) != 0; }
        public boolean strike() { return (value & STYLE_STRIKE) != 0; }
        public boolean under()  { return (value & STYLE_UNDER ) != 0; }
        public boolean sub()	{ return (value & STYLE_SUB   ) != 0; }
        public boolean sup()	{ return (value & STYLE_SUP   ) != 0; }
        public boolean hidden() { return (value & STYLE_HIDDEN) != 0; }
        public boolean obj()	{ return (value & STYLE_OBJ   ) != 0; }
        public boolean ole2()   { return (value & STYLE_OLE2  ) != 0; }

        public int level()  { return (value & MASK_LEVEL ) >> SHIFT_LEVEL; }
        public int align()  { return (value & MASK_ALIGN ) >> SHIFT_ALIGN; }
        public int special(){ return (value & MASK_SPECIAL) >> SHIFT_SPECIAL; }

        protected void newPar (boolean v) { if (v) value |=  STYLE_NEWPAR;
        else   value &= ~STYLE_NEWPAR; }
        protected void bold   (boolean v) { if (v) value |=  STYLE_BOLD;
        else   value &= ~STYLE_BOLD; }
        protected void italic (boolean v) { if (v) value |=  STYLE_ITALIC;
        else   value &= ~STYLE_ITALIC; }
        protected void strike (boolean v) { if (v) value |=  STYLE_STRIKE;
        else   value &= ~STYLE_STRIKE; }
        protected void under  (boolean v) { if (v) value |=  STYLE_UNDER;
        else   value &= ~STYLE_UNDER; }
        protected void sub (boolean v) {	if (v) value |=  STYLE_SUB;
        else   value &= ~STYLE_SUB; }
        protected void sup (boolean v) {	if (v) value |=  STYLE_SUP;
        else   value &= ~STYLE_SUP; }
        protected void hidden (boolean v) { if (v) value |=  STYLE_HIDDEN;
        else   value &= ~STYLE_HIDDEN; }
        protected void obj (boolean v) {	if (v) value |=  STYLE_OBJ;
        else   value &= ~STYLE_OBJ; }
        protected void ole2   (boolean v) { if (v) value |=  STYLE_OLE2;
        else   value &= ~STYLE_OLE2; }

        protected void level  (int v) { value &= ~MASK_LEVEL;
            value |= (v << SHIFT_LEVEL) & MASK_LEVEL; }
        protected void align  (int v) { value &= ~MASK_ALIGN;
            value |= (v << SHIFT_ALIGN) & MASK_ALIGN; }
        protected void special(int v) { value &= ~MASK_SPECIAL;
            value |= (v << SHIFT_SPECIAL) & MASK_SPECIAL; }
    }

    public Format format = new Format();
    private Format style = new Format();

    public void getFormat(int addr) {
        format.value = style.value = 0;
        format.xnote = format.xdata = -1;
        if (isUnicode()) addr >>= 1;

        try {

            // определяем диапазоны
            int np = findPiece(addr);
            int pieceStart = piece.get(np-1).charPos;
            int pieceLimit = piece.get(np).charPos;
            int offset = addr - pieceStart;
            Prm charPrm, paraPrm;
            if (piece.get(np).codepage == (char) 1200) {
                int filePos = piece.get(np).filePos + (offset << 1);
                charPrm = getCharPrm(filePos);
                paraPrm = getParaPrm(filePos);
                charPrm.start = addr - ((filePos - charPrm.start) >> 1);
                charPrm.limit = addr - ((filePos - charPrm.limit) >> 1);
                paraPrm.start = addr - ((filePos - paraPrm.start) >> 1);
                paraPrm.limit = addr - ((filePos - paraPrm.limit) >> 1);
            } else {
                int filePos = piece.get(np).filePos + offset;
                charPrm = getCharPrm(filePos);
                paraPrm = getParaPrm(filePos);
                charPrm.start = addr - (filePos - charPrm.start);
                charPrm.limit = addr - (filePos - charPrm.limit);
                paraPrm.start = addr - (filePos - paraPrm.start);
                paraPrm.limit = addr - (filePos - paraPrm.limit);
            }
            Note note = findNote(addr);

            // еще раз проверяем на начало абзаца
            if (paraPrm.start < pieceStart) {
                if (pieceStart == 0) {
                    paraPrm.start = 0;
                } else {
                    int ne = np - 1;
                    int prevStart = piece.get(ne-1).charPos;
                    int prevLimit = piece.get(ne).charPos;
                    int prevAddr = prevLimit - 1;
                    int prevOffset = prevAddr - prevStart;
                    Prm prevPrm;
                    if (piece.get(ne).codepage == (char) 1200) {
                        int filePos = piece.get(ne).filePos + (prevOffset << 1);
                        prevPrm = getParaPrm(filePos);
                        prevPrm.limit = prevAddr - ((filePos - prevPrm.limit) >> 1);
                    } else {
                        int filePos = piece.get(ne).filePos + prevOffset;
                        prevPrm = getParaPrm(filePos);
                        prevPrm.limit = prevAddr - (filePos - prevPrm.limit);
                    }
                    if (prevPrm.limit == prevLimit) {
                        paraPrm.start = prevPrm.limit;
                    }
                }
            }

            // вычисляем наименьший
            int start = pieceStart;
            if (start < charPrm.start) start = charPrm.start;
            if (start < paraPrm.start) start = paraPrm.start;
            if (note.type != 0 && start < note.charPos) start = note.charPos;
            int limit = pieceLimit;
            if (limit > charPrm.limit) limit = charPrm.limit;
            if (limit > paraPrm.limit) limit = paraPrm.limit;

            // вычисляем свойства фрагмента
            if (start == paraPrm.start)
                format.newPar(true);
            applyParaPrm(paraPrm.buf, paraPrm.pos);
            applyCharPrm(charPrm.buf, charPrm.pos);
            applyPiecePrm(piece.get(np).prm);
            if (note.type != 0 && start == note.charPos) {
                format.special(note.type);
                format.xnote = note.index;
            }

            if (isUnicode()) {
                format.start = start << 1;
                format.limit = limit << 1;
            } else {
                format.start = start;
                format.limit = limit;
            }

        } catch (RuntimeException e) {
            format.value = style.value = 0;
            format.xnote = format.xdata = -1;
            format.limit = format.start + 1;
            e.printStackTrace();

        } catch (IOException e) {
            format.value = style.value = 0;
            format.xnote = format.xdata = -1;
            format.limit = format.start + 1;
            e.printStackTrace();

        }
    }

    private int docStream; /* WordDocument Stream  */
    private int tabStream; /* 1Table Stream or 0Table Stream */
    private int datStream; /* Data Stream  */

    //////////////////////////////////////////
    // file information block
    //////////////////////////////////////////

    private static class Fib {
        int lid;
        int flags;
        int fcMin, fcMac, cbMac;
        int ccpText, ccpFtn, ccpHdd, ccpMcr, ccpAtn;
        int ccpEdn, ccpTxbx, ccpHdrTxbx;
        int fcClx, cbClx;
        int fcStshf, cbStshf;
        int fcSttbfbkmk,  cbSttbfbkmk;
        int fcPlcfbkf, cbPlcfbkf;
        int fcPlcffndRef, cbPlcffndRef;
        int fcPlcffndTxt, cbPlcffndTxt;
        int fcPlcfendRef, cbPlcfendRef;
        int fcPlcfendTxt, cbPlcfendTxt;
        int fcPlcfandRef, cbPlcfandRef;
        int fcPlcfandTxt, cbPlcfandTxt;
        int fcPlcfbtePapx, cbPlcfbtePapx;
        int fcPlcfbteChpx, cbPlcfbteChpx;
        int pnChar, pnPara, pnFntb;
    }
    Fib fib = new Fib();
    int FKP_SIZE;

    private void read_fib() throws IOException {
        byte[] FIB = new byte[0x400];

        switch (version) {

            case DOS:
                FKP_SIZE = 128;
                docStream = tabStream = datStream = 0;
                super.read(0, FIB, 0x80);

                fib.fcMac = dword(FIB, 0xE);
                fib.pnPara = word(FIB, 0x12);
                fib.pnFntb = word(FIB, 0x14);
                fib.cbMac = word(FIB, 0x60);
                break;

            case WW2:
                FKP_SIZE = 512;
                docStream = tabStream = datStream = 0;
                super.read(0, FIB, 0x146);

                fib.lid = word(FIB, 0x06);
                fib.flags = word(FIB, 0x0A);
                fib.fcMin = dword(FIB, 0x18);
                fib.fcMac = dword(FIB, 0x1C);
                fib.cbMac = dword(FIB, 0x20);
                fib.ccpText = dword(FIB, 0x34);
                fib.ccpFtn = dword(FIB, 0x38);
                fib.ccpHdd = dword(FIB, 0x3C);
                fib.ccpMcr = dword(FIB, 0x40);
                fib.ccpAtn = dword(FIB, 0x44);
                fib.fcStshf = dword(FIB, 0x5E);
                fib.cbStshf = word (FIB, 0x62);
                fib.fcPlcffndRef = dword(FIB, 0x64);
                fib.cbPlcffndRef = word(FIB, 0x68);
                fib.fcPlcffndTxt = dword(FIB, 0x6A);
                fib.cbPlcffndTxt = word(FIB, 0x6E);
                fib.fcPlcfandRef = dword(FIB, 0x70);
                fib.cbPlcfandRef = word(FIB, 0x74);
                fib.fcPlcfandTxt = dword(FIB, 0x76);
                fib.cbPlcfandTxt = word(FIB, 0x7A);
                fib.fcPlcfbteChpx = dword(FIB, 0xA0);
                fib.cbPlcfbteChpx = word(FIB, 0xA4);
                fib.fcPlcfbtePapx = dword(FIB, 0xA6);
                fib.cbPlcfbtePapx = word(FIB, 0xAA);
                fib.fcSttbfbkmk = dword(FIB, 0xD6);
                fib.cbSttbfbkmk = word(FIB, 0xDA);
                fib.fcPlcfbkf = dword(FIB, 0xDC);
                fib.cbPlcfbkf = word(FIB, 0xE0);
                fib.fcClx = dword(FIB, 0x11E);
                fib.cbClx = word(FIB, 0x122);

                if (fib.cbClx == 0)
                    fib.cbClx = fib.cbMac - fib.fcClx;
                break;

            case WW6:
                FKP_SIZE = 512;
                super.read(docStream, FIB, 0x2AA);

                fib.lid = word(FIB, 0x06);
                fib.flags = word(FIB, 0x0A);
                fib.fcMin = dword(FIB, 0x18);
                fib.fcMac = dword(FIB, 0x1C);
                fib.ccpText = dword(FIB, 0x034);
                fib.ccpFtn = dword(FIB, 0x038);
                fib.ccpHdd = dword(FIB, 0x03C);
                fib.ccpMcr = dword(FIB, 0x040);
                fib.ccpAtn = dword(FIB, 0x044);
                fib.ccpEdn = dword(FIB, 0x048);
                fib.ccpTxbx = dword(FIB, 0x04C);
                fib.ccpHdrTxbx = dword(FIB, 0x050);
                fib.fcStshf = dword(FIB, 0x060);
                fib.cbStshf = dword(FIB, 0x064);
                fib.fcPlcffndRef = dword(FIB, 0x068);
                fib.cbPlcffndRef = dword(FIB, 0x06C);
                fib.fcPlcffndTxt = dword(FIB, 0x070);
                fib.cbPlcffndTxt = dword(FIB, 0x074);
                fib.fcPlcfandRef = dword(FIB, 0x078);
                fib.cbPlcfandRef = dword(FIB, 0x07C);
                fib.fcPlcfandTxt = dword(FIB, 0x080);
                fib.cbPlcfandTxt = dword(FIB, 0x084);
                fib.fcPlcfbteChpx = dword(FIB, 0x0B8);
                fib.cbPlcfbteChpx = dword(FIB, 0x0BC);
                fib.fcPlcfbtePapx = dword(FIB, 0x0C0);
                fib.cbPlcfbtePapx = dword(FIB, 0x0C4);
                fib.fcSttbfbkmk = dword(FIB, 0x100);
                fib.cbSttbfbkmk = dword(FIB, 0x104);
                fib.fcPlcfbkf = dword(FIB, 0x108);
                fib.cbPlcfbkf = dword(FIB, 0x10C);
                fib.fcClx = dword(FIB, 0x160);
                fib.cbClx = dword(FIB, 0x164);
                fib.fcPlcfendRef = dword(FIB, 0x1D2);
                fib.cbPlcfendRef = dword(FIB, 0x1D6);
                fib.fcPlcfendTxt = dword(FIB, 0x1DA);
                fib.cbPlcfendTxt = dword(FIB, 0x1DE);

                if ((fib.flags & 0x0100) != 0)
                    throw new IOException(); // encrypted, not implemented
                tabStream = datStream = docStream;
                break;

            case WW8:
                FKP_SIZE = 512;
                super.read(docStream, FIB, 0x382);

                fib.lid = word(FIB, 0x06);
                fib.flags = word(FIB, 0x0A);
                fib.fcMin = dword(FIB, 0x18);
                fib.fcMac = dword(FIB, 0x1C);
                fib.ccpText = dword(FIB, 0x04C);
                fib.ccpFtn = dword(FIB, 0x050);
                fib.ccpHdd = dword(FIB, 0x054);
                fib.ccpMcr = dword(FIB, 0x058);
                fib.ccpAtn = dword(FIB, 0x05C);
                fib.ccpEdn = dword(FIB, 0x060);
                fib.ccpTxbx = dword(FIB, 0x064);
                fib.ccpHdrTxbx = dword(FIB, 0x068);
                fib.fcStshf = dword(FIB, 0x0A2);
                fib.cbStshf = dword(FIB, 0x0A6);
                fib.fcPlcffndRef = dword(FIB, 0x0AA);
                fib.cbPlcffndRef = dword(FIB, 0x0AE);
                fib.fcPlcffndTxt = dword(FIB, 0x0B2);
                fib.cbPlcffndTxt = dword(FIB, 0x0B6);
                fib.fcPlcfandRef = dword(FIB, 0x0BA);
                fib.cbPlcfandRef = dword(FIB, 0x0BE);
                fib.fcPlcfandTxt = dword(FIB, 0x0C2);
                fib.cbPlcfandTxt = dword(FIB, 0x0C6);
                fib.fcPlcfbteChpx = dword(FIB, 0x0FA);
                fib.cbPlcfbteChpx = dword(FIB, 0x0FE);
                fib.fcPlcfbtePapx = dword(FIB, 0x102);
                fib.cbPlcfbtePapx = dword(FIB, 0x106);
                fib.fcSttbfbkmk = dword(FIB, 0x142);
                fib.cbSttbfbkmk = dword(FIB, 0x146);
                fib.fcPlcfbkf = dword(FIB, 0x14A);
                fib.cbPlcfbkf = dword(FIB, 0x14E);
                fib.fcClx = dword(FIB, 0x1A2);
                fib.cbClx = dword(FIB, 0x1A6);
                fib.fcPlcfendRef = dword(FIB, 0x20A);
                fib.cbPlcfendRef = dword(FIB, 0x20E);
                fib.fcPlcfendTxt = dword(FIB, 0x212);
                fib.cbPlcfendTxt = dword(FIB, 0x216);

                if ((fib.flags & 0x0100) != 0)
                    throw new IOException(); // encrypted file, not implemented
                tabStream = super.stream((fib.flags & 0x0200) != 0 ? "1Table" : "0Table");
                if (tabStream < 0)
                    throw new IOException();
                datStream = super.stream("Data");
                break;

            default:
                throw new IOException();
        }
    }

    //////////////////////////////////////////
    // piece table
    //////////////////////////////////////////

    private static class Piece {
        int charPos;
        int filePos;
        int codepage;
        char prm;
        public Piece(int charPos, int filePos, int codepage, char prm) {
            this.charPos = charPos;
            this.filePos = filePos;
            this.codepage = codepage;
            this.prm = prm;
        }
    }
    private ArrayList<Piece> piece;
    int nPieces;
    byte[] CLX;

    private void readPiece() throws IOException {
        if ((version == Version.WW8) || ((fib.flags & 4) != 0)) {
            CLX = new byte[fib.cbClx];
            super.add(tabStream+fib.fcClx, CLX, 0, fib.cbClx, 0);
        }
    }

    private void makePiece() throws IOException {

        if (version == Version.DOS) {
            int cp = fib.cbMac == 0 ? 866 : 1251;
            fib.ccpText = fib.fcMac - 0x80;
            piece = new ArrayList<Piece>(3);
            piece.add(new Piece(0, Integer.MAX_VALUE, cp, (char) 0));
            piece.add(new Piece(fib.ccpText, 0x80, cp, (char) 0));
            piece.add(new Piece(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, (char) 0));
            nPieces = 1;
            size = fib.ccpText;
            return;
        }

        if (CLX == null) {
            int cp = isUnicode() ? 1200 : 1251;
            piece = new ArrayList<Piece>(3);
            piece.add(new Piece(0, Integer.MAX_VALUE, cp, (char) 0));
            piece.add(new Piece(fib.fcMac - fib.fcMin, fib.fcMin, cp, (char) 0));
            piece.add(new Piece(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, (char) 0));
            nPieces = 1;

        } else {
            int cp = isUnicode() ? 1200 : 1251;
            int addr = 0;
            boolean keep_clx = false;
            byte tag = CLX[addr++];
            while (tag == 1) {
                keep_clx = true;
                addr += 2 + word(CLX, addr);
                tag = CLX[addr++];
            }
            if (tag != 2)
                throw new IOException();
            if (version == Version.WW2) {
                nPieces = word(CLX, addr);
                addr += 2;
            } else {
                nPieces = dword(CLX, addr);
                addr += 4;
            }
            nPieces = (nPieces-4) / (4+8);
            piece = new ArrayList<Piece>(nPieces + 2);
            if (dword(CLX, addr) != 0)
                throw new IOException();
            addr += 4;
            piece.add(new Piece(0, Integer.MAX_VALUE, cp, (char) 0));
            for (int i = 1; i <= nPieces; i++, addr += 4)
                piece.add(new Piece(dword(CLX, addr), 0, 0, (char) 0));
            piece.add(new Piece(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, (char) 0));
            for (int i = 1; i <= nPieces; i++, addr += 8) {
                Piece p = piece.get(i);
                if (p.charPos <= piece.get(i-1).charPos)
                    throw new IOException();
                int dtemp = dword(CLX, addr + 2);
                if ((dtemp & (1<<30)) == 0) {
                    p.codepage = cp;
                } else {
                    p.codepage = 1252;
                    dtemp &= ~(1<<30);
                    dtemp >>= 1;
                }
                p.filePos = dtemp;
                p.prm = word(CLX, addr + 6);
            }
            if (!keep_clx)
                CLX = null;
        }

        if (piece.get(nPieces).charPos < fib.ccpText)
            throw new IOException();
        if (isUnicode()) {
            size = (fib.ccpText+fib.ccpFtn+fib.ccpHdd+fib.ccpMcr+
                    fib.ccpAtn+fib.ccpEdn+fib.ccpTxbx+fib.ccpHdrTxbx)*2;
        } else {
            size = (fib.ccpText+fib.ccpFtn+fib.ccpHdd+fib.ccpMcr+
                    fib.ccpAtn+fib.ccpEdn+fib.ccpTxbx+fib.ccpHdrTxbx);
            piece.get(0).codepage = 1251;
        }
    }

    private int findPiece(int cp) {
        int np = 1;
        while (piece.get(np).charPos <= cp)
            np++;
        return np;
    }

    //////////////////////////////////////////
    // bin tables & formatted disk pages
    //////////////////////////////////////////

    private static class FkPage {
        int pageNum;
        SoftReference<byte[]> buf;
        public FkPage(int pageNum) {
            this.pageNum = pageNum;
            this.buf = new SoftReference<byte[]>(null);
        }
    }
    private TreeMap<Integer, FkPage> binTabPara = new TreeMap<Integer, FkPage>();
    private TreeMap<Integer, FkPage> binTabChar = new TreeMap<Integer, FkPage>();
    private byte[] plcfbtePapx; private int nBinsPara;
    private byte[] plcfbteChpx; private int nBinsChar;

    private void readBinTab() throws IOException {
        switch (version) {

            case DOS:
                fib.pnChar = (fib.fcMac + 127) / 128;
                if (fib.pnChar == 0) fib.pnChar = 1;
                if (fib.pnChar < fib.pnPara) {
                    nBinsChar = fib.pnPara - fib.pnChar;
                    plcfbteChpx = new byte[nBinsChar * 4];
                    for (int i = 1; i < nBinsChar; i++)
                        super.add((fib.pnChar + i) * 128, plcfbteChpx, i * 4, 4, 0);
                }
                if (fib.pnPara < fib.pnFntb) {
                    nBinsPara = fib.pnFntb - fib.pnPara;
                    plcfbtePapx = new byte[nBinsPara * 4];
                    for (int i = 1; i < nBinsPara; i++)
                        super.add((fib.pnPara + i) * 128, plcfbtePapx, i * 4, 4, 0);
                }
                break;

            case WW2:
            case WW6:
            case WW8:
                if (fib.cbPlcfbteChpx > 0) {
                    plcfbteChpx = new byte[fib.cbPlcfbteChpx];
                    super.add(tabStream+fib.fcPlcfbteChpx, plcfbteChpx, 0, fib.cbPlcfbteChpx, 0);
                }
                if (fib.cbPlcfbtePapx > 0) {
                    plcfbtePapx = new byte[fib.cbPlcfbtePapx];
                    super.add(tabStream+fib.fcPlcfbtePapx, plcfbtePapx, 0, fib.cbPlcfbtePapx, 0);
                }
                break;
        }
    }

    private void makeBinTab() throws IOException {
        switch (version) {

            case DOS:
                if (fib.pnChar < fib.pnPara) {
                    binTabChar.put(0, new FkPage(fib.pnChar));
                    for (int i = 1; i < nBinsChar; i++) {
                        int filePos = dword(plcfbteChpx, i * 4);
                        binTabChar.put(filePos, new FkPage(fib.pnChar + i));
                    }
                }
                if (fib.pnPara < fib.pnFntb) {
                    binTabPara.put(0, new FkPage(fib.pnPara));
                    for (int i = 1; i < nBinsPara; i++) {
                        int filePos = dword(plcfbtePapx, i * 4);
                        binTabPara.put(filePos, new FkPage(fib.pnPara + i));
                    }
                }
                break;

            case WW2:
                if (fib.cbPlcfbteChpx == 10 && fib.cbPlcfbtePapx == 10) {
                    int pnChar = word(plcfbteChpx, 8);
                    int pnPara = word(plcfbtePapx, 8);
                    int pnFntb = (fib.fcStshf + 511) / 512;
                    if (pnChar + 1 != pnPara || pnPara + 1 != pnFntb) {
                        nBinsChar = pnPara - pnChar;
                        plcfbteChpx = new byte[nBinsChar * 4];
                        for (int i = 1; i < nBinsChar; i++)
                            super.add((pnChar + i) * 512, plcfbteChpx, i * 4, 4, 0);
                        nBinsPara = pnFntb - pnPara;
                        plcfbtePapx = new byte[nBinsPara * 4];
                        for (int i = 1; i < nBinsPara; i++)
                            super.add((pnPara + i) * 512, plcfbtePapx, i * 4, 4, 0);
                        super.kick();
                        binTabChar.put(0, new FkPage(pnChar));
                        for (int i = 1; i < nBinsChar; i++) {
                            int filePos = dword(plcfbteChpx, i * 4);
                            binTabChar.put(filePos, new FkPage(pnChar + i));
                        }
                        binTabPara.put(0, new FkPage(pnPara));
                        for (int i = 1; i < nBinsPara; i++) {
                            int filePos = dword(plcfbtePapx, i * 4);
                            binTabPara.put(filePos, new FkPage(pnPara + i));
                        }
                        break;
                    }
                }
                //no break

            case WW6:
                if (fib.cbPlcfbteChpx > 0) {
                    nBinsChar = (fib.cbPlcfbteChpx - 4) / (4 + 2);
                    int fp = 0;
                    int pn = (nBinsChar + 1) * 4;
                    for (int i = 0; i < nBinsChar; i++, fp += 4, pn += 2) {
                        int filePos = (i == 0) ? 0 : dword(plcfbteChpx, fp);
                        int pageNum = word(plcfbteChpx, pn);
                        binTabChar.put(filePos, new FkPage(pageNum));
                    }
                }
                if (fib.cbPlcfbtePapx > 0) {
                    nBinsPara = (fib.cbPlcfbtePapx - 4) / (4 + 2);
                    int fp = 0;
                    int pn = (nBinsPara + 1) * 4;
                    for (int i = 0; i < nBinsPara; i++, fp += 4, pn += 2) {
                        int filePos = (i == 0) ? 0 : dword(plcfbtePapx, fp);
                        int pageNum = word(plcfbtePapx, pn);
                        binTabPara.put(filePos, new FkPage(pageNum));
                    }
                }
                break;

            case WW8:
                if (fib.cbPlcfbteChpx > 0) {
                    nBinsChar = (fib.cbPlcfbteChpx - 4) / (4 + 4);
                    int fp = 0;
                    int pn = (nBinsChar + 1) * 4;
                    for (int i = 0; i < nBinsChar; i++, fp += 4, pn += 4) {
                        int filePos = (i == 0) ? 0 : dword(plcfbteChpx, fp);
                        int pageNum = dword(plcfbteChpx, pn);
                        binTabChar.put(filePos, new FkPage(pageNum));
                    }
                }
                if (fib.cbPlcfbtePapx > 0) {
                    nBinsPara = (fib.cbPlcfbtePapx - 4) / (4 + 4);
                    int fp = 0;
                    int pn = (nBinsPara + 1) * 4;
                    for (int i = 0; i < nBinsPara; i++, fp += 4, pn += 4) {
                        int filePos = (i == 0) ? 0 : dword(plcfbtePapx, fp);
                        int pageNum = dword(plcfbtePapx, pn);
                        binTabPara.put(filePos, new FkPage(pageNum));
                    }
                }
                break;
        }

        plcfbtePapx = null;
        plcfbteChpx = null;
    }

    private byte[] loadFkp(TreeMap<Integer, FkPage> tab, int filePos)
            throws IOException {
        filePos &= 0x3FFFFFFF;
        FkPage fkp = tab.floorEntry(filePos).getValue();
        byte[] buf = fkp.buf.get();
        if (buf == null) {
            buf = new byte[FKP_SIZE];
            super.read(docStream + fkp.pageNum * FKP_SIZE, buf, FKP_SIZE);
            fkp.buf = new SoftReference<byte[]>(buf);
        }
        return buf;
    }

    public static final class Prm {
        public int start;
        public int limit;
        public byte[] buf;
        public int pos;

        public Prm(int start, int limit, byte[] buf, int pos) {
            this.start = start;
            this.limit = limit;
            this.buf = buf;
            this.pos = pos;
        }
    }

    private Prm getCharPrm(int filePos) throws IOException {

        byte[] fkp = loadFkp(binTabChar, filePos);
        int crun = fkp[FKP_SIZE - 1];
        int start, limit, idx;

        switch (version) {

            case DOS:
                start = dword(fkp, 0);
                for (int i = 0; i < crun; i++) {
                    limit = dword(fkp, 4 + (i * 6));
                    if (filePos < limit) {
                        idx = word(fkp, 8 + (i * 6));
                        if (idx > 0 && idx < 0x7C)
                            return new Prm(start, limit, fkp, idx + 4);
                        return new Prm(start, limit, null, 0);
                    }
                    start = limit;
                }
                break;

            default:
                for (int i = 0; i < crun; i++) {
                    limit = dword(fkp, (i * 4) + 4);
                    if (filePos < limit) {
                        start = dword(fkp, (i * 4));
                        idx = fkp[(crun * 4) + 4 + i] & 255;
                        if (idx > 0)
                            return new Prm(start, limit, fkp, idx << 1);
                        return new Prm(start, limit, null, 0);
                    }
                }
                break;
        }

        throw new IOException();
    }

    private Prm getParaPrm(int filePos) throws IOException {

        byte[] fkp = loadFkp(binTabPara, filePos);
        int crun = fkp[FKP_SIZE - 1];
        int start, limit, idx;

        switch (version) {

            case DOS:
                start = dword(fkp, 0);
                for (int i = 0; i < crun; i++) {
                    limit = dword(fkp, 4 + (i * 6));
                    if (filePos < limit) {
                        idx = word(fkp, 8 + (i * 6));
                        if (idx > 0 && idx < 0x7C)
                            return new Prm(start, limit, fkp, idx + 4);
                        return new Prm(start, limit, null, 0);
                    }
                    start = limit;
                }
                break;

            case WW2:
                for (int i = 0; i < crun; i++) {
                    limit = dword(fkp, 4 + (i * 4));
                    if (filePos < limit) {
                        start = dword(fkp, (i * 4));
                        idx = fkp[(crun * 4) + 4 + i] & 255;
                        if (idx > 0)
                            return new Prm(start, limit, fkp, idx << 1);
                        return new Prm(start, limit, null, 0);
                    }
                }
                break;

            case WW6:
                for (int i = 0; i < crun; i++) {
                    limit = dword(fkp, 4 + (i * 4));
                    if (filePos < limit) {
                        start = dword(fkp, (i * 4));
                        idx = fkp[(crun * 4) + 4 + (i * 7)] & 255;
                        if (idx > 0)
                            return new Prm(start, limit, fkp, idx << 1);
                        return new Prm(start, limit, null, 0);
                    }
                }
                break;

            case WW8:
                for (int i = 0; i < crun; i++) {
                    limit = dword(fkp, 4 + (i * 4));
                    if (filePos < limit) {
                        start = dword(fkp, (i * 4));
                        idx = fkp[(crun * 4) + 4 + (i * 13)] & 255;
                        if (idx > 0)
                            return new Prm(start, limit, fkp, idx << 1);
                        return new Prm(start, limit, null, 0);
                    }
                }
                break;
        }

        throw new IOException();
    }

    //////////////////////////////////////////
    // links & notes
    //////////////////////////////////////////

    byte[] FNREF;
    byte[] FNTXT;
    byte[] NAMES;
    public HashMap<String, Integer> bookmarks = new HashMap<String, Integer>(256);

    int nNotes;
    private static class Note implements Comparable<Note> {
        int charPos;
        int index;
        int type;
        public Note(int charPos, int index, int type) {
            this.charPos = charPos;
            this.index = index;
            this.type = type;
        }
        public int compareTo(Note that) {
            return this.charPos - that.charPos;
        }
    }

    private ArrayList<Note> notes;
    int lastNote;

    private void readHref() throws IOException {

        if (fib.cbPlcffndRef > 0 || fib.cbPlcfendRef > 0 || fib.cbPlcfandRef > 0) {
            FNREF = new byte[fib.cbPlcffndRef + fib.cbPlcfendRef + fib.cbPlcfandRef];
            if (fib.cbPlcffndRef > 0)
                super.add(tabStream+fib.fcPlcffndRef, FNREF, 0, fib.cbPlcffndRef, 0);
            if (fib.cbPlcfendRef > 0)
                super.add(tabStream+fib.fcPlcfendRef, FNREF, fib.cbPlcffndRef, fib.cbPlcfendRef, 0);
            if (fib.cbPlcfandRef > 0)
                super.add(tabStream+fib.fcPlcfandRef, FNREF, fib.cbPlcffndRef+fib.cbPlcfendRef, fib.cbPlcfandRef, 0);
        }

        if (fib.cbPlcffndTxt > 0 || fib.cbPlcfendTxt > 0 || fib.cbPlcfandTxt > 0) {
            FNTXT = new byte[fib.cbPlcffndTxt + fib.cbPlcfendTxt + fib.cbPlcfandTxt];
            if (fib.cbPlcffndTxt > 0)
                super.add(tabStream+fib.fcPlcffndTxt, FNTXT, 0, fib.cbPlcffndTxt, 0);
            if (fib.cbPlcfendTxt > 0)
                super.add(tabStream+fib.fcPlcfendTxt, FNTXT, fib.cbPlcffndTxt, fib.cbPlcfendTxt, 0);
            if (fib.cbPlcfandTxt > 0)
                super.add(tabStream+fib.fcPlcfandTxt, FNTXT, fib.cbPlcffndTxt+fib.cbPlcfendTxt, fib.cbPlcfandTxt, 0);
        }

        if (fib.cbSttbfbkmk > 0 || fib.cbPlcfbkf > 0) {
            NAMES = new byte[fib.cbSttbfbkmk + fib.cbPlcfbkf];
            super.add(tabStream+fib.fcPlcfbkf, NAMES, 0, fib.cbPlcfbkf, 0);
            super.add(tabStream+fib.fcSttbfbkmk, NAMES, fib.cbPlcfbkf, fib.cbSttbfbkmk, 0);
        }
    }

    private void makeHref() throws IOException {

        if (NAMES != null) {
            int max = (fib.cbPlcfbkf-4)/(4+4);
            int pos = 0;
            int str = fib.cbPlcfbkf;
            boolean uni = false;
            char[] tabl = null;

            if (version == Version.WW8) {
                char wtmp = word(NAMES, str);
                str += 2;
                if (wtmp == 0xFFFF) {
                    str += 2;
                    uni = true;
                }
                str += 2;
            } else {
                str += 2;
            }

            if (!uni)
                tabl = AlUnicode.getDataCP(getCodePage());
            StringBuilder name = new StringBuilder();

            try {
                for (int i = 0; i < max; i++) {
                    int value = dword(NAMES, pos);
                    pos += 4;

                    name.setLength(0);
                    if (!uni) {
                        byte len = NAMES[str++];
                        while (len-- > 0) {
                            char c = (char) (NAMES[str++] & 0xff);
                            if (c >= 128) c = tabl[c - 128];
                            name.append(c);
                        }
                    } else {
                        char len = word(NAMES, str);
                        str += 2;
                        while (len-- > 0) {
                            char c = word(NAMES, str);
                            str += 2;
                            name.append(c);
                        }
                    }

                    bookmarks.put(name.toString(), value);
                }
            } catch(IndexOutOfBoundsException e) {

            }
        }

        nNotes = 0;
        if (fib.cbPlcffndRef > 0) nNotes += (fib.cbPlcffndRef-4)/(4+2);
        if (fib.cbPlcfendRef > 0) nNotes += (fib.cbPlcfendRef-4)/(4+2);
        if (fib.cbPlcfandRef > 0) nNotes += (fib.cbPlcfandRef-4)/(4+30);
        if (fib.cbPlcffndTxt > 0) nNotes += (fib.cbPlcffndTxt-4)/(4);
        if (fib.cbPlcfendTxt > 0) nNotes += (fib.cbPlcfendTxt-4)/(4);
        if (fib.cbPlcfandTxt > 0) nNotes += (fib.cbPlcfandTxt-4)/(4);
        if (nNotes > 0) {
            notes = new ArrayList<Note>(nNotes + 2);
            notes.add(new Note(0, 0, 0));
        }

        if (fib.cbPlcffndRef > 0) {
            int max = (fib.cbPlcffndRef-4)/(4+2);
            int fc = 0;
            for (int i=0; i<max; i++) {
                int charPos = dword(FNREF, fc);
                notes.add(new Note(charPos, i + 1, Format.FOOTREF));
                fc += 4;
            }
        }
        if (fib.cbPlcfendRef > 0) {
            int max = (fib.cbPlcfendRef-4)/(4+2);
            int fc = fib.cbPlcffndRef;
            for (int i=0; i<max; i++) {
                int charPos = dword(FNREF, fc);
                notes.add(new Note(charPos, i + 1, Format.ENDREF));
                fc += 4;
            }
        }
        if (fib.cbPlcfandRef > 0) {
            int max = (fib.cbPlcfandRef-4)/(4+30);
            int fc = fib.cbPlcffndRef + fib.cbPlcfendRef;
            for (int i=0; i<max; i++) {
                int charPos = dword(FNREF, fc);
                notes.add(new Note(charPos, i + 1, Format.ANNREF));
                fc += 4;
            }
        }
        if (fib.cbPlcffndTxt > 0) {
            int max = (fib.cbPlcffndTxt-4)/(4);
            int fc = 0;
            for (int i=0; i<max; i++) {
                int charPos = dword(FNTXT, fc) + fib.ccpText;
                notes.add(new Note(charPos, i + 1, Format.FOOTTEXT));
                fc += 4;
            }
        }
        if (fib.cbPlcfendTxt > 0) {
            int max = (fib.cbPlcfendTxt-4)/(4);
            int fc = fib.cbPlcffndTxt;
            for (int i=0; i<max; i++) {
                int charPos = dword(FNTXT, fc)
                        + fib.ccpText + fib.ccpFtn + fib.ccpHdd + fib.ccpAtn;
                notes.add(new Note(charPos, i + 1, Format.ENDTEXT));
                fc += 4;
            }
        }
        if (fib.cbPlcfandTxt > 0) {
            int max = (fib.cbPlcfandTxt-4)/(4);
            int fc = fib.cbPlcffndTxt + fib.cbPlcfendTxt;
            for (int i=0; i<max; i++) {
                int charPos = dword(FNTXT, fc)
                        + fib.ccpText + fib.ccpFtn + fib.ccpHdd;
                notes.add(new Note(charPos, i + 1, Format.ANNTEXT));
                fc += 4;
            }
        }

        if (nNotes > 0) {
            Collections.sort(notes);
            notes.add(new Note(0x1FFFFFFF, 0, 0));
            lastNote = 0;
        }

        FNREF = null;
        FNTXT = null;
        NAMES = null;
    }

    private Note findNote(int cp) {
        if (notes == null)
            return new Note(0, 0, 0);
        if (cp >= notes.get(lastNote).charPos && cp < notes.get(lastNote+1).charPos)
            return notes.get(lastNote);
        for (lastNote = 0; cp >= notes.get(lastNote+1).charPos; lastNote++)
            continue;
        return notes.get(lastNote);
    }

    //////////////////////////////////////////
    // stylesheet
    //////////////////////////////////////////

    byte[] STSH;
    int stdbase;
    int nStyles;
    private static class Style {
        int and;
        int xor;
    }
    private ArrayList<Style> styles;

    private void readStyles() throws IOException {
        if (fib.cbStshf > 0) {
            STSH = new byte[fib.cbStshf];
            super.add(tabStream+fib.fcStshf, STSH, 0, fib.cbStshf, 0);
        }
    }

    private void applyStylePrm(int istd) {
        if (STSH == null) return;
        if (version == Version.WW2) {
            int stcp = (istd + stdbase) & 255;
            int name = 4;
            int chpx = name + word(STSH, name - 2);
            int papx = chpx + word(STSH, chpx - 2);
            int base = papx + word(STSH, papx - 2);
            for (int i = 0; i < stcp; i++) {
                if (STSH[name] == -1) name++;
                else name += STSH[name] + 1;
            }
            if (STSH[name] == -1) return;
            char based = (char) (STSH[base + (stcp * 2) + 1] & 255);
            if (based != 222)
                applyStylePrm(based);
            if (istd >= 246 && istd <= 254)
                format.level(255 - istd);
            for (int i = 0; i < stcp; i++) {
                if (STSH[papx] == -1) papx++;
                else papx += STSH[papx] + 1;
            }
            if (STSH[papx] != -1)
                applyGrpPrl(STSH, papx + 8, STSH[papx - 7]);
            for (int i = 0; i < stcp; i++) {
                if (STSH[chpx] == -1) chpx++;
                else chpx += STSH[chpx] + 1;
            }
            if (STSH[chpx] != -1)
                applyCharPrm(STSH, chpx);
        } else {
            int pos = word(STSH, 0) + 2;
            for (int i = 0; i < istd; i++)
                pos += word(STSH, pos) + 2;
            if (word(STSH, pos) == 0) return;
            int sgc = STSH[pos + 4] & 0xF;
            char based = (char) (word(STSH, pos + 4) >> 4);
            if (based != 4095)
                applyStylePrm(based);
            pos += 2 + stdbase;
            int name_len;
            switch (version) {
                case WW8:
                    name_len = word(STSH, pos);
                    pos += 4 + name_len * 2;
                    break;
                case WW6:
                    name_len = STSH[pos];
                    pos += 2 + name_len;
                    break;
                default:
                    break;
            }
            if (istd >= 1 && istd <= 9)
                format.level(istd);
            if ((pos & 1) != 0) pos++;
            if (sgc == 1) {
                int len = word(STSH, pos);
                applyGrpPrl(STSH, pos + 4, len - 2);
                pos += 2 + len;
            }
            if ((pos & 1) != 0) pos++;
            if (sgc == 1 || sgc == 2) {
                int len = word(STSH, pos);
                applyGrpPrl(STSH, pos + 2, len);
                pos += 2 + len;
            }
        }
        style.value = format.value;
    }

    private void makeStyles() throws IOException {
        if (STSH == null) return;
        switch (version) {
            case WW2:
                stdbase = word(STSH, 0);
                int pos = 2;
                for (int i = 0; i < 3; i++)
                    pos += word(STSH, pos);
                nStyles = word(STSH, pos);
                styles = new ArrayList<Style>(256);
                for (int i = 0; i < 256; i++)
                    styles.add(new Style());
                break;
            case WW8:
            case WW6:
                stdbase = word(STSH, 4);
                nStyles = word(STSH, 2);
                styles = new ArrayList<Style>(nStyles);
                for (int i = 0; i < nStyles; i++)
                    styles.add(new Style());
                break;
            default:
                break;
        }
        for (int i = 0; i < nStyles; i++) {
            int istd;
            if (version == Version.WW2)
                istd = (i - stdbase) & 0xff;
            else istd = i & 0xffff;
            format.value = style.value = 0;
            applyStylePrm(istd);
            int result0 = format.value;
            format.value = style.value = -1;
            applyStylePrm(istd);
            int result1 = format.value ^ -1;
            Style mask = styles.get(istd);
            mask.and = ~ (result0 ^ result1);
            mask.xor = result0;
        }
        format.value = style.value = 0;
        STSH = null;
    }

    private void applyStyle(int istd) {
        try {
            Style mask = styles.get(istd);
            format.value &= mask.and;
            format.value ^= mask.xor;
            style.value = format.value;
        } catch(IndexOutOfBoundsException e) {
        }
    }

    //////////////////////////////////////////
    // property modifier
    //////////////////////////////////////////

    //private static final int sprmPIstd     		= 0x4600;
    //private static final int sprmPIstdPermute    	= 0xC601;
    //private static final int sprmPIncLvl         	= 0x2602;
    //private static final int sprmPJc             	= 0x2403;
    //private static final int sprmPFSideBySide    	= 0x2404;
    //private static final int sprmPFKeep          	= 0x2405;
    //private static final int sprmPFKeepFollow     = 0x2406;
    //private static final int sprmPPageBreakBefore	= 0x2407;
    //private static final int sprmPBrcl           	= 0x2408;
    //private static final int sprmPBrcp           	= 0x2409;
    //private static final int sprmPAnld           	= 0xC63E;
    //private static final int sprmPNLvlAnm        	= 0x25FF;
    //private static final int sprmPFNoLineNumb    	= 0x240C;
    //private static final int ?sprmPChgTabsPapx   	= 0xC60D;
    //private static final int sprmPDxaRight        = 0x840E;
    //private static final int sprmPDxaLeft        	= 0x840F;
    //private static final int sprmPNest           	= 0x4610;
    //private static final int sprmPDxaLeft1       	= 0x8411;
    //private static final int sprmPDyaLine        	= 0x6412;
    //private static final int sprmPDyaBefore      	= 0xA413;
    //private static final int sprmPDyaAfter       	= 0xA414;
    //private static final int ?sprmPChgTabs       	= 0xC615;
    //private static final int sprmPFInTable        = 0x2416;
    //private static final int sprmPTtp            	= 0x2417;
    //private static final int sprmPDxaAbs         	= 0x8418;
    //private static final int sprmPDyaAbs         	= 0x8419;
    //private static final int sprmPDxaWidth       	= 0x841A;
    //private static final int sprmPPc             	= 0x261B;
    //private static final int sprmPBrcTop10       	= 0x461C;
    //private static final int sprmPBrcLeft10      	= 0x461D;
    //private static final int sprmPBrcBottom10     = 0x461E;
    //private static final int sprmPBrcRight10     	= 0x461F;
    //private static final int sprmPBrcBetween10   	= 0x4620;
    //private static final int sprmPBrcBar10       	= 0x4621;
    //private static final int sprmPFromText10     	= 0x4622;
    //private static final int sprmPWr             	= 0x2423;
    //private static final int sprmPBrcTop         	= 0x4424;
    //private static final int sprmPBrcLeft        	= 0x4425;
    //private static final int sprmPBrcBottom       = 0x4426;
    //private static final int sprmPBrcRight       	= 0x4427;
    //private static final int sprmPBrcBetween     	= 0x4428;
    //private static final int sprmPBrcBar         	= 0x4629;
    //private static final int sprmPFNoAutoHyph    	= 0x242A;
    //private static final int sprmPWHeightAbs     	= 0x442B;
    //private static final int sprmPDcs            	= 0x442C;
    //private static final int sprmPShd            	= 0x442D;
    //private static final int sprmPDyaFromText     = 0x842E;
    //private static final int sprmPDxaFromText    	= 0x842F;
    //private static final int sprmPFLocked        	= 0x2430;
    //private static final int sprmPFWidowControl  	= 0x2431;
    //private static final int ?sprmPRuler         	= 0xC632;
    //private static final int ??53                	= 0x2433;
    //private static final int ??54                	= 0x2434;
    //private static final int ??55                	= 0x2435;
    //private static final int ??56                 = 0x2436;
    //private static final int ??57                	= 0x2437;
    //private static final int ??58                	= 0x2438;
    //private static final int ??61                	= 0x243B;
    //private static final int rtl bidi             = 0;
    //private static final int sprmCFStrikeRM      	= 0x0837;
    //private static final int sprmCFRMark         	= 0x0801;
    //private static final int sprmCFFldVanish     	= 0x0802;
    //private static final int sprmCPicLocation    	= 0xCA03;
    //private static final int sprmCIbstRMark      	= 0x4804;
    //private static final int sprmCDttmRMark      	= 0x6805;
    //private static final int sprmCFData          	= 0x0806;
    //private static final int sprmCRMReason        = 0x4807;
    //private static final int sprmCChse           	= 0xEA08;
    //private static final int sprmCSymbol         	= 0xCA09;
    private static final int sprmCFOle2          	= 0x080A;
    //private static final int ??77                	= 0x2A0C;
    //private static final int ??78                	= 0x0858;
    //private static final int ??79                	= 0x2859;
    private static final int sprmCIstd            	= 0x4A30;
    //private static final int sprmCIstdPermute    	= 0xCA31;
    //private static final int sprmCDefault        	= 0xCA32;
    private static final int sprmCPlain          	= 0x2A33;
    private static final int sprmCFBold          	= 0x0835;
    private static final int sprmCFItalic        	= 0x0836;
    //private static final int sprmCFStrike        	= 0x0837;
    //private static final int sprmCFOutline        = 0x0838;
    //private static final int sprmCFShadow        	= 0x0839;
    //private static final int sprmCFSmallCaps     	= 0x083A;
    //private static final int sprmCFCaps          	= 0x083B;
    private static final int sprmCFVanish        	= 0x083C;
    //private static final int sprmCFtc            	= 0x4A3D;
    private static final int sprmCKul            	= 0x2A3E;
    //private static final int sprmCSizePos        	= 0xEA3F;
    //private static final int sprmCDxaSpace        = 0x8840;
    //private static final int sprmCLid            	= 0x4A41;
    //private static final int sprmCIco            	= 0x2A42;
    //private static final int sprmCHps            	= 0x4A43;
    //private static final int sprmCHpsInc         	= 0x2A44;
    //private static final int sprmCHpsPos         	= 0x2845;
    //private static final int sprmCHpsPosAdj      	= 0x2A46;
    //private static final int ?sprmCMajority      	= 0xCA47;
    private static final int sprmCIss            	= 0x2A48;
    //private static final int sprmCHpsNew50       	= 0xCA49;
    //private static final int sprmCHpsInc1        	= 0xCA4A;
    //private static final int sprmCHpsKern        	= 0x484B;
    //private static final int sprmCMajority50     	= 0xCA4C;
    //private static final int sprmCHpsMul         	= 0x4A4D;
    //private static final int sprmCCondHyhen      	= 0x484E;
    //private static final int w7 font             	= 0x0000;
    //private static final int w7 CJK font          = 0x0000;
    //private static final int w7 rtl font         	= 0x0000;
    //private static final int w7 lid              	= 0x0000;
    //private static final int w7 rtl colour ?     	= 0x2A53;
    //private static final int                     	= 0x0854;
    //private static final int sprmCFSpec          	= 0x0855;
    private static final int sprmCFObj           	= 0x0856;
    //private static final int sprmPicBrcl         	= 0x2E00;
    //private static final int sprmPicScale         = 0xCE01;
    //private static final int sprmPicBrcTop       	= 0x4C02;
    //private static final int sprmPicBrcLeft      	= 0x4C03;
    //private static final int sprmPicBrcBottom    	= 0x4C04;
    //private static final int sprmPicBrcRight     	= 0x4C05;
    //private static final int sprmSScnsPgn        	= 0x3000;
    //private static final int sprmSiHeadingPgn    	= 0x3001;
    //private static final int sprmSOlstAnm        	= 0xD202;
    //private static final int sprmSDxaColWidth     = 0xF203;
    //private static final int sprmSDxaColSpacing  	= 0xF204;
    //private static final int sprmSFEvenlySpaced  	= 0x3005;
    //private static final int sprmSFProtected     	= 0x3006;
    //private static final int sprmSDmBinFirst     	= 0x5007;
    //private static final int sprmSDmBinOther     	= 0x5008;
    //private static final int sprmSBkc            	= 0x3009;
    //private static final int sprmSFTitlePage     	= 0x300A;
    //private static final int sprmSCcolumns        = 0x500B;
    //private static final int sprmSDxaColumns     	= 0x900C;
    //private static final int sprmSFAutoPgn       	= 0x300D;
    //private static final int sprmSNfcPgn         	= 0x300E;
    //private static final int sprmSDyaPgn         	= 0xB00F;
    //private static final int sprmSDxaPgn         	= 0xB010;
    //private static final int sprmSFPgnRestart    	= 0x3011;
    //private static final int sprmSFEndnote       	= 0x3012;
    //private static final int sprmSLnc             = 0x3013;
    //private static final int sprmSGprfIhdt       	= 0x3014;
    //private static final int sprmSNLnnMod        	= 0x500B;
    //private static final int sprmSDxaLnn         	= 0x9016;
    //private static final int sprmSDyaHdrTop      	= 0xB017;
    //private static final int sprmSDyaHdrBottom   	= 0xB018;
    //private static final int sprmSLBetween       	= 0x3019;
    //private static final int sprmSVjc            	= 0x301A;
    //private static final int sprmSLnnMin          = 0x501B;
    //private static final int sprmSPgnStart       	= 0x501C;
    //private static final int sprmSBOrientation   	= 0x301D;
    //private static final int ?SprmSBCustomize    	= 0x301E;
    //private static final int sprmSXaPage         	= 0xB01F;
    //private static final int sprmSYaPage         	= 0xB020;
    //private static final int sprmSDxaLeft        	= 0xB021;
    //private static final int sprmSDxaRight       	= 0xB022;
    //private static final int sprmSDyaTop          = 0x9023;
    //private static final int sprmSDyaBottom      	= 0x9024;
    //private static final int sprmSDzaGutter      	= 0xB025;
    //private static final int sprmSDMPaperReq     	= 0x5026;
    //private static final int sprmTJc             	= 0x5400;
    //private static final int sprmTDxaLeft        	= 0x9601;
    //private static final int sprmTDxaGapHalf      = 0x9602;
    //private static final int sprmTFCantSplit     	= 0x3403;
    //private static final int sprmTTableHeader    	= 0x3404;
    //private static final int sprmTTableBorders   	= 0xD605;
    //private static final int sprmTDefTable10     	= 0xD606;
    //private static final int sprmTDyaRowHeight   	= 0x9407;
    //private static final int ?sprmTDefTable      	= 0xD608;
    //private static final int ?sprmTDefTableShd   	= 0xD609;
    //private static final int sprmTTlp             = 0x740A;
    //private static final int sprmTSetBrc         	= 0xD620;
    //private static final int sprmTInsert         	= 0x7621;
    //private static final int sprmTDelete         	= 0x5622;
    //private static final int sprmTDxaCol         	= 0x7623;
    //private static final int sprmTMerge          	= 0x5624;
    //private static final int sprmTSplit          	= 0x5625;
    //private static final int sprmTSetBrc10       	= 0xD626;
    //private static final int sprmTSetShd          = 0x7627;

    private void applySprm(char code, byte[] param, int pos) {
        switch (code) {
            case 0: break;

            case 0x2403: //sprmPJc80
            case 0x2461: //sprmPJc
                switch (param[pos]) {
                    case 0:
                        format.align(Format.LEFT);
                        break;
                    case 1:
                        format.align(Format.CENTER);
                        break;
                    case 2:
                        format.align(Format.RIGHT);
                        break;
                    case 3:
                        format.align(Format.JUSTIFY);
                        break;
                }
                break;

            case 0x2640: //sprmPOutLvl
                //if (format.level() != 0) {
                    if (param[pos] < 9) {
                        format.level(param[pos] + 1);
                    } else {
                        format.level(0);
                    }
                //}
                break;

            case sprmCIstd:
                applyStyle(word(param, pos));
                break;

            case 0x2A32: //sprmCDefault
                format.bold  (false);
                format.italic(false);
                format.strike(false);
                format.under (false);
                break;

            case sprmCPlain:
                format.bold  (style.bold());
                format.italic(style.italic());
                format.strike(style.strike());
                format.under (style.under());
                format.sub   (style.sub());
                format.sup   (style.sup());
                break;

            case sprmCFBold:
                switch (param[pos]) {
                    case 1:
                        format.bold(true);
                        break;
                    case 0:
                        format.bold(false);
                        break;
                    case Byte.MIN_VALUE:
                        format.bold(style.bold());
                        break;
                    case Byte.MIN_VALUE + 1:
                        format.bold(!style.bold());
                        break;
                }
                break;

            case sprmCFItalic:
                switch (param[pos]) {
                    case 1:
                        format.italic(true);
                        break;
                    case 0:
                        format.italic(false);
                        break;
                    case Byte.MIN_VALUE:
                        format.italic(style.italic());
                        break;
                    case Byte.MIN_VALUE + 1:
                        format.italic(!style.italic());
                        break;
                }
                break;

            case 0x0837: //sprmCFStrike
                switch (param[pos]) {
                    case 1:
                        format.strike(true);
                        break;
                    case 0:
                        format.strike(false);
                        break;
                    case Byte.MIN_VALUE:
                        format.strike(style.strike());
                        break;
                    case Byte.MIN_VALUE + 1:
                        format.strike(!style.strike());
                        break;
                }
                break;

            case sprmCFVanish:
                switch (param[pos]) {
                    case 1:
                        format.hidden(true);
                        break;
                    case 0:
                        format.hidden(false);
                        break;
                    case Byte.MIN_VALUE:
                        format.hidden(style.hidden());
                        break;
                    case Byte.MIN_VALUE + 1:
                        format.hidden(!style.hidden());
                        break;
                }
                break;

            case sprmCKul:
                format.under(param[pos] != 0);
                break;

            case sprmCIss:
                switch (param[pos]) {
                    case 1:
                        format.sup(true);
                        break;
                    case 2:
                        format.sub(true);
                        break;
                }
                break;

            case 0x0855: //sprmCFSpec
                format.special(param[pos] != 0 ? Format.SPECIAL : Format.NORMAL);
                break;

            case 0xCA03: //sprmCPicLocation (word95)
                if (param[pos++] != 4) break;
                //no break!
            case 0x6A03: //sprmCPicLocation
                format.special(Format.SPECIAL);
                format.xdata = dword(param, pos);
                break;

            case 0x680E: //chp.fcObj
                format.xdata = dword(param, pos);
                break;

            case sprmCFObj:
                format.obj(param[pos] != 0);
                break;

            case sprmCFOle2:
                format.ole2(param[pos] != 0);
                break;

        }
    }

    private static final char[] sprmTable = new char[] {
	/*   0 */ 	0,
	/*	 1 */	0,
	/*	 2 */	0x4600,
	/*	 3 */	0xC601,
	/*	 4 */	0x2602,
	/*	 5 */	0x2403,
	/*	 6 */	0x2404,
	/*	 7 */	0x2405,
	/*   8 */ 	0x2406,
	/*	 9 */	0x2407,
	/*	10 */	0x2408,
	/*	11 */	0x2409,
	/*	12 */	0xC63E,
	/*	13 */	0x25FF,
	/*	14 */	0x240C,
	/*	15 */	0xC60D,
	/*  16 */ 	0x840E,
	/*	17 */	0x840F,
	/*	18 */	0x4610,
	/*	19 */	0x8411,
	/*	20 */	0x6412,
	/*	21 */	0xA413,
	/*	22 */	0xA414,
	/*	23 */	0xC615,
	/*  24 */ 	0x2416,
	/*	25 */	0x2417,
	/*	26 */	0x8418,
	/*	27 */	0x8419,
	/*	28 */	0x841A,
	/*	29 */	0x261B,
	/*	30 */	0x461C,
	/*	31 */	0x461D,
	/*  32 */ 	0x461E,
	/*	33 */	0x461F,
	/*	34 */	0x4620,
	/*	35 */	0x4621,
	/*	36 */	0x4622,
	/*	37 */	0x2423,
	/*	38 */	0x4424,
	/*	39 */	0x4425,
	/*  40 */ 	0x4426,
	/*	41 */	0x4427,
	/*	42 */	0x4428,
	/*	43 */	0x4629,
	/*	44 */	0x242A,
	/*	45 */	0x442B,
	/*	46 */	0x442C,
	/*	47 */	0x442D,
	/*  48 */ 	0x842E,
	/*	49 */	0x842F,
	/*	50 */	0x2430,
	/*	51 */	0x2431,
	/*	52 */	0xC632,
	/*	53 */	0x2433,
	/*	54 */	0x2434,
	/*	55 */	0x2435,
	/*  56 */ 	0x2436,
	/*	57 */	0x2437,
	/*	58 */	0x2438,
	/*	59 */	0,
	/*	60 */	0,
	/*	61 */	0x243B,
	/*	62 */	0,
	/*	63 */	0,
	/*  64 */ 	0,
	/*	65 */	0x0837,
	/*	66 */	0x0801,
	/*	67 */	0x0802,
	/*	68 */	0xCA03,
	/*	69 */	0x4804,
	/*	70 */	0x6805,
	/*	71 */	0x0806,
	/*  72 */ 	0x4807,
	/*	73 */	0xEA08,
	/*	74 */	0xCA09,
	/*	75 */	sprmCFOle2,		//0x080A
	/*	76 */	0,
	/*	77 */	0x2A0C,
	/*	78 */	0x0858,
	/*	79 */	0x2859,
	/*  80 */ 	sprmCIstd,		//0x4A30,
	/*	81 */	0xCA31,
	/*	82 */	0xCA32,
	/*	83 */	sprmCPlain,		//0x2A33,
	/*	84 */	0,
	/*	85 */	sprmCFBold,		//0x0835,
	/*	86 */	sprmCFItalic,	//0x0836,
	/*	87 */	0x0837,
	/*  88 */ 	0x0838,
	/*	89 */	0x0839,
	/*	90 */	0x083A,
	/*	91 */	0x083B,
	/*	92 */	sprmCFVanish,	//0x083C,
	/*	93 */	0x4A3D,
	/*	94 */	sprmCKul, 		//0x2A3E,
	/*	95 */	0xEA3F,
	/*  96 */ 	0x8840,
	/*	97 */	0x4A41,
	/*	98 */	0x2A42,
	/*	99 */	0x4A43,
	/* 100 */	0x2A44,
	/* 101 */	0x2845,
	/* 102 */	0x2A46,
	/* 103 */	0xCA47,
	/* 104 */ 	sprmCIss,		//0x2A48,
	/* 105 */	0xCA49,
	/* 106 */	0xCA4A,
	/* 107 */	0x484B,
	/* 108 */	0xCA4C,
	/* 109 */	0x4A4D,
	/* 110 */	0x484E,
	/* 111 */	0,
	/* 112 */ 	0,
	/* 113 */	0,
	/* 114 */	0,
	/* 115 */	0x2A53,
	/* 116 */	0x0854,
	/* 117 */	0x0855,
	/* 118 */	sprmCFObj, 		//0x0856,
	/* 119 */	0x2E00,
	/* 120 */ 	0xCE01,
	/* 121 */	0x4C02,
	/* 122 */	0x4C03,
	/* 123 */	0x4C04,
	/* 124 */	0x4C05,
	/* 125 */	0,
	/* 126 */	0,
	/* 127 */	0,
	/* 128 */ 	0,
	/* 129 */	0,
	/* 130 */	0,
	/* 131 */	0x3000,
	/* 132 */	0x3001,
	/* 133 */	0xD202,
	/* 134 */	0,
	/* 135 */	0,
	/* 136 */ 	0xF203,
	/* 137 */	0xF204,
	/* 138 */	0x3005,
	/* 139 */	0x3006,
	/* 140 */	0x5007,
	/* 141 */	0x5008,
	/* 142 */	0x3009,
	/* 143 */	0x300A,
	/* 144 */ 	0x500B,
	/* 145 */	0x900C,
	/* 146 */	0x300D,
	/* 147 */	0x300E,
	/* 148 */	0xB00F,
	/* 149 */	0xB010,
	/* 150 */	0x3011,
	/* 151 */	0x3012,
	/* 152 */ 	0x3013,
	/* 153 */	0x3014,
	/* 154 */	0x500B,
	/* 155 */	0x9016,
	/* 156 */	0xB017,
	/* 157 */	0xB018,
	/* 158 */	0x3019,
	/* 159 */	0x301A,
	/* 160 */ 	0x501B,
	/* 161 */	0x501C,
	/* 162 */	0x301D,
	/* 163 */	0x301E,
	/* 164 */	0xB01F,
	/* 165 */	0xB020,
	/* 166 */	0xB021,
	/* 167 */	0xB022,
	/* 168 */ 	0x9023,
	/* 169 */	0x9024,
	/* 170 */	0xB025,
	/* 171 */	0x5026,
	/* 172 */	0,
	/* 173 */	0,
	/* 174 */	0,
	/* 175 */	0,
	/* 176 */ 	0,
	/* 177 */	0,
	/* 178 */	0,
	/* 179 */	0,
	/* 180 */	0,
	/* 181 */	0,
	/* 182 */	0x5400,
	/* 183 */	0x9601,
	/* 184 */ 	0x9602,
	/* 185 */	0x3403,
	/* 186 */	0x3404,
	/* 187 */	0xD605,
	/* 188 */	0xD606,
	/* 189 */	0x9407,
	/* 190 */	0xD608,
	/* 191 */	0xD609,
	/* 192 */ 	0x740A,
	/* 193 */	0xD620,
	/* 194 */	0x7621,
	/* 195 */	0x5622,
	/* 196 */	0x7623,
	/* 197 */	0x5624,
	/* 198 */	0x5625,
	/* 199 */	0xD626,
	/* 200 */	0x7627 };

    /* транслиовать sprm в формат word97 */
    private char translateSprm(char code) {

        switch (version) {

            case WW6:
                if (code <= 200)
                    return sprmTable[code];
                break;

            case WW8:
                switch (code) {
                    case 12:
                        return 0x260A;
                    case 120:
                        return 0x2640;
                }
                if (code <= 127)
                    return sprmTable[code];
                break;

            case WW2:
                switch (code) {
                    case 2:
                        return 0x2400;
                    case 20:
                        return 0x4412;
                }
                if (code <= 164) {
                    if (code >= 146) code += 36;
                    else if (code >= 57) code += 25;
                    else if (code >= 53) code += 12;
                    return sprmTable[code];
                }
                break;

            default:
                break;
        }

        return 0;
    }

    /* перейти к следующему sprm */
    private int skipSprm(char code, byte[] param, int pos) {
        switch (code & 0xE000) {
            case 0x0000:
            case 0x2000:
                pos += 1;
                break;
            case 0x4000:
            case 0x8000:
            case 0xA000:
                pos += 2;
                break;
            case 0x6000:
                pos += 4;
                break;
            case 0xE000:
                pos += 3;
                break;
            case 0xC000:
                switch (code & 0x1FFF) {
                    case 0x1605:
                        if (version == Version.WW6) {
                            pos += 12;
                        } else {
                            pos += 1 + (param[pos] & 0xff);
                        }
                        break;
                    case 0x1606:
                    case 0x1608:
                        pos += 1 + word(param, pos);
                        break;
                    case 0x0615:
                        if (param[pos] == -1) {
                            pos++;
                            pos += (param[pos] & 0xff) * 4;
                            pos += (param[pos] & 0xff) * 3;
                            break;
                        }
                        pos += 1 + (param[pos] & 0xff);
                        break;
                    default:
                        pos += 1 + (param[pos] & 0xff);
                        break;
                }
        }
        return pos;
    }

    private void applyGrpPrl(byte[] prm, int pos, int len) {
        int limit = pos + len;
        if (version == Version.WW8) {
            while ((pos + 1) < limit) {
                char code = word(prm, pos);
                pos += 2;
                applySprm(code, prm, pos);
                pos = skipSprm(code, prm, pos);
            }
        } else {
            while (pos < limit) {
                char code = (char) (prm[pos++] & 255);
                code = translateSprm(code);
                applySprm(code, prm, pos);
                pos = skipSprm(code, prm, pos);
            }
        }
    }

    private void applyCharPrm(byte[] prm, int pos) {
        if (prm == null) return;
        int size;

        switch (version) {

            case WW8:
            case WW6:
                size = prm[pos];
                applyGrpPrl(prm, pos + 1, size);
                break;

            case WW2:
                size = prm[pos];
                if (size >= 1) {
                    if ((prm[pos + 1] & 0x01) != 0)
                        format.bold(!style.bold());
                    if ((prm[pos + 1] & 0x02) != 0)
                        format.italic(!style.italic());
                    if ((prm[pos + 1] & 0x04) != 0)
                        format.strike(!style.strike());
                }
                if (size >= 2) {
                    if ((prm[pos + 2] & 0x02) != 0)
                        format.special(style.special() == 0 ? 1 : 0);
                    if ((prm[pos + 2] & 0x04) != 0)
                        format.strike(!style.strike());
                    if ((prm[pos + 2] & 0x08) != 0)
                        format.obj(!style.obj());
                }
                break;

            case DOS:
                size = prm[pos];
                if (size >= 1 && (prm[pos + 1] & 1) != 0) switch (prm[pos + 1] >> 1) {
                    case 13:
                        format.special(Format.FOOTREF);
                        break;
                    case 19:
                    case 28:
                    case 29:
                        format.special(Format.SPECIAL);
                        break;
                }
                if (size >= 2) {
                    if ((prm[pos + 2] & 1) != 0)
                        format.bold(true);
                    if ((prm[pos + 2] & 2) != 0)
                        format.italic(true);
                }
                if (size >= 4) {
                    if ((prm[pos + 4] & 1) != 0)
                        format.under(true);
                    if ((prm[pos + 4] & 2) != 0)
                        format.strike(true);
                }
                if (size >= 6) {
                    if (prm[pos + 6] != 0 && (prm[pos + 6] & 0x80) == 0)
                        format.sup(true);
                    else if ((prm[6] & 0x80) != 0)
                        format.sub(true);
                }
                break;
        }
    }

    private void applyParaPrm(byte[] prm, int pos) {
        if (prm == null) return;
        int size;
        int istd;

        switch (version) {

            case WW8:
            case WW6:
                size = prm[pos++];
                if (size == 0) size = prm[pos++];
                istd = word(prm, pos);
                if (istd != 4095)
                    applyStyle(istd);
                size = (size << 1) - 2;
                if (size > 0)
                    applyGrpPrl(prm, pos + 2, size);
                break;

            case WW2:
                size = prm[pos++];
                istd = (char) (prm[pos] & 255);
                if (istd != 222)
                    applyStyle(istd);
                size = (size << 1) - 7;
                if (size > 0)
                    applyGrpPrl(prm, pos + 7, size);
                break;

            case DOS:
                size = prm[pos];
                if (size >= 1 && (prm[pos + 1] & 1) != 0) switch (prm[pos + 1] >> 1) {
                    case 39:
                        format.special(Format.FOOTTEXT);
                        break;
                }
                int align = 0;
                if (size >= 2) align = prm[pos + 2] & 3;
                switch (align) {
                    case 0:
                        format.align(Format.LEFT);
                        break;
                    case 1:
                        format.align(Format.CENTER);
                        break;
                    case 2:
                        format.align(Format.RIGHT);
                        break;
                    case 3:
                        format.align(Format.JUSTIFY);
                        break;
                }
                if (size >= 4)  {
                    int level = prm[pos + 4] & 0x7F;
                    if (level < 9) format.level(level);
                }
                if (size >= 17) {
                    if ((prm[pos + 17] & 0xF) != 0) {
                        format.special(Format.SPECIAL);
                    }
                    if (piece.get(0).codepage == 1251 && (prm[pos + 17] & 0x10) != 0) {
                        format.special(Format.SPECIAL);
                        format.xdata = 0;
                    }
                }
                break;
        }
    }

    private void applyPiecePrm(char sprm) {
        if (sprm == 0) return;
        if ((sprm & 1) == 0) {
            char code = (char) ((sprm >> 1) & 127);
            code = translateSprm(code);
            byte[] param = new byte[] { (byte) (sprm >> 8) };
            applySprm(code, param, 0);
        } else {
            int idx = sprm >> 1;
            if (CLX == null) return;
            int pos = 0;
            while (CLX[pos++] == 1) {
                if (idx-- != 0) {
                    pos += 2 + word(CLX, pos);
                } else {
                    int len = word(CLX, pos);
                    applyGrpPrl(CLX, pos + 2, len);
                }
            }
        }
    }

    //////////////////////////////////////////
    // images
    //////////////////////////////////////////

    private boolean getWmf(int pos, int end, AlOneImage ai) throws IOException {
        byte[] buf = new byte[18];
        super.read(pos, buf, 18);
        if (dword(buf, 0) != 0x90001 || word(buf, 4) != 0x300)
            return false;
        int len = dword(buf, 12) * 2;
        int recLen = 18;
        while ((pos += recLen) < end) {
            super.read(pos, buf, 6);
            recLen = dword(buf, 0) * 2;
            int type = word(buf, 4);
            if (type != 0xF43 || recLen != len)
                continue;
            super.read(pos + 6, buf, 6);
            if (dword(buf, 0) != 0xCC0020 || word(buf, 4) != 0)
                return false;
            ai.positionS = (pos + 14) | 0x80000000;
            ai.positionE = (len - 14);
            ai.iType = AlOneImage.IMG_MEMO;//BMP;
            return true;
        }
        return false;
    }

    private boolean getPicf(int pos, int end, AlOneImage ai) throws IOException {
        byte[] buf = new byte[36];
        int recLen = 0;
        while ((pos += recLen) < end) {
            super.read(pos, buf, 8);
            recLen = dword(buf, 4) + 8;
            if (recLen <= 8)
                break;
            int type = word(buf, 2);
            if (type != 0xF007)
                continue;
            pos += 8;
            super.read(pos, buf, 36);
            int len = buf[33];
            pos += 36 + len;
            super.read(pos, buf, 8);
            recLen = dword(buf, 4);
            int head = 16 + (buf[0] & 0x10) + 1;
            type = word(buf, 2);
            switch (type) {
                case 0xF01F: // BMP
                case 0xF01E: // PNG
                case 0xF01D: // JPG
                case 0xF02A: // JPG
                case 0xF029: // TIFF
                    ai.positionS = pos + 8 + head;
                    ai.positionE = recLen - head;
                    ai.iType = AlOneImage.IMG_MEMO;
                    return true;
            }
            return false;
        }
        return false;
    }

    public boolean getExternalImage(AlOneImage ai) {

        Format format = new Format();
        try {
            String[] tokens = ai.name.split("[_]+");
            format.xdata = Integer.parseInt(tokens[0]);
            format.value = Integer.parseInt(tokens[1]);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }

        try {
            if (version == Version.DOS) {
                //WRI: todo
                return false;
            }

            if (format.ole2()) {
                //OLE2: todo
                return false;
            }

            if (format.obj()) {
                //OLE1: todo
                return false;
            }

            if (datStream + format.xdata < 0)
                return false;

            byte[] buf = new byte[256];
            super.read(datStream + format.xdata, buf, 10);

            int lcb = dword(buf, 0);
            int hdr = word(buf, 4);
            int mm = word(buf, 6);

            if (hdr != ((version == Version.WW8) ? 68 : 58))
                return false;

            int rec = datStream + format.xdata + hdr;
            int end = datStream + format.xdata + lcb;

            switch (mm) {
                case 8:
                    if (getWmf(rec, end, ai)) {
                        addFile2List(ai);
                    }
                    return true;
                case 102:
                    rec += 1 + (buf[8] & 0xff);
                case 100:
                    if (getPicf(rec, end, ai)) {
                        addFile2List(ai);
                    }
                    return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void addFile2List(AlOneImage ai) {
        AlFileZipEntry of = new AlFileZipEntry();
        of.compress = 0;
        of.cSize = ai.positionS;
        of.uSize = ai.positionE;
        of.flag = 0;
        of.position = 0;
        of.time = 0;
        of.name = ai.name;
        fileList.add(of);

        mapFile.put(of.name, fileList.size() - 1);
    }

    private boolean fillExternalFile(int index, byte[] dst, int len) {
        try {
            int pos = index & 0x7fffffff;
            //int len = dst.length - 1;
            super.read(pos, dst, len);
            switch (index & 0x80000000) {
                case 0x80000000:
                    dst[ 0] = (byte) 'B';
                    dst[ 1] = (byte) 'M';
                    dst[ 2] = (byte) ((len) & 0xff);
                    dst[ 3] = (byte) ((len >> 8) & 0xff);
                    dst[ 4] = (byte) ((len >> 16) & 0xff);
                    dst[ 5] = (byte) ((len >> 24) & 0xff);
                    dst[ 6] = 0;
                    dst[ 7] = 0;
                    dst[ 8] = 0;
                    dst[ 9] = 0;
                    int bitCount = word(dst, 0x1c);
                    int compression = dword(dst, 0x1e);
                    int clrUsed = dword(dst, 0x2e);
                    int offBits = 0x36;
                    if (clrUsed != 0) {
                        offBits += clrUsed * 4;
                    } else switch (bitCount) {
                        case 1:	offBits += 8; break;
                        case 4:	offBits += 64; break;
                        case 8:	offBits += 1024; break;
                        case 16:
                        case 32:
                            if (compression == 3)
                                offBits += 12;
                    }
                    dst[10] = (byte) ((offBits) & 0xff);
                    dst[11] = (byte) ((offBits >> 8) & 0xff);
                    dst[12] = (byte) ((offBits >> 16) & 0xff);
                    dst[13] = (byte) ((offBits >> 24) & 0xff);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean fillBufFromExternalFile(int num, int pos, byte[] dst, int dst_pos, int cnt) {
        int res = 0;
        if (num >= 0 && num < fileList.size() && pos == 0) {
            if (fillExternalFile(fileList.get(num).cSize, dst, cnt))
            return true;
        }
        return false;
    }

}
