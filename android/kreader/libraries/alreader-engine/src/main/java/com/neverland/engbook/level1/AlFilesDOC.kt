package com.neverland.engbook.level1

import com.neverland.engbook.forpublic.EngBookMyType.TAL_FILE_TYPE
import com.neverland.engbook.forpublic.TAL_RESULT
import com.neverland.engbook.unicode.AlUnicode
import com.neverland.engbook.util.AlOneImage
import com.neverland.engbook.util.AlStyles

import java.io.IOException
import java.lang.ref.SoftReference

/********************************************************************************************************************
 * Reading <a href="http://msdn.microsoft.com/en-us/library/cc313153.aspx">
 * [MS-DOC]: Microsoft Word (.doc) Binary File Format</a>
 */

private const val DOS_MAGIC = 0xBE31
private const val WRI_MAGIC = 0xBE32
private const val WW2_MAGIC_1 = 0xA59B
private const val WW2_MAGIC_2 = 0xA5DB
private const val WW6_MAGIC = 0xA5DC
private const val WW8_MAGIC = 0xA5EC

fun AlFiles.isDOC(fName: String, fList: ArrayList<AlFileZipEntry>?, ext: String?): TAL_FILE_TYPE {
    if (ext != null && !ext.equals(".doc", ignoreCase = true))
        return TAL_FILE_TYPE.TXT
    if (isMSCFBFile())
        return TAL_FILE_TYPE.DOC
    if (size < 256)
        return TAL_FILE_TYPE.TXT
    read_pos = 0
    val magic = getWord().toInt()
    return when (magic) {
        DOS_MAGIC, WRI_MAGIC, WW2_MAGIC_1, WW2_MAGIC_2 -> TAL_FILE_TYPE.DOC
        else -> TAL_FILE_TYPE.TXT
    }
}

class AlFilesDOC : AlFilesMSCFB() {

    private enum class Version { DOS, WW2, WW6, WW8 }

    private lateinit var version: Version

    override fun initState(file: String, parent: AlFiles, fList: ArrayList<AlFileZipEntry>?): Int {
        super.initState(file, parent, fList)
        var res = TAL_RESULT.OK

        try {
            parent.read_pos = 0
            val magic = parent.getWord().toInt()
            version = when (magic) {
                DOS_MAGIC, WRI_MAGIC -> Version.DOS
                WW2_MAGIC_1, WW2_MAGIC_2 -> Version.WW2
                else -> {
                    parseAsMSCFB()
                    docStream = stream("WordDocument")
                    if (docStream == Integer.MAX_VALUE) throw IOException()
                    when (ByteArray(4).read(docStream, 4).uword(0)) {
                        WW6_MAGIC -> Version.WW6
                        WW8_MAGIC -> Version.WW8
                        else -> throw IOException()
                    }
                }
            }

            readFIB()
            readPieces()
            readBinTab()
            readHrefs()
            readStyles()
            doRead()

            makePieces()
            makeBinTab()
            makeHrefs()
            makeStyles()

        } catch (e: Exception) {
            e.printStackTrace()
            res = TAL_RESULT.ERROR
        }

        ident = "doc"
        return res
    }

    val isUnicode get() = fib.flags and 0x1000 != 0

    override fun getCodePage(): Int {

        if (version == Version.DOS)
            return -1

        return when (fib.lid and 0x3ff) {

            0x01E  //THAI
                -> 874

            0x011 //JAPANESE
                -> 932

            0x004 //CHINESE
                -> 936

            0x012 //KOREAN
                -> 949

            0x005, //CZECH
            0x00E, //HUNGARIAN
            0x015, //POLISH
            0x018, //ROMANIAN
            0x01A, //SERBIAN/CROATIAN
            0x01B, //SLOVAK
            0x024  //SLOVENIAN
                -> 1250

            0x002, //BULGARIAN
            0x019, //RUSSIAN
            0x022, //UKRAINIAN
            0x023, //BELARUSIAN
            0x028, //TAJIK
            0x02F, //MACEDONIAN
            0x03F, //KAZAK
            0x040, //KIRGHIZ
            0x042, //TURKMEN
            0x043, //UZBEK
            0x044, //TATAR
            0x050  //MONGOLIAN
                -> 1251

            0x008  //GREEK
                -> 1253

            0x01F  //TURKISH
                -> 1254

            0x00D  //HEBREW
                -> 1255

            0x001, //ARABIC
            0x020, //URDU
            0x021, //INDONESIAN
            0x029  //FARSI
                -> 1256

            0x025, //ESTONIAN
            0x026, //LATVIAN
            0x027  //LITHUANIAN
                -> 1257

            0x02A  //VIETNAMESE
                -> 1258

            //0x02B: ARMENIAN
            //0x02C: AZERI
            //0x037: GEORGIAN

            //0x039: HINDI
            //0x03E: MALAY
            //0x041: SWAHILI
            //0x049: TAMIL
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
            //0x01C: ALBANIAN
            //0x01D: SWEDISH
            //0x02D: BASQUE
            //0x036: AFRIKAANS
            //0x03C: GAELIC
            //0x03D: YIDDISH

            else -> 1252
        }
    }

