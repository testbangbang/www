package com.example.engbooktest2;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;

import com.example.engbooktest2.R;
import com.neverland.engbook.bookobj.AlBookEng;
import com.neverland.engbook.bookobj.AlUtilFunc;
import com.neverland.engbook.forpublic.AlBitmap;
import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlBookProperties;
import com.neverland.engbook.forpublic.AlEngineNotifyForUI;
import com.neverland.engbook.forpublic.AlOneContent;
import com.neverland.engbook.forpublic.AlPublicProfileOptions;
import com.neverland.engbook.forpublic.EngBookListener;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_GOTOCOMMAND;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_ID;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_RESULT;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_SCREEN_SELECTION_MODE;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;

import java.util.ArrayList;

public class MainActivity extends Activity implements EngBookListener {
    private AlBookEng bookEng = null;
    private MainApp appl = null;
    private MainView textViewer = null;

    private AlPublicProfileOptions profileCurrent = null;
    private AlPublicProfileOptions profileDay = new AlPublicProfileOptions();
    private AlPublicProfileOptions profileNight = new AlPublicProfileOptions();

    private AlBitmap backDay_bitmap = null;
    private AlBitmap backNight_bitmap = null;
    private AlBitmap error_bitmap = null;
    private AlBitmap table_bitmap = null;
    private AlBitmap wait_bitmap = null;

    private AlBookOptions bookOpt = new AlBookOptions();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        appl = MainApp.getOurInstance();
        bookEng = appl.getBookEngine();

        textViewer = (MainView) findViewById(R.id.mainText);
        textViewer.assignPaintViewWithBookEng(bookEng);

        backDay_bitmap = AlUtilFunc.loadImageFromResources(appl.getGlobalResources(), R.drawable.backday);
        profileDay.background = backDay_bitmap;
        profileDay.backgroundMode = AlPublicProfileOptions.BACK_TILE_Y | AlPublicProfileOptions.BACK_TILE_X;
        profileDay.bold = false;
        profileDay.font_name = "Serif";
        profileDay.font_monospace = "Monospace";
        profileDay.font_size = 18 * appl.dpiMultiplex;
        profileDay.setMargins(5); // in percent
        profileDay.twoColumn = false;
        profileDay.colorText = 0x000000;
        profileDay.colorTitle = 0x9c27b0;
        profileDay.colorBack = 0xf0f0f0;
        profileDay.interline = 0;
        profileDay.specialModeRoll = true;
        profileDay.sectionNewScreen = true;
        profileDay.justify = true;
        profileDay.notesOnPage = true;

        backNight_bitmap = AlUtilFunc.loadImageFromResources(appl.getGlobalResources(), R.drawable.backnight);
        profileNight.background = backNight_bitmap;
        profileNight.bold = false;
        profileNight.font_name = "Serif";
        profileNight.font_monospace = "Monospace";
        profileNight.font_size = 18 * appl.dpiMultiplex;
        profileNight.setMargins(5); // in percent
        profileNight.twoColumn = false;
        profileNight.colorText = 0xe0ffe0;
        profileNight.colorTitle = 0xcddc39;
        profileNight.colorBack = 0x000000;
        profileNight.interline = 0;
        profileNight.specialModeRoll = false;
        profileNight.sectionNewScreen = true;
        profileNight.justify = true;
        profileNight.notesOnPage = true;

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

    public final static int REQUEST_PERMISSION = 1000;

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
            {
                if (grantResults.length > 0) {


                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //mAsyncTaskManager.setupTask(new Task(Task.TASK_LOADLASTBOOK), null);
                        //showDialog(IDD_EXITAFTERPERMISSIONYES);
                    } else {
                        //showDialog(IDD_EXITAFTERPERMISSIONNO);
                    }
                }

                return;
            }
        }
    }


    public final boolean verifyPermission() {
        boolean permissionStorage = ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (!permissionStorage) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

        return permissionStorage;
    }


    @Override
    public void onStart() {
        super.onStart();
        verifyPermission();
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        bookEng.freeOwner();
        super.onDestroy();
    }

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

        ArrayList<AlOneContent> a;

        switch (item.getItemId()) {
            case R.id.mainmenu_open_file:
                bookOpt.codePage = TAL_CODE_PAGES.AUTO;
                bookOpt.codePageDefault = TAL_CODE_PAGES.CP1251;
                bookOpt.formatOptions = 0;
                bookOpt.readPosition = 0;
//                bookEng.openBook("/sdcard/81.zip", bookOpt);
                bookEng.openBook(Environment.getExternalStorageDirectory().getPath() + "/readerData/87.zip", bookOpt);
                //bookEng.openBook("/sdcard/67.epub", bookOpt);
                //bookEng.openBook("/sdcard/BSE.epub", bookOpt);
                //bookEng.openBook("/sdcard/18.zip", bookOpt);
                return true;
            case R.id.mainmenu_page_next:
                bookEng.gotoPosition(TAL_GOTOCOMMAND.NEXTPAGE, 0);
                return true;
            case R.id.mainmenu_page_prev:
                bookEng.gotoPosition(TAL_GOTOCOMMAND.PREVPAGE, 0);
                return true;
            case R.id.mainmenu_file_close:
                bookEng.closeBook();
                return true;
            case R.id.mainmenu_file_debug:
                bookEng.createDebugFile("/sdcard/");
                return true;
            case R.id.mainmenu_file_find:
                bookEng.findText("[11]");
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
                profileCurrent.bold = !profileCurrent.bold;
                bookEng.setNewProfileParameters(profileCurrent);
                return true;

            case R.id.mainmenu_select_start:
                bookEng.setSelectionMode(TAL_SCREEN_SELECTION_MODE.START);
                return true;
            case R.id.mainmenu_select_end:
                bookEng.setSelectionMode(TAL_SCREEN_SELECTION_MODE.END);
                return true;
            case R.id.mainmenu_select_gettext:
                String res = bookEng.getSelectedText();
                bookEng.setSelectionMode(TAL_SCREEN_SELECTION_MODE.NONE);
                return true;
            case R.id.mainmenu_select_dict:
                bookEng.setSelectionMode(TAL_SCREEN_SELECTION_MODE.DICTIONARY);
                return true;
            case R.id.mainmenu_select_clear:
                bookEng.setSelectionMode(TAL_SCREEN_SELECTION_MODE.NONE);
                return true;
        }
        return false;
    }

    AlBookProperties a;
    @Override
    public void engBookGetMessage(TAL_NOTIFY_ID id, TAL_NOTIFY_RESULT result) {

        switch (id) {
            case OPENBOOK:
                a  = bookEng.getBookProperties();

            case CLOSEBOOK:

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
