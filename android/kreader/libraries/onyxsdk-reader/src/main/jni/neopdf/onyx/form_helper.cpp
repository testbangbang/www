#include "form_helper.h"

#include <iostream>

#include <fpdf_onyx_ext.h>

#include "JNIUtils.h"

#include "log.h"

#define LOGD std::out

namespace {

// convert page's left-bottom origin to screen's left-top origin
// but there are some documents we can't get normalized coordinates simply by subtracting with page width/height,
// so it's safer to use pdfium's built-in FPDF_PageToDevice()
void pageToDevice(FPDF_PAGE page, double pageWidth, double pageHeight, int rotation,
                  double left, double top, double right, double bottom,
                  double *newLeft, double *newTop, double *newRight, double *newBottom) {
    int pw = static_cast<int>(pageWidth);
    int ph = static_cast<int>(pageHeight);

    int i, j;

    FPDF_PageToDevice(page, 0, 0, pw, ph, rotation, left, top, &i, &j);
    *newLeft = i;
    *newTop = j;

    FPDF_PageToDevice(page, 0, 0, pw, ph, rotation, right, bottom, &i, &j);
    *newRight = i;
    *newBottom = j;

    if (*newRight < *newLeft) {
        std::swap(*newRight, *newLeft);
    }
    if (*newBottom < *newTop) {
        std::swap(*newBottom, *newTop);
    }
}

jstring getFieldName(JNIEnv *env, FPDF_ONYX_FORMFIELD field) {
    // TODO memory leak?
    FPDF_BYTESTRING name = FPDF_ONYX_GetFieldName(field);
    return env->NewStringUTF(name);
}

jstring getPushButtonCaption(JNIEnv *env, const FPDF_ONYX_FORMFIELD pushButton) {
    FPDF_BYTESTRING caption = FPDF_ONYX_GetPushButtonCaption(pushButton);
    return env->NewStringUTF(caption);
}

jobject createObject(JNIEnv *env, const char *className, const char *methodName,
                     const char *methodSignature, ...) {
    jclass clazz = env->FindClass(className);
    if (!clazz) {
        LOGE("Could not find class: %s", className);
        return nullptr;
    }
    jmethodID methodCreate = env->GetStaticMethodID(clazz, methodName, methodSignature);
    if (!methodCreate) {
        LOGE("find method failed: %s", methodName);
        return nullptr;
    }

    va_list args;
    va_start(args, methodSignature);
    jobject obj = env->CallStaticObjectMethodV(clazz, methodCreate, args);
    va_end(args);
    if (!obj) {
        LOGE("calling method failed!");
    }
    return obj;
}

jobject createTextObject(JNIEnv *env, FPDF_ONYX_FORMFIELD field, FPDF_PAGE page) {
    FS_RECTF rect = FPDF_ONYX_GetFormFieldRegion(page, field);

    jstring name = getFieldName(env, field);
    if (!name) {
        return nullptr;
    }

    if (FPDF_ONYX_IsTextFieldForScribble(field)) {
        static const char *readerFormTextClassName = "com/onyx/android/sdk/reader/api/ReaderFormScribble";
        static const char *methodName = "create";
        static const char *methodSignature = "(Ljava/lang/String;FFFF)Lcom/onyx/android/sdk/reader/api/ReaderFormScribble;";
        return createObject(env, readerFormTextClassName, methodName, methodSignature,
                            name, rect.left, rect.top, rect.right, rect.bottom);
    }

    static const char *readerFormTextClassName = "com/onyx/android/sdk/reader/api/ReaderFormText";
    static const char *methodName = "create";
    static const char *methodSignature = "(Ljava/lang/String;FFFFLjava/lang/String;)Lcom/onyx/android/sdk/reader/api/ReaderFormField;";

    return createObject(env, readerFormTextClassName, methodName, methodSignature,
                        name, rect.left, rect.top, rect.right, rect.bottom, nullptr);
}

jobject createCheckBoxObject(JNIEnv *env, FPDF_ONYX_FORMFIELD field, FPDF_PAGE page) {
    FS_RECTF rect = FPDF_ONYX_GetFormFieldRegion(page, field);

    jstring name = getFieldName(env, field);
    if (!name) {
        return nullptr;
    }

    static const char *readerFormTextClassName = "com/onyx/android/sdk/reader/api/ReaderFormCheckbox";
    static const char *methodName = "create";
    static const char *methodSignature = "(Ljava/lang/String;FFFFZ)Lcom/onyx/android/sdk/reader/api/ReaderFormCheckbox;";
    return createObject(env, readerFormTextClassName, methodName, methodSignature,
                        name, rect.left, rect.top, rect.right, rect.bottom, false);
}

jobject createRadioButtonObject(JNIEnv *env, FPDF_ONYX_FORMCONTROL control, FPDF_PAGE page) {
    FS_RECTF rect = FPDF_ONYX_GetFormControlRegion(page, control);

    static const char *readerFormTextClassName = "com/onyx/android/sdk/reader/api/ReaderFormRadioButton";
    static const char *methodName = "create";
    static const char *methodSignature = "(FFFFZ)Lcom/onyx/android/sdk/reader/api/ReaderFormRadioButton;";
    return createObject(env, readerFormTextClassName, methodName, methodSignature,
                        rect.left, rect.top, rect.right, rect.bottom, false);
}

jobject createRadioGroupObject(JNIEnv *env, FPDF_ONYX_FORMFIELD field, FPDF_PAGE page) {
    int count = FPDF_ONYX_CountFormControls(field);
    if (count <= 0) {
        return nullptr;
    }

    jstring name = getFieldName(env, field);
    if (!name) {
        return nullptr;
    }

    std::vector<jobject> buttons;
    for (int i = 0; i < count; i++) {
        FPDF_ONYX_FORMCONTROL control = FPDF_ONYX_GetFormControl(field, i);
        if (!control) {
            continue;
        }
        jobject btn = createRadioButtonObject(env, control, page);
        if (btn) {
            buttons.push_back(btn);
        }
    }

    const char *radioButtonClassName = "com/onyx/android/sdk/reader/api/ReaderFormRadioButton";
    jclass clazzRadioButton = env->FindClass(radioButtonClassName);
    if (!clazzRadioButton) {
        LOGE("Could not find class: %s", radioButtonClassName);
        return nullptr;
    }

    jobjectArray array = env->NewObjectArray(buttons.size(), clazzRadioButton, nullptr);
    for (size_t i = 0; i < buttons.size(); i++) {
        env->SetObjectArrayElement(array, i, buttons.at(i));
        env->DeleteLocalRef(buttons.at(i));
    }

    static const char *readerFormTextClassName = "com/onyx/android/sdk/reader/api/ReaderFormRadioGroup";
    static const char *methodName = "create";
    static const char *methodSignature = "(Ljava/lang/String;[Lcom/onyx/android/sdk/reader/api/ReaderFormRadioButton;)Lcom/onyx/android/sdk/reader/api/ReaderFormRadioGroup;";
    return createObject(env, readerFormTextClassName, methodName, methodSignature,
                        name, array);
    return nullptr;
}

jobject createPushButtonObject(JNIEnv *env, FPDF_ONYX_FORMFIELD field, FPDF_PAGE page) {
    FS_RECTF rect = FPDF_ONYX_GetFormFieldRegion(page, field);

    jstring name = getFieldName(env, field);
    if (!name) {
        return nullptr;
    }

    jstring caption = getPushButtonCaption(env, field);

    static const char *readerFormTextClassName = "com/onyx/android/sdk/reader/api/ReaderFormPushButton";
    static const char *methodName = "create";
    static const char *methodSignature = "(Ljava/lang/String;Ljava/lang/String;FFFF)Lcom/onyx/android/sdk/reader/api/ReaderFormPushButton;";
    return createObject(env, readerFormTextClassName, methodName, methodSignature,
                        name, caption, rect.left, rect.top, rect.right, rect.bottom);
}

jobject createFieldObject(JNIEnv *env, FPDF_ONYX_FORMFIELD field, FPDF_PAGE page) {
    if (FPDF_ONYX_IsTextField(field)) {
        return createTextObject(env, field, page);
    } else if (FPDF_ONYX_IsCheckBoxField(field)) {
        return createCheckBoxObject(env, field, page);
    } else if (FPDF_ONYX_IsRadioButtonField(field)) {
        return createRadioGroupObject(env, field, page);
    } else if (FPDF_ONYX_IsPushButtonField(field)) {
        return createPushButtonObject(env, field, page);
    }

    LOGE("createFieldObject: unknown field type");
    return nullptr;
}

}

