package com.supermap.imobile.bean;

import java.util.List;

public class WebMapBean {

    /**
     * extent : {"leftBottom":{"x":7851611.545458,"y":2436200.9655012},"rightTop":{"x":1.489604807222E7,"y":6320424.9948408}}
     * level : 3
     * center : {"x":1.258477566104697E7,"y":3412439.146125613}
     * baseLayer : {"layerType":"TILE","name":"China","url":"http://rdc.ispeco.com:8080/iserver/services/map-china400/rest/maps/China"}
     * layers : [{"layerType":"TILE","visible":true,"name":"Population","url":"http://rdc.ispeco.com/iserver/services/map_hubei/rest/maps/Population"},{"layerType":"TILE","visible":true,"name":"2014年人口密度专题图","url":"http://rdc.ispeco.com/iserver/services/map_population-and-economy2/rest/maps/2014年人口密度专题图"}]
     * description : 湖北人口分布示例
     * projection : EPSG:3857
     * title : 湖北-Population
     * version : 1.0
     */

    private ExtentBean extent;
    private double level;
    private CenterBean center;
    private BaseLayerBean baseLayer;
    private String description;
    private String projection;
    private String title;
    private String version;
    private List<LayersBean> layers;

    public ExtentBean getExtent() {
        return extent;
    }

    public void setExtent(ExtentBean extent) {
        this.extent = extent;
    }

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public CenterBean getCenter() {
        return center;
    }

    public void setCenter(CenterBean center) {
        this.center = center;
    }

    public BaseLayerBean getBaseLayer() {
        return baseLayer;
    }

    public void setBaseLayer(BaseLayerBean baseLayer) {
        this.baseLayer = baseLayer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjection() {
        return projection;
    }

    public void setProjection(String projection) {
        this.projection = projection;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<LayersBean> getLayers() {
        return layers;
    }

    public void setLayers(List<LayersBean> layers) {
        this.layers = layers;
    }

    public static class ExtentBean {
        /**
         * leftBottom : {"x":7851611.545458,"y":2436200.9655012}
         * rightTop : {"x":1.489604807222E7,"y":6320424.9948408}
         */

        private LeftBottomBean leftBottom;
        private RightTopBean rightTop;

        public LeftBottomBean getLeftBottom() {
            return leftBottom;
        }

        public void setLeftBottom(LeftBottomBean leftBottom) {
            this.leftBottom = leftBottom;
        }

        public RightTopBean getRightTop() {
            return rightTop;
        }

        public void setRightTop(RightTopBean rightTop) {
            this.rightTop = rightTop;
        }

        public static class LeftBottomBean {
            /**
             * x : 7851611.545458
             * y : 2436200.9655012
             */

            private double x;
            private double y;

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
        }

        public static class RightTopBean {
            /**
             * x : 1.489604807222E7
             * y : 6320424.9948408
             */

            private double x;
            private double y;

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
        }
    }

    public static class CenterBean {
        /**
         * x : 1.258477566104697E7
         * y : 3412439.146125613
         */

        private double x;
        private double y;

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
    }

    public static class BaseLayerBean {
        /**
         * layerType : TILE
         * name : China
         * url : http://rdc.ispeco.com:8080/iserver/services/map-china400/rest/maps/China
         */

        private String layerType;
        private String name;
        private String url;

        public String getLayerType() {
            return layerType;
        }

        public void setLayerType(String layerType) {
            this.layerType = layerType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class LayersBean {
        /**
         * layerType : TILE
         * visible : true
         * name : Population
         * url : http://rdc.ispeco.com/iserver/services/map_hubei/rest/maps/Population
         */

        private String layerType;
        private boolean visible;
        private String name;
        private String url;

        public String getLayerType() {
            return layerType;
        }

        public void setLayerType(String layerType) {
            this.layerType = layerType;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
