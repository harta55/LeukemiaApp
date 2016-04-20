package com.alexhart.leukemiaapp.medication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedContent {

    public static List<MedItem> ITEMS = new ArrayList<MedItem>();

    public static Map<String, MedItem> ITEM_MAP = new HashMap<String, MedItem>();

    private static void addItem(MedItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
    }

    public static class MedItem {
        public String name;
        public Double dose,freq;

        public MedItem(String name, Double d, Double freq) {
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
