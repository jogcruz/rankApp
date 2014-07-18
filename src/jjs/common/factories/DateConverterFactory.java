package jjs.common.factories;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import jjs.common.adtclasses.exceptions.OperationFailedException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.time.FastDateFormat;


@SuppressWarnings("rawtypes")
public class DateConverterFactory {

	private static FastDateFormat sdf = FastDateFormat.getInstance(IDateTimeConstants.DATETIME_FORMAT);

	private static SimpleDateFormat sdfNOTZ = new SimpleDateFormat(IDateTimeConstants.DATETIME_FORMAT_NOTIMEZONE);

	static {
		Converter dateConverter = new DateConverter(null);
		ConvertUtils.register(dateConverter, Date.class);
	}

	private static void copyStringToDateProperties(Object source, Object target, Map srcFields, Map trgFields) throws Exception {
		try {
			// Itera por el conjunto de campos del objeto origen,
			// copiando las propiedades date a string y string a date en el objeto de destino
			for (Iterator iterator = srcFields.keySet().iterator(); iterator.hasNext();) {
				String propertyname = (String) iterator.next();
				Object value = PropertyUtils.getProperty(source, propertyname);
				if (value instanceof String) {
					// System.out.println("Propiedad String: " + propertyname + ", valor: " + value);
					if (propertyname.endsWith(IDateTimeConstants.DATETOSTR_SUFFIX)) {
						// Buscar propiedad Date correspondiente en el objeto target
						String datepropertyname = propertyname.substring(0, propertyname.length() - 3);
						if (trgFields.containsKey(datepropertyname)) {
							// SRC STR -> TRG DATE
							String strvalue = (String) value;
							Date datevalue = StrToDate(strvalue);
							Class propertyclass = PropertyUtils.getPropertyType(target, datepropertyname);
							if (propertyclass == Date.class) {
								// System.out.println("Copiando propiedad: " + propertyname + ", a propiedad: " + datepropertyname);
								PropertyUtils.setProperty(target, datepropertyname, datevalue);
							}
						}
					}
				}
			}
		} catch (IllegalAccessException e) {
			throw new Exception("No es posible acceder a las propiedades del objeto", e);
		} catch (InvocationTargetException e) {
			throw new Exception("Error al invocar método o constructor", e);
		}
	}

	public static void copyProperties(Object source, Object target) throws OperationFailedException {
		copyProperties(source, target, DateConverterMode.DATE_TO_STR);
	}

	public static void copyProperties(Object source, Object target, DateConverterMode mode) throws OperationFailedException {
		try {
			Map srcFields = BeanUtils.describe(source);
			Map trgFields = BeanUtils.describe(target);
			copyProperties(source, target, srcFields, trgFields, mode);
		} catch (IllegalAccessException e) {
			throw new OperationFailedException("No es posible acceder a las propiedades del objeto", e);
		} catch (InvocationTargetException e) {
			throw new OperationFailedException("Error al invocar método o constructor", e);
		} catch (Exception e) {
			throw new OperationFailedException(e.getMessage());
		}
	}

	private static void copyProperties(Object source, Object target, Map srcFields, Map trgFields) throws Exception {
		copyProperties(source, target, srcFields, trgFields, DateConverterMode.DATE_TO_STR);
	}

	private static void copyProperties(Object source, Object target, Map srcFields, Map trgFields, DateConverterMode mode) throws Exception {
		try {
			BeanUtils.copyProperties(target, source);
			if (mode == DateConverterMode.DATE_TO_STR)
				copyDateToStringProperties(source, target, srcFields, trgFields);
			else
				copyStringToDateProperties(source, target, srcFields, trgFields);
		} catch (IllegalAccessException e) {
			throw new Exception("No es posible acceder a las propiedades del objeto", e);
		} catch (InvocationTargetException e) {
			throw new Exception("Error al invocar método o constructor", e);
		}
	}

	public static void copyProperties(Object[] source, Object[] target, Class srcclass, Class trgclass) throws OperationFailedException {
		copyProperties(source, target, srcclass, trgclass, DateConverterMode.DATE_TO_STR);
	}

