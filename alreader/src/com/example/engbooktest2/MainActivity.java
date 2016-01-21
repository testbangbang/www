package com.example.engbooktest2;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.neverland.engbook.bookobj.AlBookEng;
import com.neverland.engbook.bookobj.AlUtilFunc;
import com.neverland.engbook.forpublic.AlBitmap;
import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlEngineNotifyForUI;
import com.neverland.engbook.forpublic.AlPublicProfileOptions;
import com.neverland.engbook.forpublic.EngBookListener;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_GOTOCOMMAND;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_ID;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_RESULT;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;

public class MainActivity extends Activity implements EngBookListener {
	private AlBookEng 	bookEng = null;
	private MainApp 	appl = null;
	private MainView 	textViewer = null;
	
	private AlPublicProfileOptions profileCurrent = null;
	private AlPublicProfileOptions profileDay = new AlPublicProfileOptions();
	private AlPublicProfileOptions profileNight = new AlPublicProfileOptions();
	
	private AlBitmap 	backDay_bitmap = null;
	private AlBitmap 	backNight_bitmap = null;
	private AlBitmap 	error_bitmap = null;
	private AlBitmap 	table_bitmap = null; 
	private AlBitmap 	wait_bitmap = null;
	
	private AlBookOptions bookOpt = new AlBookOptions();

	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.main);
    	
    	appl = MainApp.getOurInstance();    	
    	bookEng = appl.getBookEngine();
    	
    	textViewer = (MainView)findViewById(R.id.mainText);
    	textViewer.assignPaintViewWithBookEng(bookEng);
    	
    	backDay_bitmap = AlUtilFunc.loadImageFromResources(appl.getGlobalResources(), R.drawable.backday);
    	profileDay.background = backDay_bitmap;
    	profileDay.bold = false;
    	profileDay.font_name = "Serif";
    	profileDay.font_monospace = "Monospace";
    	profileDay.font_size = 18 * appl.dpiMultiplex;
    	profileDay.margin = -5; // negative - in percent, positive - in pixels
    	profileDay.twoColumn = false;
    	profileDay.colorText = 0x000000;
    	profileDay.colorTitle = 0x9c27b0;
    	profileDay.colorBack = 0xf0f0f0;
    	profileDay.interline = 0;
    	
    	backNight_bitmap = AlUtilFunc.loadImageFromResources(appl.getGlobalResources(), R.drawable.backnight);
    	profileNight.background = backNight_bitmap;
    	profileNight.bold = false;
    	profileNight.font_name = "Serif";
    	profileNight.font_monospace = "Monospace";
    	profileNight.font_size = 18 * appl.dpiMultiplex;
    	profileNight.margin = -5;
    	profileNight.twoColumn = false;
    	profileNight.colorText = 0xe0ffe0;
    	profileNight.colorTitle = 0xcddc39;
    	profileNight.colorBack = 0x000000;
    	profileNight.interline = 0;
    	
    	error_bitmap = AlUtilFunc.loadImageFromResources(appl.getGlobalResources(), R.drawable.error);
    	table_bitmap = AlUtilFunc.loadImageFromResources(appl.getGlobalResources(), R.drawable.table);
    	wait_bitmap = AlUtilFunc.loadImageFromResources(appl.getGlobalResources(), R.drawable.wait);
    	bookEng.setServiceBitmap(error_bitmap, table_bitmap, wait_bitmap);
    	
    	profileCurrent = profileDay;
    	bookEng.setNewProfileParameters(profileCurrent);
    	
    	AlEngineNotifyForUI engUI = new AlEngineNotifyForUI();		
    	engUI.appInstance = appl;
    	engUI.hWND = this;				
    	bookEng.initializeOwner(engUI);
	}
	
	@Override
    public void onResume() {
    	super.onResume();
    	
    	int width = textViewer.getWidth();
    	int height = textViewer.getHeight();
    	bookEng.setNewScreenSize(width, height);
    }

	@Override
    public void onPause() {		
		
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		bookEng.freeOwner();
		super.onDestroy();
	};
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);    	
    	getMenuInflater().inflate(R.menu.mainmenu, menu);
    	return true; 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 
		if (item == null)
	    		return true;
	    	    		
		switch (item.getItemId()) {
		case R.id.mainmenu_open_file:
			bookOpt.codePage = TAL_CODE_PAGES.AUTO;
			bookOpt.codePageDefault = TAL_CODE_PAGES.CP1251;
			bookOpt.formatOptions = 0;
			bookOpt.readPosition = 0;
			bookEng.openBook("/sdcard/31.zip", bookOpt);
			return true;
		case R.id.mainmenu_page_next:
			bookEng.gotoPosition(TAL_GOTOCOMMAND.NEXTPAGE);
			return true;
		case R.id.mainmenu_page_prev:
			bookEng.gotoPosition(TAL_GOTOCOMMAND.PREVPAGE);
			return true;
		case R.id.mainmenu_file_close:
			bookEng.closeBook();
			return true;
		case R.id.mainmenu_file_debug:
			bookEng.createDebugFile("/sdcard/");
			return true;
		case R.id.mainmenu_file_find:
			bookEng.findText("[15]");
			return true;
		case R.id.mainmenu_profile_day:
			profileCurrent = profileDay;
	    	bookEng.setNewProfileParameters(profileCurrent);
			return true;
		case R.id.mainmenu_profile_night:
			profileCurrent = profileNight;
	    	bookEng.setNewProfileParameters(profileCurrent);
			return true;
		case R.id.mainmenu_profile_decfont:
			profileCurrent.font_size--;
			bookEng.setNewProfileParameters(profileCurrent);
			return true;
		case R.id.mainmenu_profile_incfont:
			profileCurrent.font_size++;
			bookEng.setNewProfileParameters(profileCurrent);
			return true;
		case R.id.mainmenu_profile_incinterline:
			profileCurrent.interline += 10;
			bookEng.setNewProfileParameters(profileCurrent);
			return true;
		case R.id.mainmenu_profile_decinterline:
			profileCurrent.interline -= 10;
			bookEng.setNewProfileParameters(profileCurrent);
			return true;
		case R.id.mainmenu_profile_bold:
			profileCurrent.bold  = !profileCurrent.bold;
			bookEng.setNewProfileParameters(profileCurrent);
			return true;	
		}
		return false;	 
	}
	
	@Override
	public void engBookGetMessage(TAL_NOTIFY_ID id, TAL_NOTIFY_RESULT result) {
		switch (id) {
		case CLOSEBOOK:
		case OPENBOOK:
			
		case FIND:
		case NEWCALCPAGES:
			
		case STARTTHREAD:
		case STOPTHREAD:
		case NEEDREDRAW:
			textViewer.invalidate();
			break;
		}
	}
}
