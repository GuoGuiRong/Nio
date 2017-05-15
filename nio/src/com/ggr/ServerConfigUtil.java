package com.ggr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 用来读取关于服务器相关配置信息
 */
public class ServerConfigUtil {
    //用来存放配置信息
    private static Properties configs=new Properties();
    static{
        //静态信息初始化
        try {
            configs.load(new FileInputStream("nio\\config\\server.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getConfig(String name){
        return configs.getProperty(name);
    }

    public static void main(String[] args){
        System.out.println(ServerConfigUtil.getConfig("port"));
    }
}
