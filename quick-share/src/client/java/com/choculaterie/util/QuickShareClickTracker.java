package com.choculaterie.util;

import java.util.IdentityHashMap;
import java.util.Map;

public class QuickShareClickTracker {
    private static boolean preventSelection = false;
    private static final Map<Object, int[]> buttonBounds = new IdentityHashMap<>();

    public static void markPreventSelection() { preventSelection = true; }
    public static boolean shouldPreventSelection() { return preventSelection; }
    public static void clearPreventSelection() { preventSelection = false; }

    public static void updateButtonBounds(Object key, int x, int y, int w, int h) {
        if (key == null || w <= 0 || h <= 0) { buttonBounds.remove(key); return; }
        buttonBounds.put(key, new int[]{x, y, w, h});
    }

    public static void clearButtonBounds(Object key) { if (key != null) buttonBounds.remove(key); }

    public static boolean preMarkIfClickOnButton(int mx, int my) {
        for (int[] b : buttonBounds.values()) {
            if (mx >= b[0] && mx < b[0] + b[2] && my >= b[1] && my < b[1] + b[3]) {
                preventSelection = true;
                return true;
            }
        }
        return false;
    }
}

