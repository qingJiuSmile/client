package com.webservice.client.service;
 
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
 
public class WeatherUtil {
    
    /**
     * 对服务器端返回的XML文件流进行解析
     * 
     * @param city 用户输入的城市名称
     * @return          字符串 用#分割
     */
    public String getWeather(String city,String city1) {
        try {
            //使用Dom解析
            Document doc;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            //获取调用接口后返回的流
            InputStream is = getSoapInputStream(city,city1);
      /*      BufferedReader bf = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sbf = new StringBuffer();
            String temp = null;
            while ((temp = bf.readLine()) != null) {
                sbf.append(temp);
                sbf.append("\r\n");
            }
            return sbf.toString();*/
            doc = db.parse(is);
            //xml的元素标签是"<string>值1</string><string>值2</string>……"
            NodeList nl = doc.getElementsByTagName("string");
            StringBuffer sb = new StringBuffer();
            for (int count = 0; count < nl.getLength(); count++) {
                Node n = nl.item(count);
                if(n.getFirstChild().getNodeValue().equals("查询结果为空！")) {
                    sb = new StringBuffer("#") ;
                    break ;
                }
                //解析并以"#"为分隔符,拼接返回结果
                sb.append(n.getFirstChild().getNodeValue() + "#");
            }
            is.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /*
    * 用户把SOAP请求发送给服务器端，并返回服务器点返回的输入流
    * 
    * @param city  用户输入的城市名称
    * @return 服务器端返回的输入流，供客户端读取
    * @throws Exception
    * @备注：有四种请求头格式1、SOAP 1.1；  2、SOAP 1.2 ； 3、HTTP GET； 4、HTTP POST
    * 参考---》http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?op=getWeatherbyCityName
    */
    private InputStream getSoapInputStream(String city,String city1) throws Exception {
        try {
            //获取请求规范
            String soap = getSoapRequest(city,city1);
            if (soap == null) {
                return null;
            }
            //调用天气的地址
            String soapActionURITQ  = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl";
            //火车
            String soapActionURIHC = "http://www.webxml.com.cn/WebServices/TrainTimeWebService.asmx?wsdl";
            String methodTQ = "getWeatherbyCityName";
            String methodHC = "getStationAndTimeByStationName";
            //调用的天气预报webserviceURL
            URL url = new URL(soapActionURITQ);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Length", Integer.toString(soap.length()));
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            //调用的接口方法是“getWeatherbyCityName”
            conn.setRequestProperty("SOAPAction", "http://WebXml.com.cn/"+methodTQ);
            OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");
            osw.write(soap);
            osw.flush();
            osw.close();
            //获取webserivce返回的流
            InputStream is = conn.getInputStream();
            return is;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /*
    * 获取SOAP的请求头，并替换其中的标志符号为用户输入的城市
    * 
    * @param city： 用户输入的城市名称
    * @return              客户将要发送给服务器的SOAP请求规范
    * @备注：有四种请求头格式1、SOAP 1.1；  2、SOAP 1.2 ； 3、HTTP GET； 4、HTTP POST
    * 参考---》http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?op=getWeatherbyCityName
    * 本文采用：SOAP 1.1格式
    */
    private String getSoapRequest(/*String city,*/String startStation,String arriveStation) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
                + "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<soap:Body><getWeatherbyCityName xmlns=\"http://WebXml.com.cn/\">"
                + "<theCityName>"
                + startStation
                + "</theCityName></getWeatherbyCityName>"
                + "</soap:Body></soap:Envelope>");
/*       sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
               "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
               "  <soap:Body>\n" +
               "    <getStationAndTimeByStationName xmlns=\"http://WebXml.com.cn/\">\n" +
               "      <StartStation>"+ startStation +"</StartStation>\n" +
               "      <ArriveStation>"+ arriveStation +"</ArriveStation>\n" +
               "      <UserID></UserID>\n" +
               "    </getStationAndTimeByStationName>\n" +
               "  </soap:Body>\n" +
               "</soap:Envelope>");*/
        return sb.toString();
    }
    
}