FormHelper::FormHelper()
{

}

bool FormHelper::loadFormFields(JNIEnv *env, FPDF_PAGE page, jobject fieldList)
{
    static const char *readerFormFieldClassName = "com/onyx/android/sdk/reader/api/ReaderFormField";

    FPDF_ONYX_OpenFormfillPage(page);

    FPDF_ONYX_FORMFIELD *fields = FPDF_ONYX_GetFieldsByPage(page);
    if (!fields) {
        LOGE("load form fields failed!");
        FPDF_ONYX_CloseFormfillPage(page);
        return false;
    }

    jclass clzField = env->FindClass(readerFormFieldClassName);
    if (!clzField) {
        LOGE("Could not find class: %s", readerFormFieldClassName);
        FPDF_ONYX_CloseFormfillPage(page);
        return false;
    }
    jmethodID methodAddList = env->GetStaticMethodID(clzField, "addToFieldList", "(Ljava/util/List;Lcom/onyx/android/sdk/reader/api/ReaderFormField;)V");
    if (!methodAddList) {
        LOGE("find ReaderFormField.addToFieldList() failed!");
        FPDF_ONYX_CloseFormfillPage(page);
        return false;
    }

    for (int i = 0; fields[i] != nullptr; i++) {
        FPDF_ONYX_FORMFIELD field = fields[i];
        jobject obj = createFieldObject(env, field, page);
        if (!obj) {
            continue;
        }

        env->CallStaticVoidMethod(clzField, methodAddList, fieldList, obj);
        env->DeleteLocalRef(obj);
    }

    FPDF_ONYX_CloseFormfillPage(page);
    return true;
}
