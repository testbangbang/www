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

#ifndef _UFT_CONTAINER_H
#define _UFT_CONTAINER_H

#include "uft_assert.h"
#include "uft_value.h"
#include "uft_string.h"
#include "uft_alloc.h"

namespace uft
{

class Tuple : public Value
{
public:

	Tuple();
	Tuple( const Value& a0 );
	Tuple( const Value& a0, const Value& a1 );
	Tuple( const Value& a0, const Value& a1, const Value& a2 );
	Tuple( const Value& a0, const Value& a1, const Value& a2, const Value& a3 );
	Tuple( const Value * arr, size_t len );

private:

	explicit Tuple( SizedBlock * sb );

public:

	size_t length() const
	{
		assertLogic( isTuple() );
		return ((SizedBlock*)getBlock())->getByteSize()/sizeof(Value);
	}

	const Value& operator[]( size_t index ) const
	{
		assertLogic( isTuple() );
		assertLogic( index < length() );
		return ((Value*)((SizedBlock*)getBlock())->getData())[index];
	}

	bool contains( const Value& item ) const;
	Tuple remove( size_t index ) const; // returns a tuple with an element at the given index removed
	Tuple insert( size_t index, const Value& value ) const; // returns a tuple with an element at the given index inserted
	Tuple replace( size_t index, const Value& value ) const; // returns a tuple with an element at the given index inserted
	Tuple append( const Value& value ) const; // returns a tuple with an element appended
	Tuple range( size_t offset, size_t length ) const; // returns a subrange tuple

	static bool isInstanceOf( const Value& v ) { return v.isTuple(); }

	static const Tuple& nullValue() { return checked_cast<uft::Tuple>(uft::Value::nullValue()); }
	static const Tuple& emptyValue();

};

class Vector : public Value
{
public:

	Vector( size_t length = 0, size_t capacity = 10 ) { init(length,capacity); }
	Vector( const Vector& other ) : Value(other) { }
	Vector( const Value a0 );
	Vector( const Value a0, const Value& a1 );
	Vector( const Value a0, const Value& a1, const Value& a2 );
	Vector( const Value a0, const Value& a1, const Value& a2, const Value& a3 );
	Vector( const Value * arr, size_t len );

	size_t length() const;
	size_t capacity() const;

	Value& operator[]( size_t index ) const;

	Tuple toTuple() const;

	void append( const Value& v ) const;
	void insert( size_t index, const Value& v ) const;
	void remove( size_t index ) const;
	void setCapacity( size_t capacity ) const;
	void setLength( size_t length ) const;
	void makeEmpty() const { setLength(0); }

	size_t findFirst( const Value& item ) const;

	void appendElements( const uft::Vector& list ) const;
	void appendElements( const uft::Vector& list, size_t first, size_t length ) const;
	void appendElements( const uft::Tuple& list ) const;
	void appendElements( const uft::Tuple& list, size_t first, size_t length ) const;

	Vector range( size_t index1, size_t index2 ) const;
	Tuple rangeToTuple( size_t index1, size_t index2 = uft::String::npos ) const;

	// longest common subsequence
	Vector lcs( const uft::Vector& other );

	// stack-like usage
	void push( const Value& v ) { append(v); }
	Value lastElement() const;
	Value pop() const;

	static bool isInstanceOf( const Value& v ) { return v.isStruct() && v.getStructDescriptor() == uft::s_vectorDescriptor; }

	static const Vector& nullValue() { return checked_cast<uft::Vector>(uft::Value::nullValue()); }
	static const Vector& emptyValue();

private:

	VectorStruct* getVectorStruct() const { return (VectorStruct*)((StructBlock*)getBlock())->getStructPtr(); }
	void init( size_t len, size_t capacity );
};

/**
  supporting class for uft::Dict, but can be used standalone as well.

  this class implements binary search for small-size dictionaries and
  typically automatically switches to hashing for larger sizes
 */
class DictStruct
{
public:

#if UFT_MEMORY_DEBUG
	friend void uft::dumpHeap();
#endif

	DictStruct( size_t capacity );
	DictStruct( const DictStruct& other );
	DictStruct( const Value * keysAndValues, size_t nSlots );
	DictStruct( const Value * keys, const Value * values, size_t nSlots );
	~DictStruct();

	String toString() const;

	void makeEmpty();
	void setCapacity( size_t capacity, bool hashtable = false );
	size_t getCapacity() const { return m_capacity; }

	size_t length() const { return m_length; }

	Value * getValueLoc( const Value& key, uint32 action );
	word nextKey( word pos, const Value * * key, Value * * value ) const;

	bool query( const Value&, void * res ) { return false; }

	uft::WeakReferencePtr * getWeakReferencePtr() { return NULL; }

private:

	void sort();

	// not implemented
	const DictStruct& operator=( const DictStruct& v );

private:

