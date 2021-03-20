package com.converter.gpx;

import com.converter.track.Track;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.converter.track.Track.DEFAULT_DATE_FORMAT;
import static com.converter.track.Track.DEFAULT_NO_SPACES_DATE_FORMAT;

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
