package com.supermap.imobile.streamnode;

import java.util.HashMap;

public class FeatureMapMapper extends StreamNode {

    public FeatureMapMapper() {
        this.className = "com.supermap.bdt.streaming.map.FeatureMapMapper";
    }

    /**
     * nextNodes : []
     * prevNodes : []
     * srcToDesIndexPair : {"key01":1,"key02":2}
     * srcToDesNamePair : {"key01":"value01","key02":"value02"}
     */

    private SrcToDesIndexPairBean srcToDesIndexPair;
    private SrcToDesNamePairBean srcToDesNamePair;

    public SrcToDesIndexPairBean getSrcToDesIndexPair() {
        return srcToDesIndexPair;
    }

    public void setSrcToDesIndexPair(SrcToDesIndexPairBean srcToDesIndexPair) {
        this.srcToDesIndexPair = srcToDesIndexPair;
    }

    public SrcToDesNamePairBean getSrcToDesNamePair() {
        return srcToDesNamePair;
    }

    public void setSrcToDesNamePair(SrcToDesNamePairBean srcToDesNamePair) {
        this.srcToDesNamePair = srcToDesNamePair;
    }

    public static class SrcToDesIndexPairBean {
        /**
         * key01 : 1
         * key02 : 2
         * ...
         */

        private HashMap<String, Integer> hashMap;

        public HashMap<String, Integer> gethashMap() {
            return hashMap;
        }

        public void setKey01(HashMap<String, Integer> hashMap) {
            this.hashMap = hashMap;
        }
    }

    public static class SrcToDesNamePairBean {
        /**
         * key01 : value01
         * key02 : value02
         * ...
         */

        private HashMap<String, String> hashMap;

        public HashMap<String, String> getHashMap() {
            return hashMap;
        }

        public void setHashMap(HashMap<String, String> hashMap) {
            this.hashMap = hashMap;
        }

    }
}
