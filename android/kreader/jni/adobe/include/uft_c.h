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
 * Author: Peter Sorotokin, 13-JUL-2004
 * 
 */

/* Selected C interfaces for UFT library */

#ifndef _UFT_C_H
#define _UFT_C_H

#include <stdio.h>
#include <stdlib.h>
#include "uft_atomid.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef signed char uft_int8;
typedef unsigned char uft_uint8;
typedef short uft_int16;
typedef unsigned short uft_uint16;
typedef unsigned short uft_char16;
typedef int uft_int32;
typedef unsigned int uft_uint32;
typedef size_t uft_word; // an int that can hold a pointer
typedef uft_uint32 uft_cacheid;

#if defined(_MSC_VER)
typedef __int64 uft_int64;
typedef unsigned __int64 uft_uint64;
#else
typedef long long int uft_int64;
typedef unsigned long long int uft_uint64;
#endif

struct uft_Value_dummy_struct;

/* corresponds to C++ uft::Value and subclasses */
typedef struct uft_Value_dummy_struct * uft_Value;

#define UFT_FLOAT_ZERO	((uft_Value)0)
#define UFT_NULL		((uft_Value)1)
#define UFT_INT_ZERO	((uft_Value)3)

/* reference counting, not necessary (but harmless) for ints and floats */
void uft_addRef( uft_Value value );
void uft_release( uft_Value value );

/* UFT8 strings */
uft_Value uft_fromUTF8( char * str );
int uft_isString( uft_Value value ); /* string value type */
int uft_isStringValue( uft_Value value ); /* string or other string buffer holder type */
uft_Value uft_toString( uft_Value value ); /* can be called on any Value */
uft_Value uft_toStringValue( uft_Value value );
char * uft_getStringValueBuffer( uft_Value stringValue );
size_t uft_getStringValueLength( uft_Value stringValue );
uft_Value uft_atom( uft_Value str ); /* atomizes if needed; atoms are strings */
uft_uint32 uft_atomID( uft_Value str ); /* returns zero for non-atomized strings */
uft_Value uft_atom_fromUFT8( char * str );
uft_uint32 uft_atomID_fromUFT8( char * str );
size_t uft_indexOf( uft_Value str, size_t index, char c );
uft_Value uft_substring( uft_Value str, size_t index1, size_t index2 ); 
uft_Value uft_atomById( uft_int32 atomID );

#ifdef UFT_ATOM_COMPILE_TIME
uft_Value uft_predefinedAtomById( uft_int32 atomID );
#endif

/* Int */
uft_Value uft_fromInt( uft_int32 i );
int uft_isInt( uft_Value value );
uft_int32 uft_getIntValue( uft_Value value );

/* Float */
uft_Value uft_fromFloat( float f );
int uft_isFloat( uft_Value value );
float uft_getFloatValue( uft_Value value );

/* Tuple */
int uft_isTuple( uft_Value value );
size_t uft_TupleLength( uft_Value value );
uft_Value uft_getNthValue( uft_Value value, size_t index );

/* Opaque */
uft_Value uft_fromOpaque( uft_word value );
int uft_isOpaque( uft_Value value ); /* may be represented as integer, so uft_isInt may return TRUE as well! */
uft_word uft_getOpaqueValue( uft_Value opaque );

/* UFT-managed structures */

/* typedefs for structure descriptor */
typedef struct uft_struct_descriptor_s uft_struct_descriptor_t;
typedef void (*uft_struct_destroy_func_t)( uft_struct_descriptor_t * self, void * ptr );
typedef void (*uft_struct_copy_func_t)( uft_struct_descriptor_t * self, void * dest, void * src );
typedef void (*uft_struct_placeholder_func_t)();

/* structure descriptor - must be in sync with StructDescriptor in uft_value.h */
/* do not initialize directly, use uft_struct_descriptor_init functions */
struct uft_struct_descriptor_s
{
	uft_word private_blockByteSize;
	char * private_name;
	uft_struct_placeholder_func_t private_destroy;
	uft_struct_placeholder_func_t private_copy;
	uft_struct_placeholder_func_t private_toString;
	uft_struct_placeholder_func_t private_query;
	uft_struct_placeholder_func_t private_getWeakRefPtr;
	uft_struct_placeholder_func_t private_flush;
	void* private_ribBinder;
};

/* uft structure descriptor initializer */
void uft_struct_descriptor_init( uft_struct_descriptor_t * desc, uft_word structSize, 
			char * name, uft_struct_destroy_func_t destroyFunc, uft_struct_copy_func_t copyFunc );

/* uft struct functions */
uft_Value uft_struct_alloc( uft_struct_descriptor_t * desc );
uft_struct_descriptor_t * uft_struct_getDescriptor( uft_Value value ); /* returns NULL for non-structs */
void * uft_struct_getPtr( uft_Value structPtr ); /* returns NULL for non-structs */
uft_Value uft_struct_fromPtr( void * structPtr ); /* structPtr must have been returned by uft_struct_getPtr */

/* access to UFT memory allocator, buffers that don't know / know their size */
void * uft_allocBlock( size_t size );
void * uft_allocBlock_loc( size_t size, const char * file, int line );
void uft_freeBlock( size_t size, void * ptr );
void * uft_allocBlockWithSize( size_t size );
void * uft_allocBlockWithSize_loc( size_t size, const char * file, int line );
size_t uft_getBlockSize( void * ptr );
void uft_freeMemory( void * ptr );

#ifdef __cplusplus
} /* extern "C" */
#endif

#ifdef __cplusplus
/* C to C++ conversion */

#include "uft.h"

namespace uft
{

FORCE_INLINE uft_Value convertToC( const uft::Value& val )
{
	uft::Value tmp = val; // increment ref count if needed
	uft_Value result = *(uft_Value*)&tmp; // copy out the value
	*(uft_Value*)&tmp = UFT_NULL; // steal the ownership
	return result;
}

FORCE_INLINE const uft::Value& convertFromC( const uft_Value& val )
{
	return *(const uft::Value*)&val;
}

inline const char * convertToCString( const uft::String& str )
{
	if( str.isNull() )
		return NULL;
	uft::Value tmp = str; // increment ref count if needed
	*(uft_Value*)&tmp = UFT_NULL; // steal the ownership
	return str.utf8();
}

inline void releaseCString( const char * str )
{
	if( str != NULL )
	{
		uft::Value val;
		*(const char **)&val = str - sizeof(uft::SizedBlock) - sizeof(uft::uint32) + 1;
		assertLogic( val.isString() );
	}
}

}

#endif

#endif /* _UFT_C_H */

