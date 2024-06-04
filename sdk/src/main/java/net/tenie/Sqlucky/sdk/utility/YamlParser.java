package net.tenie.Sqlucky.sdk.utility;

import java.io.File;
import java.io.IOException;
import java.util.*;

//import org.apache.commons.collections.list.GrowthList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class YamlParser {
    private static final Logger logger = LoggerFactory.getLogger(YamlParser.class);

    /**
     * yml文件流转成单层map
     * 转Properties 改变了顺序
     *
     * @param yamlContent
     * @return
     */
    public static Map<String, Object> yamlToFlattenedMap(String yamlContent) {
        Yaml yaml = createYaml();
        Map<String, Object> map=new HashMap<>();
        for (Object object : yaml.loadAll(yamlContent)) {
            if (object != null) {
                map = asMap(object);
                map=getFlattenedMap(map);
            }
        }
        return map;
    }

    /**
     * yml文件流转成多次嵌套map
     *
     * @param yamlContent
     * @return
     */
    public static Map<String, Object> yamlToMultilayerMap(String yamlContent) {
        Yaml yaml = createYaml();
        Map<String, Object> result = new LinkedHashMap<>();
        for (Object object : yaml.loadAll(yamlContent)) {
            if (object != null) {
                result.putAll(asMap(object));
            }
        }
        return result;
    }

    /**
     * 多次嵌套map转成yml
     */
    public static String multilayerMapToYaml(Map<String, Object> map) {
        Yaml yaml = createYaml();
        return yaml.dumpAsMap(map);
    }

    /**
     * 单层map转成yml
     */
//    public static String flattenedMapToYaml(Map<String, Object> map) {
//        Yaml yaml = createYaml();
//        return yaml.dumpAsMap(flattenedMapToMultilayerMap(map));
//    }

    /**
     * 单层map转换多层map
     */
//    private static Map<String, Object> flattenedMapToMultilayerMap(Map<String, Object> map) {
//        Map<String, Object> result = getMultilayerMap(map);
//        return result;
//    }

    private static Yaml createYaml() {
        return new Yaml();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object object) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (!(object instanceof Map)) {
            result.put("document", object);
            return result;
        }

        Map<Object, Object> map = (Map<Object, Object>) object;
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                value = asMap(value);
            }
            Object key = entry.getKey();
            if (key instanceof CharSequence) {
                result.put(key.toString(), value);
            } else {
                result.put("[" + key.toString() + "]", value);
            }
        }
        return result;
    }

    private static Map<String, Object> getFlattenedMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        buildFlattenedMap(result, source, null);
        return result;
    }

    private static void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, String path) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            if (!StringUtils.isBlank(path)) {
                if (key.startsWith("[")) {
                    key = path + key;
                } else {
                    key = path + '.' + key;
                }
            }
            Object value = entry.getValue();
            if (value instanceof String) {
                result.put(key, value);
            } else if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) value;
                buildFlattenedMap(result, map, key);
            } else if (value instanceof Collection) {
                @SuppressWarnings("unchecked")
                Collection<Object> collection = (Collection<Object>) value;
                int count = 0;
                for (Object object : collection) {
                    buildFlattenedMap(result, Collections.singletonMap("[" + (count++) + "]", object), key);
                }
            } else {
                result.put(key, (value != null ? value.toString() : ""));
            }
        }
    }

//    private static Map<String, Object> getMultilayerMap(Map<String, Object> source) {
//        Map<String, Object> rootResult = new LinkedHashMap<>();
//        for (Map.Entry<String, Object> entry : source.entrySet()) {
//            String key = entry.getKey();
//            buildMultilayerMap(rootResult, key,entry.getValue());
//        }
//        return rootResult;
//    }

//    @SuppressWarnings("unchecked")
//    private static void buildMultilayerMap(Map<String, Object> parent, String path,Object value) {
//        String[] keys = StringUtils.split(path,".");
//        String key = keys[0];
//        if (key.endsWith("]")) {
//            String listKey=key.substring(0,key.indexOf("["));
//            String listPath=path.substring(key.indexOf("["));
//            List<Object> chlid =  bulidChlidList(parent, listKey);
//            buildMultilayerList(chlid, listPath, value);
//        }else{
//            if (keys.length == 1) {
//                parent.put(key, stringToObj(value.toString()));
//            }else{
//                String newpath = path.substring(path.indexOf(".") + 1);
//                Map<String, Object> chlid = bulidChlidMap(parent, key);;
//                buildMultilayerMap(chlid, newpath,value);
//            }
//        }
//    }


