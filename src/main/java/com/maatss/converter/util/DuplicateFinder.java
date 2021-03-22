/*
 * Segway-Ninebot Riding Record To GPX Converter
 * Copyright (C) 2021 Maatss
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.maatss.converter.util;

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
