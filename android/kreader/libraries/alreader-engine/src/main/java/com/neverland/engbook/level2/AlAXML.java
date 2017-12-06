package com.neverland.engbook.level2;

import com.neverland.engbook.allstyles.AlCSSStyles;
import com.neverland.engbook.allstyles.AlSetCSS;
import com.neverland.engbook.forpublic.AlIntHolder;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlRandomAccessFile;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.unicode.CP932;
import com.neverland.engbook.unicode.CP936;
import com.neverland.engbook.unicode.CP949;
import com.neverland.engbook.unicode.CP950;
import com.neverland.engbook.util.AlOneTable;
import com.neverland.engbook.util.AlOneTableCell;
import com.neverland.engbook.util.AlOneTableRow;
import com.neverland.engbook.util.AlParProperty;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.InternalFunc;

import java.util.HashMap;

public abstract class AlAXML extends AlFormat {

	protected int active_file = UNKNOWN_FILE_SOURCE_NUM;
	protected int active_type = 0x00;

	protected static final int UNKNOWN_FILE_SOURCE_NUM = 0x0fffff;

	protected static final int LEVEL2_XML_PARAMETER_VALUE_LEN =	4096;

	protected static final int STATE_XML_TEXT =  				0x0000;
	private static final int STATE_XML_STAG =  				0x0001;
	private static final int STATE_XML_TAG =  					0x0002;
	private static final int STATE_XML_TAG_ERROR =  			0x0003;
	private static final int STATE_XML_ETAG =  				0x0004;
	private static final int STATE_XML_ETAG2 =  				0x0005;
	private static final int STATE_XML_TEST =  				0x0006;
	private static final int STATE_XML_TEST_COMMENT =  		0x0007;
	private static final int STATE_XML_COMMENT =  				0x0008;
	private static final int STATE_XML_ECOMMENT1 =  			0x0009;
	private static final int STATE_XML_ECOMMENT2 =  			0x000a;
	private static final int STATE_XML_TEST_CDATA =  			0x000b;
	private static final int STATE_XML_CDATAWAIT =  				0x000c;
	private static final int STATE_XML_CDATAPROCESS =  				0x000d;
	private static final int STATE_XML_ECDATA1 =  				0x000e;
	private static final int STATE_XML_ECDATA2 =  				0x000f;
	private static final int STATE_XML_ATTRIBUTE_NAME =  		0x0010;
	private static final int STATE_XML_ATTRIBUTE_ENAME =  		0x0011;
	static final int STATE_XML_SKIP =  				0x0012;
		
	private static final int STATE_XML_ATTRIBUTE_VALUE1 =  	0x0100;
	private static final int STATE_XML_ATTRIBUTE_VALUE2 =  	0x0200;
	private static final int STATE_XML_ATTRIBUTE_VALUE3 =  	0x0300;
		
	private static final int STATE_XML_ENTITY_ADDSTART = 		0x1000;
	private static final int STATE_XML_ENTITY_ADDDEF = 	 	0x2000;
	private static final int STATE_XML_ENTITY_ADDNUM0 = 	 	0x3000;
	private static final int STATE_XML_ENTITY_ADDNUM1 = 	 	0x4000;
	private static final int STATE_XML_ENTITY_ADDNUMHEX  =		0x5000;

	final AlXMLTag			tag = new AlXMLTag();
	private final StringBuilder		entity = new StringBuilder();
	boolean				xml_mode;

	AlCSSStyles			cssStyles = null;


	boolean				dinamicSize = false;
	int					stop_posUsed;

	boolean				noUseCover = false;

	public AlAXML() {
		stack_styles0[0] = 0;
		parStyleCollection = 0;
	}

	boolean isNeedAttribute(int atr) {
		//noinspection RedundantIfStatement
		switch (atr) {
			case AlFormatTag.TAG_ID:
			case AlFormatTag.TAG_ROWSPAN:
			case AlFormatTag.TAG_COLSPAN:
			case AlFormatTag.TAG_HREF:
			case AlFormatTag.TAG_IDREF:
			case AlFormatTag.TAG_NAME:
			case AlFormatTag.TAG_TYPE:
			case AlFormatTag.TAG_SRC:
			case AlFormatTag.TAG_ENCODING:
			case AlFormatTag.TAG_CHARSET:
			case AlFormatTag.TAG_ALIGN:
			case AlFormatTag.TAG_CONTENT:
			case AlFormatTag.TAG_CLASS:
			case AlFormatTag.TAG_STYLE:
			case AlFormatTag.TAG_SIZE:
			case AlFormatTag.TAG_WIDTH:

				return true;
		}
		return false;
	}



	private int readRealCodepage() {
		StringBuilder rcp;
		if (xml_mode) {
			rcp = tag.getATTRValue(AlFormatTag.TAG_ENCODING);
			if (rcp != null)
				return AlUnicode.readRealCodePage(rcp);
		} else {
			rcp = tag.getATTRValue(AlFormatTag.TAG_CHARSET);
			if (rcp != null)
				return AlUnicode.readRealCodePage(rcp);
			rcp = tag.getATTRValue(AlFormatTag.TAG_CONTENT);
			if (rcp != null)
				return AlUnicode.readRealCodePage(rcp);
		}
		return TAL_CODE_PAGES.AUTO;
	}

