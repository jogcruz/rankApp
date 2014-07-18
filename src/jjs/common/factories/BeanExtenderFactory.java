package jjs.common.factories;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import jjs.common.adtclasses.exceptions.OperationFailedException;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;

public class BeanExtenderFactory 
{

	private BeanExtenderFactory() 
	{
		
	}

	private static BeanUtilsBean beanUtilsBean = null;

	static
	{
		ConvertUtilsBean convert = new ConvertUtilsBean();
		
		// Estos constructores hacen que los Wrappers de primitivos no conviertan valores null en cero
		//
		Converter longConverter 	= new LongConverter(null);
		Converter integerConverter	= new IntegerConverter(null);
		Converter dateConverter 	= new DateConverter(null);
		
		convert.register(longConverter, Long.class);
		convert.register(integerConverter, Integer.class);
		convert.register(dateConverter, Date.class);

		beanUtilsBean = new BeanUtilsBean(convert);
	}

	// Convert the value to an object of the specified class (if possible).
	//
	public static Object convert(Object value, Class<?> targetType)
	{
		Object result = beanUtilsBean.getConvertUtils().convert(value, targetType);
		
		return result;
	}
	
	public static void copyProperties(Object source, Object target) throws OperationFailedException
	{
		try
		{
			beanUtilsBean.copyProperties(target, source);
		}
		catch (IllegalAccessException e)
		{
			throw new OperationFailedException("No es posible acceder a las propiedades del objeto", e);
		}
		catch (InvocationTargetException e) 
		{
			throw new OperationFailedException("Error al invocar método o constructor", e);
		}
	}

	public static void copyProperties(Object[] source, Object[] target, Class<?> trgclass) throws OperationFailedException
	{
		if (source == null || target == null)
		{
			throw new OperationFailedException("No es posible copiar si Origen o Destino son nulos", "Operation can not be done");
		}
		if (source.length != target.length) 
		{
			throw new OperationFailedException("No es posible copiar si Origen y Destino tienen tamaños diferentes", "Operation can not be done");
		}
		try 
		{
			for (int i = 0; i < target.length; i++)
			{
				Object src = source[i];
				Object trg;
				trg = trgclass.newInstance();
				beanUtilsBean.copyProperties(trg, src);
				target[i] = trg;
			}
		}
		catch (InstantiationException e)
		{
			throw new OperationFailedException("No es posible instanciar un objeto de la clase" + trgclass.getName(), e);
		} 
		catch (IllegalAccessException e)
		{
			throw new OperationFailedException("No es posible acceder a las propiedades del objeto", e);
		}
		catch (InvocationTargetException e)
		{
			throw new OperationFailedException("Error al invocar método o constructor", e);
		}
	}

	public static void copyProperty(Object target, String propname, Object value) throws OperationFailedException
	{
		try
		{
			beanUtilsBean.copyProperty(target, propname, value);
		}
		catch (IllegalAccessException e)
		{
			throw new OperationFailedException("No es posible acceder a la propiedad del objeto", e);
		}
		catch (InvocationTargetException e)
		{
			throw new OperationFailedException("Error al invocar método", e);
		}
	}

}
