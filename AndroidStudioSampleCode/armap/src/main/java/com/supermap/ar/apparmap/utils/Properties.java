package com.supermap.ar.apparmap.utils;

import com.google.are.sceneform.rendering.Color;

/**
 * 项目属性配置类
 */
public class Properties {
    // 基础数据路径相关
    public static final String LICENSE_PATH = "/SuperMap/License/";            // 许可证位置

    public static final String DATA_PATH = "/SuperMap/ARdata/ChengduNew/";     // 数据存放位置


    // 地图控件相关
    public static final String WORK_SPACE = "Chengdu.smwu";                    // 工作空间名称

    public static final String DATASOURCE_NAME = "map2";                       // 数据源

    public static final String MAP_NAME = "MapClip";                           // 地图名称

    public static final String MAP_INDEX = "1";                                // 地图索引ID

    public static final String MAP_CENTER = "104.06053619247173,30.603989302011158";   // 地图中心点

    public static final String MAP_SCALE = "0.000015";                         // 地图比例尺


    // 建筑物相关
    public static final String BUILDING = "House_R_1";                         // 可拉高数据集名称

    public static final String BUILDING_INFO_FIELD = "PROVINCE,CITY,COUNTY,ADMINCODE,NAME,ADDRESS,BEIZHU,Height";    // 建筑物属性字段

    public static final Color COLOR_CHOOSE = new Color(0.788f,0.263F,0.019F,1f);  // 选中建筑物时候的颜色（阳光面）

    public static final Color COLOR_CHOOSE_DEEP = new Color(0.690f,0.184F,0.012F,1f);  // 选中建筑物时候的颜色(阴影面)

    public static final Color COLOR_CHOOSE_TOP = new Color(0.933f,0.909f,0.106f,1f);  // 选中建筑物时候的颜色(顶面)
}