	private void prepareTAG() {
        StringBuilder cls = null;

        if (!tag.closed && !tag.special) {

            styleStack.push(tag.tag, tag.cls);
            cssStyles.apply1ClassX(tag.tag, tag.cls, styleStack);

            cls = tag.getATTRValue(AlFormatTag.TAG_STYLE);
            if (cls != null)
                cssStyles.parseTemp(cls, styleStack);

            switch (tag.tag) {
                case AlFormatTag.TAG_P:
                case AlFormatTag.TAG_DIV:
                case AlFormatTag.TAG_SPAN:
                case AlFormatTag.TAG_H1: case AlFormatTag.TAG_H2: case AlFormatTag.TAG_H3: case AlFormatTag.TAG_H4: case AlFormatTag.TAG_H5: case AlFormatTag.TAG_H6:
                    cls = tag.getATTRValue(AlFormatTag.TAG_ALIGN);
                    if (cls != null && !cssStyles.disableExternal)
                        AlSetCSS.parseAlign(cls, styleStack);
                    break;
                case AlFormatTag.TAG_SMALL:
                    AlSetCSS.parseFontSize("-1", styleStack);
                    break;
                case AlFormatTag.TAG_BIG:
                    AlSetCSS.parseFontSize("+1", styleStack);
                    break;
                case AlFormatTag.TAG_FONT:
                    cls = tag.getATTRValue(AlFormatTag.TAG_SIZE);
                    if (cls != null && (cls.length() == 1 || (cls.length() == 2 && (cls.charAt(0) == '+' || cls.charAt(0) == '-'))))
                        AlSetCSS.parseFontSize(cls.toString(), styleStack);
                    break;
            }
        }


        if (externPrepareTAG()) {
            // kindle specific
            if (cssStyles != null && cssStyles.isKindle && !tag.ended && !tag.special) {
                switch (tag.tag) {
                    case AlFormatTag.TAG_P:
                    case AlFormatTag.TAG_BLOCKQUOTE:
                    case AlFormatTag.TAG_DIV:
                    case AlFormatTag.TAG_SPAN:
                    case AlFormatTag.TAG_H1: case AlFormatTag.TAG_H2: case AlFormatTag.TAG_H3: case AlFormatTag.TAG_H4: case AlFormatTag.TAG_H5: case AlFormatTag.TAG_H6:
                        if (parText.length == 0) {
                            cls = tag.getATTRValue(AlFormatTag.TAG_WIDTH);
                            if (cls != null) {
                                int c = AlSetCSS.parseInsertSpaces(cls);
                                clearPropStyle(AlParProperty.SL2_INDENT_MASK);
                                if (c > 0) {
                                    boolean preserve = (styleStack.buffer[styleStack.position].paragraph & AlStyles.SL_PRESERVE_SPACE) != 0;

                                    if (!preserve)
                                        setParagraphStyle(AlStyles.SL_PRESERVE_SPACE);
                                    for (int i = 0; i < c; i++)
                                        doTextChar((char) 0xa0, false);
                                    if (!preserve)
                                        clearParagraphStyle(AlStyles.SL_PRESERVE_SPACE);
                                }
                            }
                        }
                }
            }
        } else
        if (autoCodePage && ((xml_mode && tag.tag == AlFormatTag.TAG_XML) || (!xml_mode && tag.tag == AlFormatTag.TAG_META))) {
            int cp = readRealCodepage();
            if (cp != TAL_CODE_PAGES.AUTO)
                setCP(cp);
        }

        if (tag.closed || tag.ended) {
            long tmp_prop = (styleStack.buffer[styleStack.position].prop) & (AlParProperty.SL2_BREAK_BEFORE | AlParProperty.SL2_EMPTY_BEFORE);
            styleStack.pop(tag.tag);
            styleStack.buffer[styleStack.position].prop |= tmp_prop;
        }

        if (parText.length != 0) {
            doTextChar(getTextStyle(), false);
            doTextChar(getTextSize(), false);
        }

		/*StringBuilder cls = null;

		long old_size = 0, old_style = 0;

		if (tag.closed) {
			old_size = getTextSize();
			old_style = getTextStyle();
		}


		if (!tag.closed && !tag.special) {

			styleStack.push(tag.tag, tag.cls);

			if (cssStyles.apply1ClassX(tag.tag, tag.cls, styleStack) && parText.length != 0) {
				doTextChar(getTextStyle(), false);
				doTextChar(getTextSize(), false);
			}

			cls = tag.getATTRValue(AlFormatTag.TAG_STYLE);
			if (cls != null) {
				if (cssStyles.parseTemp(cls, styleStack)) {
					if (parText.length != 0) {
						doTextChar(getTextStyle(), false);
						doTextChar(getTextSize(), false);
					}
				}
			}

			switch (tag.tag) {
				case AlFormatTag.TAG_P:
				case AlFormatTag.TAG_DIV:
				case AlFormatTag.TAG_SPAN:
				case AlFormatTag.TAG_H1: case AlFormatTag.TAG_H2: case AlFormatTag.TAG_H3: case AlFormatTag.TAG_H4: case AlFormatTag.TAG_H5: case AlFormatTag.TAG_H6:
					cls = tag.getATTRValue(AlFormatTag.TAG_ALIGN);
					if (cls != null && !cssStyles.disableExternal)
						AlSetCSS.parseAlign(cls, styleStack);
					break;
				case AlFormatTag.TAG_SMALL:
					if (AlSetCSS.parseFontSize("-1", styleStack))
					if (parText.length != 0)
						doTextChar(getTextSize(), false);
					break;
				case AlFormatTag.TAG_BIG:
					if (AlSetCSS.parseFontSize("+1", styleStack))
						if (parText.length != 0)
							doTextChar(getTextSize(), false);
					break;
				case AlFormatTag.TAG_FONT:
					cls = tag.getATTRValue(AlFormatTag.TAG_SIZE);
					if (cls != null && (cls.length() == 1 || (cls.length() == 2 && (cls.charAt(0) == '+' || cls.charAt(0) == '-')))) {
						if (AlSetCSS.parseFontSize(cls.toString(), styleStack))
						if (parText.length != 0)
							doTextChar(getTextSize(), false);
					}
					break;
			}
		}


		if (externPrepareTAG()) {
			// kindle specific
			if (cssStyles != null && cssStyles.isKindle && !tag.ended && !tag.special) {
				switch (tag.tag) {
					case AlFormatTag.TAG_P:
					case AlFormatTag.TAG_BLOCKQUOTE:
					case AlFormatTag.TAG_DIV:
					case AlFormatTag.TAG_SPAN:
					case AlFormatTag.TAG_H1: case AlFormatTag.TAG_H2: case AlFormatTag.TAG_H3: case AlFormatTag.TAG_H4: case AlFormatTag.TAG_H5: case AlFormatTag.TAG_H6:
						if (parText.length == 0) {
							cls = tag.getATTRValue(AlFormatTag.TAG_WIDTH);
							if (cls != null) {
								int c = AlSetCSS.parseInsertSpaces(cls);
								clearPropStyle(AlParProperty.SL2_INDENT_MASK);
								if (c > 0) {
									boolean preserve = (styleStack.buffer[styleStack.position].paragraph & AlStyles.SL_PRESERVE_SPACE) != 0;

									if (!preserve)
										setParagraphStyle(AlStyles.SL_PRESERVE_SPACE);
									for (int i = 0; i < c; i++)
										doTextChar((char) 0xa0, false);
									if (!preserve)
										clearParagraphStyle(AlStyles.SL_PRESERVE_SPACE);
								}
							}
						}
				}
			}
		} else
		if (autoCodePage && ((xml_mode && tag.tag == AlFormatTag.TAG_XML) || (!xml_mode && tag.tag == AlFormatTag.TAG_META))) {
			int cp = readRealCodepage();
			if (cp != TAL_CODE_PAGES.AUTO)
				setCP(cp);
		}

		if (!tag.closed && tag.ended) {
			old_size = getTextSize();
			old_style = getTextStyle();
		}

		if (tag.closed || tag.ended) {
			long tmp_prop = (styleStack.buffer[styleStack.position].prop) & (AlParProperty.SL2_BREAK_BEFORE | AlParProperty.SL2_EMPTY_BEFORE);

			styleStack.pop(tag.tag);

			styleStack.buffer[styleStack.position].prop |= tmp_prop;

			if (parText.length != 0) {
				long new_size = getTextSize();
				long new_style = getTextStyle();

				if (old_style != new_style)
					doTextChar((char) new_style, false);
				if (old_size != new_size || ((new_style^old_style) & (AlStyles.STYLE_SUB | AlStyles.STYLE_SUP)) != 0)
					doTextChar((char) new_size, false);
			}
		}*/

	}

	@Override
	protected void doTextChar(char ch, boolean addSpecial) {
		
		
		if (allState.skipped_flag > 0) {
			
			if (allState.state_special_flag && addSpecial)
				specialBuff.add(ch);
			
		} else {
			switch (ch) {
				case 0xad:
					softHyphenCount++;
					break;
				case 0x20:
					if ((styleStack.buffer[styleStack.position].paragraph & AlStyles.SL_PRESERVE_SPACE) != 0) {
						if (parText.length == 0) {
							ch = 0xa0;
						} else {
							ch = parText.buffer[parText.length - 1];
							if (ch == 0x20 || ch == 0xa0) {
								ch = 0xa0;
							} else {
								ch = 0x20;
							}
						}
					} else {
						if (parText.length > 0 && parText.buffer[parText.length - 1] == 0x20)
							return;
					}
					break;
				/*case 0xa0:
					if (((*styleStack.activeParagraph) & AlStyles::SL_PRESERVE_SPACE) == 0) {
						ch = 0x20;
						if (parText.length && parText.buffer[parText.length - 1] == 0x20)
							return;
					}
					break;*/
			}
			if (parText.length > 0) {
				parText.add(ch);

				size++;
				parText.positionE = allState.start_position;
				parText.haveLetter = parText.haveLetter || (ch != 0xa0 && ch != 0x20
						&& (ch & AlStyles.STYLE_MASK_4CODECONVERT) != AlStyles.STYLE_BASE_4CODECONVERT);
				if (parText.length > EngBookMyType.AL_MAX_PARAGRAPH_LEN) {
					if (!AlUnicode.isLetterOrDigit(ch) && !allState.insertFromTag)
					newParagraph();
				}
			} else
			if (ch != 0x20) {
				parText.positionS = parText.positionE = allState.start_position_par;

				parText.paragraph = styleStack.getActualParagraph();
				parText.prop = styleStack.getActualProp();
				parText.sizeStart = size;
				parText.tableStart = currentTable.start;
				parText.tableCounter = currentTable.counter;
				parText.level = styleStack.position;

				parText.haveLetter = (ch != 0xa0 && (ch & AlStyles.STYLE_MASK_4CODECONVERT) != AlStyles.STYLE_BASE_4CODECONVERT);
				size++;

				parText.add(ch);
			}

			if (allState.state_special_flag && addSpecial)
				specialBuff.add(ch);
		}
	}

