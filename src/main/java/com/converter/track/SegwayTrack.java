package com.converter.track;

import java.util.Arrays;

/**
 * Data holder class for segway track json files
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
