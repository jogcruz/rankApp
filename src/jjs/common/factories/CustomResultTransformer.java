package jjs.common.factories;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Map;

import jjs.common.utils.BeanMethodsUtils;

import org.hibernate.HibernateException;
import org.hibernate.transform.BasicTransformerAdapter;


@SuppressWarnings("serial")
public class CustomResultTransformer extends BasicTransformerAdapter
{

	private Class<?> resultClass;

	private Map<String, Method> mapResultClassProperties;
	
	//	Constructors
	//
	
	public CustomResultTransformer(Class<?> resultClass)
	{
		if (resultClass == null)
		{
			throw new IllegalArgumentException("resultClass cannot be null");
		}
		
		this.resultClass = resultClass;
	}

	
	// Interface ResultTransformer implementation
	//
	
	@Override
	public Object transformTuple(Object[] tuples, String[] aliases)
	{
		Object result = null;

		try 
		{
			if ((mapResultClassProperties == null) || (mapResultClassProperties.size() <= 0))
			{
				mapResultClassProperties = BeanMethodsUtils.getSetterPropertiesMap(resultClass, true);
			}
			
			result = resultClass.newInstance();

			for (int i = 0; i < aliases.length; i++)
			{
				String alias	= aliases[i].toLowerCase();
				Object tuple	= tuples[i];
				
				Method setterMethod = mapResultClassProperties.get("set" + alias);
				
				if (setterMethod != null)
				{
					 Class<?> parameterClass = setterMethod.getParameterTypes()[0];
					 
					 Object parameterValue = BeanExtenderFactory.convert(tuple, parameterClass);
					 
					 setterMethod.invoke(result, parameterValue);
				}
			}
		}
		catch (InstantiationException e)
		{
			throw new HibernateException("Could not instantiate resultClass: " + resultClass.getName());
		}
		catch (IllegalAccessException e)
		{
			throw new HibernateException("Could not instantiate resultClass: " + resultClass.getName());
		} 
		catch (Exception e) 
		{
			throw new HibernateException(e);
		}

		return result;
	}

	
	@SuppressWarnings("unused")
	private String convertStreamToString(InputStream is) throws IOException
	{
		/*
		 * To convert the InputStream to String we use the Reader.read(char[]
		 * buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the StringWriter class to
		 * produce the string.
		 */
		if (is != null) 
		{
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			
			try
			{
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1)
				{
					writer.write(buffer, 0, n);
				}
			}
			finally
			{
				is.close();
			}
			
			return writer.toString();
		}
		else 
		{
			return "";
		}
	}

}
