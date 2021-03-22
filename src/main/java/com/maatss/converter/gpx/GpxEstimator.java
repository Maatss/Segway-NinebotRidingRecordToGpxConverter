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

package com.maatss.converter.gpx;

import com.maatss.converter.track.SegwayTrack;
import com.maatss.converter.track.Track;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class used to make quick estimates of gpx file sizes
 */
public class GpxEstimator {
    private static final String dummySegwayTrackStringList = "11.111111,11.111111,0.0,1600000000";
    private final long overheadBytesForGpxFile;
    private final long overheadBytesPerTrack;
    private final long bytesPerWaypoint;

    public GpxEstimator() {
        // Setup dummy SegwayTracks
        SegwayTrack segwayTrackOneWaypoint = new SegwayTrack(new String[][]{{dummySegwayTrackStringList}});
        SegwayTrack segwayTrackTwoWaypoints = new SegwayTrack(new String[][]{{dummySegwayTrackStringList, dummySegwayTrackStringList}});

        // Get size for one track with one waypoint
        List<Track> tracks = new ArrayList<>();
        tracks.add(new Track(Paths.get("my/test/path"), segwayTrackOneWaypoint, true));
        long numBytesForOneTrackOneWaypoint = GpxCreator.gpxFileSize(tracks);

        // Get size for one track with two waypoints
        tracks = new ArrayList<>();
        tracks.add(new Track(Paths.get("my/test/path"), segwayTrackTwoWaypoints, true));
        long numBytesForOneTrackTwoWaypoints = GpxCreator.gpxFileSize(tracks);

        // Get size for two tracks with one waypoint each
        tracks = new ArrayList<>();
        tracks.add(new Track(Paths.get("my/test/path"), segwayTrackOneWaypoint, true));
        tracks.add(new Track(Paths.get("my/test/path"), segwayTrackOneWaypoint, true));
        long numBytesForTwoTracksWithOneWaypointEach = GpxCreator.gpxFileSize(tracks);

        // Calculate sizes for the different track components
        bytesPerWaypoint = numBytesForOneTrackTwoWaypoints - numBytesForOneTrackOneWaypoint;
        overheadBytesPerTrack = numBytesForTwoTracksWithOneWaypointEach - numBytesForOneTrackOneWaypoint - bytesPerWaypoint;
        overheadBytesForGpxFile = numBytesForOneTrackOneWaypoint - overheadBytesPerTrack - bytesPerWaypoint;
    }

    /**
     * Returns the estimates size in bytes for the resulting gpx file when including all provided tracks.
     * Does not account for varying number of decimals, which will affect final filesize.
     */
    public long estimateSize(List<Track> tracks, Track track) {
        List<Track> tracksCombined = new ArrayList<>(tracks);
        tracksCombined.add(track);
        return estimateSize(tracksCombined);
    }

    /**
     * Returns the estimates size in bytes for the resulting gpx file when including all provided tracks
     * Does not account for varying number of decimals, which will affect final filesize.
     */
    public long estimateSize(List<Track> tracks) {
        // Count the total number of waypoints
        long numWaypoints = tracks.stream()
                .map(Track::getWaypoints)
                .mapToLong(Collection::size)
                .sum();

        // Count the total number of tracks
        int numTracks = tracks.size();

        // Includes overhead for 1 track and 1 waypoint
        long estimatesSize = overheadBytesForGpxFile;

        // Add size for all tracks and waypoints, while skipping those already included
        estimatesSize += numTracks * overheadBytesPerTrack + numWaypoints * bytesPerWaypoint;

        return estimatesSize;
    }
}
