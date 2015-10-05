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

#ifndef _UFT_QNAME_H
#define _UFT_QNAME_H

#include "uft_string.h"

namespace uft
{

class QName;

/**
  supporting class for uft::QName.
 */
class QNameStruct
{
private:

	// only used by QName object
	friend class uft::QName;

	QNameStruct( const String& nsuri, const String& prefix, const String& localName );

	const String& getPrefix() const { return m_prefix; }
	const String& getLocal() const { return m_local; }
	const String& getNSURI() const { return m_nsuri; }
	const String& getCanonical() const { return m_canonical; }
	const String getTriplet() const;
	
public:

	String toString() const
	{
		if( m_prefix.isEmpty() )
			return m_local;
		return m_prefix + ":" + m_local;
	}

	bool query( const Value&, void * res ) { return false; }

	uft::WeakReferencePtr * getWeakReferencePtr() { return NULL; }

private:

	// not implemented
	const QNameStruct& operator=( const QNameStruct& v );

private:

	String m_prefix;
	String m_local;
	String m_nsuri;
	String m_canonical;
};

/**
  W3C QName (qualified name): name, prefix, and namespace. Can be a string - in which case namespace is null
  and prefix is empty. Nomally a "canonical string" should be used as a key for a QName - since QNames with
  different prefixes but same local name and namespace are supposed to mean the same thing - but they have
  to be different objects. Canonical string is always unique, since every namespace is mapped to a unique
  internal prefix.
*/
class QName : public Value
{
public:
	QName() {} // null
	QName( const String& name ) : Value(name.atom()) {} // QName with no namespace and prefix
	QName( const String& name, bool checkTriplet ); // possible triplet
	QName( const String& nsuri, const String& prefix, const String& localName );

	const String& getNamespaceURI() const; // always an atom or null
	const String& getPrefix() const; // always an atom or null
	const String& getLocalName() const; // always an atom
	const String& getCanonicalName() const; // return name with canonical prefix; always an atom
	uint32 atomID() const; // atomID of a canonical string; never null

	/** Get expanded triplet form of qname formatted as follows:
		uri'^'localname'^'prefix
		return String value has been Atom-ized
	*/
	const String getTriplet() const;

	// equality test only compares canonical names
	bool operator==( const QName& other ) const
	{
		return atomID() == other.atomID();
	}

	bool operator!=( const QName& other ) const
	{
		return atomID() != other.atomID();
	}

	static bool isInstanceOf( const Value& v ) { return v.isString() || v.isInstanceOf(uft::s_qnameDescriptor); }
	static const QName& nullValue() { return checked_cast<uft::QName>(uft::Value::nullValue()); }

	static uft::String getCanonicalPrefix( const uft::String& ns );
	static uft::String getCanonicalNS( const uft::String& prefix );
	static uft::QName fromCanonicalString( const uft::String str );

	uft::WeakReferencePtr * getWeakReferencePtr() { return NULL; }

private:

	QNameStruct * getQNameStruct() const
	{
		assertLogic( uft::s_qnameDescriptor == ((StructBlock*)getBlock())->getStructDescriptor() );
		return (QNameStruct*)((StructBlock*)getBlock())->getStructPtr();
	}
};

}

  #ifndef HOBBES_EXCLUDED
  #include "uft_qname_list.h"
  #else
  #define UFT_CANONICAL_NS_TO_PREFIX_MAP ""
  #define UFT_CANONICAL_PREFIX_TO_NS_MAP ""
  #endif


#endif //_UFT_QNAME_H

