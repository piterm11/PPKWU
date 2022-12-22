import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
            String response = "";
            if(t.getRequestMethod().equals("POST")){
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = null;
                try {
                    builder = factory.newDocumentBuilder();
                    Document requestXml = builder.parse(t.getRequestBody());
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    StringWriter writer = new StringWriter();
                    transformer.transform(new DOMSource(requestXml), new StreamResult(writer));
                    String inputXmlString = writer.getBuffer().toString();
                    System.out.println(inputXmlString);
                } catch (Exception e) {
                }
            }
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(response);
            osw.flush();
            osw.close();
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