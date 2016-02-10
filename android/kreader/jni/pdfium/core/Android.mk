LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE:= libpdfiumcore

LOCAL_ARM_MODE := arm
LOCAL_NDK_STL_VARIANT := gnustl_static

LOCAL_CXXFLAGS += -O3 -fstrict-aliasing -fprefetch-loop-arrays -fexceptions
LOCAL_CXXFLAGS += -Wno-non-virtual-dtor -Wall

# Mask some warnings. These are benign, but we probably want to fix them
# upstream at some point.
LOCAL_CXXFLAGS += -Wno-unused-parameter \
                -Wno-unused-function \
                -Wno-sign-compare \
                -Wno-missing-braces \
                -Wno-missing-field-initializers \
                -Wno-delete-non-virtual-dtor    \
                -Wno-maybe-uninitialized        \
                -Wno-strict-aliasing            \
                -Wno-unused-but-set-variable
LOCAL_CXXCFLAGS += -Wno-overloaded-virtual
LOCAL_CXXFLAGS += -DAPI5 -D_GB1_CMAPS_ -D_GB1_CMAPS_4_ -D_CNS1_CMAPS_ -D_JPX_DECODER_


# Work around gcc text relocation bug. Fixed in gcc 4.9.
# TODO: remove this line after we've upgraded to gcc 4.9.
LOCAL_CFLAGS_arm64 += -O2

LOCAL_CFLAGS_arm64 += -D_FX_CPU_=_FX_X64_ -fPIC

LOCAL_SHARED_LIBRARIES := libft2

