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

#ifndef _UFT_STRING_H
#define _UFT_STRING_H

#include "uft_assert.h"
#include "uft_value.h"
#include "uft_atomid.h"

namespace uft
{

class UTF16StringBlock;
class UTF16String;
class Buffer;

class StringBuffer : public Value
{
public:
	StringBuffer( int bufSize = 64 );
	StringBuffer( const String& str );
	StringBuffer( const String& str, size_t first );
	StringBuffer( const String& str, size_t first, size_t last );
	StringBuffer( const Buffer& buf, size_t first, size_t last );

	StringBuffer( const StringBuffer& other ) : Value(other) { }

	size_t length() const;
	size_t utf16length() const;

	char operator[]( size_t index ) const;
	const char * buffer() const; // non-zero-terminated
	const char * utf8() const; // zero-terminated

	bool isXMLName() const;

	char * writableBuffer() const; // non-zero-terminated buffer that can be written
	char * writableBuffer( size_t reqSize ) const; // non-zero-terminated buffer that can be written

	String atom() const;

	size_t indexOf(const String& what, size_t index1 = 0, size_t index2 = npos ) const;
	size_t indexOf(const StringBuffer& what, size_t index1 = 0, size_t index2 = npos ) const;
	size_t indexOf(const char *s, size_t count, size_t index1, size_t index2 = npos ) const;
	size_t indexOf(const char *s, size_t index1 = 0) const;
	size_t indexOf(char c, size_t index1 = 0, size_t index2 = npos) const;

	size_t lastIndexOf(const String& x, size_t index2 = npos, size_t index1 = 0 ) const;
	size_t lastIndexOf(const StringBuffer& x, size_t index2 = npos, size_t index1 = 0 ) const;
	size_t lastIndexOf(const char *s, size_t count, size_t index2, size_t index1 = 0 ) const;
	size_t lastIndexOf(const char *s, size_t index2 = npos) const;
	size_t lastIndexOf(char c, size_t index2 = npos, size_t index1 = 0 ) const;

	size_t findFirstOf(const String& x, size_t index1 = 0, size_t index2 = npos ) const;
	size_t findFirstOf(const StringBuffer& x, size_t index1 = 0, size_t index2 = npos ) const;
	size_t findFirstOf(const char *s, size_t count, size_t index1, size_t index2 = npos ) const;
	size_t findFirstOf(const char *s, size_t index1 = 0) const;

	size_t findLastOf(const String& x, size_t index2 = npos, size_t index1 = 0 ) const;
	size_t findLastOf(const StringBuffer& x, size_t index2 = npos, size_t index1 = 0 ) const;
	size_t findLastOf(const char *s, size_t count, size_t index2, size_t index1 = 0  ) const;
	size_t findLastOf(const char *s, size_t index2 = npos, size_t index1 = 0 ) const;

	size_t findFirstNotOf(const String& x, size_t index1 = 0, size_t index2 = npos ) const;
	size_t findFirstNotOf(const StringBuffer& x, size_t index1 = 0, size_t index2 = npos ) const;
	size_t findFirstNotOf(const char *s, size_t count, size_t index1, size_t index2 = npos ) const;
	size_t findFirstNotOf(const char *s, size_t index1 = 0) const;

	size_t findLastNotOf(const String& x, size_t index2 = npos, size_t index1 = 0 ) const;
	size_t findLastNotOf(const StringBuffer& x, size_t index2 = npos, size_t index1 = 0 ) const;
	size_t findLastNotOf(const char *s, size_t count, size_t index2, size_t index1 = 0  ) const;
	size_t findLastNotOf(const char *s, size_t index2 = npos, size_t index1 = 0 ) const;

	int compare(const String& other) const;
	int compare(const StringBuffer& other) const;
	int compare(const char *s) const;
	int compareRegion(size_t index, const String& other, size_t oindex, size_t length ) const;
	int compareRegion(size_t index, const StringBuffer& other, size_t oindex, size_t length ) const;
	int compareRegion(size_t index, const char *other, size_t length ) const;