	protected boolean prepareTable() {
        if (preference.tableMode == EngBookMyType.TAL_TABLEMODE.NONE) {
            switch (tag.tag) {
            case AlFormatTag.TAG_TABLE:
            case AlFormatTag.TAG_TR:
            case AlFormatTag.TAG_TH:
            case AlFormatTag.TAG_TD:
			case AlFormatTag.TAG_TBL:
			case AlFormatTag.TAG_TC:
			case AlFormatTag.TAG_GRIDSPAN:

                newParagraph();
            }
            return true;
        }

		switch (tag.tag) {
			case AlFormatTag.TAG_TBL:
			case AlFormatTag.TAG_TABLE:
				if (currentTable.counter == 0)
					newParagraph();

				if (tag.closed) {

					if (currentTable.counter == 1) {
						currentTable.stop = allState.start_position;


							currentTable.verifyIsOneColumn();
							currentTable.cntrow = currentTable.rows.size();

							//
							// colspan scan
							for (int i = 0; i < currentTable.rows.size(); i++) {
								for (int j = 0; j < currentTable.rows.get(i).cells.size(); j++) {
									for (int k = 1; k < currentTable.rows.get(i).cells.get(j).colspan; k++) {
										currentCell.clear(0);
										currentCell.start = currentCell.stop = AlOneTable.LEVEL2_TABLE_CELL_COLSPANNED;
										currentCell.colspan = currentCell.rowspan = 1;
										currentTable.rows.get(i).cells.add(j + 1, currentCell);
                                        currentCell = new AlOneTableCell();
									}
								}
							}
							// rowspan scan
							for (int i = 0; i < currentTable.rows.size(); i++) {
								for (int j = 0; j < currentTable.rows.get(i).cells.size(); j++) {
									for (int m = 1; m < currentTable.rows.get(i).cells.get(j).rowspan; m++) {

										if (i + m < currentTable.rows.size() &&
												((j < currentTable.rows.get(i + m).cells.size() && currentTable.rows.get(i + m).cells.get(j).start != -1) ||
														(j == currentTable.rows.get(i + m).cells.size())
												)
												) {

											for (int k = 0; k < currentTable.rows.get(i).cells.get(j).colspan; k++) {
												currentCell.clear(0);
												currentCell.start = currentCell.stop = (k == currentTable.rows.get(i).cells.get(j).colspan - 1) ?
														AlOneTable.LEVEL2_TABLE_CELL_ROWSPANNED : AlOneTable.LEVEL2_TABLE_CELL_COLSPANNED;
												currentCell.colspan = currentCell.rowspan = 1;

												if (j == currentTable.rows.get(i + m).cells.size()) {
													currentTable.rows.get(i + m).cells.add(currentCell);
												} else {
													currentTable.rows.get(i + m).cells.add(j, currentCell);
												}
                                                currentCell = new AlOneTableCell();
											}

										} else break;

									}
								}
							}
							//all row have max column
							int max_col = 0;
							for (int i = 0; i < currentTable.rows.size(); i++) {
								if (max_col < currentTable.rows.get(i).cells.size())
									max_col = currentTable.rows.get(i).cells.size();
							}
							for (int i = 0; i < currentTable.rows.size(); i++) {
								int cnt_add = max_col - currentTable.rows.get(i).cells.size();
								for (int j = 0; j < cnt_add; j++) {
									currentCell.clear(0);
									currentCell.start = currentCell.stop = AlOneTable.LEVEL2_TABLE_CELL_COLSPANNED;//ALIGNED;
									currentTable.rows.get(i).cells.add(currentCell);
                                    currentCell = new AlOneTableCell();
								}
							}
							//

							addTable(currentTable);
							clearParagraphStyle(AlStyles.SL_TABLE);

						if (currentTable.isOneColumn) {
							for (int i = currentTable.startParagraph; i < par0.size(); i++) {
								par0.get(i).paragraph &= 0xFFFFFFFFFFFFFFFFL - AlStyles.SL_TABLE;
								par0.get(i).table_start = -1;
							}
						} else {

							allState.clearSkipped();
							setPropStyle((preference.tableMode != EngBookMyType.TAL_TABLEMODE.INTERNAL ? AlParProperty.SL2_JUST_RIGHT : AlParProperty.SL2_JUST_LEFT));

							StringBuilder s1 = new StringBuilder();

							if (preference.tableMode != EngBookMyType.TAL_TABLEMODE.EXTERNAL) {

								for (int i = 0; i < currentTable.cntrow; i++) {
									s1.setLength(0);
									s1.append((char)AlStyles.CHAR_ROWS_S);
									s1.append(Integer.toString(currentTable.start));
									s1.append(':');
									s1.append(Integer.toString(i));
									s1.append((char)AlStyles.CHAR_ROWS_E);
									addTextFromTag(s1, false);
								}


							}
							s1.setLength(0);
							s1.append((char)AlStyles.CHAR_SOFTPAR);
							addTextFromTag(s1, false);

							if (preference.tableMode != EngBookMyType.TAL_TABLEMODE.INTERNAL) {
								s1.setLength(0);
								s1.append((char)AlStyles.CHAR_LINK_S);
								s1.append("table:");
								s1.append(Integer.toString(currentTable.start));
								s1.append((char)AlStyles.CHAR_LINK_E);
								addTextFromTag(s1, false);

								setTextStyle(AlStyles.STYLE_LINK);
								addTextFromTag(currentTable.title, false);
								clearTextStyle(AlStyles.STYLE_LINK);
							}

							allState.restoreSkipped();
						}

						currentTable = new AlOneTable();
						currentTable.counter = 1;
                        newParagraph();
					}

					currentTable.counter--;
				} else
				if (!tag.ended) {

					if (currentTable.counter == 0) {

							StringBuilder s = tag.getATTRValue(AlFormatTag.TAG_TITLE);
                            if (s != null)
							    currentTable.title = s.toString();

							currentTable.startParagraph = par0.size();
							currentTable.startSize = currentRow.start = size;
							currentCell.clear(size);
							currentRow.cells.clear();
							currentTable.crow = 0;
							currentTable.start = allState.start_position_par;
							setParagraphStyle(AlStyles.SL_TABLE);

					}

					currentTable.counter++;
				} else {

				}
				return true;

            case AlFormatTag.TAG_GRIDSPAN:
                if (tag.closed) {

                } else
                if (!tag.ended) {

                } else {
                    StringBuilder s;
                    s = tag.getATTRValue(AlFormatTag.TAG_VAL);
                    if (s != null) {
                        currentCell.colspan = InternalFunc.str2int(s, 10);
                        if (currentCell.colspan < 1)
                            currentCell.colspan = 1;
                    }
                }
                return true;

            case AlFormatTag.TAG_TC:
			case AlFormatTag.TAG_TH:
			case AlFormatTag.TAG_TD:
				if (currentTable.counter == 1) {

					if (tag.closed) {
						clearParagraphStyle(AlParProperty.SL2_JUST_MASK);

						currentCell.stop = size;
						currentRow.cells.add(currentCell);
						newParagraph();
                        currentCell = new AlOneTableCell();
						currentCell.clear(size);
					} else {
						StringBuilder s;
						s = tag.getATTRValue(AlFormatTag.TAG_COLSPAN);
						if (s != null) {
							currentCell.colspan = InternalFunc.str2int(s, 10);
							if (currentCell.colspan < 1)
								currentCell.colspan = 1;
						}
						s = tag.getATTRValue(AlFormatTag.TAG_ROWSPAN);
						if (s != null) {
							currentCell.rowspan = InternalFunc.str2int(s, 10);
							if (currentCell.rowspan < 1)
								currentCell.rowspan = 1;
						}

						if (!tag.ended) {
							if (tag.tag == AlFormatTag.TAG_TH) {
								setParagraphStyle(AlParProperty.SL2_JUST_CENTER);
							}
							if (tag.tag != AlFormatTag.TAG_TC) {
								s = tag.getATTRValue(AlFormatTag.TAG_ALIGN);
								if (s != null) {
									String ss = s.toString().toLowerCase();
									if (ss.contentEquals("center")) {
										setParagraphStyle(AlParProperty.SL2_JUST_CENTER);
									} else
									if (ss.contentEquals("left")) {
										setParagraphStyle(AlParProperty.SL2_JUST_LEFT);
									} else
									if (ss.contentEquals("right")) {
										setParagraphStyle(AlParProperty.SL2_JUST_RIGHT);
									}
								}
							}
						} else {
							currentCell.stop = size;
							currentRow.cells.add(currentCell);
							newParagraph();
                            currentCell = new AlOneTableCell();
							currentCell.clear(size);
						}
					}
				}
				return true;
			case AlFormatTag.TAG_TR:
				if (currentTable.counter == 1) {
					if (tag.closed) {
						currentTable.rows.add(currentRow);
						currentRow = new AlOneTableRow();
						currentRow.start = size;
						currentTable.crow++;
						newParagraph();
					} else
					if (!tag.ended) {

					} else {

					}
				} else {

				}
				return true;
		}
		return false;
	}

	@Override
	public void debugStyles(AlRandomAccessFile df) {
		if (df != null && cssStyles != null) {
			cssStyles.debugStyles(df);
		}
	}

	protected long[] stack_styles0 = new long[MAX_STACK_STYLES + 1];
	protected int parStyleCollection;

	protected int	getEntityCode(String val, int base) {
		return InternalFunc.str2int(val, base);
	};

