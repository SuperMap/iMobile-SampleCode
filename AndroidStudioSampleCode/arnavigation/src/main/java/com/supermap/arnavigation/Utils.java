package com.supermap.arnavigation;

import com.supermap.data.Point3D;

public class Utils {
  /**
   * 通过坐标旋转校正
   * <p>AREngine场景相对ARCore的场景，会有一个角度偏移，此处对坐标进行旋转，使其与ARCore一致</p>
   */
  public static com.supermap.data.Point3D correctPosition(float offsetAngle, com.supermap.data.Point3D position){
    //平面中，一个点(x,y)绕任意点(dx,dy)逆时针旋转a度后的坐标
    //xx= (x - dx)*cos(a) - (y - dy)*sin(a) + dx ;
    //yy= (x - dx)*sin(a) + (y - dy)*cos(a) +dy ;
    double x = position.getX();
    double y = position.getY();
    double xx,yy;
    xx= x * Math.cos(Math.toRadians(-offsetAngle)) - y * Math.sin(Math.toRadians(-offsetAngle));
    yy= x * Math.sin(Math.toRadians(-offsetAngle)) + y * Math.cos(Math.toRadians(-offsetAngle));
    return new Point3D(xx,yy,position.getZ() );
  }

  /**
   * 通过坐标旋转校正
   * <p>AREngine场景相对ARCore的场景，会有一个角度偏移，此处对坐标进行旋转，使其与ARCore一致</p>
   */
  public static com.supermap.ar.Point3D correctPosition(float offsetAngle, com.supermap.ar.Point3D position){
    float x = position.x;
    float y = position.y;
    float xx,yy;
    xx= (float) (x * Math.cos(Math.toRadians(-offsetAngle)) - y * Math.sin(Math.toRadians(-offsetAngle)));
    yy= (float) (x * Math.sin(Math.toRadians(-offsetAngle)) + y * Math.cos(Math.toRadians(-offsetAngle)));
    return new com.supermap.ar.Point3D(xx,yy,position.z);
  }
}
