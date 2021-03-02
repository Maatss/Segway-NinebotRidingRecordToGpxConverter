package com.Track;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Track {
    private final SegwayTrack segwayTrack;
    private final Path path;
    private final String name;
    private long startTimestamp;
    private List<Waypoint> waypoints;

    public Track(Path path, SegwayTrack segwayTrack) {
        this.name = getFileName(path);
        this.segwayTrack = segwayTrack;
        this.path = path;

        setupWaypoints();
        setupStartTimestamp();
    }

    private static String getFileName(Path path) {
        String filename = path.getFileName().toString();

        // Remove file extension, if it exists
        int pos = filename.lastIndexOf(".");
        if (pos > 0) {
            filename = filename.substring(0, pos);
        }

        return filename;
    }

    private void setupStartTimestamp() {
        startTimestamp = waypoints.size() > 0
                ? waypoints.get(0).getStartTimestamp()
                : 0;
    }

    private void setupWaypoints() {
        // Get list of raw-json waypoints
        String[][] segwayWaypointList = segwayTrack.getList();

        // Map raw-json waypoints to Waypoints
        waypoints = Arrays.stream(segwayWaypointList)
                .flatMap(Arrays::stream)
                .map(segwayWaypointString -> {
                    String[] strings = segwayWaypointString.split(",");
                    double longitude = Double.parseDouble(strings[0]);
                    double latitude = Double.parseDouble(strings[1]);
                    double unknown = Double.parseDouble(strings[2]);
                    long timestamp = Long.parseLong(strings[3]) * 1000; // Convert to milliseconds
                    return new Waypoint(longitude, latitude, unknown, timestamp);
                })
                .collect(Collectors.toList());
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public Path getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }
}
