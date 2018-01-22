package com.onyx.jdread.manager;

import com.kingsoft.iciba.sdk2.KSCibaEngine;
import com.kingsoft.iciba.sdk2.interfaces.IKSCibaQueryResult;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class KSICibaTranslate {
    private static KSICibaTranslate instance = null;
    private static KSCibaEngine mKsCibaEngine = null;
    static long mCompanyID = 65892187L;
    private static int mSearchType = 2;
    public static final String BR = "<br>";
    public static final String DICT_PATH = "dicts";

    private static String seachKeyWords;

    static String dictPath = JDReadApplication.getInstance().getCacheDir() + File.separator + DICT_PATH;

    public static KSICibaTranslate getInstance() {
        if (instance == null) {
            instance = new KSICibaTranslate();
            mKsCibaEngine = new KSCibaEngine(JDReadApplication.getInstance());
            dictPath = Device.currentDevice.getExternalStorageDirectory() + File.separator + DICT_PATH;
            mKsCibaEngine.installEngine(dictPath, mCompanyID);
        }
        return instance;
    }

    public static void refreshEngine() {
        mKsCibaEngine.installEngine(dictPath, mCompanyID);
    }

    public void getTranslateResult(String keywords, IKSCibaQueryResult iksCibaQueryResult) {
        if (StringUtils.isNotBlank(keywords)) {
            seachKeyWords = keywords;
            mKsCibaEngine.startSearchWord(keywords.trim(), mSearchType, iksCibaQueryResult);
        }
    }

    static public class OnyxIKSCibaQueryResult implements IKSCibaQueryResult {
        private JSONObject result, part, symbol, message, baseInfo, ccMean, spellObject;
        private JSONArray symbols, parts, means, suggests, spells;
        private String status, resultStr, partstr, translate_type, translate_result, translate_msg, word_symbol, spell;
        private StringBuffer strBuffer;
        private TranslateResult translateResult;

        public interface TranslateResult {
            void translateResult(String result);
        }

        public OnyxIKSCibaQueryResult(TranslateResult translateResult) {
            this.translateResult = translateResult;
        }

        @Override
        public void searchResult(final String arg0) {
            if (arg0 == null) {
                translateResult.translateResult(JDReadApplication.getInstance().getString(R.string.missing_translation_tools));
                return;
            }
            try {
                strBuffer = new StringBuffer();
                result = new JSONObject(arg0);
                status = result.optString("status");
                message = result.optJSONObject("message");
                baseInfo = message.optJSONObject("baseInfo");
                if (status.equals("1")) {
                    parseBaseInfo();
                    ccMean = message.optJSONObject("cc_mean");
                    if (ccMean != null && !ccMean.equals("")) {
                        parseCcMean();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (strBuffer.length() == 0) {
                strBuffer.append(message.opt("result_info"));
            }
            resultStr = strBuffer.toString();
            if (translateResult != null) {
                translateResult.translateResult(resultStr);
            }
            strBuffer.setLength(0);
        }

        private void parseCcMean() throws JSONException {
            spells = ccMean.optJSONArray("spells");
            if (spells != null && spells.length() > 0) {
                for (int n = 0; n < spells.length(); n++) {
                    spellObject = spells.getJSONObject(n);
                    spell = spellObject.optString("spell");
                    if (strBuffer.length() != 0) {
                        strBuffer.append(BR + BR);
                    }
                    strBuffer.append("[" + spell + "]");
                    means = spellObject.optJSONArray("means");
                    if (means != null) {
                        for (int i = 0; i < means.length(); i++) {
                            if (strBuffer.length() != 0) {
                                strBuffer.append(BR);
                            }
                            strBuffer.append(means.optString(i));
                        }
                    }
                }
            }
        }

        private void parseBaseInfo() throws JSONException {
            if (baseInfo != null && !baseInfo.equals("")) {
                translate_type = baseInfo.optString("translate_type");
                if (translate_type.equals("1")) {
                    symbols = baseInfo.getJSONArray("symbols");
                    for (int n = 0; n < symbols.length(); n++) {
                        symbol = symbols.getJSONObject(n);
                        word_symbol = symbol.optString("word_symbol");
                        if (strBuffer.length() != 0) {
                            strBuffer.append(BR + BR);
                        }
                        strBuffer.append("[" + word_symbol + "]");
                        parts = symbol.getJSONArray("parts");
                        for (int i = 0; i < parts.length(); i++) {
                            part = parts.getJSONObject(i);
                            if (strBuffer.length() != 0) {
                                strBuffer.append(BR);
                            }
                            partstr = part.optString("part");
                            if (!partstr.equals("")) {
                                strBuffer.append(partstr + BR);
                            }
                            means = part.getJSONArray("means");
                            for (int k = 0; k < means.length(); k++) {
                                strBuffer.append(means.optString(k) + "; ");
                            }
                        }
                    }
                } else if (translate_type.equals("2")) {
                    translate_result = baseInfo.optString("translate_result");
                    if (!translate_result.equals("")) {
                        strBuffer.append(translate_result);
                        translate_msg = baseInfo.optString("translate_msg");
                        if (!translate_msg.equals("")) {
                            strBuffer.append(translate_msg);
                        }
                    }
                } else if (translate_type.equals("3")) {
                    suggests = baseInfo.getJSONArray("suggest");
                    for (int i = 0; i < suggests.length(); i++) {
                        strBuffer.append(suggests.getJSONObject(i).optString("key"));
                    }
                }
            }
        }
    }
}
