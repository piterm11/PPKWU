import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
            if(t.getRequestMethod().equals("POST")){
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = null;
                try {
                    builder = factory.newDocumentBuilder();
                    Document requestXml = builder.parse(t.getRequestBody());
                    Document responseXml = process(requestXml);
                    
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(responseXml);
                    t.sendResponseHeaders(200, 0);
                    OutputStream os = t.getResponseBody();
                    transformer.transform(source, new StreamResult(os));
                    os.close();
                } catch (Exception e) {
                }
            }
        }

        private Document process(Document requestXml) throws ParserConfigurationException {
            Document response;
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                response = builder.newDocument();

                Element root = response.createElement("root");
                response.appendChild(root);
                NodeList children = requestXml.getChildNodes();
                for(int i = 0;i<children.getLength();i++){

                    System.out.println(children.item(i).getNodeName());
                    if(children.item(i).getNodeName().equals("str")){
                        String text = children.item(i).getTextContent();
                        Element lowercase = response.createElement("lowercase");
                        Element uppercase = response.createElement("uppercase");
                        Element digits = response.createElement("digits");
                        Element specials = response.createElement("special");
                        root.appendChild(lowercase);
                        root.appendChild(uppercase);
                        root.appendChild(digits);
                        root.appendChild(specials);
                        long up = text.chars().filter((c->Character.isUpperCase(c))).count();
                        long low = text.chars().filter((c->Character.isLowerCase(c))).count();
                        long digit = text.chars().filter((c->Character.isDigit(c))).count();
                        long special = text.length()-(up+low+digit);
                        lowercase.setTextContent(low+"");
                        uppercase.setTextContent(up+"");
                        digits.setTextContent(digit+"");
                        specials.setTextContent(special+"");
                    }
                }






            } catch (ParserConfigurationException e) {
                throw new ParserConfigurationException(e.getMessage());
            }


            return response;
        }


    }
}