package com.onyx.android.libsetting.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.Preference;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.model.LocaleInfo;
import com.onyx.android.sdk.utils.CompatibilityUtil;
import com.onyx.android.sdk.utils.ReflectUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by solskjaer49 on 2016/12/05 10:31.
 */
public class InputMethodLanguageSettingUtil {
    private static final boolean DEBUG = false;

    private static final char INPUT_METHOD_SEPARATOR = ':';
    private static final char INPUT_METHOD_SUBTYPE_SEPARATOR = ';';
    private static final int NOT_A_SUBTYPE_ID = -1;

    private static final String DISABLED_SYSTEM_INPUT_METHODS = "disabled_system_input_methods";

    private static final String SYSTEM_PRESERVED_IME = "com.android.inputmethod.latin/.LatinIME";

    private static final String TAG = InputMethodLanguageSettingUtil.class.getSimpleName();

    public static boolean isInputMethodPreference(List<InputMethodInfo> mImis, Preference preference) {
        for (InputMethodInfo inputMethodInfo : mImis) {
            if (inputMethodInfo.getId().equals(preference.getKey())) {
                return true;
            }
        }
        return false;
    }

    public static List<InputMethodInfo> getCurrentEnableIMEList(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.getEnabledInputMethodList();
    }

    public static List<InputMethodInfo> getInstalledIMEList(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.getInputMethodList();
    }

    public static InputMethodInfo getDefaultIMEInfo(Context context) {
        for (InputMethodInfo info : getCurrentEnableIMEList(context)) {
            if (info.getId().equalsIgnoreCase(getDefaultIMEID(context))) {
                return info;
            }
        }
        return null;
    }

    public static CharSequence getDefaultIMEName(Context context) {
        for (InputMethodInfo info : getCurrentEnableIMEList(context)) {
            if (info.getId().equalsIgnoreCase(getDefaultIMEID(context))) {
                return info.loadLabel(context.getPackageManager());
            }
        }
        return null;
    }

    public static boolean isSpecificIMEEnabled(Context context, String imeID) {
        for (InputMethodInfo info : getCurrentEnableIMEList(context)) {
            if (info.getId().equalsIgnoreCase(imeID)) {
                return true;
            }
        }
        return false;
    }

    public static void setSpecificIMEEnabled(Context context, String imeID, boolean enabled) {
        for (InputMethodInfo info : getInstalledIMEList(context)) {
            if (info.getId().equalsIgnoreCase(imeID)) {
                String targetString;
                if (enabled) {
                    targetString = Settings.Secure.getString(
                            context.getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS);
                    if (targetString.contains(imeID)) {
                        return;
                    }
                    targetString = targetString + INPUT_METHOD_SEPARATOR + imeID;

                } else {
                    targetString = Settings.Secure.getString(
                            context.getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS).replace((INPUT_METHOD_SEPARATOR + imeID), "");
                }
                Settings.Secure.putString(context.getContentResolver(),
                        Settings.Secure.ENABLED_INPUT_METHODS, targetString);
            }
        }
    }

    public static void setSpecificIMEDefault(Context context, String imeID) {
        Settings.Secure.putString(
                context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, imeID);
    }

    public static String getDefaultIMEID(Context context) {
        return Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
    }

    public static void updateLocale(Locale locale) {
        try {
            Class<?> localePickerClass = ReflectUtil.classForName("com.android.internal.app.LocalePicker");
            Method methodLocalePicker = ReflectUtil.getMethodSafely(localePickerClass, "updateLocale", Locale.class);
            ReflectUtil.invokeMethodSafely(methodLocalePicker, null, locale);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentLanguage(Context context) {
        Configuration conf = context.getResources().getConfiguration();
        Locale locale;
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.N)) {
            locale = conf.getLocales().get(0);
        } else {
            locale = conf.locale;
        }
        String langName = locale.getDisplayName();
        if (langName != null && langName.length() > 1) {
            langName = Character.toUpperCase(langName.charAt(0)) + langName.substring(1);
        }
        return langName;
    }