	bool operator==( const String& other ) const;
	bool operator==( const StringBuffer& other ) const;
	bool operator==( const char * other ) const;
	bool operator!=( const String& other ) const;
	bool operator!=( const StringBuffer& other ) const;
	bool operator!=( const char * other ) const;

	bool startsWith( const String& other ) const;
	bool startsWith( const StringBuffer& other ) const;
	bool startsWith( const char * other ) const;
	bool startsWith( const char * other, size_t len ) const;
	bool endsWith( const String& other ) const;
	bool endsWith( const StringBuffer& other ) const;
	bool endsWith( const char * other ) const;
	bool endsWith( const char * other, size_t len ) const;

	StringBuffer substr( size_t index ) const;
	StringBuffer substr( size_t index1, size_t index2 ) const;

	void append( const String& other );
	void append( const StringBuffer& other );
	void append( const Value& v );
	void append( const char * utf8 );
	void append( const char * utf8, size_t len );
	void append( char c );
	void append( int n );
	void append( float f );

	StringBuffer& operator+=( const String& v ) { append(v); return *this; }
	StringBuffer& operator+=( const char * v ) { append(v); return *this; }
	StringBuffer& operator+=( char v ) { append(v); return *this; }
	StringBuffer& operator+=( int v ) { append(v); return *this; }
	StringBuffer& operator+=( float v ) { append(v); return *this; }
	StringBuffer& operator+=( const Value& v ) { append(v); return *this; }

	void splice( size_t index1, size_t index2, const StringBuffer& str );
	void splice( size_t index1, size_t index2, const StringBuffer& str, size_t sIndex1, size_t sIndex2 );
	void splice( size_t index1, size_t index2, const String& str );
	void splice( size_t index1, size_t index2, const String& str, size_t sIndex1, size_t sIndex2 );
	void splice( size_t index1, size_t index2, const Value& str );
	void splice( size_t index1, size_t index2, const char * uft8 );
	void splice( size_t index1, size_t index2, const char * uft8, size_t len );

	void remove( size_t index1, size_t index2 ) { splice( index1, index2, NULL ); }
	void trimToSize( size_t size ) { splice( size, length(), NULL ); }
	void insert( size_t index, const String& str ) { splice( index, index, str ); }
	void insert( size_t index, const StringBuffer& str ) { splice( index, index, str ); }
	void insert( size_t index, const Value& val ) { splice( index, index, val ); }
	void insert( size_t index, const char * utf8 ) { splice( index, index, utf8 ); }
	
	uft::StringBuffer trimWhitespace() const;
	uft::StringBuffer lowercase() const;
	uft::StringBuffer uppercase() const;

	static const size_t npos = (size_t)(-1); // "not found" position = -1

	static bool isInstanceOf( const Value& v ) { return v.isStringBuffer(); }

private:

	StringBuffer( const Value& bufferOwner, char * bytes, size_t length );

private:

	StringBufferStruct* getStringBufferStruct() const { return (StringBufferStruct*)((StructBlock*)getBlock())->getStructPtr(); }
};

class TransientStringBuffer : public StringBuffer
{
public:

	TransientStringBuffer( const String& str )
		: StringBuffer( str )
	{
	}

	TransientStringBuffer( const String& str, size_t first )
		: StringBuffer( str, first )
	{
	}

	TransientStringBuffer( const String& str, size_t first, size_t last )
		: StringBuffer( str, first, last )
	{
	}

	// this may seem wierd, but it is needed to make str+str+str+... work efficiently
	TransientStringBuffer& operator+( const String& v ) { append(v); return *this; }
	TransientStringBuffer& operator+( const char * v ) { append(v); return *this; }
	TransientStringBuffer& operator+( int v ) { append(v); return *this; }
	TransientStringBuffer& operator+( float v ) { append(v); return *this; }
	TransientStringBuffer& operator+( const Value& v ) { append(v); return *this; }
};

//--------------------------------------------------------------------

class String : public Value
{
	friend class uft::Runtime;
	friend class uft::RuntimeImpl;
	friend class uft::BlockHead;

public:

