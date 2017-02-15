package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.sdk.utils.ChineseTextUtils;
import com.onyx.android.sdk.utils.FileUtils;

/**
 * Created by joy on 9/29/16.
 */
public class ChineseTextUtilsTest extends ApplicationTestCase<Application> {
    public ChineseTextUtilsTest() {
        super(Application.class);
    }

    public void testGetBaseName() {
        Log.d(getClass().getSimpleName(), ChineseTextUtils.removeWhiteSpacesBetweenChineseText("中华 人 民共 和国 hello Word"));
        Log.d(getClass().getSimpleName(), ChineseTextUtils.removeWhiteSpacesBetweenChineseText("毛 紫 薇 Lagerstroemia villosa Wall. ex Kurz 中国 植 物图 像库"));
        Log.d(getClass().getSimpleName(), ChineseTextUtils.removeWhiteSpacesBetweenChineseText("界之伟大与自己之渺小，越读会越觉出好书之极多与所读 \r\n之 极 少 。��"));
        Log.d(getClass().getSimpleName(), ChineseTextUtils.removeWhiteSpacesBetweenChineseText("\r\n读 书 人 是 不 可 能 张 扬 的 ，读 书 人 是 不 可 能 显 摆 的 。��"));
        Log.d(getClass().getSimpleName(), ChineseTextUtils.removeWhiteSpacesBetweenChineseText(" 真 \r\n正 的 读 书 人 ， 知 道 其 所 学 、其 所 读 ， 永 远 只 是 挂 一 漏 万 ， \r\n永 远 只 是 盲 人 摸 象 。��"));
        Log.d(getClass().getSimpleName(), ChineseTextUtils.removeWhiteSpacesBetweenChineseText("\r\n这 样 的 一 些 书 ，我 们 读 一 页 ，就 有 一 页 的 “好 ”，读一 \r\n本 ，就 有 一 本 的 “益 ”。��"));
    }
}
