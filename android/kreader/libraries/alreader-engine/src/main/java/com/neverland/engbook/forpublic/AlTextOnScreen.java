package com.neverland.engbook.forpublic;

import android.graphics.Bitmap;

import com.neverland.engbook.util.AlImage;

import java.util.ArrayList;

public class AlTextOnScreen {

    public boolean needCorrectStart = false;
    public boolean needCorrectEnd = false;

    public int defaultResultForStart = 0;
    public int defaultResultForEnd = 0;

    public int correctedPositionStart = 0;
    public int correctedPositionEnd = 0;

    public int numWordWithStartSelection = -1;
    public int numWordWithEndSelection = -1;

    public class AlPieceOfText {
        public String              word;
        public final AlRect        rect = new AlRect();
        public final int[]         positions;

        public AlPieceOfText(String w, AlRect r, int[] p) {
            word = w;

            rect.x0 = r.x0;
            rect.y0 = r.y0;
            rect.x1 = r.x1;
            rect.y1 = r.y1;

            positions = p;
        }
    }

    public class AlPieceOfLink {
        public int                  pos;
        public final AlRect         rect = new AlRect();


        public AlPieceOfLink(int p, AlRect r) {
            pos = p;

            rect.x0 = r.x0;
            rect.y0 = r.y0;
            rect.x1 = r.x1;
            rect.y1 = r.y1;
        }
    }

    public class AlPieceOfImage {
        public int pos;
        public final AlRect rect = new AlRect();
        public final Bitmap bitmap;

        public AlPieceOfImage(int p, AlRect r, Bitmap b) {
            pos = p;
            rect.set(r.x0, r.y0, r.x1, r.y1);
            bitmap = b;
        }
    }

    public final ArrayList<AlPieceOfText> regionList = new ArrayList<>();
    public final ArrayList<AlPieceOfLink> linkList = new ArrayList<>();
    public final ArrayList<AlPieceOfImage> imageList = new ArrayList<>();

    public void clear() {
        numWordWithStartSelection = numWordWithEndSelection = -1;
        needCorrectStart = needCorrectEnd = false;
        regionList.clear();
        linkList.clear();
        imageList.clear();
    }

    public void addText(StringBuilder word, AlRect rect, ArrayList<Integer> pos) {
        int len = word.length();
        if (len > 0) {

            int[] p = new int[len];
            for (int i = 0; i < len; i++)
                p[i] = pos.get(i);

            AlPieceOfText a = new AlPieceOfText(word.toString(), rect, p);
            regionList.add(a);

            word.setLength(0);
        }
    }

    public void addLink(int p, AlRect rect) {
        if (p >= 0) {
            AlPieceOfLink a = new AlPieceOfLink(p, rect);
            linkList.add(a);
        }
    }

    public void addImage(int p, AlRect rect, Bitmap bitmap) {
        if (p >= 0) {
            imageList.add(new AlPieceOfImage(p, rect, bitmap));
        }
    }

    public boolean verifyStart(boolean needStart, int posStart) {
        if (needStart)
            return lastStart != posStart;
        return false;
    }

    public boolean verifyEnd(boolean needEnd, int posEnd) {
        if (needEnd)
            return lastEnd != posEnd;
        return false;
    }

    public void prepareBeforeCorrect0(boolean needStart, int posStart, boolean needEnd, int posEnd) {

        needCorrectStart = needStart;
        defaultResultForStart = posStart;
        if (needCorrectStart) {
            numWordWithStartSelection = findWordByPos(posStart);
            if (numWordWithStartSelection < 0) {
                needCorrectStart = false;
            } else {
                lastStart = posStart;
            }
        }

        needCorrectEnd = needEnd;
        defaultResultForEnd = posEnd;

        if (needCorrectEnd) {
            numWordWithEndSelection = findWordByPos(posEnd);
            if (numWordWithEndSelection < 0) {
                needCorrectEnd = false;
            } else {
                lastEnd = posEnd;
            }
        }

    }

    public void clearBeforeNormalCall() {
        lastStart = -1;
        lastEnd = -1;
    }

    private int lastStart = -1;
    private int lastEnd = -1;

    private int findWordByPos(int pos) {

        int s, e;

        for (int i = 0; i < regionList.size(); i++) {
            AlPieceOfText a = regionList.get(i);

            s = a.positions[0];
            e = a.positions[a.positions.length - 1];

            if (pos >= s && pos <= e)
                return i;
        }

        return -1;
    }

}