    public static List<LocaleInfo> buildLanguageList(Context context) {
        String[] locales = Resources.getSystem().getAssets().getLocales();
        final int origSize = locales.length;
        final LocaleInfo[] preprocess = new LocaleInfo[origSize];
        final String[] specialLocaleCodes = context.getResources().getStringArray(R.array.special_locale_codes);
        final String[] specialLocaleNames = context.getResources().getStringArray(R.array.special_locale_names);
        int finalSize = 0;
        Arrays.sort(locales);
        for (final String s : locales) {
            final int len = s.length();
            if (len == 5) {
                String language = s.substring(0, 2);
                String country = s.substring(3, 5);
                final Locale l = new Locale(language, country);
                if (finalSize == 0) {
                    preprocess[finalSize++] = new LocaleInfo(toTitleCase(l.getDisplayLanguage(l)), l);
                } else {
                    // check previous entry:
                    //  same lang and a country -> upgrade to full name and
                    //    insert ours with full name
                    //  diff lang -> insert ours with lang-only name
                    if (preprocess[finalSize - 1].getLocale().getLanguage().equals(language)) {
                        preprocess[finalSize - 1].setLabel(toTitleCase(getDisplayName(preprocess[finalSize - 1].getLocale(),
                                specialLocaleCodes, specialLocaleNames)));
                        preprocess[finalSize++] = new LocaleInfo(toTitleCase(getDisplayName(l, specialLocaleCodes, specialLocaleNames)), l);
                    } else {
                        String displayName;
                        if (s.equals("zz_ZZ")) {
                            displayName = "Pseudo...";
                        } else {
                            displayName = toTitleCase(l.getDisplayLanguage(l));
                        }
                        preprocess[finalSize++] = new LocaleInfo(displayName, l);
                    }
                }
            }
        }
        ArrayList<LocaleInfo> dataList = new ArrayList<>(Arrays.asList(preprocess));
        dataList.removeAll(Collections.singleton(null));
        return dataList;
    }

    private static String getDisplayName(
            Locale l, String[] specialLocaleCodes, String[] specialLocaleNames) {
        String code = l.toString();
        for (int i = 0; i < specialLocaleCodes.length; i++) {
            if (specialLocaleCodes[i].equals(code)) {
                return specialLocaleNames[i];
            }
        }
        return l.getDisplayName(l);
    }

