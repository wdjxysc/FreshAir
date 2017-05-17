package com.rthc.freshair.net;

/**
 * Created by Administrator on 2016/7/11.
 */
public class NetConst {
    /**
     *
     * 淘宝物流接口 根据城市区代码获取下属街道
     * https://lsp.wuliu.taobao.com/locationservice/addr/output_address_town_array.do?l1=110000&l2=110103&l3=110101
       callback({success:true,result:[['110101001','东华门街道','110101','dong hua men jie dao'],['110101002','景山街道','110101','jing shan jie dao'],['110101003','交道口街道','110101','jiao dao kou jie dao'],['110101004','安定门街道','110101','an ding men jie dao'],['110101005','北新桥街道','110101','bei xin qiao jie dao'],['110101006','东四街道','110101','dong si jie dao'],['110101007','朝阳门街道','110101','chao yang men jie dao'],['110101008','建国门街道','110101','jian guo men jie dao'],['110101009','东直门街道','110101','dong zhi men jie dao'],['110101010','和平里街道','110101','he ping li jie dao'],['110101011','前门街道','110101','qian men jie dao'],['110101012','崇文门外街道','110101','chong wen men wai jie dao'],['110101013','东花市街道','110101','dong hua shi jie dao'],['110101014','龙潭街道','110101','long tan jie dao'],['110101015','体育馆路街道','110101','ti yu guan lu jie dao'],['110101016','天坛街道','110101','tian tan jie dao'],['110101017','永定门外街道','110101','yong ding men wai jie dao']]});
     */
    public static final String URL_TAOBAO_WULIU = "https://lsp.wuliu.taobao.com/locationservice/addr/output_address_town_array.do";


    /**
     * 地址字符串--->经纬度
     * http://api.map.baidu.com/geocoder/v2/?address=%E5%8C%97%E4%BA%AC%E5%B8%82%E6%B5%B7%E6%B7%80%E5%8C%BA%E4%B8%8A%E5%9C%B0%E5%8D%81%E8%A1%9710%E5%8F%B7&output=json&ak=E4805d16520de693a3fe707cdc962045&callback=showLocation
     * showLocation&&showLocation({"status":0,"result":{"location":{"lng":116.30815063007148,"lat":40.056890127931279},"precise":1,"confidence":80,"level":"道路"}})
     *
     * 经纬度--->地址
     * http://api.map.baidu.com/geocoder/v2/?ak=E4805d16520de693a3fe707cdc962045&callback=renderReverse&location=39.983424,116.322987&output=json&pois=1
     * renderReverse&&renderReverse({"status":0,"result":{"location":{"lng":116.32298699999993,"lat":39.98342407140365},"formatted_address":"北京市海淀区中关村大街27号1101-08室","business":"中关村,人民大学,苏州街","addressComponent":{"adcode":"110108","city":"北京市","country":"中国","direction":"附近","distance":"7","district":"海淀区","province":"北京市","street":"中关村大街","street_number":"27号1101-08室","country_code":0},"pois":[{"addr":"北京北京海淀海淀区中关村大街27号（地铁海淀黄庄站A1","cp":"NavInfo","direction":"内","distance":"0","name":"北京远景国际公寓(中关村店)","poiType":"房地产","point":{"x":116.32294589160056,"y":39.983610361549299},"tag":"房地产","tel":"","uid":"35a08504cb51b1138733049d","zip":""},{"addr":"海淀区中关村北大街27号","cp":" ","direction":"附近","distance":"21","name":"中关村大厦","poiType":"房地产","point":{"x":116.32290995938016,"y":39.98356198726214},"tag":"房地产;写字楼","tel":"","uid":"06d2dffdaef1b7ef88f15d04","zip":""},{"addr":"北京市海淀区中关村大街27号中关村大厦二层","cp":" ","direction":"附近","distance":"5","name":"眉州东坡酒楼","poiType":"美食","point":{"x":116.32293690854546,"y":39.98343759607948},"tag":"美食;中餐厅","tel":"","uid":"2c0bd6c57dbdd3b342ab9a8c","zip":""},{"addr":"北京市海淀区中关村大街29号（海淀黄庄路口）","cp":" ","direction":"东北","distance":"116","name":"海淀医院","poiType":"医疗","point":{"x":116.32234402690887,"y":39.98278799397245},"tag":"医疗;综合医院","tel":"","uid":"fa01e9371a040053774ff1ca","zip":""},{"addr":"中关村大街19号新中关大厦(近丹棱街)","cp":" ","direction":"东南","distance":"179","name":"新中关购物中心","poiType":"购物","point":{"x":116.32172419610699,"y":39.984190850302329},"tag":"购物;购物中心","tel":"","uid":"8d96925c0372e65cc1f1cf7f","zip":""},{"addr":"中关村大街与大泥湾路交叉口东北角","cp":" ","direction":"西","distance":"159","name":"必胜客","poiType":"美食","point":{"x":116.32440114652673,"y":39.983230276934488},"tag":"美食;外国餐厅","tel":"","uid":"c85cfc41edd6631cc5effb84","zip":""},{"addr":"北京市海淀区","cp":" ","direction":"东北","distance":"101","name":"北京大学第三医院海淀院区","poiType":"医疗","point":{"x":116.32249673884557,"y":39.98283636881199},"tag":"医疗;综合医院","tel":"","uid":"98ed26eff408a2e79cec2b8c","zip":""},{"addr":"北京市海淀区丹棱街","cp":" ","direction":"东南","distance":"132","name":"新中关大厦c座","poiType":"房地产","point":{"x":116.32237097607417,"y":39.98421158195154},"tag":"房地产;写字楼","tel":"","uid":"bb566acf61f5a07b1b11d59e","zip":""},{"addr":"北京市海淀区中关村大街28号","cp":" ","direction":"西北","distance":"229","name":"海淀剧院","poiType":"休闲娱乐","point":{"x":116.32476046873072,"y":39.98262213711767},"tag":"休闲娱乐;电影院","tel":"","uid":"edd64ce1a6d799913ee231b3","zip":""},{"addr":"中关村大街28-1号海淀文化艺术大厦A座","cp":" ","direction":"西北","distance":"311","name":"北京市海淀区博物馆","poiType":"旅游景点","point":{"x":116.32543419786319,"y":39.98238026181034},"tag":"旅游景点;博物馆","tel":"","uid":"5b25f446a72b99ea112935ad","zip":""}],"poiRegions":[],"sematic_description":"北京远景国际公寓(中关村店)内0米","cityCode":131}})
     */
    public static final String URL_BAIDU_GEOCODER = "http://api.map.baidu.com/geocoder/v2/";


