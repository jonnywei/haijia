/**
 *@version:2012-11-26-下午06:35:49
 *@author:jianjunwei
 *@date:下午06:35:49
 *
 */
package com.sohu.wap;

import org.json.JSONObject;

import com.sohu.wap.core.Constants;

/**
 *
 * @author jianjunwei
 *
 */
public class YueCheItem {


    //1开始
    public static int BOOK_CAR_NOT_SET = -1;

    public static int BOOK_CAR_SUCCESS = 0;

    public static int BOOK_CAR_ALREADY_BOOKED_CAR=1;

    public static int BOOK_CAR_KEMU2_NO_TIME = 2;

    public static int BOOK_CAR_NOT_BOOK_WEEKEND_CAR= 3;

    public static int BOOK_CAR_NO_CAR =4;

    public static int BOOK_CAR_NO_FAIL =5;

    public static int BOOK_CAR_ERROR = 6;

    public static int BOOK_CAR_ACCOUNT_ERROR = 7;

    public static int BOOK_CAR_PHONE_NUM_ERROR=8;

    public static int BOOK_CAR_CAR_TYPE_ERROR=9;
    private int id;

    private String userName;

    private String password;

    private String carType;

    private String yueCheDate;

    private String amPm;

    private String whiteCar;

    private String blackCar;

    private String km ="km2";

    private int ycResult;

    private String ycResultInfo;

    private String phoneNum; //海驾预留的手机或者电话号码

    private String xueYuanId;

    private boolean isBookSuccess = false;
//
//    {
//        "pk": 1,
//            "model": "yueche.yueche",
//            "fields": {
//        "update_date": "2014-04-28T16:00:52Z",
//                "yc_date": "2014-05-06",
//                "black_car": "",
//                "xue_yuan": 1,
//                "yc_info": "",
//                "white_car": "",
//                "yc_km": "km2",
//                "yc_result": null,
//                "create_date": "2014-04-28T13:39:26Z",
//                "yc_time": "am",
//                "reserve": "d er"
//    }
//    }

    public static YueCheItem jsonToYueCheItem(JSONObject json){

        YueCheItem yc= new YueCheItem();

        yc.setId(json.optInt("pk"));
        JSONObject field = json.optJSONObject("fields");
        yc.setXueYuanId(String.valueOf(field.optInt("xue_yuan")) );
        yc.setYueCheDate(field.optString("yc_date").replace("-", ""));
        if(field.isNull("yc_result")){
            yc.setYcResult(BOOK_CAR_NOT_SET);
        }else{
            yc.setYcResult(field.optInt("yc_result"));
        }
        yc.setYcResultInfo(field.optString("yc_info"));
        yc.setKm(field.optString("yc_km", Constants.KM2));
        yc.setAmPm(field.optString("yc_time"));
        yc.setWhiteCar(field.optString("white_car"));
        yc.setBlackCar(field.optString("black_car"));



        return yc;
    }

    public void  addXueYuanDetailInfo( XueYuanAccount xueYuanAccount){

        this.setUserName(xueYuanAccount.getUserName());
        this.setPassword(xueYuanAccount.getPassword());
        this.setCarType(xueYuanAccount.getCarType());
        this.setPhoneNum(xueYuanAccount.getPhoneNum());
    }

    @Override
    public String toString() {
        return "YueCheItem{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", carType='" + carType + '\'' +
                ", yueCheDate='" + yueCheDate + '\'' +
                ", amPm='" + amPm + '\'' +
                ", whiteCar='" + whiteCar + '\'' +
                ", blackCar='" + blackCar + '\'' +
                ", km='" + km + '\'' +
                ", ycResult=" + ycResult +
                ", ycResultInfo='" + ycResultInfo + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", xueYuanId='" + xueYuanId + '\'' +
                ", isBookSuccess=" + isBookSuccess +
                '}';
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }
    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * @return the isBookSuccess
     */
    public boolean isBookSuccess() {
        return isBookSuccess;
    }
    /**
     * @param isBookSuccess the isBookSuccess to set
     */
    public void setBookSuccess(boolean isBookSuccess) {
        this.isBookSuccess = isBookSuccess;
    }
    public String getCarType() {
        return carType;
    }
    public void setCarType(String carType) {
        this.carType = carType;
    }
    public String getYueCheDate() {
        return yueCheDate;
    }
    public void setYueCheDate(String yueCheDate) {
        this.yueCheDate = yueCheDate;
    }
    public String getYueCheAmPm() {
        return amPm;
    }
    public void setYueCheAmPm(String amPm) {
        this.amPm = amPm;
    }
    /**
     * @param whiteCar the whiteCar to set
     */
    public void setWhiteCar(String whiteCar) {
        this.whiteCar = whiteCar;
    }
    /**
     * @return the whiteCar
     */
    public String getWhiteCar() {
        return whiteCar;
    }
    public String getAmPm() {
        return amPm;
    }
    public void setAmPm(String amPm) {
        this.amPm = amPm;
    }
    public String getKm() {
        return km;
    }
    public void setKm(String km) {
        this.km = km;
    }

    public String getBlackCar() {
        return blackCar;
    }
    public void setBlackCar(String blackCar) {
        this.blackCar = blackCar;
    }
    public int getYcResult() {
        return ycResult;
    }
    public void setYcResult(int ycResult) {
        this.ycResult = ycResult;
    }
    public String getYcResultInfo() {
        return ycResultInfo;
    }
    public void setYcResultInfo(String ycResultInfo) {
        this.ycResultInfo = ycResultInfo;
    }
    public String getPhoneNum() {
        return phoneNum;
    }
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getXueYuanId() {
        return xueYuanId;
    }

    public void setXueYuanId(String xueYuanId) {
        this.xueYuanId = xueYuanId;
    }
}
