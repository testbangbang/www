/*
 *
 *                       ADOBE CONFIDENTIAL
 *                     _ _ _ _ _ _ _ _ _ _ _ _
 *
 * Copyright 2004-2006, Adobe Systems Incorporated
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
 */

#ifndef _UFT_BYTESWAP_H
#define _UFT_BYTESWAP_H

#include "uft_value.h"

namespace uft
{

inline bool isLittleEndian()
{
	static char probe[] = "\1";
	return *(uft::uint16*)probe == 1;
}

// byte swap utilities
inline uft::uint16 Swap( uft::uint16 in )
{
	if( isLittleEndian() )
		return in;
	else
		return ( in >> 8 ) | ( ( in & 0xFF ) << 8 );
}

inline uft::uint32 Swap( uft::uint32 in )
{
	if( isLittleEndian() )
		return in;
	else
		return ( Swap( (uft::uint16)( in >> 16 ) ) ) | ( (uft::uint32)Swap( (uft::uint16)( in & 0xFFFF ) ) << 16 );
}

}

#endif // _UFT_BYTESWAP_H
