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
 * Author: Peter Sorotokin, 21-APR-2004
 * Migrated SVG/AXE assert here
 */

#ifndef _UFT_ASSERT_H
#define _UFT_ASSERT_H

/* Note: this file should be both C and C++ compatible */

#ifdef __cplusplus
// only defined for C++ code since repeated typedefs could give error in C code (this violates ANSI C)
#define assertCompileTime(cond) typedef char XPAssertCompileTimeDummyType[(cond)?1:-1]
#endif

#if !defined(_DEBUG) && !defined(UFT_ASSERT_ALWAYS)

/* not a debug build, and not forcing XPAsserts on */

#ifndef UFT_ASSERT_MACROS
#define UFT_ASSERT_MACROS /* as nothing */
#define assertLogic(cond)
#define assertData(cond)
#define assertTODO()
#define assertLogicMsg(cond,msg)
#define assertDataMsg(cond,msg)
#define assertTODOMsg(msg)
#define assertLogicOnce(cond)
#endif

#else  /* !defined(_DEBUG) && !defined(XPASSERT_ALWAYS) */

/* asserts are active */

/*
 * There are three kinds of asserts:  logic and data and todo.  Logic asserts 
 * are those are that are serious internal consistency errors; data asserts 
 * merely indicate bad input data. "todo" asserts indicate that something is
 * not yet completed in the code. Asserts are raised in the following way:
 *
 *     assertTYPE(condition);
 *     assertTODO();
 *     assertTYPEMsg(condition, message);
 *     assertTODOMsg(message);
 *
 * where TYPE is Logic or Data.
 */


#define UFT_ASSERT_UNKNOWN_TYPE -1
#define UFT_ASSERT_TODO_TYPE   0
#define UFT_ASSERT_LOGIC_TYPE   1
#define UFT_ASSERT_DATA_TYPE    2
#define UFT_ASSERT_TYPES        3

#ifdef __cplusplus
// for C++ we can handle uft::Value as a message
namespace uft
{
class Value;
int assertProc( const char * condStr, const char * file, int line, int type, const uft::Value& msg );
int assertProc( const char * condStr, const char * file, int line, int type, const char * msg );
int getAssertLevel();
bool assertFailed();
}
#define uft_assertFailed uft::assertFailed
#define uft_assertProc uft::assertProc
#else
// C only
int uft_assertFailed();
int uft_assertProc( const char * condStr, const char * file, int line, int type, const char * msg );
#endif

#ifndef UFT_ASSERT_MACROS
#define UFT_ASSERT_MACROS /* as nothing */
#define assertLogic(cond) \
	((cond) || \
	uft_assertProc( #cond, __FILE__, __LINE__, UFT_ASSERT_LOGIC_TYPE, 0 ))
#define assertData(cond) \
	((cond) || \
	 uft_assertProc( #cond, __FILE__, __LINE__, UFT_ASSERT_DATA_TYPE, 0 ))
#define assertTODO() \
	(uft_assertProc( "TODO", __FILE__, __LINE__, UFT_ASSERT_TODO_TYPE, 0 ))
#define assertLogicMsg(cond,msg) \
	((cond) || \
	 uft_assertProc( #cond, __FILE__, __LINE__, UFT_ASSERT_LOGIC_TYPE, msg ))
#define assertDataMsg(cond,msg) \
	((cond) || \
	 uft_assertProc( #cond, __FILE__, __LINE__, UFT_ASSERT_DATA_TYPE, msg ))
#define assertLogicFirstTime(cond) \
	((cond) || uft_assertFailed() || \
	 uft_assertProc( #cond, __FILE__, __LINE__, UFT_ASSERT_LOGIC_TYPE, 0 ))
#define assertTODOMsg(msg) \
	(uft_assertProc( "TODO", __FILE__, __LINE__, UFT_ASSERT_TODO_TYPE, msg ))
#endif

#endif /* !defined(_DEBUG) && !defined(XPASSERT_ALWAYS) */

#endif // _UFT_ASSERT_H