	String() : Value(Atom(EMPTY)) {}
	String( const char * utf8 ) : Value(utf8) {}
	String( const char * utf8, size_t length ) : Value(utf8,length) {}
	String( const String& str ) : Value(str) {}
	String( const String& str, size_t start, size_t len ) : Value(str.utf8()+start,len) {}
	String( const StringBuffer& buf ) : Value(buf.toString()) {}
	String( const TransientStringBuffer& buf ) : Value(buf.toString()) {}
	explicit String( const char16 * utf16 );
	String( const char16 * utf16, size_t len );
	String( const PlatformString& str );
	String( const UTF16String& str );
	String( const UTF16String& str, size_t index1, size_t index2 );

	const String& operator=( const String& val ) { Value::operator=(val); return *this; }
	const String& operator=( char c ) { Value::operator=(c); return *this; }

	// number of utf8-encoded bytes
	size_t length() const { return getStringBlock()->getByteSize() - (sizeof(uint32)+1); }
	size_t utf16length() const;
	const char * utf8() const { return getStringBlock()->utf8(); }

	// returns the number of bytes that constitute the maximum valid whole Unicode characters
	static size_t utf8RoundOffLen( const char * s, size_t offset );

	// returns number of utf16 characters in uft8 fragment
	static size_t utf8count( const char * utf8, size_t utf8len, size_t uft16count );

	const char * c_str() const
	{
		if( isNull() )
			return NULL;
		return utf8();
	}

	bool isEmpty() const { return isNull() || length() == 0; }

	static bool isXMLName( const char * str, size_t length );

	bool isXMLName() const { return !isNull() && isXMLName( c_str(), length() ); }

	char operator[]( size_t index ) const
	{
		assertLogic( index < length() );
		return getStringBlock()->utf8()[index];
	}

	TransientStringBuffer substr( size_t index ) const
	{
		return TransientStringBuffer( *this, index );
	}

	TransientStringBuffer substr( size_t index1, size_t index2 ) const
	{
		return TransientStringBuffer( *this, index1, index2 );
	}

	TransientStringBuffer operator+( const String& other ) const
	{
		return TransientStringBuffer(*this) + other;
	}

	TransientStringBuffer operator+( const char * utf8 ) const
	{
		return TransientStringBuffer(*this) + utf8;
	}

	TransientStringBuffer operator+( int n ) const
	{
		return TransientStringBuffer(*this) + n;
	}

	TransientStringBuffer operator+( float f ) const
	{
		return TransientStringBuffer(*this) + f;
	}

	TransientStringBuffer operator+( const Value& v ) const
	{
		return TransientStringBuffer(*this) + v;
	}

	float atof() const;
	static float atof( const char * s );
	static float atof( const char * s, size_t length );

	int atoi() const;

	// lower/uppercase guaranteed to work only on ASCII chars at the moment
	String lowercase() const;
	String uppercase() const;

	// make the string appropriate for a file name by filtering out inappropriate characters
	String filename( size_t maxLen = 64 ) const;

	String atom() const;
	static String atom( const char * utf8 );
	static String atom( const char * utf8, size_t length );
	static String atom( uint32 atomID );

#ifdef UFT_ATOM_COMPILE_TIME
	static const String& predefinedAtom( uint32 atomID ) { assertLogic(atomsInitialized()); return *((uft::String*)(s_rawAtomList-1+atomID)); }
#else
	static String predefinedAtom( uint32 atomID ) { return atom(atomID); }
#endif

	// Note: no lookup required, fast inlined function; returns 0 for non-atoms
	uint32 atomID() const { return getStringBlock()->atomID(); }
	
	static uint32 atomID( const char * utf8 ) { return atom(utf8).atomID(); }

