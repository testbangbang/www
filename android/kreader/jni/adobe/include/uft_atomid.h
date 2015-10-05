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

/* Atom ID definitions */

/* This file is used in both C and C++ */

#ifndef _UFT_ATOMID_H
#define _UFT_ATOMID_H

#define UFT_STANDALONE

// Compiling UFT as DLL or standalone assumes compile-time atoms
#if defined(UFT_DLL) || defined(UFT_STANDALONE)
#ifndef UFT_ATOM_COMPILE_TIME
#define UFT_ATOM_COMPILE_TIME
#endif
#endif

#ifdef UFT_ATOM_COMPILE_TIME

/* atom IDs are constants and resolved at compile time */

#ifdef __cplusplus
#define ATOM_ID_PASTER(id)		ID_ ## id
#define AtomID(id)				uft::ATOM_ID_PASTER(id)
#define Atom(identifier)		uft::String::predefinedAtom(AtomID(identifier))
#else
#define ATOM_ID_PASTER(id)		UFT_ID_ ## id
#define AtomID(id)				ATOM_ID_PASTER(id)
#define Atom(identifier)		uft_predefinedAtomById(AtomID(identifier))
#endif

/* actual constants are defined below */

#ifdef __cplusplus
namespace uft {
enum AtomIndex
#else
enum uft_AtomIndex
#endif
{
	/* create an enum of atom ID's and get NUMBER_OF_ATOMS at the same time */
	ATOM_ID_PASTER(DUMMY),
#ifdef ATOM_ENTRY
#undef ATOM_ENTRY
#endif
#define ATOM_ENTRY(identifier, str)		ATOM_ID_PASTER(identifier),
#include "uft_atomlist.h"
#undef ATOM_ENTRY
	NUMBER_OF_ATOMS
};

#ifdef __cplusplus
}
#endif

#else /* UFT_ATOM_COMPILE_TIME not defined */

#include "uft_atomdefs.h"

#define ATOM_STR_PASTER(id)		UFT_ATOM_STR_ ## id

/* atom IDs are not constants and looked up at run time */
#ifdef __cplusplus
#define AtomID(id)				uft::String::atomID(ATOM_STR_PASTER(id))
#define Atom(id)				uft::String::atom(ATOM_STR_PASTER(id))
#else
#define AtomID(id)				uft_atomID_fromUFT8(ATOM_STR_PASTER(id))
#define Atom(id)				uft_atom_fromUFT8(ATOM_STR_PASTER(id))
#endif

#endif /* UFT_ATOM_COMPILE_TIME */


#endif /* _UFT_ATOMID_H */
