package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.http.HttpClientCodec;
import org.apache.mina.http.HttpRequestImpl;
import org.apache.mina.http.api.HttpEndOfContent;
import org.apache.mina.http.api.HttpMethod;
import org.apache.mina.http.api.HttpRequest;
import org.apache.mina.http.api.HttpResponse;
import org.apache.mina.http.api.HttpStatus;
import org.apache.mina.http.api.HttpVersion;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import objects.GameResult;

public class CallbackTaskHttpRes extends CallbackTaskAbstract {
	private static final Logger LOG = LoggerFactory.getLogger(CallbackTaskHttpRes.class);
	
	final private NioSocketConnector socketConnector;
	private InetSocketAddress address;
	private StringBuilder jsonStr = new StringBuilder();
	
	public CallbackTaskHttpRes () {
		this.socketConnector = new NioSocketConnector();
		
		socketConnector.setHandler(new IoHandlerAdapter() {
			@Override
			public void messageReceived(IoSession session, Object message) throws Exception {
				LOG.debug("session received!");
				if (message instanceof HttpRequest) { // 헤더 수신이 다 되었을때
					LOG.debug("Receive HttpRequest ==>");
					HttpRequest req = (HttpRequest) message;
					LOG.debug("HttpRequest = " + req);
					LOG.debug("path=" + req.getRequestPath());
				} else if (message instanceof IoBuffer) { // Body 수신
					LOG.debug("Receive IoBuffer ==>");
					IoBuffer buf = (IoBuffer) message;
					LOG.debug("BODY = " + new String(buf.array()));
				} else if (message instanceof HttpEndOfContent) { // 요청 패킷 완료
					LOG.debug("Receive HttpEndOfContent ==>");
				} else if (message instanceof HttpResponse) {
					HttpResponse req = (HttpResponse) message;
					
					if (HttpStatus.SUCCESS_OK.equals(req.getStatus())) {
						session.closeNow();
					} else {
						LOG.debug("Receive HttpRequest ==>");
						LOG.debug(req.getStatus().toString());
					}
				}
			}
		});
		socketConnector.setConnectTimeoutMillis(3000);
		DefaultIoFilterChainBuilder ioFilterChainBuilder = new DefaultIoFilterChainBuilder();
		Map<String, HttpClientCodec> map = new LinkedHashMap<String, HttpClientCodec>();
		HttpClientCodec codec = new HttpClientCodec();
		map.put("httpClientCodec", codec);
		ioFilterChainBuilder.setFilters((Map<String, ? extends IoFilter>)map);
		socketConnector.setFilterChainBuilder(ioFilterChainBuilder);
	}
	
	public void setAddress(String ip, int port) {
		this.address = new InetSocketAddress(ip, port);
	}

	@Override
	protected Object summaryResult(int completeTurn) {
		Map<Integer, GameResult> gameResults = new HashMap<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(ServerSettings.Instance().ResultsFile))) {
			
			String line;
			
			while ((line = br.readLine()) != null) {
				if (line.trim().length() == 0 || line.startsWith("#")) {
					continue;
				}
				
				String[] data = line.trim().split(" +");
				
				int turn = Integer.parseInt(data[0]);
				
				if (completeTurn != turn) {
					continue;
				}
				
				int gameID = Integer.parseInt(data[1]);
				
				if (gameResults.containsKey(gameID))
				{
					gameResults.get(gameID).setResult(line);
				}
				else
				{
					gameResults.put(gameID, new GameResult(line));
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		
		if (gameResults.isEmpty()) {
			return null;
		} else {
			jsonStr.setLength(0);
			jsonStr.append("{[");
			
			for (GameResult gr : gameResults.values()) {
				jsonStr.append(gr.toJSONString());
				jsonStr.append(",");
			}
			
			jsonStr.setLength(jsonStr.length() - 1);
			jsonStr.append("}]");
			return jsonStr.toString();
		}
	}
	
	@Override
	protected void sendResult(Object summary) {
		try {
			String retMsg = (String)summary;
			
			Map<String, String> headers = new HashMap<String, String>();
			byte[] body = retMsg.getBytes();
			
			headers.put("Content-Length", body.length + "");
			headers.put("content-type", "text/plain");
			String queryString = "";
			HttpRequestImpl msg = new HttpRequestImpl(HttpVersion.HTTP_1_1, HttpMethod.POST, ServerSettings.Instance().SendUrl, queryString, headers);
			
			ConnectFuture connect = socketConnector.connect(address).awaitUninterruptibly();
			IoSession session = connect.getSession();
			
			StringBuilder sb = new StringBuilder(msg.getMethod().toString());
			sb.append(" ");
			sb.append(msg.getRequestPath());
			if (!("".equals(msg.getQueryString()))) {
				sb.append("?");
				sb.append(msg.getQueryString());
			}
			sb.append(" ");
			sb.append(msg.getProtocolVersion());
			sb.append("\r\n");
			
			for (Map.Entry<String, String> header : msg.getHeaders().entrySet()) {
				sb.append(header.getKey());
				sb.append(": ");
				sb.append(header.getValue());
				sb.append("\r\n");
			}
			sb.append("\r\n").append(retMsg);
			
			LOG.debug(sb.toString());
			
			IoBuffer buf = IoBuffer.allocate(sb.length()).setAutoExpand(true);
			try {
				buf.putString(sb.toString(), Charset.forName("UTF-8").newEncoder());
			} catch (CharacterCodingException e) {
				LOG.error(e.getMessage(), e);
			}
			buf.flip();
			
			session.write(buf);
			
			session.getCloseFuture().awaitUninterruptibly(); // 세션이 닫힐 때까지 기다림
			LOG.debug("Sent to Portal!");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
