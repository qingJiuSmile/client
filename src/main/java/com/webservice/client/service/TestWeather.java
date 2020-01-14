package com.webservice.client.service;

public class TestWeather {
    /**
    * 测试
    */
    public static void main(String[] args) throws Exception {
        WeatherUtil weath=new WeatherUtil();
        //查看城市：济南
        String weather=weath.getWeather("济南","烟台");
        String len[]=weather.split("#");
        for(int i=0;i<len.length-1;i++){
        System.out.println(len[i]);
        }
    }
}