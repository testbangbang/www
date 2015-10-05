/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 * Copyright 2011 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/

#ifndef _DP_SEC_H
#define _DP_SEC_H

#include "dp_core.h"

namespace dpsec
{

enum SecurityHandlerType
{
	kStandard,
	kEDC
};

/**
 *	This interface should be extended and used for passing data to and fro from the T3 security handler classes 
 */
class SecurityHandlerData
{
public:
	virtual SecurityHandlerType getHandlerType() = 0;
};

/**
 *	Used for data passing by the EDCSecurityHandler
 */
class EDCSecurityHandlerData : public SecurityHandlerData
{
public:
	virtual SecurityHandlerType getHandlerType()
	{
		return kEDC;
	}

	dp::String getBaseURL()                         { return m_baseURL; }
	void  setBaseURL(const dp::String& src)         { m_baseURL = src; }

	dp::String getUsernamePrompt()                  { return m_usernamePrompt; }
	void  setUsernamePrompt(const dp::String& src)  { m_usernamePrompt = src; }

	dp::String getPasswordPrompt()                  { return m_passwordPrompt; }
	void  setPasswordPrompt(const dp::String& src)  { m_passwordPrompt = src; }

	dp::String getWelcomeText()                     { return m_welcomeText; }
	void  setWelcomeText(const dp::String& src )    { m_welcomeText = src; }

	dp::String getPrivacyURL()                      { return m_privacyURL; }
	void  setPrivacyURL(const dp::String& src )     { m_privacyURL = src; }

	dp::String getUsername()                        { return m_username; }
	void  setUsername(const dp::String& src)        { m_username = src; }

	dp::String getPassword()                        { return m_password; }
	void  setPassword(const dp::String& src )       { m_password = src; }

	bool  getAnonymous()                           { return m_isAnonymous; }
	void  setAnonymous(bool isAnonymous)           { m_isAnonymous = isAnonymous; }

	bool  getAnonNotificationShown()               { return m_anonNotificationShown; }
	void  setAnonNotificationShown(bool shown)     { m_anonNotificationShown = shown; }

	bool  getDialogWasCancelled()                  { return m_cancelled; }
	void  setDialogWasCancelled(bool cancelled)    { m_cancelled = cancelled; }

	bool getIsAuthRetry()                          { return m_isAuthRetry; }
	void setIsAuthRetry(bool isRetry)              { m_isAuthRetry = isRetry; }

private:
	/* in/out with respect to AREDCSecurityHandler */
	/*[in]*/  dp::String   m_baseURL;
	/*[in]*/  dp::String   m_usernamePrompt;
	/*[in]*/  dp::String   m_passwordPrompt;
	/*[in]*/  dp::String   m_welcomeText;
	/*[in]*/  dp::String   m_privacyURL;
	/*[in]*/  bool         m_isAnonymous;
	/*[in]*/  bool         m_isAuthRetry;
	/*[out]*/ dp::String   m_username;
	/*[out]*/ dp::String   m_password;
	/*[out]*/ bool         m_anonNotificationShown;
	/*[out]*/ bool         m_cancelled;
};

/**
 *	TODO_READER_MOBIE<gaurav>: This should be used for data passing by the StandardSecurityHandler. Right now the conventional RMSDK APIs 
 *	'requestDocumentPassword' and 'setDocumentPassword' are used for getting the password from the user and passing it to the standard security
 *	handler in T3. We should change this to use the more generic APIs 'requestCredentials' and 'setCredentials' added to RMSDK. For more info, 
 *	see how this workflow has been implemented for the Rights Managed documents (EDCSecurityHandler).
 */
class StandardSecurityHandlerData : public SecurityHandlerData
{
public:
	virtual SecurityHandlerType getHandlerType()
	{
		return kStandard;
	}
	dp::String getPassword();
	void setPassword(dp::String password);
};


} //namespace dpsec

#endif //_DP_SEC_H
