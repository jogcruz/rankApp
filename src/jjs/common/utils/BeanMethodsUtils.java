package jjs.common.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class BeanMethodsUtils
{

	//	**********************************************************************************************************************************************************
	//	STARTING SECTION	-->									CONSTRUCTORS
	//	**********************************************************************************************************************************************************

	private BeanMethodsUtils()
	{
		
	}
	
	//	**********************************************************************************************************************************************************
	//	ENDING SECTION		-->									CONSTRUCTORS
	//	**********************************************************************************************************************************************************

	//	**********************************************************************************************************************************************************
	//	STARTING SECTION	-->									METHODS
	//	**********************************************************************************************************************************************************

	public static Map<String, Method> getSetterPropertiesMap(Class<?> targetClass, boolean normalizeKeys)
	{
		Map<String, Method> mapProperties = new HashMap<String, Method>();
	
		if (targetClass != null)
		{
			Method[] declaredMethods = targetClass.getDeclaredMethods();
			
			if ((declaredMethods != null) && (declaredMethods.length > 0))
			{
				for (Method method : declaredMethods) 
				{
					String methodName = method.getName();
					
					if (methodName.startsWith("set") && (method.getGenericParameterTypes().length == 1) && (method.getReturnType() == void.class))
					{
						methodName = normalizeKeys ? methodName.toLowerCase() : methodName;
						
						if (!mapProperties.containsKey(methodName))
						{
							mapProperties.put(methodName, method);
						}
					}
				}
			}
			
			if (targetClass.getSuperclass() != null)
			{
				mapProperties.putAll(getSetterPropertiesMap(targetClass.getSuperclass(), normalizeKeys));
			}
		}
		
		return mapProperties;
	}
	
	public static Map<String, Method> getGetterPropertiesMap(Class<?> targetClass, boolean normalizeKeys)
	{
		Map<String, Method> mapProperties = new HashMap<String, Method>();
	
		if (targetClass != null)
		{
			Method[] declaredMethods = targetClass.getDeclaredMethods();
			
			if ((declaredMethods != null) && (declaredMethods.length > 0))
			{
				for (Method method : declaredMethods) 
				{
					String methodName = method.getName();
					
					if (methodName.startsWith("get") && (method.getGenericParameterTypes().length <= 0) && (method.getReturnType() != void.class))
					{
						methodName = normalizeKeys ? methodName.toLowerCase() : methodName;
						
						if (!mapProperties.containsKey(methodName))
						{
							mapProperties.put(methodName, method);
						}
					}
				}
			}
			
			if (targetClass.getSuperclass() != null)
			{
				mapProperties.putAll(getGetterPropertiesMap(targetClass.getSuperclass(), normalizeKeys));
			}
		}
		
		return mapProperties;
	}
	
	public static Map<String, Method> getPropertiesMap(Class<?> targetClass, boolean normalizeKeys)
	{
		Map<String, Method> mapProperties = new HashMap<String, Method>();
	
		if (targetClass != null)
		{
			Method[] declaredMethods = targetClass.getDeclaredMethods();
			
			if ((declaredMethods != null) && (declaredMethods.length > 0))
			{
				for (Method method : declaredMethods) 
				{
					String methodName = method.getName();
					
					if ((methodName.startsWith("get") && (method.getGenericParameterTypes().length <= 0) && (method.getReturnType() != void.class)) ||
						(methodName.startsWith("set") && (method.getGenericParameterTypes().length == 1) && (method.getReturnType() == void.class)))
					{
						methodName = normalizeKeys ? methodName.toLowerCase() : methodName;
						
						if (!mapProperties.containsKey(methodName))
						{
							mapProperties.put(methodName, method);
						}
					}
				}
			}
			
			if (targetClass.getSuperclass() != null)
			{
				mapProperties.putAll(getPropertiesMap(targetClass.getSuperclass(), normalizeKeys));
			}
		}
		
		return mapProperties;
	}
	
	//	**********************************************************************************************************************************************************
	//	ENDING SECTION		-->									METHODS
	//	**********************************************************************************************************************************************************


}