	@Override
	protected void parser(final int start_pos, final int stop_posRequest) {
		dinamicSize = stop_posRequest == -1;
		stop_posUsed = dinamicSize ? aFiles.getSize() : stop_posRequest;

		// this code must be in any parser without change!!!
		int 	buf_cnt, i, j;
		//AlIntHolder jVal = new AlIntHolder(0);
		char 	ch, ch1;

		for (i = start_pos; i < stop_posUsed;) {
			
				//Log.e("xml read pos ", Integer.toString(i));
							
				buf_cnt = AlFiles.LEVEL1_FILE_BUF_SIZE;
				if (i + buf_cnt > stop_posUsed) {
					buf_cnt = aFiles.getByteBuffer(i, parser_inBuff, stop_posUsed - i + 2);
					if (buf_cnt > stop_posUsed - i)
						buf_cnt = stop_posUsed - i;
				} else {
					buf_cnt = aFiles.getByteBuffer(i, parser_inBuff, buf_cnt + 2);
					buf_cnt -= 2;				
				}
				

				label_get_next_char:
				for (j = 0; j < buf_cnt;) {
					allState.start_position = i + j;	

					/*jVal.value = j;
					ch = AlUnicode.byte2Wide(use_cpR0, parser_inBuff, jVal);
					j = jVal.value;*/
					
					ch = (char)parser_inBuff[j++];
					ch &= 0xff;
					//if (ch >= 0x80) {
						switch (use_cpR0) {
						case TAL_CODE_PAGES.CP65001:
							if ((ch & 0x80) == 0) { } else
							if ((ch & 0x20) == 0) {				
								ch = (char)((ch & 0x1f) << 6);				
								ch1 = (char)parser_inBuff[j++];
								ch += (char)(ch1 & 0x3f);
							} else
							if ((ch & 0x10) == 0) {
								ch = (char)((ch & 0x1f) << 6);
								ch1 = (char)parser_inBuff[j++];
								ch += (char)(ch1 & 0x3f);
								ch <<= 6;
								ch1 = (char)parser_inBuff[j++];
								ch += (char)(ch1 & 0x3f);
							} else {
								if ((ch & 0x08) == 0) {
									j += 3;
								} else if ((ch & 0x04) == 0) {
									j += 4;
								} else if ((ch & 0x02) == 0) {
									j += 5;
								}
								ch = '?';
							}
							break;
						case TAL_CODE_PAGES.CP1201:
							ch <<= 8;
							ch1 = (char)parser_inBuff[j++];
							ch |= ch1 & 0xff;
							break;
						case TAL_CODE_PAGES.CP1200:			
							ch1 = (char)parser_inBuff[j++];					
							ch |= ch1 << 8;
							break;
						case 932:
                            if (ch >= 0x80) {
                                switch (ch) {
                                    case 0x80:
                                    case 0xfd:
                                    case 0xfe:
                                    case 0xff:
                                        ch = 0x0000;
                                        break;
                                    default:
                                        if (ch >= 0xa1 && ch <= 0xdf) {
                                            ch = (char) (ch + 0xfec0);
                                            break;
                                        }
                                        ch1 = (char) (parser_inBuff[j++] & 0xff);
                                        ch = (ch1 >= 0x40 && ch1 <= 0xfc) ? CP932.getChar(ch, ch1) : 0x00;
                                        break;
                                }
                            }
							break;
						case 936:
                            if (ch >= 0x80) {
                                switch (ch) {
                                    case 0x80:
                                        ch = 0x20AC;
                                        break;
                                    case 0xff:
                                        ch = 0x0000;
                                        break;
                                    default:
                                        ch1 = (char) (parser_inBuff[j++] & 0xff);
                                        ch = (ch1 >= 0x40 && ch1 <= 0xfe) ? CP936.getChar(ch, ch1) : 0x00;
                                        break;
                                }
                            }
							break;	
						case 949:
                            if (ch >= 0x80) {
                                switch (ch) {
                                    case 0x80:
                                    case 0xff:
                                        ch = 0x0000;
                                        break;
                                    default:
                                        ch1 = (char) (parser_inBuff[j++] & 0xff);
                                        ch = (ch1 >= 0x41 && ch1 <= 0xfe) ? CP949.getChar(ch, ch1) : 0x00;
                                        break;
                                }
                            }
							break;
						case 950:
                            if (ch >= 0x80) {
                                switch (ch) {
                                    case 0x80:
                                    case 0xff:
                                        ch = 0x0000;
                                        break;
                                    default:
                                        ch1 = (char) (parser_inBuff[j++] & 0xff);
                                        ch = (ch1 >= 0x40 && ch1 <= 0xfe) ? CP950.getChar(ch, ch1) : 0x00;
                                        break;
                                }
                            }
							break;
							
						default:
                            if (ch >= 0x80)
							    ch = data_cp[ch - 0x80];
							break;
						}
					//}
					if ((ch & AlStyles.STYLE_MASK_4CODECONVERT) == AlStyles.STYLE_BASE_4CODECONVERT)
						ch = 0x00;

			// end must be code				
					/////////////////// Begin Real Parser	


					/*if (allState.start_position > 1680080*//*1680157*//*) {
						ch--;
						ch++;
					}*/



					label_repeat_letter:
					while (true) {
						switch (allState.state_parser) {
							case STATE_XML_TEXT:
								allState.start_position_par = allState.start_position;
								if (ch > '<') {
									if ((ch & AlStyles.STYLE_MASK_4CODECONVERT) != AlStyles.STYLE_BASE_4CODECONVERT)
										doTextChar(ch, true);
								} else if (ch == '<') {
									allState.state_parser = STATE_XML_STAG;
									tag.resetTag(allState.start_position);
								} else if (ch == '&') {
									allState.state_parser += STATE_XML_ENTITY_ADDSTART;
									entity.setLength(0);
									entity.append('&');
								} else if (ch < 0x20) {
									if (allState.state_code_flag) {
										if (ch == 0x09) {
											ch = 0xa0;
										} else if (ch == 0x0a) {
											newParagraph();
											continue label_get_next_char;
										} else if (ch < 0x20)
											ch = 0x20;
									} else {
										if (ch == 0x0a || ch == 0x09) {
											ch = 0x20;
										} else if (ch < 0x20)
											continue label_get_next_char;
									}
									doTextChar(ch, true);
								} else {
									doTextChar(ch, true);
								}
								continue label_get_next_char;
							case STATE_XML_TEXT + STATE_XML_ENTITY_ADDNUM0:
								if (ch == 'x') {
									allState.state_parser += 0x2000;
									entity.append(ch);
									continue label_get_next_char;
								}
								if (AlUnicode.isDecDigit(ch)) {
									entity.append(ch);
									allState.state_parser += 0x1000;
									continue label_get_next_char;
								}
								allState.state_parser = STATE_XML_TEXT;
								addTextFromTag(entity, true);
								continue label_repeat_letter;
							case STATE_XML_TEXT + STATE_XML_ENTITY_ADDNUM1:
								if (AlUnicode.isDecDigit(ch)) {
									entity.append(ch);
									continue label_get_next_char;
								}
								allState.state_parser = STATE_XML_TEXT;

							{
								int res = getEntityCode(entity.substring(2), 10);//InternalFunc.str2int(entity.substring(2), 10);
								if (res > 0) {
									if (res < 0x20)
										res = 0x20;
									doTextChar((char) res, true);
								} else {
									addTextFromTag(entity.toString(), true);
								}
							}

							if (ch != ';')
								continue label_repeat_letter;
							continue label_get_next_char;
							case STATE_XML_TEXT + STATE_XML_ENTITY_ADDNUMHEX:
								if (AlUnicode.isHEXDigit(ch)) {
									entity.append(ch);
									continue label_get_next_char;
								}
								allState.state_parser = STATE_XML_TEXT;

							{
								int res = getEntityCode(entity.substring(3), 16);//InternalFunc.str2int(entity.substring(3), 16);
								if (res > 0) {
									if (res < 0x20)
										res = 0x20;
									doTextChar((char) res, true);
								} else {
									addTextFromTag(entity.toString(), true);
								}
							}

							if (ch != ';')
								continue label_repeat_letter;
							continue label_get_next_char;
							case STATE_XML_TEXT + STATE_XML_ENTITY_ADDDEF:
								if (AlUnicode.isLetterOrDigit(ch) && ch > 0x20 && ch < 0x80) {
									entity.append(ch);
									continue label_get_next_char;
								}
								allState.state_parser = STATE_XML_TEXT;

								ch1 = findEntity(entity.substring(1));
								if (ch1 != 0) {
									doTextChar(ch1, true);
									if (ch != ';')
										continue label_repeat_letter;
									continue label_get_next_char;
								}

								addTextFromTag(entity, true);
								continue label_repeat_letter;
							case STATE_XML_TEXT + STATE_XML_ENTITY_ADDSTART:
								if (ch == '#') {
									entity.append(ch);
									allState.state_parser += 0x2000;
									continue label_get_next_char;
								}
								if (AlUnicode.isLetter(ch) && ch > 0x20 && ch < 0x80) {
									entity.append(ch);
									allState.state_parser += 0x1000;
									continue label_get_next_char;
								}
								allState.state_parser = STATE_XML_TEXT;
								doTextChar('&', true);
								continue label_repeat_letter;

							case STATE_XML_SKIP:
								if (ch == '<') {
									allState.state_parser = STATE_XML_STAG;
									tag.resetTag(allState.start_position);
									continue label_get_next_char;
								}
								switch (use_cpR0) {
									case 1201:
										for (; j < buf_cnt; j += 2)
											if (parser_inBuff[j + 1] == '<') {
												break;
											}
										break;
									case 1200:
										for (; j < buf_cnt; j += 2)
											if (parser_inBuff[j] == '<') {
												break;
											}
										break;
									default:
										for (; j < buf_cnt; j++)
											if (parser_inBuff[j] == '<')
												break;
								}
								continue label_get_next_char;
							case STATE_XML_STAG:
								if (AlUnicode.isLetter(ch)) {
									allState.state_parser = STATE_XML_TAG;
									tag.add2Tag(ch);
								} else if (ch == '/') {
									if (tag.closed)
										allState.state_parser = STATE_XML_TAG_ERROR;
									tag.closed = true;
									tag.resetAttr();
								} else if (ch == '>') {
									allState.state_parser = STATE_XML_TEXT;
								} else if (ch == '?') {
									tag.special = true;
									continue label_get_next_char;
								} else if (ch == '!') {
									allState.state_parser = STATE_XML_TEST;
								} else if (AlUnicode.isSpace(ch)) {

								} else {
									allState.state_parser = STATE_XML_TAG_ERROR;
								}
								continue label_get_next_char;
							case STATE_XML_TAG:
								if (ch == '>') {
									allState.state_parser = STATE_XML_TEXT;
									prepareTAG();
								} else if (AlUnicode.isSpace(ch)) {
									allState.state_parser = STATE_XML_ETAG;
								} else if (ch == '<' || ch == '&') {
									allState.state_parser = STATE_XML_TAG_ERROR;
								} else if (ch == '?') {
									allState.state_parser = STATE_XML_ETAG2;
								} else if (ch == '/') {
									allState.state_parser = STATE_XML_ETAG2;
									if (tag.closed)
										allState.state_parser = STATE_XML_TAG_ERROR;
									tag.ended = true;
								} else if (ch == ':') {
									AlIntHolder tmp_position = new AlIntHolder(i + j);
									ch = getConvertChar(use_cpR0, tmp_position);
									if (AlUnicode.isLetter(ch)) {
										tag.clearTag();
									} else {
										allState.state_parser = STATE_XML_TAG_ERROR;
									}
								} else if (AlUnicode.isLetterOrDigit(ch) || ch == '-' || ch == '_') {
									tag.add2Tag(ch);
								} else {
									allState.state_parser = STATE_XML_TAG_ERROR;
								}
								continue label_get_next_char;
							case STATE_XML_ETAG2:
								if (ch == '>') {
									allState.state_parser = STATE_XML_TEXT;
									prepareTAG();
								}
								continue label_get_next_char;
							case STATE_XML_TAG_ERROR:
								if (ch == '>') {
									allState.state_parser = STATE_XML_TEXT;
								}
								continue label_get_next_char;
							case STATE_XML_ETAG:
								if (ch == '>') {
									allState.state_parser = STATE_XML_TEXT;
									prepareTAG();
								} else if (ch == '/') {
									allState.state_parser = STATE_XML_ETAG2;
									if (tag.closed)
										allState.state_parser = STATE_XML_TAG_ERROR;
									tag.ended = true;
								} else if (AlUnicode.isLetter(ch)) {
									tag.clearAttrName();
									tag.add2AttrName(ch);
									allState.state_parser = STATE_XML_ATTRIBUTE_NAME;
								} else if (AlUnicode.isSpace(ch) || ch == '?') {

								} else {
									allState.state_parser = STATE_XML_TAG_ERROR;
								}
								continue label_get_next_char;
							case STATE_XML_ATTRIBUTE_NAME:
								if (ch == '=' || AlUnicode.isSpace(ch)) {
									allState.state_parser = STATE_XML_ATTRIBUTE_ENAME;
								} else if (ch == '/') {
									allState.state_parser = STATE_XML_ETAG;
									if (tag.closed)
										allState.state_parser = STATE_XML_TAG_ERROR;
									tag.closed = true;
								} else if (ch == '>') {
									allState.state_parser = STATE_XML_TEXT;
									prepareTAG();
								} else if (ch == '<' || ch == '&') {
									allState.state_parser = STATE_XML_TAG_ERROR;
								} else if (ch == ':') {
									AlIntHolder tmp_position = new AlIntHolder(i + j);
									ch = getConvertChar(use_cpR0, tmp_position);
									if (AlUnicode.isLetter(ch)) {
										tag.clearAttrName();
									} else {
										allState.state_parser = STATE_XML_TAG_ERROR;
									}
								} else if (AlUnicode.isLetterOrDigit(ch) || ch == '-' || ch == '_') {
									tag.add2AttrName(ch);
								} else {
									allState.state_parser = STATE_XML_TAG_ERROR;
								}
								continue label_get_next_char;
							case STATE_XML_ATTRIBUTE_ENAME:
								if (ch == '\'') {
									allState.state_parser = STATE_XML_ATTRIBUTE_VALUE1;
									tag.clearAttrVal(allState.start_position + (use_cpR0 == 1200 || use_cpR0 == 1201 ? 2 : 1));
								} else if (ch == '\"') {
									allState.state_parser = STATE_XML_ATTRIBUTE_VALUE2;
									tag.clearAttrVal(allState.start_position + (use_cpR0 == 1200 || use_cpR0 == 1201 ? 2 : 1));
								} else if (ch == '=' || AlUnicode.isSpace(ch)) {

								} else if (AlUnicode.isLetterOrDigit(ch) || AlUnicode.isPunctuation(ch)) {
									allState.state_parser = STATE_XML_ATTRIBUTE_VALUE3;
									tag.clearAttrVal(allState.start_position);
									tag.add2AttrValue(ch);
								} else {
									allState.state_parser = STATE_XML_TAG_ERROR;
								}
								continue label_get_next_char;

							case STATE_XML_ATTRIBUTE_VALUE1 + STATE_XML_ENTITY_ADDNUM0:
							case STATE_XML_ATTRIBUTE_VALUE2 + STATE_XML_ENTITY_ADDNUM0:
							case STATE_XML_ATTRIBUTE_VALUE3 + STATE_XML_ENTITY_ADDNUM0:
								if (ch == 'x') {
									allState.state_parser += 0x2000;
									entity.append(ch);
									continue label_get_next_char;
								}
								if (AlUnicode.isDecDigit(ch)) {
									entity.append(ch);
									allState.state_parser += 0x1000;
									continue label_get_next_char;
								}
								allState.state_parser -= STATE_XML_ENTITY_ADDNUM0;
								tag.add2AttrValue(entity);
								continue label_repeat_letter;
							case STATE_XML_ATTRIBUTE_VALUE1 + STATE_XML_ENTITY_ADDNUM1:
							case STATE_XML_ATTRIBUTE_VALUE2 + STATE_XML_ENTITY_ADDNUM1:
							case STATE_XML_ATTRIBUTE_VALUE3 + STATE_XML_ENTITY_ADDNUM1:
								if (AlUnicode.isDecDigit(ch)) {
									entity.append(ch);
									continue label_get_next_char;
								}
								allState.state_parser -= STATE_XML_ENTITY_ADDNUM1;

							{
								Integer res = getEntityCode(entity.substring(2), 10);//InternalFunc.str2int(entity.substring(2), 10);
								if (res != null) {
									if (res < 0x20) res = 0x20;
									tag.add2AttrValue((char) res.intValue());
								} else {
									tag.add2AttrValue(entity);
								}
							}

							if (ch != ';')
								continue label_repeat_letter;
							continue label_get_next_char;
							case STATE_XML_ATTRIBUTE_VALUE1 + STATE_XML_ENTITY_ADDNUMHEX:
							case STATE_XML_ATTRIBUTE_VALUE2 + STATE_XML_ENTITY_ADDNUMHEX:
							case STATE_XML_ATTRIBUTE_VALUE3 + STATE_XML_ENTITY_ADDNUMHEX:
								if (AlUnicode.isHEXDigit(ch)) {
									entity.append(ch);
									continue label_get_next_char;
								}
								allState.state_parser -= STATE_XML_ENTITY_ADDNUMHEX;

							{
								Integer res = getEntityCode(entity.substring(3), 16);//InternalFunc.str2int(entity.substring(3), 16);
								if (res != null) {
									if (res < 0x20) res = 0x20;
									tag.add2AttrValue((char) res.intValue());
								} else {
									tag.add2AttrValue(entity);
								}
							}

							if (ch != ';')
								continue label_repeat_letter;
							continue label_get_next_char;
							case STATE_XML_ATTRIBUTE_VALUE1 + STATE_XML_ENTITY_ADDDEF:
							case STATE_XML_ATTRIBUTE_VALUE2 + STATE_XML_ENTITY_ADDDEF:
							case STATE_XML_ATTRIBUTE_VALUE3 + STATE_XML_ENTITY_ADDDEF:
								if (AlUnicode.isLatinLetter(ch)) {
									entity.append(ch);
									continue label_get_next_char;
								}
								allState.state_parser -= STATE_XML_ENTITY_ADDDEF;

								ch1 = findEntity(entity.substring(1));
								if (ch1 != 0) {
									tag.add2AttrValue(ch1);
									continue label_get_next_char;
								}

								tag.add2AttrValue(entity);
								continue label_repeat_letter;
							case STATE_XML_ATTRIBUTE_VALUE1 + STATE_XML_ENTITY_ADDSTART:
							case STATE_XML_ATTRIBUTE_VALUE2 + STATE_XML_ENTITY_ADDSTART:
							case STATE_XML_ATTRIBUTE_VALUE3 + STATE_XML_ENTITY_ADDSTART:
								if (ch == '#') {
									entity.append(ch);
									allState.state_parser += 0x2000;
									continue label_get_next_char;
								}
								if (AlUnicode.isLatinLetter(ch)) {
									entity.append(ch);
									allState.state_parser += 0x1000;
									continue label_get_next_char;
								}
								allState.state_parser -= STATE_XML_ENTITY_ADDSTART;
								tag.add2AttrValue('&');
								continue label_get_next_char;
							case STATE_XML_ATTRIBUTE_VALUE3:
								if (ch == '>') {
									if (isNeedAttribute(tag.aname))
										tag.addAttribute(allState.start_position);
									allState.state_parser = STATE_XML_TEXT;
									prepareTAG();
								} else if (ch == ';' || AlUnicode.isSpace(ch)) {
									if (isNeedAttribute(tag.aname))
										tag.addAttribute(allState.start_position);
									allState.state_parser = STATE_XML_ETAG;
								} else if (ch == '&') {
									allState.state_parser += STATE_XML_ENTITY_ADDSTART;
									entity.setLength(0);
									entity.append(ch);
								} else if (ch < 0x20) {

								} else {
									tag.add2AttrValue(ch);
								}
								continue label_get_next_char;
							case STATE_XML_ATTRIBUTE_VALUE1:
							case STATE_XML_ATTRIBUTE_VALUE2:
								if (ch == '>') {
									allState.state_parser = STATE_XML_TEXT;
								} else if (ch == '\'' && allState.state_parser == STATE_XML_ATTRIBUTE_VALUE1) {
									if (isNeedAttribute(tag.aname))
										tag.addAttribute(allState.start_position);
									allState.state_parser = STATE_XML_ETAG;
								} else if (ch == '\"' && allState.state_parser == STATE_XML_ATTRIBUTE_VALUE2) {
									if (isNeedAttribute(tag.aname))
										tag.addAttribute(allState.start_position);
									allState.state_parser = STATE_XML_ETAG;
								} else if (ch == '&') {
									allState.state_parser += STATE_XML_ENTITY_ADDSTART;
									entity.setLength(0);
									entity.append(ch);
								} else if (AlUnicode.isSpace(ch)) {
									tag.add2AttrValue(' ');
								} else if (ch < 0x20) {

								} else {
									tag.add2AttrValue(ch);
								}
								continue label_get_next_char;
								///////////////////////////////////////////////////////////
							case STATE_XML_TEST:
								if (ch == '<') {
									allState.state_parser = STATE_XML_TAG_ERROR;
								} else if (ch == '-') {
									allState.state_parser = STATE_XML_TEST_COMMENT;
								} else if (ch == 0x20 || ch == 0x09 || ch == 0x0a) {
									tag.add2Tag(ch);
									allState.state_parser = STATE_XML_ETAG;
								} else if (ch == '[') {
									doTextChar(ch, false);
									allState.state_parser = STATE_XML_TEXT;
								} else if (ch > 0x20) {
									tag.add2Tag('!');
									tag.add2Tag(ch);
									allState.state_parser = STATE_XML_TAG;
								}
								continue label_get_next_char;
							case STATE_XML_TEST_CDATA:
								if (ch == '<') {
									allState.state_parser = STATE_XML_ETAG2;
								} else if (ch == 'C') {
									allState.state_parser = STATE_XML_CDATAWAIT;
								} else if (AlUnicode.isSpace(ch)) {
									tag.add2Tag('!');
									allState.state_parser = STATE_XML_ETAG;
								} else if (ch > 0x20) {
									tag.add2Tag('!');
									tag.add2Tag('-');
									tag.add2Tag(ch);
									allState.state_parser = STATE_XML_TAG;
								}
								continue label_get_next_char;
							case STATE_XML_TEST_COMMENT:
								if (ch == '<') {
									allState.state_parser = STATE_XML_ETAG2;
								} else if (ch == '-') {
									allState.state_parser = STATE_XML_COMMENT;
								} else if (AlUnicode.isSpace(ch)) {
									tag.add2Tag('!');
									allState.state_parser = STATE_XML_ETAG;
								} else if (ch > 0x20) {
									tag.add2Tag('!');
									tag.add2Tag('-');
									tag.add2Tag(ch);
									allState.state_parser = STATE_XML_TAG;
								}
								continue label_get_next_char;
							case STATE_XML_COMMENT:
								if (ch == '-') {
									allState.state_parser = STATE_XML_ECOMMENT1;
								}
								continue label_get_next_char;
							case STATE_XML_ECOMMENT1:
								if (ch == '-') {
									allState.state_parser = STATE_XML_ECOMMENT2;
								} else {
									allState.state_parser = STATE_XML_COMMENT;
								}
								continue label_get_next_char;
							case STATE_XML_ECOMMENT2:
								if (ch == '>') {
									allState.state_parser = STATE_XML_TEXT;
								} else if (ch == '-') {

								} else {
									allState.state_parser = STATE_XML_COMMENT;
								}
								continue label_get_next_char;
							case STATE_XML_CDATAWAIT:
								if (ch == '[') {
									allState.state_parser = STATE_XML_CDATAPROCESS;
								}
								continue label_get_next_char;
							case STATE_XML_CDATAPROCESS:
								if (ch == ']') {
									allState.state_parser = STATE_XML_ECDATA1;
								} else {
									if (allState.state_special_flag)
										specialBuff.add(ch);
								}
								continue label_get_next_char;
							case STATE_XML_ECDATA1:
								if (ch == ']') {
									allState.state_parser = STATE_XML_ECDATA2;
								} else {
									if (allState.state_special_flag) {
										specialBuff.add(']');
										specialBuff.add(ch);
									}
									allState.state_parser = STATE_XML_CDATAPROCESS;
								}
								continue label_get_next_char;
							case STATE_XML_ECDATA2:
								if (ch == '>') {
									allState.state_parser = STATE_XML_TEXT;
								} else if (ch == ']') {

								} else if (AlUnicode.isSpace(ch)) {
									allState.state_parser = STATE_XML_CDATAPROCESS;
								}
								continue label_get_next_char;
						}

						/////////////////// End Real Parser
					// this code must be in any parser without change!!!
					}
				}
				i += j;
			}

			newParagraph();
			// end must be code
	}

