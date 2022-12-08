import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
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
            String jsonString = "{\"str\":\"teststring\"}";
            Gson gson = new Gson();
            Map<String, String> map = gson.fromJson(jsonString, new TypeToken<Map<String,String>>(){}.getType());
            String response = "";
            JsonObject json = new JsonObject();
            if(map.containsKey("str")){
                String text = map.get("str");
                long up = text.chars().filter((c->Character.isUpperCase(c))).count();
                long low = text.chars().filter((c->Character.isLowerCase(c))).count();
                long digit = text.chars().filter((c->Character.isDigit(c))).count();
                long special = text.length()-(up+low+digit);

                json.addProperty("lowercase", low);
                json.addProperty("uppercase", up);
                json.addProperty("digits", digit);
                json.addProperty("special", special);
            } if(map.containsKey("num1") && map.containsKey("num2")){
                int num1 = Integer.parseInt(params.get("num1"));
                int num2 = Integer.parseInt(params.get("num2"));
                json.addProperty("sum", num1+num2);
                json.addProperty("sub", num1-num2);
                json.addProperty("mul", num1*num2);
                json.addProperty("div", num1/num2);
                json.addProperty("mod", num1%num2);
            }
			System.out.println(json.getAsString());
			System.out.println(json.toString());
            response = json.getAsString();
            t.getResponseHeaders().set("Content-Type", "application/json");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
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
