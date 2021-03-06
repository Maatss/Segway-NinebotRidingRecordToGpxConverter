package com;

import com.Track.SegwayTrackParser;
import com.Track.Track;

import java.nio.file.Path;
import java.nio.file.Paths;
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
        System.out.println(" Done.");

        System.out.printf("Creating gpx files of size %d bytes..", MAX_GPX_FILE_SIZE);
        GpxPartitioner.createGpxFilesOfSize(tracks, MAX_GPX_FILE_SIZE, ALLOW_FILENAME_SPACES);
        System.out.println(" Done.");

        System.out.println("\nProgram has successfully completed.");
    }

    /**
     * Creates an array of Paths from the string array of file paths
     */
    private static Path[] extractFilesFromArguments(final String[] args) {
        return Arrays.stream(args).map(Paths::get).toArray(Path[]::new);
    }
}
