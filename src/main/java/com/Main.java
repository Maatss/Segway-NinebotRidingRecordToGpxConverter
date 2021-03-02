package com;

import com.Track.SegwayTrackParser;
import com.Track.Track;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // If no arguments were given, let the user enter them
        if (args.length == 0) {
            throw new IllegalArgumentException("No arguments were provided. Pass the files you want to convert as arguments, each filepath separated by a space.");
        }

        System.out.printf("Received arguments (%d):\n", args.length);
        for (String arg : args) {
            System.out.println("\t" + arg);
        }

        System.out.println("\nExtracting paths from arguments..");
        Path[] filesToConvert = extractFilesFromArguments(args);

        System.out.println("Parsing segway tracks..");
        List<Track> tracks = SegwayTrackParser.parseTracks(filesToConvert);

        System.out.println("Creating gpx files..");
        tracks.forEach(GpxCreator::createGpx);

        System.out.println("\nDone.");
    }

    /**
     * Creates an array of Paths from the string array of file paths
     */
    private static Path[] extractFilesFromArguments(String[] args) {
        return Arrays.stream(args).map(Paths::get).toArray(Path[]::new);
    }
}
