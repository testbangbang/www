package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.AlIntHolder;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.util.Base32Hex;
import com.neverland.engbook.util.InternalFunc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AlFilesMOBI extends AlFilesPDB {
    private static final boolean UNPACK_FIRST = true;

    private static final int MOBI_NOTSET = 0xffffffff;
    private static final int MOBI_LINK_SHIFT = 16;// </body></html>

    protected int       maxRec = 0;
    /*protected int       numRec = 0;


    protected int	    rec0_ver;
    protected int	    rec0_res1;
    protected int	    rec0_usize;
    protected int	    rec0_nrec;
    protected int	    rec0_rsize;
    protected int	    rec0_res2;

    protected int	    data_start = 0;
*/

    private   int     version = 0;

    private   int     huffman_recordOffset = -1;
    private   int     huffman_recordCount = -1;
    private   int     huffman_extra = 0;

    private   int	  codepage = 65001;
    private   int	  first_image_rec = -1;
    private   int     last_image_rec = -1;
    private   int     first_text_rec = -1;
    private   int 	  last_book_text0 = -1;
    private   int     fullname_offset = -1;
    private   int     fullname_length = -1;

    private   int     index_skel = -1;
    private   int     index_ncx = -1;
    private   int     index_frag = -1;
    private   int     index_guide = -1;

    private   int     part_flow = -1;
    private   int     count_flow = -1;

    private   String  book_title = null;
    private   final ArrayList<String>  book_author = new ArrayList<>();
    private   String  book_descrition = null;
    private   int  	  book_cover = -1;
    private   int  	  book_year = 0;
    private   final ArrayList<String>  book_ganre0 =  new ArrayList<>();
    private   String  book_lang = null;

    private class TAGXTags {
        public int tag; /**< Tag */
        public int values_count; /**< Number of values */
        public int bitmask; /**< Bitmask */
        public int control_byte; /**< EOF control byte */
    }

    private class MOBITagx {
        public final ArrayList<TAGXTags> tags =  new ArrayList<>(); /**< Array of tag entries */
        public int tags_count; /**< Number of tag entries */
        public int control_byte_count; /**< Number of control bytes */
    }

    private class MOBIIdxt {
        public final ArrayList<Integer> offsets = new ArrayList<>(); /**< Offsets to index entries */
        public int offsets_count; /**< Offsets count */
    }

    private class MOBIOrdt {
        //public int ordt1; /**< ORDT1 offsets */
        //public int ordt2; /**< ORDT2 offsets */
        public int type; /**< Type (0: 16, 1: 8 bit offsets) */
        public int ordt1_pos; /**< Offset of ORDT1 data */
        public int ordt2_pos; /**< Offset of ORDT2 data */
        public int offsets_count; /**< Offsets count */
    }

    private class MOBIIndexTag {
        public int tagid = 0; /**< Tag id */
        public int tagvalues_count = 0; /**< Number of tag values */
        public final ArrayList<Integer> tagvalues =  new ArrayList<>();
    }

    private class MOBIIndexEntry {
        public String label = null; /**< Entry string, zero terminated */
        public int tags_count = 0; /**< Number of tags */
        public final ArrayList<MOBIIndexTag> tags = new ArrayList<>();
    }

    private class MOBIPtagx {
        public int tag;
        public int tag_value_count;
        public int value_count;
        public int value_bytes;
    }

    public class MOBITOC {
        public String label = null;
        public int pos;
        public int level;
        public int fid;
        public int off;

        public int parent;
        public int childstart;
        public int childend;

        public int real;

        //public int postitle;

        public void clear() {
            label = null;
            level = pos = fid = off = real = 0;
        }
    }

    private class MOBIIndex {
        public int type = 0; /**< Index type: 0 - normal, 2 - inflection */
        public int entries_count = 0; /**< Index entries count */
        public int encoding = 0; /**< Index encoding */
        public int total_entries_count = 0; /**< Total index entries count */
        public int ordt_offset = 0; /**< ORDT offset */
        public int ligt_offset = 0; /**< LIGT offset */
        public int ligt_entries_count = 0; /**< LIGT index entries count */
        public int cncx_records_count = 0; /**< Number of compiled NCX records */
        public int cncx_record = -1; /**< Link to CNCX record */
        public final ArrayList<MOBIIndexEntry> entries =  new ArrayList<>(); /**< Index entries array */
        //char *orth_index_name; /**< Orth index name */
    }

    private class FLOWIndex {
        public int cnt = 0;
        public final ArrayList<Integer> ends = new ArrayList<>();
        //public final ArrayList<byte[]> data = new ArrayList<>();
        public final ArrayList<String> data0 = new ArrayList<>();
    }

    private final FLOWIndex indFLOW = new FLOWIndex();

    //private MOBIIndex indSKEL = null;
    private final MOBIIndex indFRAG = new MOBIIndex();
    private final MOBIIndex indNCX = new MOBIIndex();
    //private MOBIIndex indGUIDE = null;

    //protected final ArrayList<AlOnePDBRecord>	recordList = new ArrayList<AlOnePDBRecord>();

    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
        parent = myParent;
        fileName = file;
        fileList.clear();
        if (fList != null)
            fileList = (ArrayList<AlFileZipEntry>) fList.clone();
        //super.initState(file, myParent, fList);

        ident = "mobi";

        int i, j;
        StringBuilder docName = new StringBuilder(32);
        docName.append('/');

        char ch;
        for (i = 0; i < 32; i++) {
            ch = (char)parent.getByte(i);
            if (ch >= 0x80)
                ch = '_';
            if (ch >= 0x20)
                docName.append(ch);
        }

        fileName = docName.toString();

        parent.read_pos = 0x4c;
        numRec = maxRec = parent.getRevWord();

        recordList.clear();

        int maxBuffSize = 0;

        AlOnePDBRecord oc, ocPrev = null;
        for (i = 0; i < numRec; i++) {
            oc = new AlOnePDBRecord();
            oc.start = (int) parent.getRevDWord();
            parent.getRevDWord();

            if (i > 0) {
                ocPrev.len1 = oc.start - ocPrev.start;
                if (ocPrev.len1 > maxBuffSize)
                    maxBuffSize = ocPrev.len1;
            }

            ocPrev = oc;
            recordList.add(oc);
        }

        ocPrev.len1 = parent.size - ocPrev.start;
        if (ocPrev.len1 > maxBuffSize)
            maxBuffSize = ocPrev.len1;
        /*if (maxBuffSize > (rec0_rsize << 1))
            maxBuffSize = (rec0_rsize << 1);*/
        in_buff = new byte[maxBuffSize];

        parent.read_pos = recordList.get(0).start;
        rec0_ver = parent.getRevWord();
        if (rec0_ver == 1) {

        } else
        if (rec0_ver == 2 || rec0_ver == 258) {
            rec0_ver = 2;
            ident += "comp";
        } else {
            ident += "high";
        }
        rec0_res1 = parent.getRevWord();
        rec0_usize = (int) parent.getRevDWord();
        rec0_nrec = parent.getRevWord();
        rec0_rsize = parent.getRevWord();
        rec0_res2 = (int) parent.getRevDWord();

        byte[] tmp_byte;
        StringBuilder tmp_sb = new StringBuilder();

        int len_header;
        oc = recordList.get(0);
        j = getRDWord(oc.start, 0x10, oc.start + oc.len1);
        if (j == 0x4d4f4249) {
            len_header = getRDWord(oc.start, 0x14, oc.start + oc.len1);
            len_header += oc.start + 0x10;

            codepage = getRDWord(oc.start, 0x1c, len_header);
            if (codepage != 1252 && codepage != 65001)
                codepage = 1252;
            version = getRDWord(oc.start, 0x24, len_header);
            ident += Integer.toString(version);

            last_book_text0 = getRDWord(oc.start, 0x50, len_header);
            fullname_offset = getRDWord(oc.start, 0x54, len_header);
            fullname_length = getRDWord(oc.start, 0x58, len_header);
            if (fullname_length > 0) {
                if (oc.start + fullname_offset + fullname_length < oc.start + oc.len1) { // may be title
                    tmp_byte = new byte[fullname_length];
                    getByteArray(tmp_byte, oc.start + fullname_offset, fullname_length);

                    AlIntHolder pos = new AlIntHolder(0);
                    while (pos.value < fullname_length)
                        tmp_sb.append(AlUnicode.byte2Wide(codepage, tmp_byte, pos));

                    if (tmp_sb.length() > 0) {
                        book_title = tmp_sb.toString();
                        fileName = '/' + book_title + ".mobi";
                    }
                }
            }
            first_image_rec = getRDWord(oc.start, 0x6c, len_header);
            huffman_recordOffset = getRDWord(oc.start, 0x70, len_header);
            huffman_recordCount = getRDWord(oc.start, 0x74, len_header);
            if (version >= 8) {
                part_flow = getRDWord(oc.start, 0xc0, len_header);
                if (part_flow > first_image_rec)
                    last_image_rec = part_flow - 1;
            } else {
                first_text_rec = getRWord(oc.start, 0xc0, len_header);
                last_image_rec = getRDWord(oc.start, 0xc2, len_header);
            }
            count_flow = getRDWord(oc.start, 0xc4, len_header);
            huffman_extra = getRWord(oc.start, 0xf2, len_header);
            index_ncx = getRDWord(oc.start, 0xf4, len_header);

            if (version >= 8) {
                index_frag = getRDWord(oc.start, 0xf8, len_header);
                index_skel = getRDWord(oc.start, 0xfc, len_header);
                index_guide = getRDWord(oc.start, 0x104, len_header);
            }

            parent.read_pos = oc.start + 0x80;
            if (len_header > parent.read_pos) {
                j = (int) (parent.getRevDWord());
                if ((j & 0x40) != 0) {
                    parent.read_pos = len_header;
                    j = (int) (parent.getRevDWord());
                    if (j == 0x45585448) { // EXTH
                        int exth_len, exth_count, cnt = 0, pos = len_header + 0x0c, tp, ln, k;
                        parent.read_pos = len_header + 0x04;
                        exth_len = (int) (parent.getRevDWord());
                        parent.read_pos = len_header + 0x08;
                        exth_count = (int) (parent.getRevDWord());

                        while (pos < len_header + exth_len && cnt < exth_count) {
                            cnt++;

                            parent.read_pos = pos;
                            tp = (int) (parent.getRevDWord());
                            pos += 0x04;
                            parent.read_pos = pos;
                            ln = (int) (parent.getRevDWord());
                            pos += 0x04;

                            if (ln > 8) {
                                ln -= 8;
                                switch (tp) {
                                    case 100: //author
                                        tmp_byte = new byte[ln];
                                        getByteArray(tmp_byte, pos, ln);
                                        getEncodeString(tmp_byte, ln, tmp_sb);
                                        addAllString2List(book_author, tmp_sb);
                                        break;
                                    case 103 : //description
                                        tmp_byte = new byte[ln];
                                        getByteArray(tmp_byte, pos, ln);
                                        book_descrition = getEncodeString(tmp_byte, ln, tmp_sb);
                                        break;
                                    case 105: //subject
                                        tmp_byte = new byte[ln];
                                        getByteArray(tmp_byte, pos, ln);
                                        getEncodeString(tmp_byte, ln, tmp_sb);
                                        addAllString2List(book_ganre0, tmp_sb);
                                        break;
                                    case 106: //publich date
                                        tmp_byte = new byte[ln];
                                        getByteArray(tmp_byte, pos, ln);
                                        getEncodeString(tmp_byte, ln, tmp_sb);
                                        try {
                                            book_year = Integer.parseInt(tmp_sb.toString().trim());
                                        } catch (Exception e) {
                                            book_year = 0;
                                        }
                                        break;
                                    case 129: // kf8 cover offset

                                        tmp_byte = new byte[ln];
                                        getByteArray(tmp_byte, pos, ln);
                                        String s = getEncodeString(tmp_byte, ln, tmp_sb);
                                        if (s.startsWith("kindle:embed:")) {
                                            s = s.substring(13);
                                            k = s.indexOf('?');
                                            if (k > 1)
                                                s = s.substring(0, k);

                                            try {
                                                k = (int) Base32Hex.decode2int(s, false);
                                                if (k > 0) {
                                                    book_cover = k - 1;
                                                    if (book_cover + first_image_rec > last_image_rec)
                                                        book_cover = -1;
                                                }
                                            } catch (Exception e) {	}
                                        }
                                        break;
                                    case 201: // cover offset
                                        if (ln == 4) {
                                            book_cover = (int) (parent.getRevDWord());
                                            //book_cover += first_image_rec;
                                            if (book_cover + first_image_rec > last_image_rec)
                                                book_cover = -1;
                                        }
                                        break;
                                    case 524: //lang
                                        tmp_byte = new byte[ln];
                                        getByteArray(tmp_byte, pos, ln);
                                        book_lang = getEncodeString(tmp_byte, ln, tmp_sb).trim();
                                        break;
                                }

                                pos += ln;
                            }
                        }

                    }
                }
            }
        }

        for (i = 0; i < numRec; i++) {
            oc = recordList.get(i);
            try {
                debug_src(i, oc.start, oc.len1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        j = 0;
        if (last_book_text0 > 1 && last_book_text0 < maxRec) {
            numRec = last_book_text0;
        } else {
            numRec = rec0_nrec + 1;
        }

        out_buff = new byte[rec0_rsize];

        if (rec0_ver > 2) {
            HUFFreader = new HuffcdicReader();

            oc = recordList.get(huffman_recordOffset);
            HUFFreader.loadHuff(parent, oc.start);
            for (i = huffman_recordOffset + 1; i < huffman_recordOffset + huffman_recordCount; i++) {
                oc = recordList.get(i);
                HUFFreader.loadCdic(parent, oc.start);
            }
        }

        if (UNPACK_FIRST && rec0_ver > 1 && !onlyScan) {
            unpack_buffer = new byte[rec0_usize];
            useUnpack = false;
        }

        size = 0;
        int trall, mcopy;
        if (!onlyScan) {
            for (i = 1; i < numRec; i++) {
                oc = recordList.get(i);

                if (rec0_ver == 1) {
                    oc.len2 = oc.len1;
                    if (oc.len2 > rec0_rsize)
                        oc.len2 = rec0_rsize;
                } else if (rec0_ver == 2) {
                    parent.getByteBuffer(oc.start, in_buff, oc.len1);

                    if (UNPACK_FIRST) {
                        oc.len2 = AlFilesPDB.decompressPDB(in_buff, out_buff, oc.len1);
                        mcopy = Math.min(rec0_usize - size, oc.len2);
                        if (mcopy > 0)
                            System.arraycopy(out_buff, 0, unpack_buffer, size, mcopy);
                    } else {
                        oc.len2 = AlFilesPDB.calcsize_decompressPDB(in_buff, oc.len1, rec0_rsize);
                    }
                } else {
                    parent.getByteBuffer(oc.start, in_buff, oc.len1);
                    trall = HUFFreader.calcTrailingDataEntries(in_buff, oc.len1, huffman_extra);

                    if (UNPACK_FIRST) {
                        byte[] b = HUFFreader.unpack(in_buff, oc.len1 - trall, 0);
                        oc.len2 = b.length;
                        mcopy = Math.min(rec0_usize - size, oc.len2);
                        if (mcopy > 0)
                            System.arraycopy(b, 0, unpack_buffer, size, mcopy);
                    } else {
                        oc.len2 = HUFFreader.calcSizeBlock(in_buff, oc.len1 - trall, 0);
                    }
                }

                oc.pos = size;
                size += oc.len2;
            }
        } else {
            size = (numRec - 1) * rec0_rsize;
        }

        if (index_ncx >= numRec && index_ncx < recordList.size())
            readIndex(indNCX, index_ncx);

	/*if (index_skel >= numRec && index_skel < recordList.size())
		readIndex(&indSKEL, index_skel);*/

        if (index_frag >= numRec && index_frag < recordList.size())
            readIndex(indFRAG, index_frag);

	/*if (index_guide >= numRec && index_guide < recordList.size())
		readIndex(&indGUIDE, index_guide);*/

        if (indFRAG.total_entries_count > 0)
            readRealFRAQ();
        if (indNCX.total_entries_count > 0)
            readRealTOC2();

        if (part_flow > numRec && part_flow < recordList.size()) {
            if (read_flow() && indFLOW.cnt > 0) {
                //indFLOW.data.add(null);
                indFLOW.data0.add(null);
                for (i = 1; i < indFLOW.cnt; i++) {
                    byte[] buf = new byte[indFLOW.ends.get(i) - indFLOW.ends.get(i - 1)];
                    getBuffer(indFLOW.ends.get(i - 1), buf, buf.length);

                    StringBuilder ss = new StringBuilder();
                    ss.append((codepage == TAL_CODE_PAGES.CP1252) ?
                            AlUnicode.ANSIbuffer2Ustring(buf, indFLOW.ends.get(i) - indFLOW.ends.get(i - 1)) :
                            AlUnicode.UTFbuffer2Ustring(buf, indFLOW.ends.get(i) - indFLOW.ends.get(i - 1)));

                    while (ss.length() < buf.length)
                        ss.append(' ');

                    indFLOW.data0.add(ss.toString());
                }

                size = indFLOW.ends.get(0);
            }
        }

        if (size > rec0_usize)
            size = rec0_usize - 1;

        if (UNPACK_FIRST && rec0_ver > 1 && !onlyScan) {
            useUnpack = true;
        }

        return TAL_RESULT.OK;
    }

    private boolean readIndex(MOBIIndex indx, int nrec) {
        int maxoff = recordList.get(nrec).start + recordList.get(nrec).len1;
        int start = recordList.get(nrec).start;

        int tmp = getRDWord(start, 0x00, maxoff);
        if (tmp != 0x494E4458)
            return false;

        MOBITagx tagx = new MOBITagx();
        MOBIOrdt ordt = new MOBIOrdt();

        if (!readRealIndex(indx, tagx, ordt, nrec)) {
            indx.total_entries_count = 0;
            return false;
        }

        int count = indx.entries_count;
        indx.entries_count = 0;

        for (int i = 1; i <= count; i++) {
            if (!readRealIndex(indx, tagx, ordt, nrec + i)) {
                indx.total_entries_count = 0;
                return false;
            }
        }

        if (indx.cncx_records_count != 0)
            indx.cncx_record = nrec + count + 1;

        if (indx.entries_count != indx.total_entries_count) {
            indx.total_entries_count = 0;
            return false;
        }

        return true;
    }

    private int getTagValue(MOBIIndexEntry e, int tagId, int pos) {
        int res = -1;

        if (e == null)
            return res;

        for (int i = 0; i < e.tags_count; i++) {
            if (e.tags.get(i).tagid == tagId) {
                if (pos < e.tags.get(i).tagvalues_count) {
                    res = e.tags.get(i).tagvalues.get(pos);
                }
                break;
            }
        }

        return res;
    }

    public int	getFIDPosition(int fid, int off) {
        if (fid >= 0 && fid < indFRAG.total_entries_count) {
            //return InternalFunc.str2int(indFRAG.entries.get(fid).label, 10) + off + MOBI_LINK_SHIFT;
            return frag.get(fid) + off + MOBI_LINK_SHIFT;
        }
        return -1;
    }

    private final ArrayList<MOBITOC> toc = new ArrayList<>();

    public ArrayList<MOBITOC> getTOC() {
        if (toc.isEmpty())
            return null;
        return toc;
    }

    private final ArrayList<Integer> frag = new ArrayList<>();

    private boolean readRealFRAQ() {
        frag.clear();
        for (int i = 0; i < indFRAG.entries.size(); i++) {
            frag.add(InternalFunc.str2int(indFRAG.entries.get(i).label, 10));
        }
        indFRAG.entries.clear();
        return true;
    }

    private boolean  readRealTOC2() {
        int maxoff = recordList.get(indNCX.cncx_record).start + recordList.get(indNCX.cncx_record).len1;
        int start = recordList.get(indNCX.cncx_record).start;

        // hack. try later
        for (int i = 1; i < indNCX.cncx_records_count; i++) {
            maxoff += recordList.get(indNCX.cncx_record + 1).len1;
        }
        //

        for (int i = 0; i < indNCX.total_entries_count; i++) {
            MOBITOC t = new MOBITOC();

            if (!getInfoOneTOC(t, i, start, maxoff))
                return false;

            if (t.parent < 0)
                if (!addInfoOneTOC(t, start, maxoff))
                    return false;
        }

        indNCX.entries.clear();

        Collections.sort(toc, new Comparator<MOBITOC>() {
            public int compare(MOBITOC o1, MOBITOC o2) {
                return o2.pos > o1.pos ? -1 : (o2.pos < o1.pos ? 1 : 0);
            }
        });


        return true;
    }

    private boolean  getInfoOneTOC(MOBITOC m, int i, int start, int maxoff) {
        AlIntHolder pos = new AlIntHolder(0);

        byte[] text = new byte [1024];
        int cnt;
        pos.value = getTagValue(indNCX.entries.get(i), 3, 0);
        if (pos.value != -1) {

            // hack. try later
            int pos_hi = pos.value >> 16;
            while ((pos_hi--) > 0) {
                pos.value -= 0x10000;
                pos.value += recordList.get(indNCX.cncx_record + pos_hi).len1;
            }
            //

            cnt = getVarlen(start, pos, maxoff, 1);
            if (start + pos.value + cnt <= maxoff && cnt < 1024) {
                StringBuilder tmp_sb = new StringBuilder();
                getByteArray(text, start + pos.value, cnt);
                m.label = getEncodeString(text, cnt, tmp_sb);
            } else {
                m.label = "";
            }
        }

        if (m.label.trim().isEmpty())
            m.label = "* * *";

        m.pos = getTagValue(indNCX.entries.get(i), 1, 0) + MOBI_LINK_SHIFT;
        m.level = getTagValue(indNCX.entries.get(i), 4, 0);
        m.fid = getTagValue(indNCX.entries.get(i), 6, 0);
        m.off = getTagValue(indNCX.entries.get(i), 6, 1);

        m.parent = getTagValue(indNCX.entries.get(i), 21, 0);
        m.childstart = getTagValue(indNCX.entries.get(i), 22, 0);
        m.childend = getTagValue(indNCX.entries.get(i), 23, 0);

        if (m.fid != -1 && m.off != -1) {
            int u = getFIDPosition(m.fid, m.off);
            if (u > m.pos)
                m.pos = u;
        }

        if (m.level < 0)
            m.level = 0;

        return true;
    }

    private boolean  addInfoOneTOC(MOBITOC m, int start, int maxoff) {
        toc.add(m);

        if (m.childstart > 0)
            for (int i = m.childstart; i <= m.childend; i++) {
                MOBITOC t = new MOBITOC();
                if (!getInfoOneTOC(t, i, start, maxoff))
                    return false;
                if (!addInfoOneTOC(t, start, maxoff))
                    return false;
            }

        return true;
    }

    /*private boolean  readRealTOC() {
        int maxoff = recordList.get(indNCX.cncx_record).start + recordList.get(indNCX.cncx_record).len1;
        int start = recordList.get(indNCX.cncx_record).start;

        byte[] text = new byte [1024];
        int cnt;
        AlIntHolder pos = new AlIntHolder(0);

        for (int i = 0; i < indNCX.total_entries_count; i++) {
            MOBITOC t = new MOBITOC();

            pos.value = getTagValue(indNCX.entries.get(i), 3, 0);
            if (pos.value != -1) {
                cnt = getVarlen(start, pos, maxoff, 1);
                if (pos.value + cnt < maxoff && cnt < 1024) {
                    StringBuilder tmp_sb = new StringBuilder();
                    getByteArray(text, start + pos.value, cnt);
                    t.label = getEncodeString(text, cnt, tmp_sb);
                }
            }

            if (t.label.trim().isEmpty())
                t.label = "*";

            t.pos = getTagValue(indNCX.entries.get(i), 1, 0) + MOBI_LINK_SHIFT;
            t.level = getTagValue(indNCX.entries.get(i), 4, 0);
            t.fid = getTagValue(indNCX.entries.get(i), 6, 0);
            t.off = getTagValue(indNCX.entries.get(i), 6, 1);

            if (t.fid != -1 && t.off != -1) {
                int u = getFIDPosition(t.fid, t.off);
                if (u > t.pos)
                    t.pos = u;
            }

            if (t.level < 0)
                t.level = 0;

            toc.add(t);
        }

        indNCX.entries.clear();

        return true;
    }*/

    private boolean  readRealIndex(MOBIIndex indx, MOBITagx tagx, MOBIOrdt ordt, int nrec) {
        int maxoff = recordList.get(nrec).start + recordList.get(nrec).len1;
        int start = recordList.get(nrec).start;

        int tmp = getRDWord(start, 0x00, maxoff);
        if (tmp != 0x494E4458) //INDX
            return false;

        int header_length = getRDWord(start, 0x04, maxoff);
        int type = getRDWord(start, 0x08, maxoff);
        int idxt_offset = getRDWord(start, 0x14, maxoff);

        int entries_count = getRDWord(start, 0x18, maxoff);
        if (entries_count > 5000)
            return false;

        int encoding = getRDWord(start, 0x1c, maxoff);

        int total_entries_count = getRDWord(start, 0x24, maxoff);
        if (total_entries_count > 5000 * 0xffff)
            return false;

        int ordt_offset = getRDWord(start, 0x28, maxoff);
        int ligt_offset = getRDWord(start, 0x2c, maxoff);

        int ligt_entries_count = getRDWord(start, 0x30, maxoff);
        if (ligt_entries_count > 5)
            return false;

        int cncx_records_count = getRDWord(start, 0x34, maxoff);
        if (cncx_records_count > 0x0f)
            return false;

        int ordt_type = getRDWord(start, 0xa4, maxoff);
        int ordt_entries_count = getRDWord(start, 0xa8, maxoff);
        if (ordt_entries_count > 1024)
            return false;

        int ordt1_offset = getRDWord(start, 0xac, maxoff);
        int ordt2_offset = getRDWord(start, 0xb0, maxoff);
        int index_name_offset = getRDWord(start, 0xb4, maxoff);
        int index_name_length = getRDWord(start, 0xb8, maxoff);

        tmp = getRDWord(start, header_length, maxoff);
        if (tmp == 0x54414758 && indx.total_entries_count == 0) { //TAGX
            indx.encoding = encoding;
            if (!readRealTAGX(tagx, start + header_length, maxoff)) {
                return false;
            }

            if (index_name_offset > 0 && index_name_length > 0) {
                if (index_name_length <= header_length - index_name_offset && index_name_length < 255) {
				/*buffer_setpos(buf, index_name_offset);
				char *name = malloc(index_name_length + 1);
				buffer_getstring(name, buf, index_name_length);
				indx->orth_index_name = name;*/
                    parent.read_pos = start + index_name_offset;
                    parent.read_pos += index_name_length;
                }
            }

            indx.type = type;
            indx.entries_count = entries_count;
            indx.total_entries_count = total_entries_count;
            indx.ligt_offset = ligt_offset;
            tmp = getRDWord(start, ligt_offset, maxoff);
            if (tmp != 0x4c495754 && ligt_entries_count != 0) { //LIGT
                ligt_entries_count = 0;
            }
            indx.ligt_entries_count = ligt_entries_count;
            indx.ordt_offset = ordt_offset;
            indx.cncx_records_count = cncx_records_count;

            return true;
        }

        if (idxt_offset == 0)
            return false;

        MOBIIdxt idxt = new MOBIIdxt();

        tmp = getRDWord(start, idxt_offset, maxoff);
        if (tmp != 0x49445854 || !readRealIDXT(idxt, start + idxt_offset, maxoff, entries_count)) //IDXT
            return false;
        idxt.offsets.add(idxt_offset);

        if (entries_count > 0) {
            int i = 0;
            while (i < entries_count) {
                if (!readRealEntry(indx, idxt, tagx, ordt, i, start, maxoff))
                return false;
                i++;
            }
            indx.entries_count += entries_count;
        }

        return true;
    }

    private boolean  readRealEntry(MOBIIndex indx, MOBIIdxt idxt, MOBITagx tagx, MOBIOrdt ordt, int curr_number, int off, int maxoff) {

        int entry_offset = indx.entries_count;
        int entry_length = idxt.offsets.get(curr_number + 1) - idxt.offsets.get(curr_number);

        int entry_number = curr_number + entry_offset;
        if (entry_number >= indx.total_entries_count)
            return false;
        if (off + idxt.offsets.get(curr_number) + entry_length > maxoff)
            return false;

        AlIntHolder pos = new AlIntHolder(idxt.offsets.get(curr_number));

        int label_length = getUByte(off, pos.value++, maxoff);
        if (label_length > entry_length)
            return false;

        MOBIIndexEntry entry = new MOBIIndexEntry();

        byte[] text = new byte [255];
        if (false) {//ordt->ordt2) {
            //label_length = mobi_getstring_ordt(ordt, buf, (unsigned char*)text, label_length);
            pos.value += label_length;
            entry.label = "*";
        } else {
            getByteArray(text, off + pos.value, label_length);
            pos.value += label_length;
            StringBuilder tmp_sb = new StringBuilder();
            entry.label = getEncodeString(text, label_length, tmp_sb);//mobi_indx_get_label((unsigned char*)text, buf, label_length, indx->ligt_entries_count);
        }

        if (tagx.tags_count > 255)
            return false;
        MOBIPtagx[] ptagx = new MOBIPtagx[256];
        for (int i = 0; i < 255; i++)
            ptagx[i] = new MOBIPtagx();

        int control_bytes = getUByte(off, pos.value++, maxoff);
        if (tagx.control_byte_count > 1)
            pos.value += tagx.control_byte_count - 1;

        entry.tags_count = 0;

        if (tagx.tags_count > 0) {
            int ptagx_count = 0;
            int len;
            int i = 0, j;
            while (i < tagx.tags_count) {
                if (tagx.tags.get(i).control_byte == 1) {
                    control_bytes++;
                    i++;
                    continue;
                }
                int value = control_bytes/*[0]*/ & tagx.tags.get(i).bitmask;
                if (value != 0) {
                    int value_count = MOBI_NOTSET;
                    int value_bytes = MOBI_NOTSET;
                    if (value == tagx.tags.get(i).bitmask) {
                        if (LEVEL1_MOBI_SETBITS[tagx.tags.get(i).bitmask] > 1) {
                            len = 0;
                            value_bytes = getVarlen(off, pos, maxoff, 1);
                        } else {
                            value_count = 1;
                        }
                    } else {
                        int mask = tagx.tags.get(i).bitmask;
                        while ((mask & 1) == 0) {
                            mask >>= 1;
                            value >>= 1;
                        }
                        value_count = value;
                    }
                    ptagx[ptagx_count].tag = tagx.tags.get(i).tag;
                    ptagx[ptagx_count].tag_value_count = tagx.tags.get(i).values_count;
                    ptagx[ptagx_count].value_count = value_count;
                    ptagx[ptagx_count].value_bytes = value_bytes;
                    ptagx_count++;
                }
                i++;
            }

            for (j = 0; j < tagx.tags_count; j++) {
                MOBIIndexTag t = new MOBIIndexTag();
                entry.tags.add(t);
            }

            i = 0;
            while (i < ptagx_count) {
                int tagvalues_count = 0;
                int[] tagvalues = new int[100];
                if (ptagx[i].value_count != MOBI_NOTSET) {
                    int count = ptagx[i].value_count * ptagx[i].tag_value_count;
                    while ((count--) > 0 && tagvalues_count < 100) {
                        len = 0;
                        final int value_bytes = getVarlen(off, pos, maxoff, 1);
                        tagvalues[tagvalues_count++] = value_bytes;
                    }
				/* value count is not set */
                } else {
				/* read value_bytes bytes */
                    len = 0;
                    while (len < ptagx[i].value_bytes && tagvalues_count < 100) {
                        final int value_bytes = getVarlen(off, pos, maxoff, 1);
                        tagvalues[tagvalues_count++] = value_bytes;
                    }
                }

                if (tagvalues_count > 0) {
                    for (j = 0; j < tagvalues_count; j++) {
                        entry.tags.get(i).tagvalues.add(tagvalues[j]);
                    }
                } else {
                    entry.tags.get(i).tagvalues.clear();
                }

                entry.tags.get(i).tagid = ptagx[i].tag;
                entry.tags.get(i).tagvalues_count = tagvalues_count;
                entry.tags_count++;
                i++;
            }
        }

        indx.entries.add(entry);

        return true;
    }

    private int getVarlen(int off1, AlIntHolder off2, int maxoff, int direction) {
        int val = 0;
        char byte_count = 0;
        char bt;
        final char stop_flag = 0x80;
        final char mask = 0x7f;
        long shift = 0;
        do {
            if (direction == 1) {
                bt = (char) getUByte(off1, off2.value++, maxoff);
                val <<= 7;
                val |= (bt & mask);
            } else {
                bt = (char) getUByte(off1, off2.value--, maxoff);
                val = val | (bt & mask) << shift;
                shift += 7;
            }
            byte_count++;
        } while ((bt & stop_flag) == 0 && (byte_count < 4));
        return val;
    }


    private boolean readRealIDXT(MOBIIdxt idxt, int off, int maxoff, int entries_count) {

        if (off + entries_count * 2 + 0x04 > maxoff)
            return false;

        parent.read_pos = off + 0x04;
        while ((entries_count--) > 0)
            idxt.offsets.add((int) parent.getRevWord());
        idxt.offsets_count = entries_count;

        return true;
    }

    private boolean  readRealTAGX(MOBITagx tagx, int off, int maxoff) {
        tagx.control_byte_count = 0;
        tagx.tags_count = 0;
        tagx.tags.clear();

        int tagx_record_length = getRDWord(off, 0x04, maxoff);
        if (tagx_record_length < 12)
            return false;

        tagx.control_byte_count = getRDWord(off, 0x08, maxoff);
        if (off + tagx_record_length > maxoff)
            return false;
        tagx_record_length -= 12;

        int i = 0, j = 0x0c;

        while (i < tagx_record_length >> 2) {
            TAGXTags t = new TAGXTags();

            t.tag = getUByte(off, j++, maxoff);
            t.values_count = getUByte(off, j++, maxoff);
            t.bitmask = getUByte(off, j++, maxoff);
            t.control_byte = getUByte(off, j++, maxoff);

            tagx.tags.add(t);
            i++;
        }
        tagx.tags_count = tagx_record_length >> 2;

        return true;
    }



    private final boolean read_flow() {

        int maxoff = recordList.get(part_flow).start + recordList.get(part_flow).len1;
        int start =  recordList.get(part_flow).start;

        int tmp = getRDWord(start, 0x00, maxoff);
        if (tmp != 0x46445354)
            return false;

        int data_offset = getRDWord(start, 0x04, maxoff);
        int section_count = getRDWord(start, 0x08, maxoff);

        if (section_count != count_flow || section_count < 1 || data_offset != 12)
            return false;

        if ((maxoff - start - 12) < section_count * 8)
            return false;

        indFLOW.cnt = 0;
        while (indFLOW.cnt < section_count) {
            parent.getRevDWord();
            indFLOW.ends.add((int)parent.getRevDWord());
            indFLOW.cnt++;
        }

        return indFLOW.cnt == count_flow;
    }

    private int getRDWord(int off1, int off2, int maxOff) {
        if (maxOff > off1 + off2) {
            parent.read_pos = off1 + off2;
            return (int) parent.getRevDWord();
        }
        return -1;
    }

    private int getRWord(int off1, int off2, int maxOff) {
        if (maxOff > off1 + off2) {
            parent.read_pos = off1 + off2;
            return parent.getRevWord();
        }
        return -1;
    }

    private int getUByte(int off1, int off2, int maxOff) {
        if (maxOff > off1 + off2) {
            parent.read_pos = off1 + off2;
            return parent.getUByte();
        }
        return -1;
    }

    private byte[] in_buff = null;
    private byte[] out_buff = null;
    private HuffcdicReader HUFFreader = null;

    private final AlIntHolder posHolder = new AlIntHolder(0);



    private String getEncodeString(byte[] buff, int len, StringBuilder outBuff) {
        outBuff.setLength(0);
        posHolder.value = 0;
        while (posHolder.value < len) {
            outBuff.append(AlUnicode.byte2Wide(codepage, buff, posHolder));
        }
        return outBuff.toString();
    }

    @Override
    protected int getBuffer(int pos, byte[] dst, int cnt) {
        //if (rec0_ver == 1)
        //    return parent.getByteBuffer(pos + data_start, dst, cnt);

        int i, j, dst_start = 0, dst_max = dst.length, len = recordList.size();
        AlOnePDBRecord oc;

        for (i = 1; i < len; i++) {
            if (dst_start >= dst_max)
                break;

            if (i == len - 1)
                i = len - 1;
            oc = recordList.get(i);

            if (pos >= oc.pos && pos < oc.pos + oc.len2) {
                //parent.getByteBuffer(oc.start, in_buff, oc.len1);

                if (rec0_ver == 1) {
                    parent.getByteBuffer(oc.start, out_buff, oc.len2);
                    //System.arraycopy(in_buff, 0, out_buff, 0, oc.len2);
                } else
                if (rec0_ver == 2) {
                    parent.getByteBuffer(oc.start, in_buff, oc.len1);
                    AlFilesPDB.decompressPDB(in_buff, out_buff, oc.len1);
                } else {
                    parent.getByteBuffer(oc.start, in_buff, oc.len1);
                    j = HUFFreader.calcTrailingDataEntries(in_buff, oc.len1, huffman_extra);
                    out_buff = HUFFreader.unpack(in_buff, oc.len1 - j, 0);
                }

                j = oc.pos + oc.len2 - pos;
                if (pos + j >= size)
                    j = size - pos;
                if (dst_start + j >= dst_max)
                    j = dst_max - dst_start;
                System.arraycopy(out_buff, pos - oc.pos, dst, dst_start, j);

                pos += j;
                dst_start += j;
            }
        }

        return dst.length;
    }

    public int getGanre() {
        int book_ganre = 0;

        if (/*book_ganre0 != null && */book_ganre0.size() > 0) {
            for (int i = 0; i < book_ganre0.size(); i++) {
                //book_ganre |= AlGenre.getSympleGanre(book_ganre0.get(i));
            }
        }

        return book_ganre;
    }

    public ArrayList<String> getGanres() {
        return book_ganre0;
    }

    public ArrayList<String> getAuthors() {
        return book_author;
    }

    private static void addAllString2List(ArrayList<String> sl, StringBuilder sb) {
        while (true) {
            int i = sb.indexOf(";");
            if (i > 0) {
                sl.add(sb.substring(0, i).trim());
                sb.delete(0,  i + 1);
                continue;
            }
            break;
        }
        sl.add(sb.toString().trim());
    }

    @Override
    public int getCodePage() {
        return codepage;
    }

    private void getByteArray(byte[] dst, int start_pos, int len) {
        int i = 0;
        while (i < len) {
            dst[i++] = parent.getByte(start_pos++);
        }
    }

    public String getTitle() {
        return book_title;
    }

    public String getDescription() {
        return book_descrition;
    }

    public String getLang() {
        return book_lang;
    }

    public int getCover() {
        return book_cover;
    }

    public int getYear() {
        return book_year;
    }

    private void debug_src(int num, int pos, int len) throws IOException {
		/*byte[] inb = new byte[len < CfgConst.FILE_BUF_SIZE ? CfgConst.FILE_BUF_SIZE : len];
		parent.getBuffer(pos, inb);

		FileOutputStream os;

		os = new FileOutputStream("/mnt/sdcard/test/" + num + '_' + Integer.toHexString(pos));
		os.write(inb);
		os.flush();
		os.close();

		byte[] ind = new byte[4096];
		FPDB.decompress(inb, ind, len);
		os = new FileOutputStream("/mnt/sdcard/test/" + num + '_' + Integer.toHexString(pos) + ".u");
		os.write(ind);
		os.flush();
		os.close();*/
    }

    @Override
    public int getExternalFileNum(String fname) {
        if (fname == null)
            return LEVEL1_FILE_NOT_FOUND;

        Integer recNum = -1;
        boolean isFlow = false;

        if (fname.startsWith("kindle:flow:")) {
            isFlow = true;

            StringBuilder ff = new StringBuilder(fname);
            ff.delete(0, 12);
            recNum = ff.indexOf("?");
            if (recNum != -1)
                ff.delete(recNum, ff.length());

            recNum = InternalFunc.str2int(ff, 10);

            if (recNum == -1)
                return LEVEL1_FILE_NOT_FOUND;

            if (recNum < 1 || recNum >= indFLOW.cnt)
                return LEVEL1_FILE_NOT_FOUND;

            if (indFLOW.data0.get(recNum).isEmpty())
                return LEVEL1_FILE_NOT_FOUND;
        } else {
            for (int i = 0; i < fname.length(); i++)
                if (!AlUnicode.isDecDigit(fname.charAt(i)))
                    return LEVEL1_FILE_NOT_FOUND;


            recNum = InternalFunc.str2int(fname, 10);

            if (recNum == -1)
                return LEVEL1_FILE_NOT_FOUND;

            recNum += first_image_rec;
            if (recNum > last_image_rec)
                return LEVEL1_FILE_NOT_FOUND;

            if (recNum < 3 || recNum >= maxRec)
                return LEVEL1_FILE_NOT_FOUND;

            if (recNum >= recordList.size())
                return LEVEL1_FILE_NOT_FOUND;

            if (mapFile.size() == 0) {
                for (int i = 0; i < fileList.size(); i++) {
                    mapFile.put(fileList.get(i).name, i);
                }
            }
        }

        Integer i = mapFile.get(fname);
        if (i != null)
            return i;

        AlFileZipEntry of = new AlFileZipEntry();
        if (isFlow) {

            of.compress = 1;
            of.cSize = (indFLOW.ends.get(recNum) - indFLOW.ends.get(recNum - 1)) << 1;
            of.uSize = of.cSize;
            of.flag = 0;
            of.position = recNum;
            of.time = 0;
            of.name = fname;
            fileList.add(of);

            mapFile.put(fname, fileList.size() - 1);

            return fileList.size() - 1;
        } else {

            AlOnePDBRecord oc = recordList.get(recNum);
            if (oc.len1 > 0) {
                of.compress = 0;
                of.cSize = oc.len1;
                of.uSize = oc.len1;
                of.flag = 0;
                of.position = oc.start;
                of.time = 0;
                of.name = fname;
                fileList.add(of);

                mapFile.put(fname, fileList.size() - 1);

                return fileList.size() - 1;
            }
        }

        return LEVEL1_FILE_NOT_FOUND;
    }

    @Override
    public boolean fillBufFromExternalFile(int num, int pos, byte[] dst, int dst_pos, int cnt) {

        if (num >= 0 && num < fileList.size() && pos == 0) {
            AlFileZipEntry of = fileList.get(num);

            if (of.compress == 1) {
                int c = Math.min(cnt, of.uSize - pos);
                byte[] src = null;
                try {
                    src = indFLOW.data0.get(of.position).getBytes("UTF-16LE");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (src != null)
                    System.arraycopy(src, pos, dst, dst_pos, c);
            } else {
                parent.getByteBuffer(of.position, dst, cnt);
            }
            return true;
        }

        return false;
    }

    public String getFlowString(int num) {
        if (num < 1 || num >= indFLOW.cnt)
            return null;

        return indFLOW.data0.get(num);
    }

    @Override
    public void	needUnpackData() {

        if (useUnpack)
            return;

        useUnpack = true;

        try {
            unpack_buffer = new byte[size];
        } catch (Exception e) {
            e.printStackTrace();
            unpack_buffer = null;
        }
        if (unpack_buffer != null) {
            getBuffer(0, unpack_buffer, size);
        } else {
            useUnpack = false;
        }
    }

    private static final char LEVEL1_MOBI_SETBITS[] = {
        0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4,
                1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
                1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
                2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
                1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
                2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
                2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
                3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
                1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
                2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
                2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
                3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
                2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
                3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
                3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
                4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8,
    };
}