	public static void copyProperties(Object[] source, Object[] target, Class srcclass, Class trgclass, DateConverterMode mode) throws OperationFailedException {
		if (source == null || target == null) {
			throw new OperationFailedException("No es posible copiar si Origen o Destino son nulos");
		}
		if (source.length != target.length) {
			throw new OperationFailedException("No es posible copiar si Origen y Destino tienen tamaños diferentes");
		}
		try {
			Object srcobj = srcclass.newInstance();
			Object trgobj = trgclass.newInstance();
			Map srcFields = BeanUtils.describe(srcobj);
			Map trgFields = BeanUtils.describe(trgobj);
			// Iterar por cada elemento del arreglo de origen
			for (int i = 0; i < source.length; i++) {
				Object src = source[i];
				Object trg = trgclass.newInstance();
				copyProperties(src, trg, srcFields, trgFields, mode);
				target[i] = trg;
			}
		} catch (InstantiationException e) {
			throw new OperationFailedException("No es posible instanciar un objeto de la clase" + trgclass.getName(), e);
		} catch (IllegalAccessException e) {
			throw new OperationFailedException("No es posible acceder a las propiedades del objeto", e);
		} catch (InvocationTargetException e) {
			throw new OperationFailedException("Error al invocar método o constructor", e);
		} catch (NoSuchMethodException e) {
			throw new OperationFailedException("Error al invocar método o constructor", e);
		} catch (Exception e) {
			throw new OperationFailedException(e.getMessage());
		}
	}

	private static void copyDateToStringProperties(Object source, Object target, Map srcFields, Map trgFields) throws Exception {
		try {
			// Itera por el conjunto de campos del objeto origen,
			// copiando las propiedades date a string en el objeto de destino
			for (Iterator iterator = srcFields.keySet().iterator(); iterator.hasNext();) {
				String propertyname = (String) iterator.next();
				Object value = PropertyUtils.getProperty(source, propertyname);
				if (value instanceof Date) {
					// System.out.println("Propiedad Date: " + propertyname + ", valor: " + value);
					// Buscar propiedad String correspondiente en el objeto target
					String strpropertyname = propertyname + IDateTimeConstants.DATETOSTR_SUFFIX;
					if (trgFields.containsKey(strpropertyname)) {
						// SRC DATE -> TRG STR
						Date datevalue = (Date) value;
						String strvalue = DateToStr(datevalue);
						Class propertyclass = PropertyUtils.getPropertyType(target, strpropertyname);
						if (propertyclass == String.class) {
							// System.out.println("Copiando propiedad: " + propertyname + ", a propiedad: " + strpropertyname);
							PropertyUtils.setProperty(target, strpropertyname, strvalue);
						}
					}
				}
			}
		} catch (IllegalAccessException e) {
			throw new Exception("No es posible acceder a las propiedades del objeto", e);
		} catch (InvocationTargetException e) {
			throw new Exception("Error al invocar método o constructor", e);
		}
	}

	public static String DateToStr(Date date) {
		String value = sdf.format(date);
		return value;
	}

	private static void fillDateFromStringProperties(Object source, Map srcFields) throws Exception {
		try {
			// Itera por el conjunto de campos del objeto origen,
			// copiando las propiedades date a string y string a date en el objeto de destino
			for (Iterator iterator = srcFields.keySet().iterator(); iterator.hasNext();) {
				String propertyname = (String) iterator.next();
				Object value = PropertyUtils.getProperty(source, propertyname);
				if (value instanceof String) {
					// System.out.println("Propiedad String: " + propertyname + ", valor: " + value);
					if (propertyname.endsWith(IDateTimeConstants.DATETOSTR_SUFFIX)) {
						String datepropertyname = propertyname.substring(0, propertyname.length() - 3);
						// Buscar propiedad Date correspondiente en el objeto target
						if (srcFields.containsKey(datepropertyname)) {
							// SRC STR -> TRG DATE
							String strvalue = (String) value;
							Date datevalue = StrToDate(strvalue);
							Class propertyclass = PropertyUtils.getPropertyType(source, datepropertyname);
							if (propertyclass == Date.class) {
								// System.out.println("Copiando propiedad: " + propertyname + ", a propiedad: " + datepropertyname);
								PropertyUtils.setProperty(source, datepropertyname, datevalue);
							}
						}
					}
				}
			}
		} catch (IllegalAccessException e) {
			throw new Exception("No es posible acceder a las propiedades del objeto", e);
		} catch (InvocationTargetException e) {
			throw new Exception("Error al invocar método o constructor", e);
		}
	}

