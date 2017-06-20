LOCAL_PATH:= $(call my-dir)

MY_LOCAL_PATH := $(LOCAL_PATH)
FREETYPE_SRC_ROOT := $(MY_LOCAL_PATH)/../../libpdfium/third_party/freetype
OPENSSL_ROOT := $(MY_LOCAL_PATH)/../../../prebuilt/openssl

include $(CLEAR_VARS)

LOCAL_MODULE := podofo

LOCAL_NDK_STL_VARIANT := gnustl_static

# TODO: figure out why turning on exceptions requires manually linking libdl
#LOCAL_SHARED_LIBRARIES := libdl

LOCAL_LDLIBS := -llog -lz

LOCAL_STATIC_LIBRARIES += libfx_freetype libssl-prebuilt libcrypto-prebuilt

LOCAL_CXXFLAGS += --rtti
LOCAL_CXXFLAGS += -DBUILDING_PODOFO 

LOCAL_SRC_FILES := \
  src/base/PdfArray.cpp \
  src/base/PdfCanvas.cpp \
  src/base/PdfColor.cpp \
  src/base/PdfContentsTokenizer.cpp \
  src/base/PdfData.cpp \
  src/base/PdfDataType.cpp \
  src/base/PdfDate.cpp \
  src/base/PdfDictionary.cpp \
  src/base/PdfEncoding.cpp \
  src/base/PdfEncodingFactory.cpp \
  src/base/PdfEncrypt.cpp \
  src/base/PdfError.cpp \
  src/base/PdfFileStream.cpp \
  src/base/PdfFilter.cpp \
  src/base/PdfFiltersPrivate.cpp \
  src/base/PdfImmediateWriter.cpp \
  src/base/PdfInputDevice.cpp \
  src/base/PdfInputStream.cpp \
  src/base/PdfLocale.cpp \
  src/base/PdfMemStream.cpp \
  src/base/PdfMemoryManagement.cpp \
  src/base/PdfName.cpp \
  src/base/PdfObject.cpp \
  src/base/PdfObjectStreamParserObject.cpp \
  src/base/PdfOutputDevice.cpp \
  src/base/PdfOutputStream.cpp \
  src/base/PdfParser.cpp \
  src/base/PdfParserObject.cpp \
  src/base/PdfRect.cpp \
  src/base/PdfRefCountedBuffer.cpp \
  src/base/PdfRefCountedInputDevice.cpp \
  src/base/PdfReference.cpp \
  src/base/PdfSigIncWriter.cpp \
  src/base/PdfStream.cpp \
  src/base/PdfString.cpp \
  src/base/PdfTokenizer.cpp \
  src/base/PdfVariant.cpp \
  src/base/PdfVecObjects.cpp \
  src/base/PdfWriter.cpp \
  src/base/PdfXRef.cpp \
  src/base/PdfXRefStream.cpp \
  src/base/PdfXRefStreamParserObject.cpp \
  src/doc/PdfAcroForm.cpp \
  src/doc/PdfAction.cpp \
  src/doc/PdfAnnotation.cpp \
  src/doc/PdfCMapEncoding.cpp \
  src/doc/PdfContents.cpp \
  src/doc/PdfDestination.cpp \
  src/doc/PdfDifferenceEncoding.cpp \
  src/doc/PdfDocument.cpp \
  src/doc/PdfElement.cpp \
  src/doc/PdfEncodingObjectFactory.cpp \
  src/doc/PdfExtGState.cpp \
  src/doc/PdfField.cpp \
  src/doc/PdfFileSpec.cpp \
  src/doc/PdfFont.cpp \
  src/doc/PdfFontCID.cpp \
  src/doc/PdfFontCache.cpp \
  src/doc/PdfFontConfigWrapper.cpp \
  src/doc/PdfFontFactory.cpp \
  src/doc/PdfFontMetrics.cpp \
  src/doc/PdfFontMetricsBase14.cpp \
  src/doc/PdfFontMetricsFreetype.cpp \
  src/doc/PdfFontMetricsObject.cpp \
  src/doc/PdfFontSimple.cpp \
  src/doc/PdfFontTTFSubset.cpp \
  src/doc/PdfFontTrueType.cpp \
  src/doc/PdfFontType1.cpp \
  src/doc/PdfFontType3.cpp \
  src/doc/PdfFontType1Base14.cpp \
  src/doc/PdfFunction.cpp \
  src/doc/PdfHintStream.cpp \
  src/doc/PdfIdentityEncoding.cpp \
  src/doc/PdfImage.cpp \
  src/doc/PdfInfo.cpp \
  src/doc/PdfMemDocument.cpp \
  src/doc/PdfNamesTree.cpp \
  src/doc/PdfOutlines.cpp \
  src/doc/PdfPage.cpp \
  src/doc/PdfPagesTree.cpp \
  src/doc/PdfPagesTreeCache.cpp \
  src/doc/PdfPainter.cpp \
  src/doc/PdfPainterMM.cpp \
  src/doc/PdfShadingPattern.cpp \
  src/doc/PdfSigIncMemDocument.cpp \
  src/doc/PdfSigIncPainter.cpp \
  src/doc/PdfSigIncSignatureField.cpp \
  src/doc/PdfSignOutputDevice.cpp \
  src/doc/PdfSignatureField.cpp \
  src/doc/PdfStreamedDocument.cpp \
  src/doc/PdfTable.cpp \
  src/doc/PdfTilingPattern.cpp \
  src/doc/PdfXObject.cpp

LOCAL_C_INCLUDES := \
  $(FREETYPE_SRC_ROOT)/include \
  $(MY_LOCAL_PATH)/src/.. \
  $(MY_LOCAL_PATH)/src
  
include $(BUILD_STATIC_LIBRARY)
