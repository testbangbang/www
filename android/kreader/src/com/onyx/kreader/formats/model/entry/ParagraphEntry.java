package com.onyx.kreader.formats.model.entry;

import java.util.List;

/**
 * Created by zengzhu on 3/1/16.
 */
public class ParagraphEntry {

    static public enum EntryKind {
        TEXT_ENTRY,
        IMAGE_ENTRY,
        CONTROL_ENTRY,
        HYPERLINK_CONTROL_ENTRY,
        STYLE_CSS_ENTRY,
        STYLE_OTHER_ENTRY,
        STYLE_CLOSE_ENTRY,
        FIXED_HSPACE_ENTRY,
        RESET_BIDI_ENTRY,
        AUDIO_ENTRY,
        VIDEO_ENTRY,
        EXTENSION_ENTRY,
    }

    protected EntryKind entryKind;

    public final EntryKind getEntryKind() {
        return entryKind;
    }

}
