package com.neverland.engbook.level2;

public abstract class AlScan extends AlAXML{

    public void reset() {

    }

    @Override
    protected void doTextChar(char ch, boolean addSpecial) {

        if (allState.state_special_flag && ch != 0x00) {
            if (ch == 0x20 || ch == 0xa0 || ch == 0x09) {
                if (specialBuff.buff.length() > 0 && specialBuff.buff.charAt(specialBuff.buff.length() - 1) != 0x20)
                    specialBuff.buff.append(' ');
            } else
           /* if (ch == 0x0a) {
                state_specialBuff0.append(ch);
            } else*/
            if (ch < 0x20) {

            } else {
                specialBuff.buff.append(ch);
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
