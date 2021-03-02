package com.Track;

public class Waypoint {
    private final double lat;
    private final double lon;
    private final double unknown; // Unknown value found in the raw-json file
    private final long timestamp;

    public Waypoint(double lat, double lon, double unknown, long timestamp) {
        this.lat = lat;
        this.lon = lon;
        this.unknown = unknown;
        this.timestamp = timestamp;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getUnknown() {
        return unknown;
    }

    public long getStartTimestamp() {
        return timestamp;
    }
}
