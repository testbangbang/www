package com.neverland.engbook.util;

import android.content.res.AssetManager;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Typeface;

import com.neverland.engbook.forpublic.AlEngineOptions;
import com.neverland.engbook.forpublic.AlResourceFont;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;


public class AlFonts {
	private final ArrayList<AlOneFont> allfonts = new ArrayList<>();

	public ArrayList<AlOneFont> getFontList() {
		return allfonts;
	}

	private final HashMap<String, Integer> allfontsMaps = new HashMap<>();
	
	private final HashMap<Long, AlTypefaces> collTPF = new HashMap<>();
	private final FontMetricsInt font_metrics = new FontMetricsInt();
	private final static String SPACE_SPECIAL_STRCHAR = " ";
	private final static char SPACE_SPECIAL_CHAR = ' ';
	private final static String HYPH_SPECIAL_STRCHAR = "-";
	private final static char HYPH_SPECIAL_CHAR = '-';
	private final static String EM_SPECIAL_STRCHAR = "M";
	private final static char EM_SPECIAL_CHAR = 'M';

	private final static float FONT_STEP_MULTIPLE = 0.7f;
	/*public final static float FONT_STEP_MULTIPLE_P1 = FONT_STEP_MULTIPLE;
	public final static float FONT_STEP_MULTIPLE_P2 = FONT_STEP_MULTIPLE_P1 * FONT_STEP_MULTIPLE;
	public final static float FONT_STEP_MULTIPLE_P3 = FONT_STEP_MULTIPLE_P2 * FONT_STEP_MULTIPLE;
	public final static float FONT_STEP_MULTIPLE_P4 = FONT_STEP_MULTIPLE_P3 * FONT_STEP_MULTIPLE;
	public final static float FONT_STEP_MULTIPLE_P5 = FONT_STEP_MULTIPLE_P4 * FONT_STEP_MULTIPLE;
	public final static float FONT_STEP_MULTIPLE_P6 = FONT_STEP_MULTIPLE_P5 * FONT_STEP_MULTIPLE;
	public final static float FONT_STEP_MULTIPLE_P7 = FONT_STEP_MULTIPLE_P6 * FONT_STEP_MULTIPLE;
	public final static float FONT_STEP_MULTIPLE_P8 = FONT_STEP_MULTIPLE_P7 * FONT_STEP_MULTIPLE;

	public final static float FONT_STEP_MULTIPLE_M1 = 1 / FONT_STEP_MULTIPLE;
	public final static float FONT_STEP_MULTIPLE_M2 = FONT_STEP_MULTIPLE_M1 / FONT_STEP_MULTIPLE;
	public final static float FONT_STEP_MULTIPLE_M3 = FONT_STEP_MULTIPLE_M2 / FONT_STEP_MULTIPLE;
	public final static float FONT_STEP_MULTIPLE_M4 = FONT_STEP_MULTIPLE_M3 / FONT_STEP_MULTIPLE;
	public final static float FONT_STEP_MULTIPLE_M5 = FONT_STEP_MULTIPLE_M4 / FONT_STEP_MULTIPLE;
	public final static float FONT_STEP_MULTIPLE_M6 = FONT_STEP_MULTIPLE_M5 / FONT_STEP_MULTIPLE;
	public final static float FONT_STEP_MULTIPLE_M7 = FONT_STEP_MULTIPLE_M6 / FONT_STEP_MULTIPLE;
	public final static float FONT_STEP_MULTIPLE_M8 = FONT_STEP_MULTIPLE_M7 / FONT_STEP_MULTIPLE;*/


	private AssetManager assetManager;

	private float multiplexer = 1;
	private AlCalc calc = null;
	private AlPaintFont fparam;
	public void init(AlEngineOptions opt, AlCalc c, AlPaintFont	fontparam) {
		calc = c;
		fparam = fontparam;
        assetManager = opt.appInstance.getResources().getAssets();
		loadAllFonts(opt.font_catalog, opt.font_catalogs_addon, opt.font_resource);
		multiplexer = opt.multiplexer;
	}

	public boolean isLoaded(String fontName) {
		return allfontsMaps.get(fontName) != null;
	}

	public void addNewFont(TTFInfo ttfInfo, File file) {
		addToCollection(ttfInfo, file);
		allfontsMaps.clear();
		for (int i = 0; i < allfonts.size(); i++)
			allfontsMaps.put(allfonts.get(i).aName, i);
	}

