package kr.tracom.platform.common.geo;

import lombok.Data;

@Data
public class GeoXY {
    private double x;
    private double y;
    private String name = "-";

    public GeoXY() {

    }

    public GeoXY(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public GeoXY(double x, double y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }
}
