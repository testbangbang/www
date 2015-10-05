/*
 *
 *                       ADOBE CONFIDENTIAL
 *                     _ _ _ _ _ _ _ _ _ _ _ _
 *
 * Copyright 2007, Adobe Systems Incorporated
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

#ifndef UFT_ERROR_HANDLER_H_
#define UFT_ERROR_HANDLER_H_

#include "uft_value.h"
#include "uft_string.h"

/**
 *  An abstract class which provides methods for reporting errors.
 */
namespace uft
{

/**
 * Implementation of a simple error list using uft constructs. Use this if use of uft
 * is OK and the error list does not need to include subsidiary error lists.
 */
class ErrorHandler
{
public:
	virtual ~ErrorHandler() {}

	/* Called to report a persistent error. This type of error typically gets added to
	   an error list. */
	virtual void reportStateError( const uft::String& errorString ) = 0;

	/* Called to report a transient error. This type of error typically get logged and
	   possibly reported to the user, but is not saved on an error list. */
	virtual void reportProcessError( const uft::String& errorString ) = 0;

	/* Get the URL to be used in the errorString */
	virtual const uft::String getURLString() = 0;

	/* Create a new error handler identical to the old one, but with a different URL */
	virtual ErrorHandler * changeURL( const uft::String& urlString ) = 0;
};

}

#endif /* UFT_ERROR_HANDLER_H_ */
