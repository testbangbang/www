package com.example.engbooktest2;

import com.neverland.engbook.bookobj.AlBookEng;
import com.neverland.engbook.forpublic.AlEngineNotifyForUI;
import com.neverland.engbook.forpublic.AlEngineOptions;
import com.neverland.engbook.forpublic.EngBookListener;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_HYPH_LANG;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_SCREEN_DPI;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_SCREEN_PAGES_COUNT;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class MainApp extends Application {
	public static MainApp ourInstance;
	
	private AlBookEng bookEng = null;
	private Resources globalRes = null;
	
	public int	dpiMultiplex = 1;
	
	public static MainApp getOurInstance() {
		return ourInstance;
	}
	
	public Resources getGlobalResources() {
		return globalRes;
	}

	public AlBookEng getBookEngine() {
		return bookEng;
	}
	
	public MainApp() {
		ourInstance = this;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	
	@Override
	public void onCreate() {
		super.onCreate();
		
		globalRes = getApplicationContext().getResources();
		
		bookEng = new AlBookEng();
		AlEngineOptions opt = new AlEngineOptions();
		opt.appInstance = ourInstance;
		opt.font_catalog = "/sdcard/fonts";
		opt.hyph_lang = TAL_HYPH_LANG.ENGRUS;
		opt.useScreenPages = TAL_SCREEN_PAGES_COUNT.SIZE;
		opt.pageSize4Use = AlEngineOptions.AL_USEAUTO_PAGESIZE;
		opt.chinezeFormatting = false;
		
		DisplayMetrics m = this.getResources().getDisplayMetrics();
		dpiMultiplex = 1;
		opt.DPI = TAL_SCREEN_DPI.TAL_SCREEN_DPI_160;		
		if (m.densityDpi >= 640) {
			dpiMultiplex = 2;
			opt.DPI = TAL_SCREEN_DPI.TAL_SCREEN_DPI_320;
		} else
		if (m.densityDpi >= 480) {
			dpiMultiplex = 3;
			opt.DPI = TAL_SCREEN_DPI.TAL_SCREEN_DPI_480;
		} else
		if (m.densityDpi >= 320) {
			dpiMultiplex = 4; 
			opt.DPI = TAL_SCREEN_DPI.TAL_SCREEN_DPI_320;
		} 
		
		bookEng.initializeBookEngine(opt);
	}	

	@Override
	public void onTerminate() {

		super.onTerminate();
	}

	
}
