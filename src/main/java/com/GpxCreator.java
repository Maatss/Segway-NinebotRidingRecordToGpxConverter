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
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
     * Creates a gpx file from a track object in the same location as the original track file
     */
    public static void createGpx(Track track) {
        try {
            // Setup xml builder
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            document.setXmlStandalone(true);

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

            Element timeElement = document.createElement("time");
            timeElement.appendChild(document.createTextNode(ZULU_DATE_FORMAT.format(new Date(track.getStartTimestamp()))));
            metaElement.appendChild(timeElement);

            // Setup trk tree
            Element trkElement = document.createElement("trk");
            rootElement.appendChild(trkElement);

            Element nameElement = document.createElement("name");
            nameElement.appendChild(document.createTextNode(track.getName()));
            trkElement.appendChild(nameElement);

            Element trksegElement = document.createElement("trkseg");
            trkElement.appendChild(trksegElement);

            // Create a tree for every waypoint
            for (Waypoint waypoint : track.getWaypoints()) {
                Element trkptElement = document.createElement("trkpt");
                trkptElement.setAttribute("lat", Double.toString(waypoint.getLat()));
                trkptElement.setAttribute("lon", Double.toString(waypoint.getLon()));
                trksegElement.appendChild(trkptElement);

                timeElement = document.createElement("time");
                timeElement.appendChild(document.createTextNode(ZULU_DATE_FORMAT.format(new Date(waypoint.getStartTimestamp()))));
                trkptElement.appendChild(timeElement);
            }

            // Get file save location
            Path trackFilePath = track.getPath();

            // Replace filename with its corresponding date and its file extension with '.gpx'
            String path = trackFilePath.getParent().resolve(track.getName()).toString() + ".gpx";

            // Create gpx file
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(path));
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }
}
