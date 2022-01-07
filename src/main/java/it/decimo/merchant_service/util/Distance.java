package it.decimo.merchant_service.util;

import org.springframework.data.geo.Point;

public class Distance {

    /**
     * Calcola la distanza in linea d'aria in metri fra due punti
     *
     * @param pointA Il punto di partenza
     * @param pointB Il punto di arrivo
     * @return La distanza in metri
     */
    public static double gps2m(Point pointA, Point pointB) {
        float pk = (float) (180 / 3.14169);

        double a1 = pointA.getX() / pk;
        double a2 = pointA.getY() / pk;
        double b1 = pointB.getX() / pk;
        double b2 = pointB.getY() / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

}
