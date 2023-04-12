package net.tenie.Sqlucky.sdk.utility;
 
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.extern.slf4j.Slf4j;
import net.tenie.Sqlucky.sdk.po.component.xmlPdmModel;

/**
 * @ClassName: XMLUtil
 * @Description:生成XML文件，使用Jdom2进行节点的拼装，写入xml文件，注意添加outStream.close();防止java占用文件不释放，导致内存溢出。 实现java对象和xml的互转操作。
 * @author: 郭秀志 jbcode@126.com
 * @date: 2020年1月8日 上午10:47:31
 * @Copyright:
 */
@Slf4j
public class XmlUtil {

    public static XmlMapper xmlMapper = new XmlMapper();
    ObjectMapper om = new ObjectMapper();
    static {
       	/**
    	//反序列化时，若实体类没有对应的属性，是否抛出JsonMappingException异常，false忽略掉
    	xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	//序列化是否绕根元素，true，则以类名为根元素
    	xmlMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    	//忽略空属性
    	xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    	//XML标签名:使用骆驼命名的属性名，
    	xmlMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
    	//设置转换模式
    	xmlMapper.enable(MapperFeature.USE_STD_BEAN_NAMING);
    	    	 */
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        xmlMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        xmlMapper.enable(MapperFeature.USE_STD_BEAN_NAMING);
    }

    /**
     * 将xml转为bean对象 
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static <T> T xmlToBean(String input, Class<T> cls) throws IOException {
        return xmlMapper.readValue(input, cls);
    }

    /**
     * 将bean转为xml字符串，bean需要配置注解@JacksonXmlProperty等。
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static String beanToXmlStr(Object input) throws IOException {
        String xmlStr = xmlMapper.writeValueAsString(input);
        return xmlStr;
    }

    /**
     * 将bean的xml字符串转为map，bean需要配置注解@JacksonXmlProperty等。
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static Map<String, Object> beanToXmlStrToMap(Object input) throws IOException {
        String xmlStr = xmlMapper.writeValueAsString(input);
        Map<String, Object> map = xmlMapper.readValue(xmlStr, Map.class);
        return map;
    }

    /*
     * @Description 读取文件成XML格式。
     * @Param [fileName]
     * @return java.lang.String
     */
    public static String xmlFileToString(String fileName) {
        try {
            SAXReader saxReader = new SAXReader();//新建一个解析类
            org.dom4j.Document tempDocument = saxReader.read(fileName);//读入一个文件
            return tempDocument.asXML();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Description 字符串输出到XML文件。
     * @return void
     * @Param [str, fileName]
     */
    public static void strToXmlFile(String str, File fileName) throws IOException {
        SAXReader saxReader = new SAXReader();
        org.dom4j.Document document;
        XMLWriter writer = null;
        try {
            document = saxReader.read(new ByteArrayInputStream(str.getBytes("UTF-8")));
            OutputFormat format = OutputFormat.createPrettyPrint();
            /** 将document中的内容写入文件中 */
            writer = new XMLWriter(new FileWriter(fileName), format);
            writer.write(document);
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

    }
    
    public static void main(String[] args) throws IOException {
//    	//构造数据模型实例并赋值
//        XmlSendFileModel xmlSendFileModel = XmlSendFileModel.builder(). 
//                bankType("SELF").
//                moneySum(new BigDecimal(20)).
//                totalIty(new BigDecimal(20)).build();
//        //调用util类把bean对象转换成XML字符串
//        String xmlStr = XMLUtil.beanToXmlStr(xmlSendFileModel);
//        // 写入xml字符串到文件。
//        XMLUtil.strToXmlFile(xmlStr, new File("D:\\myGit\\xmltext.xml"));
        log.info("xml文件生成完毕");
//
//        //读取一个XML文件成XML字符串格式。
////        log.info("输出XML文件的字符串 = " + XMLUtil.xmlFileToString("D:\\myGit\\xmltext.xml"));
//        String xmlFileToString = XmlUtil.xmlFileToString("D:\\myGit\\xmltext.xml");
    	  String xmlFileToString = XmlUtil.xmlFileToString("D:\\workDir\\data\\InfoDMS&GMS_DB2.pdm");
//        
//        
//        //读取一个XML文件成XML字符串格式。
////        String xmlFileToString = XmlUtil.xmlFileToString(file.getAbsolutePath());
        xmlPdmModel model = XmlUtil.xmlToBean(xmlFileToString, xmlPdmModel.class);
        
//       System.out.println(model);
       System.out.println(model.getoRootObject().getcChildren().size());
       System.out.println(model.getoRootObject().getcChildren().get(0).getcPackages().size());

       System.out.println(model.getoRootObject().getcChildren().get(0).getcPackages().get(0).getcTables().size());

       System.out.println(model.getoRootObject().getcChildren().get(0).getcPackages().get(1).getcTables().size());
       System.out.println(model.getoRootObject().getcChildren().get(0).getcPackages().get(2).getcTables());
       System.out.println(model.getoRootObject().getcChildren().get(0).getcPackages().get(3));
	}
    
    
}