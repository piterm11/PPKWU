import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Test {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(4080), 0);
		server.createContext("/", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
	}

	static class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			Map<String,String> params = queryToMap(t.getRequestURI().getQuery());
			String response = "";
			if(params.containsKey("num1")&&params.containsKey("num2")){
				int num1 = Integer.parseInt(params.get("num1"));
				int num2 = Integer.parseInt(params.get("num2"));
				response = buildJSON(num1,num2);
				
			}

			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}

		private String buildJSON(int num1, int num2) {
			return "{" +
					"\"sum\" : "+(num1+num2)+", " +
					"\"sub\" : "+(num1-num2)+", " +
					"\"mul\" : "+(num1*num2)+", " +
					"\"div\" : "+(num1/num2)+", " +
					"\"mod\" : "+(num1%num2)+"}";
		}

		public Map<String, String> queryToMap(String query) {
			if(query == null) {
				return null;
			}
			Map<String, String> result = new HashMap<>();
			for (String param : query.split("&")) {
				String[] entry = param.split("=");
				if (entry.length > 1) {
					result.put(entry[0], entry[1]);
				}else{
					result.put(entry[0], "");
				}
			}
			return result;
		}

	}
}