package jjs.common.factories;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import jjs.common.adtclasses.exceptions.OperationFailedException;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.log4j.Logger;


@SuppressWarnings({"rawtypes", "unchecked"})
public class DateConverterFactory2 {

	private Logger logger = Logger.getLogger(this.getClass());

	private static FastDateFormat sdf = FastDateFormat.getInstance(IDateTimeConstants.DATETIME_FORMAT);

	private static SimpleDateFormat sdfNOTZ = new SimpleDateFormat(IDateTimeConstants.DATETIME_FORMAT_NOTIMEZONE);

	private static BeanUtilsBean beanutilsbean = null;

	static {
		ConvertUtilsBean convert = new ConvertUtilsBean();
		// Estos constructores hacen que los Wrappers de primitivos no conviertan valores null en cero
		Converter longConverter = new LongConverter(null);
		Converter integerConverter = new IntegerConverter(null);
		Converter dateConverter = new DateConverter(null);
		convert.register(longConverter, Long.class);
		convert.register(integerConverter, Integer.class);
		convert.register(dateConverter, Date.class);
		// Instancia el conversor con la configuración de conversión deseada
		beanutilsbean = new BeanUtilsBean(convert);
	}

	/*
	 * Este método copia las propiedades tipo String del objeto fuente a propiedades tipo Date del objeto destino,
	 * cuando COINCIDAN los nombres de las propiedades en ambos objetos
	 */
	private static void copyStringToDateProperties(Object source, Object target, Map srcFields, Map trgFields) throws Exception {
		try {
			// Itera por el conjunto de campos del objeto origen,
			// copiando las propiedades date a string en el objeto de destino
			for (Iterator<String> iterator = srcFields.keySet().iterator(); iterator.hasNext();) {
				String propertyname = iterator.next();
				// Validar si existe esa propiedad en el objeto de destino
				if (trgFields.containsKey(propertyname)) {
					Class propertyclass_src = PropertyUtils.getPropertyType(source, propertyname);
					Class propertyclass_trg = PropertyUtils.getPropertyType(target, propertyname);
					if (propertyclass_src == String.class && propertyclass_trg == Date.class) {
						Object value = PropertyUtils.getProperty(source, propertyname);
						String datestr = (String) value;
						Date datevalue = StrToDate(datestr);
						PropertyUtils.setProperty(target, propertyname, datevalue);
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
			Map srcFields = beanutilsbean.describe(source);
			Map trgFields = beanutilsbean.describe(target);
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
			beanutilsbean.copyProperties(target, source);
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
			Map srcFields = beanutilsbean.describe(srcobj);
			Map trgFields = beanutilsbean.describe(trgobj);
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

	/*
	 * Este método copia las propiedades tipo Date del objeto fuente a propiedades tipo String del objeto destino,
	 * cuando COINCIDAN los nombres de las propiedades en ambos objetos
	 */
	private static void copyDateToStringProperties(Object source, Object target, Map srcFields, Map trgFields) throws Exception {
		try {
			// Itera por el conjunto de campos del objeto origen,
			// copiando las propiedades date a string en el objeto de destino
			for (Iterator<String> iterator = srcFields.keySet().iterator(); iterator.hasNext();) {
				String propertyname = iterator.next();
				// Validar si existe esa propiedad en el objeto de destino
				if (trgFields.containsKey(propertyname)) {
					Class propertyclass_src = PropertyUtils.getPropertyType(source, propertyname);
					Class propertyclass_trg = PropertyUtils.getPropertyType(target, propertyname);
					if (propertyclass_src == Date.class && propertyclass_trg == String.class) {
						Object value = PropertyUtils.getProperty(source, propertyname);
						Date datevalue = (Date) value;
						String strvalue = DateToStr(datevalue);
						PropertyUtils.setProperty(target, propertyname, strvalue);
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
					// logger.debug("Propiedad String: " + propertyname + ", valor: " + value);
					if (propertyname.endsWith(IDateTimeConstants.DATETOSTR_SUFFIX)) {
						String datepropertyname = propertyname.substring(0, propertyname.length() - 3);
						// Buscar propiedad Date correspondiente en el objeto target
						if (srcFields.containsKey(datepropertyname)) {
							// SRC STR -> TRG DATE
							String strvalue = (String) value;
							Date datevalue = StrToDate(strvalue);
							Class propertyclass = PropertyUtils.getPropertyType(source, datepropertyname);
							if (propertyclass == Date.class) {
								// logger.debug("Copiando propiedad: " + propertyname + ", a propiedad: " +
								// datepropertyname);
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
			Map srcFields = beanutilsbean.describe(srcobj);
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
					// logger.debug("Propiedad Date: " + propertyname + ", valor: " + value);
					String strpropertyname = propertyname + IDateTimeConstants.DATETOSTR_SUFFIX;
					// Buscar propiedad String correspondiente en el objeto target
					if (srcFields.containsKey(strpropertyname)) {
						// SRC DATE -> TRG STR
						Date datevalue = (Date) value;
						String strvalue = DateToStr(datevalue);
						Class propertyclass = PropertyUtils.getPropertyType(source, strpropertyname);
						if (propertyclass == String.class) {
							// logger.debug("Copiando propiedad: " + propertyname + ", a propiedad: " +
							// strpropertyname);
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
			Map srcFields = beanutilsbean.describe(srcobj);
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

	public static Date StrToDate(String datestr) throws ParseException {
		// DVI Ignorar la zona horaria
		datestr = datestr.substring(0, IDateTimeConstants.DATETIME_FORMAT.length());
		Date value = sdfNOTZ.parse(datestr);
		return value;
	}

	private DateConverterFactory2() {
	}

}
