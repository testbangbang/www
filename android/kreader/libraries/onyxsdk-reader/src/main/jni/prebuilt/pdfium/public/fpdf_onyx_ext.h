#ifndef FPDF_ONYX_EXT_H
#define FPDF_ONYX_EXT_H

#ifdef __cplusplus
extern "C" {
#endif

#include <stddef.h>

#include "fpdfview.h"

typedef struct _IPDF_ONYX_DrmCallback {

    FPDF_BOOL (*isEncrypted)();

    unsigned char* (*decrypt)(const unsigned char* data, const size_t dataLen, size_t *resultLen);

} IPDF_ONYX_DRMCALLBACK;

typedef void* FPDF_ONYX_FORMFIELD;
typedef void* FPDF_ONYX_FORMCONTROL;

FPDF_EXPORT FPDF_BOOL FPDF_CALLCONV FPDF_ONYX_IsUsingHighQualityImageRenderer();

FPDF_EXPORT void FPDF_CALLCONV FPDF_ONYX_SetUsingHighQualityImageRenderer(FPDF_BOOL use);

FPDF_EXPORT FPDF_BOOL FPDF_CALLCONV FPDF_ONYX_IsTextPage(FPDF_PAGE page);

FPDF_EXPORT void FPDF_CALLCONV FPDF_ONYX_SetTextGamma(FS_FLOAT gamma);

FPDF_EXPORT IPDF_ONYX_DRMCALLBACK * FPDF_CALLCONV FPDF_ONYX_DrmGetCallback();

FPDF_EXPORT void FPDF_CALLCONV FPDF_ONYX_DrmSetupCallback(IPDF_ONYX_DRMCALLBACK *callback);

///
/// \brief FPDF_ONYX_OpenFormfillPage
/// \param page
///
/// we need first open the page's formfill before we work on form fields
FPDF_EXPORT void FPDF_CALLCONV FPDF_ONYX_OpenFormfillPage(FPDF_PAGE page);

///
/// \brief FPDF_ONYX_CloseFormfillPage
/// \param page
///
/// close page's formfill when we finished
FPDF_EXPORT void FPDF_CALLCONV FPDF_ONYX_CloseFormfillPage(FPDF_PAGE page);

///
/// \brief GetFieldsByPage
/// \param page
/// \return nullptr terminated array
///
FPDF_EXPORT FPDF_ONYX_FORMFIELD* FPDF_CALLCONV FPDF_ONYX_GetFieldsByPage(FPDF_PAGE page);

///
/// \brief FPDF_ONYX_GetFieldType
/// \param field
/// \return
///
/// for debug purpose
FPDF_EXPORT int FPDF_CALLCONV FPDF_ONYX_GetFieldType(FPDF_ONYX_FORMFIELD field);

FPDF_EXPORT FPDF_BOOL FPDF_CALLCONV FPDF_ONYX_IsTextField(FPDF_ONYX_FORMFIELD field);

FPDF_EXPORT FPDF_BOOL FPDF_CALLCONV FPDF_ONYX_IsTextFieldForScribble(FPDF_ONYX_FORMFIELD field);

FPDF_EXPORT FPDF_BOOL FPDF_CALLCONV FPDF_ONYX_IsCheckBoxField(FPDF_ONYX_FORMFIELD field);

FPDF_EXPORT FPDF_BOOL FPDF_CALLCONV FPDF_ONYX_IsRadioButtonField(FPDF_ONYX_FORMFIELD field);

FPDF_EXPORT FPDF_BOOL FPDF_CALLCONV FPDF_ONYX_IsPushButtonField(FPDF_ONYX_FORMFIELD field);

///
/// \brief FPDF_ONYX_getFieldName
/// \param field
/// \return UTF-8 encoded
///
FPDF_EXPORT FPDF_BYTESTRING FPDF_CALLCONV FPDF_ONYX_GetFieldName(FPDF_ONYX_FORMFIELD field);

///
/// \brief FPDF_ONYX_getPushButtonCaption
/// \param pushButton
/// \return UTF-8 encoded
///
FPDF_EXPORT FPDF_BYTESTRING FPDF_CALLCONV FPDF_ONYX_GetPushButtonCaption(FPDF_ONYX_FORMFIELD pushButton);

FPDF_EXPORT FPDF_DWORD FPDF_CALLCONV FPDF_ONYX_CountFormControls(FPDF_ONYX_FORMFIELD field);

FPDF_EXPORT FPDF_ONYX_FORMCONTROL FPDF_CALLCONV FPDF_ONYX_GetFormControl(FPDF_ONYX_FORMFIELD field, FPDF_DWORD index);

FPDF_EXPORT FS_RECTF FPDF_CALLCONV FPDF_ONYX_GetFormFieldRegion(FPDF_PAGE page, FPDF_ONYX_FORMFIELD field);

FPDF_EXPORT FS_RECTF FPDF_CALLCONV FPDF_ONYX_GetFormControlRegion(FPDF_PAGE page, FPDF_ONYX_FORMCONTROL control);

#ifdef __cplusplus
}
#endif

#endif // FPDF_ONYX_EXT_H