//    @SuppressWarnings("unchecked")
//    private static void buildMultilayerList(List<Object> parent,String path,Object value) {
//        String[] keys = StringUtils.split(path,".");
//        String key = keys[0];
//        int index=Integer.valueOf(key.replace("[", "").replace("]", ""));
//        if (keys.length == 1) {
//            parent.add(index,stringToObj(value.toString()));
//        } else {
//            String newpath = path.substring(path.indexOf(".") + 1);
//            Map<String, Object> chlid = bulidChlidMap(parent, index);;
//            buildMultilayerMap(chlid, newpath, value);
//        }
//    }


    @SuppressWarnings("unchecked")
    private static Map<String, Object> bulidChlidMap(Map<String, Object> parent,String key){
        if (parent.containsKey(key)) {
            return (Map<String, Object>) parent.get(key);
        } else {
            Map<String, Object> chlid = new LinkedHashMap<>(16);
            parent.put(key, chlid);
            return chlid;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> bulidChlidMap(List<Object> parent,int index){
        Map<String, Object> chlid = null;
        try{
            Object obj=parent.get(index);
            if(null != obj){
                chlid = (Map<String, Object>)obj;
            }
        }catch(Exception e){
            logger.warn("get list error");
        }

        if (null == chlid) {
            chlid = new LinkedHashMap<>(16);
            parent.add(index,chlid);
        }
        return chlid;
    }

//    @SuppressWarnings("unchecked")
//    private static List<Object> bulidChlidList(Map<String, Object> parent,String key){
//        if (parent.containsKey(key)) {
//            return (List<Object>) parent.get(key);
//        } else {
//            List<Object> chlid = new GrowthList(16);
//            parent.put(key, chlid);
//            return chlid;
//        }
//    }

    private static Object stringToObj(String obj){
        Object result=null;
        if(obj.equals("true") || obj.equals("false")){
            result=Boolean.valueOf(obj);
        }else if(isBigDecimal(obj)){
            if(obj.indexOf(".") == -1){
                result=Long.valueOf(obj.toString());
            }else{
                result=Double.valueOf(obj.toString());
            }
        }else{
            result=obj;
        }
        return result;
    }


    public static boolean isBigDecimal(String str){
        if(str==null || str.trim().length() == 0){
            return false;
        }
        char[] chars = str.toCharArray();
        int sz = chars.length;
        int i = (chars[0] == '-') ? 1 : 0;
        if(i == sz) return false;

        if(chars[i] == '.') return false;//除了负号，第一位不能为'小数点'

        boolean radixPoint = false;
        for(; i < sz; i++){
            if(chars[i] == '.'){
                if(radixPoint) return false;
                radixPoint = true;
            }else if(!(chars[i] >= '0' && chars[i] <= '9')){
                return false;
            }
        }
        return true;
    }

    /**
     * map 转 Properties
     * @param mapVal
     * @return
     */
    public static Properties mapToProperties( Map<String, Object> mapVal){
        Properties properties = new Properties();
        if(mapVal != null && !mapVal.isEmpty()){
            for(var keyVal : mapVal.entrySet()){
                properties.put(keyVal.getKey(), keyVal.getValue());
            }
        }
        return properties;
    }

    /**
     * yaml 字符串转 Properties
     * @param yamlStr
     * @return
     */
    public static Properties yamlToProperties(String yamlStr){
        Map<String, Object> mapVal = YamlParser.yamlToFlattenedMap(yamlStr);
        Properties properties = mapToProperties(mapVal);
        return properties;
    }
    /**
     * 从classPath 下读yaml文件 转 Properties
     * @return
     */
    public static Properties yamlToPropertiesFromClassPath(Class zclss, String classPathFileName){
        String fileName = zclss.getResource(classPathFileName).getFile();
        File file = new File(fileName);
        try {
            String content = Files.toString(file, Charsets.UTF_8);
            Properties properties = yamlToProperties(content);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }




    public static void main(String[] args) throws IOException {
        String content = YamlParser.class.getResource("/application.yml").getFile();
        File file = new File(content);
          content = Files.toString(file, Charsets.UTF_8);
        Properties properties = yamlToProperties(content);
        System.out.println(properties);

//        Map<String, Object> a= YamlParser.yamlToFlattenedMap(content);
//        System.out.println(a);
//        Properties properties = mapToProperties(a);
//        System.out.println(properties);

//        Properties properties = new Properties();
//        properties.
//        Map<String, Object> b = YamlParser.yamlToMultilayerMap(content);
//        System.out.println(b);
//
//
//        String c = YamlParser.multilayerMapToYaml(b);
//        System.out.println(c);
//        String d = YamlParser.flattenedMapToYaml(a);
//        System.out.println(d);

    }



}
