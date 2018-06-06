package com.uib.web.peptideshaker.model.core;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class responsible for calculating matrix for DivaMatrixLayoutChartFilter
 *
 * @author YEhia Farag
 */
public class ModificationMatrix {

    private final Map<String, Integer> rows;
    private final Map<String, Set<Comparable>> calculatedMatrix;
    private final Set<String> keySorter;

    public ModificationMatrix(Map<String, Set<Comparable>> data) {
        rows = new LinkedHashMap<>();
        keySorter = new TreeSet<>();
        calculatedMatrix = calculateMatrix(data);
    }

    public Map<String, Integer> getRows() {
        return rows;
    }

    public Map<String, Set<Comparable>> getCalculatedColumns() {
        return calculatedMatrix;
    }

    private Map<String, Set<Comparable>> calculateMatrix(Map<String, Set<Comparable>> data) {
        //calculate matrix
        Map<String, Set<Comparable>> matrixData = new LinkedHashMap<>();
        TreeMap<AlphanumComparator, String> sortingMap = new TreeMap<>(Collections.reverseOrder());
        data.keySet().forEach((key) -> {
            AlphanumComparator sortingKey = new AlphanumComparator(data.get(key).size() + "_" + key);
            sortingMap.put(sortingKey, key);
        });
        Map<String, Set<Comparable>> sortedData = new LinkedHashMap<>();
        sortingMap.values().stream().map((key) -> {
            int size = data.get(key).size();
            this.rows.put(key, size);
            return key;
        }).forEachOrdered((key) -> {
            sortedData.put(key, data.get(key));
        });
        Map<String, Set<Comparable>> rowsII = new LinkedHashMap<>(sortedData);
        Map<String, Set<Comparable>> tempColumns = new LinkedHashMap<>();
        tempColumns.putAll(sortedData);
        Map<String, Set<Comparable>> trows = new LinkedHashMap<>(sortedData);
        sortedData.keySet().stream().map((keyI) -> {
            rowsII.keySet().stream().filter((keyII) -> !(keyI.equals(keyII) || keyII.contains(keyI))).forEachOrdered((keyII) -> {
                String key = (keyII + "," + keyI).replace("[", "").replace("]", "");//.replace(" ", "");
                keySorter.addAll(Arrays.asList(key.split(",")));
                key = keySorter.toString();
                keySorter.clear();
                if (trows.containsKey(key)) {
                    Set<Comparable> union = new LinkedHashSet<>();
                    union.addAll(com.google.common.collect.Sets.union(trows.get(key), com.google.common.collect.Sets.intersection(rowsII.get(keyII), sortedData.get(keyI))));
                    trows.put(key, union);
                } else {
                    Set<Comparable> intersection = new LinkedHashSet<>();
                    intersection.addAll(com.google.common.collect.Sets.intersection(rowsII.get(keyII), sortedData.get(keyI)));
                    trows.put(key, intersection);
                    Set<Comparable> tempSetI = new LinkedHashSet<>();
                    tempSetI.addAll(rowsII.get(keyII));
                    tempSetI.removeAll(intersection);
                    rowsII.replace(keyII, tempSetI);
                    Set<Comparable> tempSetII = new LinkedHashSet<>();
                    tempSetII.addAll(sortedData.get(keyI));
                    tempSetII.removeAll(intersection);
                    sortedData.replace(keyI, tempSetII);
                }
            });
            return keyI;
        }).map((_item) -> {
            rowsII.clear();
            return _item;
        }).map((_item) -> {
            rowsII.putAll(trows);
            return _item;
        }).forEachOrdered((_item) -> {
            tempColumns.putAll(trows);
        });
        Map<AlphanumComparator, String> sortingMap2 = new TreeMap<>(Collections.reverseOrder());
        tempColumns.keySet().forEach((key) -> {
            AlphanumComparator sortingKey = new AlphanumComparator(tempColumns.get(key).size() + "_" + key);
            sortingMap2.put(sortingKey, key);
        });
        List<String> sortingKeysList = new ArrayList<>(rows.keySet());
        Map<Integer, String> sortingKysMap = new TreeMap<>();
        sortingMap2.values().forEach((key) -> {
            String[] arr = key.split(",");
            String updatedKey = key;
            if (arr.length > 1) {
                sortingKysMap.clear();
                for (String sub : arr) {
                    sub = sub.replace("]", "").replace("[", "").trim();
                    sortingKysMap.put(sortingKeysList.indexOf(sub), sub);
                }
                updatedKey = sortingKysMap.values().toString();
            }
            if (!tempColumns.get(key).isEmpty()) {
                matrixData.put(updatedKey, tempColumns.get(key));
            }
        });
        matrixData.keySet().forEach((key1) -> {
            matrixData.keySet().forEach((key2) -> {
                HashSet<Comparable> intersction = new HashSet<>();
                intersction.addAll(Sets.intersection(matrixData.get(key2), matrixData.get(key1)));
                if (!intersction.isEmpty() && !key2.equalsIgnoreCase(key1)) {
                    if (key1.split(",").length > key2.split(",").length) {
                        matrixData.get(key2).removeAll(intersction);
                    } else if (key1.split(",").length < key2.split(",").length) {
                        matrixData.get(key1).removeAll(intersction);
                    } else {
                        matrixData.get(key1).removeAll(intersction);
                        matrixData.get(key2).removeAll(intersction);
                    }
                }
            });
        });

        Map<String, Set<Comparable>> tempMatrixData = new LinkedHashMap<>(matrixData);
        tempMatrixData.keySet().stream().filter((key1) -> (matrixData.get(key1).isEmpty())).forEachOrdered((key1) -> {
            matrixData.remove(key1);
        });

        TreeMap<AlphanumComparator, String> sortingKeyColumnsMap = new TreeMap<>(Collections.reverseOrder());
        matrixData.keySet().forEach((key) -> {
            AlphanumComparator sortKey = new AlphanumComparator(matrixData.get(key).size() + "_" + key);
            sortingKeyColumnsMap.put(sortKey, key);
        });
        tempMatrixData.clear();
//        ArrayList<String> rowSortingList = new ArrayList<>(rows.keySet());
        sortingKeyColumnsMap.values().forEach((key) -> {
            //            if (key.contains("[")) {
//                Set<Comparable> set = tempMatrixData.get(key);
//                String[] columnKeys = key.replace("[", "").replace("]", "").split(",");
//                TreeMap<Integer, String> sorter = new TreeMap<>();
//                for (String s : columnKeys) {
//                    sorter.put(rowSortingList.indexOf(s.trim()), s.trim());
//                    System.out.println("---->> --->> " + key + "------>> " + rowSortingList.indexOf(s.trim()) + "," + s.trim()+"  "+rowSortingList.contains(s.trim())+"   "+rowSortingList);
//                }
//                System.out.println("at ---->> before --->> " + key + " --- after --->> " + sorter.values().toString());
//            }
            tempMatrixData.put(key, matrixData.get(key));
        });

        return tempMatrixData;
    }
}
