package com.neverland.engbook.level2;

import com.neverland.engbook.util.AlOneXMLAttrClass;

import java.util.ArrayList;

public class AlXMLTag {
	public int			tag;
	public boolean		closed;
	public boolean		ended;
	public boolean		special;
	public int			start_pos;

	public int			astart;
	public int			aname;
	private int					alen;
	private final char[]		aval = new char[AlAXML.LEVEL2_XML_PARAMETER_VALUE_LEN + 1];

	private int			attr0_count = 0;
	private final ArrayList<AlXMLTagParam>		attr0 = new ArrayList<>();

	public AlOneXMLAttrClass		cls = new AlOneXMLAttrClass();

	public AlXMLTag() {
		attr0_count = 0x00;
		attr0.ensureCapacity(64);
	}

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
		cls.clear();
	}

	public final StringBuilder getATTRValue(int param){
		for (int i = 0; i < attr0_count; i++) {
			if (attr0.get(i).name == param) {// && attr.get(i).value.length() > 0) {
				return attr0.get(i).value;//.toString();
			}
		}
		return null;
	}

	public final int getATTRStart(int param){
		for (int i = 0; i < attr0_count; i++) {
			if (attr0.get(i).name == param) {// && attr.get(i).value.length() > 0) {
				return attr0.get(i).start;//.toString();
			}
		}
		return -1;
	}

	public final int getATTREnd(int param){
		for (int i = 0; i < attr0_count; i++) {
			if (attr0.get(i).name == param) {// && attr.get(i).value.length() > 0) {
				return attr0.get(i).end;//.toString();
			}
		}
		return -1;
	}

	public final void resetAttr(){
		attr0_count = 0;
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
		if (attr0_count < attr0.size()) {
			a = attr0.get(attr0_count);
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
			attr0.add(a);
		}
		attr0_count++;

		if (aname == AlFormatTag.TAG_CLASS && cls.count == 0) {
			cls.start();
			aval[alen] = 0x00;
			for (int i = 0;; i++) {
				switch (aval[i]) {
					case 0x00:
						cls.end();
						return;
					case 0x20:
						cls.end();
						cls.start();
						break;
					default:
						cls.add(aval[i]);
						break;
				}
			}
		}
	}
}
