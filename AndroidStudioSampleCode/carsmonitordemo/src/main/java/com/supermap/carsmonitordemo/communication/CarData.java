package com.supermap.carsmonitordemo.communication;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * 车辆信息类
 *
 */
public class CarData implements Parcelable {


	// 状态 1：正常   2：异常  3：超速
	private int mState = 0;
	private String mCarName ="";
	private String mCarNo = "";
	private double mX;
	private double mY;
	private String mPhoneNo = "";

	public void setCarName(String carName) {
		mCarName = carName;
	}

	public String getCarName() {
		return mCarName;
	}

	public void setCarNo(String carNo) {
		mCarNo = carNo;
	}

	public String getCarNo() {
		return mCarNo;
	}

	public double getX() {
		return mX;
	}

	public void setX(double x) {
		mX = x;
	}

	public double getY() {
		return mY;
	}

	public void setY(double y) {
		mY = y;
	}

	public int getState() {
		return mState;
	}

	public void setState(int state) {
		mState = state;
	}

	public static final Parcelable.Creator<CarData> CREATOR = new Creator<CarData>(){

		@Override
		public CarData createFromParcel(Parcel source) {
			CarData cus = new CarData();
			cus.mCarName = source.readString();
			cus.mCarNo = source.readString();
			cus.mPhoneNo = source.readString();
			cus.mState = source.readInt();
			cus.mX = source.readDouble();
			cus.mY = source.readDouble();
			return cus;
		}

		@Override
		public CarData[] newArray(int size) {
			// TODO Auto-generated method stub
			return new CarData[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		// TODO Auto-generated method stub
		dest.writeString(mCarName);
		dest.writeString(mCarNo);
		dest.writeString(mPhoneNo);
		dest.writeInt(mState);
		dest.writeDouble(mX);
		dest.writeDouble(mY);
	}

	public String getPhoneNo() {
		return mPhoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.mPhoneNo = phoneNo;
	}


}
