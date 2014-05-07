/**
 *@version:2012-11-26-下午06:35:49
 *@author:jianjunwei
 *@date:下午06:35:49
 *
 */
package com.sohu.wap;

import com.sohu.wap.core.Constants;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jianjunwei
 *
 */
public class XueYuanAccount {


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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }


    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public List<YueCheItem> getYueCheItemList() {
        return yueCheItemList;
    }

    public void setYueCheItemList(List<YueCheItem> yueCheItemList) {
        this.yueCheItemList = yueCheItemList;
    }

    public void addYueCheItem(YueCheItem yueCheItem){
        this.yueCheItemList.add(yueCheItem);
    }
    private String password;

    private String carType;


    private String phoneNum; //海驾预留的手机或者电话号码

    List<YueCheItem>  yueCheItemList  = new ArrayList<YueCheItem>();






    //    {
//        "pk": 1,
//            "model": "yueche.xueyuan",
//            "fields": {
//        "car_type": "byd",
//                "phone_num": "18811415862",
//                "name": "徐红敏",
//                "update_date": "2014-04-28T14:43:42Z",
//                "passwd": "33333333333333",
//                "id_num": "410923198702113103",
//                "create_date": "2014-04-28T13:38:03Z",
//                "ding_dan": 1,
//                "jia_xiao": "haijia",
//                "reserve": ""
//    }
//    }
    public static XueYuanAccount jsonToXueYuanAccount(JSONObject json){

        XueYuanAccount yc= new XueYuanAccount();

        yc.setId(json.optInt("pk"));
        JSONObject field = json.optJSONObject("fields");
        yc.setUserName(field.optString("id_num").trim().toUpperCase());
        yc.setPassword(field.optString("passwd").trim());
        yc.setCarType(field.optString("car_type"));
        yc.setPhoneNum(field.optString("phone_num", "").trim());

        return yc;
    }


    @Override
    public String toString() {
        return "XueYuanAccount{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", carType='" + carType + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", yueCheItemList=" + yueCheItemList +
                '}';
    }


}
