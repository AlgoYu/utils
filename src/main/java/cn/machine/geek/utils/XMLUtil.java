package cn.machine.geek.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
* @Author: MachineGeek
* @Description: XML与Map对象的转换工具类
* @Date: 2020/9/27 11:34
*/
public class XMLUtil {
    /**
    * @Author: MachineGeek
    * @Description: 将Map对象转换为XML字符串
    * @Date: 2020/9/27 14:25
    * @param data: 要转换成XML的Map对象
    * @return: java.lang.String
    */
    public static String convertToXML(Map<String, String> data) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder= null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element root = document.createElement("xml");
            document.appendChild(root);
            for (String key: data.keySet()) {
                String value = data.get(key);
                if (value == null) {
                    value = "";
                }
                value = value.trim();
                Element filed = document.createElement(key);
                filed.appendChild(document.createTextNode(value));
                root.appendChild(filed);
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(document);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            String output = writer.getBuffer().toString();

            try {
                writer.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            output = output.substring(output.indexOf("?>")+2,output.length());
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
    * @Author: MachineGeek
    * @Description: 将XML字符串转换为Map对象
    * @Date: 2020/9/27 14:26
    * @param xmlStr: 要转换为Map对象的XML字符串
    * @return: java.util.Map<java.lang.String,java.lang.String>
    */
    public static Map<String, String> convertToMap(String xmlStr) {

        Map<String, String> map = new HashMap<String, String>();
        if (isNullOrEmpty(xmlStr)) {
            throw new IllegalArgumentException("xml is empty");
        } else {
            Document document = null;
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                try {
                    DocumentBuilder documentBuilder= documentBuilderFactory.newDocumentBuilder();
                    InputStream is = new ByteArrayInputStream(xmlStr.getBytes());
                    document  =documentBuilder.parse(is);
                } catch (ParserConfigurationException e) {
                    System.out.println(e.getMessage()+e);
                } catch (org.xml.sax.SAXException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage()+e);
                }

            } catch (IOException e) {
                System.out.println(e.getMessage()+e);
            }


            Element element = document.getDocumentElement();
            if (element != null) {
                NodeList nodeList = element.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    String nodeName = node.getNodeName();
                    String nodeText = node.getTextContent();
                    if("#text".equals(nodeName)){
                        continue;
                    }
                    map.put(nodeName, nodeText);
                }
            }
        }
        return map;
    }

    /**
    * @Author: MachineGeek
    * @Description: 判断XML字符串是否为空
    * @Date: 2020/9/27 14:27
    * @param xmlStr: 需要判断的XML字符串
    * @return: boolean
    */
    public static boolean isNullOrEmpty(String xmlStr) {
        return (null == xmlStr || "".equals(xmlStr));
    }
}
