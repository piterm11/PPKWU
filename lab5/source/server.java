import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

import com.google.gson.Gson;
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
            InputStreamReader isr =  new InputStreamReader(t.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String value = br.readLine();
            Gson gson = new Gson();
            Map<String, String> map = gson.fromJson(value, new TypeToken<Map<String,String>>(){}.getType());
            String response = "";
            JsonObject json = new JsonObject();
			System.out.println(1);
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
                int num1 = Integer.parseInt(map.get("num1"));
                int num2 = Integer.parseInt(map.get("num2"));
                json.addProperty("sum", num1+num2);
                json.addProperty("sub", num1-num2);
                json.addProperty("mul", num1*num2);
                json.addProperty("div", num1/num2);
                json.addProperty("mod", num1%num2);
            }
            response = json.toString();
            t.sendResponseHeaders(200, response.toString().length());
			OutputStream os = t.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();
        }

    }
}