	size_t indexOf(const String& what, size_t index1 = 0, size_t index2 = npos ) const;
	size_t indexOf(const StringBuffer& what, size_t index1 = 0, size_t index2 = npos ) const;
	size_t indexOf(const char *s, size_t count, size_t index1, size_t index2 = npos ) const;
	size_t indexOf(const char *s, size_t index1 = 0) const;
	size_t indexOf(char c, size_t index1 = 0, size_t index2 = npos) const;

	size_t lastIndexOf(const String& x, size_t index2 = npos, size_t index1 = 0 ) const;
	size_t lastIndexOf(const StringBuffer& x, size_t index2 = npos, size_t index1 = 0 ) const;
	size_t lastIndexOf(const char *s, size_t count, size_t index2, size_t index1 = 0 ) const;
	size_t lastIndexOf(const char *s, size_t index2 = npos) const;
	size_t lastIndexOf(char c, size_t index2 = npos, size_t index1 = 0 ) const;

	size_t findFirstOf(const String& x, size_t index1 = 0, size_t index2 = npos ) const;
	size_t findFirstOf(const StringBuffer& x, size_t index1 = 0, size_t index2 = npos ) const;
	size_t findFirstOf(const char *s, size_t count, size_t index1, size_t index2 = npos ) const;
	size_t findFirstOf(const char *s, size_t index1 = 0) const;

	size_t findLastOf(const String& x, size_t index2 = npos, size_t index1 = 0 ) const;
	size_t findLastOf(const StringBuffer& x, size_t index2 = npos, size_t index1 = 0 ) const;
	size_t findLastOf(const char *s, size_t count, size_t index2, size_t index1 ) const;
	size_t findLastOf(const char *s, size_t index2 = npos, size_t index1 = 0 ) const;

	size_t findFirstNotOf(const String& x, size_t index1 = 0, size_t index2 = npos ) const;
	size_t findFirstNotOf(const StringBuffer& x, size_t index1 = 0, size_t index2 = npos ) const;
	size_t findFirstNotOf(const char *s, size_t count, size_t index1, size_t index2 = npos ) const;
	size_t findFirstNotOf(const char *s, size_t index1 = 0) const;

	size_t findLastNotOf(const String& x, size_t index2 = npos, size_t index1 = 0 ) const;
	size_t findLastNotOf(const StringBuffer& x, size_t index2 = npos, size_t index1 = 0 ) const;
	size_t findLastNotOf(const char *s, size_t count, size_t index2, size_t index1 = 0  ) const;
	size_t findLastNotOf(const char *s, size_t index2 = npos, size_t index1 = 0 ) const;

	int compare(const String& other) const;
	int compare(const char *s) const;
	int compareRegion(size_t index, const String& other, size_t oindex, size_t length ) const;
	int compareRegion(size_t index, const char *other, size_t length ) const;

	// because atoms can be compared very fast, == is more efficient then compare
	bool operator==( const String& other ) const;
	bool operator==( const char * other ) const;
	bool operator!=( const String& other ) const;
	bool operator!=( const char * other ) const;

	bool startsWith( const String& other ) const;
	bool startsWith( const char * other ) const;
	bool endsWith( const String& other ) const;
	bool endsWith( const char * other ) const;

	String replaceAll( char search, char replace );
	String replaceAll( const String& search, const String& replace );

	static const size_t npos = (size_t)(-1); // "not found" position = -1

	static String toString( int32 i );
	static String toString( float v );
	static String toString( int64 i );
	static String toString( double v );

	// return final position in the stream; return initial position and don't assign out param
	// if could not parse
	static const char * parseInt( const char * str, int32 * valOut, const char * end = NULL );
	static const char * parseLong( const char * str, int64 * valOut, const char * end = NULL );
	static const char * parseFloat( const char * str, float * valOut, const char * end = NULL );
	static const char * parseDouble( const char * str, double * valOut, const char * end = NULL );
	static const char * parseASCIIIdent( const char * str, uft::String * atomOut, bool dashIsOK = true, const char * end = NULL );
	static const char * skipWhitespace( const char * str, const char * end = NULL );
	static const char * skipWhitespaceOrComma( const char * str, bool * hadComma = NULL, const char * end = NULL );
	static const char * parseNonWhitespace( const char * str, uft::String * strOut, const char * end = NULL );

