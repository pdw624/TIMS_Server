package kr.tracom.platform.common.geo;

import lombok.Data;

@Data
public class GeoMath {
    public static double getDistanceBetween(double x1, double y1, double x2, double y2) {
        final double kEarthRadiusKms = 6376.5;

        double lat1_rad = y1 * (Math.PI / 180.0);
        double lng1_rad = x1 * (Math.PI / 180.0);
        double lat2_rad = y2 * (Math.PI / 180.0);
        double lng2_rad = x2 * (Math.PI / 180.0);

        double lat_gap = lat2_rad - lat1_rad;
        double lng_gap = lng2_rad - lng1_rad;

        double mid_val = Math.pow(Math.sin(lat_gap / 2.0), 2.0) +
                Math.cos(lat1_rad) *
                        Math.cos(lat2_rad) *
                        Math.pow(Math.sin(lng_gap / 2.0), 2.0);

        double circle_distance = 2.0 * Math.atan2(Math.sqrt(mid_val), Math.sqrt(1.0 - mid_val));
        double distance = kEarthRadiusKms * circle_distance * 1000;

        return distance;
    }

    public static double getDistanceBetween(GeoXY geo1, GeoXY geo2) {
        return getDistanceBetween(geo1.getX(), geo1.getY(), geo2.getX(), geo2.getY());
    }

    public static int getBearingBetween(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double angle = Math.atan2(dx, dy) * (180.0/Math.PI);

        if(angle < 0)
            angle += 360;

        return (int)angle;
    }

    public static int getDifferBearing(int angle1, int angle2) {
        int angle = Math.abs(angle1 - angle2);
        return (angle > 180) ? (360 - angle) : angle;
    }

    public static GeoDistance getDistanceToLine(double x, double y, double x1, double y1, double x2, double y2) {
        GeoDistance dto = new GeoDistance();
        GeoXY point = getPointToLine(x, y, x1, y1, x2, y2);
        if(point == null) {
            dto.setX(0.0);
            dto.setY(0.0);
            dto.setDistance(9999.0);
        } else {
            dto.setX(point.getX());
            dto.setY(point.getY());
            dto.setDistance(getDistanceBetween(x, y, point.getX(), point.getY()));
        }

        return dto;
    }

    public static GeoXY getPointToLine(double x, double y, double x1, double y1, double x2, double y2) {
        boolean isValid;

        GeoXY point = new GeoXY();

        if (y1 == y2 && x1 == y2) y1 -= 0.00001;
        double U = ((y - y1) * (y2 - y1)) + ((x - x1) * (x2 - x1));
        double Udenom = Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2);
        U /= Udenom;

        point.setY(y1 + (U * (y2 - y1)));
        point.setX(x1 + (U * (x2 - x1)));

        double minx, maxx, miny, maxy;

        minx = Math.min(y1, y2);
        maxx = Math.max(y1, y2);

        miny = Math.min(x1, x2);
        maxy = Math.max(x1, x2);

        isValid = (point.getY() >= minx && point.getY() <= maxx) && (point.getX() >= miny && point.getX() <= maxy);

        return isValid ? point : null;
    }
}
