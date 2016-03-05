package com.onyx.kreader.text;

import com.onyx.kreader.formats.model.entry.ParagraphEntry;
import com.onyx.kreader.formats.model.entry.TextParagraphEntry;
import com.onyx.kreader.utils.UnicodeUtils;

/**
 * Created by zengzhu on 3/4/16.
 */
public class TextController {

    public boolean addEntryToLayout(final TextLayoutContext context, final ParagraphEntry entry) {

        if (entry == null || entry.getEntryKind() != ParagraphEntry.EntryKind.TEXT_ENTRY) {
            return false;
        }
        final TextParagraphEntry textParagraphEntry = (TextParagraphEntry)entry;
        int index = -1;

        while (true) {
            Character character = textParagraphEntry.nextCharacter(index++);
            if (character == null) {
                break;
            }
            if (UnicodeUtils.isCJKCharacter(character)) {
                // create element
            } else {
                // continue collecting characters.

            }
        }
        return true;
    }

}
