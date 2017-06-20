package com.neverland.engbook.util;

import java.util.ArrayList;

public class AlPagePositionStack {
	public int				start;
	public int				back;

	public static AlPagePositionStack add(int s, int b) {
		AlPagePositionStack a = new AlPagePositionStack();
		a.start = s;
		a.back = b;
		return a;
	}

	public static void	addBackPage(ArrayList<AlPagePositionStack> arr, int pos, int b) {
		for (int i = 0; i < arr.size(); i++) 
			if (arr.get(i).start == pos)
				return;
		arr.add(add(pos, b));
	}

	public static int getBackPage(ArrayList<AlPagePositionStack> arr, int pos) {
		for (int i = 0; i < arr.size(); i++) 
			if (arr.get(i).start == pos)
				return arr.get(i).back;
		return -1;
	}
}