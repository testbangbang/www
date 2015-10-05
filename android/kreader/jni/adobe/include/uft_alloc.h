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
 * Author: Peter Sorotokin, 9-APR-2004
 */

#ifndef _UFT_ALLOC_H
#define _UFT_ALLOC_H

#include <stdio.h>
#include <stdlib.h>
#include <stdexcept>
#include <cstddef>

#ifdef STL_EXCLUDED
#include <limits.h>
#else
#include <limits>
#endif

#ifdef malloc
#ifdef UFT_UNDEF_MEMFUNC
#undef malloc
#else
#error "malloc defined as macro; either undefine malloc or define UFT_UNDEF_MEMFUNC"
#endif
#endif

#ifdef alloc
#ifdef UFT_UNDEF_MEMFUNC
#undef alloc
#else
#error "alloc defined as macro; either undefine alloc or define UFT_UNDEF_MEMFUNC"
#endif
#endif

#ifdef realloc
#ifdef UFT_UNDEF_MEMFUNC
#undef realloc
#else
#error "realloc defined as macro; either undefine realloc or define UFT_UNDEF_MEMFUNC"
#endif
#endif

#ifdef free
#ifdef UFT_UNDEF_MEMFUNC
#undef free
#else
#error "free defined as macro; either undefine free or define UFT_UNDEF_MEMFUNC"
#endif
#endif

#ifndef UFT_MEMORY_DEBUG
#if defined(_DEBUG)
#define UFT_MEMORY_DEBUG 1
#else
#define UFT_MEMORY_DEBUG 0
#endif
#endif

#if UFT_MEMORY_DEBUG
#if defined(_MSC_VER) && !defined(WINCE)
// on Windows desktop, use MS utilities
// defining _CRTDBG_MAP_ALLOC conflicts with Flash runtime's operator new
//#define _CRTDBG_MAP_ALLOC
#include "crtdbg.h"
#undef free
#undef malloc
#undef realloc
#define UFT_NEW_PARAM _NORMAL_BLOCK,
#define UFT_NEW new(UFT_NEW_PARAM __FILE__, __LINE__)
#else //defined(_MSC_VER) && !defined(WINCE)
// outside Windows desktop don't override new for now
#define UFT_NEW_PARAM
#define UFT_NEW new
#endif //defined(_MSC_VER) && !defined(WINCE)
#define UFT_NEW_ARRAY(obj, count) uft::ArrayHelper::arrayFactory<obj>( count, __FILE__, __LINE__ )
#define UFT_DELETE_ARRAY( arrayPtr ) uft::ArrayHelper::destroyArray( arrayPtr )
#define UFT_NEW_VALUE(val) new(val, __FILE__, __LINE__)
#define UFT_NEW_TOKEN() uft::Token::newToken(__FILE__, __LINE__)
#define UFT_ALLOC_BLOCK(sz) uft::allocBlock((sz),__FILE__, __LINE__)
#define UFT_ALLOC_BLOCK_I(sz) uft::Runtime::s_instance->allocBlock((sz),__FILE__, __LINE__)
#else
#define UFT_NEW new
#define UFT_NEW_ARRAY(obj, count) uft::ArrayHelper::arrayFactory<obj>( count )
#define UFT_DELETE_ARRAY( arrayPtr ) uft::ArrayHelper::destroyArray( arrayPtr )
#define UFT_NEW_VALUE(val) new(val)
#define UFT_NEW_TOKEN() uft::Token::newToken()
#define UFT_ALLOC_BLOCK(sz) uft::allocBlock(sz)
#define UFT_ALLOC_BLOCK_I(sz) uft::Runtime::s_instance->allocBlock(sz)
#endif

#define UFT_FREE_BLOCK(sz,ptr) uft::freeBlock(sz,ptr)
#define UFT_FREE_BLOCK_I(sz,ptr) uft::Runtime::s_instance->freeBlock(sz,ptr)

