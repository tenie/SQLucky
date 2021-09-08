package net.tenie.lib.reflex;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

/*   @author tenie */
public class BuildObject {
	// 根据类名和类的字段生成bean对象
	@SuppressWarnings("deprecation")
	public static Object buildObject(String classname, Map<String, Object> fieldname) throws Exception {
		Class<?> classobj = Class.forName(classname);
		Object obj = classobj.newInstance();
		Method[] methods = classobj.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String methodName = method.getName();
			if (methodName.startsWith("set")) {
				String key = methodName.substring(3, methodName.length() - 1);
				Object val = fieldname.get(key);
				if (val != null)
					method.invoke(obj, val);
			}
		}
		return obj;
	}

	@SuppressWarnings("rawtypes")
	public static Object buildObj(String classStr, Object parameterObj) throws Exception {
		if(classStr.equals("java.lang.Object")) {
			return parameterObj;
		}
		Class<?> classobj = Class.forName(classStr);
		Constructor cs = classobj.getConstructor(parameterObj.getClass());
		Object o = cs.newInstance(parameterObj);
		return o;

	}

}
