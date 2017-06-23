#ifndef FORM_HELPER_H
#define FORM_HELPER_H

#include <jni.h>

#include "fpdf_doc.h"

class FormHelper
{
public:
    FormHelper();

    static bool loadFormFields(JNIEnv *env, FPDF_PAGE page, jobject fieldList);
};

#endif // FORM_HELPER_H
