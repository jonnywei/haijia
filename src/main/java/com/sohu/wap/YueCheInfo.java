/**
 *@version:2012-5-8-下午03:03:50
 *@author:jianjunwei
 *@date:下午03:03:50
 *
 */
package com.sohu.wap;

import java.util.concurrent.ConcurrentHashMap;

import com.sohu.wap.util.DateUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.core.Constants;
import com.sohu.wap.http.HttpUtil4Exposer;




/**
 * 用户约车(*^__^*) 嘻嘻……
 *
 * @author jianjunwei
 *
 */
public class YueCheInfo {



    private static Logger  log = LoggerFactory.getLogger(YueCheInfo.class);

    private String ycDate ;

    private String carType;

    private    ConcurrentHashMap <Integer , YueCheItem>  yueCheInfoMap   = new ConcurrentHashMap<Integer, YueCheItem> ();


    public  YueCheInfo (String carType, String ycDate){
        this.carType = carType;
        this.ycDate = ycDate;
        init();
    }


    public ConcurrentHashMap  <Integer , YueCheItem>   getYueCheInfo(){
        return yueCheInfoMap;
    }


    /**
     *
     * 初始化 account 对象 
     *
     */
    private  void init (){

        initYueCheInfos();
    }


    private void initYueCheInfos(){
        try {

            JSONArray ycArray = new JSONArray();

            String yueCheUrl = String.format(Constants.YUECHE_URL, DateUtil.getDashDate(ycDate));
            String  ycInfo  = HttpUtil4Exposer.getInstance().getContent(yueCheUrl);
            if(ycInfo != null){
                ycArray  = new JSONArray(ycInfo);

            }else{
                log.error("get yuche info from server error");
                return;
            }
            for (int index =0; index < ycArray.length(); index ++){

                JSONObject yc =  ycArray.getJSONObject(index);

                YueCheItem yueCheItem = YueCheItem.jsonToYueCheItem(yc);

                String xueYuanDetailUrl =   String.format(Constants.XUEYUAN_DETAIL_URL, Integer.valueOf(yueCheItem.getXueYuanId()) );

                String  xueYuanDetailInfo  = HttpUtil4Exposer.getInstance().getContent(xueYuanDetailUrl);

                if(xueYuanDetailInfo != null){
                    JSONArray  xueYuanArray  = new JSONArray(xueYuanDetailInfo);
                    XueYuanAccount  xueYuanAccount = XueYuanAccount.jsonToXueYuanAccount(xueYuanArray.getJSONObject(0)) ;
                    yueCheItem.addXueYuanDetailInfo( xueYuanAccount);
                }
                if (carType ==null || carType.equals(yueCheItem.getCarType())){
                    System.out.println(yueCheItem);
                    yueCheInfoMap.put(yueCheItem.getId(), yueCheItem);
                }


            }
        } catch (JSONException e) {
            log.error("init yueche account from net error", e);
        }
        log.info("initYueCheInfos over! size=" +  yueCheInfoMap.size() );
    }


    public static void main(String[] args){
        YueCheInfo ycInfo = new YueCheInfo(null,"20140512");
    }





}
