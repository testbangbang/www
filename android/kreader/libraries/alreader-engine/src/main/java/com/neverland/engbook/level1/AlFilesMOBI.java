package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.AlIntHolder;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.util.Base32Hex;
import com.neverland.engbook.util.InternalFunc;

import java.io.IOException;
import java.util.ArrayList;

public class AlFilesMOBI extends AlFilesPDB {
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

    private   String  book_title = null;
    private   final ArrayList<String>  book_author = new ArrayList<>();
    private   String  book_descrition = null;
    private   int  	  book_cover = -1;
    private   int  	  book_year = 0;
    private   final ArrayList<String>  book_ganre0 =  new ArrayList<>();
    private   String  book_lang = null;


    //protected final ArrayList<AlOnePDBRecord>	recordList = new ArrayList<AlOnePDBRecord>();


    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
        super.initState(file, myParent, fList);

        //ident = "mobi";

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
        in_buff = new byte[maxBuffSize];

        parent.read_pos = recordList.get(0).start;
        rec0_ver = parent.getRevWord();
        if (rec0_ver == 1) {
            ident = "mobi";
        } else
        if (rec0_ver == 2) {
            ident = "mobicomp";
        } else {
            ident = "mobihigh";
        }
        rec0_res1 = parent.getRevWord();
        rec0_usize = (int) parent.getRevDWord();
        rec0_nrec = parent.getRevWord();
        rec0_rsize = parent.getRevWord();
        rec0_res2 = (int) parent.getRevDWord();

        data_start = recordList.get(1).start;

        byte[] tmp_byte;
        StringBuilder tmp_sb = new StringBuilder();

        int len_header;
        oc = recordList.get(0);
        parent.read_pos = oc.start + 0x10;
        j = (int) parent.getRevDWord();
        if (j == 0x4d4f4249) {
            parent.read_pos = oc.start + 0x14;
            len_header = (int) parent.getRevDWord();
            len_header += oc.start + 0x10;

            parent.read_pos = oc.start + 0x1c;
            if (len_header > parent.read_pos)
                codepage = (int) parent.getRevDWord();
            if (codepage != 1252 && codepage != 65001)
                codepage = 1252;

            parent.read_pos = oc.start + 0x50;
            if (len_header > parent.read_pos)
                last_book_text0 = (int) (parent.getRevDWord() /*- 1*/);

            parent.read_pos = oc.start + 0x54;
            if (len_header > parent.read_pos)
                fullname_offset = (int) (parent.getRevDWord());

            parent.read_pos = oc.start + 0x58;
            if (len_header > parent.read_pos) {
                fullname_length = (int) (parent.getRevDWord());

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
            }

            parent.read_pos = oc.start + 0x6c;
            if (len_header > parent.read_pos)
                first_image_rec = (int) (parent.getRevDWord());
            parent.read_pos = oc.start + 0x70;
            if (len_header > parent.read_pos)
                huffman_recordOffset = (int) (parent.getRevDWord());
            parent.read_pos = oc.start + 0x74;
            if (len_header > parent.read_pos)
                huffman_recordCount = (int) (parent.getRevDWord());
            parent.read_pos = oc.start + 0x78;

            parent.read_pos = oc.start + 0xc0;
            if (len_header > parent.read_pos)
                first_text_rec = (int) (parent.getRevWord());

            parent.read_pos = oc.start + 0xc2;
            if (len_header > parent.read_pos)
                last_image_rec = (int) (parent.getRevWord());

            if (len_header > 0xf3) {
                parent.read_pos = oc.start + 0xf2;
                if (len_header > parent.read_pos) {
                    huffman_extra = (parent.getRevWord());
                }
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
                                                    if (book_cover + first_image_rec> last_image_rec)
                                                        book_cover = -1;
                                                }
                                            } catch (Exception e) {	}
                                        }
                                        break;
                                    case 201: // cover offset
                                        if (ln == 4) {
                                            book_cover = (int) (parent.getRevDWord());
                                            //book_cover += first_image_rec;
                                            if (book_cover + first_image_rec> last_image_rec)
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

        for (i = 1; i < numRec; i++) {
            oc = recordList.get(i);

            oc.len2 = rec0_rsize;
            oc.pos = j;

            j += rec0_rsize;
        }

        size = (numRec - 2) * rec0_rsize;

        if (!onlyScan) {

            oc = recordList.get(numRec - 1);
            if (rec0_ver == 1) {
                out_buff = new byte[rec0_rsize];
                size += oc.len1;
            } else if (rec0_ver == 2) {
                out_buff = new byte[rec0_rsize];
                parent.getByteBuffer(oc.start, in_buff, oc.len1);
                size += AlFilesPDB.decompressPDB(in_buff, out_buff, oc.len1);
            } else {
                HUFFreader = new HuffcdicReader();

                oc = recordList.get(huffman_recordOffset);
                HUFFreader.loadHuff(parent, oc.start);
                for (i = huffman_recordOffset + 1; i < huffman_recordOffset + huffman_recordCount; i++) {
                    oc = recordList.get(i);
                    HUFFreader.loadCdic(parent, oc.start);
                }

                size = 0;
                j = 0;
                int sz, trall;
                for (i = 1; i < numRec; i++) {
                    oc = recordList.get(i);

                    parent.getByteBuffer(oc.start, in_buff, oc.len1);

                    trall = HUFFreader.calcTrailingDataEntries(in_buff, oc.len1, huffman_extra);
                    sz = HUFFreader.calcSizeBlock(in_buff, oc.len1 - trall, 0);

                    size += sz;

                    oc.len2 = sz;
                    oc.pos = j;
                    j += sz;
                }
            }
        }

        if (size > rec0_usize)
            size = rec0_usize;

        return TAL_RESULT.OK;
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

        if (book_ganre0 != null && book_ganre0.size() > 0) {
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

    public static final void addAllString2List(ArrayList<String> sl, StringBuilder sb) {
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

        Integer recNum = InternalFunc.str2int(fname, 10);
        if (recNum == null)
            return LEVEL1_FILE_NOT_FOUND;
            //recNum = 0;

        if (recNum == -1)
            return LEVEL1_FILE_NOT_FOUND;

        recNum += first_image_rec;
        if (recNum > last_image_rec)
            return LEVEL1_FILE_NOT_FOUND;

        if (recNum < 3 || recNum >= maxRec)
            return LEVEL1_FILE_NOT_FOUND;

        if (recNum >= recordList.size())
            return LEVEL1_FILE_NOT_FOUND;

        for (int i = 0; i < fileList.size(); i++) {
            if (fileList.get(i).name.contentEquals(fname)) {
                return i;
            }
        }


        AlOnePDBRecord oc = recordList.get(recNum);
        if (oc.len1 > 0) {
            AlFileZipEntry of = new AlFileZipEntry();
            of.compress = 0;
            of.cSize = oc.len1;
            of.uSize = oc.len1;
            of.flag = 0;
            of.position = oc.start;
            of.time = 0;
            of.name = fname;
            fileList.add(of);
            return fileList.size() - 1;
        }

        return LEVEL1_FILE_NOT_FOUND;
    }

    @Override
    public boolean fillBufFromExternalFile(int num, int pos, byte[] dst, int dst_pos, int cnt) {

        if (num >= 0 && num < fileList.size() && pos == 0) {
            AlFileZipEntry of = fileList.get(num);
            parent.getByteBuffer(of.position, dst, cnt);
            return true;
        }

        return false;
    }

}
