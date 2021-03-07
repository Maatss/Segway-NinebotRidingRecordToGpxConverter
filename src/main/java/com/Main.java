package com;

import com.Track.SegwayTrackParser;
import com.Track.Track;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final long MAX_GPX_FILE_SIZE = 5_000_000; // 5 Megabytes, the limit set for Google myMaps
    private static final boolean ALLOW_FILENAME_SPACES = true;

    public static void main(String[] args) {
        // If no arguments were given, let the user enter them
        if (args.length == 0) {
            throw new IllegalArgumentException("No arguments were provided. Pass the files you want to convert as arguments, each filepath separated by a space.");
        }

        System.out.printf("Received arguments (%d):\n", args.length);
        for (String arg : args) {
            System.out.println("\t" + arg);
        }

        System.out.print("\nExtracting paths from arguments..");
        Path[] filesToConvert = extractFilesFromArguments(args);
        System.out.println(" Done.");

        System.out.print("Parsing segway tracks..");
        List<Track> tracks = SegwayTrackParser.parseTracks(filesToConvert, ALLOW_FILENAME_SPACES);
        System.out.printf(" (found %d tracks) Done.\n", tracks.size());

        System.out.print("Searching for duplicate tracks..");
        DuplicateFinder<Track> trackDuplicateFinder = new DuplicateFinder<>(tracks);
        System.out.println(" Done.");

        List<Track> duplicates = trackDuplicateFinder.getDuplicates();
        if (!duplicates.isEmpty()) {
            System.out.printf("\nFound the following duplicate tracks (%d):\n", duplicates.size());
            for (int i = 0; i < duplicates.size(); i++) {
                System.out.printf("\t%d. %s\n", i + 1, duplicates.get(i).getPath());
            }
            tracks = trackDuplicateFinder.getUniques();
            System.out.printf("The program will continue with the non-duplicate tracks (%d).\n", tracks.size());
            System.out.println("(Duplicate tracks are defined as tracks with identical waypoint data, e.g. coordinates and timestamps)\n");
        }

        System.out.printf("Creating gpx files of size %s..", humanReadableByteCountSI(MAX_GPX_FILE_SIZE));
        GpxPartitioner.createGpxFilesOfSize(tracks, MAX_GPX_FILE_SIZE, ALLOW_FILENAME_SPACES);
        System.out.println(" Done.");

        System.out.println("\nProgram has successfully completed.");
    }

    /**
     * Creates an array of Paths from the string array of file paths.
     */
    private static Path[] extractFilesFromArguments(final String[] args) {
        return Arrays.stream(args).map(Paths::get).toArray(Path[]::new);
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
}