    override fun getBuffer(pos: Int, dst: ByteArray, cnt: Int): Int {
        dst.fill(0, toIndex = cnt)

        var pos = if (!isUnicode) pos else pos shr 1
        var cnt = cnt
        var res = 0

        try {
            var n = findPiece(pos)
            while (cnt > 0) {
                if (piece[n].filePos == Integer.MAX_VALUE)
                    break
                val charPos = piece[n - 1].charPos
                val offset = pos - charPos
                var len = piece[n].charPos - charPos - offset
                if (!isUnicode) {
                    if (len > cnt) len = cnt
                    dst.lazyRead(docStream + piece[n].filePos + offset, res, len)
                    res += len
                    cnt -= len
                } else {
                    if (len > cnt / 2) len = cnt / 2
                    if (piece[n].codepage == 1200) {
                        dst.lazyRead(docStream + piece[n].filePos + offset * 2, res, len * 2)
                    } else {
                        dst.lazyRead(docStream + piece[n].filePos + offset, res + len, len, 1)
                    }
                    res += len * 2
                    cnt -= len * 2
                }
                pos += len
                n++
            }

            doRead {
                if (it.tag == 1) { // converting from cp1252 to utf16
                    val tabl = AlUnicode.getDataCP(1252)
                    var bpos = it.pos
                    var wpos = it.pos - it.len
                    for (i in 0 until it.len) {
                        var ch = it.buf.ubyte(bpos++)
                        if (ch >= 128) ch = tabl[ch - 128].toInt()
                        it.buf[wpos++] = (ch and 255).toByte()
                        it.buf[wpos++] = (ch shr 8).toByte()
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return res
    }

    class Format {
        @JvmField var start = 0
        @JvmField var limit = 0
        @JvmField var xnote = 0
        @JvmField var xdata = 0

        internal var value: Long = 0
        internal var color: Long = 0
        internal var indent: Long = 0
        internal var lines: Long = 0

        val format: Int
            get() = value.toInt() and 0x3ffffff

        var newPar: Boolean
            get() = value and STYLE_NEWPAR != 0L
            set(on) = if (on)
                value = value or STYLE_NEWPAR
            else value = value and STYLE_NEWPAR.inv()

        var bold: Boolean
            get() = value and STYLE_BOLD != 0L
            set(on) = if (on)
                value = value or STYLE_BOLD
            else value = value and STYLE_BOLD.inv()

        var italic: Boolean
            get() = value and STYLE_ITALIC != 0L
            set(on) = if (on)
                value = value or STYLE_ITALIC
            else value = value and STYLE_ITALIC.inv()

        var strike: Boolean
            get() = value and STYLE_STRIKE != 0L
            set(on) = if (on)
                value = value or STYLE_STRIKE
            else value = value and STYLE_STRIKE.inv()

        var dstrike: Boolean
            get() = value and STYLE_DSTRIKE != 0L
            set(on) = if (on)
                //value = value or STYLE_DSTRIKE
            //else value = value and STYLE_DSTRIKE.inv()
                value = value or STYLE_STRIKE
            else value = value and STYLE_STRIKE.inv()

        var under: Boolean
            get() = value and STYLE_UNDER != 0L
            set(on) = if (on)
                value = value or STYLE_UNDER
            else value = value and STYLE_UNDER.inv()

        var sub: Boolean
            get() = value and STYLE_SUB != 0L
            set(on) = if (on)
                value = value or STYLE_SUB
            else value = value and STYLE_SUB.inv()

        var sup: Boolean
            get() = value and STYLE_SUP != 0L
            set(on) = if (on)
                value = value or STYLE_SUP
            else value = value and STYLE_SUP.inv()

        var caps: Boolean
            get() = value and STYLE_CAPS != 0L
            set(on) = if (on)
                value = value or STYLE_CAPS
            else value = value and STYLE_CAPS.inv()

        var smallCaps: Boolean
            get() = value and STYLE_SMALLCAPS != 0L
            set(on) = if (on)
                value = value or STYLE_SMALLCAPS
            else value = value and STYLE_SMALLCAPS.inv()

        var hidden: Boolean
            get() = value and STYLE_HIDDEN != 0L
            set(on) = if (on)
                value = value or STYLE_HIDDEN
            else value = value and STYLE_HIDDEN.inv()

        var obj: Boolean
            get() = value and STYLE_OBJ != 0L
            set(on) = if (on)
                value = value or STYLE_OBJ
            else value = value and STYLE_OBJ.inv()

        var ole2: Boolean
            get() = value and STYLE_OLE2 != 0L
            set(on) = if (on)
                value = value or STYLE_OLE2
            else value = value and STYLE_OLE2.inv()

        var level: Int
            get() = (value and MASK_LEVEL shr SHIFT_LEVEL).toInt()
            set(level) {
                value = value and MASK_LEVEL.inv()
                value = value or ((level.toLong() shl SHIFT_LEVEL) and MASK_LEVEL)
            }

        var align: Int
            get() = (value and MASK_ALIGN shr SHIFT_ALIGN).toInt()
            set(align) {
                value = value and MASK_ALIGN.inv()
                value = value or ((align.toLong() shl SHIFT_ALIGN) and MASK_ALIGN)
            }

        var fontSize: Int
            get() = (value and MASK_FONTSIZE shr SHIFT_FONTSIZE).toInt()
                .let { if (it == 0) 20 else it }
            set(fontSize) {
                value = value and MASK_FONTSIZE.inv()
                value = value or ((fontSize.toLong() shl SHIFT_FONTSIZE) and MASK_FONTSIZE)
            }

        var fontWidth: Int
            get() = (value and MASK_FONTWIDTH shr SHIFT_FONTWIDTH).toInt()
                .let { if (it == 0) 100 else it }
            set(fontWidth) {
                value = value and MASK_FONTWIDTH.inv()
                value = value or ((fontWidth.toLong() shl SHIFT_FONTWIDTH) and MASK_FONTWIDTH)
            }

        var fontSpacing: Int
            get() = (value and MASK_FONTSPACING shr SHIFT_FONTSPACING).toInt()
                .let { if (it < 0x200) it else it or 0x3ff.inv() }
            set(fontWidth) {
                value = value and MASK_FONTSPACING.inv()
                value = value or ((fontWidth.toLong() shl SHIFT_FONTSPACING) and MASK_FONTSPACING)
            }

        var fontColor: Int
            get() = (color and 0xffffffff).toInt()
            set(fontColor) {
                color = color and 0xffffffff.inv()
                color = color or (fontColor.toLong() and 0xffffffff)
            }

        var bgColor: Int
            get() = ((color shr 32) and 0xffffffff).toInt()
            set(bgColor) {
                color = color and 0xffffffff
                color = color or ((bgColor.toLong() and 0xffffffff) shl 32)
            }

        var indLeft: Int
            get() = (indent and MASK_INDLEFT shr SHIFT_INDLEFT).toInt()
            set(ind) {
                indent = indent and MASK_INDLEFT.inv()
                indent = indent or ((ind.toLong() shl SHIFT_INDLEFT) and MASK_INDLEFT)
            }

        var indRight: Int
            get() = (indent and MASK_INDRIGHT shr SHIFT_INDRIGHT).toInt()
            set(ind) {
                indent = indent and MASK_INDRIGHT.inv()
                indent = indent or ((ind.toLong() shl SHIFT_INDRIGHT) and MASK_INDRIGHT)
            }

        var indBefore: Int
            get() = (indent and MASK_INDTOP shr SHIFT_INDTOP).toInt()
            set(ind) {
                indent = indent and MASK_INDTOP.inv()
                indent = indent or ((ind.toLong() shl SHIFT_INDTOP) and MASK_INDTOP)
            }

        var indAfter: Int
            get() = (indent and MASK_INDBOTTOM shr SHIFT_INDBOTTOM).toInt()
            set(ind) {
                indent = indent and MASK_INDBOTTOM.inv()
                indent = indent or ((ind.toLong() shl SHIFT_INDBOTTOM) and MASK_INDBOTTOM)
            }

        var indFirst: Int
            get() = (lines and MASK_LINFIRST shr SHIFT_LINFIRST).toInt()
                    .let { if (it < 0x8000) it else it or 0x7fff.inv() }
            set(ind) {
                lines = lines and MASK_LINFIRST.inv()
                lines = lines or ((ind.toLong() shl SHIFT_LINFIRST) and MASK_LINFIRST)
            }

        var special: Int
            get() = (value and MASK_SPECIAL shr SHIFT_SPECIAL).toInt()
            set(special) {
                value = value and MASK_SPECIAL.inv()
                value = value or ((special.toLong() shl SHIFT_SPECIAL) and MASK_SPECIAL)
            }

        var keepLines: Boolean
            get() = value and STYLE_KEEPLINES != 0L
            set(on) = if (on)
                value = value or STYLE_KEEPLINES
            else value = value and STYLE_KEEPLINES.inv()

        var keepFollow: Boolean
            get() = value and STYLE_KEEPFOLLOW != 0L
            set(on) = if (on)
                value = value or STYLE_KEEPFOLLOW
            else value = value and STYLE_KEEPFOLLOW.inv()

        var pageBreakBefore: Boolean
            get() = value and STYLE_BREAKBEFORE != 0L
            set(on) = if (on)
                value = value or STYLE_BREAKBEFORE
            else value = value and STYLE_BREAKBEFORE.inv()

        var noHyph: Boolean
            get() = value and STYLE_NOHYPH != 0L
            set(on) = if (on)
                value = value or STYLE_NOHYPH
            else value = value and STYLE_NOHYPH.inv()

        companion object {
            const val STYLE_BOLD = AlStyles.STYLE_BOLD
            const val STYLE_ITALIC = AlStyles.STYLE_ITALIC
            const val STYLE_SUP = AlStyles.STYLE_SUP
            const val STYLE_SUB = AlStyles.STYLE_SUB
            const val STYLE_UNDER = AlStyles.STYLE_UNDER
            const val STYLE_STRIKE = AlStyles.STYLE_STRIKE
            const val STYLE_HIDDEN = AlStyles.STYLE_HIDDEN
            const val STYLE_DSTRIKE = 0x0400L
            const val STYLE_CAPS = 0x0800L
            const val STYLE_SMALLCAPS = 0x1000L
            const val STYLE_NEWPAR = 0x2000L
            const val STYLE_OBJ = 0x4000L
            const val STYLE_OLE2 = 0x8000L

            const val SHIFT_LEVEL = 16
            const val MASK_LEVEL = 0xfL shl SHIFT_LEVEL

            const val SHIFT_ALIGN = 20
            const val MASK_ALIGN = 7L shl SHIFT_ALIGN
            const val NORMAL = 0
            const val LEFT = 1
            const val RIGHT = 2
            const val CENTER = 3
            const val JUSTIFY = 4

            const val SHIFT_SPECIAL = 23
            const val MASK_SPECIAL = 7L shl SHIFT_SPECIAL
            const val SPECIAL = 1
            const val FOOTREF = 2
            const val FOOTTEXT = 3
            const val ENDREF = 4
            const val ENDTEXT = 5
            const val ANNREF = 6
            const val ANNTEXT = 7

            const val STYLE_NOHYPH = 0x10000000L
            const val STYLE_KEEPLINES = 0x20000000L
            const val STYLE_KEEPFOLLOW = 0x40000000L
            const val STYLE_BREAKBEFORE = 0x80000000L

            const val SHIFT_FONTSIZE = 32
            const val MASK_FONTSIZE = 0xfffL shl SHIFT_FONTSIZE
            const val SHIFT_FONTWIDTH = 44
            const val MASK_FONTWIDTH = 0x3ffL shl SHIFT_FONTWIDTH
            const val SHIFT_FONTSPACING = 54
            const val MASK_FONTSPACING = 0x3ffL shl SHIFT_FONTSPACING

            const val SHIFT_INDLEFT = 0
            const val MASK_INDLEFT = 0xffffL shl SHIFT_INDLEFT
            const val SHIFT_INDRIGHT = 16
            const val MASK_INDRIGHT = 0xffffL shl SHIFT_INDRIGHT
            const val SHIFT_INDTOP = 32
            const val MASK_INDTOP = 0xffffL shl SHIFT_INDTOP
            const val SHIFT_INDBOTTOM = 48
            const val MASK_INDBOTTOM = 0xffffL shl SHIFT_INDBOTTOM

            const val SHIFT_LINFIRST = 0
            const val MASK_LINFIRST = 0xffffL shl SHIFT_LINFIRST
        }
    }

    @JvmField var format = Format()
    private val style = Format()

    fun getFormat(pos: Int) {
        style.values(0)
        format.values(0)
        format.xdata = Integer.MAX_VALUE
        format.xnote = Integer.MAX_VALUE

        val pos = if (!isUnicode) pos else pos shr 1

        try {

            // определяем диапазоны
            val n = findPiece(pos)
            val pieceStart = piece[n - 1].charPos
            val pieceLimit = piece[n].charPos
            val offset = pos - pieceStart
            val charPrm: Prm
            val paraPrm: Prm
            if (piece[n].codepage == 1200) {
                val filePos = piece[n].filePos + (offset shl 1)
                charPrm = getCharPrm(filePos)
                paraPrm = getParaPrm(filePos)
                charPrm.start = pos - (filePos - charPrm.start shr 1)
                charPrm.limit = pos - (filePos - charPrm.limit shr 1)
                paraPrm.start = pos - (filePos - paraPrm.start shr 1)
                paraPrm.limit = pos - (filePos - paraPrm.limit shr 1)
            } else {
                val filePos = piece[n].filePos + offset
                charPrm = getCharPrm(filePos)
                paraPrm = getParaPrm(filePos)
                charPrm.start = pos - (filePos - charPrm.start)
                charPrm.limit = pos - (filePos - charPrm.limit)
                paraPrm.start = pos - (filePos - paraPrm.start)
                paraPrm.limit = pos - (filePos - paraPrm.limit)
            }
            val note = findNote(pos)

            // еще раз проверяем на начало абзаца
            if (paraPrm.start < pieceStart) {
                if (pieceStart == 0) {
                    paraPrm.start = 0
                } else {
                    val e = n - 1
                    val prevStart = piece[e - 1].charPos
                    val prevLimit = piece[e].charPos
                    val prevAddr = prevLimit - 1
                    val prevOffset = prevAddr - prevStart
                    val prevPrm: Prm
                    if (piece[e].codepage == 1200) {
                        val filePos = piece[e].filePos + (prevOffset shl 1)
                        prevPrm = getParaPrm(filePos)
                        prevPrm.limit = prevAddr - (filePos - prevPrm.limit shr 1)
                    } else {
                        val filePos = piece[e].filePos + prevOffset
                        prevPrm = getParaPrm(filePos)
                        prevPrm.limit = prevAddr - (filePos - prevPrm.limit)
                    }
                    if (prevPrm.limit == prevLimit) {
                        paraPrm.start = prevPrm.limit
                    }
                }
            }

            // вычисляем наименьший
            var start = pieceStart
            if (start < charPrm.start) start = charPrm.start
            if (start < paraPrm.start) start = paraPrm.start
            if (note.type != 0 && start < note.charPos) start = note.charPos
            var limit = pieceLimit
            if (limit > charPrm.limit) limit = charPrm.limit
            if (limit > paraPrm.limit) limit = paraPrm.limit

            // вычисляем свойства фрагмента
            if (start == paraPrm.start) format.newPar = true
            applyParaPrm(paraPrm.buf, paraPrm.pos)
            applyCharPrm(charPrm.buf, charPrm.pos)
            applyPiecePrm(piece[n].prm)
            if (note.type != 0 && start == note.charPos) {
                format.special = note.type
                format.xnote = note.index
            }

            if (isUnicode) {
                format.start = start shl 1
                format.limit = limit shl 1
            } else {
                format.start = start
                format.limit = limit
            }

        } catch (e: Exception) {
            style.values(0)
            format.values(0)
            format.xdata = Integer.MAX_VALUE
            format.xnote = Integer.MAX_VALUE
            format.limit = format.start + 1
            e.printStackTrace()
        }
    }

    private var docStream = 0 // WordDocument Stream
    private var tabStream = 0 // 1Table Stream or 0Table Stream
    private var datStream = 0 // Data Stream

    //////////////////////////////////////////
    // file information block
    //////////////////////////////////////////

    private class Fib(
            val lid: Int = 0,
            val flags: Int = 0,
            val fcMin: Int = 0,
            val fcMac: Int = 0,
            val cbMac: Int = 0,
            val ccpText: Int = 0,
            val ccpFtn: Int = 0,
            val ccpHdd: Int = 0,
            val ccpMcr: Int = 0,
            val ccpAtn: Int = 0,
            val ccpEdn: Int = 0,
            val ccpTxbx: Int = 0,
            val ccpHdrTxbx: Int = 0,
            val fcClx: Int = 0,
            val cbClx: Int = 0,
            val fcStshf: Int = 0,
            val cbStshf: Int = 0,
            val fcSttbfbkmk: Int = 0,
            val cbSttbfbkmk: Int = 0,
            val fcPlcfbkf: Int = 0,
            val cbPlcfbkf: Int = 0,
            val fcPlcffndRef: Int = 0,
            val cbPlcffndRef: Int = 0,
            val fcPlcffndTxt: Int = 0,
            val cbPlcffndTxt: Int = 0,
            val fcPlcfendRef: Int = 0,
            val cbPlcfendRef: Int = 0,
            val fcPlcfendTxt: Int = 0,
            val cbPlcfendTxt: Int = 0,
            val fcPlcfandRef: Int = 0,
            val cbPlcfandRef: Int = 0,
            val fcPlcfandTxt: Int = 0,
            val cbPlcfandTxt: Int = 0,
            val fcPlcfbtePapx: Int = 0,
            val cbPlcfbtePapx: Int = 0,
            val fcPlcfbteChpx: Int = 0,
            val cbPlcfbteChpx: Int = 0,
            val pnChar: Int = 0,
            val pnPara: Int = 0,
            val pnFntb: Int = 0
    )

    private lateinit var fib: Fib
    private var FKP_SIZE = 0

    private fun readFIB() {
        val FIB = ByteArray(0x400)

        when (version) {

            Version.WW8 -> {
                FKP_SIZE = 512
                FIB.read(docStream, 0x382)
                fib = Fib(
                        lid = FIB.uword(0x006),
                        flags = FIB.uword(0x00A),
                        fcMin = FIB.dword(0x018),
                        fcMac = FIB.dword(0x01C),
                        ccpText = FIB.dword(0x04C),
                        ccpFtn = FIB.dword(0x050),
                        ccpHdd = FIB.dword(0x054),
                        ccpMcr = FIB.dword(0x058),
                        ccpAtn = FIB.dword(0x05C),
                        ccpEdn = FIB.dword(0x060),
                        ccpTxbx = FIB.dword(0x064),
                        ccpHdrTxbx = FIB.dword(0x068),
                        fcStshf = FIB.dword(0x0A2),
                        cbStshf = FIB.dword(0x0A6),
                        fcPlcffndRef = FIB.dword(0x0AA),
                        cbPlcffndRef = FIB.dword(0x0AE),
                        fcPlcffndTxt = FIB.dword(0x0B2),
                        cbPlcffndTxt = FIB.dword(0x0B6),
                        fcPlcfandRef = FIB.dword(0x0BA),
                        cbPlcfandRef = FIB.dword(0x0BE),
                        fcPlcfandTxt = FIB.dword(0x0C2),
                        cbPlcfandTxt = FIB.dword(0x0C6),
                        fcPlcfbteChpx = FIB.dword(0x0FA),
                        cbPlcfbteChpx = FIB.dword(0x0FE),
                        fcPlcfbtePapx = FIB.dword(0x102),
                        cbPlcfbtePapx = FIB.dword(0x106),
                        fcSttbfbkmk = FIB.dword(0x142),
                        cbSttbfbkmk = FIB.dword(0x146),
                        fcPlcfbkf = FIB.dword(0x14A),
                        cbPlcfbkf = FIB.dword(0x14E),
                        fcClx = FIB.dword(0x1A2),
                        cbClx = FIB.dword(0x1A6),
                        fcPlcfendRef = FIB.dword(0x20A),
                        cbPlcfendRef = FIB.dword(0x20E),
                        fcPlcfendTxt = FIB.dword(0x212),
                        cbPlcfendTxt = FIB.dword(0x216)
                )

                if (fib.flags and 0x0100 != 0) throw IOException() // encrypted file - not implemented
                tabStream = stream(if (fib.flags and 0x0200 == 0) "0Table" else "1Table")
                if (tabStream == Integer.MAX_VALUE) throw IOException()
                datStream = stream("Data")
            }

            Version.WW6 -> {
                FKP_SIZE = 512
                FIB.read(docStream, 0x2AA)
                fib = Fib(
                        lid = FIB.uword(0x006),
                        flags = FIB.uword(0x00A),
                        fcMin = FIB.dword(0x018),
                        fcMac = FIB.dword(0x01C),
                        ccpText = FIB.dword(0x034),
                        ccpFtn = FIB.dword(0x038),
                        ccpHdd = FIB.dword(0x03C),
                        ccpMcr = FIB.dword(0x040),
                        ccpAtn = FIB.dword(0x044),
                        ccpEdn = FIB.dword(0x048),
                        ccpTxbx = FIB.dword(0x04C),
                        ccpHdrTxbx = FIB.dword(0x050),
                        fcStshf = FIB.dword(0x060),
                        cbStshf = FIB.dword(0x064),
                        fcPlcffndRef = FIB.dword(0x068),
                        cbPlcffndRef = FIB.dword(0x06C),
                        fcPlcffndTxt = FIB.dword(0x070),
                        cbPlcffndTxt = FIB.dword(0x074),
                        fcPlcfandRef = FIB.dword(0x078),
                        cbPlcfandRef = FIB.dword(0x07C),
                        fcPlcfandTxt = FIB.dword(0x080),
                        cbPlcfandTxt = FIB.dword(0x084),
                        fcPlcfbteChpx = FIB.dword(0x0B8),
                        cbPlcfbteChpx = FIB.dword(0x0BC),
                        fcPlcfbtePapx = FIB.dword(0x0C0),
                        cbPlcfbtePapx = FIB.dword(0x0C4),
                        fcSttbfbkmk = FIB.dword(0x100),
                        cbSttbfbkmk = FIB.dword(0x104),
                        fcPlcfbkf = FIB.dword(0x108),
                        cbPlcfbkf = FIB.dword(0x10C),
                        fcClx = FIB.dword(0x160),
                        cbClx = FIB.dword(0x164),
                        fcPlcfendRef = FIB.dword(0x1D2),
                        cbPlcfendRef = FIB.dword(0x1D6),
                        fcPlcfendTxt = FIB.dword(0x1DA),
                        cbPlcfendTxt = FIB.dword(0x1DE)
                )

                if (fib.flags and 0x0100 != 0) throw IOException() // encrypted file - not implemented
                datStream = docStream
                tabStream = docStream
            }

            Version.WW2 -> {
                FKP_SIZE = 512
                datStream = 0
                tabStream = 0
                docStream = 0
                FIB.read(0, 0x146)
                val cbMac = FIB.dword(0x20)
                val fcClx = FIB.dword(0x11E)
                val cbClx = FIB.uword(0x122)
                fib = Fib(
                        lid = FIB.uword(0x06),
                        flags = FIB.uword(0x0A),
                        fcMin = FIB.dword(0x18),
                        fcMac = FIB.dword(0x1C),
                        cbMac = cbMac,
                        ccpText = FIB.dword(0x34),
                        ccpFtn = FIB.dword(0x38),
                        ccpHdd = FIB.dword(0x3C),
                        ccpMcr = FIB.dword(0x40),
                        ccpAtn = FIB.dword(0x44),
                        fcStshf = FIB.dword(0x5E),
                        cbStshf = FIB.uword(0x62),
                        fcPlcffndRef = FIB.dword(0x64),
                        cbPlcffndRef = FIB.uword(0x68),
                        fcPlcffndTxt = FIB.dword(0x6A),
                        cbPlcffndTxt = FIB.uword(0x6E),
                        fcPlcfandRef = FIB.dword(0x70),
                        cbPlcfandRef = FIB.uword(0x74),
                        fcPlcfandTxt = FIB.dword(0x76),
                        cbPlcfandTxt = FIB.uword(0x7A),
                        fcPlcfbteChpx = FIB.dword(0xA0),
                        cbPlcfbteChpx = FIB.uword(0xA4),
                        fcPlcfbtePapx = FIB.dword(0xA6),
                        cbPlcfbtePapx = FIB.uword(0xAA),
                        fcSttbfbkmk = FIB.dword(0xD6),
                        cbSttbfbkmk = FIB.uword(0xDA),
                        fcPlcfbkf = FIB.dword(0xDC),
                        cbPlcfbkf = FIB.uword(0xE0),
                        fcClx = fcClx,
                        cbClx = if (cbClx != 0) cbClx else cbMac - fcClx
                )
            }

            Version.DOS -> {
                FKP_SIZE = 128
                datStream = 0
                tabStream = 0
                docStream = 0
                FIB.read(0, 0x80)
                val fcMac = FIB.dword(0x0E)
                val pnChar = (fcMac + 127) / 128
                fib = Fib(
                        fcMac = fcMac,
                        pnPara = FIB.uword(0x12),
                        pnFntb = FIB.uword(0x14),
                        cbMac = FIB.uword(0x60),
                        ccpText = fcMac - 0x80,
                        pnChar = if (pnChar > 0) pnChar else 1
                )
            }
        }
    }

    //////////////////////////////////////////
    // piece table
    //////////////////////////////////////////

    private class Piece(val charPos: Int, var filePos: Int, var codepage: Int, var prm: Int = 0)

    private lateinit var piece: ArrayList<Piece>
    private var nPieces = 0
    private var CLX: ByteArray? = null

    private fun readPieces() {
        if (version == Version.WW8 || (fib.flags and 4) != 0) {
            CLX = ByteArray(fib.cbClx)
            CLX!!.lazyRead(tabStream + fib.fcClx)
        }
    }

    private fun makePieces() {

        if (version == Version.DOS) {
            val cp = if (fib.cbMac == 0) 866 else 1251
            piece = arrayListOf<Piece>(
                    Piece(0, Integer.MAX_VALUE, cp),
                    Piece(fib.ccpText, 0x80, cp),
                    Piece(Integer.MAX_VALUE, Integer.MAX_VALUE, 0))
            nPieces = 1
            size = fib.ccpText
            return
        }

        val CLX = CLX
        if (CLX == null) {
            val cp = if (isUnicode) 1200 else 1251
            piece = arrayListOf<Piece>(
                    Piece(0, Integer.MAX_VALUE, cp),
                    Piece(fib.fcMac - fib.fcMin, fib.fcMin, cp),
                    Piece(Integer.MAX_VALUE, Integer.MAX_VALUE, 0))
            nPieces = 1

        } else {
            val cp = if (isUnicode) 1200 else 1251
            var addr = 0
            var keep_clx = false
            var tag = CLX.ubyte(addr++)
            while (tag == 1) {
                keep_clx = true
                addr += 2 + CLX.uword(addr)
                tag = CLX.ubyte(addr++)
            }
            if (tag != 2) throw IOException()
            if (version == Version.WW2) {
                nPieces = CLX.uword(addr)
                addr += 2
            } else {
                nPieces = CLX.dword(addr)
                addr += 4
            }
            nPieces = (nPieces - 4) / (4 + 8)
            piece = ArrayList<Piece>(nPieces + 2)
            if (CLX.dword(addr) != 0) throw IOException()
            addr += 4
            piece.add(Piece(0, Integer.MAX_VALUE, cp))
            for (i in 1..nPieces) {
                piece.add(Piece(CLX.dword(addr), 0, 0))
                addr += 4
            }
            piece.add(Piece(Integer.MAX_VALUE, Integer.MAX_VALUE, 0))
            for (i in 1..nPieces) {
                val p = piece[i]
                if (piece[i].charPos <= piece[i - 1].charPos) throw IOException()
                var dtemp = CLX.dword(addr + 2)
                if ((dtemp and (1 shl 30)) == 0) {
                    p.codepage = cp
                } else {
                    p.codepage = 1252
                    dtemp = dtemp and (1 shl 30).inv()
                    dtemp = dtemp shr 1
                }
                p.filePos = dtemp
                p.prm = CLX.uword(addr + 6)
                addr += 8
            }
            if (!keep_clx)
                this.CLX = null
        }

        if (piece[nPieces].charPos < fib.ccpText)
            throw IOException()
        if (isUnicode) {
            size = (fib.ccpText + fib.ccpFtn + fib.ccpHdd + fib.ccpMcr +
                    fib.ccpAtn + fib.ccpEdn + fib.ccpTxbx + fib.ccpHdrTxbx) * 2
        } else {
            size = (fib.ccpText + fib.ccpFtn + fib.ccpHdd + fib.ccpMcr +
                    fib.ccpAtn + fib.ccpEdn + fib.ccpTxbx + fib.ccpHdrTxbx)
            piece[0].codepage = 1251
        }
    }

    private fun findPiece(cp: Int): Int {
        val n = piece.binarySearchBy(cp) { it.charPos }
        return if (n >= 0) n + 1 else -n - 1
    }

    //////////////////////////////////////////
    // bin tables & formatted disk pages
    //////////////////////////////////////////

    private class Prm(var start: Int, var limit: Int, var buf: ByteArray?, var pos: Int)
    private class FkPage(val pageNum: Int, val filePos: Int) {
        var buf = SoftReference<ByteArray>(null)
    }

    private val binTabPara = mutableListOf<FkPage>()
    private val binTabChar = mutableListOf<FkPage>()
    private var plcfbtePapx: ByteArray? = null
    private var nBinsPara = 0
    private var plcfbteChpx: ByteArray? = null
    private var nBinsChar = 0

    private fun readBinTab() {
        when (version) {

            Version.WW2, Version.WW6, Version.WW8 -> {
                if (fib.cbPlcfbteChpx > 0) {
                    plcfbteChpx = ByteArray(fib.cbPlcfbteChpx)
                    plcfbteChpx!!.lazyRead(tabStream + fib.fcPlcfbteChpx)
                }
                if (fib.cbPlcfbtePapx > 0) {
                    plcfbtePapx = ByteArray(fib.cbPlcfbtePapx)
                    plcfbtePapx!!.lazyRead(tabStream + fib.fcPlcfbtePapx)
                }
            }

            Version.DOS -> {
                if (fib.pnChar < fib.pnPara) {
                    nBinsChar = fib.pnPara - fib.pnChar
                    plcfbteChpx = ByteArray(nBinsChar * 4)
                    for (i in 1 until nBinsChar)
                        plcfbteChpx!!.lazyRead((fib.pnChar + i) * 128, i * 4, 4)
                }
                if (fib.pnPara < fib.pnFntb) {
                    nBinsPara = fib.pnFntb - fib.pnPara
                    plcfbtePapx = ByteArray(nBinsPara * 4)
                    for (i in 1 until nBinsPara)
                        plcfbtePapx!!.lazyRead((fib.pnPara + i) * 128, i * 4, 4)
                }
            }
        }
    }

    private fun makeBinTab() {
        fun parseBin(cb: Int, plcfbte: ByteArray, binTab: MutableList<FkPage>, s: Int): Int {
            val nBins = (cb - 4) / (4 + s)
            var fp = 0
            var pn = (nBins + 1) * 4
            for (i in 0 until nBins) {
                val filePos = if (i == 0) 0 else plcfbte.dword(fp)
                val pageNum = if (s == 4) plcfbte.dword(pn) else plcfbte.uword(pn)
                binTab.add(FkPage(pageNum, filePos))
                fp += 4
                pn += s
            }
            return nBins
        }

        when (version) {

            Version.WW8 -> {
                if (fib.cbPlcfbteChpx > 0) nBinsChar = parseBin(fib.cbPlcfbteChpx, plcfbteChpx!!, binTabChar, 4)
                if (fib.cbPlcfbtePapx > 0) nBinsPara = parseBin(fib.cbPlcfbtePapx, plcfbtePapx!!, binTabPara, 4)
            }

            Version.WW6 -> {
                if (fib.cbPlcfbteChpx > 0) nBinsChar = parseBin(fib.cbPlcfbteChpx, plcfbteChpx!!, binTabChar, 2)
                if (fib.cbPlcfbtePapx > 0) nBinsPara = parseBin(fib.cbPlcfbtePapx, plcfbtePapx!!, binTabPara, 2)
            }

            Version.WW2 -> {
                if (fib.cbPlcfbteChpx == 10 && fib.cbPlcfbtePapx == 10) {
                    val pnChar = plcfbteChpx!!.uword(8)
                    val pnPara = plcfbtePapx!!.uword(8)
                    val pnFntb = (fib.fcStshf + 511) / 512
                    if (pnChar + 1 != pnPara || pnPara + 1 != pnFntb) {
                        nBinsChar = pnPara - pnChar
                        plcfbteChpx = ByteArray(nBinsChar * 4)
                        for (i in 1 until nBinsChar)
                            plcfbteChpx!!.lazyRead((pnChar + i) * 512, i * 4, 4)
                        nBinsPara = pnFntb - pnPara
                        plcfbtePapx = ByteArray(nBinsPara * 4)
                        for (i in 1 until nBinsPara)
                            plcfbtePapx!!.lazyRead((pnPara + i) * 512, i * 4, 4)
                        doRead()
                        binTabChar.add(FkPage(pnChar, 0))
                        for (i in 1 until nBinsChar) {
                            val filePos = plcfbteChpx!!.dword(i * 4)
                            binTabChar.add(FkPage(pnChar + i, filePos))
                        }
                        binTabPara.add(FkPage(pnPara, 0))
                        for (i in 1 until nBinsPara) {
                            val filePos = plcfbtePapx!!.dword(i * 4)
                            binTabPara.add(FkPage(pnPara + i, filePos))
                        }
                        binTabPara.sortBy { it.filePos }
                        binTabChar.sortBy { it.filePos }
                        plcfbtePapx = null
                        plcfbteChpx = null
                        return
                    }
                }
                // else same as WW6
                if (fib.cbPlcfbteChpx > 0) nBinsChar = parseBin(fib.cbPlcfbteChpx, plcfbteChpx!!, binTabChar, 2)
                if (fib.cbPlcfbtePapx > 0) nBinsPara = parseBin(fib.cbPlcfbtePapx, plcfbtePapx!!, binTabPara, 2)
            }

            Version.DOS -> {
                val plcfbteChpx = plcfbteChpx
                if (plcfbteChpx != null) {
                    binTabChar.add(FkPage(fib.pnChar, 0))
                    for (i in 1 until nBinsChar) {
                        val filePos = plcfbteChpx.dword(i * 4)
                        binTabChar.add(FkPage(fib.pnChar + i, filePos))
                    }
                }
                val plcfbtePapx = plcfbtePapx
                if (plcfbtePapx != null) {
                    binTabPara.add(FkPage(fib.pnPara, 0))
                    for (i in 1 until nBinsPara) {
                        val filePos = plcfbtePapx.dword(i * 4)
                        binTabPara.add(FkPage(fib.pnPara + i, filePos))
                    }
                }
            }
        }

        binTabPara.sortBy { it.filePos }
        binTabChar.sortBy { it.filePos }
        plcfbtePapx = null
        plcfbteChpx = null
    }

    private fun loadFkp(tab: List<FkPage>, filePos: Int): ByteArray {
        val pos = filePos and 0x3FFFFFFF
        val n = tab.binarySearchBy(pos) { it.filePos }
        val fkp = if (n >= 0) tab[n] else tab[-n - 2]
        var buf: ByteArray? = fkp.buf.get()
        if (buf == null) {
            buf = ByteArray(FKP_SIZE).read(docStream + fkp.pageNum * FKP_SIZE)
            fkp.buf = SoftReference(buf)
        }
        return buf
    }

    private fun getCharPrm(filePos: Int): Prm {
        val fkp = loadFkp(binTabChar, filePos)
        val crun = fkp.ubyte(FKP_SIZE - 1)

        when (version) {

            Version.WW2, Version.WW6, Version.WW8 -> {
                for (i in 0 until crun) {
                    val limit = fkp.dword(i * 4 + 4)
                    if (filePos < limit) {
                        val start = fkp.dword(i * 4)
                        val idx = fkp.ubyte(crun * 4 + 4 + i)
                        if (idx > 0)
                            return Prm(start, limit, fkp, idx shl 1)
                        return Prm(start, limit, null, 0)
                    }
                }
            }

            Version.DOS -> {
                var start = fkp.dword(0)
                for (i in 0 until crun) {
                    val limit = fkp.dword(4 + i * 6)
                    if (filePos < limit) {
                        val idx = fkp.uword(8 + i * 6)
                        if (idx > 0 && idx < 0x7C)
                            return Prm(start, limit, fkp, idx + 4)
                        return Prm(start, limit, null, 0)
                    }
                    start = limit
                }
            }
        }

        return Prm(0, Integer.MAX_VALUE, null, 0)
    }

    private fun getParaPrm(filePos: Int): Prm {
        val fkp = loadFkp(binTabPara, filePos)
        val crun = fkp.ubyte(FKP_SIZE - 1)

        when (version) {

            Version.WW8 -> {
                for (i in 0 until crun) {
                    val limit = fkp.dword(4 + i * 4)
                    if (filePos < limit) {
                        val start = fkp.dword(i * 4)
                        val idx = fkp.ubyte(crun * 4 + 4 + i * 13)
                        return Prm(start, limit, if (idx > 0) fkp else null, idx shl 1)
                    }
                }
            }

            Version.WW6 -> {
                for (i in 0 until crun) {
                    val limit = fkp.dword(4 + i * 4)
                    if (filePos < limit) {
                        val start = fkp.dword(i * 4)
                        val idx = fkp.ubyte(crun * 4 + 4 + i * 7)
                        return Prm(start, limit, if (idx > 0) fkp else null, idx shl 1)
                    }
                }
            }

            Version.WW2 -> {
                for (i in 0 until crun) {
                    val limit = fkp.dword(4 + i * 4)
                    if (filePos < limit) {
                        val start = fkp.dword(i * 4)
                        val idx = fkp.ubyte(crun * 4 + 4 + i)
                        return Prm(start, limit, if (idx > 0) fkp else null, idx shl 1)
                    }
                }
            }

            Version.DOS -> {
                var start = fkp.dword(0)
                for (i in 0 until crun) {
                    val limit = fkp.dword(4 + i * 6)
                    if (filePos < limit) {
                        val idx = fkp.uword(8 + i * 6).let { if (it >= 0x7C) 0 else it }
                        return Prm(start, limit, if (idx > 0) fkp else null, if (idx > 0) idx + 4 else 0)
                    }
                    start = limit
                }
            }
        }

        return Prm(0, Integer.MAX_VALUE, null, 0)
    }

    //////////////////////////////////////////
    // links & notes
    //////////////////////////////////////////

    internal var FNREF: ByteArray? = null
    internal var FNTXT: ByteArray? = null
    internal var NAMES: ByteArray? = null
    @JvmField var bookmarks = HashMap<String, Int>(256)

    private class Note(var charPos: Int, var index: Int, var type: Int)
    private var notes: ArrayList<Note>? = null
    internal var nNotes = 0

    private fun readHrefs() {
        if (fib.cbPlcffndRef > 0 || fib.cbPlcfendRef > 0 || fib.cbPlcfandRef > 0) {
            FNREF = ByteArray(fib.cbPlcffndRef + fib.cbPlcfendRef + fib.cbPlcfandRef)
            if (fib.cbPlcffndRef > 0)
                FNREF!!.lazyRead(tabStream + fib.fcPlcffndRef, 0, fib.cbPlcffndRef)
            if (fib.cbPlcfendRef > 0)
                FNREF!!.lazyRead(tabStream + fib.fcPlcfendRef, fib.cbPlcffndRef, fib.cbPlcfendRef)
            if (fib.cbPlcfandRef > 0)
                FNREF!!.lazyRead(tabStream + fib.fcPlcfandRef, fib.cbPlcffndRef + fib.cbPlcfendRef, fib.cbPlcfandRef)
        }

        if (fib.cbPlcffndTxt > 0 || fib.cbPlcfendTxt > 0 || fib.cbPlcfandTxt > 0) {
            FNTXT = ByteArray(fib.cbPlcffndTxt + fib.cbPlcfendTxt + fib.cbPlcfandTxt)
            if (fib.cbPlcffndTxt > 0)
                FNTXT!!.lazyRead(tabStream + fib.fcPlcffndTxt, 0, fib.cbPlcffndTxt)
            if (fib.cbPlcfendTxt > 0)
                FNTXT!!.lazyRead(tabStream + fib.fcPlcfendTxt, fib.cbPlcffndTxt, fib.cbPlcfendTxt)
            if (fib.cbPlcfandTxt > 0)
                FNTXT!!.lazyRead(tabStream + fib.fcPlcfandTxt, fib.cbPlcffndTxt + fib.cbPlcfendTxt, fib.cbPlcfandTxt)
        }

        if (fib.cbSttbfbkmk > 0 || fib.cbPlcfbkf > 0) {
            NAMES = ByteArray(fib.cbSttbfbkmk + fib.cbPlcfbkf)
            NAMES!!.lazyRead(tabStream + fib.fcPlcfbkf, 0, fib.cbPlcfbkf)
            NAMES!!.lazyRead(tabStream + fib.fcSttbfbkmk, fib.cbPlcfbkf, fib.cbSttbfbkmk)
        }
    }

    private fun makeHrefs() {
        val NAMES = NAMES
        if (NAMES != null) {
            val max = (fib.cbPlcfbkf - 4) / (4 + 4)
            var pos = 0
            var str = fib.cbPlcfbkf
            var uni = false

            if (version == Version.WW8) {
                val wtmp = NAMES.uword(str)
                str += 2
                if (wtmp == 0xFFFF) {
                    str += 2
                    uni = true
                }
                str += 2
            } else {
                str += 2
            }

            val tabl: CharArray? = if (!uni) AlUnicode.getDataCP(codePage) else null
            val name = StringBuilder()
            try {
                for (i in 0 until max) {
                    val value = NAMES.dword(pos)
                    pos += 4
                    name.setLength(0)
                    if (!uni) {
                        var len = NAMES.ubyte(str++)
                        while (len-- > 0) {
                            var c = NAMES.ubyte(str++)
                            if (c >= 128) c = tabl!![c - 128].toInt()
                            name.append(c.toChar())
                        }
                    } else {
                        var len = NAMES.uword(str)
                        str += 2
                        while (len-- > 0) {
                            val c = NAMES.uword(str)
                            str += 2
                            name.append(c.toChar())
                        }
                    }
                    bookmarks.put(name.toString(), value)
                }

            } catch (e: Exception) {
            }
        }

        nNotes = 0
        if (fib.cbPlcffndRef > 0) nNotes += (fib.cbPlcffndRef - 4) / (4 + 2)
        if (fib.cbPlcfendRef > 0) nNotes += (fib.cbPlcfendRef - 4) / (4 + 2)
        if (fib.cbPlcfandRef > 0) nNotes += (fib.cbPlcfandRef - 4) / (4 + 30)
        if (fib.cbPlcffndTxt > 0) nNotes += (fib.cbPlcffndTxt - 4) / 4
        if (fib.cbPlcfendTxt > 0) nNotes += (fib.cbPlcfendTxt - 4) / 4
        if (fib.cbPlcfandTxt > 0) nNotes += (fib.cbPlcfandTxt - 4) / 4
        if (nNotes > 0) {
            notes = ArrayList<Note>(nNotes + 2)
            notes!!.add(Note(0, 0, 0))
        }

        if (fib.cbPlcffndRef > 0) {
            val max = (fib.cbPlcffndRef - 4) / (4 + 2)
            var fc = 0
            for (i in 0 until max) {
                val charPos = FNREF!!.dword(fc)
                notes!!.add(Note(charPos, i + 1, Format.FOOTREF))
                fc += 4
            }
        }
        if (fib.cbPlcfendRef > 0) {
            val max = (fib.cbPlcfendRef - 4) / (4 + 2)
            var fc = fib.cbPlcffndRef
            for (i in 0 until max) {
                val charPos = FNREF!!.dword(fc)
                notes!!.add(Note(charPos, i + 1, Format.ENDREF))
                fc += 4
            }
        }
        if (fib.cbPlcfandRef > 0) {
            val max = (fib.cbPlcfandRef - 4) / (4 + 30)
            var fc = fib.cbPlcffndRef + fib.cbPlcfendRef
            for (i in 0 until max) {
                val charPos = FNREF!!.dword(fc)
                notes!!.add(Note(charPos, i + 1, Format.ANNREF))
                fc += 4
            }
        }
        if (fib.cbPlcffndTxt > 0) {
            val max = (fib.cbPlcffndTxt - 4) / 4
            var fc = 0
            for (i in 0 until max) {
                val charPos = FNTXT!!.dword(fc) + fib.ccpText
                notes!!.add(Note(charPos, i + 1, Format.FOOTTEXT))
                fc += 4
            }
        }
        if (fib.cbPlcfendTxt > 0) {
            val max = (fib.cbPlcfendTxt - 4) / 4
            var fc = fib.cbPlcffndTxt
            for (i in 0 until max) {
                val charPos = FNTXT!!.dword(fc) + fib.ccpText + fib.ccpFtn + fib.ccpHdd + fib.ccpAtn
                notes!!.add(Note(charPos, i + 1, Format.ENDTEXT))
                fc += 4
            }
        }
        if (fib.cbPlcfandTxt > 0) {
            val max = (fib.cbPlcfandTxt - 4) / 4
            var fc = fib.cbPlcffndTxt + fib.cbPlcfendTxt
            for (i in 0 until max) {
                val charPos = FNTXT!!.dword(fc) + fib.ccpText + fib.ccpFtn + fib.ccpHdd
                notes!!.add(Note(charPos, i + 1, Format.ANNTEXT))
                fc += 4
            }
        }

        if (nNotes > 0) {
            notes!!.add(Note(0x1FFFFFFF, 0, 0))
            notes!!.sortBy { it.charPos }
        }

        this.FNREF = null
        this.FNTXT = null
        this.NAMES = null
    }

    private fun findNote(cp: Int): Note {
        val notes = notes
        if (notes == null) return Note(0, 0, 0)
        val n = notes.binarySearchBy(cp) { it.charPos }
        return notes[if (n >= 0) n else -n - 2]
    }

    //////////////////////////////////////////
    // stylesheet
    //////////////////////////////////////////

    internal var STSH: ByteArray? = null
    internal var stdbase = 0
    internal var nStyles = 0

    private class Style {
        var valueAnd: Long = 0
        var valueXor: Long = 0
        var colorAnd: Long = 0
        var colorXor: Long = 0
        var indentAnd: Long = 0
        var indentXor: Long = 0
        var linesAnd: Long = 0
        var linesXor: Long = 0
    }

    private lateinit var styles: ArrayList<Style>

    private fun Style.init(init0: Format, init1: Format) {
        this.valueAnd = init0.value xor init1.value
        this.valueXor = init0.value
        this.colorAnd = init0.color xor init1.color
        this.colorXor = init0.color
        this.indentAnd = init0.indent xor init1.indent
        this.indentXor = init0.indent
        this.linesAnd = init0.lines xor init1.lines
        this.linesXor = init0.lines
    }

    private fun Format.apply(style: Style) {
        this.value = this.value and style.valueAnd
        this.value = this.value xor style.valueXor
        this.color = this.color and style.colorAnd
        this.color = this.color xor style.colorXor
        this.indent = this.indent and style.indentAnd
        this.indent = this.indent xor style.indentXor
        this.lines = this.lines and style.linesAnd
        this.lines = this.lines xor style.linesXor
    }

    private fun Format.values(value: Long) {
        this.value = value
        this.color = value
        this.indent = value
        this.lines = value
    }

    private fun Format.values(other: Format) {
        this.value = other.value
        this.color = other.color
        this.indent = other.indent
        this.lines = other.lines
    }

    private fun readStyles() {
        if (fib.cbStshf > 0) {
            STSH = ByteArray(fib.cbStshf)
            STSH!!.lazyRead(tabStream + fib.fcStshf)
        }
    }

    private fun applyStylePrm(istd: Int) {
        val STSH = STSH
        if (STSH == null) return
        if (version == Version.WW2) {
            val stcp = istd + stdbase and 255
            var name = 4
            var chpx = name + STSH.uword(name - 2)
            var papx = chpx + STSH.uword(chpx - 2)
            val base = papx + STSH.uword(papx - 2)
            for (i in 0 until stcp) {
                if (STSH.ubyte(name) == 0xff) name++
                else name += STSH.ubyte(name) + 1
            }
            if (STSH.ubyte(name) == 0xff) return
            val based = STSH.ubyte(base + stcp * 2 + 1)
            if (based != 222) applyStylePrm(based)
            if (istd >= 246 && istd <= 254) format.level = 255 - istd
            for (i in 0 until stcp) {
                val len = STSH.ubyte(papx)
                papx += if (len == 0xff) 1 else 1 + len
            }
            if (STSH.ubyte(papx) != 0xff) applyGrpPrl(STSH, papx + 8, STSH.sbyte(papx - 7))
            for (i in 0 until stcp) {
                val len = STSH.ubyte(chpx)
                chpx += if (len == 0xff) 1 else 1 + len
            }
            if (STSH.ubyte(chpx) != 0xff) applyCharPrm(STSH, chpx)
        } else {
            var pos = STSH.uword(0) + 2
            for (i in 0 until istd) pos += STSH.uword(pos) + 2
            if (STSH.uword(pos) == 0) return
            val sgc = STSH.ubyte(pos + 4) and 0xf
            val based = STSH.uword(pos + 4) shr 4
            if (based != 4095) applyStylePrm(based)
            pos += 2 + stdbase
            when (version) {
                Version.WW6 -> pos += 2 + STSH.ubyte(pos)
                Version.WW8 -> pos += 4 + STSH.uword(pos) * 2
            }
            if (istd >= 1 && istd <= 9) format.level = istd
            if (pos and 1 != 0) pos++
            if (sgc == 1) {
                val len = STSH.uword(pos)
                applyGrpPrl(STSH, pos + 4, len - 2)
                pos += 2 + len
            }
            if (pos and 1 != 0) pos++
            if (sgc == 1 || sgc == 2) {
                val len = STSH.uword(pos)
                applyGrpPrl(STSH, pos + 2, len)
                pos += 2 + len
            }
        }

        style.values(format)
    }

    private fun makeStyles() {
        val STSH = STSH
        if (STSH == null) return

        when (version) {

            Version.WW6, Version.WW8 -> {
                stdbase = STSH.uword(4)
                nStyles = STSH.uword(2)
                styles = ArrayList<Style>(nStyles)
                for (i in 0 until nStyles) styles.add(Style())
            }

            Version.WW2 -> {
                stdbase = STSH.uword(0)
                var pos = 2
                for (i in 0..2) pos += STSH.uword(pos)
                nStyles = STSH.uword(pos)
                styles = ArrayList<Style>(256)
                for (i in 0 until 255) styles.add(Style())
            }
        }

        val init0 = Format()
        val init1 = Format()
        for (i in 0 until nStyles) {
            val istd = if (version == Version.WW2) ((i - stdbase) and 0xff) else (i and 0xffff)

            style.values(0)
            format.values(0)
            applyStylePrm(istd)
            init0.values(format)

            style.values(-1)
            format.values(-1)
            applyStylePrm(istd)
            init1.values(format)

            try {
                styles[istd].init(init0, init1)
            } catch (e: Exception) {
            }
        }

        style.values(0)
        format.values(0)
        this.STSH = null
    }

    private fun applyStyle(istd: Int) {
        try {
            format.apply(styles[istd])
            style.values(format)
        } catch (e: Exception) {
        }
    }

    //////////////////////////////////////////
    // property modifier
    //////////////////////////////////////////

    private fun applySprm(code: Int, param: ByteArray, pos: Int) {

        when (code) {

            sprmPJc80, sprmPJc -> when (param.ubyte(pos)) {
                0 -> format.align = Format.LEFT
                1 -> format.align = Format.CENTER
                2 -> format.align = Format.RIGHT
                3 -> format.align = Format.JUSTIFY
            }

            sprmPDxaLeft, sprmPDxaLeft80, sprmPNest, sprmPNest80 ->
                format.indLeft = param.uword(pos)

            sprmPDxaLeft1, sprmPDxaLeft180 ->
                format.indFirst = param.uword(pos)

            sprmPDxaRight, sprmPDxaRight80 ->
                format.indRight = param.uword(pos)

            sprmPDyaBefore ->
                format.indBefore = param.uword(pos)

            sprmPDyaAfter ->
                format.indAfter = param.uword(pos)

            sprmPFKeep ->
                format.keepLines = (param.ubyte(pos) != 0)

            sprmPFKeepFollow ->
                format.keepFollow = (param.ubyte(pos) != 0)

            sprmPFPageBreakBefore ->
                format.pageBreakBefore = (param.ubyte(pos) != 0)

            sprmPFNoAutoHyph ->
                format.noHyph = (param.ubyte(pos) != 0)

            sprmPOutLvl ->
                format.level = param.ubyte(pos).let { if (it < 9) it + 1 else 0 }

            sprmCIstd ->
                applyStyle(param.uword(pos))

            sprmCDefault80, sprmCDefault -> {
                format.bold = false
                format.italic = false
                format.strike = false
                format.under = false
            }

            sprmCPlain -> {
                format.bold = style.bold
                format.italic = style.italic
                format.strike = style.strike
                format.under = style.under
                format.sub = style.sub
                format.sup = style.sup
            }

            sprmCFBold -> when (param.ubyte(pos)) {
                0x01 -> format.bold = true
                0x00 -> format.bold = false
                0x80 -> format.bold = style.bold
                0x81 -> format.bold = !style.bold
            }

            sprmCFItalic -> when (param.ubyte(pos)) {
                0x01 -> format.italic = true
                0x00 -> format.italic = false
                0x80 -> format.italic = style.italic
                0x81 -> format.italic = !style.italic
            }

            sprmCFStrike -> when (param.ubyte(pos)) {
                0x01 -> format.strike = true
                0x00 -> format.strike = false
                0x80 -> format.strike = style.strike
                0x81 -> format.strike = !style.strike
            }

            sprmCFDStrike -> when (param.ubyte(pos)) {
                0x01 -> format.dstrike = true
                0x00 -> format.dstrike = false
                0x80 -> format.dstrike = style.dstrike
                0x81 -> format.dstrike = !style.dstrike
            }

            sprmCFVanish -> when (param.ubyte(pos)) {
                0x01 -> format.hidden = true
                0x00 -> format.hidden = false
                0x80 -> format.hidden = style.hidden
                0x81 -> format.hidden = !style.hidden
            }

            sprmCKul ->
                format.under = (param.ubyte(pos) != 0)

            sprmCFCaps -> when (param.ubyte(pos)) {
                0x01 -> format.caps = true
                0x00 -> format.caps = false
                0x80 -> format.caps = style.caps
                0x81 -> format.caps = !style.caps
            }

            sprmCFSmallCaps -> when (param.ubyte(pos)) {
                0x01 -> format.smallCaps = true
                0x00 -> format.smallCaps = false
                0x80 -> format.smallCaps = style.smallCaps
                0x81 -> format.smallCaps = !style.smallCaps
            }

            sprmCIss -> when (param.ubyte(pos)) {
                1 -> format.sup = true
                2 -> format.sub = true
            }

            sprmCHps, sprmCHpsBi ->
                format.fontSize = param.uword(pos)

            sprmCCharScale ->
                format.fontWidth = param.uword(pos)

            sprmCDxaSpace ->
                format.fontSpacing = param.uword(pos)

            sprmCCv -> {
                val r = param.ubyte(pos) and 0xff
                val g = param.ubyte(pos + 1) and 0xff
                val b = param.ubyte(pos + 2) and 0xff
                val a = param.ubyte(pos + 3) and 0xff xor 0xff
                format.fontColor = (a shl 24) or (r shl 16) or (g shl 8) or (b)
            }

            sprmCIco, sprmCIcoBi ->
                format.fontColor = colorPalette(param.ubyte(pos))

            sprmCHighlight ->
                format.bgColor = colorPalette(param.ubyte(pos))

            sprmCFSpec ->
                format.special = if (param.ubyte(pos) != 0) Format.SPECIAL else Format.NORMAL

            sprmCPicLocation06 -> if (param.ubyte(pos) == 4) {
                format.special = Format.SPECIAL
                format.xdata = param.dword(pos + 1)
            }

            sprmCPicLocation -> {
                format.special = Format.SPECIAL
                format.xdata = param.dword(pos)
            }

            sprmCObjLocation ->
                format.xdata = param.dword(pos)

            sprmCFObj ->
                format.obj = (param.ubyte(pos) != 0)

            sprmCFOle2 ->
                format.ole2 = (param.ubyte(pos) != 0)
        }
    }

    private fun colorPalette(param: Int) = when (param) {
        0x01 -> 0xFF000000.toInt()
        0x02 -> 0xFF0000FF.toInt()
        0x03 -> 0xFF00FFFF.toInt()
        0x04 -> 0xFF00FF00.toInt()
        0x05 -> 0xFFFF00FF.toInt()
        0x06 -> 0xFFFF0000.toInt()
        0x07 -> 0xFFFFFF00.toInt()
        0x08 -> 0xFFFFFFFF.toInt()
        0x09 -> 0xFF000080.toInt()
        0x0A -> 0xFF008080.toInt()
        0x0B -> 0xFF008000.toInt()
        0x0C -> 0xFF800080.toInt()
        0x0D -> 0xFF800080.toInt()
        0x0E -> 0xFF808000.toInt()
        0x0F -> 0xFF808080.toInt()
        0x10 -> 0xFFC0C0C0.toInt()
        else -> 0
    }

    // транслиовать sprm в формат word97
    private fun translateSprm(code: Int): Int {

        when (version) {

            Version.WW8 -> when (code) {
                12 -> return sprmPIlvl
                120 -> return sprmPOutLvl
                in 0..127 -> return sprmTable[code]
            }

            Version.WW6 -> when (code) {
                in 0..200 -> return sprmTable[code]
            }

            Version.WW2 -> when (code) {
                2 -> return sprmPIstd20
                20 -> return sprmPDyaLine20
                in 0..52 -> return sprmTable[code]
                in 53..56 -> return sprmTable[code + 12]
                in 57..145 -> return sprmTable[code + 25]
                in 146..164 -> return sprmTable[code + 36]
            }
        }

        return 0
    }

    /* перейти к следующему sprm */
    private fun skipSprm(code: Int, param: ByteArray, pos: Int): Int {

        when (code and 0xE000) {
            0x0000, 0x2000 -> return pos + 1
            0x4000, 0x8000, 0xA000 -> return pos + 2
            0x6000 -> return pos + 4
            0xE000 -> return pos + 3
            0xC000 -> when (code) {
                sprmTTableBorders80, sprmTTableBorders ->
                    return if (version == Version.WW6) pos + 12 else pos + 1 + param.ubyte(pos)
                sprmTDefTable10, sprmTDefTable ->
                    return pos + 1 + param.uword(pos)
                sprmPChgTabs -> {
                    if (param.ubyte(pos) == 0xff) {
                        var ret = pos + 1
                        ret += param.ubyte(ret) * 4
                        ret += param.ubyte(ret) * 3
                        return ret
                    }
                    return pos + 1 + param.ubyte(pos)
                }
                else ->
                    return pos + 1 + param.ubyte(pos)
            }
        }
        return pos
    }

    private fun applyGrpPrl(prm: ByteArray, pos: Int, len: Int) {
        var pos = pos
        val limit = pos + len

        when (version) {

            Version.WW8 -> {
                while (pos + 1 < limit) {
                    val code = prm.uword(pos)
                    pos += 2
                    applySprm(code, prm, pos)
                    pos = skipSprm(code, prm, pos)
                }
            }

            else -> {
                while (pos < limit) {
                    var code = prm.ubyte(pos++)
                    code = translateSprm(code)
                    applySprm(code, prm, pos)
                    pos = skipSprm(code, prm, pos)
                }
            }
        }
    }

    private fun applyCharPrm(prm: ByteArray?, pos: Int) {
        if (prm == null) return

        when (version) {

            Version.WW8, Version.WW6 -> {
                val size = prm.sbyte(pos)
                applyGrpPrl(prm, pos + 1, size)
            }

            Version.WW2 -> {
                val size = prm.ubyte(pos)
                if (size >= 1) {
                    if ((prm.ubyte(pos + 1) and 0x01) != 0)
                        format.bold = !style.bold
                    if ((prm.ubyte(pos + 1) and 0x02) != 0)
                        format.italic = !style.italic
                    if ((prm.ubyte(pos + 1) and 0x04) != 0)
                        format.strike = !style.strike
                }
                if (size >= 2) {
                    if ((prm.ubyte(pos + 2) and 0x02) != 0)
                        format.special = if (style.special == 0) 1 else 0
                    if ((prm.ubyte(pos + 2) and 0x04) != 0)
                        format.strike = !style.strike
                    if ((prm.ubyte(pos + 2) and 0x08) != 0)
                        format.obj = !style.obj
                }
            }

            Version.DOS -> {
                val size = prm.ubyte(pos)
                if (size >= 1 && prm.ubyte(pos + 1) and 1 != 0)
                    when (prm.ubyte(pos + 1) shr 1) {
                        13 -> format.special = Format.FOOTREF
                        19, 28, 29 -> format.special = Format.SPECIAL
                    }
                if (size >= 2) {
                    if (prm.ubyte(pos + 2) and 1 != 0)
                        format.bold = true
                    if (prm.ubyte(pos + 2) and 2 != 0)
                        format.italic = true
                }
                if (size >= 4) {
                    if (prm.ubyte(pos + 4) and 1 != 0)
                        format.under = true
                    if (prm.ubyte(pos + 4) and 2 != 0)
                        format.strike = true
                }
                if (size >= 6) {
                    if (prm.ubyte(pos + 6) != 0 && prm.ubyte(pos + 6) and 0x80 == 0)
                        format.sup = true
                    else if (prm.ubyte(pos + 6) and 0x80 != 0)
                        format.sub = true
                }
            }
        }
    }

    private fun applyParaPrm(prm: ByteArray?, pos: Int) {
        var pos = pos
        if (prm == null) return

        when (version) {

            Version.WW8, Version.WW6 -> {
                var size = prm.sbyte(pos++)
                if (size == 0) size = prm.ubyte(pos++)
                val istd = prm.uword(pos)
                if (istd != 4095)
                    applyStyle(istd)
                size = (size shl 1) - 2
                if (size > 0)
                    applyGrpPrl(prm, pos + 2, size)
            }

            Version.WW2 -> {
                var size = prm.sbyte(pos++)
                val istd = prm.ubyte(pos)
                if (istd != 222)
                    applyStyle(istd)
                size = (size shl 1) - 7
                if (size > 0)
                    applyGrpPrl(prm, pos + 7, size)
            }

            Version.DOS -> {
                val size = prm.ubyte(pos)
                if (size >= 1 && (prm.ubyte(pos + 1) and 1) != 0)
                    when (prm.ubyte(pos + 1) shr 1) {
                        39 -> format.special = Format.FOOTTEXT
                    }
                var align = 0
                if (size >= 2) align = prm.ubyte(pos + 2) and 3
                when (align) {
                    0 -> format.align = Format.LEFT
                    1 -> format.align = Format.CENTER
                    2 -> format.align = Format.RIGHT
                    3 -> format.align = Format.JUSTIFY
                }
                if (size >= 4) {
                    val level = prm.ubyte(pos + 4) and 0x7F
                    if (level < 9) format.level = level
                }
                if (size >= 17) {
                    if ((prm.ubyte(pos + 17) and 0xF) != 0) {
                        format.special = Format.SPECIAL
                    }
                    if (piece[0].codepage == 1251 && prm.ubyte(pos + 17) and 0x10 != 0) {
                        format.special = Format.SPECIAL
                        format.xdata = 0
                    }
                }
            }
        }
    }

    private fun applyPiecePrm(sprm: Int) {
        if (sprm == 0) return
        if ((sprm and 1) == 0) {
            val code = translateSprm((sprm shr 1) and 0x7f)
            val param = byteArrayOf((sprm shr 8).toByte(), 0, 0, 0)
            applySprm(code, param, 0)
        } else {
            var idx = sprm shr 1
            val CLX = CLX
            if (CLX == null) return
            var pos = 0
            while (CLX.ubyte(pos++) == 1) {
                if (idx-- != 0) {
                    pos += 2 + CLX.uword(pos)
                } else {
                    val len = CLX.uword(pos)
                    applyGrpPrl(CLX, pos + 2, len)
                }
            }
        }
    }

    //////////////////////////////////////////
    // images
    //////////////////////////////////////////

    private fun getWmf(pos: Int, end: Int, ai: AlOneImage): Boolean {
        var pos = pos
        val buf = ByteArray(18).read(pos)
        if (buf.dword(0) != 0x90001 || buf.uword(4) != 0x300) return false
        val len = buf.dword(12) * 2
        var recLen = 18
        while ((pos + recLen) < end) {
            pos += recLen
            buf.read(pos, 6)
            recLen = buf.dword(0) * 2
            val type = buf.uword(4)
            if (type != 0xF43 || recLen != len) continue
            buf.read(pos + 6, 6)
            if (buf.dword(0) != 0xCC0020 || buf.uword(4) != 0) return false
            ai.positionS = pos + 14 or 0x80000000.toInt()
            ai.positionE = len - 14
            ai.iType = AlOneImage.IMG_MEMO//BMP;
            return true
        }
        return false
    }

    private fun getPicf(pos: Int, end: Int, ai: AlOneImage): Boolean {
        var pos = pos
        val buf = ByteArray(36)
        var recLen = 0
        while ((pos + recLen) < end) {
            pos += recLen
            buf.read(pos, 8)
            recLen = buf.dword(4) + 8
            if (recLen <= 8) break
            var type = buf.uword(2)
            if (type != 0xF007) continue
            pos += 8
            buf.read(pos, 36)
            val len = buf.ubyte(33)
            pos += 36 + len
            buf.read(pos, 8)
            recLen = buf.dword(4)
            val head = 16 + (buf.ubyte(0) and 0x10) + 1
            type = buf.uword(2)
            when (type) {
                0xF01F, // BMP
                0xF01E, // PNG
                0xF01D, // JPG
                //0xF01A, // EMF
                //0xF01B, // WMF
                0xF02A, // JPG
                0xF029 -> { // TIFF
                    ai.positionS = pos + 8 + head
                    ai.positionE = recLen - head
                    ai.iType = AlOneImage.IMG_MEMO
                    return true
                }
            }
            return false
        }
        return false
    }

    fun getExternalImage(ai: AlOneImage): Boolean {
        val format = Format()

        try {
            val tokens = ai.name!!.split("[_]+".toRegex()).dropLastWhile { it.isEmpty() }
            format.xdata = tokens[0].toInt()
            format.value = tokens[1].toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        try {
            if (version == Version.DOS) return false //WRI: todo
            if (format.ole2) return false //OLE2: todo
            if (format.obj) return false //OLE1: todo
            if (datStream + format.xdata < 0) return false

            val buf = ByteArray(10).read(datStream + format.xdata, 10)
            val lcb = buf.dword(0)
            val hdr = buf.uword(4)
            val mm = buf.uword(6)
            if (hdr != (if (version == Version.WW8) 68 else 58)) return false
            var rec = datStream + format.xdata + hdr
            val end = datStream + format.xdata + lcb
            when (mm) {
                8 -> {
                    if (getWmf(rec, end, ai)) addFile2List(ai)
                    return true
                }
                102 -> {
                    rec += 1 + buf.ubyte(8)
                    if (getPicf(rec, end, ai)) addFile2List(ai)
                    return true
                }
                100 -> {
                    if (getPicf(rec, end, ai)) addFile2List(ai)
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    private fun addFile2List(ai: AlOneImage) {
        val of = AlFileZipEntry()
        of.compress = 0
        of.cSize = ai.positionS
        of.uSize = ai.positionE
        of.flag = 0
        of.position = 0
        of.time = 0
        of.name = ai.name
        fileList.add(of)

        mapFile[of.name] = fileList.size - 1
    }

    private fun fillExternalFile(index: Int, dst: ByteArray, len: Int): Boolean {
        try {
            val pos = index and 0x7fffffff
            dst.read(pos, len)
            if ((index and 0x80000000.toInt()) != 0) {
                dst[0] = 'B'.toByte()
                dst[1] = 'M'.toByte()
                dst[2] = (len and 0xff).toByte()
                dst[3] = (len shr 8 and 0xff).toByte()
                dst[4] = (len shr 16 and 0xff).toByte()
                dst[5] = (len shr 24 and 0xff).toByte()
                dst[6] = 0
                dst[7] = 0
                dst[8] = 0
                dst[9] = 0
                val bitCount = dst.uword(0x1c)
                val compression = dst.dword(0x1e)
                val clrUsed = dst.dword(0x2e)
                var offBits = 0x36
                if (clrUsed != 0) {
                    offBits += clrUsed * 4
                } else
                    when (bitCount) {
                        1 -> offBits += 8
                        4 -> offBits += 64
                        8 -> offBits += 1024
                        16, 32 -> if (compression == 3)
                            offBits += 12
                    }
                dst[10] = (offBits and 0xff).toByte()
                dst[11] = (offBits shr 8 and 0xff).toByte()
                dst[12] = (offBits shr 16 and 0xff).toByte()
                dst[13] = (offBits shr 24 and 0xff).toByte()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

    override fun fillBufFromExternalFile(num: Int, pos: Int, dst: ByteArray, dst_pos: Int, cnt: Int): Boolean {
        val res = 0
        if (num >= 0 && num < fileList.size && pos == 0) {
            if (fillExternalFile(fileList[num].cSize, dst, cnt))
                return true
        }
        return false
    }
}

// Paragraph sprms
private const val sprmPIstd                 = 0x4600
private const val sprmPIstd20               = 0x2400
private const val sprmPIstdPermute          = 0xC601
private const val sprmPIncLvl               = 0x2602
private const val sprmPJc                   = 0x2461
private const val sprmPJc80                 = 0x2403
private const val sprmPFSideBySide          = 0x2404
private const val sprmPFKeep                = 0x2405
private const val sprmPFKeepFollow          = 0x2406
private const val sprmPFPageBreakBefore     = 0x2407
private const val sprmPBrcl                 = 0x2408
private const val sprmPBrcp                 = 0x2409
private const val sprmPAnld                 = 0xC63E
private const val sprmPNLvlAnm              = 0x25FF
private const val sprmPIlvl                 = 0x260A
private const val sprmPIlfo                 = 0x460B
private const val sprmPFNoLineNumb          = 0x240C
private const val sprmPChgTabsPapx          = 0xC60D
private const val sprmPDxaLeft              = 0x845E
private const val sprmPDxaLeft80            = 0x840F
private const val sprmPDxaLeft1             = 0x8460
private const val sprmPDxaLeft180           = 0x8411
private const val sprmPDxaRight             = 0x845D
private const val sprmPDxaRight80           = 0x840E
private const val sprmPDxcLeft              = 0x4456
private const val sprmPDxcLeft1             = 0x4457
private const val sprmPDxcRight             = 0x4455
private const val sprmPNest                 = 0x465F
private const val sprmPNest80               = 0x4610
private const val sprmPDyaLine              = 0x6412
private const val sprmPDyaLine20            = 0x4412
private const val sprmPDyaBefore            = 0xA413
private const val sprmPDyaAfter             = 0xA414
private const val sprmPFDyaAfterAuto        = 0x245C
private const val sprmPFDyaBeforeAuto       = 0x245B
private const val sprmPDylAfter             = 0x4459
private const val sprmPDylBefore            = 0x4458
private const val sprmPChgTabs              = 0xC615
private const val sprmPFInTable             = 0x2416
private const val sprmPTtp                  = 0x2417
private const val sprmPDxaAbs               = 0x8418
private const val sprmPDyaAbs               = 0x8419
private const val sprmPDxaWidth             = 0x841A
private const val sprmPPc                   = 0x261B
private const val sprmPBrcTop10             = 0x461C
private const val sprmPBrcLeft10            = 0x461D
private const val sprmPBrcBottom10          = 0x461E
private const val sprmPBrcRight10           = 0x461F
private const val sprmPBrcBetween10         = 0x4620
private const val sprmPBrcBar10             = 0x4621
private const val sprmPFromText10           = 0x4622
private const val sprmPWr                   = 0x2423
private const val sprmPBrcBar               = 0xC653
private const val sprmPBrcBar70             = 0x4629
private const val sprmPBrcBar80             = 0x6629
private const val sprmPBrcBetween           = 0xC652
private const val sprmPBrcBetween70         = 0x4428
private const val sprmPBrcBetween80         = 0x6428
private const val sprmPBrcBottom            = 0xC650
private const val sprmPBrcBottom70          = 0x4426
private const val sprmPBrcBottom80          = 0x6426
private const val sprmPBrcLeft              = 0xC64F
private const val sprmPBrcLeft70            = 0x4425
private const val sprmPBrcLeft80            = 0x6425
private const val sprmPBrcRight             = 0xC651
private const val sprmPBrcRight70           = 0x4427
private const val sprmPBrcRight80           = 0x6427
private const val sprmPBrcTop               = 0xC64E
private const val sprmPBrcTop70             = 0x4424
private const val sprmPBrcTop80             = 0x6424
private const val sprmPFNoAutoHyph          = 0x242A
private const val sprmPWHeightAbs           = 0x442B
private const val sprmPDcs                  = 0x442C
private const val sprmPShd80                = 0x442D
private const val sprmPShd                  = 0xC64D
private const val sprmPDyaFromText          = 0x842E
private const val sprmPDxaFromText          = 0x842F
private const val sprmPFLocked              = 0x2430
private const val sprmPFWidowControl        = 0x2431
private const val sprmPRuler                = 0xC632
private const val sprmPFKinsoku             = 0x2433
private const val sprmPFWordWrap            = 0x2434
private const val sprmPFOverflowPunct       = 0x2435
private const val sprmPFTopLinePunct        = 0x2436
private const val sprmPFAutoSpaceDE         = 0x2437
private const val sprmPFAutoSpaceDN         = 0x2438
private const val sprmPWAlignFont           = 0x4439
private const val sprmPFrameTextFlow        = 0x443A
private const val sprmPISnapBaseLine        = 0x243B
private const val sprmPAnld80               = 0xC63E
private const val sprmPAnldCv               = 0x6654
private const val sprmPPropRMark            = 0xC63F
private const val sprmPOutLvl               = 0x2640
private const val sprmPFBiDi                = 0x2441
private const val sprmPFNumRMIns            = 0x2443
private const val sprmPNumRM                = 0xC645
private const val sprmPHugePapx             = 0x6645
private const val sprmPFUsePgsuSettings     = 0x2447
private const val sprmPFAdjustRight         = 0x2448
private const val sprmPDtap                 = 0x664A
private const val sprmPFInnerTableCell      = 0x244B
private const val sprmPFInnerTtp            = 0x244C
private const val sprmPFNoAllowOverlap      = 0x2462
private const val sprmPItap                 = 0x6649
private const val sprmPWall                 = 0x2664
private const val sprmPIpgp                 = 0x6465
private const val sprmPCnf                  = 0xC666
private const val sprmPRsid                 = 0x6467
private const val sprmPIstdList             = 0x4468
private const val sprmPIstdListPermute      = 0xC669
private const val sprmPDyaBeforeNotCp0      = 0xA46A
private const val sprmPTableProps           = 0x646B
private const val sprmPTIstdInfo            = 0xC66C
private const val sprmPFContextualSpacing   = 0x246D
private const val sprmPRpf                  = 0x246E
private const val sprmPPropRMark90          = 0xC66F

// Character sprms
private const val sprmCFRMarkDel            = 0x0800
private const val sprmCFRMark               = 0x0801
private const val sprmCFFldVanish           = 0x0802
private const val sprmCFSdtVanish           = 0x2A90
private const val sprmCPicLocation06        = 0xCA03
private const val sprmCPicLocation          = 0x6A03
private const val sprmCIbstRMark            = 0x4804
private const val sprmCDttmRMark            = 0x6805
private const val sprmCFData                = 0x0806
private const val sprmCIdslRMark            = 0x4807
private const val sprmCChs                  = 0xEA08
private const val sprmCSymbol               = 0xCA09
private const val sprmCFOle2                = 0x080A
private const val sprmCIdCharType           = 0x480B
private const val sprmCHighlight            = 0x2A0C
private const val sprmCObjLocation          = 0x680E
private const val sprmCFFtcAsciSymb         = 0x2A10
private const val sprmCRgFtc0               = 0x4A4F
private const val sprmCRgFtc1               = 0x4A50
private const val sprmCRgFtc2               = 0x4A51
private const val sprmCCharScale            = 0x4852
private const val sprmCPropRMark1           = 0xCA57
private const val sprmCFEmboss              = 0x0858
private const val sprmCSfxText              = 0x2859
private const val sprmCIstd                 = 0x4A30
private const val sprmCIstdPermute          = 0xCA31
private const val sprmCDefault              = 0xCA32
private const val sprmCDefault80            = 0x2A32
private const val sprmCPlain                = 0x2A33
private const val sprmCKcd                  = 0x2A34
private const val sprmCFBold                = 0x0835
private const val sprmCFItalic              = 0x0836
private const val sprmCFStrike              = 0x0837
private const val sprmCFOutline             = 0x0838
private const val sprmCFShadow              = 0x0839
private const val sprmCFSmallCaps           = 0x083A
private const val sprmCFCaps                = 0x083B
private const val sprmCFVanish              = 0x083C
private const val sprmCFtcDefault           = 0x4A3D
private const val sprmCKul                  = 0x2A3E
private const val sprmCSizePos              = 0xEA3F
private const val sprmCDxaSpace             = 0x8840
private const val sprmCLid                  = 0x4A41
private const val sprmCIco                  = 0x2A42
private const val sprmCHps                  = 0x4A43
private const val sprmCHpsInc               = 0x2A44
private const val sprmCHpsPos               = 0x2845
private const val sprmCHpsPosAdj            = 0x2A46
private const val sprmCMajority             = 0xCA47
private const val sprmCIss                  = 0x2A48
private const val sprmCHpsNew50             = 0xCA49
private const val sprmCHpsInc1              = 0xCA4A
private const val sprmCHpsKern              = 0x484B
private const val sprmCMajority50           = 0xCA4C
private const val sprmCHpsMul               = 0x4A4D
private const val sprmCHresi                = 0x484E
private const val sprmCFDStrike             = 0x2A53
private const val sprmCFImprint             = 0x0854
private const val sprmCFSpec                = 0x0855
private const val sprmCFObj                 = 0x0856
private const val sprmCFBiDi                = 0x085A
private const val sprmCFDiacColor           = 0x085B
private const val sprmCFBoldBi              = 0x085C
private const val sprmCFItalicBi            = 0x085D
private const val sprmCFtcBi                = 0x4A5E
private const val sprmCLidBi                = 0x485F
private const val sprmCIcoBi                = 0x4A60
private const val sprmCHpsBi                = 0x4A61
private const val sprmCDispFldRMark         = 0xCA62
private const val sprmCIbstRMarkDel         = 0x4863
private const val sprmCDttmRMarkDel         = 0x6864
private const val sprmCBrc80                = 0x6865
private const val sprmCBrc                  = 0xCA72
private const val sprmCShd80                = 0x4866
private const val sprmCShd                  = 0xCA71
private const val sprmCIdslRMarkDel         = 0x4867
private const val sprmCFUsePgsuSettings     = 0x0868
private const val sprmCCpg                  = 0x486B
private const val sprmCRgLid0_80            = 0x486D
private const val sprmCRgLid0               = 0x4873
private const val sprmCRgLid1_80            = 0x486E
private const val sprmCRgLid1               = 0x4874
private const val sprmCIdctHint             = 0x286F
private const val sprmCCv                   = 0x6870
private const val sprmCCvPermute            = 0xCA7C
private const val sprmCCvUl                 = 0x6877
private const val sprmCFBoldPresent         = 0x287F
private const val sprmCFELayout             = 0xCA78
private const val sprmCFItalicPresent       = 0x287F
private const val sprmCFitText              = 0xCA76
private const val sprmCFLangApplied         = 0x2A7A
private const val sprmCFNoProof             = 0x0875
private const val sprmCFWebHidde            = 0x0811
private const val sprmCHsp                  = 0x6A12
private const val sprmCLbcCRJ               = 0x2879
private const val sprmCNewIbstRM            = 0xCA13
private const val sprmCTransNoProof0        = 0x287F
private const val sprmCTransNoProof1        = 0x2880
private const val sprmCFRMMove              = 0x2814
private const val sprmCRsidProp             = 0x6815
private const val sprmCRsidText             = 0x6816
private const val sprmCRsidRMDel            = 0x6817
private const val sprmCFSpecVanish          = 0x0818
private const val sprmCFComplexScripts      = 0x0882
private const val sprmCWall                 = 0x2A83
private const val sprmCPbi                  = 0xCA84
private const val sprmCCnf                  = 0xCA85
private const val sprmCNeedFontFixup        = 0x2A86
private const val sprmCPbiIBullet           = 0x6887
private const val sprmCPbiGrf               = 0x4888
private const val sprmCPropRMark2           = 0xCA89

//Picture sprms
private const val sprmPicBrcl               = 0x2E00
private const val sprmPicScale              = 0xCE01
private const val sprmPicBrcTop80           = 0x6C02
private const val sprmPicBrcBottom          = 0xCE0A
private const val sprmPicBrcBottom70        = 0x4C04
private const val sprmPicBrcLeft80          = 0x6C03
private const val sprmPicBrcLeft            = 0xCE09
private const val sprmPicBrcLeft70          = 0x4C03
private const val sprmPicBrcBottom80        = 0x6C04
private const val sprmPicBrcRight           = 0xCE0B
private const val sprmPicBrcRight70         = 0x4C05
private const val sprmPicBrcRight80         = 0x6C05
private const val sprmPicBrcTop             = 0xCE08
private const val sprmPicBrcTop70           = 0x4C02
private const val sprmPicSpare4             = 0xCE06
private const val sprmCFOle2WasHere         = 0xCE07

// Section sprms
private const val sprmScnsPgn               = 0x3000
private const val sprmSiHeadingPgn          = 0x3001
private const val sprmSOlstAnm              = 0xD202
private const val sprmSOlstAnm80            = 0xD202
private const val sprmSOlstCv               = 0xD238
private const val sprmSDxaColWidth          = 0xF203
private const val sprmSDxaColSpacing        = 0xF204
private const val sprmSFEvenlySpaced        = 0x3005
private const val sprmSFProtected           = 0x3006
private const val sprmSDmBinFirst           = 0x5007
private const val sprmSDmBinOther           = 0x5008
private const val sprmSBkc                  = 0x3009
private const val sprmSFTitlePage           = 0x300A
private const val sprmSCcolumns             = 0x500B
private const val sprmSDxaColumns           = 0x900C
private const val sprmSFAutoPgn             = 0x300D
private const val sprmSNfcPgn               = 0x300E
private const val sprmSDyaPgn               = 0xB00F
private const val sprmSDxaPgn               = 0xB010
private const val sprmSFPgnRestart          = 0x3011
private const val sprmSFEndnote             = 0x3012
private const val sprmSLnc                  = 0x3013
private const val sprmSGprfIhdt             = 0x3014
private const val sprmSNLnnMod              = 0x500B
private const val sprmSDxaLnn               = 0x9016
private const val sprmSDyaHdrTop            = 0xB017
private const val sprmSDyaHdrBottom         = 0xB018
private const val sprmSLBetween             = 0x3019
private const val sprmSVjc                  = 0x301A
private const val sprmSLnnMin               = 0x501B
private const val sprmSPgnStart             = 0x501C
private const val sprmSBOrientation         = 0x301D
private const val SprmSBCustomize           = 0x301E
private const val sprmSXaPage               = 0xB01F
private const val sprmSYaPage               = 0xB020
private const val sprmSDxaLeft              = 0xB021
private const val sprmSDxaRight             = 0xB022
private const val sprmSDyaTop               = 0x9023
private const val sprmSDyaBottom            = 0x9024
private const val sprmSDzaGutter            = 0xB025
private const val sprmSDMPaperReq           = 0x5026
private const val sprmSPropRMark1           = 0xD227
private const val sprmSFBiDi                = 0x3228
private const val sprmSFFacingCol           = 0x3229
private const val sprmSFRTLGutter           = 0x322A
private const val sprmSBrcTop80             = 0x702B
private const val sprmSBrcTop               = 0xD234
private const val sprmSBrcLeft80            = 0x702C
private const val sprmSBrcLeft              = 0xD235
private const val sprmSBrcBottom80          = 0x702D
private const val sprmSBrcBottom            = 0xD236
private const val sprmSBrcRight80           = 0x702E
private const val sprmSBrcRight             = 0xD237
private const val sprmSPgbProp              = 0x522F
private const val sprmSDxtCharSpace         = 0x7030
private const val sprmSDyaLinePitch         = 0x9031
private const val sprmSClm                  = 0x5032
private const val sprmSTextFlow             = 0x5033
private const val sprmSWall                 = 0x3239
private const val sprmSRsid                 = 0x703A
private const val sprmSFpc                  = 0x303B
private const val sprmSRncFtn               = 0x303C
private const val sprmSEpc                  = 0x303D
private const val sprmSRncEdn               = 0x303E
private const val sprmSNFtn                 = 0x503F
private const val sprmSNfcFtnRef            = 0x5040
private const val sprmSNEdn                 = 0x5041
private const val sprmSNfcEdnRef            = 0x5042
private const val sprmSPropRMark2           = 0xD243

// Table sprms
private const val sprmTDefTable             = 0xD608
private const val sprmTDefTable10           = 0xD606
private const val sprmTDefTableShd80        = 0xD609
private const val sprmTDefTableShd          = 0xD612
private const val sprmTDefTableShd2nd       = 0xD616
private const val sprmTDefTableShd3rd       = 0xD60C
private const val sprmTDelete               = 0x5622
private const val sprmTDiagLine             = 0xD630
private const val sprmTDiagLine80           = 0xD62A
private const val sprmTDxaCol               = 0x7623
private const val sprmTDxaGapHalf           = 0x9602
private const val sprmTDxaLeft              = 0x9601
private const val sprmTDyaRowHeight         = 0x9407
private const val sprmTFBiDi80              = 0x560B
private const val sprmTFCantSplit           = 0x3403
private const val sprmTHTMLProps            = 0x740C
private const val sprmTInsert               = 0x7621
private const val sprmTJc                   = 0x5400
private const val sprmTMerge                = 0x5624
private const val sprmTSetBrc80             = 0xD620
private const val sprmTSetBrc10             = 0xD626
private const val sprmTSetBrc               = 0xD62F
private const val sprmTSetShd80             = 0x7627
private const val sprmTSetShdOdd80          = 0x7628
private const val sprmTSetShd               = 0xD62D
private const val sprmTSetShdOdd            = 0xD62E
private const val sprmTSetShdTable          = 0xD660
private const val sprmTSplit                = 0x5625
private const val sprmTTableBorders         = 0xD613
private const val sprmTTableBorders80       = 0xD605
private const val sprmTTableHeader          = 0x3404
private const val sprmTTextFlow             = 0x7629
private const val sprmTTlp                  = 0x740A
private const val sprmTVertAlign            = 0xD62C
private const val sprmTVertMerge            = 0xD62B
private const val sprmTFCellNoWrap          = 0xD639
private const val sprmTFitText              = 0xF636
private const val sprmTFKeepFollow          = 0x3619
private const val sprmTFNeverBeenAutofit    = 0x3663
private const val sprmTFNoAllowOverlap      = 0x3465
private const val sprmTPc                   = 0x360D
private const val sprmTBrcBottomCv          = 0xD61C
private const val sprmTBrcLeftCv            = 0xD61B
private const val sprmTBrcRightCv           = 0xD61D
private const val sprmTBrcTopCv             = 0xD61A
private const val sprmTCellBrcType          = 0xD662
private const val sprmTCellPadding          = 0xD632
private const val sprmTCellPaddingDefault   = 0xD634
private const val sprmTCellPaddingOuter     = 0xD638
private const val sprmTCellSpacing          = 0xD631
private const val sprmTCellSpacingDefault   = 0xD633
private const val sprmTCellSpacingOuter     = 0xD637
private const val sprmTCellWidth            = 0xD635
private const val sprmTDxaAbs               = 0x940E
private const val sprmTDxaFromText          = 0x9410
private const val sprmTDxaFromTextRight     = 0x941E
private const val sprmTDyaAbs               = 0x940F
private const val sprmTDyaFromText          = 0x9411
private const val sprmTDyaFromTextBottom    = 0x941F
private const val sprmTFAutofit             = 0x3615
private const val sprmTTableWidth           = 0xF614
private const val sprmTWidthAfter           = 0xF618
private const val sprmTWidthBefore          = 0xF617
private const val sprmTWidthIndent          = 0xF661
private const val sprmTIstd                 = 0x563A
private const val sprmTSetShdRaw            = 0xD63B
private const val sprmTSetShdOddRaw         = 0xD63C
private const val sprmTIstdPermute          = 0xD63D
private const val sprmTCellPaddingStyle     = 0xD63E
private const val sprmTFCantSplit90         = 0x3466
private const val sprmTPropRMark            = 0xD667
private const val sprmTWall                 = 0x3668
private const val sprmTIpgp                 = 0x7469
private const val sprmTCnf                  = 0xD66A
private const val sprmTSetShdTableDef       = 0xD66B
private const val sprmTDiagLine2nd          = 0xD66C
private const val sprmTDiagLine3rd          = 0xD66D
private const val sprmTDiagLine4th          = 0xD66E
private const val sprmTDiagLine5th          = 0xD66F
private const val sprmTDefTableShdRaw       = 0xD670
private const val sprmTDefTableShdRaw2nd    = 0xD671
private const val sprmTDefTableShdRaw3rd    = 0xD672
private const val sprmTSetShdRowFirst       = 0xD673
private const val sprmTSetShdRowLast        = 0xD674
private const val sprmTSetShdColFirst       = 0xD675
private const val sprmTSetShdColLast        = 0xD676
private const val sprmTSetShdBand1          = 0xD677
private const val sprmTSetShdBand2          = 0xD678
private const val sprmTRsid                 = 0x7479
private const val sprmTCellWidthStyle       = 0xF47A
private const val sprmTCellPaddingStyleBad  = 0xD67B
private const val sprmTCellVertAlignStyle   = 0x347C
private const val sprmTCellNoWrapStyle      = 0x347D
private const val sprmTCellFitTextStyle     = 0x347E
private const val sprmTCellBrcTopStyle      = 0xD47F
private const val sprmTCellBrcBottomStyle   = 0xD680
private const val sprmTCellBrcLeftStyle     = 0xD681
private const val sprmTCellBrcRightStyle    = 0xD682
private const val sprmTCellBrcInsideHStyle  = 0xD683
private const val sprmTCellBrcInsideVStyle  = 0xD684
private const val sprmTCellBrcTL2BRStyle    = 0xD685
private const val sprmTCellBrcTR2BLStyle    = 0xD686
private const val sprmTCellShdStyle         = 0xD687
private const val sprmTCHorzBands           = 0x3488
private const val sprmTCVertBands           = 0x3489
private const val sprmTJcRow                = 0x548A
private const val sprmTTableBrcTop          = 0xD68B
private const val sprmTTableBrcLeft         = 0xD68C
private const val sprmTTableBrcBottom       = 0xD68D
private const val sprmTTableBrcRight        = 0xD68E
private const val sprmTTableBrcInsideH      = 0xD68F
private const val sprmTTableBrcInsideV      = 0xD690
private const val sprmTFBiDi                = 0x560B
private const val sprmTFBiDi90              = 0x5664

private val sprmTable = intArrayOf(
/*   0 */    0,
/*   1 */    0,
/*   2 */    sprmPIstd,
/*   3 */    sprmPIstdPermute,
/*   4 */    sprmPIncLvl,
/*   5 */    sprmPJc,
/*   6 */    sprmPFSideBySide,
/*   7 */    sprmPFKeep,
/*   8 */    sprmPFKeepFollow,
/*   9 */    sprmPFPageBreakBefore,
/*  10 */    sprmPBrcl,
/*  11 */    sprmPBrcp,
/*  12 */    sprmPAnld,
/*  13 */    sprmPNLvlAnm,
/*  14 */    sprmPFNoLineNumb,
/*  15 */    sprmPChgTabsPapx,
/*  16 */    sprmPDxaRight80,
/*  17 */    sprmPDxaLeft80,
/*  18 */    sprmPNest80,
/*  19 */    sprmPDxaLeft1,
/*  20 */    sprmPDyaLine,
/*  21 */    sprmPDyaBefore,
/*  22 */    sprmPDyaAfter,
/*  23 */    sprmPChgTabs,
/*  24 */    sprmPFInTable,
/*  25 */    sprmPTtp,
/*  26 */    sprmPDxaAbs,
/*  27 */    sprmPDyaAbs,
/*  28 */    sprmPDxaWidth,
/*  29 */    sprmPPc,
/*  30 */    sprmPBrcTop10,
/*  31 */    sprmPBrcLeft10,
/*  32 */    sprmPBrcBottom10,
/*  33 */    sprmPBrcRight10,
/*  34 */    sprmPBrcBetween10,
/*  35 */    sprmPBrcBar10,
/*  36 */    sprmPFromText10,
/*  37 */    sprmPWr,
/*  38 */    sprmPBrcTop70,
/*  39 */    sprmPBrcLeft70,
/*  40 */    sprmPBrcBottom70,
/*  41 */    sprmPBrcRight70,
/*  42 */    sprmPBrcBetween70,
/*  43 */    sprmPBrcBar70,
/*  44 */    sprmPFNoAutoHyph,
/*  45 */    sprmPWHeightAbs,
/*  46 */    sprmPDcs,
/*  47 */    sprmPShd,
/*  48 */    sprmPDyaFromText,
/*  49 */    sprmPDxaFromText,
/*  50 */    sprmPFLocked,
/*  51 */    sprmPFWidowControl,
/*  52 */    sprmPRuler,
/*  53 */    sprmPFKinsoku,
/*  54 */    sprmPFWordWrap,
/*  55 */    sprmPFOverflowPunct,
/*  56 */    sprmPFTopLinePunct,
/*  57 */    sprmPFAutoSpaceDE,
/*  58 */    sprmPFAutoSpaceDN,
/*  59 */    0,
/*  60 */    0,
/*  61 */    sprmPISnapBaseLine,
/*  62 */    0,
/*  63 */    0,
/*  64 */    0,
/*  65 */    sprmCFStrike,
/*  66 */    sprmCFRMark,
/*  67 */    sprmCFFldVanish,
/*  68 */    sprmCPicLocation06,
/*  69 */    sprmCIbstRMark,
/*  70 */    sprmCDttmRMark,
/*  71 */    sprmCFData,
/*  72 */    sprmCIdslRMark,
/*  73 */    sprmCChs,
/*  74 */    sprmCSymbol,
/*  75 */    sprmCFOle2,
/*  76 */    0,
/*  77 */    sprmCHighlight,
/*  78 */    sprmCFEmboss,
/*  79 */    sprmCSfxText,
/*  80 */    sprmCIstd,
/*  81 */    sprmCIstdPermute,
/*  82 */    sprmCDefault,
/*  83 */    sprmCPlain,
/*  84 */    0,
/*  85 */    sprmCFBold,
/*  86 */    sprmCFItalic,
/*  87 */    sprmCFStrike,
/*  88 */    sprmCFOutline,
/*  89 */    sprmCFShadow,
/*  90 */    sprmCFSmallCaps,
/*  91 */    sprmCFCaps,
/*  92 */    sprmCFVanish,
/*  93 */    sprmCFtcDefault,
/*  94 */    sprmCKul,
/*  95 */    sprmCSizePos,
/*  96 */    sprmCDxaSpace,
/*  97 */    sprmCLid,
/*  98 */    sprmCIco,
/*  99 */    sprmCHps,
/* 100 */    sprmCHpsInc,
/* 101 */    sprmCHpsPos,
/* 102 */    sprmCHpsPosAdj,
/* 103 */    sprmCMajority,
/* 104 */    sprmCIss,
/* 105 */    sprmCHpsNew50,
/* 106 */    sprmCHpsInc1,
/* 107 */    sprmCHpsKern,
/* 108 */    sprmCMajority50,
/* 109 */    sprmCHpsMul,
/* 110 */    sprmCHresi,
/* 111 */    0,
/* 112 */    0,
/* 113 */    0,
/* 114 */    0,
/* 115 */    sprmCFDStrike,
/* 116 */    sprmCFImprint,
/* 117 */    sprmCFSpec,
/* 118 */    sprmCFObj,
/* 119 */    sprmPicBrcl,
/* 120 */    sprmPicScale,
/* 121 */    sprmPicBrcTop70,
/* 122 */    sprmPicBrcLeft70,
/* 123 */    sprmPicBrcBottom70,
/* 124 */    sprmPicBrcRight70,
/* 125 */    0,
/* 126 */    0,
/* 127 */    0,
/* 128 */    0,
/* 129 */    0,
/* 130 */    0,
/* 131 */    sprmScnsPgn,
/* 132 */    sprmSiHeadingPgn,
/* 133 */    sprmSOlstAnm,
/* 134 */    0,
/* 135 */    0,
/* 136 */    sprmSDxaColWidth,
/* 137 */    sprmSDxaColSpacing,
/* 138 */    sprmSFEvenlySpaced,
/* 139 */    sprmSFProtected,
/* 140 */    sprmSDmBinFirst,
/* 141 */    sprmSDmBinOther,
/* 142 */    sprmSBkc,
/* 143 */    sprmSFTitlePage,
/* 144 */    sprmSCcolumns,
/* 145 */    sprmSDxaColumns,
/* 146 */    sprmSFAutoPgn,
/* 147 */    sprmSNfcPgn,
/* 148 */    sprmSDyaPgn,
/* 149 */    sprmSDxaPgn,
/* 150 */    sprmSFPgnRestart,
/* 151 */    sprmSFEndnote,
/* 152 */    sprmSLnc,
/* 153 */    sprmSGprfIhdt,
/* 154 */    sprmSNLnnMod,
/* 155 */    sprmSDxaLnn,
/* 156 */    sprmSDyaHdrTop,
/* 157 */    sprmSDyaHdrBottom,
/* 158 */    sprmSLBetween,
/* 159 */    sprmSVjc,
/* 160 */    sprmSLnnMin,
/* 161 */    sprmSPgnStart,
/* 162 */    sprmSBOrientation,
/* 163 */    SprmSBCustomize,
/* 164 */    sprmSXaPage,
/* 165 */    sprmSYaPage,
/* 166 */    sprmSDxaLeft,
/* 167 */    sprmSDxaRight,
/* 168 */    sprmSDyaTop,
/* 169 */    sprmSDyaBottom,
/* 170 */    sprmSDzaGutter,
/* 171 */    sprmSDMPaperReq,
/* 172 */    0,
/* 173 */    0,
/* 174 */    0,
/* 175 */    0,
/* 176 */    0,
/* 177 */    0,
/* 178 */    0,
/* 179 */    0,
/* 180 */    0,
/* 181 */    0,
/* 182 */    sprmTJc,
/* 183 */    sprmTDxaLeft,
/* 184 */    sprmTDxaGapHalf,
/* 185 */    sprmTFCantSplit,
/* 186 */    sprmTTableHeader,
/* 187 */    sprmTTableBorders,
/* 188 */    sprmTDefTable10,
/* 189 */    sprmTDyaRowHeight,
/* 190 */    sprmTDefTable,
/* 191 */    sprmTDefTableShd,
/* 192 */    sprmTTlp,
/* 193 */    sprmTSetBrc,
/* 194 */    sprmTInsert,
/* 195 */    sprmTDelete,
/* 196 */    sprmTDxaCol,
/* 197 */    sprmTMerge,
/* 198 */    sprmTSplit,
/* 199 */    sprmTSetBrc10,
/* 200 */    sprmTSetShd,
/* 201 */    0,
/* 202 */    0,
/* 203 */    0,
/* 204 */    0,
/* 205 */    0,
/* 206 */    0,
/* 207 */    0,
/* 208 */    0)

/********************************************************************************************************************
 * Reading <a href="http://msdn.microsoft.com/en-us/library/dd942138.aspx">
 * [MS-CFB]</a>: Microsoft Compound File Binary file format
 * (aka the Object Linking and Embedding (OLE), or Component Object Model (COM)
 * structured storage compound file implementation binary file format)
 *
 * основные задачи:
 * 1) представить потоки в файле как сплошные массивы, скрыть все извраты формата,
 * 2) желательно читать транзакциями от начала к концу, для ускорения прямого чтения
 *    упакованных в архив файлов.
 */

private const val MSCFB_MAGIC_1 = 0xE011CFD0L.toInt()
private const val MSCFB_MAGIC_2 = 0xE11AB1A1L.toInt()

/* быстрая проверка на соответствие формату MS-CFB:
 * 1) размер должен быть минимум 3 сектора,
 * 2) в первых 8 байтах должна быть правильная сигнатура
 */
private fun AlFiles.isMSCFBFile(): Boolean {
    if (size < 3 * 512) return false
    read_pos = 0
    val magic1 = getDWord().toInt()
    val magic2 = getDWord().toInt()
    return magic1 == MSCFB_MAGIC_1 && magic2 == MSCFB_MAGIC_2
}

abstract class AlFilesMSCFB : AlFiles() {

    /* вначале представляем файл просто как один сплошной поток,
     * виртуальные адреса совпадают с файловыми.
     * (для совместимости со старыми форматами - Word2DOS и WinWord2.0)
     */

    private class Chunk(val addr: Int, val filePos: Int)
    private var chunks = mutableListOf(Chunk(0, 0), Chunk(Integer.MAX_VALUE, Integer.MAX_VALUE))

    protected class Read(val filePos: Int, val buf: ByteArray, val pos: Int, val len: Int, val tag: Int)
    protected var queue = mutableListOf<Read>()

    private var dir = mutableMapOf<String, Int>()


    /* для WinWord95+: парсим содержимое контейнера, строим в памяти нужные таблицы
     * для чтения через виртуальные адреса
     */
    protected fun parseAsMSCFB() {

        val header = ByteArray(512).read(0)

        val magic1 = header.dword(0x00)
        val magic2 = header.dword(0x04)
        if (magic1 != MSCFB_MAGIC_1 || magic2 != MSCFB_MAGIC_2) throw IOException()
        if (header.uword(0x1c) != 0xfffe) throw IOException()

        val powerSector = header.uword(0x1e)
        val powerMiniSect = header.uword(0x20)
        if (powerSector > 15 || powerMiniSect > 15 || powerMiniSect >= powerSector) throw IOException()

        val sizeofSector = 1 shl powerSector
        val sizeofMiniSect = 1 shl powerMiniSect
        val sizeofHeader = 512
        val arrsizeSector = sizeofSector / 4

        val arrsizeFAT = header.dword(0x2c) * arrsizeSector
        var sectorDir = header.dword(0x30)
        val cutoffMiniSect = header.dword(0x38)
        if (cutoffMiniSect < sizeofSector) throw IOException()
        var sectorMiniFAT = header.dword(0x3c)
        val arrsizeMiniFAT = header.dword(0x40) * arrsizeSector
        var sectorDIFAT = header.dword(0x44)

        val FAT = ByteArray(arrsizeFAT * 4) { 0xee.toByte() }
        for (n in 0..108) {
            if (n * arrsizeSector >= arrsizeFAT) break
            FAT.lazyRead(sizeofHeader + header.dword(0x4c + n * 4) * sizeofSector, n * sizeofSector, sizeofSector)
        }

        val DIFAT: ByteArray?
        if (sectorDIFAT >= 0) {
            DIFAT = ByteArray(sizeofSector)
            DIFAT.lazyRead(sizeofHeader + sectorDIFAT * sizeofSector, 0, sizeofSector, 1)
        } else {
            DIFAT = null
        }

        val MiniFAT: ByteArray?
        if (sectorMiniFAT >= 0) {
            MiniFAT = ByteArray(arrsizeMiniFAT * 4)
            MiniFAT.lazyRead(sizeofHeader + sectorMiniFAT * sizeofSector, 0, sizeofSector, 2)
        } else {
            MiniFAT = null
        }

        val DirSectors = arrayListOf<ByteArray>()
        if (sectorDir >= 0) {
            val page = ByteArray(sizeofSector)
            page.lazyRead(sizeofHeader + sectorDir * sizeofSector, 0, sizeofSector, 3)
            DirSectors.add(page)
        }

        var posMiniFAT = 0
        var posDIFAT = 0
        doRead {
            when (it.tag) {
                0 -> {
                    while (sectorMiniFAT >= 0) {
                        val sect = FAT.dword(sectorMiniFAT * 4)
                        if (sect < 0)
                            break
                        sectorMiniFAT = sect
                        posMiniFAT += sizeofSector
                        MiniFAT!!.lazyRead(sizeofHeader + sectorMiniFAT * sizeofSector, posMiniFAT, sizeofSector, 2)
                    }
                    while (sectorDir >= 0) {
                        val sect = FAT.dword(sectorDir * 4)
                        if (sect < 0)
                            break
                        sectorDir = sect
                        val page = ByteArray(sizeofSector)
                        page.lazyRead(sizeofHeader + sectorDir * sizeofSector, 0, sizeofSector, 2)
                        DirSectors.add(page)
                    }
                }
                1 -> {
                    for (n in 0 until arrsizeSector - 1) {
                        val pos = 109 + posDIFAT + n
                        if (pos * arrsizeSector >= arrsizeFAT)
                            break
                        FAT.lazyRead(sizeofHeader + DIFAT!!.dword(n * 4) * sizeofSector, pos * sizeofSector, sizeofSector)
                    }
                    sectorDIFAT = DIFAT!!.dword(sizeofSector - 4)
                    if (sectorDIFAT >= 0) {
                        DIFAT.fill(0xee.toByte())
                        posDIFAT += arrsizeSector - 1
                        DIFAT.lazyRead(sizeofHeader + sectorDIFAT * sizeofSector, 0, sizeofSector, 1)
                    }
                }
            }
        }

        chunks.clear()
        var virtualAddr = 0
        val dirsInSector = sizeofSector / 0x80
        val sectorMini = DirSectors[0].dword(0x74)
        val rootEntry = DirSectors[0].dword(0x4C)

        class Entry(val index: Int, val path: String)

        val traversal = mutableListOf<Entry>()
        if (rootEntry >= 0) traversal.add(Entry(rootEntry, ""))
        traversal@while (traversal.size > 0) {
            val entry = traversal.removeAt(traversal.size - 1)
            val buf = DirSectors[entry.index / dirsInSector]
            val pos = entry.index % dirsInSector * 0x80
            val leftSibling = buf.dword(pos + 0x44)
            if (leftSibling >= 0) traversal.add(Entry(leftSibling, entry.path))
            val rightSibling = buf.dword(pos + 0x48)
            if (rightSibling >= 0) traversal.add(Entry(rightSibling, entry.path))
            val entryType = buf.ubyte(pos + 0x42)
            when (entryType) {
                1 -> {
                    val child = buf.dword(pos + 0x4C)
                    var name = ""
                    val nameLen = buf.uword(pos + 0x40) / 2 - 1
                    for (i in 0 until nameLen)
                        name += buf.uword(pos + i * 2).toChar()
                    if (child >= 0) traversal.add(Entry(child, entry.path + name + "/"))
                }
                2 -> {
                    val entrySize = buf.dword(pos + 0x78)
                    if (entrySize == 0) continue@traversal
                    val entrySect = buf.dword(pos + 0x74)
                    var name = ""
                    val nameLen = buf.uword(pos + 0x40) / 2 - 1
                    for (i in 0 until nameLen)
                        name += buf.uword(pos + i * 2).toChar()
                    dir[entry.path + name] = virtualAddr
                    if (entrySize >= cutoffMiniSect) {
                        var first = entrySect
                        var last = entrySect
                        do {
                            var next = FAT.dword(4 * last)
                            while (next == last + 1) {
                                last++
                                next = FAT.dword(4 * last)
                            }
                            val fileOffset = sizeofHeader + first * sizeofSector
                            chunks.add(Chunk(virtualAddr, fileOffset))
                            virtualAddr += (last - first + 1) * sizeofSector
                            first = next
                            last = next
                        } while (first >= 0)
                    } else {
                        var first = entrySect
                        var last = entrySect
                        var firstOff = entrySect
                        do {
                            var firstFAT = sectorMini
                            while (firstOff >= (sizeofSector / sizeofMiniSect)) {
                                firstFAT = FAT.dword(4 * firstFAT)
                                firstOff -= (sizeofSector / sizeofMiniSect)
                            }
                            var lastFAT = firstFAT
                            var lastOff = firstOff
                            var next = MiniFAT!!.dword(4 * last)
                            while (next == last + 1) {
                                if (lastOff + 1 < (sizeofSector / sizeofMiniSect)) {
                                    lastOff++
                                } else {
                                    val nextFAT = FAT.dword(4 * lastFAT)
                                    if (nextFAT != lastFAT + 1)
                                        break
                                    lastOff = 0
                                    lastFAT = nextFAT
                                }
                                last++
                                next = MiniFAT.dword(4 * last)
                            }
                            val fileOffset = sizeofHeader + firstFAT * sizeofSector + firstOff * sizeofMiniSect
                            chunks.add(Chunk(virtualAddr, fileOffset))
                            virtualAddr += (last - first + 1) * sizeofMiniSect
                            first = next
                            last = next
                            firstOff = next
                        } while (first >= 0)
                    }
                }
            }
        }

        chunks.add(Chunk(Integer.MAX_VALUE, Integer.MAX_VALUE))
        chunks.sortBy { it.addr }
    }

    /* ищем начальный виртуальный адрес потока с заданным именем,
     * если возвращает не Integer.MAX_VALUE - то для чтения из потока
     * нужно добавить это число к адресу в потоке
     */
    protected fun stream(name: String): Int {
        return dir[name] ?: Integer.MAX_VALUE
    }

    /* ставим в очередь чтения, преобразуя виртуальные адреса в файловые
     */
    protected fun ByteArray.lazyRead(addr: Int, pos: Int = 0, len: Int = this.size, tag: Int = 0) {
        var n = chunks.binarySearchBy(addr) { it.addr }
        if (n < 0) n = -n - 2 // floorEntry

        var readAddr = addr
        var readPos = pos
        var remaining = len

        while (remaining > 0) {
            val offset = readAddr - chunks[n].addr
            val filePos = chunks[n].filePos + offset
            var readLen = chunks[n + 1].addr - chunks[n].addr - offset
            if (readLen > remaining) readLen = remaining

            queue.add(Read(filePos, this, readPos, readLen, tag))

            readAddr += readLen
            readPos += readLen
            remaining -= readLen
            n++
        }
    }

    /* читаем все в очереди, в порядке возрастания файловых адресов
     * если в процессе добавляются новые с адресом меньше текущего - потом делаем дополнительный проход
     */
    protected fun doRead(handler: (read: Read) -> Unit = {}) {
        queue.sortBy { it.filePos }

        while (queue.isNotEmpty()) {
            var cur = 0
            while (cur < queue.size) {
                val read = queue[cur]

                val res = parent!!.getByteBuffer(read.filePos, read.buf, read.pos, read.len)
                if (res != read.len) throw IOException()

                val saved_size = queue.size
                handler(read)
                if (queue.size != saved_size) {
                    queue.sortBy { it.filePos }
                    cur = queue.binarySearchBy(read.filePos) { it.filePos }
                }

                queue.removeAt(cur)
            }
        }
    }

    /* просто читаем, сразу, без всяких отложенных чтений
     */
    protected fun ByteArray.read(addr: Int, len: Int = this.size): ByteArray {
        this.lazyRead(addr, 0, len)
        doRead()
        return this
    }
}

private fun ByteArray.sbyte(pos: Int): Int {
    return this[pos].toInt()
}

private fun ByteArray.ubyte(pos: Int): Int {
    return ((this[pos].toInt() and 0xff))
}

private fun ByteArray.uword(pos: Int): Int {
    return ((this[pos].toInt() and 0xff) or
            (this[pos + 1].toInt() and 0xff shl 8))
}

private fun ByteArray.dword(pos: Int): Int {
    return ((this[pos].toInt() and 0xff) or
            (this[pos + 1].toInt() and 0xff shl 8) or
            (this[pos + 2].toInt() and 0xff shl 16) or
            (this[pos + 3].toInt() and 0xff shl 24))
}
