package com.neverland.engbook.level2;

public abstract class AlScan extends AlAXML{

    public void reset() {

    }

    @Override
    protected void doTextChar(char ch, boolean addSpecial) {

        if (allState.state_special_flag0 && ch != 0x00) {
            if (ch == 0x20 || ch == 0xa0 || ch == 0x09) {
                if (state_specialBuff0.length() > 0 && state_specialBuff0.charAt(state_specialBuff0.length() - 1) != 0x20)
                    state_specialBuff0.append(' ');
            } else
           /* if (ch == 0x0a) {
                state_specialBuff0.append(ch);
            } else*/
            if (ch < 0x20) {

            } else {
                state_specialBuff0.append(ch);
            }
        }

    }

    @Override
    void newParagraph() {

    }

    @Override
    public String toString() {
        return "\r\n" + ident + " cp:" + Integer.toString(use_cpR0) + "\r\n";
    }
}
