package com.neverland.engbook.forpublic;

import java.io.File;

public class AlResourceFont {
    public final String aName;
    public String[] aFile = {null, null, null, null};

    public AlResourceFont(final String name, String normal, String bold, String italic, String boldItalic) {
        aName = name;
        aFile[0] = normal;
        aFile[1] = bold;
        aFile[2] = italic;
        aFile[3] = boldItalic;
    }
}