	static const String& nullValue() { return checked_cast<uft::String>(uft::Value::nullValue()); }

#ifdef UFT_ATOM_COMPILE_TIME
	static const String& emptyValue() { return Atom(EMPTY); }
#else
	static const String& emptyValue();
#endif

	static bool isInstanceOf( const Value& v ) { return v.isString(); }

private:

	static char * s_rawAtomList[];

	// only for use in asserts; if this one returns false, it means that atom table was not initialized yet
	static bool atomsInitialized() { return ((uft::Value*)(s_rawAtomList))->isAtom(); }

	void initPlatform( const char * localStr, size_t nChars );
	void initUTF16( const char16 * str, size_t nChars );

private:

	String( StringBlock * block ) : Value(block) {}
	StringBlock * getStringBlock() const { assertLogic(isNull()||isString()); return (StringBlock*)getBlock(); }
};

inline TransientStringBuffer operator+( const char * s, const String& str )
{
	TransientStringBuffer res( s );
	res.append( str );
	return res;
}

class PlatformString : public Value
{
	friend class uft::BlockHead;

public:

	PlatformString() : Value(Atom(EMPTY)) {}
	PlatformString( const char * localStr );
	PlatformString( const char * localStr, size_t length );
	PlatformString( const String& str );
	PlatformString( const PlatformString& str );
	PlatformString( const String& str, size_t index1, size_t index2 );
	PlatformString( const StringBuffer& buf );
	explicit PlatformString( const char16 * utf16 );
	PlatformString( const char16 * utf16, size_t len );
	PlatformString( const UTF16String& str );
	PlatformString( const UTF16String& str, size_t index1, size_t index2 );

	const PlatformString& operator=( const PlatformString& val ) { Value::operator=(val); return *this; }
	const PlatformString& operator=( char c ) { Value::operator=(c); return *this; }

	// number of bytes - no knowledge about double-byte characters
	size_t length() const { return getPlatformStringBlock()->getByteSize() - (sizeof(uint32)+1); }
	const char * localStr() const { return getPlatformStringBlock()->localStr(); }

	const char * c_str() const
	{
		if( isNull() )
			return NULL;
		return localStr();
	}

	bool isEmpty() const { return isNull() || length() == 0; }

	static bool isInstanceOf( const Value& v ) { return v.isPlatformString(); }

private:

	void initUTF8( const char * str, size_t nChars );
	void initUTF16( const char16 * str, size_t nChars );

private:

	PlatformString( PlatformStringBlock * block ) : Value(block) {}
	PlatformStringBlock * getPlatformStringBlock() const { assertLogic(isNull()||isPlatformString()); return (PlatformStringBlock*)getBlock(); }
};

class UTF16String : public Value
{
	friend class uft::BlockHead;

public:
	UTF16String() : Value(s_empty) {}
	UTF16String( const char * utf8 );
	UTF16String( const char * utf8, size_t len );
	UTF16String( const String& str );
	UTF16String( const String& str, size_t index1, size_t index2 );
	UTF16String( const StringBuffer& str );
	UTF16String( const StringBuffer& str, size_t index1, size_t index2 );
	UTF16String( const char16 * utf16 ) : Value(utf16) {}
	UTF16String( const char16 * utf16, size_t len ) : Value(utf16,len) {}
	UTF16String( const PlatformString& str );
	UTF16String( const UTF16String& str ) : Value(str) {}

	const char16 * utf16() const;
	size_t length() const;

	static bool isInstanceOf( const Value& v ) { return v.isUTF16String(); }
	static size_t str16len( const char16 * str );
	static size_t utf16length( const char * utf8, size_t len );
	static bool isWhiteSpace( const char16 wideChar );

	static UTF16String s_empty;

private:

