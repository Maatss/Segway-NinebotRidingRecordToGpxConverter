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

package com.maatss.converter.track;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SegwayTrackParser {
    public static List<Track> parseTracks(List<Path> paths, boolean allowSpaces) {
        // Create a Track object for each file
        List<Track> tracks = new ArrayList<>(paths.size());
        Gson gson = new Gson();
        for (Path path : paths) {
            try {
                // Parse file and initialize a SegwayTrack object to hold its data
                SegwayTrack segwayTrack = gson.fromJson(new FileReader(path.toFile()), SegwayTrack.class);

                // Wrap the data holder class
                Track track = new Track(path, segwayTrack, allowSpaces);
                tracks.add(track);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return tracks;
    }
}
