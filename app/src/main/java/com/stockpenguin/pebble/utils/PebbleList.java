package com.stockpenguin.pebble.utils;

import androidx.annotation.NonNull;

import com.stockpenguin.pebble.interfaces.PebbleListListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PebbleList<T> extends LinkedList<T> implements Serializable{
    private PebbleListListener listener;

    public void addOnListChangedListener(@NonNull PebbleListListener listener) {
        this.listener = listener;
        listener.onListChanged();
    }

    @Override
    public boolean add(T t) {
        boolean b = super.add(t);
        listener.onListChanged();
        return b;
    }

    @Override
    public void clear() {
        super.clear();
        listener.onListChanged();
    }
}
