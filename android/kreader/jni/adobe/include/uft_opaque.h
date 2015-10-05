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
 * Author: Peter Sorotokin, 5-NOV-2004
 */
#ifndef _UFT_OPAQUE_H
#define _UFT_OPAQUE_H

#include "uft_ref.h"

namespace uft
{

class Opaque;

class OpaqueStruct
{
	friend class uft::Opaque;

public:

	OpaqueStruct( word opaque ) : m_opaque(opaque) {}

	// cloning Opaque value might be OK, but right now I don't think it should ever happen. If it does
	// it needs to be reviewed -- psorotok
	OpaqueStruct( const OpaqueStruct& s ) : m_opaque(s.m_opaque) { assertLogicMsg(false, "uft::Opaque cloned"); }

	uft::String toString() const { return String::toString((int64)m_opaque); }

	UFT_STRUCT_DECL

private:

	word m_opaque;
};

// an efficient and reliable way to store an opaque uft::word value (normally an opaque pointer)
// that uses either uft::Integer or uft::sref<uft::OpaqueStruct>
class Opaque : public Value
{
public:

	Opaque() {}
	Opaque( word value );

	uft::word getStoredValue() const;

	static bool isInstanceOf( const Value& v );
};

}

#endif // _UFT_OPAQUE_H