	public void	modifyPaint(long				old_style,
		    long				new_style, 
			AlProfileOptions	profile,
			boolean needDraw) {
		
		boolean modify = false;

		int fnt_num = (int) ((new_style & AlStyles.SL_FONT_MASK) >> AlStyles.SL_FONT_SHIFT);
		int text_size = (int) (profile.font_sizes[fnt_num] * profile.multiplexer);
		boolean needCorrectItalic = false;
		
		fparam.style = new_style & (AlStyles.LMASK_REAL_FONT | AlStyles.LMASK_PAINT_FONT);
		
		long tns = new_style & AlStyles.LMASK_REAL_FONT; 
		long tos = old_style & AlStyles.LMASK_REAL_FONT;		
		if (tns != tos) {

			AlTypefaces mtpf = getTPF(tns, profile);
						
			
			/*int flags = 0;
			flags |= (PrefManager.getInt(R.string.keyoptuser_image) & 0x0040) != 0 ? Paint.DITHER_FLAG : 0;
			flags |= (PrefManager.getInt(R.string.keyoptuser_image) & 0x0080) != 0 ? Paint.SUBPIXEL_TEXT_FLAG : 0;
			flags |= (PrefManager.getInt(R.string.keyoptuser_image) & 0x0100) != 0 ? Paint.LINEAR_TEXT_FLAG : 0;
			flags |= (PrefManager.getInt(R.string.keyoptuser_image) & 0x0200) != 0 ? Paint.DEV_KERN_TEXT_FLAG : 0;
			if (AlApp.IS_API >= 14)
				flags |= (PrefManager.getInt(R.string.keyoptuser_image) & 0x2000) != 0 ? 0x01Paint.HINTING_ON : 0;
			
			paint.setFlags(flags);*/
			
			calc.fontPaint.setTypeface(mtpf.tpf);
			calc.fontPaint.setTextScaleX(profile.font_widths[fnt_num] / 100f);
			calc.fontPaint.setAntiAlias(profile.useCT || profile.font_widths[fnt_num] != 0);

			calc.fontPaint.setTextSkewX(mtpf.emul_italic ? -0.25f : 0.0f);
			calc.fontPaint.setFakeBoldText(mtpf.emul_bold);
			
			if (mtpf.emul_italic)
				needCorrectItalic = true;
			
			modify = true;
		}
			
		tns = new_style & AlStyles.LMASK_PAINT_FONT;
		tos = old_style & AlStyles.LMASK_PAINT_FONT;
		if (tns != tos || modify) {


			long kSize = new_style & AlStyles.SL_SIZE_MASK;
			if (kSize < AlStyles.SL_SIZE_NORMAL) {
				kSize >>= AlStyles.SL_SIZE_SHIFT;
				text_size *= (100 - ((100 - kSize) * FONT_STEP_MULTIPLE)) / 100.0;
			} else
			if (kSize > AlStyles.SL_SIZE_NORMAL) {
				kSize >>= AlStyles.SL_SIZE_SHIFT;
				text_size *= (100 + ((kSize - 100) * FONT_STEP_MULTIPLE)) / 100.0;
			}

			if ((new_style & (AlStyles.STYLE_SUB | AlStyles.STYLE_SUP)) != 0)
				text_size = text_size * 7 / 10;
			
			if (text_size < 3)
				text_size = 3;
			if (text_size > 300)
				text_size = 300;

			if (profile.classicFirstLetter && (new_style & AlStyles.SL_MARKFIRTSTLETTER0) != 0)
				text_size *= 2;

			modify = true;
		}
			
			
		if (modify) {
			calc.fontPaint.setTextSize(text_size);

			calc.fontPaint.getFontMetricsInt(font_metrics);
			
			if (fparam.style == 0) {
				if (calc.mainWidth[SPACE_SPECIAL_CHAR] == AlCalc.UNKNOWNWIDTH)
					calc.mainWidth[SPACE_SPECIAL_CHAR] = (char) (calc.fontPaint.measureText(SPACE_SPECIAL_STRCHAR));
				fparam.space_width_current = calc.mainWidth[SPACE_SPECIAL_CHAR];

				if (calc.mainWidth[HYPH_SPECIAL_CHAR] == AlCalc.UNKNOWNWIDTH)
					calc.mainWidth[HYPH_SPECIAL_CHAR] = (char) calc.fontPaint.measureText(HYPH_SPECIAL_STRCHAR);
				fparam.hyph_width_current = calc.mainWidth[HYPH_SPECIAL_CHAR];

				if (calc.mainWidth[EM_SPECIAL_CHAR] == AlCalc.UNKNOWNWIDTH)
					calc.mainWidth[EM_SPECIAL_CHAR] = (char) text_size;//(char) calc.fontPaint.measureText(EM_SPECIAL_STRCHAR);
				fparam.em_width_current = calc.mainWidth[EM_SPECIAL_CHAR];
			} else {
				fparam.space_width_current = (int) calc.fontPaint.measureText(SPACE_SPECIAL_STRCHAR);
				fparam.hyph_width_current = (int) calc.fontPaint.measureText(HYPH_SPECIAL_STRCHAR);
				fparam.em_width_current = text_size;//(int) calc.fontPaint.measureText(EM_SPECIAL_STRCHAR);
			}
			
			fparam.space_width_standart = fparam.space_width_current;
			
			if (fnt_num == 0) {
				fparam.space_width_current *= profile.font_space;
				if (fparam.space_width_current < 2)
					fparam.space_width_current = 2;
			}

			
			if (new_style == AlStyles.SL_SIZE_NORMAL) {
				/*if (PrefManager.font_height_asc) {
					param.height = (int)(font_metrics.descent - font_metrics.ascent + font_metrics.leading + 0.5f);
					param.def_line_down = (int) font_metrics.descent;
				} else {*/
					fparam.height = (int) (font_metrics.bottom - font_metrics.top + font_metrics.leading + 0.5f);
					fparam.def_line_down = font_metrics.bottom;
				//}
				
				fparam.space_width = fparam.space_width_current;
				fparam.hyph_width = fparam.hyph_width_current;
				fparam.em_width = fparam.em_width_current;
				fparam.def_reserv = 0;
			}
			
			/*if (PrefManager.font_height_asc) {
				param.base_line_up = (int)(font_metrics.leading - font_metrics.ascent + 0.5f);
				param.base_line_down = (int)(font_metrics.descent + 0.5f);
			} else {*/
				fparam.base_line_up = (int) (font_metrics.leading - font_metrics.top + 0.5f);
				fparam.base_line_down = (int) (font_metrics.bottom + 0.5f);
			//}
			
			fparam.base_ascent = (int) (-font_metrics.ascent + 0.5f);
			
			fparam.correct_italic = 0;
			if (needCorrectItalic)
				 fparam.correct_italic = fparam.height / 7;
			
			/*if (fparam.height < text_size && (tns & AlStyles.SL_MARKFIRTSTLETTER) == 0) {
				float m = (float)text_size / fparam.height;
				
				if (new_style == 0) {
					fparam.height *= m;
					fparam.def_line_down *= m;
				}
				
				fparam.base_line_up *= m;
				fparam.base_line_down *=m;				
				fparam.base_ascent *= m;
			}*/

		}

		fparam.unvisible_text = (new_style & AlStyles.SL_COLOR_MASK) == AlStyles.SL_COLOR_NOVISIBLE;
		fparam.color = profile.colors[(int)((new_style & AlStyles.SL_COLOR_MASK) >> AlStyles.SL_COLOR_SHIFT)] | 0xff000000;
		calc.fontPaint.setColor(fparam.color);
		calc.fontPaint.setStrikeThruText((new_style & AlStyles.STYLE_STRIKE) != 0);
			
		if (needDraw) {
			/*if ((new_style & AlStyles.SL_SHADOW) != 0) {	
				fparam.fontPaint.setShadowLayer(1.5f * profile.DPIMultiplex, profile.DPIMultiplex, profile.DPIMultiplex,
						profile.colors[InternalConst.TAL_PROFILE_COLOR_SHADOW] | 0xff000000);
			} else {*/
				switch (profile.font_weigths[fnt_num]) {
				case 1:  calc.fontPaint.setShadowLayer(0.03f * multiplexer, 0, 0, fparam.color); break;
				case 2:  calc.fontPaint.setShadowLayer(0.07f * multiplexer, 0, 0, fparam.color); break;
				case 3:  calc.fontPaint.setShadowLayer(0.11f * multiplexer, 0, 0, fparam.color); break;
				case 4:  calc.fontPaint.setShadowLayer(0.17f * multiplexer, 0, 0, fparam.color); break;
				case 5:	 calc.fontPaint.setShadowLayer(0.25f * multiplexer, 0, 0, fparam.color); break;
				case 6:  calc.fontPaint.setShadowLayer(0.40f * multiplexer, 0, 0, fparam.color); break;
				case 7:  calc.fontPaint.setShadowLayer(0.65f * multiplexer, 0, 0, fparam.color); break;
				default: calc.fontPaint.clearShadowLayer();
				}
			//}
		}
	}
	
