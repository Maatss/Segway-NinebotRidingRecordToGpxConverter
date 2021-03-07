package com;

import com.Track.Track;
import com.Track.Waypoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Based on: https://gitlab.com/rcgroot/open-gps-tracker/-/blob/develop/studio/exporter/src/main/java/nl/renedegroot/opengpstracker/exporter/GpxCreator.java
 */
public class GpxCreator {
    private static final SimpleDateFormat ZULU_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    static {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        ZULU_DATE_FORMAT.setTimeZone(utc); // ZULU_DATE_FORMAT format ends with Z for UTC so make that true
    }

    /**
     * Creates a gpx file with an filename based on the current time,
     * from a track object in the same location as the original track file
     */
    public static void createGpxFile(List<Track> tracks) {
        // Create a unique filename based on the current time
        long millis = System.currentTimeMillis();
        String filename = String.format("ScooterTracks_%s", Track.DEFAULT_DATE_FORMAT.format(millis));

        createGpxFile(tracks, filename);
    }

    /**
     * Creates a gpx file with the provided filename from a track object in the same location as the original track file
     */
    public static void createGpxFile(List<Track> tracks, String filename) {
        if (tracks.size() == 0) {
            return;
        }

        // Arbitrary track used to get file save location and populate metadata
        Track firstTrack = tracks.get(0);

        // Get file save location
        Path trackFilePath = firstTrack.getPath();

        // Replace filename with its corresponding date and its file extension with '.gpx'
        String path = trackFilePath.getParent().resolve(filename).toString() + ".gpx";

        // Create a file to write data to
        File file = new File(path);
        StreamResult streamResult = new StreamResult(file);

        createGpx(tracks, streamResult);
    }

    /**
     * Returns the exact gpx filesize in bytes when including all provided tracks.
     */
    public static long gpxFileSize(List<Track> tracks, Track track) {
        List<Track> tracksCombined = new ArrayList<>(tracks);
        tracksCombined.add(track);
        return gpxFileSize(tracksCombined);
    }

    /**
     * Returns the exact gpx filesize in bytes.
     */
    public static long gpxFileSize(List<Track> tracks) {
        // Use an arbitrary reference type to enable mutability of the long value
        long[] sizeInBytes = new long[]{0};

        // Create a byte counter outputStream
        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                // The method contract tells us b is a single byte,
                // that the 24 high-order bits are ignored
                sizeInBytes[0]++;
            }
        };

        // Pass along the byte counter
        StreamResult streamResult = new StreamResult(outputStream);
        createGpx(tracks, streamResult);

        // Return the number of bytes handled by the outputStream
        return sizeInBytes[0];
    }

    /**
     * Creates a gpx document from the provided tracks and outputs it to the provided streamResult
     */
    private static void createGpx(List<Track> tracks, StreamResult streamResult) {
        // Stop early if no tracks
        if (tracks.size() == 0) {
            return;
        }

        // Arbitrary track used to get file save location and populate metadata
        Track firstTrack = tracks.get(0);

        try {
            // Setup xml builder
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            document.setXmlStandalone(true);

            // Create root element
            Element rootElement = createGpxRoot(document, firstTrack.getStartTimestamp());

            // Setup trk tree for each track
            for (Track track : tracks) {
                appendTrackElement(document, rootElement, track);
            }

            // Create gpx file
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            transformer.transform(source, streamResult);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private static Element createGpxRoot(Document document, long timestamp) {
        // Create root element
        Element rootElement = document.createElement("gpx");
        document.appendChild(rootElement);

        // Setup file attributes
        rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        rootElement.setAttribute("xmlns:gpx10", "http://www.topografix.com/GPX/1/0");
        rootElement.setAttribute("xmlns:ogt10", "http://gpstracker.android.sogeti.nl/GPX/1/0");
        rootElement.setAttribute("version", "1.1");
        rootElement.setAttribute("creator", "SegwayJsonToGpxConverter");
        rootElement.setAttribute("xsi:schemaLocation", "http://www.topografix.com/GPX/1/1 http://www.topografix.com/gpx/1/1/gpx.xsd");
        rootElement.setAttribute("xmlns", "http://www.topografix.com/GPX/1/1");

        // Setup metadata tree
        Element metaElement = document.createElement("metadata");
        rootElement.appendChild(metaElement);

        // Setup time element
        Element timeElement = document.createElement("time");
        timeElement.appendChild(document.createTextNode(ZULU_DATE_FORMAT.format(new Date(timestamp))));
        metaElement.appendChild(timeElement);

        return rootElement;
    }

    private static void appendTrackElement(Document document, Element rootElement, Track track) {
        Element trkElement = document.createElement("trk");
        rootElement.appendChild(trkElement);

        Element nameElement = document.createElement("name");
        nameElement.appendChild(document.createTextNode(track.getName()));
        trkElement.appendChild(nameElement);

        Element trksegElement = document.createElement("trkseg");
        trkElement.appendChild(trksegElement);

        // Create a tree for every waypoint
        for (Waypoint waypoint : track.getWaypoints()) {
            appendWaypointElement(document, trksegElement, waypoint);
        }
    }

    private static void appendWaypointElement(Document document, Element trksegElement, Waypoint waypoint) {
        Element trkptElement = document.createElement("trkpt");
        trkptElement.setAttribute("lat", Double.toString(waypoint.getLat()));
        trkptElement.setAttribute("lon", Double.toString(waypoint.getLon()));
        trksegElement.appendChild(trkptElement);

        Element timeElement = document.createElement("time");
        timeElement.appendChild(document.createTextNode(ZULU_DATE_FORMAT.format(new Date(waypoint.getStartTimestamp()))));
        trkptElement.appendChild(timeElement);
    }
}