#ifdef UFT_NEW_DISABLE
#undef UFT_NEW
#define UFT_NEW new
#endif

namespace uft
{

struct Exception;
struct NoThrow {};
const NoThrow NOTHROW = {};

void * allocBlock( size_t size );
void * allocBlock( size_t size, const char * file, int line );
void freeBlock( size_t size, void * block );

#if UFT_MEMORY_DEBUG
void dumpHeap();
#endif

/* This provides memory allocation funtions similar to the standard
 * C language functions except the memory usage is tracked for interaction
 * with the caching and limiting overall usage. */
class Allocator 
{
public:
	/* Has the same functionality as the standard malloc function. This should not
	   be used unless the caller checks for a NULL return and handles it
	   appropriately. */
	static void * malloc( size_t size );

	/* Similar in functionality to malloc, but never return a NULL. Throws an
	   std::bad_alloc exception is insuffient memory is present. */
	static void * mallocThrow( size_t size ) /* -later- throw( uft::Exception ) */;

	/* Has same functionality as standard free function. */
	static void free( void * p );

	/* Set the amount of memory that can be used. The upperLimit is the maximum
	 * amount that will be allocated. If upperLimit is exceeded, further memory
	 * allocations will throw or return NULL. highWaterMark is the desired maximum
	 * usage. If an allocation would exceed this usage, memory will be freed from
	 * the cache until lowWaterMark is reached. If the caches have been completely
	 * cleared of all item that are not locked, allocations will continue beyond
	 * highWaterMark until upperLimit is reached. A mimimal amount of memory is
	 * made available before this is called to aid in system startup. */
	/* To be implemented later */
	// static void setMemoryLimits( size_t lowWaterMark, size_t highWaterMark, size_t upperLimit );

	/* Get the size of the allocation specified by p. This is needed to implement
	   uft_realloc */
	/* To be implemented later */
	// static size_t getAllocationSize( void *p ); 

	/* The following two functions are low-level versions of malloc and free where the
	   client is responsible for saving the size of the allocation and passing it to free */

	/* Has the same functionality as the standard malloc function except it does not save
	   the size of the allocation. The client must save the size and pass it to
	   freeClientMaintainsSize This should not be used unless the caller checks for a
	   NULL return and handles it appropriately. */
	static void * mallocClientMaintainsSize( size_t size );

	/* Similar in functionality to mallocClientMaintainsSize, but never return a NULL.
	   Throws an uft::Exception(uft::BAD_ALLOC) exception is insuffient memory is present. */
	static void * mallocClientMaintainsSizeThrow( size_t size ) /* -later- throw( uft::Exception ) */;

	/* Has same functionality as standard free function except it expects the client to
	   pass in the size of the area to be freed. This may only be used with memory obtained
	   from the mallocClientMaintainsSize or mallocClientMaintainsSizeThrow call. */
	static void freeClientMaintainsSize( void * p, size_t size );

	/* Allows us to notify T3 if this is the first call for freeing cache items in
	   this allocation or if it is a subsequent call. This should only be called
	   by embed::EmbedMemoryContext. This cannot be declared as private with
	   embed::EmbedMemoryContext as a friend because embed::EmbedMemoryContext is
	   a template class and it would drag in too much of T3 to define all the
	   template aparameters. */
	/* To be implemented later */
	//static bool isFirstCallThisAlloc();

};


/* This provides new and delete methods that function in the same manner as
 * the standard ones, except except the memory usage is tracked for interaction
 * with the caching and limiting overall usage. Any class whose objects are created
 * with UFT_NEW should be derived from this class. */
class AllocBase
{
public:
	/* Have the same functionality as standard new and delete . */
	void * operator new( size_t size ) /* -later- throw( uft::Exception ) */;
	void operator delete( void * obj) throw();

	/* Has same functionality as standard new and delete with no_throw specified */
	void * operator new( size_t size, const NoThrow& noThrow) throw();
	void operator delete( void * obj, const NoThrow& noThrow) throw();

