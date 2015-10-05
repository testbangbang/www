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
 * Author: Peter Sorotokin, 27-JAN-2005
 */

#ifndef _UFT_QNAME_LIST_H
#define _UFT_QNAME_LIST_H

#ifndef _UFT_QNAME_H
#include "uft_qname.h"
#endif

namespace uft
{

class XMLName : public QName
{
public:
	XMLName( const String& localName ) : QName( Atom(xml_namespace_uri), Atom(xml), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(xml_namespace_uri)); }
};

class CSSPseudoName : public QName
{
public:
	CSSPseudoName( const String& localName ) : QName( Atom(css_pseudo_namespace_uri), Atom(css), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(css_pseudo_namespace_uri)); }
};

class XDAName : public QName
{
public:
	XDAName( const String& localName ) : QName( Atom(xda_namespace_uri), Atom(xda), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(xda_namespace_uri)); }
};

class RVTName : public QName
{
public:
	RVTName( const String& localName ) : QName( Atom(rvt_namespace_uri), Atom(rvt), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(rvt_namespace_uri)); }
};

class ATFName : public QName
{
public:
	ATFName( const String& localName ) : QName( Atom(atf_namespace_uri), Atom(atf), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(atf_namespace_uri)); }
};

class ADEName : public QName
{
public:
	ADEName( const String& localName ) : QName( Atom(ade_namespace_uri), Atom(ade), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(ade_namespace_uri)); }
};

class XLinkName : public QName
{
public:
	XLinkName( const String& localName ) : QName( Atom(xlink_namespace_uri), Atom(xlink), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(xlink_namespace_uri)); }
};

class SVGName : public QName
{
public:
	SVGName( const String& localName ) : QName( Atom(svg_namespace_uri), Atom(svg), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(svg_namespace_uri)); }
};

class XHTMLName : public QName
{
public:
	XHTMLName( const String& localName ) : QName( Atom(xhtml_namespace_uri), Atom(xhtml), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(xhtml_namespace_uri)); }
};

class FOName : public QName
{
public:
	FOName( const String& localName ) : QName( Atom(fo_namespace_uri), Atom(fo), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(fo_namespace_uri)); }
};

class XBLName : public QName
{
public:
	XBLName( const String& localName ) : QName( Atom(xbl_namespace_uri), Atom(xbl), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(xbl_namespace_uri)); }
};

class DPDFName : public QName
{
public:
	DPDFName( const String& localName ) : QName( Atom(dpdf_namespace_uri), Atom(dpdf), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(dpdf_namespace_uri)); }
};

class EVName : public QName
{
public:
	EVName( const String& localName ) : QName( Atom(xml_events_namespace_uri), Atom(ev), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(xml_events_namespace_uri)); }
};

class SMILName : public QName
{
public:
	SMILName( const String& localName ) : QName( Atom(smil_namespace_uri), Atom(smil), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(smil_namespace_uri)); }
};

class DCName : public QName
{
public:
	DCName( const String& localName ) : QName( Atom(dc_namespace_uri), Atom(dc), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(dc_namespace_uri)); }
};

class DCTERMSName : public QName
{
public:
	DCTERMSName( const String& localName ) : QName( Atom(dcterms_namespace_uri), Atom(dcterms), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(dcterms_namespace_uri)); }
};

class XSIName : public QName
{
public:
	XSIName( const String& localName ) : QName( Atom(xsi_namespace_uri), Atom(xsi), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(xsi_namespace_uri)); }
};

class ADOBEMETAName : public QName
{
public:
	ADOBEMETAName( const String& localName ) : QName( Atom(adobemeta_namespace_uri), Atom(adobemeta), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(adobemeta_namespace_uri)); }
};

class ENCName : public QName
{
public:
	ENCName( const String& localName ) : QName( Atom(enc_namespace_uri), Atom(enc), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(enc_namespace_uri)); }
};

class DSName : public QName
{
public:
	DSName( const String& localName ) : QName( Atom(ds_namespace_uri), Atom(ds), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(ds_namespace_uri)); }
};

class ADOBEAPSName : public QName
{
public:
	ADOBEAPSName( const String& localName ) : QName( Atom(adobeaps_namespace_uri), Atom(adobeaps), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(adobeaps_namespace_uri)); }
};

