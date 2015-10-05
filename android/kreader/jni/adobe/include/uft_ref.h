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

#ifndef _UFT_REF_H
#define _UFT_REF_H

#include "uft_container.h"

// Macros to define members for structs to be usable as uft::Value

// without weak reference support
#define UFT_STRUCT_DECL_NO_QUERY \
	static const uft::StructDescriptor * s_descriptor; \
	void * operator new( size_t size, uft::Value& value ) { return ::operator new( size, s_descriptor, &value ); } \
	void operator delete( void *ptr, uft::Value& value ) { assertLogic(false); } \
	void * operator new( size_t size, uft::Value& value, const char * file, int line ) { return ::operator new( size, s_descriptor, &value, file, line ); } \
	void operator delete( void *ptr, uft::Value& value, const char * file, int line ) { assertLogic(false); } \
	uft::WeakReferencePtr * getWeakReferencePtr() { return NULL; } \
	static const uft::StructDescriptor * staticInit();

// with weak reference support
#define UFT_STRUCT_W_DECL_NO_QUERY \
	static const uft::StructDescriptor * s_descriptor; \
	void * operator new( size_t size, uft::Value& value ) { return ::operator new( size, s_descriptor, &value ); } \
	void operator delete( void *ptr, uft::Value& value ) { assertLogic(false); } \
	void * operator new( size_t size, uft::Value& value, const char * file, int line ) { return ::operator new( size, s_descriptor, &value, file, line ); } \
	void operator delete( void *ptr, uft::Value& value, const char * file, int line ) { assertLogic(false); } \
	uft::WeakReferencePtr m_weakReference_; \
	uft::WeakReferencePtr * getWeakReferencePtr() { return &m_weakReference_; } \
	static const uft::StructDescriptor * staticInit();

// without weak reference support
#define UFT_STRUCT_DECL \
	UFT_STRUCT_DECL_NO_QUERY \
	bool query( const uft::Value&, void * res ) { return false; }

// with weak reference support
#define UFT_STRUCT_W_DECL \
	UFT_STRUCT_W_DECL_NO_QUERY \
	bool query( const uft::Value&, void * res ) { return false; }

