package com.onyx.android.sun.view;

/**
 * Created by zhuzeng on 9/3/14.
 */
public class GPaginator {
    private int rows;
    private int columns;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    private int currentPage = 0;
    private int size;

    public GPaginator() {
        resize(1, 1, 0);
    }

    public GPaginator(int r, int c, int s) {
        resize(r, c, s);
    }

    public void resize(int newRows, int newColumns, int newSize) {
        rows = newRows;
        columns = newColumns;
        size = newSize;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getSize() {
        return size;
    }

    public int itemsPerPage() {
        int itemsPerPage = rows * columns;
        if (itemsPerPage <= 0) {
            itemsPerPage = 1;
        }
        return itemsPerPage;
    }

    public int itemsInCurrentPage() {
        int items = itemsPerPage();
        if (currentPage < lastPage()) {
            return items;
        }
        int n = size % items;
        return n != 0 ? n : items;
    }

    public int pages() {
        int itemsPerPage = itemsPerPage();
        int pages = size / itemsPerPage;
        if (pages * itemsPerPage < size) {
            return pages + 1;
        }
        return pages;
    }

    public int lastPage() {
        return pages() - 1;
    }

    public boolean gotoPageByIndex(int index) {
        int page = pageByIndex(index);
        if (page < 0) {
            return false;
        }
        return gotoPage(page);
    }

    public int rowInCurrentPage(int index) {
        int offset = index - getCurrentPageBegin();
        int row = offset / getColumns();
        return row;
    }

    public int columnInCurrentPage(int index) {
        int offset = index - getCurrentPageBegin();
        int column = offset % getColumns();
        return column;
    }

    public int offsetInCurrentPage(int index) {
        int offset = index - getCurrentPageBegin();
        return offset;
    }

    public int pageByIndex(int index) {
        if (index < 0 || index >= size) {
            return -1;
        }
        int count = itemsPerPage();
        int page = index / count;
        return page;
    }

    public boolean canNextPage(){
        return currentPage + 1 < pages();
    }
    public boolean canPrevPage(){
        return currentPage > 0;
    }

    public boolean nextPage() {
        if (currentPage + 1 < pages()) {
            gotoPage(currentPage + 1);
            return true;
        }
        return false;
    }

    public boolean prevPage() {
        if (currentPage > 0) {
            gotoPage(currentPage - 1);
            return true;
        }
        return false;
    }

    public boolean gotoPage(int newPage) {
        int pages = pages();
        if (newPage < 0 || newPage >= pages) {
            return false;
        }
        currentPage = newPage;
        return true;
    }

    public int indexByPageOffset(int offset) {
        return getCurrentPageBegin() + offset;
    }

    public boolean isItemInCurrentPage(int index) {
        return getCurrentPageBegin() <= index && index < (getCurrentPageBegin() + itemsInCurrentPage());
    }

    public int getCurrentPageBegin() {
        return getPageBegin(currentPage);
    }

    public int getCurrentPageEnd() {
        return getPageEnd(currentPage);
    }

    public int getPageBegin(int page) {
        if (page < 0 || page >= pages()) {
            return -1;
        }
        return page * rows * columns;
    }

    public int getPageEnd(int page) {
        if (page < 0 || page >= pages()) {
            return -1;
        }

        int index = (page + 1) * rows * columns - 1;
        if (index >= size) {
            return size - 1;
        }
        if (index < 0) {
            return 0;
        }
        return index;
    }

    public int nextColumn(int index) {
        return index + 1;
    }

    public int prevColumn(int index) {
        return index - 1;
    }

    public int nextRow(int index) {
        return index + getColumns();
    }

    public int prevRow(int index) {
        return index - getColumns();
    }
}
