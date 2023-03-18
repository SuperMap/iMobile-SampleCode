package com.supermap.ar.apparmap;

import com.google.are.sceneform.rendering.Color;

/**
 * 项目属性配置类
 */
public class PropertiesSuper {
    // 基础数据路径
    public static final String LICENSE_PATH = "/SuperMap/License/";            // 许可证位置

    public static final String DATA_PATH = "/SuperMap/ARdata/Chengdu2/";       // 数据存放位置

    public static final String LOG_PATH = "/SuperMap/ARdata/Chengdu2/log";     // 日志存放位置

    // 地图控件
    public static final String WORK_SPACE = "Sichuan_clip1.smwu";              // 工作空间名称

    public static final String DATASOURCE_NAME = "Sichuan_clip_1";             // 数据源

    public static final String MAP_NAME = "MapClip";                           // 地图名称

    public static final String MAP_INDEX = "1";                                // 地图索引ID

    // 11584719.334590483,3576471.4588109236
    public static final String MAP_CENTER = "11584719.334590483,3576471.4588109236";   // 地图中心点

    public static final String MAP_SCALE = "0.000013";                         // 地图比例尺

    public static final String SELECTED_LAYER_INDEX = "0";                     // 可选中图层ID

    // 建筑物相关
    public static final String BUILDING = "House_R_1";                         // 可拉高数据集名称

    public static final String BUILDING_INFO_FIELD = "PROVINCE,CITY,COUNTY,ADMINCODE,NAME,ADDRESS,BEIZHU,Height";    // 建筑物属性字段

    public static final Color COLOR_CHOOSE = new Color(0.788f,0.263F,0.019F,1f);  // 选中建筑物时候的颜色（阳光面）

    public static final Color COLOR_CHOOSE_DEEP = new Color(0.690f,0.184F,0.012F,1f);  // 选中建筑物时候的颜色(阴影面)

    public static final Color COLOR_CHOOSE_TOP = new Color(0.933f,0.909f,0.106f,1f);  // 选中建筑物时候的颜色(顶面)

    public static final Color COLOR_NEAR_CAR = new Color(0.008f,0.369F,0.812F,1f);  // 小车靠近时的颜色(阳光面)

    public static final Color COLOR_NEAR_CAR_DEEP = new Color(0.012f,0.314F,0.690F,1f);  // 小车靠近时的颜色(阴影面)

    public static final Color COLOR_NEAR_CAR_TOP = new Color(0.290f,0.827f,0.984f,1f);  // 小车靠近时的颜色(顶面)


    // 小车模型相关
    public static final String DATASOURCE_CAR_NAME = "map";                    // 小车行进路线数据源

    public static final String ROAD_LINE = "Road_Line";                        // 小车行进路线数据集

    public static final String CAR_MODEL_NAME = "R.raw.newcar";                // 小车模型资源路径


}
