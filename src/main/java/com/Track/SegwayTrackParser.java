package com.Track;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SegwayTrackParser {
    public static List<Track> parseTracks(Path[] paths) {
        // Create a Track object for each file
        List<Track> tracks = new ArrayList<>(paths.length);
        Gson gson = new Gson();
        for (Path path : paths) {
            try {
                // Parse file and initialize a SegwayTrack object to hold its data
                SegwayTrack segwayTrack = gson.fromJson(new FileReader(path.toFile()), SegwayTrack.class);

                // Wrap the data holder class
                Track track = new Track(path, segwayTrack);
                tracks.add(track);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return tracks;
    }
}
