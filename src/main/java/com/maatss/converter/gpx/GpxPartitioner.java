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

import com.maatss.converter.track.Track;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.maatss.converter.track.Track.DEFAULT_DATE_FORMAT;
import static com.maatss.converter.track.Track.DEFAULT_NO_SPACES_DATE_FORMAT;

public class GpxPartitioner {

    public static List<Path> createGpxFilesOfSize(List<Track> tracks, Path saveDirectory, long maxFileSize, boolean allowSpaces) {
        List<List<Track>> trackPartitions = partitionTracks(tracks, maxFileSize);

        // Setup file name template
        String filenameTemplate = allowSpaces
                ? "ScooterTracks [%d of %d] %s"
                : "ScooterTracks_[%d_of_%d]_%s";
        SimpleDateFormat dateFormat = allowSpaces ? DEFAULT_DATE_FORMAT : DEFAULT_NO_SPACES_DATE_FORMAT;

        // Create a gpx file for each trackPartition
        List<Path> filePaths = new ArrayList<>();
        for (int i = 0; i < trackPartitions.size(); i++) {
            // Create filename from template
            long millis = System.currentTimeMillis();
            String filename = String.format(filenameTemplate,
                    i + 1, trackPartitions.size(), dateFormat.format(millis));

            Path filePath = GpxCreator.createGpxFile(trackPartitions.get(i), saveDirectory, filename);
            filePaths.add(filePath);
        }

        return filePaths;
    }

    private static List<List<Track>> partitionTracks(List<Track> tracks, long maxFileSize) {
        GpxEstimator gpxEstimator = new GpxEstimator();
        List<Track> trackPartition = new ArrayList<>();
        List<List<Track>> trackPartitions = new ArrayList<>();

        // Partition tracks into partitions with a max filesize
        for (Track track : tracks) {
            long nextPartitionSizeInBytes = gpxEstimator.estimateSize(trackPartition, track);

            // Create a partition if above filesize limit
            // Use quick estimation and recheck with exact size if the first one passes
            if (nextPartitionSizeInBytes > maxFileSize
                    && GpxCreator.gpxFileSize(trackPartition, track) > maxFileSize) {
                if (trackPartition.size() == 0) {
                    throw new RuntimeException(
                            String.format("partitionTracks: maxFileSize (%d bytes) is too small," +
                                    " a single track (%d bytes) could not fit inside it.", maxFileSize, nextPartitionSizeInBytes));
                }

                // Create partition
                trackPartitions.add(new ArrayList<>(trackPartition));
                trackPartition.clear();
            }

            // Add track to current partial partition
            trackPartition.add(track);
        }

        // Create final partition
        trackPartitions.add(new ArrayList<>(trackPartition));

        return trackPartitions;
    }
}
