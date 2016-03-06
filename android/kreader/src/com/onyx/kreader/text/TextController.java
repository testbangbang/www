package com.onyx.kreader.text;

import com.onyx.kreader.formats.model.entry.ParagraphEntry;
import com.onyx.kreader.formats.model.entry.TextParagraphEntry;
import com.onyx.kreader.utils.UnicodeUtils;

/**
 * Created by zengzhu on 3/4/16.
 */
public class TextController {

    public boolean addEntryToLayout(final TextLayoutContext context, final ParagraphEntry entry, final Style style) {

        if (entry == null || entry.getEntryKind() != ParagraphEntry.EntryKind.TEXT_ENTRY) {
            return false;
        }
        final TextParagraphEntry textParagraphEntry = (TextParagraphEntry)entry;
        int index = -1;

        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            Character character = textParagraphEntry.nextCharacter(index++);
            if (character == null) {
                break;
            }
            stringBuilder.append(character);
            if (UnicodeUtils.isWhitespace(character)) {
                TextElement textElement = TextElement.create(stringBuilder.toString(), style);
                context.addElement(textElement);
                stringBuilder.setLength(0);
            } else if (UnicodeUtils.isCJKCharacter(character)) {
                TextElement textElement = TextElement.create(stringBuilder.toString(), style);
                context.addElement(textElement);
                stringBuilder.setLength(0);
                // create element
            } else {
                // continue collecting characters.

            }
        }
        return true;
    }

    static public abstract class TextControllerCallback {

        abstract public Character nextCharacter();

    }


    public boolean addWithCallback() {
        return false;
    }
}
