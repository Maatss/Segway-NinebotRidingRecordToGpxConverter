package com;

import com.Track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.Track.Track.DEFAULT_DATE_FORMAT;
import static com.Track.Track.DEFAULT_NO_SPACES_DATE_FORMAT;

public class GpxPartitioner {

    public static void createGpxFilesOfSize(List<Track> tracks, long maxFileSize, boolean allowSpaces) {
        List<List<Track>> trackPartitions = partitionTracks(tracks, maxFileSize);

        // Setup file name template
        String filenameTemplate = allowSpaces
                ? "ScooterTracks [%d of %d] %s"
                : "ScooterTracks_[%d_of_%d]_%s";
        SimpleDateFormat dateFormat = allowSpaces ? DEFAULT_DATE_FORMAT : DEFAULT_NO_SPACES_DATE_FORMAT;

        // Create a gpx file for each trackPartition
        for (int i = 0; i < trackPartitions.size(); i++) {
            // Create filename from template
            long millis = System.currentTimeMillis();
            String filename = String.format(filenameTemplate,
                    i + 1, trackPartitions.size(), dateFormat.format(millis));

            GpxCreator.createGpxFile(trackPartitions.get(i), filename);
        }
    }

    private static List<List<Track>> partitionTracks(List<Track> tracks, long maxFileSize) {
        List<List<Track>> trackPartitions = new ArrayList<>();

        // Divide tracks into partitions respecting maxFileSize
        List<Track> trackPartition = new ArrayList<>();
        for (Track track : tracks) {
            // Get size
            // A size of 0 is expected for the first loop iteration
            long sizeInBytes = GpxCreator.estimateGpxSize(trackPartition);

            // Create a partition if above filesize limit
            if (sizeInBytes > maxFileSize) {
                if (trackPartition.size() == 1) {
                    throw new RuntimeException(
                            String.format("partitionTracks: maxFileSize (%d bytes) is too small," +
                                    " a single track (%d) cannot fit inside it.", maxFileSize, sizeInBytes));
                }

                trackPartitions.add(new ArrayList<>(trackPartition));
                trackPartition.clear();
            }

            // Add track to current partition
            trackPartition.add(track);
        }

        return trackPartitions;
    }
}
