package com.neverland.engbook;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.neverland.engbook.util.ChineseTextSectionRecognizer;

/**
 * Created by joy on 2/14/17.
 */

public class ChineseTextSectionRecognizerTest extends ApplicationTestCase<Application> {

    public ChineseTextSectionRecognizerTest() {
        super(Application.class);
    }

    private void matchWith(ChineseTextSectionRecognizer recognizer, String text, boolean isValidText) {
        recognizer.reset();
        char[] array = text.toCharArray();
        for (char ch : array) {
            if (isValidText) {
                assertTrue(recognizer.onNewCharacter(ch));
            }
        }
        assertEquals(recognizer.matches(), isValidText);
        if (isValidText) {
            assertEquals(text, recognizer.getSectionText());
        }
    }

    public void testChineseSections() {
        ChineseTextSectionRecognizer recognizer = ChineseTextSectionRecognizer.create();
        matchWith(recognizer, "第一卷", true);
        matchWith(recognizer, "第235章 出击", true);
        matchWith(recognizer, "第一章", true);
        matchWith(recognizer, "第一章 ", true);
        matchWith(recognizer, "第一章 少年壮志不言愁", true);
        matchWith(recognizer, "　　第五章 高岗的失误 18 ", true);
        matchWith(recognizer, "   第二篇 反教条中刘伯承和粟裕", true);
        matchWith(recognizer, "　　第四章 略微比较一下刘伯承和粟裕的指挥风格 38", true);

        matchWith(recognizer, "　　x第四章 略微比较一下刘伯承和粟裕的指挥风格 38", false);
        matchWith(recognizer, "　　第 四章 略微比较一下刘伯承和粟裕的指挥风格 38", false);
        matchWith(recognizer, "　　第 四 章 略微比较一下刘伯承和粟裕的指挥风格 38", false);
        matchWith(recognizer, "　　第x四章 略微比较一下刘伯承和粟裕的指挥风格 38", false);
        matchWith(recognizer, "　　第四x章 略微比较一下刘伯承和粟裕的指挥风格 38", false);
    }
}
