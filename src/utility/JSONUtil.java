package utility;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONUtil {
	private static final Logger LOG = LoggerFactory.getLogger(JSONUtil.class);
	
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static Object readValue(String jsonStr, Class<?> c) {
		try {
			return OBJECT_MAPPER.readValue(jsonStr, c);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static String writeValue(Object obj) {
		return writeValue(obj, false);
	}
	
	public static String writeValue(Object obj, boolean isPretty) {
		try {
			if (isPretty) {
				return OBJECT_MAPPER.writer(new PrettyFormatter()).writeValueAsString(obj);
			} else {
				return OBJECT_MAPPER.writeValueAsString(obj);
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
}