LOCAL_SRC_FILES := \
    src/fdrm/crypto/fx_crypt.cpp \
    src/fdrm/crypto/fx_crypt_aes.cpp \
    src/fdrm/crypto/fx_crypt_sha.cpp \
    src/fpdfdoc/doc_action.cpp \
    src/fpdfdoc/doc_annot.cpp \
    src/fpdfdoc/doc_ap.cpp \
    src/fpdfdoc/doc_basic.cpp \
    src/fpdfdoc/doc_bookmark.cpp \
    src/fpdfdoc/doc_form.cpp \
    src/fpdfdoc/doc_formcontrol.cpp \
    src/fpdfdoc/doc_formfield.cpp \
    src/fpdfdoc/doc_link.cpp \
    src/fpdfdoc/doc_metadata.cpp \
    src/fpdfdoc/doc_ocg.cpp \
    src/fpdfdoc/doc_tagged.cpp \
    src/fpdfdoc/doc_utils.cpp \
    src/fpdfdoc/doc_viewerPreferences.cpp \
    src/fpdfdoc/doc_vt.cpp \
    src/fpdfdoc/doc_vtmodule.cpp \
    src/fpdfapi/fpdf_basic_module.cpp \
    src/fpdfapi/fpdf_cmaps/CNS1/Adobe-CNS1-UCS2_5.cpp \
    src/fpdfapi/fpdf_cmaps/CNS1/B5pc-H_0.cpp \
    src/fpdfapi/fpdf_cmaps/CNS1/B5pc-V_0.cpp \
    src/fpdfapi/fpdf_cmaps/CNS1/cmaps_cns1.cpp \
    src/fpdfapi/fpdf_cmaps/CNS1/CNS-EUC-H_0.cpp \
    src/fpdfapi/fpdf_cmaps/CNS1/CNS-EUC-V_0.cpp \
    src/fpdfapi/fpdf_cmaps/CNS1/ETen-B5-H_0.cpp \
    src/fpdfapi/fpdf_cmaps/CNS1/ETen-B5-V_0.cpp \
    src/fpdfapi/fpdf_cmaps/CNS1/ETenms-B5-H_0.cpp \
    src/fpdfapi/fpdf_cmaps/CNS1/ETenms-B5-V_0.cpp \
    src/fpdfapi/fpdf_cmaps/CNS1/HKscs-B5-H_5.cpp \
    src/fpdfapi/fpdf_cmaps/CNS1/HKscs-B5-V_5.cpp \
    src/fpdfapi/fpdf_cmaps/CNS1/UniCNS-UCS2-H_3.cpp \
    src/fpdfapi/fpdf_cmaps/CNS1/UniCNS-UCS2-V_3.cpp \
    src/fpdfapi/fpdf_cmaps/CNS1/UniCNS-UTF16-H_0.cpp \
    src/fpdfapi/fpdf_cmaps/fpdf_cmaps.cpp \
    src/fpdfapi/fpdf_cmaps/GB1/Adobe-GB1-UCS2_5.cpp \
    src/fpdfapi/fpdf_cmaps/GB1/cmaps_gb1.cpp \
    src/fpdfapi/fpdf_cmaps/GB1/GB-EUC-H_0.cpp \
    src/fpdfapi/fpdf_cmaps/GB1/GB-EUC-V_0.cpp \
    src/fpdfapi/fpdf_cmaps/GB1/GBK-EUC-H_2.cpp \
    src/fpdfapi/fpdf_cmaps/GB1/GBK-EUC-V_2.cpp \
    src/fpdfapi/fpdf_cmaps/GB1/GBK2K-H_5.cpp \
    src/fpdfapi/fpdf_cmaps/GB1/GBK2K-V_5.cpp \
    src/fpdfapi/fpdf_cmaps/GB1/GBKp-EUC-H_2.cpp \
    src/fpdfapi/fpdf_cmaps/GB1/GBKp-EUC-V_2.cpp \
    src/fpdfapi/fpdf_cmaps/GB1/GBpc-EUC-H_0.cpp \
    src/fpdfapi/fpdf_cmaps/GB1/GBpc-EUC-V_0.cpp \
    src/fpdfapi/fpdf_cmaps/GB1/UniGB-UCS2-H_4.cpp \
    src/fpdfapi/fpdf_cmaps/GB1/UniGB-UCS2-V_4.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/83pv-RKSJ-H_1.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/90ms-RKSJ-H_2.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/90ms-RKSJ-V_2.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/90msp-RKSJ-H_2.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/90msp-RKSJ-V_2.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/90pv-RKSJ-H_1.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/Add-RKSJ-H_1.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/Add-RKSJ-V_1.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/Adobe-Japan1-UCS2_4.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/cmaps_japan1.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/EUC-H_1.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/EUC-V_1.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/Ext-RKSJ-H_2.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/Ext-RKSJ-V_2.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/H_1.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/UniJIS-UCS2-HW-H_4.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/UniJIS-UCS2-HW-V_4.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/UniJIS-UCS2-H_4.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/UniJIS-UCS2-V_4.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/UniJIS-UTF16-H_5.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/UniJIS-UTF16-V_5.cpp \
    src/fpdfapi/fpdf_cmaps/Japan1/V_1.cpp \
    src/fpdfapi/fpdf_cmaps/Korea1/Adobe-Korea1-UCS2_2.cpp \
    src/fpdfapi/fpdf_cmaps/Korea1/cmaps_korea1.cpp \
    src/fpdfapi/fpdf_cmaps/Korea1/KSC-EUC-H_0.cpp \
    src/fpdfapi/fpdf_cmaps/Korea1/KSC-EUC-V_0.cpp \
    src/fpdfapi/fpdf_cmaps/Korea1/KSCms-UHC-HW-H_1.cpp \
    src/fpdfapi/fpdf_cmaps/Korea1/KSCms-UHC-HW-V_1.cpp \
    src/fpdfapi/fpdf_cmaps/Korea1/KSCms-UHC-H_1.cpp \
    src/fpdfapi/fpdf_cmaps/Korea1/KSCms-UHC-V_1.cpp \
    src/fpdfapi/fpdf_cmaps/Korea1/KSCpc-EUC-H_0.cpp \
    src/fpdfapi/fpdf_cmaps/Korea1/UniKS-UCS2-H_1.cpp \
    src/fpdfapi/fpdf_cmaps/Korea1/UniKS-UCS2-V_1.cpp \
    src/fpdfapi/fpdf_cmaps/Korea1/UniKS-UTF16-H_0.cpp \
    src/fpdfapi/fpdf_edit/fpdf_edit_content.cpp \
    src/fpdfapi/fpdf_edit/fpdf_edit_create.cpp \
    src/fpdfapi/fpdf_edit/fpdf_edit_doc.cpp \
    src/fpdfapi/fpdf_edit/fpdf_edit_image.cpp \
    src/fpdfapi/fpdf_font/fpdf_font.cpp \
    src/fpdfapi/fpdf_font/fpdf_font_charset.cpp \
    src/fpdfapi/fpdf_font/fpdf_font_cid.cpp \
    src/fpdfapi/fpdf_font/ttgsubtable.cpp \
    src/fpdfapi/fpdf_page/fpdf_page.cpp \
    src/fpdfapi/fpdf_page/fpdf_page_colors.cpp \
    src/fpdfapi/fpdf_page/fpdf_page_doc.cpp \
    src/fpdfapi/fpdf_page/fpdf_page_func.cpp \
    src/fpdfapi/fpdf_page/fpdf_page_graph_state.cpp \
    src/fpdfapi/fpdf_page/fpdf_page_image.cpp \
    src/fpdfapi/fpdf_page/fpdf_page_parser.cpp \
    src/fpdfapi/fpdf_page/fpdf_page_parser_old.cpp \
    src/fpdfapi/fpdf_page/fpdf_page_path.cpp \
    src/fpdfapi/fpdf_page/fpdf_page_pattern.cpp \
    src/fpdfapi/fpdf_parser/fpdf_parser_decode.cpp \
    src/fpdfapi/fpdf_parser/fpdf_parser_document.cpp \
    src/fpdfapi/fpdf_parser/fpdf_parser_encrypt.cpp \
    src/fpdfapi/fpdf_parser/fpdf_parser_fdf.cpp \
    src/fpdfapi/fpdf_parser/fpdf_parser_filters.cpp \
    src/fpdfapi/fpdf_parser/fpdf_parser_objects.cpp \
    src/fpdfapi/fpdf_parser/fpdf_parser_parser.cpp \
    src/fpdfapi/fpdf_parser/fpdf_parser_utility.cpp \
    src/fpdfapi/fpdf_render/fpdf_render.cpp \
    src/fpdfapi/fpdf_render/fpdf_render_cache.cpp \
    src/fpdfapi/fpdf_render/fpdf_render_image.cpp \
    src/fpdfapi/fpdf_render/fpdf_render_loadimage.cpp \
    src/fpdfapi/fpdf_render/fpdf_render_pattern.cpp \
    src/fpdfapi/fpdf_render/fpdf_render_text.cpp \
    src/fpdftext/fpdf_text.cpp \
    src/fpdftext/fpdf_text_int.cpp \
    src/fpdftext/fpdf_text_search.cpp \
    src/fpdftext/unicodenormalization.cpp \
    src/fpdftext/unicodenormalizationdata.cpp \
    src/fxcodec/codec/fx_codec.cpp \
    src/fxcodec/codec/fx_codec_fax.cpp \
    src/fxcodec/codec/fx_codec_flate.cpp \
    src/fxcodec/codec/fx_codec_icc.cpp \
    src/fxcodec/codec/fx_codec_jbig.cpp \
    src/fxcodec/codec/fx_codec_jbig_enc.cpp \
    src/fxcodec/codec/fx_codec_jpeg.cpp \
    src/fxcodec/codec/fx_codec_jpx_opj.cpp \
    src/fxcodec/fx_libopenjpeg/src/fx_bio.c \
    src/fxcodec/fx_libopenjpeg/src/fx_cio.c \
    src/fxcodec/fx_libopenjpeg/src/fx_dwt.c \
    src/fxcodec/fx_libopenjpeg/src/fx_event.c \
    src/fxcodec/fx_libopenjpeg/src/fx_function_list.c \
    src/fxcodec/fx_libopenjpeg/src/fx_image.c \
    src/fxcodec/fx_libopenjpeg/src/fx_invert.c \
    src/fxcodec/fx_libopenjpeg/src/fx_j2k.c \
    src/fxcodec/fx_libopenjpeg/src/fx_j2k_lib.c \
    src/fxcodec/fx_libopenjpeg/src/fx_jpt.c \
    src/fxcodec/fx_libopenjpeg/src/fx_mct.c \
    src/fxcodec/fx_libopenjpeg/src/fx_mqc.c \
    src/fxcodec/fx_libopenjpeg/src/fx_openjpeg.c \
    src/fxcodec/fx_libopenjpeg/src/fx_openjpeg_jp2.c \
    src/fxcodec/fx_libopenjpeg/src/fx_opj_clock.c \
    src/fxcodec/fx_libopenjpeg/src/fx_pi.c \
    src/fxcodec/fx_libopenjpeg/src/fx_raw.c \
    src/fxcodec/fx_libopenjpeg/src/fx_t1.c \
    src/fxcodec/fx_libopenjpeg/src/fx_t1_generate_luts.c \
    src/fxcodec/fx_libopenjpeg/src/fx_t2.c \
    src/fxcodec/fx_libopenjpeg/src/fx_tcd.c \
    src/fxcodec/fx_libopenjpeg/src/fx_tgt.c \
    src/fxcodec/fx_zlib/src/fx_zlib_adler32.c \
    src/fxcodec/fx_zlib/src/fx_zlib_compress.c \
    src/fxcodec/fx_zlib/src/fx_zlib_crc32.c \
    src/fxcodec/fx_zlib/src/fx_zlib_deflate.c \
    src/fxcodec/fx_zlib/src/fx_zlib_gzclose.c \
    src/fxcodec/fx_zlib/src/fx_zlib_gzlib.c \
    src/fxcodec/fx_zlib/src/fx_zlib_gzread.c \
    src/fxcodec/fx_zlib/src/fx_zlib_gzwrite.c \
    src/fxcodec/fx_zlib/src/fx_zlib_infback.c \
    src/fxcodec/fx_zlib/src/fx_zlib_inffast.c \
    src/fxcodec/fx_zlib/src/fx_zlib_inflate.c \
    src/fxcodec/fx_zlib/src/fx_zlib_inftrees.c \
    src/fxcodec/fx_zlib/src/fx_zlib_trees.c \
    src/fxcodec/fx_zlib/src/fx_zlib_uncompr.c \
    src/fxcodec/fx_zlib/src/fx_zlib_zutil.c \
    src/fxcodec/jbig2/JBig2_ArithIntDecoder.cpp \
    src/fxcodec/jbig2/JBig2_Context.cpp \
    src/fxcodec/jbig2/JBig2_GeneralDecoder.cpp \
    src/fxcodec/jbig2/JBig2_HuffmanDecoder.cpp \
    src/fxcodec/jbig2/JBig2_HuffmanTable.cpp \
    src/fxcodec/jbig2/JBig2_Image.cpp \
    src/fxcodec/jbig2/JBig2_Object.cpp \
    src/fxcodec/jbig2/JBig2_PatternDict.cpp \
    src/fxcodec/jbig2/JBig2_Segment.cpp \
    src/fxcodec/jbig2/JBig2_SymbolDict.cpp \
    src/fxcodec/lcms2/src/fx_cmscam02.c \
    src/fxcodec/lcms2/src/fx_cmscgats.c \
    src/fxcodec/lcms2/src/fx_cmscnvrt.c \
    src/fxcodec/lcms2/src/fx_cmserr.c \
    src/fxcodec/lcms2/src/fx_cmsgamma.c \
    src/fxcodec/lcms2/src/fx_cmsgmt.c \
    src/fxcodec/lcms2/src/fx_cmshalf.c \
    src/fxcodec/lcms2/src/fx_cmsintrp.c \
    src/fxcodec/lcms2/src/fx_cmsio0.c \
    src/fxcodec/lcms2/src/fx_cmsio1.c \
    src/fxcodec/lcms2/src/fx_cmslut.c \
    src/fxcodec/lcms2/src/fx_cmsmd5.c \
    src/fxcodec/lcms2/src/fx_cmsmtrx.c \
    src/fxcodec/lcms2/src/fx_cmsnamed.c \
    src/fxcodec/lcms2/src/fx_cmsopt.c \
    src/fxcodec/lcms2/src/fx_cmspack.c \
    src/fxcodec/lcms2/src/fx_cmspcs.c \
    src/fxcodec/lcms2/src/fx_cmsplugin.c \
    src/fxcodec/lcms2/src/fx_cmsps2.c \
    src/fxcodec/lcms2/src/fx_cmssamp.c \
    src/fxcodec/lcms2/src/fx_cmssm.c \
    src/fxcodec/lcms2/src/fx_cmstypes.c \
    src/fxcodec/lcms2/src/fx_cmsvirt.c \
    src/fxcodec/lcms2/src/fx_cmswtpnt.c \
    src/fxcodec/lcms2/src/fx_cmsxform.c \
    src/fxcodec/libjpeg/fpdfapi_jcapimin.c \
    src/fxcodec/libjpeg/fpdfapi_jcapistd.c \
    src/fxcodec/libjpeg/fpdfapi_jccoefct.c \
    src/fxcodec/libjpeg/fpdfapi_jccolor.c \
    src/fxcodec/libjpeg/fpdfapi_jcdctmgr.c \
    src/fxcodec/libjpeg/fpdfapi_jchuff.c \
    src/fxcodec/libjpeg/fpdfapi_jcinit.c \
    src/fxcodec/libjpeg/fpdfapi_jcmainct.c \
    src/fxcodec/libjpeg/fpdfapi_jcmarker.c \
    src/fxcodec/libjpeg/fpdfapi_jcmaster.c \
    src/fxcodec/libjpeg/fpdfapi_jcomapi.c \
    src/fxcodec/libjpeg/fpdfapi_jcparam.c \
    src/fxcodec/libjpeg/fpdfapi_jcphuff.c \
    src/fxcodec/libjpeg/fpdfapi_jcprepct.c \
    src/fxcodec/libjpeg/fpdfapi_jcsample.c \
    src/fxcodec/libjpeg/fpdfapi_jctrans.c \
    src/fxcodec/libjpeg/fpdfapi_jdapimin.c \
    src/fxcodec/libjpeg/fpdfapi_jdapistd.c \
    src/fxcodec/libjpeg/fpdfapi_jdcoefct.c \
    src/fxcodec/libjpeg/fpdfapi_jdcolor.c \
    src/fxcodec/libjpeg/fpdfapi_jddctmgr.c \
    src/fxcodec/libjpeg/fpdfapi_jdhuff.c \
    src/fxcodec/libjpeg/fpdfapi_jdinput.c \
    src/fxcodec/libjpeg/fpdfapi_jdmainct.c \
    src/fxcodec/libjpeg/fpdfapi_jdmarker.c \
    src/fxcodec/libjpeg/fpdfapi_jdmaster.c \
    src/fxcodec/libjpeg/fpdfapi_jdmerge.c \
    src/fxcodec/libjpeg/fpdfapi_jdphuff.c \
    src/fxcodec/libjpeg/fpdfapi_jdpostct.c \
    src/fxcodec/libjpeg/fpdfapi_jdsample.c \
    src/fxcodec/libjpeg/fpdfapi_jdtrans.c \
    src/fxcodec/libjpeg/fpdfapi_jerror.c \
    src/fxcodec/libjpeg/fpdfapi_jfdctfst.c \
    src/fxcodec/libjpeg/fpdfapi_jfdctint.c \
    src/fxcodec/libjpeg/fpdfapi_jidctfst.c \
    src/fxcodec/libjpeg/fpdfapi_jidctint.c \
    src/fxcodec/libjpeg/fpdfapi_jidctred.c \
    src/fxcodec/libjpeg/fpdfapi_jmemmgr.c \
    src/fxcodec/libjpeg/fpdfapi_jmemnobs.c \
    src/fxcodec/libjpeg/fpdfapi_jutils.c \
    src/fxcrt/fx_arabic.cpp \
    src/fxcrt/fx_basic_array.cpp \
    src/fxcrt/fx_basic_bstring.cpp \
    src/fxcrt/fx_basic_buffer.cpp \
    src/fxcrt/fx_basic_coords.cpp \
    src/fxcrt/fx_basic_gcc.cpp \
    src/fxcrt/fx_basic_list.cpp \
    src/fxcrt/fx_basic_maps.cpp \
    src/fxcrt/fx_basic_memmgr.cpp \
    src/fxcrt/fx_basic_plex.cpp \
    src/fxcrt/fx_basic_utf.cpp \
    src/fxcrt/fx_basic_util.cpp \
    src/fxcrt/fx_basic_wstring.cpp \
    src/fxcrt/fx_extension.cpp \
    src/fxcrt/fx_ucddata.cpp \
    src/fxcrt/fx_unicode.cpp \
    src/fxcrt/fx_xml_composer.cpp \
    src/fxcrt/fx_xml_parser.cpp \
    src/fxcrt/fxcrt_platforms.cpp \
    src/fxcrt/fxcrt_posix.cpp \
    src/fxcrt/fxcrt_windows.cpp \
    src/fxge/agg/src/fxfx_agg_curves.cpp \
    src/fxge/agg/src/fxfx_agg_driver.cpp \
    src/fxge/agg/src/fxfx_agg_path_storage.cpp \
    src/fxge/agg/src/fxfx_agg_rasterizer_scanline_aa.cpp \
    src/fxge/agg/src/fxfx_agg_vcgen_dash.cpp \
    src/fxge/agg/src/fxfx_agg_vcgen_stroke.cpp \
    src/fxge/android/fpf_skiafont.cpp \
    src/fxge/android/fpf_skiafontmgr.cpp \
    src/fxge/android/fpf_skiamodule.cpp \
    src/fxge/android/fx_android_font.cpp \
    src/fxge/android/fx_android_imp.cpp \
    src/fxge/apple/fx_apple_platform.cpp \
    src/fxge/apple/fx_mac_imp.cpp \
    src/fxge/apple/fx_quartz_device.cpp \
    src/fxge/dib/fx_dib_composite.cpp \
    src/fxge/dib/fx_dib_convert.cpp \
    src/fxge/dib/fx_dib_engine.cpp \
    src/fxge/dib/fx_dib_main.cpp \
    src/fxge/dib/fx_dib_transform.cpp \
    src/fxge/fontdata/chromefontdata/FoxitDingbats.c \
    src/fxge/fontdata/chromefontdata/FoxitFixed.c \
    src/fxge/fontdata/chromefontdata/FoxitFixedBold.c \
    src/fxge/fontdata/chromefontdata/FoxitFixedBoldItalic.c \
    src/fxge/fontdata/chromefontdata/FoxitFixedItalic.c \
    src/fxge/fontdata/chromefontdata/FoxitSans.c \
    src/fxge/fontdata/chromefontdata/FoxitSansBold.c \
    src/fxge/fontdata/chromefontdata/FoxitSansBoldItalic.c \
    src/fxge/fontdata/chromefontdata/FoxitSansItalic.c \
    src/fxge/fontdata/chromefontdata/FoxitSansMM.c \
    src/fxge/fontdata/chromefontdata/FoxitSerif.c \
    src/fxge/fontdata/chromefontdata/FoxitSerifBold.c \
    src/fxge/fontdata/chromefontdata/FoxitSerifBoldItalic.c \
    src/fxge/fontdata/chromefontdata/FoxitSerifItalic.c \
    src/fxge/fontdata/chromefontdata/FoxitSerifMM.c \
    src/fxge/fontdata/chromefontdata/FoxitSymbol.c \
    src/fxge/freetype/fx_freetype.c \
    src/fxge/ge/fx_ge.cpp \
    src/fxge/ge/fx_ge_device.cpp \
    src/fxge/ge/fx_ge_font.cpp \
    src/fxge/ge/fx_ge_fontmap.cpp \
    src/fxge/ge/fx_ge_linux.cpp \
    src/fxge/ge/fx_ge_path.cpp \
    src/fxge/ge/fx_ge_ps.cpp \
    src/fxge/ge/fx_ge_text.cpp


MY_SRC_ROOT := $(LOCAL_PATH)/..
LOCAL_C_INCLUDES := \
    $(MY_SRC_ROOT)  \
    $(MY_SRC_ROOT)/third_party/freetype/include


include $(BUILD_STATIC_LIBRARY)
