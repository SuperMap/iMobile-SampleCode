package com.supermap.fragment;

import com.supermap.data.TextAlignment;

import java.util.ArrayList;

/**
 * Created by wangli on 2018/10/25.
 */

public class TextStyleProcess {

    /**
     * flag:1 根据字体文件获取中文名字
     * 2： 根据中文名字获取字体文件
     * @return
     */
    public static String getFontName(String name, int flag){
        ArrayList<String> listFontNames = new ArrayList<String>();
        ArrayList<String> listFontFiles = new ArrayList<String>();

        listFontNames.add("黑体");
        listFontNames.add("宋体");
        listFontNames.add("微软雅黑");
        listFontNames.add("仿宋");
        listFontNames.add("幼圆");
        listFontNames.add("华文新魏");
        listFontNames.add("华文中宋");
        listFontNames.add("新罗马");
        listFontNames.add("楷体");

        listFontFiles.add("simhei.ttf");
        listFontFiles.add("simsun.ttc");
        listFontFiles.add("msyh.ttf");
        listFontFiles.add("simfang.ttf");
        listFontFiles.add("SIMYOU.TTF");
        listFontFiles.add("STXINWEI.TTF");
        listFontFiles.add("STZHONGS.TTF");
        listFontFiles.add("times.ttf");
        listFontFiles.add("simkai.ttf");

        if(flag == 1){
            for(int i = 0; i< listFontFiles.size();i++){
                if(name.equals(listFontFiles.get(i))){
                    return  listFontNames.get(i);
                }
            }
        }else if(flag == 2){
            for(int i = 0; i< listFontNames.size();i++){
                if(name.equals(listFontNames.get(i))){
                    return  listFontFiles.get(i);
                }
            }
        }
        return " ";
    }

    /**
     * 根据文本对齐中文返回文本对齐类型
     * @param alignmentText
     * @return
     */
    public static TextAlignment getTextAlignment(String alignmentText){

//        String alignmentText = mPositionCustomData[index].getText();

        if(alignmentText == "左上角" ){
            return  TextAlignment.TOPLEFT;
        }else if(alignmentText == "中上点" ){
            return  TextAlignment.TOPCENTER;
        }else if(alignmentText == "右上角" ){
            return  TextAlignment.TOPRIGHT;
        }else if(alignmentText == "左基线" ){
            return  TextAlignment.BASELINELEFT;
        }else if(alignmentText == "中心基线" ){
            return  TextAlignment.BASELINECENTER;
        }else if(alignmentText == "右基线" ){
            return  TextAlignment.BASELINERIGHT;
        }else if(alignmentText == "左下角" ){
            return  TextAlignment.BOTTOMLEFT;
        }else if(alignmentText == "中下点" ){
            return  TextAlignment.BOTTOMCENTER;
        }else if(alignmentText == "右下角" ){
            return  TextAlignment.BOTTOMRIGHT;
        }else if(alignmentText == "左中点" ){
            return  TextAlignment.MIDDLELEFT;
        }else if(alignmentText == "中心点" ){
            return  TextAlignment.MIDDLECENTER;
        }else if(alignmentText == "右中点" ){
            return  TextAlignment.MIDDLERIGHT;
        }

        return TextAlignment.MIDDLECENTER;
    }

    /**
     * 根据文本对齐类型返回文本对齐中文
     * @param textAlignment
     * @return
     */
    public static String getTextAlignment(TextAlignment textAlignment){

//        String alignmentText = mPositionCustomData[index].getText();

        if(textAlignment == TextAlignment.TOPLEFT ){
            return  "左上角";
        }else if(textAlignment ==  TextAlignment.TOPCENTER){
            return  "中上点";
        }else if(textAlignment == TextAlignment.TOPRIGHT ){
            return  "右上角";
        }else if(textAlignment == TextAlignment.BASELINELEFT ){
            return  "左基线";
        }else if(textAlignment ==  TextAlignment.BASELINECENTER){
            return  "中心基线";
        }else if(textAlignment == TextAlignment.BASELINERIGHT){
            return  "右基线";
        }else if(textAlignment == TextAlignment.BOTTOMLEFT){
            return  "左下角" ;
        }else if(textAlignment == TextAlignment.BOTTOMCENTER){
            return  "中下点";
        }else if(textAlignment == TextAlignment.BOTTOMRIGHT){
            return  "右下角" ;
        }else if(textAlignment == TextAlignment.MIDDLELEFT ){
            return  "左中点";
        }else if(textAlignment == TextAlignment.MIDDLECENTER ){
            return  "中心点";
        }else if(textAlignment == TextAlignment.MIDDLERIGHT ){
            return  "右中点";
        }

        return "";
    }
}
