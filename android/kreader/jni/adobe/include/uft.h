/*
 *
 *                       ADOBE CONFIDENTIAL
 *                     _ _ _ _ _ _ _ _ _ _ _ _
 *
 * Copyright 2004, Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of
 * Adobe Systems Incorporated and its suppliers, if any.  The intellectual and
 * technical concepts contained herein are proprietary to Adobe Systems
 * Incorporated and its suppliers and may be covered by U.S. and Foreign
 * Patents, patents in process, and are protected by trade secret or copyright
 * law.  Dissemination of this information or reproduction of this material is
 * strictly forbidden unless prior written permission is obtained from Adobe
 * Systems Incorporated.
 *
 * Author: Peter Sorotokin
 * 
 */
#ifndef _UFT_H
#define _UFT_H

#ifdef _MSC_VER
  #ifdef USE_DECLSPEC
    #ifdef BUILDING_DLL
      #define UFTEXPORT __declspec(dllexport)
    #else
      #define UFTEXPORT __declspec(dllimport)
    #endif
  #else
    #define UFTEXPORT
  #endif
  #define UFTLOCAL
#else
  #ifdef HAVE_GCCVISIBILITYPATCH
    #define UFTEXPORT __attribute__ ((visibility("default")))
    #define UFTLOCAL __attribute__ ((visibility("hidden")))
  #else
    #define UFTEXPORT
    #define UFTLOCAL
  #endif
#endif

//#include "uft_alloc.h"
#include "uft_assert.h"
#include "uft_value.h"
#include "uft_string.h"
#include "uft_container.h"
#include "uft_url.h"
#include "uft_qname.h"
#include "uft_ref.h"
#include "uft_parser.h"
#include "uft_buffer.h"
#include "uft_opaque.h"
#include "uft_image.h"
#include "uft_date.h"
#include "uft_cache.h"
#include "uft_trace.h"
#include "uft_math.h"

#endif //_UFT_H

