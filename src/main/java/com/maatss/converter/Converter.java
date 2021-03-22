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

import com.maatss.converter.gpx.GpxCreator;
import com.maatss.converter.gpx.GpxPartitioner;
import com.maatss.converter.track.SegwayTrackParser;
import com.maatss.converter.track.Track;
import com.maatss.converter.util.DuplicateFinder;

import java.nio.file.Path;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Converter {
    private static final String SEPARATOR = "////////////////////////////////////////////////////////////";
    private final List<ConverterEvent> eventListeners;

    public Converter() {
        eventListeners = new ArrayList<>();

        // Add debug printout
        eventListeners.add((eventMessage) -> System.out.printf("EventMessage: %s\n", eventMessage));
    }

    /**
     * Returns a formatted string for the provided number of bytes.
     * Based on: https://stackoverflow.com/questions/3758606/how-can-i-convert-byte-size-into-a-human-readable-format-in-java
     */
    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    private static String addSIfPlural(String word, int i) {
        return String.format("%s%s", word, i == 1 ? "" : "s");
    }

    public void convert(ConverterConfiguration config) {
        // Pass exception messages to listeners, if something were to happen
        try {

            triggerEvent("Conversion started..");
            List<Track> tracks = parseTracks(config.getTargetFiles(), config.areSpacesAllowed());
            searchForDuplicateTracks(tracks, config.areDuplicatesExcluded());
            createGPXFiles(tracks, config.getSaveDirectoryPath(), config.areSpacesAllowed(), config.isFileSizeLimitActive(), config.getFileSizeLimit());
            triggerEvent(String.format("\n%s\nConversion has successfully completed.\n%s", SEPARATOR, SEPARATOR));

        } catch (Exception exception) {
            // Build error message
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Something went wrong, sorry :(");
            stringBuilder.append("\n==============================");
            stringBuilder.append("\nError message: \n");
            stringBuilder.append(exception.getMessage());
            stringBuilder.append("\n------------------------------");
            stringBuilder.append("\nError stacktrace: ");
            for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
                stringBuilder.append("\n");
                stringBuilder.append(stackTraceElement);
            }
            stringBuilder.append("\n==============================");

            // Send error message as an event
            triggerEvent(stringBuilder.toString());
            triggerEvent(String.format("\n%s\nConversion has failed.\n%s", SEPARATOR, SEPARATOR));
        }
    }

    private List<Track> parseTracks(List<Path> targetFiles, boolean allowSpaces) {
        triggerEvent("Parsing files for tracks..");
        List<Track> tracks = SegwayTrackParser.parseTracks(targetFiles, allowSpaces);
        triggerEvent(String.format("Found %d %s.", tracks.size(), addSIfPlural("track", tracks.size())));
        return tracks;
    }

    private void searchForDuplicateTracks(List<Track> targetTracks, boolean removeDuplicates) {
        triggerEvent("Searching for duplicate tracks..");
        DuplicateFinder<Track> trackDuplicateFinder = new DuplicateFinder<>(targetTracks);
        List<Track> duplicates = trackDuplicateFinder.getDuplicates();

        // Stop if no duplicates were found
        if (duplicates.isEmpty()) {
            triggerEvent("Found no duplicate tracks.");
            return;
        }

        // Announce all duplicates found
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("Found the following duplicate %s (%d):", addSIfPlural("track", duplicates.size()), duplicates.size()));
        for (int i = 0; i < duplicates.size(); i++) {
            stringBuilder.append(String.format("\n\t%d. %s", i + 1, duplicates.get(i).getPath()));
        }
        triggerEvent(stringBuilder.toString());

        // Remove duplicates if applicable
        if (removeDuplicates) {
            List<Track> uniques = trackDuplicateFinder.getUniques();
            targetTracks.clear();
            targetTracks.addAll(uniques);
            triggerEvent(String.format("Removed %d duplicate %s, continuing with a total of %d %s.",
                    duplicates.size(),
                    addSIfPlural("track", duplicates.size()),
                    targetTracks.size(),
                    addSIfPlural("track", targetTracks.size())));
        } else {
            triggerEvent(String.format("Did not remove any duplicate tracks, continuing with a total of %d %s.",
                    targetTracks.size(),
                    addSIfPlural("track", targetTracks.size())));
        }
    }

    private void createGPXFiles(List<Track> targetTracks, Path saveDirectory, boolean allowSpaces, boolean useFileSizeLimit, long fileSizeLimit) {
        List<Path> filePaths;

        // Create files
        if (useFileSizeLimit) {
            triggerEvent(String.format("Creating GPX files with a size limit of %s (%d KB)..", humanReadableByteCountSI(fileSizeLimit), fileSizeLimit / 1000L));
            filePaths = GpxPartitioner.createGpxFilesOfSize(targetTracks, saveDirectory, fileSizeLimit, allowSpaces);
        } else {
            triggerEvent("Creating GPX files with no size limit..");
            Path filePath = GpxCreator.createGpxFile(targetTracks, saveDirectory, allowSpaces);
            filePaths = new ArrayList<>(Collections.singletonList(filePath));
        }

        // Announce all files created
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("The following GPX %s created (%d):",
                filePaths.size() == 1 ? "file was" : "files were",
                filePaths.size()));
        for (int i = 0; i < filePaths.size(); i++) {
            stringBuilder.append(String.format("\n\t%d. %s", i + 1, filePaths.get(i).getFileName()));
        }
        stringBuilder.append(String.format("\nThe GPX %s saved at:\n\t%s",
                filePaths.size() == 1 ? "file was" : "files were",
                saveDirectory));
        triggerEvent(stringBuilder.toString());
    }

    private void triggerEvent(String eventMessage) {
        eventListeners.forEach(listener -> listener.onEvent(eventMessage));
    }

    public void addEventListener(ConverterEvent eventListener) {
        eventListeners.add(eventListener);
    }
}
