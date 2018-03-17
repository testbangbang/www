package com.onyx.jdread.main.model;

import java.util.LinkedList;

/**
 * Created by huxiaomao on 2017/12/9.
 */

public class StackList<T> {
    private LinkedList<T> stack = new LinkedList<>();

    public int size() {
        return stack.size();
    }

    public void push(T item){
        stack.addFirst(item);
    }

    public T peek(){
        return stack.getFirst();
    }

    public T pop(){
        return stack.removeFirst();
    }

    public boolean empty(){
        return stack.isEmpty();
    }

    public T popChildView(){
        if(stack.size() <= 1){
            return stack.peek();
        }
        stack.pop();
        return stack.peek();
    }

    public T remainLastStack() {
        if (stack.size() <= 1) {
            return stack.peek();
        }
        T last = stack.getLast();
        stack.clear();
        push(last);
        return last;
    }

    public String toString(){
        return stack.toString();
    }

    public T getLast() {
        return stack.getLast();
    }
}
