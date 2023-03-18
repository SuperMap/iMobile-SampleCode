package com.supermap.ar.apparmap.Element;

import android.animation.Animator;
import android.util.Log;

import com.google.are.sceneform.math.Quaternion;
import com.google.are.sceneform.math.Vector3;
import com.supermap.ar.Point3D;
import com.supermap.ar.apparmap.R;
import com.supermap.ar.areffect.ARAnimationGroup;
import com.supermap.ar.areffect.ARAnimationParameter;
import com.supermap.ar.areffect.ARAnimationTranslation2;
import com.supermap.ar.areffect.AREffectElement;
import com.supermap.ar.areffect.ARGltfElement;
import com.supermap.ar.areffect.ARMapElement;
import com.supermap.ar.areffect.ConvertTool;
import com.supermap.ar.areffect.preset.BaseShape;
import com.supermap.ar.areffect.preset.Shape;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.hiar.AREngine;
import com.supermap.mapping.Map;

import java.util.List;
import java.util.Objects;


public class CarEntity {
    private ARAnimationTranslation2 animation;
    private ARAnimationGroup group;

    private AREffectElement carModel;
    private Map map;
    private Point2Ds roadLine;
    private float speed = 0;
    private int count = -1;
    private ARMapElement arMap;
    private Point3D offset = new Point3D();
    private Shape collisionShape;
    private String name;         // 小车名称

    public CarEntity(ARMapElement e) {
        this.arMap = e;
    }

    public Point3D getOffset() {
        return offset;
    }

    public String getName(){ return this.name; }

    public void setName(String name){
        this.name = name;
    }

    public void setOffset(Point3D offset) {
        this.offset = offset;
    }

    public Shape getCollisionShape() {
        return collisionShape;
    }

    public void init(int index){
        carModel = new AREffectElement(arMap.getContext());
        carModel.setParentNode(arMap.getArObjParent());
//        float arMapScale = arMap.getArMapScale();
        float arMapScale = arMap.getARScaleRatio();
        float scale = (float) (arMapScale * 0.16f);
        //创建父节点
        AREffectElement baseShapePar = new AREffectElement(arMap.getContext());
        baseShapePar.setParentNode(carModel);
        baseShapePar.setRelativePosition(new Point3D(0, 0, 0.01f));
        baseShapePar.setScaleFactor(new float[]{0.25f,0.25f,0.25f});

        Shape shape1  = createBuffer(baseShapePar,arMapScale,BaseShape.MatType.OPAQUE,index);
        shape1.setColor(0.043f,0.078f,0.243f,1F);
        shape1.drawTorus(arMapScale*0,arMapScale * 40,80);     // ��Ȧ

        collisionShape = createBuffer(baseShapePar,arMapScale,BaseShape.MatType.OPAQUE,index);
        collisionShape.setColor(0f, 0.169f, 0.4f, 1);
        collisionShape.drawTorus(arMapScale * 48,arMapScale * 57,80);   // ��Ȧ

        ARGltfElement car = new ARGltfElement(arMap.getContext());
        car.setParentNode(carModel);
        car.setScaleFactor(new float[]{scale,scale,scale});
        car.setRelativePosition(new Point3D(0, 0, 0.01f));
        //0.00005/
        car.setRotationAngle(new Quaternion(new Vector3(0,1,0),90));
        car.loadModel(R.raw.truck);

        Objects.requireNonNull(arMap,"ARMapElement was null.");


        /**动画**/
        //创建动画参数
        ARAnimationParameter parameter = new ARAnimationParameter();

        float distanceSum = 0;
        for (int i = 0; i < roadLine.getCount(); i++) {
            Point3D point3D = toArPoint(roadLine.getItem(i), map);
//            point3D.z +=0.032f;
            if (i == 0){
                parameter.setStartPosition(point3D);
            }
            else if (i == roadLine.getCount() - 1) {
                parameter.setEndPosition(point3D);
            }
            else {
                parameter.addWayPoint(point3D);
            }

            if ( i > 0) {
                distanceSum += ConvertTool
                        .getDistance(toArPoint(roadLine.getItem(i - 1),map), point3D);
            }
        }

//        parameter.setDuration(16000L);  // 0.040494513m/s
        parameter.setRepeatCount(count);
        ARAnimationTranslation2 translation = new ARAnimationTranslation2(carModel);
        translation.setSpeed(speed);
        translation.creatAnimation(parameter);

        group.addAnimation(translation);

        //设置动画更新监听
        translation.setTranslationUpdateListener(new ARAnimationTranslation2.TranslationUpdateListener() {
            @Override
            public void onTranslationStart(Animator animation) {
//                carModel.setVisiblity(true);
                Log.i("CAREntity",carModel.toString() + "->start");
            }

            @Override
            public void onUpdateSegment(Animator singleAnimation, int segment, List<Vector3> pList) {
                //segmentΪ��ǰ�����߶ε�����
                Vector3 a = pList.get(segment);
                Vector3 b = pList.get(segment + 1);
                Vector3 subtract = Vector3.subtract(b, a);
                //������ת�Ƕ�
                float angle = (float) Math.toDegrees(Math.atan2(-subtract.z, subtract.x));
                Quaternion rotationQuaternion = arMap.getRotationQuaternion();
                Vector3 vector3 = toEulerAngle(rotationQuaternion);
                Quaternion quaternion;
                //根据不同的AR平台做适配
                if (AREngine.isUsingAREngine()) {
                    quaternion = new Quaternion(new Vector3(0, 1, 0), angle + 90 + vector3.y);
                } else {
                    quaternion = new Quaternion(new Vector3(0, 1, 0), angle - 90 + vector3.y);
                }
                carModel.setRotationAngleRelative(quaternion);
            }

            @Override
            public void onTranslationEnd(Animator animation) {
//                carModel.setVisiblity(false);
                Log.i("CAREntity",carModel.toString() + "->end");
            }
        });
    }


