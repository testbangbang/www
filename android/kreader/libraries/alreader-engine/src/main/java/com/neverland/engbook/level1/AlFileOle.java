package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlStylesOptions;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public abstract class AlFileOle extends AlFiles {
    private static final long OLE_MAGIC_1 = 0xE011CFD0L;
    private static final long OLE_MAGIC_2 = 0xE11AB1A1L;

    public static boolean isOleFile(AlFiles a) {
        int size = a.getSize();
        if (size < 256)
            return false;

        a.read_pos = 0;
        long magic1 = a.getDWord() & 0xFFFFFFFFL;
        long magic2 = a.getDWord() & 0xFFFFFFFFL;
        return (magic1 == OLE_MAGIC_1 && magic2 == OLE_MAGIC_2);
    }

    @Override
    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
        super.initState(file, myParent, fList);

        chunks = new TreeMap<Integer, Chunk>();
        Chunk chunk = new Chunk();
        chunk.virtPos = 0;
        chunk.filePos = 0;
        Chunk nextChunk = new Chunk();
        chunk.next = new WeakReference<Chunk>(nextChunk);
        chunks.put(0, chunk);
        chunk = nextChunk;
        chunk.virtPos = Integer.MAX_VALUE;
        chunk.filePos = Integer.MAX_VALUE;
        chunks.put(Integer.MAX_VALUE, chunk);

        return TAL_RESULT.OK;
    }

    private static class Chunk {
        private int virtPos;
        private int filePos;
        private WeakReference<Chunk> next;
    }

    protected TreeMap<Integer, Chunk> chunks;

    private static class Dir {
        private String name;
        private int type;
        private int left;
        private int right;
        private int child;
        private int first;
        private int size;
        private int virtPos;
    }

    private ArrayList<Dir> dir;

    protected static class Read {
        protected byte[] buf;
        protected int pos;
        protected int len;
        protected int tag;

        public Read(byte[] buf, int pos, int len, int tag) {
            this.buf = buf;
            this.pos = pos;
            this.len = len;
            this.tag = tag;
        }
    }

    protected TreeMap<Integer, Read> queue = new TreeMap<Integer, Read>();

    protected final void add(int addr, byte[] buf, int pos, int len, int id)
            throws IOException {

        Chunk chunk = null;
        try {
            chunk = chunks.floorEntry(addr).getValue();
        } catch (Exception e) { }
        if (chunk == null)
            throw new IOException();

        while (len > 0) {
            int virt = chunk.virtPos;
            int offs = addr - virt;
            int file = chunk.filePos + offs;
            chunk = chunk.next.get();
            if (chunk == null)
                throw new IOException();
            int size = chunk.virtPos - virt - offs;
            if (size > len)
                size = len;
            queue.put(file, new Read(buf, pos, size, id));
            addr += size;
            pos += size;
            len -= size;
        }
    }

    protected final Read step() throws IOException {
        Map.Entry<Integer, Read> entry = queue.firstEntry();
        if (entry == null)
            return null;
        int filePos = entry.getKey();
        Read rd = entry.getValue();
        byte[] buf = rd.buf;
        int pos = rd.pos;
        int end = rd.pos + rd.len;
        //int res = parent.getBytePosBuffer(filePos, buf, pos, end);
        //if (res != end)
        int res = parent.getByteBuffer(filePos, buf, pos, end - pos);
        if (res != end - pos)
            throw new IOException();
        queue.remove(filePos);
        return rd;
    }

    protected final void kick() throws IOException {
        while (step() != null)
            continue;
    }

    protected final void read(int addr, byte[] buf, int len) throws IOException {
        add(addr, buf, 0, len, 0);
        kick();
    }

    protected final void parse() throws IOException {

        byte[] header = new byte[512];
        read(0, header, 512);
        long magic1 = dword(header, 0x00) & 0xFFFFFFFFL;
        long magic2 = dword(header, 0x04) & 0xFFFFFFFFL;
        if (magic1 != OLE_MAGIC_1 || magic2 != OLE_MAGIC_2)
            throw new IOException();
        if (word(header, 0x1c) != 0xfffe)
            throw new IOException();
        int uSectorSize = word(header, 0x1e);
        int uMiniSectorSize = word(header, 0x20);
        if (uSectorSize > 15 || uMiniSectorSize > 15
                || uMiniSectorSize >= uSectorSize)
            throw new IOException();
        uSectorSize = 1 << uSectorSize;
        uMiniSectorSize = 1 << uMiniSectorSize;
        int cFat = dword(header, 0x2c) * (uSectorSize / 4);
        int sectDirStart = dword(header, 0x30);
        int ulMiniSectorCutoff = dword(header, 0x38);
        if (ulMiniSectorCutoff < uSectorSize)
            throw new IOException();
        int sectMiniFatStart = dword(header, 0x3c);
        int cMiniFat = dword(header, 0x40) * (uSectorSize / 4);
        int sectDifStart = dword(header, 0x44);

        byte[] bufFat = new byte[cFat * 4];
        Arrays.fill(bufFat, (byte) 0xee);
        for (int n = 0; n < 109; n++) {
            if (n * (uSectorSize / 4) >= cFat)
                break;
            add(512 + dword(header, 0x4c + n * 4) * uSectorSize, bufFat,
                    n * uSectorSize, uSectorSize, 0);
        }

        int posDif = 0;
        byte[] bufDif = null;
        if (sectDifStart >= 0) {
            bufDif = new byte[uSectorSize];
            add(512 + sectDifStart * uSectorSize, bufDif, 0, uSectorSize, 1);
        }

        int posMiniFat = 0;
        byte[] bufMiniFat = null;
        if (sectMiniFatStart >= 0) {
            bufMiniFat = new byte[cMiniFat * 4];
            add(512 + sectMiniFatStart * uSectorSize, bufMiniFat, 0, uSectorSize, 2);
        }

        class DirPage {
            public byte[] buf;
            public DirPage(int size) {
                buf = new byte[size];
            }
        }
        ArrayList<DirPage> dirPages = new ArrayList<DirPage>();
        if (sectDirStart >= 0) {
            DirPage page = new DirPage(uSectorSize);
            dirPages.add(page);
            add(512 + sectDirStart * uSectorSize, page.buf, 0, uSectorSize, 2);
        }

        for (Read rd = step(); rd != null; rd = step()) {
            switch (rd.tag) {
                case 0:
                    while (sectMiniFatStart >= 0) {
                        int sect = dword(bufFat, sectMiniFatStart * 4);
                        if (sect < 0)
                            break;
                        sectMiniFatStart = sect;
                        add(512 + sectMiniFatStart * uSectorSize, bufMiniFat,
                                ++posMiniFat * uSectorSize, uSectorSize, 2);
                    }
                    while (sectDirStart >= 0) {
                        int sect = dword(bufFat, sectDirStart * 4);
                        if (sect < 0)
                            break;
                        sectDirStart = sect;
                        DirPage page = new DirPage(uSectorSize);
                        dirPages.add(page);
                        add(512 + sectDirStart * uSectorSize, page.buf, 0,
                                uSectorSize, 2);
                    }
                    break;
                case 1:
                    for (int n = 0; n < (uSectorSize / 4) - 1; n++) {
                        int s = 109 + posDif * ((uSectorSize / 4) - 1) + n;
                        if (s * (uSectorSize / 4) >= cFat)
                            break;
                        add(512 + dword(bufDif, n * 4) * uSectorSize, bufFat,
                                s * uSectorSize, uSectorSize, 0);
                    }
                    sectDifStart = dword(bufDif, uSectorSize - 4);
                    if (sectDifStart >= 0) {
                        Arrays.fill(bufDif, (byte) 0xee);
                        posDif++;
                        add(512 + sectDifStart * uSectorSize, bufDif, 0, uSectorSize, 1);
                    }
                    break;
            }
        }

        int dirsInSector = uSectorSize / 128;
        int nDirs = dirPages.size() * dirsInSector;
        dir = new ArrayList<Dir>(nDirs);
        for (int n = 0; n < nDirs; n++) {
            Dir entry = new Dir();
            byte[] buf = dirPages.get(n/dirsInSector).buf;
            int pos = (n%dirsInSector) * 128;
            StringBuilder name = new StringBuilder();
            int nameLen = word(buf, pos + 0x40) / 2 - 1;
            for (int i = 0; i < nameLen; i++)
                name.append(word(buf, pos + i * 2));
            entry.name = name.toString();
            entry.type = buf[pos + 0x42];
            entry.left = dword(buf, pos + 0x44);
            entry.right = dword(buf, pos + 0x48);
            entry.child = dword(buf, pos + 0x4C);
            entry.first = dword(buf, pos + 0x74);
            entry.size = dword(buf, pos + 0x78);
            dir.add(entry);
        }
        dirPages.clear();

        chunks.clear();
        Chunk chunk = new Chunk();
        int virtPos = 0;

        for (int n = 0; n < nDirs; n++) {
            Dir entry = dir.get(n);
            if (entry.type != 2)
                continue;
            entry.virtPos = virtPos;
            if (entry.size == 0)
                continue;
            int first, last, next;
            if (entry.size >= ulMiniSectorCutoff) {
                first = last = entry.first;
                do {
                    next = dword(bufFat, last * 4);
                    while (next == last + 1) {
                        last++;
                        next = dword(bufFat, last * 4);
                    }
                    chunk.virtPos = virtPos;
                    chunk.filePos = 512 + first * uSectorSize;
                    Chunk nextChunk = new Chunk();
                    chunk.next = new WeakReference<Chunk>(nextChunk);
                    chunks.put(virtPos, chunk);
                    chunk = nextChunk;
                    virtPos += (last - first + 1) * uSectorSize;
                    first = last = next;
                } while (first >= 0);
            } else {
                int fatFirst, fatLast, fatNext = 0;
                int fatOffFirst, fatOffLast;
                first = last = fatOffFirst = entry.first;
                do {
                    fatFirst = dir.get(0).first;
                    while (fatOffFirst >= (uSectorSize / uMiniSectorSize)) {
                        fatFirst = dword(bufFat, fatFirst * 4);
                        fatOffFirst -= (uSectorSize / uMiniSectorSize);
                    }
                    fatLast = fatFirst;
                    fatOffLast = fatOffFirst;
                    next = dword(bufMiniFat, last * 4);
                    while (next == last + 1) {
                        if (fatOffLast + 1 < (uSectorSize / uMiniSectorSize)) {
                            fatOffLast++;
                        } else {
                            fatNext = dword(bufFat, fatLast * 4);
                            if (fatNext != fatLast + 1)
                                break;
                            fatOffLast = 0;
                            fatLast = fatNext;
                        }
                        last++;
                        next = dword(bufMiniFat, last * 4);
                    }
                    chunk.virtPos = virtPos;
                    chunk.filePos = 512 + fatFirst * uSectorSize
                            + fatOffFirst * uMiniSectorSize;
                    Chunk nextChunk = new Chunk();
                    chunk.next = new WeakReference<Chunk>(nextChunk);
                    chunks.put(virtPos, chunk);
                    chunk = nextChunk;
                    virtPos += (last - first + 1) * uMiniSectorSize;
                    first = last = fatOffFirst = next;
                } while (first >= 0);
            }
        }
        chunk.virtPos = Integer.MAX_VALUE;
        chunk.filePos = Integer.MAX_VALUE;
        chunks.put(Integer.MAX_VALUE, chunk);

    }

    protected final int stream(String name) {
        if (dir == null)
            return -1;
        int len = name.length();
        int n = dir.get(0).child;
        while (n > 0) {
            Dir entry = dir.get(n);
            if (len < entry.name.length()) {
                n = entry.left;
            } else if (len > entry.name.length()) {
                n = entry.right;
            } else {
                int dif = name.compareTo(entry.name);
                if (dif < 0)
                    n = entry.left;
                else if (dif > 0)
                    n = entry.right;
                else
                    return entry.virtPos;
            }
        }
        return -1;
    }

    protected final char word(byte[] buf, int pos) {
        return (char) ((buf[pos] & 0xff) | ((buf[pos + 1] & 0xff) << 8));
    }

    protected final int dword(byte[] buf, int pos) {
        return (int) ((buf[pos] & 0xff) | ((buf[pos + 1] & 0xff) << 8)
                | ((buf[pos + 2] & 0xff) << 16) | ((buf[pos + 3] & 0xff) << 24));
    }

    public String externalFileExists(String fname) {
        return null;
    }

}