	public static void fillDateProperties(Object[] source, Class srcclass) throws OperationFailedException {
		if (source == null || source.length == 0) {
			throw new OperationFailedException("No es posible continuar si Origen es nulo o vacío");
		}
		try {
			Object srcobj = srcclass.newInstance();
			Map srcFields = BeanUtils.describe(srcobj);
			// Iterar por cada elemento del arreglo de origen
			for (int i = 0; i < source.length; i++) {
				Object src = source[i];
				fillDateFromStringProperties(src, srcFields);
			}
		} catch (IllegalAccessException e) {
			throw new OperationFailedException("No es posible acceder a las propiedades del objeto", e);
		} catch (InvocationTargetException e) {
			throw new OperationFailedException("Error al invocar método o constructor", e);
		} catch (NoSuchMethodException e) {
			throw new OperationFailedException("Error al invocar método o constructor", e);
		} catch (Exception e) {
			throw new OperationFailedException(e.getMessage());
		}
	}

	private static void fillStringFromDateProperties(Object source, Map srcFields) throws Exception {
		try {
			// Itera por el conjunto de campos del objeto origen,
			// copiando las propiedades date a string y string a date en el objeto de destino
			for (Iterator iterator = srcFields.keySet().iterator(); iterator.hasNext();) {
				String propertyname = (String) iterator.next();
				Object value = PropertyUtils.getProperty(source, propertyname);
				if (value instanceof Date) {
					// System.out.println("Propiedad Date: " + propertyname + ", valor: " + value);
					String strpropertyname = propertyname + IDateTimeConstants.DATETOSTR_SUFFIX;
					// Buscar propiedad String correspondiente en el objeto target
					if (srcFields.containsKey(strpropertyname)) {
						// SRC DATE -> TRG STR
						Date datevalue = (Date) value;
						String strvalue = DateToStr(datevalue);
						Class propertyclass = PropertyUtils.getPropertyType(source, strpropertyname);
						if (propertyclass == String.class) {
							// System.out.println("Copiando propiedad: " + propertyname + ", a propiedad: " + strpropertyname);
							PropertyUtils.setProperty(source, strpropertyname, strvalue);
						}
					}
				}
			}
		} catch (IllegalAccessException e) {
			throw new Exception("No es posible acceder a las propiedades del objeto", e);
		} catch (InvocationTargetException e) {
			throw new Exception("Error al invocar método o constructor", e);
		}
	}

	public static void fillStringProperties(Object[] source, Class srcclass) throws OperationFailedException {
		if (source == null) {
			throw new OperationFailedException("No es posible continuar si Origen es nulo");
		}
		try {
			Object srcobj = srcclass.newInstance();
			Map srcFields = BeanUtils.describe(srcobj);
			// Iterar por cada elemento del arreglo de origen
			for (int i = 0; i < source.length; i++) {
				Object src = source[i];
				fillStringFromDateProperties(src, srcFields);
			}
		} catch (IllegalAccessException e) {
			throw new OperationFailedException("No es posible acceder a las propiedades del objeto", e);
		} catch (InvocationTargetException e) {
			throw new OperationFailedException("Error al invocar método o constructor", e);
		} catch (NoSuchMethodException e) {
			throw new OperationFailedException("Error al invocar método o constructor", e);
		} catch (Exception e) {
			throw new OperationFailedException(e.getMessage());
		}
	}

	public static Date StrToDate(String datestr) throws OperationFailedException {
		// DVI Ignorar la zona horaria
		datestr = datestr.substring(0, IDateTimeConstants.DATETIME_FORMAT_NOTIMEZONE.length());
		Date value;
		try {
			value = sdfNOTZ.parse(datestr);
		} catch (ParseException e) {
			throw new OperationFailedException("Error parseando fecha", e);
		}
		return value;
	}

	private DateConverterFactory() {
	}

}