	protected char findEntity(String key) {
		Character ch = entityMap.get(key);
		if (ch == null)
			return 0x00;
		return ch;
	}

	private final static HashMap<String, Character> entityMap;
	
	static {
/////////////// XML  ///////////////////////		
		entityMap = new HashMap<>();
		entityMap.put("amp", '&');
		entityMap.put("apos", '\'');
		entityMap.put("gt", '>');
		entityMap.put("lt", '<');
		entityMap.put("quot", '\"');
/////////////// HTML ///////////////////////		
		entityMap.put("nbsp", (char)160);//����������� ������
		entityMap.put("iexcl", (char)161);//������������ ��������������� ����
		entityMap.put("cent", (char)162);//����
		entityMap.put("pound",(char)163);//���� ����������
		entityMap.put("curren", (char)164);//�������� �������
		entityMap.put("yen", (char)165);//���� ��� ����
		entityMap.put("brvbar", (char)166);//����������� ������������ �����
		entityMap.put("sect", (char)167);//��������
		entityMap.put("uml", (char)168);//����� (���� ��� ������� ��� ������������ �� �������� �� �������������� �������
		entityMap.put("copy", (char)169);//���� copyright
		entityMap.put("ordf", (char)170);//������� ���������� ���������
		entityMap.put("laquo", (char)171);//����� ������� ������� ������
		entityMap.put("not", (char)172);//���� ���������

		//{173,"shy");//����� ���������� ��������
		entityMap.put("shy", (char)173);//����� ���������� ��������

		entityMap.put("reg", (char)174);//���� ������������������ �������� �����
		entityMap.put("macr", (char)175);//���� ������� ��� �������
		entityMap.put("deg", (char)176);//������
		entityMap.put("plusmn", (char)177);//����-�����
		entityMap.put("sup2", (char)178);//������� ������ '���' - "� ��������"
		entityMap.put("sup3", (char)179);//������� ������ '���' - "� ����"
		entityMap.put("acute", (char)180);//���� ��������
		entityMap.put("micro", (char)181);//�����
		entityMap.put("para", (char)182);//������ ���������
		entityMap.put("middot", (char)183);//�����
		entityMap.put("cedil", (char)184);//������ (��������������� ����)
		entityMap.put("sup1", (char)185);//������� ������ '����'
		entityMap.put("ordm", (char)186);//������� ���������� ���������
		entityMap.put("raquo", (char)187);//������ ������� ������� ������
		entityMap.put("frac14", (char)188);//����� - ���� ��������
		entityMap.put("frac12", (char)189);//����� - ���� ������
		entityMap.put("frac34", (char)190);//����� - ��� ��������
		entityMap.put("iquest", (char)191);//������������ �������������� ����
		entityMap.put("Agrave", (char)192);//��������� ��������� ����� � � ����� ���������
		entityMap.put("Aacute", (char)193);//��������� ��������� ����� � � ������ ���������
		entityMap.put("Acirc", (char)194);//��������� ��������� ����� � � ������������� (�������������� ���� ��� �������)
		entityMap.put("Atilde",(char)195);//��������� ��������� ����� � � �������
		entityMap.put("Auml",(char)196);//��������� ��������� ����� � � ������ (���� ��� ������� ��� ������������ �� �������� �� �������������� �������)
		entityMap.put("Aring",(char)197);//��������� ��������� ����� � � ������� �������
		entityMap.put("AElig",(char)198);//��������� ��������� ������� AE
		entityMap.put("Ccedil",(char)199);//��������� ��������� ����� C � �������
		entityMap.put("Egrave",(char)200);//��������� ��������� ����� E � ����� ���������
		entityMap.put("Eacute", (char)201);//��������� ��������� ����� E � ������ ���������
		entityMap.put("Ecirc", (char)202);//��������� ��������� ����� E � ������������� (�������������� ���� ��� �������)
		entityMap.put("Euml", (char)203);//��������� ��������� ����� E � ������
		entityMap.put("Igrave", (char)204);//��������� ��������� ����� I � ����� ���������
		entityMap.put("Iacute", (char)205);//��������� ��������� ����� I � ������ ���������
		entityMap.put("Icirc", (char)206);//��������� ��������� ����� I � �������������
		entityMap.put("Iuml", (char)207);//��������� ��������� ����� I � ������
		entityMap.put("ETH", (char)208);//��������� ��������� ������� ETH
		entityMap.put("Ntilde", (char)209);//��������� ��������� ����� N � �������
		entityMap.put("Ograve", (char)210);//��������� ��������� ����� O � ����� ���������
		entityMap.put("Oacute", (char)211);//��������� ��������� ����� O � ������ ���������
		entityMap.put("Ocirc", (char)212);//��������� ��������� ����� O � �������������
		entityMap.put("Otilde", (char)213);//��������� ��������� ����� O � �������
		entityMap.put("Ouml", (char)214);//��������� ��������� ����� O � ������
		entityMap.put("times", (char)215);//���� ���������
		entityMap.put("Oslash", (char)216);//��������� ��������� ����� O �� �������
		entityMap.put("Ugrave", (char)217);//��������� ��������� ����� U � ����� ���������
		entityMap.put("Uacute", (char)218);//��������� ��������� ����� U � ������ ���������
		entityMap.put("Ucirc", (char)219);//��������� ��������� ����� U � �������������
		entityMap.put("Uuml", (char)220);//��������� ��������� ����� U � ������
		entityMap.put("Yacute", (char)221);//��������� ��������� ����� Y � ������ ���������
		entityMap.put("THORN", (char)222);//��������� ��������� ����� THORN
		entityMap.put("agrave", (char)224);//��������� �������� ����� � � ����� ���������
		entityMap.put("szlig", (char)223);// �������� �������� ������� s
		entityMap.put("aacute", (char)225);//��������� �������� ����� � � ������ ���������
		entityMap.put("acirc", (char)226);//��������� �������� ����� � � �������������
		entityMap.put("atilde", (char)227);//��������� �������� ����� � � �������
		entityMap.put("auml", (char)228);//��������� �������� ����� � � ������
		entityMap.put("aring", (char)229);//��������� �������� ����� � � ������� �������
		entityMap.put("aelig", (char)230);//��������� �������� ����� �E
		entityMap.put("ccedil", (char)231);//��������� �������� ����� � � �������
		entityMap.put("egrave", (char)232);//��������� �������� ����� E � ����� ���������
		entityMap.put("eacute", (char)233);//��������� �������� ����� E � ������ ���������
		entityMap.put("ecirc", (char)234);//��������� �������� ����� E � �������������
		entityMap.put("euml", (char)235);//��������� �������� ����� E � ������
		entityMap.put("igrave", (char)236);//��������� �������� ����� I � ����� ���������
		entityMap.put("iacute", (char)237);//��������� �������� ����� I � ������ ���������
		entityMap.put("icirc", (char)238);//��������� �������� ����� I � �������������
		entityMap.put("iuml", (char)239);//��������� �������� ����� I � ������
		entityMap.put("eth", (char)240);//��������� �������� ������� eth
		entityMap.put("ntilde", (char)241);//��������� �������� ����� N � �������
		entityMap.put("ograve", (char)242);//��������� �������� ����� O � ����� ���������
		entityMap.put("oacute", (char)243);//��������� �������� ����� O � ������ ���������
		entityMap.put("ocirc", (char)244);//��������� �������� ����� O � �������������
		entityMap.put("otilde", (char)245);//��������� �������� ����� I � �������
		entityMap.put("ouml", (char)246);//��������� �������� ����� I � ������
		entityMap.put("divide", (char)247);//���� �������
		entityMap.put("oslash", (char)248);//��������� �������� ����� O �� �������
		entityMap.put("ugrave", (char)249);//��������� �������� ����� U � ����� ���������
		entityMap.put("uacute", (char)250);//��������� �������� ����� U � ������ ���������
		entityMap.put("ucirc", (char)251);//��������� �������� ����� U � �������������
		entityMap.put("uuml", (char)252);//��������� �������� ����� U � ������
		entityMap.put("yacute", (char)253);//��������� �������� ����� Y � ������ ���������
		entityMap.put("thorn", (char)254);//��������� �������� ����� thorn
		entityMap.put("yuml", (char)255);//��������� �������� ����� Y � ������
		entityMap.put("OElig", (char)338);
		entityMap.put("oelig", (char)339);
		entityMap.put("Scaron", (char)352);
		entityMap.put("scaron", (char)353);
		entityMap.put("Yuml", (char)376);
		entityMap.put("fnof", (char)402);//���� �������
		entityMap.put("circ", (char)710);//������ ������������ (�������������� ���� ��� �������)
		entityMap.put("tilde", (char)732);//������	
		entityMap.put("Alpha", (char)913);//��������� ��������� ����� �����
		entityMap.put("Beta", (char)914);//��������� ��������� ����� ����
		entityMap.put("Gamma", (char)915);//��������� ��������� ����� �����
		entityMap.put("Delta", (char)916);//��������� ��������� ����� ������
		entityMap.put("Epsilon", (char)917);//��������� ��������� ����� �������
		entityMap.put("Zeta", (char)918);//��������� ��������� ����� �����
		entityMap.put("Eta", (char)919);//��������� ��������� ����� ���
		entityMap.put("Theta", (char)920);//��������� ��������� ����� ����
		entityMap.put("Iota", (char)921);//��������� ��������� ����� ����
		entityMap.put("Kappa", (char)922);//��������� ��������� ����� �����
		entityMap.put("Lambda", (char)923);//��������� ��������� ����� ������
		entityMap.put("Mu", (char)924);//��������� ��������� ����� ��
		entityMap.put("Nu", (char)925);//��������� ��������� ����� ��
		entityMap.put("Xi", (char)926);//��������� ��������� ����� ���
		entityMap.put("Omicron", (char)927);//��������� ��������� ����� �������
		entityMap.put("Pi", (char)928);//��������� ��������� ����� ��
		entityMap.put("Rho", (char)929);//��������� ��������� ����� ��
		entityMap.put("Sigma", (char)931);//��������� ��������� ����� �����
		entityMap.put("Tau", (char)932);//��������� ��������� ����� ���
		entityMap.put("Upsilon", (char)933);//��������� ��������� ����� �������
		entityMap.put("Phi",(char)934);//��������� ��������� ����� ��
		entityMap.put("Chi",(char)935);//��������� ��������� ����� ��
		entityMap.put("Psi",(char)936);//��������� ��������� ����� ���
		entityMap.put("Omega",(char)937);//��������� ��������� ����� �����
		entityMap.put("alpha",(char)945);//��������� �������� ����� �����
		entityMap.put("beta",(char)946);	//��������� �������� ����� ����
		entityMap.put("gamma",(char)947);	//��������� �������� ����� �����
		entityMap.put("delta",(char)948);	//��������� �������� ����� ������
	    entityMap.put("epsilon",(char)949);	//��������� �������� ����� �������
		entityMap.put("zeta",(char)950);	//��������� �������� ����� �����
		entityMap.put("eta",(char)951);		//��������� �������� ����� ���
		entityMap.put("theta",(char)952);	//��������� �������� ����� ����
		entityMap.put("iota",(char)953);	//��������� �������� ����� ����      
		entityMap.put("kappa",(char)954);	//��������� �������� ����� �����
		entityMap.put("lambda",(char)955);	//��������� �������� ����� ������
		entityMap.put("mu",(char)956);		//��������� �������� ����� ��
		entityMap.put("nu",(char)957);		//��������� �������� ����� ��
		entityMap.put("xi",(char)958);		//��������� �������� ����� ���
		entityMap.put("omicron",(char)959);	//��������� �������� ����� �������
		entityMap.put("pi",(char)960);		//��������� �������� ����� ��
		entityMap.put("rho",(char)961);		//��������� �������� ����� ��
		entityMap.put("sigmaf",(char)962);	//��������� �������� ����� ����� (final)
		entityMap.put("sigma",(char)963);	//��������� �������� ����� �����
		entityMap.put("tau",(char)964);		//��������� �������� ����� ���
		entityMap.put("upsilon",(char)965);	//��������� �������� ����� �������
		entityMap.put("phi",(char)966);			//��������� �������� ����� ��
		entityMap.put("chi",(char)967);			//��������� �������� ����� ��
		entityMap.put("psi",(char)968);			//��������� �������� ����� ���
		entityMap.put("omega",(char)969);		//��������� �������� ����� �����
		entityMap.put("thetasym",(char)977);   //��������� �������� ����� ����
		entityMap.put("upsih",(char)978);      //��������� ������ ������� � �������
		entityMap.put("piv",(char)982);        //��������� ������ ��	
		entityMap.put("ensp",(char)8194);       //en space
		entityMap.put("emsp",(char)8195);       //em space
		entityMap.put("thinsp",(char)8201);     //����� ������
		entityMap.put("zwnj",(char)8204);       //zero width non-joiner
		entityMap.put("zwj",(char)8205);        //zero width joiner
		entityMap.put("lrm",(char)8206);        //left-to-right mark
		entityMap.put("rlm",(char)8207);        //right-to-left mark
		entityMap.put("ndash",(char)8211);		//����
		entityMap.put("mdash",(char)8212);		//������� ����
		entityMap.put("lsquo",(char)8216);		//����� ��������� �������
		entityMap.put("rsquo",(char)8217);		//������ ��������� �������
		entityMap.put("sbquo",(char)8218);		//������ ��������� �������
		entityMap.put("ldquo",(char)8220);		//����� ������� �������
		entityMap.put("rdquo",(char)8221);		//������ ������� �������
		entityMap.put("bdquo",(char)8222);		//������ �������
		entityMap.put("dagger",(char)8224);     //�������
		entityMap.put("Dagger",(char)8225);     //������� �������
		entityMap.put("bull",(char)8226);		//bullet - ��������� ������ ������
		entityMap.put("hellip",(char)8230);		//���������� ...
		entityMap.put("permil",(char)8240);     //���� �� ������	
		entityMap.put("prime",(char)8242);		//��������� ����� - ������ � ����
		entityMap.put("Prime",(char)8243);		//������� ����� - ������� � �����<
		entityMap.put("lsaquo",(char)8249);
		entityMap.put("rsaquo",(char)8250);
		entityMap.put("oline",(char)8254);		//�������������
		entityMap.put("frasl",(char)8260);		//����� ������� �����
		entityMap.put("euro",(char)8364);
		entityMap.put("image",(char)8465);      //��������� ���������� I = ������ �����
		entityMap.put("weierp",(char)8472);     //���������� �������� P
		entityMap.put("real",(char)8476);       //���������� ��������� R = ������� �����	
		entityMap.put("trade",(char)8482);		//���� �������� �����
		entityMap.put("alefsym",(char)8501);    //������ ���� = ������ ������������� ������������	
		entityMap.put("larr",(char)8592);		//������� �����
		entityMap.put("uarr",(char)8593);		//������� �����
		entityMap.put("rarr",(char)8594);		//������� ������
		entityMap.put("darr",(char)8595);		//������� ����
		entityMap.put("harr",(char)8596);		//������� �����-������
		entityMap.put("crarr",(char)8629);      //������� ������� ����� ���� = ������� �������
		entityMap.put("lArr",(char)8656);       //������� ������� �����
		entityMap.put("uArr",(char)8657);       //������� ������� �����
		entityMap.put("rArr",(char)8658);       //������� ������� ������
		entityMap.put("dArr",(char)8659);       //������� ������� ����
		entityMap.put("hArr",(char)8660);       //������� ������� �����-������		
		entityMap.put("forall",(char)8704);     //��� ����
		entityMap.put("part",(char)8706);       //��������� ������������
		entityMap.put("exist",(char)8707);      //����������
		entityMap.put("empty",(char)8709);      //������ ���������
		entityMap.put("nabla",(char)8711);      //����� = ����� ��������
		entityMap.put("isin",(char)8712);       //������
		entityMap.put("notin",(char)8713);      //�� ������
		entityMap.put("ni",(char)8715);         //��������
		entityMap.put("prod",(char)8719);       //n-����� ������������ >> ��������� ��������� ����� pi
		entityMap.put("sum",(char)8721);        //n-����� ����������� >>��������� ��������� ����� sigma
		entityMap.put("minus",(char)8722);      //���� �����
		entityMap.put("lowast",(char)8727);     //�������� ���������
		entityMap.put("radic",(char)8730);      //���������� ������
		entityMap.put("prop",(char)8733);       //���������������
		entityMap.put("infin",(char)8734);      //�������������
		entityMap.put("ang",(char)8736);        //����
		entityMap.put("and",(char)8743);        //���������� �
		entityMap.put("or",(char)8744);         //���������� ���
		entityMap.put("cap",(char)8745);        //�����������
		entityMap.put("cup",(char)8746);        //�����������
		entityMap.put("int",(char)8747);        //��������
		entityMap.put("there4",(char)8756);     //�������������
		entityMap.put("sim",(char)8764);        //�������� ������ = �������������� ����� >> ������
		entityMap.put("cong",(char)8773);       //�������������� �����
		entityMap.put("asymp",(char)8776);      //����� ����� = �������������� �����	
		entityMap.put("ne",(char)8800);         //�� �����
		entityMap.put("equiv",(char)8801);      //��������� �
		entityMap.put("le",(char)8804);         //������ ��� �����
		entityMap.put("ge",(char)8805);         //������ ��� �����
		entityMap.put("sub",(char)8834);        //�������� �������������
		entityMap.put("sup",(char)8835);        //�������� �������������
		entityMap.put("nsub",(char)8836);       //�� �������� �������������
		entityMap.put("sube",(char)8838);       //�������� ������������� ��� ���������
		entityMap.put("supe",(char)8839);       //�������� ������������� ��� ���������
		entityMap.put("oplus",(char)8853);      //���� � ������ = ������ �����
		entityMap.put("otimes",(char)8855);     //�������� ��������� � ������ = ������������ ��������
		entityMap.put("perp",(char)8869);       //���������������
		entityMap.put("sdot",(char)8901);       //�������� �����
		entityMap.put("lceil",(char)8968);      //����� ������� ������
		entityMap.put("rceil",(char)8969);      //������ ������� ������
		entityMap.put("lfloor",(char)8970);     //����� ������ ������
		entityMap.put("rfloor",(char)8971);     //������ ������ ������	
		entityMap.put("lang",(char)9001);       //����� ������� ������
		entityMap.put("rang",(char)9002);       //������ ������� ������
		entityMap.put("loz",(char)9674);        //����	
		entityMap.put("spades",(char)9824);		//���� ����� '����'
		entityMap.put("clubs",(char)9827);		//���� ����� '�����' - shamrock
		entityMap.put("hearts",(char)9829);		//���� ����� '�����' - valentine
		entityMap.put("diams",(char)9830);		//���� ����� '�����'		
	}
	
	protected boolean externPrepareTAG() {
		return false;
	}

}
