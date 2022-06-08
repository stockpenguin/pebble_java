package com.stockpenguin.pebble.interfaces;

import java.io.Serializable;

public interface PebbleListListener extends Serializable {
    public void onListChanged();
}
