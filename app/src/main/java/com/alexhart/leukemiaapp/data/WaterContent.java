package com.alexhart.leukemiaapp.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaterContent {

    public static List<WaterItem> ITEMS = new ArrayList<WaterItem>();

    public static Map<String, WaterItem> ITEM_MAP = new HashMap<String, WaterItem>();

    private static void addItem(WaterItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
    }

    public static class WaterItem {
        public String name;
        public Double dose,freq;

        public WaterItem(String name, Double d, Double freq) {
            this.dose = d;
            this.freq = freq;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
