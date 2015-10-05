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
 * Author: Peter Sorotokin, 26-MAY-2004
 */

#ifndef _UFT_PARSER_H
#define _UFT_PARSER_H

#include "uft_value.h"
#include "uft_container.h"
#include "uft_error_handler.h"

namespace uft
{

// All context information allowed for parsing must be encapsulated here. It must not affect
// what toString() returns for parsed value and holder of the parsed value (DOM most of the times)
// must reparse all attributes _string_ (not parsed) values (and call listeners).
class ParserContext
{
public:
	ParserContext(ErrorHandler * handler) : m_errorHandler(handler) {}
	virtual String resolveNSPrefix( const String& prefix ) = 0;
	virtual bool prefixBindingsChanged() = 0;
	virtual uft::uint32 getNodeType() = 0;
	virtual ErrorHandler * getErrorHandler() { return m_errorHandler; }

	static ParserContext * getMSchemaContext();
private:
	ErrorHandler * m_errorHandler;
};

class ValueParser
{
public:

	virtual Value parse( ParserContext * context, const Value& rawValue ) const = 0;
	virtual const char * getValidStartChars() const = 0;

	static const ValueParser * const s_atomParser;
	static const ValueParser * const s_lowercasingAtomParser;
	static const ValueParser * const s_atomListParser; // parses space-separated atoms, returns tuple
	static const ValueParser * const s_intParser;
	static const ValueParser * const s_contextParser;
	static const ValueParser * const s_floatParser;
	static const ValueParser * const s_longParser;
	static const ValueParser * const s_doubleParser;
	static const ValueParser * const s_stringParser;
	static const ValueParser * const s_qnameParser;
	static const ValueParser * const s_nullParser;
	static const ValueParser * const s_nameParser;
	static const ValueParser * const s_passthruParser;
};

/**
 *  Parses atoms from a limited set, returns null for everything else.
 */
class EnumParser : public uft::ValueParser
{
public:

	EnumParser( const uft::Set& atomSet, bool lowercase );

	virtual uft::Value parse( uft::ParserContext * context, const uft::Value& rawValue ) const;
	virtual const char * getValidStartChars() const;

private:

	bool			m_lowercase;
	uft::Set		m_atomSet;
	uft::String		m_validStart;
};

template<class ParseClass> class SimpleValueParser : public ValueParser
{
public:
	SimpleValueParser( const char * validStartChars )
		: m_validStartChars(validStartChars)
	{
	}

	virtual Value parse( ParserContext *, const Value& rawValue ) const
	{
		Value res;
		uft::String str = rawValue.toString();
		ParseClass::parse( str.utf8(), &res );
		return res;
	}

	virtual const char * getValidStartChars() const
	{
		return m_validStartChars;
	}
private:

	const char * m_validStartChars;
};

class ChainingParser : public ValueParser
{
public:

	ChainingParser( const ValueParser * * parsers, int nParsers );
	ChainingParser( const ValueParser * parser1, const ValueParser * parser2 );
	ChainingParser( const ValueParser * parser1, const ValueParser * parser2, const ValueParser * parser3 );
 
	virtual ~ChainingParser();

	virtual Value parse( ParserContext *, const Value& rawValue ) const;
	virtual const char * getValidStartChars() const;

private:

	void init( const ValueParser * * parsers, int nParsers );

private:

	const ValueParser * * m_map[256];
	const ValueParser * * m_parserSeq;
	char * m_validStartChars;
};

}

#endif //_UFT_PARSER_H