#define UFT_STRUCT_IMPL(type) \
	const uft::StructDescriptor * type::s_descriptor = type::staticInit(); \
	const uft::StructDescriptor * type::staticInit() { \
		static uft::ClassDescriptor<type> desc(#type); \
		s_descriptor = &desc; \
		return &desc; \
	}

#define UFT_STRUCT_CACHING_IMPL(type) \
	const uft::StructDescriptor * type::s_descriptor = type::staticInit(); \
	const uft::StructDescriptor * type::staticInit() { \
		static uft::CachingClassDescriptor<type> desc(#type); \
		s_descriptor = &desc; \
		return &desc; \
	}

#define UFT_STRUCT_RIBBINDER_IMPL(type, binder) \
	const uft::StructDescriptor * type::s_descriptor = type::staticInit(); \
	const uft::StructDescriptor * type::staticInit() { \
		static uft::ClassDescriptor<type> desc(#type, binder); \
		s_descriptor = &desc; \
		return &desc; \
	}

namespace uft
{

class WeakRef;

class WeakReferencePtr
{
	friend class SimpleRefStruct;
	friend class WeakRef;

public:

	WeakReferencePtr() : m_weakRef(NULL) {}
	WeakReferencePtr( const WeakReferencePtr& ) : m_weakRef(NULL) {}
	const WeakReferencePtr& operator=( const WeakReferencePtr& p ) { return p; }
	inline ~WeakReferencePtr()
	{
		if( m_weakRef )
			*m_weakRef = 1; // no longer valid, was not participating in ref count
	}

private:

	word * m_weakRef;
};

template<class Cs> class ClassDescriptor : public StructDescriptor
{
public:

	ClassDescriptor( const char * name, rib::Binder* binder = NULL, uft::flushFunc_t flushFunc = NULL )
		: StructDescriptor( sizeof(Cs), name, destroyFunc, copyFunc, toStringFunc, queryFunc, getWeakRefPtr, flushFunc, binder )
	{
	}

	static void destroyFunc( const StructDescriptor * , void * ptr )
	{
		((Cs*)ptr)->~Cs();
	}

	static void copyFunc( const StructDescriptor * , void * dest, const void * src )
	{
		::new( (RawMemory*)dest ) Cs( *(const Cs*)src );
	}

	static String toStringFunc( const StructDescriptor * , void * ptr )
	{
		return ((Cs*)ptr)->toString();
	}

	static bool queryFunc( const StructDescriptor * , void * ptr, const Value& what, void * result )
	{
		return ((Cs*)ptr)->query(what,result);
	}

	static WeakReferencePtr * getWeakRefPtr( const StructDescriptor * self, void * ptr )
	{
		return ((Cs*)ptr)->getWeakReferencePtr();
	}

};

template<class Cs> class CachingClassDescriptor : public ClassDescriptor<Cs>
{
public:

	CachingClassDescriptor( const char * name, rib::Binder* binder = NULL )
		: ClassDescriptor<Cs>( name, binder, flushFunc )
	{
	}

	static void flushFunc( const StructDescriptor * , void * ptr, CacheFlushContext * context )
	{
		return ((Cs*)ptr)->flush(context);
	}

};

template<class Cs> class sref : public Value
{
public:

	sref() {}

	sref( const Cs& other )
	{
		new(Cs::s_descriptor,this) Cs(other);
	}

	Cs* operator->() const
	{
		StructBlock * block = (StructBlock*)getBlock();
		assertLogic( block->getStructDescriptor() == Cs::s_descriptor );
		return (Cs*)block->getStructPtr();
	}

	Cs& operator*() const
	{
		StructBlock * block = (StructBlock*)getBlock();
		assertLogic( block->getStructDescriptor() == Cs::s_descriptor );
		return *(Cs*)block->getStructPtr();
	}

	operator Cs*() const
	{
		if( isNull() )
			return NULL;
		StructBlock * block = (StructBlock*)getBlock();
		assertLogic( block->getStructDescriptor() == Cs::s_descriptor );
		return (Cs*)block->getStructPtr();
	}

	static const StructDescriptor * structDescriptor() { return Cs::s_descriptor; }

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(Cs::s_descriptor); }

	static const uft::sref<Cs>& nullValue() { return checked_cast<uft::sref<Cs> >(uft::Value::nullValue()); }
};

class ValueRef;
class WeakRef;

typedef Value (*LazyFunc_t)( const uft::Value& val );

class ValueRef : public Value
{
public:

	enum Flags
	{
		MUTABLE = 1,
		CACHE = 2
	};

	ValueRef() {}
	ValueRef( const Value& value, uft::uint32 flags = 0 );

	uft::Value operator*() const { return extractValue(); }

public:

	static bool isInstanceOf( const Value& v ) { return v.query( Atom(ValueRef), NULL ); }
};

class LazyRef : public ValueRef
{
public:

	LazyRef() {}
	LazyRef( const uft::Value& val, LazyFunc_t lazy, bool cache = false );

	static bool isInstanceOf( const Value& v ) { return v.query( Atom(LazyRef), NULL ); }
};

class WeakRef : public ValueRef
{
public:

	WeakRef() {}
	WeakRef( const uft::Value& val );

	static bool isInstanceOf( const Value& v )  { return v.query( Atom(WeakRef), NULL ); }
};

class CacheRef : public ValueRef
{
public:

	CacheRef() {}
	CacheRef( const uft::Value& val ) : ValueRef( val, CACHE ) {}
	
	static bool isInstanceOf( const Value& v ) { return v.query( Atom(CacheRef), NULL ); }
};

class MutableRefAssigner;

class MutableRef : public ValueRef
{
public:

	MutableRef() {}
	MutableRef( const uft::Value& val ) : ValueRef( val, MUTABLE ) {}

	void assign( const uft::Value& value ) const;
	inline MutableRefAssigner operator*() const;

	static bool isInstanceOf( const Value& v ) { return v.query( Atom(MutableRef_accessor), NULL ); }
};

class MutableRefAssigner
{
public:

	MutableRefAssigner( const MutableRef& ref ) : m_ref(ref) {}

	const Value& operator=( const uft::Value& val ) { m_ref.assign(val); return val; }
	operator Value() const { return m_ref.extractValue(); }

private:

	const MutableRef& m_ref;
};

// inline
MutableRefAssigner MutableRef::operator*() const
{
	return MutableRefAssigner(*this);
}

class MutableRefAccessor
{
public:

	static const uft::uint32 CLASS_ID;

	virtual void assign( const uft::Value& refValue, const uft::Value& valueToAssign ) = 0;
};

}

#endif // _UFT_REF_H