    private Vector3 toEulerAngle(Quaternion q){
        float x = (float) Math.toDegrees(Math.atan2(2*(q.w*q.x + q.y*q.z), 1-2*(q.x * q.x + q.y*q.y)));
        float y = (float) Math.toDegrees(Math.asin( 2 * (q.w * q.y - q.x * q.z)));
        float z = (float) Math.toDegrees(Math.atan2(2*(q.w*q.z + q.x*q.y), 1-2*(q.y * q.y + q.z*q.z)));
        return new Vector3(x,y,z);
    }

    private Shape createBuffer(AREffectElement baseShapePar, float arMapScale, BaseShape.MatType type, int index) {
        Shape shape = new Shape(type);
        shape.setParentNode(baseShapePar);
        shape.setRoughness(1);
        shape.setReflectance(0);
//        shape.getParentElement().setRelativePosition(new Point3D(0,0,index*10));
        return shape;
    }


    private Point3D toArPoint(Point2D point2D, Map map){
        Point point = map.mapToPixel(point2D);
        //����·����
        arMap.checkPoint(point, false);
//        arMap.checkPoint(point,true);
//        Point3D point3D = arMap.convertToArPoint(point);
        Point3D point3D = arMap.getARFromMap(point);
        point3D.x += offset.x;
        point3D.y += offset.y;
        point3D.z += offset.z;
        return point3D;
    }


    public void bindAnimationGroup(ARAnimationGroup animationGroup){
        this.group = animationGroup;
    }

    public AREffectElement getCarModel() {
        return carModel;
    }

    public void setCarModel(ARGltfElement carModel) {
        this.carModel = carModel;
    }

    public Map getMap() {
        return map;
    }

    public void bindMap(Map map) {
        this.map = map;
    }

    public Point2Ds getRoadLine() {
        return roadLine;
    }

    public void setRoadLine(Point2Ds roadLine) {
        this.roadLine = roadLine;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
