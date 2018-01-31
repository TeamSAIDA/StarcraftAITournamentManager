package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import utility.GameParser;
import utility.JSONUtil;

public class HttpServerHandler implements HttpHandler {
	static final private Logger LOG = LoggerFactory.getLogger(HttpServerHandler.class);
	
	@Override
	public void handle(HttpExchange paramHttpExchange) throws IOException {
		String requestMethod = paramHttpExchange.getRequestMethod();
		URI uri = paramHttpExchange.getRequestURI();
		LOG.debug("[" + requestMethod + "] " + uri.getPath());

		LOG.debug(paramHttpExchange.getRequestHeaders().getFirst("Content-Type"));
		int length = Integer.parseInt(paramHttpExchange.getRequestHeaders().getFirst("content-length"));
		
		byte[] msg = new byte[length];
		InputStream requestBodyIs = paramHttpExchange.getRequestBody();
		
		String jsonStr = "";
		int size;
		while ((size = requestBodyIs.read(msg)) != -1) {
			jsonStr += new String(msg, 0, size, "UTF-8");
		}
		
		LOG.debug(jsonStr);
		
		HashMap<String, Object> ba = (HashMap<String, Object>) JSONUtil.readValue(jsonStr, HashMap.class);
		
		GameParser.addBot(ba);
		
		String writeValue = JSONUtil.writeValue("success");
		
		Headers responseHeaders = paramHttpExchange.getResponseHeaders();

		// HTTP : text/html, SOAP : text/xml
		responseHeaders.set("Content-Type", "application/json");

		paramHttpExchange.sendResponseHeaders(200, 0);

		OutputStream responseBody = paramHttpExchange.getResponseBody();
		
		LOG.debug("[return] : " + writeValue);
		responseBody.write(writeValue.getBytes());

		responseBody.flush();

		responseBody.close();
	}
}
