package cn.machine.geek.test;

import cn.machine.geek.utils.RandomUtil;
import cn.machine.geek.utils.WeChatPayUtil;
import cn.machine.geek.utils.XMLUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author: MachineGeek
 * @Description:
 * @Date: 2020/9/27 11:53
 */
public class Main {
    public static void main(String[] args) {
        Map<String,String> map = new HashMap<>();
        map.put("appid","wxd2fd816265991109");
        map.put("mch_id","1336573601");
        map.put("nonce_str", RandomUtil.generateRandomString(10));
        map.put("notify_url","asd");
        map.put("trade_type","JSAPI");
        map.put("sign",WeChatPayUtil.weChatPaySign(new TreeMap<>(map),"e19e6c2b71cc48978b72709bfa4a8888"));
        System.out.println(XMLUtil.convertToXML(map));
    }
}
