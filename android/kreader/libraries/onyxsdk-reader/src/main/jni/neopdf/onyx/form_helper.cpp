#include "form_helper.h"

#include <iostream>

#include <core/fpdfapi/fpdf_page/include/cpdf_page.h>
#include <core/fpdfdoc/include/cpdf_interform.h>
#include <core/fpdfdoc/include/cpdf_formfield.h>
#include <core/fpdfdoc/include/cpdf_formcontrol.h>
#include <fpdfsdk/include/fsdk_define.h>

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
    FPDF_PageToDeviceEx(page, 0, 0, pw, ph, rotation, left, top, newLeft, newTop);
    FPDF_PageToDeviceEx(page, 0, 0, pw, ph, rotation, right, bottom, newRight, newBottom);
    if (*newRight < *newLeft) {
        std::swap(*newRight, *newLeft);
    }
    if (*newBottom < *newTop) {
        std::swap(*newBottom, *newTop);
    }
}

bool getFieldControlRegion(FPDF_PAGE page, const CPDF_FormControl *control, CFX_FloatRect *region) {
    double pageWidth = FPDF_GetPageWidth(page);
    double pageHeight = FPDF_GetPageHeight(page);

    CFX_FloatRect rect = control->GetRect();
    double newLeft, newRight, newBottom, newTop;
    pageToDevice(page, pageWidth, pageHeight, 0,
                 static_cast<double>(rect.left), static_cast<double>(rect.top),
                 static_cast<double>(rect.right), static_cast<double>(rect.bottom),
                 &newLeft, &newTop, &newRight, &newBottom);

    region->left = static_cast<float>(newLeft);
    region->top = static_cast<float>(newTop);
    region->right = static_cast<float>(newRight);
    region->bottom = static_cast<float>(newBottom);
    return true;
}

bool getFieldRegion(FPDF_PAGE page, const CPDF_FormField *field, CFX_FloatRect *region) {
    int count = field->CountControls();
    if (count <= 0 || !field->GetControl(0)) {
        return false;
    }

    return getFieldControlRegion(page, field->GetControl(0), region);
}

jstring getFieldName(JNIEnv *env, const CPDF_FormField *field) {
    CFX_WideString name = field->GetFullName();
    return env->NewStringUTF(name.UTF8Encode().c_str());
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

jobject createTextObject(JNIEnv *env, CPDF_FormField *field, FPDF_PAGE page) {
    CFX_FloatRect rect;
    if (!getFieldRegion(page, field, &rect)) {
        return nullptr;
    }

    jstring name = getFieldName(env, field);
    if (!name) {
        return nullptr;
    }

    CPDF_AAction action = field->GetAdditionalAction();
    if (action.ActionExist(CPDF_AAction::GetFocus)) {
        CPDF_Action actionGetFocus = action.GetAction(CPDF_AAction::GetFocus);
        if (actionGetFocus.GetType() == CPDF_Action::Named &&
                actionGetFocus.GetNamedAction().Compare("Annots:Tool:InkMenuItem") == 0) {
            static const char *readerFormTextClassName = "com/onyx/android/sdk/reader/api/ReaderFormScribble";
            static const char *methodName = "create";
            static const char *methodSignature = "(Ljava/lang/String;FFFF)Lcom/onyx/android/sdk/reader/api/ReaderFormScribble;";
            return createObject(env, readerFormTextClassName, methodName, methodSignature,
                                name, rect.left, rect.top, rect.right, rect.bottom);
        }
    }

    static const char *readerFormTextClassName = "com/onyx/android/sdk/reader/api/ReaderFormText";
    static const char *methodName = "create";
    static const char *methodSignature = "(Ljava/lang/String;FFFFLjava/lang/String;)Lcom/onyx/android/sdk/reader/api/ReaderFormText;";
    return createObject(env, readerFormTextClassName, methodName, methodSignature,
                        name, rect.left, rect.top, rect.right, rect.bottom, nullptr);
}

jobject createCheckBoxObject(JNIEnv *env, CPDF_FormField *field, FPDF_PAGE page) {
    CFX_FloatRect rect;
    if (!getFieldRegion(page, field, &rect)) {
        return nullptr;
    }

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

jobject createRadioButtonObject(JNIEnv *env, CPDF_FormControl *control, FPDF_PAGE page) {
    CFX_FloatRect rect;
    if (!getFieldControlRegion(page, control, &rect)) {
        return nullptr;
    }

    static const char *readerFormTextClassName = "com/onyx/android/sdk/reader/api/ReaderFormRadioButton";
    static const char *methodName = "create";
    static const char *methodSignature = "(FFFFZ)Lcom/onyx/android/sdk/reader/api/ReaderFormRadioButton;";
    return createObject(env, readerFormTextClassName, methodName, methodSignature,
                        rect.left, rect.top, rect.right, rect.bottom, false);
}

jobject createRadioGroupObject(JNIEnv *env, CPDF_FormField *field, FPDF_PAGE page) {
    int count = field->CountControls();
    if (count <= 0) {
        return nullptr;
    }

    jstring name = getFieldName(env, field);
    if (!name) {
        return nullptr;
    }

    std::vector<jobject> buttons;
    for (int i = 0; i < count; i++) {
        CPDF_FormControl *control = field->GetControl(i);
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
}

jobject createFieldObject(JNIEnv *env, CPDF_FormField *field, FPDF_PAGE page) {
    CPDF_FormField::Type type = field->GetType();
    switch (type) {
    case CPDF_FormField::Text:
        return createTextObject(env, field, page);
    case CPDF_FormField::CheckBox:
        return createCheckBoxObject(env, field, page);
    case CPDF_FormField::RadioButton:
        return createRadioGroupObject(env, field, page);
    default:
        return nullptr;
    }
}

}

FormHelper::FormHelper()
{

}

bool FormHelper::loadFormFields(JNIEnv *env, FPDF_PAGE page, jobject fieldList)
{
    static const char *readerFormFieldClassName = "com/onyx/android/sdk/reader/api/ReaderFormField";

    CPDF_Page* pPage = CPDFPageFromFPDFPage(page);
    CPDF_InterForm interform(pPage->m_pDocument);

    std::set<CPDF_FormField *> fields = interform.GetFieldsByPage(pPage);
    if (fields.size() <= 0) {
        return false;
    }

    jclass clzField = env->FindClass(readerFormFieldClassName);
    if (!clzField) {
         LOGE("Could not find class: %s", readerFormFieldClassName);
         return false;
    }
    jmethodID methodAddList = env->GetStaticMethodID(clzField, "addToFieldList", "(Ljava/util/List;Lcom/onyx/android/sdk/reader/api/ReaderFormField;)V");
    if (!methodAddList) {
        LOGE("find ReaderFormField.addToFieldList() failed!");
        return false;
    }

    for (auto field : fields) {
        jobject obj = createFieldObject(env, field, page);
        if (!obj) {
            continue;
        }

        env->CallStaticVoidMethod(clzField, methodAddList, fieldList, obj);
        env->DeleteLocalRef(obj);
    }

    return true;
}