    private static String toTitleCase(String s) {
        if (s.length() == 0) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static final TextUtils.SimpleStringSplitter sStringInputMethodSplitter
            = new TextUtils.SimpleStringSplitter(INPUT_METHOD_SEPARATOR);

    private static final TextUtils.SimpleStringSplitter sStringInputMethodSubtypeSplitter
            = new TextUtils.SimpleStringSplitter(INPUT_METHOD_SUBTYPE_SEPARATOR);

    private static void buildEnabledInputMethodsString(
            StringBuilder builder, String imi, HashSet<String> subtypes) {
        builder.append(imi);
        // Inputmethod and subtypes are saved in the settings as follows:
        // ime0;subtype0;subtype1:ime1;subtype0:ime2:ime3;subtype0;subtype1
        for (String subtypeId : subtypes) {
            builder.append(INPUT_METHOD_SUBTYPE_SEPARATOR).append(subtypeId);
        }
    }

    public static void buildInputMethodsAndSubtypesString(
            StringBuilder builder, HashMap<String, HashSet<String>> imsList) {
        boolean needsAppendSeparator = false;
        for (String imi : imsList.keySet()) {
            if (needsAppendSeparator) {
                builder.append(INPUT_METHOD_SEPARATOR);
            } else {
                needsAppendSeparator = true;
            }
            buildEnabledInputMethodsString(builder, imi, imsList.get(imi));
        }
    }

    public static void buildDisabledSystemInputMethods(
            StringBuilder builder, HashSet<String> imes) {
        boolean needsAppendSeparator = false;
        for (String ime : imes) {
            if (needsAppendSeparator) {
                builder.append(INPUT_METHOD_SEPARATOR);
            } else {
                needsAppendSeparator = true;
            }
            builder.append(ime);
        }
    }

    private static int getInputMethodSubtypeSelected(ContentResolver resolver) {
        try {
            return Settings.Secure.getInt(resolver,
                    Settings.Secure.SELECTED_INPUT_METHOD_SUBTYPE);
        } catch (Settings.SettingNotFoundException e) {
            return NOT_A_SUBTYPE_ID;
        }
    }

    private static boolean isInputMethodSubtypeSelected(ContentResolver resolver) {
        return getInputMethodSubtypeSelected(resolver) != NOT_A_SUBTYPE_ID;
    }

    private static void putSelectedInputMethodSubtype(ContentResolver resolver, int hashCode) {
        Settings.Secure.putInt(resolver, Settings.Secure.SELECTED_INPUT_METHOD_SUBTYPE, hashCode);
    }

    // Needs to modify InputMethodManageService if you want to change the format of saved string.
    private static HashMap<String, HashSet<String>> getEnabledInputMethodsAndSubtypeList(
            ContentResolver resolver) {
        final String enabledInputMethodsStr = Settings.Secure.getString(
                resolver, Settings.Secure.ENABLED_INPUT_METHODS);
        HashMap<String, HashSet<String>> imsList
                = new HashMap<>();
        if (DEBUG) {
            Log.d(TAG, "--- Load enabled input methods: " + enabledInputMethodsStr);
        }

        if (TextUtils.isEmpty(enabledInputMethodsStr)) {
            return imsList;
        }
        sStringInputMethodSplitter.setString(enabledInputMethodsStr);
        while (sStringInputMethodSplitter.hasNext()) {
            String nextImsStr = sStringInputMethodSplitter.next();
            sStringInputMethodSubtypeSplitter.setString(nextImsStr);
            if (sStringInputMethodSubtypeSplitter.hasNext()) {
                HashSet<String> subtypeHashes = new HashSet<>();
                // The first element is ime id.
                String imeId = sStringInputMethodSubtypeSplitter.next();
                while (sStringInputMethodSubtypeSplitter.hasNext()) {
                    subtypeHashes.add(sStringInputMethodSubtypeSplitter.next());
                }
                imsList.put(imeId, subtypeHashes);
            }
        }
        return imsList;
    }

    private static HashSet<String> getDisabledSystemIMEs(ContentResolver resolver) {
        HashSet<String> set = new HashSet<>();
        String disabledIMEsStr = Settings.Secure.getString(
                resolver, DISABLED_SYSTEM_INPUT_METHODS);
        if (TextUtils.isEmpty(disabledIMEsStr)) {
            return set;
        }
        sStringInputMethodSplitter.setString(disabledIMEsStr);
        while (sStringInputMethodSplitter.hasNext()) {
            set.add(sStringInputMethodSplitter.next());
        }
        return set;
    }

    public static CharSequence getCurrentInputMethodName(Context context, ContentResolver resolver,
                                                         InputMethodManager imm, List<InputMethodInfo> imis, PackageManager pm) {
        if (resolver == null || imis == null) return null;
        final String currentInputMethodId = Settings.Secure.getString(resolver,
                Settings.Secure.DEFAULT_INPUT_METHOD);
        if (TextUtils.isEmpty(currentInputMethodId)) return null;
        for (InputMethodInfo imi : imis) {
            if (currentInputMethodId.equals(imi.getId())) {
                final InputMethodSubtype subtype = imm.getCurrentInputMethodSubtype();
                final CharSequence imiLabel = imi.loadLabel(pm);
                final CharSequence summary = subtype != null
                        ? TextUtils.concat(subtype.getDisplayName(context,
                        imi.getPackageName(), imi.getServiceInfo().applicationInfo),
                        (TextUtils.isEmpty(imiLabel) ?
                                "" : " - " + imiLabel))
                        : imiLabel;
                return summary;
            }
        }
        return null;
    }

    //    public static void saveInputMethodSubtypeList(Context context,
//                                                  ContentResolver resolver, List<InputMethodInfo> inputMethodInfos,
//                                                  boolean hasHardKeyboard) {
//        String currentInputMethodId = Settings.Secure.getString(resolver,
//                Settings.Secure.DEFAULT_INPUT_METHOD);
//        final int selectedInputMethodSubtype = getInputMethodSubtypeSelected(resolver);
//        HashMap<String, HashSet<String>> enabledIMEAndSubtypesMap =
//                getEnabledInputMethodsAndSubtypeList(resolver);
//        HashSet<String> disabledSystemIMEs = getDisabledSystemIMEs(resolver);
//
//        final boolean onlyOneIME = inputMethodInfos.size() == 1;
//        boolean needsToResetSelectedSubtype = false;
//        for (InputMethodInfo imi : inputMethodInfos) {
//            final String imiId = imi.getId();
//            Preference pref = context.findPreference(imiId);
//            if (pref == null) continue;
//            // In the Configure input method screen or in the subtype enabler screen.
//            // pref is instance of CheckBoxPreference in the Configure input method screen.
//            final boolean isImeChecked = (pref instanceof CheckBoxPreference) ?
//                    ((CheckBoxPreference) pref).isChecked()
//                    : enabledIMEAndSubtypesMap.containsKey(imiId);
//            final boolean isCurrentInputMethod = imiId.equals(currentInputMethodId);
//            final boolean auxIme = isAuxiliaryIme(imi);
//            final boolean systemIme = isSystemIme(imi);
//            if (((onlyOneIME || (systemIme && !auxIme)) && !hasHardKeyboard) || isImeChecked) {
//                if (!enabledIMEAndSubtypesMap.containsKey(imiId)) {
//                    // imiId has just been enabled
//                    enabledIMEAndSubtypesMap.put(imiId, new HashSet<String>());
//                }
//                HashSet<String> subtypesSet = enabledIMEAndSubtypesMap.get(imiId);
//
//                boolean subtypePrefFound = false;
//                final int subtypeCount = imi.getSubtypeCount();
//                for (int i = 0; i < subtypeCount; ++i) {
//                    InputMethodSubtype subtype = imi.getSubtypeAt(i);
//                    final String subtypeHashCodeStr = String.valueOf(subtype.hashCode());
//                    CheckBoxPreference subtypePref = (CheckBoxPreference) context.findPreference(
//                            imiId + subtypeHashCodeStr);
//                    // In the Configure input method screen which does not have subtype preferences.
//                    if (subtypePref == null) continue;
//                    if (!subtypePrefFound) {
//                        // Once subtype checkbox is found, subtypeSet needs to be cleared.
//                        // Because of system change, hashCode value could have been changed.
//                        subtypesSet.clear();
//                        // If selected subtype preference is disabled, needs to reset.
//                        needsToResetSelectedSubtype = true;
//                        subtypePrefFound = true;
//                    }
//                    if (subtypePref.isChecked()) {
//                        subtypesSet.add(subtypeHashCodeStr);
//                        if (isCurrentInputMethod) {
//                            if (selectedInputMethodSubtype == subtype.hashCode()) {
//                                // Selected subtype is still enabled, there is no need to reset
//                                // selected subtype.
//                                needsToResetSelectedSubtype = false;
//                            }
//                        }
//                    } else {
//                        subtypesSet.remove(subtypeHashCodeStr);
//                    }
//                }
//            } else {
//                enabledIMEAndSubtypesMap.remove(imiId);
//                if (isCurrentInputMethod) {
//                    // We are processing the current input method, but found that it's not enabled.
//                    // This means that the current input method has been uninstalled.
//                    // If currentInputMethod is already uninstalled, InputMethodManagerService will
//                    // find the applicable IME from the history and the system locale.
//                    if (DEBUG) {
//                        Log.d(TAG, "Current IME was uninstalled or disabled.");
//                    }
//                    currentInputMethodId = null;
//                }
//            }
//            // If it's a disabled system ime, add it to the disabled list so that it
//            // doesn't get enabled automatically on any changes to the package list
//            if (systemIme && hasHardKeyboard) {
//                if (disabledSystemIMEs.contains(imiId)) {
//                    if (isImeChecked) {
//                        disabledSystemIMEs.remove(imiId);
//                    }
//                } else {
//                    if (!isImeChecked) {
//                        disabledSystemIMEs.add(imiId);
//                    }
//                }
//            }
//        }
//
//        StringBuilder builder = new StringBuilder();
//        buildInputMethodsAndSubtypesString(builder, enabledIMEAndSubtypesMap);
//        StringBuilder disabledSysImesBuilder = new StringBuilder();
//        buildDisabledSystemInputMethods(disabledSysImesBuilder, disabledSystemIMEs);
//        if (DEBUG) {
//            Log.d(TAG, "--- Save enabled inputmethod settings. :" + builder.toString());
//            Log.d(TAG, "--- Save disable system inputmethod settings. :"
//                    + disabledSysImesBuilder.toString());
//            Log.d(TAG, "--- Save default inputmethod settings. :" + currentInputMethodId);
//            Log.d(TAG, "--- Needs to reset the selected subtype :" + needsToResetSelectedSubtype);
//            Log.d(TAG, "--- Subtype is selected :" + isInputMethodSubtypeSelected(resolver));
//        }
//
//        // Redefines SelectedSubtype when all subtypes are unchecked or there is no subtype
//        // selected. And if the selected subtype of the current input method was disabled,
//        // We should reset the selected input method's subtype.
//        if (needsToResetSelectedSubtype || !isInputMethodSubtypeSelected(resolver)) {
//            if (DEBUG) {
//                Log.d(TAG, "--- Reset inputmethod subtype because it's not defined.");
//            }
//            putSelectedInputMethodSubtype(resolver, NOT_A_SUBTYPE_ID);
//        }
//
//        Settings.Secure.putString(resolver,
//                Settings.Secure.ENABLED_INPUT_METHODS, builder.toString());
//        if (disabledSysImesBuilder.length() > 0) {
//            Settings.Secure.putString(resolver, DISABLED_SYSTEM_INPUT_METHODS,
//                    disabledSysImesBuilder.toString());
//        }
//        // If the current input method is unset, InputMethodManagerService will find the applicable
//        // IME from the history and the system locale.
//        Settings.Secure.putString(resolver, Settings.Secure.DEFAULT_INPUT_METHOD,
//                currentInputMethodId != null ? currentInputMethodId : "");
//    }
//
//    public static void loadInputMethodSubtypeList(
//            SettingsPreferenceFragment context, ContentResolver resolver,
//            List<InputMethodInfo> inputMethodInfos,
//            final Map<String, List<Preference>> inputMethodPrefsMap) {
//        HashMap<String, HashSet<String>> enabledSubtypes =
//                getEnabledInputMethodsAndSubtypeList(resolver);
//
//        for (InputMethodInfo imi : inputMethodInfos) {
//            final String imiId = imi.getId();
//            Preference pref = context.findPreference(imiId);
//            if (pref != null && pref instanceof CheckBoxPreference) {
//                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) pref;
//                boolean isEnabled = enabledSubtypes.containsKey(imiId);
//                checkBoxPreference.setChecked(isEnabled);
//                if (inputMethodPrefsMap != null) {
//                    for (Preference childPref : inputMethodPrefsMap.get(imiId)) {
//                        childPref.setEnabled(isEnabled);
//                    }
//                }
//                setSubtypesPreferenceEnabled(context, inputMethodInfos, imiId, isEnabled);
//            }
//        }
//        updateSubtypesPreferenceChecked(context, inputMethodInfos, enabledSubtypes);
//    }
//
//    public static void setSubtypesPreferenceEnabled(SettingsPreferenceFragment context,
//                                                    List<InputMethodInfo> inputMethodProperties, String id, boolean enabled) {
//        PreferenceScreen preferenceScreen = context.getPreferenceScreen();
//        for (InputMethodInfo imi : inputMethodProperties) {
//            if (id.equals(imi.getId())) {
//                final int subtypeCount = imi.getSubtypeCount();
//                for (int i = 0; i < subtypeCount; ++i) {
//                    InputMethodSubtype subtype = imi.getSubtypeAt(i);
//                    CheckBoxPreference pref = (CheckBoxPreference) preferenceScreen.findPreference(
//                            id + subtype.hashCode());
//                    if (pref != null) {
//                        pref.setEnabled(enabled);
//                    }
//                }
//            }
//        }
//    }
//
//    public static void updateSubtypesPreferenceChecked(SettingsPreferenceFragment context,
//                                                       List<InputMethodInfo> inputMethodProperties,
//                                                       HashMap<String, HashSet<String>> enabledSubtypes) {
//        PreferenceScreen preferenceScreen = context.getPreferenceScreen();
//        for (InputMethodInfo imi : inputMethodProperties) {
//            String id = imi.getId();
//            if (!enabledSubtypes.containsKey(id)) break;
//            final HashSet<String> enabledSubtypesSet = enabledSubtypes.get(id);
//            final int subtypeCount = imi.getSubtypeCount();
//            for (int i = 0; i < subtypeCount; ++i) {
//                InputMethodSubtype subtype = imi.getSubtypeAt(i);
//                String hashCode = String.valueOf(subtype.hashCode());
//                if (DEBUG) {
//                    Log.d(TAG, "--- Set checked state: " + "id" + ", " + hashCode + ", "
//                            + enabledSubtypesSet.contains(hashCode));
//                }
//                CheckBoxPreference pref = (CheckBoxPreference) preferenceScreen.findPreference(
//                        id + hashCode);
//                if (pref != null) {
//                    pref.setChecked(enabledSubtypesSet.contains(hashCode));
//                }
//            }
//        }
//    }
//

    public static boolean isSystemIme(InputMethodInfo property) {
        return (property.getServiceInfo().applicationInfo.flags
                & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    public static boolean isSystemPreservedIme(InputMethodInfo property) {
        return property.getId().equals(SYSTEM_PRESERVED_IME);
    }

    //
//    public static boolean isAuxiliaryIme(InputMethodInfo imi) {
//        return imi.isAuxiliaryIme();
//    }
}
