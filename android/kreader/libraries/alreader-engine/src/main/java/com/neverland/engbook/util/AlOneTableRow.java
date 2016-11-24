package com.neverland.engbook.util;

import java.util.ArrayList;

public class AlOneTableRow {

    public int					                    shtamp = -10;
    public int					                    cell_accepted;
    public int					                    height;
    public final ArrayList<AlOneTableCell>	        cells = new ArrayList<>();
    public int					                    start;


    public int					                    cnt_pages = 0;
    public AlOnePage			                    pages[] = null;

    public void	addAllPages() {
        cnt_pages = cells.size();
        pages = new AlOnePage[cnt_pages];
        for (int i = 0; i < cnt_pages; i++) {
            if (cells.get(i).start >= 0) {
                pages[i] = new AlOnePage();
                AlOnePage.init(pages[i], InternalConst.TAL_PAGE_MODE.ROWS);
            }
        }
    }


}
