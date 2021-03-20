package com.converter.track;

import java.util.Objects;

public class Waypoint {
    private final double lat;
    private final double lon;
    private final double unknown; //todo unknown value found in the raw-json file
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Waypoint waypoint = (Waypoint) o;
        return Double.compare(waypoint.lat, lat) == 0
                && Double.compare(waypoint.lon, lon) == 0
                && Double.compare(waypoint.unknown, unknown) == 0
                && timestamp == waypoint.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon, unknown, timestamp);
    }
}
