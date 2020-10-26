package com.example.abhi.basiclauncher;

import java.util.List;



public class SortApps {
    public void exchange_sort(List<Pac> pacs) {
        int i, j;
        Pac temp;

        for(i = 0; i<pacs.size()-1; i++ ) {
            for (j = i+1; j<pacs.size(); j++ ) {
                if (pacs.get(i).label.compareToIgnoreCase(pacs.get(j).label)>0) {
                    temp = pacs.get(i);
                    pacs.set(i, pacs.get(j));
                    pacs.set(j, temp);
                }
            }
        }
    }
}
