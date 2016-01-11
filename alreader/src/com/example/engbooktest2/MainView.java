package com.example.engbooktest2;

import com.neverland.engbook.bookobj.AlBookEng;
import com.neverland.engbook.forpublic.AlBitmap;
import com.neverland.engbook.forpublic.AlIntHolder;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_PAGE_INDEX;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MainView extends View {

	private AlBookEng bookEng = null;
	private Context mcontext;
	private Paint paint = new Paint();
	
	
	public MainView(Context context, AttributeSet attrs) {		
		super(context, attrs);
		mcontext = context; 
		
		paint.setColor(0xffff0000);
		paint.setTextSize(15);
		
	}
	
	public MainView(Context context) {
		this(context, null);
	}

	public void assignPaintViewWithBookEng(AlBookEng eng) {
		bookEng = eng;	
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int width = this.getWidth();
		int height = this.getHeight();
	
		if (bookEng != null) {
			bookEng.setNewScreenSize(width, height);
			AlBitmap bmp = bookEng.getPageBitmap(TAL_PAGE_INDEX.CURR, width, height);
			if (bmp != null) {
				canvas.drawBitmap(bmp.bmp, 0, 0, null);
				
				AlIntHolder currPage = new AlIntHolder(0), allPage = new AlIntHolder(0), readPosition = new AlIntHolder(0);
				if (bookEng.getPageCount(currPage, allPage, readPosition) == TAL_RESULT.OK) {
					canvas.drawText("pages " + currPage.toString() + '/' + allPage.toString() + " position " + readPosition.toString(), 5, height - 5, paint);
				}
				bmp = null;
			}			
		}
	}

}
