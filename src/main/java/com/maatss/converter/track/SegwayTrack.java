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

import java.util.Arrays;

/**
 * Data holder class for Segway riding record json files
 */
public class SegwayTrack {
    private int coordinateType;
    private String countryCode;
    private String[][] list;
    private int version;

    public SegwayTrack(String[][] list) {
        this.list = list;
    }

    public int getCoordinateType() {
        return coordinateType;
    }

    public void setCoordinateType(int coordinateType) {
        this.coordinateType = coordinateType;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String[][] getList() {
        return list;
    }

    public void setList(String[][] list) {
        this.list = list;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "SegwayTrack{" +
                "coordinateType=" + coordinateType +
                ", countryCode='" + countryCode + '\'' +
                ", list=" + Arrays.toString(list) +
                '}';
    }
}
