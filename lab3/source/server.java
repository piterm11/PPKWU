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
			if(params!=null) {
				String text = params.get("str");
				int up = 0;
				int low = 0;
				int special = 0;
				int digit = 0;
				for(int i=0;i<text.length();i++){
					if(Character.isUpperCase(text.charAt(i)))
						up++;
					else if(Character.isLowerCase(text.charAt(i)))
						low++;
					else if(Character.isDigit(text.charAt(i)))
						digit++;
					else
						special++;
				}
				response = buildJSON(low,up,digit,special);
			}

			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
			System.out.println("Served hello world...");
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


		public String buildJSON(int low, int up, int digit, int special){
			return " { \"lowercase\" : "+low+", \"uppercase\" : "+up+", \"digits\" : "+digit+", \"special\" : "+special+"}";
		}
	}
}