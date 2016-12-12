package com.neverland.engbook.util;

import com.neverland.engbook.forpublic.AlBitmap;
import com.neverland.engbook.forpublic.AlIntHolder;

import android.graphics.Bitmap;
import android.graphics.Canvas;



public class EngBitmap {

	public static boolean reCreateBookBitmap(AlBitmap bmp, int newWidth, int newHeight, AlIntHolder shtamp) {

		// ������� ������� ������ � ����� ������� ������ ���� ������ 4-��,
		//�.�. ���� ����� �������� 3 �� 3 ������� - ���������� 4 �� 4
		final int rW = (newWidth + 0x03) & 0xfffc;
		final int rH = (newHeight + 0x03) & 0xfffc;

		// ���� ����� � ������ ������� ��������� - ������ �� ������
		if (bmp.width == rW && bmp.height == rH)
			return true;

		// ���������� � ���� ��������� ����� �������� ������� ��������
		bmp.width = rW;
		bmp.height = rH;

		// ����������� ������, ���� ������ ��� ��� ������ �����
		if (bmp.bmp != null) {
			bmp.bmp.recycle();
			bmp.bmp = null;
			bmp.canvas = null;
			System.gc();
		}
		
		bmp.bmp = null;
		bmp.canvas = null;

		// ���� ������ ����� 0 - ����� ������ �� �����
		if (newWidth == 0)
			return true;

		// ��������� ��� ����
		if (shtamp != null)
			shtamp.value++;

		// ����� ������ ��������� ��������� ��� - �� ������ bmp->bmp ������ ���� ������� ����� ������
		// 32 ���� �� �����		
		try {
			bmp.bmp = Bitmap.createBitmap(rW, rH, Bitmap.Config.ARGB_8888);
			bmp.canvas = new Canvas(bmp.bmp);
			bmp.canvas.drawColor(0x00000000);
		} catch (Exception e) {
			bmp.bmp = null;
			bmp.canvas = null;
		}

		if (bmp.bmp == null || bmp.canvas == null) {
			if (bmp.bmp != null)
				bmp.bmp.recycle();
			bmp.bmp = null;
			bmp.canvas = null;
			System.gc();
			return false;
		}

		// ����� ���� ��� ��

		return true;
	}
}