	void initUTF8( const char * utf8, size_t len );
	void initPlatform( const char * localStr, size_t nChars );

	UTF16String( UTF16StringBlock * block );
	UTF16StringBlock * getUTF16StringBlock() const { assertLogic(isNull()||isUTF16String()); return (UTF16StringBlock*)getBlock(); }
};

//------------------ underlying data structures, never manipulated directly ------------------------

class UTF16StringBlock : public SizedBlock
{
	friend class uft::BlockHead;

public:

	size_t length() const { return getByteSize()/sizeof(char16) - 1; }
	char16 * utf16() { return m_utf16; }

private:
	char16 m_utf16[1]; // m_byteSize/2 characters including trailing 0
};

class StringBufferStruct
{

	friend class uft::StringBuffer;

private:

	StringBufferStruct( size_t size ) : m_buffer(String( (char*)NULL, size )), m_start(0), m_length(0)
	{
	    m_start = const_cast<char*>(assumed_cast<uft::String>(m_buffer).utf8());
	}

	StringBufferStruct( const String& str, size_t first, size_t last ) : m_buffer(str), m_start(0), m_length(last-first)
	{
	    assertLogic( last >= first );
	    m_start = const_cast<char*>(str.utf8()) + first;
	}

	StringBufferStruct( const Buffer& buf, size_t first, size_t last );

	StringBufferStruct( const Value& bufferOwner, char * bytes, size_t length );

public:

	~StringBufferStruct();

	String toString() const
	{
	    if (m_buffer.isString())
	    {
	        const String s = assumed_cast<const String>(m_buffer);
	        if (const_cast<char*>(s.utf8()) == m_start && s.length() == m_length)
	        {
	            // optimization: just return m_buffer
	            return s;
	        }
	        // fall thru
	    }
	    return String( m_start, m_length );
	}
	
	bool query( const Value& what, void * res ) { return false; }
	uft::WeakReferencePtr * getWeakReferencePtr() { return NULL; }

private:

	Value m_buffer;
	char * m_start;
	size_t m_length;
};

/* Inline functions for StringBuffer */

inline size_t StringBuffer::length() const
{
	StringBufferStruct * self = getStringBufferStruct();
	return self->m_length;
}

inline char StringBuffer::operator[]( size_t index ) const
{
	StringBufferStruct * self = getStringBufferStruct();
	assertLogic( index < self->m_length && index >= 0 );
	return self->m_start[index];
}

inline const char * StringBuffer::buffer() const
{
	StringBufferStruct * self = getStringBufferStruct();
	return self->m_start;
}

inline String StringBuffer::atom() const
{
	return toString().atom();
}

//------------------------------ Accessor interface extraction ------------------------
// [have to go here instead of uft_value.h, because it needs String class]

/**
 * this is a simple object model on top of uft::Value: Values can implement certain "Accessor" interfaces
 * or contain some data structures. Such object needs to respond to queries for Accessor CLASS_ID (which is an UFT Atom ID)
 * and return a pointer to an Accessor interface or a data structure. These templates function facilitates the process.
 */
template<class T> T * checked_query(const Value& obj)
{
	T * t;
	if( obj.query( String::predefinedAtom(T::CLASS_ID), &t ) )
		return t;
	return NULL;
}

template<class T> T * assumed_query(const Value& obj)
{
	T * t;
	if( obj.query( String::predefinedAtom(T::CLASS_ID), &t ) )
		return t;
	assertLogicMsg( false, "Failed to extract a type from uft::Value" );
	return NULL;
}

// convenience Macros to implement query. Use inside switch(what.atomID()) { ... }

#define UFT_QUERY_ACCESSOR_CASE(Impl)		case Impl::CLASS_ID : if(res) *(Impl**)res = &Impl::s_instance; return true
#define UFT_QUERY_STRUCT_CASE(Struct)		case Struct::CLASS_ID : if(res) *(Struct**)res = this; return true

} // end namespace uft

#endif // _UFT_STRING_H
