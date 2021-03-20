package com.converter.track;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Track {
    public static final SimpleDateFormat DEFAULT_NO_SPACES_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.US);

    private final SegwayTrack segwayTrack;
    private final Path path;
    private String name;
    private long startTimestamp;
    private List<Waypoint> waypoints;

    public Track(Path path, SegwayTrack segwayTrack, boolean allowSpaces) {
        this.segwayTrack = segwayTrack;
        this.path = path;

        setupWaypoints();
        setupStartTimestamp();
        setupName(allowSpaces);
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

    private void setupName(boolean allowSpaces) {
        // If there is no startTimestamp, keep original filename
        if (startTimestamp == 0) {
            name = getFileName(path);
            return;
        }

        // Else, use startTimestamp as filename
        SimpleDateFormat dateFormat = allowSpaces ? DEFAULT_DATE_FORMAT : DEFAULT_NO_SPACES_DATE_FORMAT;
        name = dateFormat.format(new Date(startTimestamp));
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
                    return new Waypoint(latitude, longitude, unknown, timestamp);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return waypoints.equals(track.waypoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(waypoints);
    }
}