	Value * m_keysAndValues;
	size_t m_length;
	size_t m_capacity;
	size_t m_taken; // 0 when sorted array, number of "taken" cells plus 1 when hashtable
};

/**
   A compact mutable key-value table. This code will iterate though all elements:
<pre>
	const Value * key;
	Value * val;
	word p = 0;
	while( (p = dict.nextKey(0,&key,&val)) != 0 )
	{
		// read *key
		// read/assign *val
		// cannot add/delete entries
	}
</pre>
 */
class Dict : public Value
{
public:

	enum Action
	{
		ACT_NONE,
		ACT_INSERT,
		ACT_DELETE
	};

	Dict() : Value() {} // null Dict
	Dict( size_t capacity ) { new(uft::s_dictDescriptor,this) DictStruct(capacity>0?capacity:1); }
	Dict( const Dict& other ) : Value(other) {}
	Dict( const Value * keysAndValues, size_t nSlots ) { new(uft::s_dictDescriptor,this) DictStruct(keysAndValues,nSlots); }
	Dict( const Value * keys, const Value * values, size_t nSlots ) { new(uft::s_dictDescriptor,this) DictStruct(keys,values,nSlots); }

	size_t length() const { return getSelf()->length(); }
	Value * getValueLoc( const Value& key, Action action = ACT_NONE ) const { return getSelf()->getValueLoc(key,action); }
	word nextKey( word pos, const Value * * key, Value * * value ) const { return getSelf()->nextKey(pos,key,value); }

	bool contains( const Value& key ) const { return getValueLoc(key,ACT_NONE) != NULL; }
	Value& operator[]( const Value& key ) const { return *getValueLoc(key,ACT_INSERT); }
	const Value& get( const Value& key ) const { Value * p = getValueLoc(key); if(p) return *p; return String::nullValue(); }
	void set( const Value& key, const Value& val ) const { assertLogic(!ptrEq(emptyValue())); *getValueLoc(key,ACT_INSERT) = val; }
	void remove( const Value& key ) const { getValueLoc(key,ACT_DELETE); }
	void setCapacity( size_t capacity ) const { getSelf()->setCapacity(capacity); }
	void makeEmpty() const { getSelf()->makeEmpty(); }
	void makeHashtable() const { getSelf()->setCapacity(getSelf()->getCapacity(), true); }

	void mergeDict( const Dict& other, bool override = false ) const;

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_dictDescriptor); }

	static const Dict& nullValue() { return checked_cast<uft::Dict>(uft::Value::nullValue()); }
	static const Dict& emptyValue();

private:

	DictStruct * getSelf() const
	{
		return (DictStruct*)((StructBlock*)getBlock())->getStructPtr();
	}
};

/**
   An unordered set of objects. Each object is included in the set only once.
 */
class Set : public Value
{
public:

	enum Action
	{
		ACT_NONE,
		ACT_INSERT,
		ACT_DELETE
	};

	Set() {} // null
	Set( size_t capacity ) { init(NULL,0,capacity); }
	Set( const Set& other ) : Value(other) { }
	Set( const Value * arr, size_t len ) { init(arr,len,len); }
	Set( const Value * arr, size_t len, size_t capacity ) { init(arr,len,capacity); }
	Set( const Value& e0 );
	Set( const Value& e0, const Value& e1 );
	Set( const Value& e0, const Value& e1, const Value& e2 );
	Set( const Value& e0, const Value& e1, const Value& e2, const Value& e3 );
	Set( const Value& e0, const Value& e1, const Value& e2, const Value& e3, const Value& e4 );

	size_t length() const;
	bool manage( const Value& item, Action action = ACT_NONE ) const;
	word nextItem( word pos, const Value * * item ) const;

	bool contains( const Value& item ) const { return manage(item,ACT_NONE); }
	void add( const Value& item ) const { manage(item,ACT_INSERT); }
	void remove( const Value& item ) const { manage(item,ACT_DELETE); }
	void setCapacity( size_t capacity ) const;
	void makeEmpty() const;

	static bool isInstanceOf( const Value& v ) { return v.isStruct() && v.getStructDescriptor() == uft::s_setDescriptor; }

	static const Set& nullValue() { return checked_cast<uft::Set>(uft::Value::nullValue()); }
	static const Set& emptyValue();

private:

	void init();

	VectorStruct * getSelf() const { return (VectorStruct*)((StructBlock*)getBlock())->getStructPtr(); }
	void init( const Value * arr, size_t len, size_t capacity );
};

//------------------ underlying data structures, never manipulated directly ------------------------

class VectorStruct
{
public:

#if UFT_MEMORY_DEBUG
	friend void uft::dumpHeap();
#endif

	VectorStruct( size_t length, size_t capacity );
	VectorStruct( const VectorStruct& other );
	~VectorStruct();

	String toString() const;
	bool query( const Value&, void * res ) { return false; }
	uft::WeakReferencePtr * getWeakReferencePtr() { return NULL; }

	void setCapacity( size_t capacity );
	void setLength( size_t len );

	bool set_manage( const Value& item, Set::Action action );
	word set_nextItem( word pos, const Value * * item ) const;

private:

	// not implemented
	const VectorStruct& operator=( const VectorStruct& v );

public:

	Value * m_buf;
	size_t m_length;
	size_t m_capacity;
};

}

#endif //_UFT_CONTAINER_H

