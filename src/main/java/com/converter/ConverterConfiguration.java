package com.converter;

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