    /**
     * 服务端ak(key)
     *
     * 因为没有用到SDK, 只是使用的http API, 所以这里使用的是服务ak(key) mcode参数也就不需要了
     * */
    public static final String KYE_BAIDU_SERVER = "93fce2bc97c4bcf1d64a2c76fee410b7";

    /**
     * 百度APIStore API_KEY  wdjxysc
     */
    public static final String KEY_BAIDU_API_KEY = "ec541b7ac34bd007e9b75a8f78f38f61";


    /**
     * 百度APIStore:中国和世界天气预报
     * http://apistore.baidu.com/apiworks/servicedetail/478.html
     */
    public static final String URL_HEWEATHER = "http://apis.baidu.com/heweather/weather/free";


    /**
     * 飘爱项目服务端URL
     */
//    public static final String URL_PIAOAI = "http://120.76.155.77/piaoai/rest/api";
    public static final String URL_PIAOAI = "http://172.19.172.19:20007/piaoai/rest/api";

    public static final int API_RESULT_SUCCESS = 200;
    public static final int API_RESULT_FAIL = 200;

    //获取验证码
    public static final String API_USER_CHECK_CODE = "/user/getCheckCode";

    //注册
    public static final String API_USER_REGISTER = "/user/register";

    //登录
    public static final String API_USER_LOGIN = "/user/login";

    //修改密码
    public static final String API_USER_SET_NEW_PWD = "/user/resetPassword";

    //首页
    public static final String API_USER_FIRST_PAGE = "/user/firstPage";

    //添加设备
    public static final String API_USER_ADD_DEVICE = "/user/addDevice";

    //关于我们
    public static final String API_USER_ABOUT_US = "/user/aboutUs";

    //报修列表
    public static final String API_USER_REPORT_REPAIR_LIST = "/user/reportRepairList";

    //报修详情
    public static final String API_USER_REPORT_REPAIR_DETAIL = "/user/repairReportDetail";

    //提交保修单（用户提交）
    public static final String API_USER_REPORT_REPAIR = "/user/reportRepair";

    //提交保修单（维修工提交）
    public static final String API_USER_REPORT_REPAIR_RESULT = "/user/reportRepairResult";

    //安装设备（安装人员提交）
    public static final String API_USER_INSTALL_DEV = "/user/installDev";

    //设置设备名字
    public static final String API_USER_DEV_SET_NAME = "/user/device/setName";

    //设置定时开机
    public static final String API_USER_DEV_SET_TIMING = "/user/device/setTiming";

    //设置是否启用异常提醒
    public static final String API_USER_DEV_SET_WARNING = "/user/device/setExceptionNotify";

    //删除设备 （解除该设备与当前用户的绑定）
    public static final String API_USER_DEV_DELETE = "/user/device/delete";

    //检查固件更新
    public static final String API_USER_DEV_CHECK_VERSION = "/user/device/checkHardwareVersion";

    //下载文件
    public static final String API_DOWNLOAD_FILE = "/file";

    //获取设备列表
    public static final String API_USER_DEVICE_LIST = "/user/deviceList";

    //获取设备最新数据
    public static final String API_USER_DEVICE_LAST_DATA = "/user/deviceLastData";

    //获取设备历史数据
    public static final String API_USER_DEVICE_HISTORY_DATA = "/user/deviceHistoryData";


}
