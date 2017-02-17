package com.onyx.android.sdk.data.model;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.List;

/**
 * Created by ming on 2017/2/14.
 */

public class StatisticsResult {

    private long totalReadTime;
    private double readingLevel;
    private List<Integer> myEventHourlyAgg;
    private List<Integer> eventHourlyAgg;
    private long dailyAvgReadTime;
    private Book longestReadTimeBook;
    private Book mostCarefulBook;
    private List<Book> recentReadingBooks;
    private LinkedMap<String, Double> bookTypeAgg;

    private EventTypeAggBean eventTypeAgg = new EventTypeAggBean();

    public double getReadingLevel() {
        return readingLevel;
    }

    public void setReadingLevel(double readingLevel) {
        this.readingLevel = readingLevel;
    }

    public long getTotalReadTime() {
        return totalReadTime;
    }

    public void setTotalReadTime(long totalReadTime) {
        this.totalReadTime = totalReadTime;
    }

    public List<Integer> getEventHourlyAgg() {
        return eventHourlyAgg;
    }

    public void setEventHourlyAgg(List<Integer> eventHourlyAgg) {
        this.eventHourlyAgg = eventHourlyAgg;
    }

    public List<Integer> getMyEventHourlyAgg() {
        return myEventHourlyAgg;
    }

    public void setMyEventHourlyAgg(List<Integer> myEventHourlyAgg) {
        this.myEventHourlyAgg = myEventHourlyAgg;
    }

    public long getDailyAvgReadTime() {
        return dailyAvgReadTime;
    }

    public void setDailyAvgReadTime(long dailyAvgReadTime) {
        this.dailyAvgReadTime = dailyAvgReadTime;
    }

    public Book getMostCarefulBook() {
        return mostCarefulBook == null ? longestReadTimeBook : mostCarefulBook;
    }

    public void setMostCarefulBook(Book mostCarefulBook) {
        this.mostCarefulBook = mostCarefulBook;
    }

    public Book getLongestReadTimeBook() {
        return longestReadTimeBook;
    }

    public void setLongestReadTimeBook(Book longestReadTimeBook) {
        this.longestReadTimeBook = longestReadTimeBook;
    }

    public List<Book> getRecentReadingBooks() {
        return recentReadingBooks;
    }

    public void setRecentReadingBooks(List<Book> recentReadingBooks) {
        this.recentReadingBooks = recentReadingBooks;
    }

    public EventTypeAggBean getEventTypeAgg() {
        return eventTypeAgg;
    }

    public void setEventTypeAgg(EventTypeAggBean eventTypeAgg) {
        this.eventTypeAgg = eventTypeAgg;
    }

    public LinkedMap<String, Double> getBookTypeAgg() {
        return bookTypeAgg;
    }

    public void setBookTypeAgg(LinkedMap<String, Double> bookTypeAgg) {
        this.bookTypeAgg = bookTypeAgg;
    }
}
