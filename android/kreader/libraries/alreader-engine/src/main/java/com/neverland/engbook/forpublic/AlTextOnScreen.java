package com.neverland.engbook.forpublic;

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
        public final AlRect rect = new AlRect();
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

    public class AlTextLink {
        public int startPosition = -1;
        public int endPosition;
        public int linkLocalPosition;
    }

    public ArrayList<AlPieceOfText> regionList = new ArrayList<>();
    public ArrayList<AlTextLink> linkList = new ArrayList<>();

    private boolean isLink = false;
    private int linkPosition = -1;
    private AlTextLink link;

    public void clear() {
        numWordWithStartSelection = numWordWithEndSelection = -1;
        needCorrectStart = needCorrectEnd = false;
        regionList.clear();
    }

    public void add(StringBuilder word, AlRect rect, ArrayList<Integer> pos) {
        int len = word.length();
        if (len > 0) {

            int[] p = new int[len];
            for (int i = 0; i < len; i++)
                p[i] = pos.get(i);

            AlPieceOfText a = new AlPieceOfText(word.toString(), rect, p);
            regionList.add(a);

            word.setLength(0);

            if (isLink) {
                if (link == null) {
                    link = new AlTextLink();
                    linkList.add(link);
                    link.startPosition = pos.get(0);
                    link.endPosition = pos.get(pos.size() - 1);
                    link.linkLocalPosition = linkPosition;
                }
                link.endPosition = pos.get(pos.size() - 1);
            }
        }
    }

    public void markLinkStart(int linkPosition) {
        isLink = true;
        this.linkPosition = linkPosition;
    }

    public void markLinkEnd() {
        isLink = false;
        linkPosition = -1;
        link = null;
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

    public int findWordByPos(int pos) {

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