class ACSName : public QName
{
public:
	ACSName( const String& localName ) : QName( Atom(acs_namespace_uri), Atom(acs), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(acs_namespace_uri)); }
};

class NCXName : public QName
{
public:
	NCXName( const String& localName ) : QName( Atom(ncx_namespace_uri), Atom(ncx), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(ncx_namespace_uri)); }
};

class DTBOOKName : public QName
{
public:
	DTBOOKName( const String& localName ) : QName( Atom(dtbook_namespace_uri), Atom(dtbook), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(dtbook_namespace_uri)); }
};

class OCFName : public QName
{
public:
	OCFName( const String& localName ) : QName( Atom(ocf_namespace_uri), Atom(ocf), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(ocf_namespace_uri)); }
};

class OPFName : public QName
{
public:
	OPFName( const String& localName ) : QName( Atom(opf_namespace_uri), Atom(opf), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(opf_namespace_uri)); }
};

class OPSName : public QName
{
public:
	OPSName( const String& localName ) : QName( Atom(ops_namespace_uri), Atom(ops), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(ops_namespace_uri)); }
};

class ANYName : public QName
{
public:
	ANYName( const String& localName ) : QName( Atom(any_namespace_uri), Atom(any), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(any_namespace_uri)); }
};

class ADEPTName : public QName
{
public:
	ADEPTName( const String& localName ) : QName( Atom(adept_namespace_uri), Atom(adept), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(adept_namespace_uri)); }
};

class DPName : public QName
{
public:
	DPName( const String& localName ) : QName( Atom(dp_namespace_uri), Atom(dp), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(dp_namespace_uri)); }
};

class DEName : public QName
{
public:
	DEName( const String& localName ) : QName( Atom(de_namespace_uri), Atom(de), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(de_namespace_uri)); }
};

class OOName : public QName
{
public:
	OOName( const String& localName ) : QName( Atom(oo_namespace_uri), Atom(oo), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(oo_namespace_uri)); }
};

class OOStyleName : public QName
{
public:
	OOStyleName( const String& localName ) : QName( Atom(ooStyle_namespace_uri), Atom(ooStyle), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(ooStyle_namespace_uri)); }
};

class OOTextName : public QName
{
public:
	OOTextName( const String& localName ) : QName( Atom(ooText_namespace_uri), Atom(ooText), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(ooText_namespace_uri)); }
};

class OOTableName : public QName
{
public:
	OOTableName( const String& localName ) : QName( Atom(ooTable_namespace_uri), Atom(ooTable), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(ooTable_namespace_uri)); }
};

class OODrawName : public QName
{
public:
	OODrawName( const String& localName ) : QName( Atom(ooDraw_namespace_uri), Atom(ooDraw), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(ooDraw_namespace_uri)); }
};

class OOFOName : public QName
{
public:
	OOFOName( const String& localName ) : QName( Atom(ooFo_namespace_uri), Atom(ooFo), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(ooFo_namespace_uri)); }
};

class OOSVGName : public QName
{
public:
	OOSVGName( const String& localName ) : QName( Atom(ooSvg_namespace_uri), Atom(ooSvg), localName ) {}

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_qnameDescriptor) && ((const QName&)v).getNamespaceURI().ptrEq(Atom(ooSvg_namespace_uri)); }
};

