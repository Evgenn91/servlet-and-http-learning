package HTTPExample.b.little.server;

import com.sun.net.httpserver.*;
import com.sun.net.httpserver.spi.HttpServerProvider;
import sun.net.httpserver.DefaultHttpServerProvider;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Тут мы можем посмотреть сам запрос как выглядит
 * Из чего он состоит и т.д.
 * Результат можно увидеть в брайзере(не в консоле)
 */


public class StartServerPro {
    public static void main(String[] args) throws IOException {
        final int backlog = 64;  //очередь сокетов на "сэт" какой то
        final InetSocketAddress serverAddress = new InetSocketAddress(80);

        HttpServerProvider provider = DefaultHttpServerProvider.provider();
        HttpServer server = provider.createHttpServer(serverAddress,backlog);
        server.setExecutor(Executors.newCachedThreadPool());  //это пул потоков

        HttpContext baseContext = server.createContext("/");   //полезный класс
        baseContext.setHandler(new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String protocol = exchange.getProtocol();
                String requestMethod = exchange.getRequestMethod();
                URI requestURI = exchange.getRequestURI();
                Headers requestHeaders = exchange.getRequestHeaders();

                String htmlPage = createResponsePage(protocol,requestMethod,requestURI,requestHeaders);

                Headers responseHeaders = exchange.getResponseHeaders();
                responseHeaders.add("X-MyHeader","1");
                responseHeaders.add("X-MyHeader","2");
                responseHeaders.add("X-MyHeader","3");
                exchange.sendResponseHeaders(200,htmlPage.length());

                OutputStream os = exchange.getResponseBody();
                os.write(htmlPage.getBytes(StandardCharsets.US_ASCII));
                os.close();

            }
        });

        server.start();

    }

    public static String createResponsePage(String protocol,String requestMethod, URI requestURI, Headers requestHeaders ){
        String htmlPage = "<html><body>";
        htmlPage +="<br>requestMethod: " + requestMethod + "</br>";
        htmlPage +="<br>requestURI: " + requestURI + "</br>";
        htmlPage +="<br>requestHeaders: " + requestHeaders+ "</br>";
        for(Map.Entry<String , List<String>> header : requestHeaders.entrySet()){
            String key = header.getKey();
            List<String> values = header.getValue();
            htmlPage += "<br>" +key + ": ";
            for(String value: values){
                htmlPage += value + ", ";
            }
            htmlPage += "</br>";
        }
        return htmlPage;

    }
}
