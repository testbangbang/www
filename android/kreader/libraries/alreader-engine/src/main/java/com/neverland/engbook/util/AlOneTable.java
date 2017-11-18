package com.neverland.engbook.util;

import java.util.ArrayList;

public class AlOneTable {
    public static final int LEVEL2_TABLE_CELL_COLSPANNED = -1;
    public static final int LEVEL2_TABLE_CELL_ROWSPANNED = -2;
    public static final int LEVEL2_TABLE_CELL_ALIGNED = -3;

    public static final int LEVEL2_TABLE_ROW_HEIGHT_IFERROR = 3;

	public int			                        start = -1;
    public int			                        stop;
    public int		                            counter = 0;
    public int		                            crow = 0;

    public int				                    cntrow;
	public final ArrayList<AlOneTableRow>       rows = new ArrayList<>();

    public String		                        title = "Table";
    public int			                        startParagraph = 0;
    public int			                        startSize = 0;
    public boolean			                    isOneColumn = false;

    public void verifyIsOneColumn() {
        isOneColumn = true;
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).cells.size() > 1) {
                isOneColumn = false;
                break;
            }
        }
        //isOneColumn = !(rows.size() > 0 && rows.get(0).cells.size() > 1);
    }

	@Override
	public String toString() {
		return Integer.toString(start) + '/' + stop + '/' + (isOneColumn ? 'N' : 'Y');
	}

}
