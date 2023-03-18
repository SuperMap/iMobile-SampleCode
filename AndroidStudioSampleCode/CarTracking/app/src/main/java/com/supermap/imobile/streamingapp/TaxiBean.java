package com.supermap.imobile.streamingapp;

import java.util.List;

public class TaxiBean {

    /**
     * type : Feature
     * properties : {"a":0,"datetime":"2007/2/20 0:07","b":45,"c":0,"x":121.4695,"y":31.2215,"id":"105"}
     * geometry : {"type":"Point","coordinates":[121.4695,31.2215]}
     */

    private String type;
    private PropertiesBean properties;
    private GeometryBean geometry;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PropertiesBean getProperties() {
        return properties;
    }

    public void setProperties(PropertiesBean properties) {
        this.properties = properties;
    }

    public GeometryBean getGeometry() {
        return geometry;
    }

    public void setGeometry(GeometryBean geometry) {
        this.geometry = geometry;
    }

    public static class PropertiesBean {
        /**
         * a : 0
         * datetime : 2007/2/20 0:07
         * b : 45
         * c : 0
         * x : 121.4695
         * y : 31.2215
         * id : 105
         */

        private int a;
        private String datetime;
        private int b;
        private int c;
        private double x;
        private double y;
        private String id;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        public int getC() {
            return c;
        }

        public void setC(int c) {
            this.c = c;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class GeometryBean {
        /**
         * type : Point
         * coordinates : [121.4695,31.2215]
         */

        private String type;
        private List<Double> coordinates;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Double> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Double> coordinates) {
            this.coordinates = coordinates;
        }
    }
}
