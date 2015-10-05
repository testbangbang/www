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

#ifndef _UFT_URL_H
#define _UFT_URL_H

#include "uft_string.h"

namespace uft
{

class ValueParser;
class URL;

/**
  supporting class for uft::URL.
 */
class URLStruct
{
	friend class uft::URL;

public:

	URLStruct( const String& protocol, const String& login, const String& password,
				const String& host, uint32 port, const String& path, const String& params,
				const String& query, const String& fragment )
				: m_protocol(protocol), m_login(login), m_password(password), m_host(host),
				m_port(port), m_path(path), m_params(params), m_query(query),
				m_fragment(fragment)
	{
		m_baseURL = constructStandardBaseURL();
	}

	URLStruct( const String& protocol, const String& path, const String& fragment, const String& baseURL )
				: m_protocol(protocol), m_login(Atom(EMPTY)), m_password(Atom(EMPTY)), m_host(Atom(EMPTY)),
				m_port(0), m_path(path), m_params(Atom(EMPTY)), m_query(Atom(EMPTY)), m_fragment(fragment),
				m_baseURL(baseURL)
	{
	}

	String toString() const;

	bool query( const Value&, void * res );

	uft::WeakReferencePtr * getWeakReferencePtr() { return NULL; }

	void * operator new( size_t size, uft::Value& value ) { return ::operator new( size, uft::s_urlDescriptor, &value ); }
	void operator delete( void *ptr, uft::Value& value ) { assertLogic(false); }
	void * operator new( size_t size, uft::Value& value, const char * file, int line ) { return ::operator new( size, uft::s_urlDescriptor, &value, file, line ); }
	void operator delete( void *ptr, uft::Value& value, const char * file, int line ) { assertLogic(false); }

private:

	String constructStandardBaseURL() const;

	// all fields except for m_query are not URL-encoded
	String	m_protocol;
	String	m_login;
	String	m_password;
	String	m_host;
	uint32	m_port;
	String	m_path;
	String	m_params;
	String	m_query; // kept URL-encoded (has to be because of '&' used as separator)
	String	m_fragment;
	String	m_baseURL; // kept URL-encoded
};


/**
<pre>
	RFC1738/1808/2396
		HTTP-like URL: 
			proto://login:passw@host:port/path;params?query#fragment
		mailto URL:
			mailto:login@host
		file URL:
			file:///path
			file://hostname/path
			file:/path
		relative URL:
			#fragment
			path;params?query#fragment
			/path;params?query#fragment
	RFC2397 data URL:
		data:mime-type[;base64],enc-data
	Also, "bad" characters are encoded using %XX or + for space

	Examples of URLs:
		http://me:secret@foo.adobe.com:8080/mumba/yumba?res=aa/qq?nnn#header?#(a/b)
			Parsed:
				port = http
				login = me
				password = secret
				host = foo.adobe.com
				port = 8080
				path = /mumba/yumba
				query = res=aa/qq?nnn
				fragment = header?#(a/b)
		file:///C:/Program%20Files
		file://localhost/C:/Program%20Files
		file://hera/apps
		file:/C:/Program%20Files
		file:///usr/X11R6
</pre>
*/
class URL : public Value
{
public:

	URL() {} // null
	URL( const String& url ); // must pass fully encoded string

	bool isSelfReferring() const; // if only fragment is there, URL is referring to the same document

	bool isAbsolute() const;

	// for all protools
	String getProtocol() const; // protocol - lowercased atom
	String getBaseURL() const; // URL without #fragment part - atomized
	String getFragment() const;

	// for HTTP-like protocols
	String getDomain() const; // login:passw@host:port
	String getLogin() const;
	String getPassword() const;
	String getHost() const;
	uint32 getPort() const;
	String getPath() const;
	String getParams() const;
	String getQuery() const;

	// for data protocol
	String getMimeType() const;
	String getEncoding() const;
	StringBuffer getEncodedData() const;

	bool operator==( const URL& other ) const { return isNull() == other.isNull() && toString() == other.toString(); }
	bool operator!=( const URL& other ) const { return isNull() != other.isNull() || toString() != other.toString(); }

	// resolving relative URLs
	URL resolve( const URL& relURL ) const;
	URL resolve( const String& relURL ) const { return resolve( URL(relURL) ); }

	// URL encoding
	static String encode( const StringBuffer& data, bool aggressive = false );
	static String decode( const StringBuffer& data );

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_urlDescriptor); }

	static const URL& nullValue() { return checked_cast<uft::URL>(uft::Value::nullValue()); }

	// return final position in the stream; if could not parse, return initial position and don't assign out param
	static const char * parse( const char * s, uft::Value * outURL );

	static ValueParser * getParser();

private:

	URLStruct* operator->() const
	{
		StructBlock * block = (StructBlock*)getBlock();
		assertLogic( block->getStructDescriptor() == uft::s_urlDescriptor );
		return (URLStruct*)block->getStructPtr();
	}

	void initRelativeURL( const String& url );
	void initFileURL( const String& url );
	void initOtherURL( const String& protocol, const String& url );
	void initAbsoluteURL( const String& protocol, const String& url );

};



}

#endif //_UFT_URL_H

