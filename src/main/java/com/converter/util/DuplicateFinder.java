package com.converter.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DuplicateFinder<T> {
    private final List<T> all;
    private final List<T> duplicates;
    private final List<T> uniques;

    public DuplicateFinder(List<T> all) {
        this.all = all;

        // Separate duplicates and uniques
        duplicates = new ArrayList<>();
        final Set<T> uniques = new LinkedHashSet<>(all.size());
        for (T t : all) {
            if (uniques.add(t)) continue;

            // Add to duplicates if already present in uniques
            duplicates.add(t);
        }

        // Convert set to list
        this.uniques = new ArrayList<>(uniques);
    }

    public List<T> getAll() {
        return all;
    }

    public List<T> getDuplicates() {
        return duplicates;
    }

    public List<T> getUniques() {
        return uniques;
    }
}