#define UFT_CANONICAL_NS_TO_PREFIX_MAP \
	Atom(xmlns_namespace_uri), Atom(xmlns), \
	Atom(xml_namespace_uri), Atom(xml), \
	Atom(css_pseudo_namespace_uri), Atom(css), \
	Atom(xda_namespace_uri), Atom(xda), \
	Atom(rvt_namespace_uri), Atom(rvt), \
	Atom(atf_namespace_uri), Atom(atf), \
	Atom(ade_namespace_uri), Atom(ade), \
	Atom(xlink_namespace_uri), Atom(xlink), \
	Atom(svg_namespace_uri), Atom(svg), \
	Atom(xhtml_namespace_uri), Atom(xhtml), \
	Atom(fo_namespace_uri), Atom(fo), \
	Atom(xbl_namespace_uri), Atom(xbl), \
	Atom(dpdf_namespace_uri), Atom(dpdf), \
	Atom(xml_events_namespace_uri), Atom(ev), \
	Atom(smil_namespace_uri), Atom(smil), \
	Atom(dc_namespace_uri), Atom(dc), \
	Atom(dcterms_namespace_uri), Atom(dcterms), \
	Atom(xsi_namespace_uri), Atom(xsi), \
	Atom(adobemeta_namespace_uri), Atom(adobemeta), \
	Atom(enc_namespace_uri), Atom(enc), \
	Atom(ds_namespace_uri), Atom(ds), \
	Atom(adobeaps_namespace_uri), Atom(adobeaps), \
	Atom(acs_namespace_uri), Atom(acs), \
	Atom(ncx_namespace_uri), Atom(ncx), \
	Atom(dtbook_namespace_uri), Atom(dtbook), \
	Atom(ocf_namespace_uri), Atom(ocf), \
	Atom(opf_namespace_uri), Atom(opf), \
	Atom(ops_namespace_uri), Atom(ops), \
	Atom(any_namespace_uri), Atom(any), \
	Atom(adept_namespace_uri), Atom(adept), \
	Atom(dp_namespace_uri), Atom(dp), \
	Atom(de_namespace_uri), Atom(de), \
	Atom(oo_namespace_uri), Atom(oo), \
	Atom(ooStyle_namespace_uri), Atom(ooStyle), \
	Atom(ooText_namespace_uri), Atom(ooText), \
	Atom(ooTable_namespace_uri), Atom(ooTable), \
	Atom(ooDraw_namespace_uri), Atom(ooDraw), \
	Atom(ooFo_namespace_uri), Atom(ooFo), \
	Atom(ooSvg_namespace_uri), Atom(ooSvg)

#define UFT_CANONICAL_PREFIX_TO_NS_MAP \
	Atom(xmlns), Atom(xmlns_namespace_uri), \
	Atom(xml), Atom(xml_namespace_uri), \
	Atom(css), Atom(css_pseudo_namespace_uri), \
	Atom(xda), Atom(xda_namespace_uri), \
	Atom(rvt), Atom(rvt_namespace_uri), \
	Atom(atf), Atom(atf_namespace_uri), \
	Atom(ade), Atom(ade_namespace_uri), \
	Atom(xlink), Atom(xlink_namespace_uri), \
	Atom(svg), Atom(svg_namespace_uri), \
	Atom(xhtml), Atom(xhtml_namespace_uri), \
	Atom(fo), Atom(fo_namespace_uri), \
	Atom(xbl), Atom(xbl_namespace_uri), \
	Atom(dpdf), Atom(dpdf_namespace_uri), \
	Atom(ev), Atom(xml_events_namespace_uri), \
	Atom(smil), Atom(smil_namespace_uri), \
	Atom(dc), Atom(dc_namespace_uri), \
	Atom(dcterms), Atom(dcterms_namespace_uri), \
	Atom(xsi), Atom(xsi_namespace_uri), \
	Atom(adobemeta), Atom(adobemeta_namespace_uri), \
	Atom(enc), Atom(enc_namespace_uri), \
	Atom(ds), Atom(ds_namespace_uri), \
	Atom(adobeaps), Atom(adobeaps_namespace_uri), \
	Atom(acs), Atom(acs_namespace_uri), \
	Atom(ncx), Atom(ncx_namespace_uri), \
	Atom(dtbook), Atom(dtbook_namespace_uri), \
	Atom(ocf), Atom(ocf_namespace_uri), \
	Atom(opf), Atom(opf_namespace_uri), \
	Atom(ops), Atom(ops_namespace_uri), \
	Atom(any), Atom(any_namespace_uri), \
	Atom(adept), Atom(adept_namespace_uri), \
	Atom(dp), Atom(dp_namespace_uri), \
	Atom(de), Atom(de_namespace_uri), \
	Atom(oo), Atom(oo_namespace_uri), \
	Atom(ooStyle), Atom(ooStyle_namespace_uri), \
	Atom(ooText), Atom(ooText_namespace_uri), \
	Atom(ooTable), Atom(ooTable_namespace_uri), \
	Atom(ooDraw), Atom(ooDraw_namespace_uri), \
	Atom(ooFo), Atom(ooFo_namespace_uri), \
	Atom(ooSvg), Atom(ooSvg_namespace_uri)
}

#endif //_UFT_QNAME_LIST_H

