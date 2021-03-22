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

package com.maatss.converter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConverterConfiguration {
    public static final long DEFAULT_FILE_SIZE_LIMIT = 5_000_000L;
    public static final boolean DEFAULT_USE_FILE_SIZE_LIMIT = true;
    public static final boolean DEFAULT_EXCLUDE_DUPLICATES = true;
    public static final boolean DEFAULT_ALLOW_SPACE = true;
    public static final Path DEFAULT_SAVE_DIRECTORY_PATH = Paths.get("My converted files").toAbsolutePath();

    private final List<Path> targetFiles;
    private long fileSizeLimit;
    private boolean useFileSizeLimit;
    private boolean excludeDuplicates;
    private boolean allowSpaces;
    private Path saveDirectoryPath;

    public ConverterConfiguration() {
        targetFiles = new ArrayList<>();
        reset();
    }

    public void reset() {
        targetFiles.clear();
        fileSizeLimit = DEFAULT_FILE_SIZE_LIMIT;
        useFileSizeLimit = DEFAULT_ALLOW_SPACE;
        excludeDuplicates = DEFAULT_USE_FILE_SIZE_LIMIT;
        allowSpaces = DEFAULT_EXCLUDE_DUPLICATES;
        saveDirectoryPath = DEFAULT_SAVE_DIRECTORY_PATH;
    }

    public List<Path> getTargetFiles() {
        return targetFiles;
    }

    public long getFileSizeLimit() {
        return fileSizeLimit;
    }

    public void setFileSizeLimit(long fileSizeLimit) {
        this.fileSizeLimit = fileSizeLimit;
    }

    public boolean isFileSizeLimitActive() {
        return useFileSizeLimit;
    }

    public void setUseFileSizeLimit(boolean useFileSizeLimit) {
        this.useFileSizeLimit = useFileSizeLimit;
    }

    public boolean areDuplicatesExcluded() {
        return excludeDuplicates;
    }

    public void setExcludeDuplicates(boolean excludeDuplicates) {
        this.excludeDuplicates = excludeDuplicates;
    }

    public boolean areSpacesAllowed() {
        return allowSpaces;
    }

    public void setAllowSpaces(boolean allowSpaces) {
        this.allowSpaces = allowSpaces;
    }

    public Path getSaveDirectoryPath() {
        return saveDirectoryPath;
    }

    public void setSaveDirectoryPath(Path saveDirectoryPath) {
        this.saveDirectoryPath = saveDirectoryPath;
    }
}
