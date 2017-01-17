package com.neverland.engbook.level2;

import java.util.ArrayList;

public class AlXMLTag {
	public int			tag;
	public boolean		closed;
	public boolean		ended;
	public boolean		special;
	public int			start_pos;

	public int			astart;
	public int			aname;
	private int			alen;
	private final char[]		aval = new char[AlAXML.LEVEL2_XML_PARAMETER_VALUE_LEN + 1];

	private int			attr_len = 0;
	private final ArrayList<AlXMLTagParam>		attr = new ArrayList<>();

	public final void resetTag(int start_position) {
		clearTag();
		closed = false;
		ended = false;
        special = false;
		start_pos = start_position;	
		resetAttr();
	}
	
	public final void add2Tag(char val){
		tag = (tag * 31) + Character.toLowerCase(val);
	}
	
	public final void clearTag(){
		tag = 0x00;
	}

	public final StringBuilder getATTRValue(int param){
		for (int i = 0; i < attr_len; i++) {
			if (attr.get(i).name == param) {// && attr.get(i).value.length() > 0) {
				return attr.get(i).value;//.toString();
			}
		}
		return null;
	}

	public final int getATTRStart(int param){
		for (int i = 0; i < attr_len; i++) {
			if (attr.get(i).name == param) {// && attr.get(i).value.length() > 0) {
				return attr.get(i).start;//.toString();
			}
		}
		return -1;
	}

	public final int getATTREnd(int param){
		for (int i = 0; i < attr_len; i++) {
			if (attr.get(i).name == param) {// && attr.get(i).value.length() > 0) {
				return attr.get(i).end;//.toString();
			}
		}
		return -1;
	}

	public final void resetAttr(){
		attr_len = 0;
		alen = 0;
		aname = 0x00;
	}
	
	public final void add2AttrName(char val){
		aname = (aname * 31) + Character.toLowerCase(val);
	}

	public final void add2AttrValue(char val){
		if (alen < AlAXML.LEVEL2_XML_PARAMETER_VALUE_LEN) {
			aval[alen++] = val;
		}
	}
	
	public final void add2AttrValue(String val){
		final int l = val.length();
		for (int i = 0; i < l; i++) {
			if (alen < AlAXML.LEVEL2_XML_PARAMETER_VALUE_LEN) {
				aval[alen++] = val.charAt(i);
			}
		}
	}
	
	public final void add2AttrValue(StringBuilder val){
		final int l = val.length();
		for (int i = 0; i < l; i++) {
			if (alen < AlAXML.LEVEL2_XML_PARAMETER_VALUE_LEN) {
				aval[alen++] = val.charAt(i);
			}
		}
	}
	
	public final void clearAttrName(){
		aname = 0x00;
		alen = 0x00;
	}
	
	public final void clearAttrVal(int start_pos){
		alen = 0x00;
		astart = start_pos;
	}

	public final void addAttribute(int end_pos){
		if (alen < 1)
			return;
		
		AlXMLTagParam a/* = null*/;
		if (attr_len < attr.size()) {
			a = attr.get(attr_len);
			a.name = aname;
			a.start = astart;
			a.end = end_pos;
			a.value.setLength(0);
			a.value.append(aval, 0, alen);
		} else {
			a = new AlXMLTagParam();
			a.name = aname;
			a.value.append(aval, 0, alen);
			a.start = astart;
			a.end = end_pos;
			attr.add(a);
		}
		attr_len++;		
	}
}

/*public class AlXMLTag {
	public int					tag;
	public boolean					closed;
	public boolean					ended;
	public int					start_pos;
	
	public int					aname;
	public StringBuilder						aval = new StringBuilder();

	public ArrayList<AlXMLTagParam>		attr = new ArrayList<AlXMLTagParam>(16);

	public final void resetTag(int start_position) {
		clearTag();
		closed = false;
		ended = false;
		start_pos = start_position;	
		resetAttr();
	}
	
	public final void add2Tag(char val){
		tag = (tag * 31) + Character.toLowerCase(val);
	}
	
	public final void clearTag(){
		tag = 0x00;
	};

	public final String getATTRValue(int param){
		for (int i = 0; i < attr.size(); i++) {
			if (attr.get(i).name == param) {// && attr.get(i).value.length() > 0) {
				return attr.get(i).value;//.toString();
			}
		}
		return null;
	}	

	public final void resetAttr(){
		attr.clear();
		aval.setLength(0);
		aname = 0x00;
	};
	
	public final void add2AttrName(char val){
		aname = (aname * 31) + Character.toLowerCase(val);
	};

	public final void add2AttrValue(char val){
		if (aval.length() < AlAXML.LEVEL2_XML_PARAMETER_VALUE_LEN)
			aval.append(val);
	};
	
	public final void add2AttrValue(String val){
		aval.append(val);
	};
	
	public final void add2AttrValue(StringBuilder val){
		aval.append(val);
	};
	
	public final void clearAttrName(){
		aname = 0x00;
		aval.setLength(0);
	};
	
	public final void clearAttrVal(){	
		aval.setLength(0);
	};

	public final void addAttribute(){
		AlXMLTagParam a = new AlXMLTagParam();
		a.name = aname;
		if (aval.length() > 0)
			a.value = aval.toString();
		attr.add(a);
	};
}
*/