	private AlTypefaces addTPF(long style, AlProfileOptions	profile) {
		long st0 = style & (Typeface.BOLD_ITALIC);
		long tmp = (style & AlStyles.SL_FONT_MASK) >> AlStyles.SL_FONT_SHIFT;
		
		long st1 = (profile.font_bold[(int) tmp] ? Typeface.BOLD : 0) |
				  (profile.font_italic[(int) tmp] ? Typeface.ITALIC : 0);
		
		if (profile.style_summ) {
			st0 |= st1;
		} else {
			st0 ^= st1;
		}
		
		//String s = profile.font_names[(int) tmp].toLowerCase();
		String s = profile.font_names[(int) tmp];

		AlTypefaces typeface = null;

		int i;
		Integer iFont = allfontsMaps.get(s);
		if (iFont == null) i = 0; else i = iFont;

		switch (i) {
		case 0:
			typeface = new AlTypefaces();
			typeface.tpf = Typeface.create(Typeface.SANS_SERIF, (int) st0);
			break;
		case 1:
			typeface = new AlTypefaces();
			typeface.tpf = Typeface.create(Typeface.SERIF, (int) (st0 & 0x01));
			if ((st0 & 0x02) != 0)
				typeface.emul_italic = true;
			break;
		case 2:
			typeface = new AlTypefaces();
			typeface.tpf = Typeface.create(Typeface.MONOSPACE, 0);
			if ((st0 & 0x02) != 0)
				typeface.emul_italic = true;
			if ((st0 & 0x01) != 0)
				typeface.emul_bold = true;
			break;
		default:
			int j;
			boolean ebold = false;
			boolean eitalic = false;
			AlOneFont fi = allfonts.get(iFont);
			if (fi.res != null) {
				String f = null;
				switch ((int) st0) {
				case 0:
					f = fi.res.aFile[0];
					break;
				case 1:
					f = fi.res.aFile[1];
					if (f == null) {
						f = fi.res.aFile[0];
						ebold = true;
					}
					break;
				case 2:
					f = fi.res.aFile[2];
					if (f == null) {
						f = fi.res.aFile[0];
						eitalic = true;
					}
					break;
				case 3:
					f = fi.res.aFile[3];
					if (f == null) {
						if (fi.res.aFile[2] != null) {
							f = fi.res.aFile[2];
							ebold = true;
						} else if (fi.res.aFile[1] != null) {
							f = fi.res.aFile[1];
							eitalic = true;
						} else {
							f = fi.res.aFile[0];
							eitalic = true;
							ebold = true;
						}
					}
					break;
				}

				if (f == null) {
					for (j = 0; j < 4; j++)
						if (fi.res.aFile[j] != null)
							f = fi.res.aFile[j];
				}

				try {
					if (f != null) {
						typeface = new AlTypefaces();
						typeface.tpf = Typeface.createFromAsset(assetManager, f);
						typeface.emul_bold = ebold;
						typeface.emul_italic = eitalic;
						if (typeface.tpf != null) {
							return typeface;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				File f = null;
				switch ((int) st0) {
				case 0:
					f = fi.aFile[0];
					break;
				case 1:
					f = fi.aFile[1];
					if (f == null) {
						f = fi.aFile[0];
						ebold = true;
					}
					break;
				case 2:
					f = fi.aFile[2];
					if (f == null) {
						f = fi.aFile[0];
						eitalic = true;
					}
					break;
				case 3:
					f = fi.aFile[3];
					if (f == null) {
						if (fi.aFile[2] != null) {
							f = fi.aFile[2];
							ebold = true;
						} else if (fi.aFile[1] != null) {
							f = fi.aFile[1];
							eitalic = true;
						} else {
							f = fi.aFile[0];
							eitalic = true;
							ebold = true;
						}
					}
					break;
				}

				if (f == null) {
					for (j = 0; j < 4; j++)
						if (fi.aFile[j] != null)
							f = fi.aFile[j];
				}

				try {
					if (f != null) {
						typeface = new AlTypefaces();
						typeface.tpf = Typeface.createFromFile(f);
						typeface.emul_bold = ebold;
						typeface.emul_italic = eitalic;
						if (typeface.tpf != null) {
							return typeface;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}

		return typeface;
	}


	/*private AlTypefaces addTPF(long style, AlProfileOptions	profile) {
		long st0 = style & (Typeface.BOLD_ITALIC);
		long tmp = (style & AlStyles.SL_FONT_MASK) >> AlStyles.SL_FONT_SHIFT;

		long st1 = (profile.font_bold[(int) tmp] ? Typeface.BOLD : 0) |
				  (profile.font_italic[(int) tmp] ? Typeface.ITALIC : 0);

		if (profile.style_summ) {
			st0 |= st1;
		} else {
			st0 ^= st1;
		}

		String s = profile.font_names[(int) tmp].toLowerCase();

		AlTypefaces typeface = null;
		if (s.contentEquals("serif")) {
			typeface = new AlTypefaces();
			typeface.tpf = Typeface.create(s, (int) st0);
		} else
		if (s.contentEquals("sans-serif")) {
			typeface = new AlTypefaces();
			typeface.tpf = Typeface.create(s, (int) (st0 & 0x01));
			if ((st0 & 0x02) != 0)
				typeface.emul_italic = true;
		} else
		if (s.contentEquals("monospace")) {
			typeface = new AlTypefaces();
			typeface.tpf = Typeface.create(s, 0);
			if ((st0 & 0x02) != 0)
				typeface.emul_italic = true;
			if ((st0 & 0x01) != 0)
				typeface.emul_bold = true;
		} else {
			int i, j;
			boolean ebold = false;
			boolean eitalic = false;

			AlOneFont fi;
			for (i = 3; i < allfonts.size(); i++) {
				fi = allfonts.get(i);
				if (fi.aName.equalsIgnoreCase(s)) {

                    if (fi.res != null) {
                        String f = null;
                        switch ((int) st0) {
                            case 0:
                                f = fi.res.aFile[0];
                                break;
                            case 1:
                                f = fi.res.aFile[1];
                                if (f == null) {
                                    f = fi.res.aFile[0];
                                    ebold = true;
                                }
                                break;
                            case 2:
                                f = fi.res.aFile[2];
                                if (f == null) {
                                    f = fi.res.aFile[0];
                                    eitalic = true;
                                }
                                break;
                            case 3:
                                f = fi.res.aFile[3];
                                if (f == null) {
                                    if (fi.res.aFile[2] != null) {
                                        f = fi.res.aFile[2];
                                        ebold = true;
                                    } else if (fi.res.aFile[1] != null) {
                                        f = fi.res.aFile[1];
                                        eitalic = true;
                                    } else {
                                        f = fi.res.aFile[0];
                                        eitalic = true;
                                        ebold = true;
                                    }
                                }
                                break;
                        }

                        if (f == null) {
                            for (j = 0; j < 4; j++)
                                if (fi.res.aFile[j] != null)
                                    f = fi.res.aFile[j];
                        }

                        try {
                            if (f != null) {
                                typeface = new AlTypefaces();
                                typeface.tpf = Typeface.createFromAsset(assetManager, f);
                                typeface.emul_bold = ebold;
                                typeface.emul_italic = eitalic;
                                if (typeface.tpf != null) {
                                    return typeface;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        File f = null;
                        switch ((int) st0) {
                            case 0:
                                f = fi.aFile[0];
                                break;
                            case 1:
                                f = fi.aFile[1];
                                if (f == null) {
                                    f = fi.aFile[0];
                                    ebold = true;
                                }
                                break;
                            case 2:
                                f = fi.aFile[2];
                                if (f == null) {
                                    f = fi.aFile[0];
                                    eitalic = true;
                                }
                                break;
                            case 3:
                                f = fi.aFile[3];
                                if (f == null) {
                                    if (fi.aFile[2] != null) {
                                        f = fi.aFile[2];
                                        ebold = true;
                                    } else if (fi.aFile[1] != null) {
                                        f = fi.aFile[1];
                                        eitalic = true;
                                    } else {
                                        f = fi.aFile[0];
                                        eitalic = true;
                                        ebold = true;
                                    }
                                }
                                break;
                        }

                        if (f == null) {
                            for (j = 0; j < 4; j++)
                                if (fi.aFile[j] != null)
                                    f = fi.aFile[j];
                        }

                        try {
                            if (f != null) {
                                typeface = new AlTypefaces();
                                typeface.tpf = Typeface.createFromFile(f);
                                typeface.emul_bold = ebold;
                                typeface.emul_italic = eitalic;
                                if (typeface.tpf != null) {
                                    return typeface;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
					break;
				}
			}
			typeface = new AlTypefaces();
			typeface.tpf = Typeface.create("serif", (int) st0);
		}

		return typeface;
	}*/



	
	private AlTypefaces getTPF(long style, AlProfileOptions profile) {
		AlTypefaces tf = collTPF.get(style & (AlStyles.SL_FONT_MASK | AlStyles.STYLE_BOLD | AlStyles.STYLE_ITALIC));
		if (tf == null) {
			tf = addTPF(style, profile);
			collTPF.put(style & (AlStyles.SL_FONT_MASK | AlStyles.STYLE_BOLD | AlStyles.STYLE_ITALIC), tf);
		}		
		return tf;
	}
	
	public final void clearFontCache() {
		collTPF.clear();
	}
	
	private void addToCollection(TTFInfo ttfi, File f) {
		if (ttfi == null || ttfi.Name == null)
			return;
		
		if (ttfi.Name.equalsIgnoreCase("droid sans") ||
				ttfi.Name.equalsIgnoreCase("droid serif") ||
				ttfi.Name.equalsIgnoreCase("droid sans mono"))
			return;
		
		AlOneFont fi = null;
		int i;
		for (i = 3; i < allfonts.size(); i++) {
			fi = allfonts.get(i);
			if (fi.aName.equalsIgnoreCase(ttfi.Name)) {
				AlOneFont.addFontInfo(fi, ttfi.Type, f);
				return;
			}
		}
		allfonts.add(new AlOneFont(ttfi.Name, ttfi.Type, f));
	}

	private void reinitExtPath(String path, final boolean notUseDroid) {
		try {
		File ff = new File(path);
		if (ff.isDirectory() && ff.exists()) {
			File[] fileList = ff.listFiles(
				new FilenameFilter() {
					public boolean accept(File dir, String name) {
						if (name.startsWith("."))
							return false;
						final String fontname = name.toLowerCase();
						
						if (fontname.endsWith("fallback.ttf"))
							return false;
						
						if (notUseDroid) {
							if (fontname.equalsIgnoreCase("droidsans.ttf"))
								return false;
							if (fontname.equalsIgnoreCase("droidsans-bold.ttf"))
								return false;
							if (fontname.equalsIgnoreCase("droidsansmono.ttf"))
								return false;
							if (fontname.equalsIgnoreCase("droidserif-regular.ttf"))
								return false;
							if (fontname.equalsIgnoreCase("droidserif-bold.ttf"))
								return false;
							if (fontname.equalsIgnoreCase("droidserif-italic.ttf"))
								return false;
							if (fontname.equalsIgnoreCase("droidserif-bolditalic.ttf"))
								return false;
						}
						
						return fontname.endsWith(".ttf") || fontname.endsWith(".otf");
					}
				}
			);
			if (fileList != null) {
				boolean flagInsert;
				int i, j, k;
				AlOneFont fi;
				for (i = 0; i < fileList.length; i++) {
					flagInsert = true;
					for (j = 3; j < allfonts.size(); j++) {
						fi = allfonts.get(j);
						for (k = 0; k < 4; k++)
							if (fi.aFile[k] != null && 
									fileList[i].getAbsolutePath().equalsIgnoreCase(fi.aFile[k].getAbsolutePath())) {
								flagInsert = false;
							}
					}
					if (flagInsert)
						addToCollection(TTFScan.getTTFInfo(fileList[i], false), fileList[i]);					
				}
			}
		}
		} catch (Exception e) {
			
		}
	}

	private int	internalFontCount = 0;

	private void loadAllFonts(String mainPath, String[] addPaths, AlResourceFont resFont[]) {
		if (internalFontCount != 0)
			return;
		
		allfonts.add(new AlOneFont("Sans-Serif", 0, null));
		allfonts.add(new AlOneFont("Serif", 0, null));
		allfonts.add(new AlOneFont("Monospace", 0, null));

		if (resFont != null) {
            for (int i = 0; i < resFont.length; i++)
                allfonts.add(new AlOneFont(resFont[i]));
		}

		reinitExtPath("/system/fonts", true);		
		internalFontCount = allfonts.size();

		if (mainPath != null)
			reinitExtPath(mainPath, false);
		if (addPaths != null && addPaths.length > 0)
			for (String item : addPaths) {
				reinitExtPath(item, false);
			}

		for (int i = 0; i < allfonts.size(); i++)
			allfontsMaps.put(allfonts.get(i).aName, i);
	}
	
}
