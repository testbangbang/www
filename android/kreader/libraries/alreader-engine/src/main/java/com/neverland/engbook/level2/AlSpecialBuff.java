package com.neverland.engbook.level2;


public class AlSpecialBuff {
    public StringBuilder		buff = new StringBuilder();

    public boolean						isBookTitle = false;
    public boolean						isTitle = false;
    public boolean						isCSSStyle = false;
    public boolean						isContent = false;
    public boolean						isGenre = false;
    public boolean						isAuthor = false;
    public boolean						isAuthorFirst = false;
    public boolean						isAuthorLast = false;
    public boolean						isAuthorNick = false;
    public boolean						isAuthorMiddle = false;
    public boolean						isProgramUsed = false;
    public boolean						isTOC = false;;
    public boolean						isTitle0 = false;
    public boolean						isTitle1 = false;
    public boolean						isGenreList = false;

    public AlSpecialBuff() {
        clear();
    }

    public void clear() {
        buff.setLength(0);
    }

    public void add(char ch) {
        buff.append(ch);
    }
}
