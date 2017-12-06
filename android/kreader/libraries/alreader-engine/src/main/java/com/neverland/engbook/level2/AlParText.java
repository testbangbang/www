package com.neverland.engbook.level2;

public class AlParText {
    public char[]						buffer = null;
    public int							size;
    public int							length;

    public int							positionS;
    public int							positionE;
    public int							sizeStart;
    public long							paragraph;
    public long							prop;
    public int							level;
    public int							tableStart;
    public int						tableCounter;
    public boolean							haveLetter;


    public void add(char ch) {
        if (length >= size) {
            size += 512;
            char tmp[] = new char[size];
            System.arraycopy(buffer, 0, tmp, 0, length);
            buffer = tmp;
        }
        buffer[length++] = ch;
    }

    public int copy(AlOneParagraph a) {
        if (a.ptext == null)
            a.ptext = new char[length];
        System.arraycopy(buffer, 0, a.ptext, 0, length);
        return length;
    }

    public void clear() {
        length = 0;
        haveLetter = false;
        //tableStart = -1;
        //tableCounter = 0;
    }

    public AlParText() {
        size = 512;
        buffer = new char[size];
        length = 0;
    }

}