	/* Have the same functionality as standard array versions of new and delete . */
	void * operator new[]( size_t size ) /* -later- throw( uft::Exception ) */;
	void operator delete[]( void * obj) throw();

	/* Has same functionality as standard array versions of new  and delete
	 * with no_throw specified */
	void * operator new[]( size_t size, const NoThrow& noThrow) throw();
	void operator delete[]( void * obj, const NoThrow& noThrow) throw();

	/* Have the same functionality as standard placement new and delete . */ 
	void * operator new( size_t, void * p ) throw() { return p; }
	void operator delete( void *, void * ) throw() {}

	/* Have the same functionality as standard array placement new and delete . */ 
	void * operator new[]( size_t, void * p ) throw() { return p; }
	void operator delete[]( void *, void * ) throw() {}

};

#ifdef max
#define MAX_SAVE(a, b) max(a, b)
#undef max
#endif

template<class T>
class AllocatorForSTL
{	// allocator for objects of class T. Intended for use with STL.
public:
	typedef T value_type;
	typedef size_t size_type;
	typedef ptrdiff_t difference_type;

	typedef value_type * pointer;
	typedef const value_type * const_pointer;

	typedef value_type & reference;
	typedef const value_type & const_reference;

	pointer address(reference V) const
	{
		return (&V);
	}

	const_pointer address(const_reference V) const
	{
		return (V);
	}

	AllocatorForSTL() throw()
	{
	}

	template<class U>
	AllocatorForSTL( const AllocatorForSTL<U>& ) throw()
	{
	}

	~AllocatorForSTL() throw()
	{
	}

	pointer allocate(size_type count, const void * hint= 0)
	{	
		return static_cast<T *>(uft::Allocator::mallocThrow( count * sizeof(T) ));
	}

	void deallocate(pointer p, size_type)
	{
		uft::Allocator::free( p );
	}

	void construct(pointer p, const T& val)
	{
		new (p) T(val);
	}

	void destroy(pointer p)
	{
		p->~T();
	}

	size_t max_size() const throw()
	{
		// TODO - hook into uft::Allocator
	#ifdef STL_EXCLUDED
		size_t num = UINT_MAX / sizeof (T);
	#else
		size_t num = std::numeric_limits<size_t>::max() / sizeof (T);
	#endif
		return num;
	}

	template<class U>
	struct rebind
	{
		typedef AllocatorForSTL<U> other;
	};
};

#ifdef MAX_SAVE
#define max(a, b) MAX_SAVE(a, b)
#undef MAX_SAVE
#endif

class ArrayHelper
{
public:
	template<class T>
#if UFT_MEMORY_DEBUG
	static T* arrayFactory( size_t count, const char * file, int line )
#else
	static T* arrayFactory( size_t count )
#endif
	{
		size_t allocSize = (sizeof(T) * count) + sizeof( size_t );
		void* location = uft::Allocator::mallocThrow( allocSize );
		void* itemLocation = static_cast<char *>(location) + sizeof( size_t );
		T * result = static_cast<T *>(itemLocation);
		for ( size_t i = 0; i < count; i++ )
		{
			new( itemLocation ) T;
			itemLocation = static_cast<char *>(itemLocation) + sizeof( T );
		}
		size_t * countLocation = static_cast<size_t *>(location);
		*countLocation = count;
		return result;
	}

	template<class T>
	static void destroyArray( T* ptr )
	{
		if ( ptr == NULL )
			return;
		void * location = static_cast<void *>( ptr );
		location = static_cast<char *>( location ) - sizeof( size_t );
		size_t * countLocation = static_cast<size_t *>(location);
		size_t count = *countLocation;
		for ( size_t i = count; i > 0; )
		{
			(ptr + --i)->~T();
		}
		uft::Allocator::free( location );
	}
};

}

#endif //_UFT_ALLOC_H

