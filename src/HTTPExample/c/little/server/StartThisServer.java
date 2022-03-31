package HTTPExample.c.little.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import sun.net.httpserver.DefaultHttpServerProvider;

import java.io.IOException;
import java.net.InetSocketAddress;

public class StartThisServer {

    public static void main(String[] args) throws IOException {
        final int backlog = 64;  //очередь сокетов на "сэт" какой то
        final InetSocketAddress serverAddress = new InetSocketAddress(80);

        HttpServerProvider provider = DefaultHttpServerProvider.provider();
        HttpServer server = provider.createHttpServer(serverAddress,backlog);

        HttpContext baseContext = server.createContext("/");   //полезный класс
        baseContext.setHandler(new PageHttpHandler("" +
                "<html>" +
                "   <body>" +
                "      <p><a href =\"/a.do\">a.do</a></p>"+
                "      <p><a href =\"/b.do\">b.do</a></p>"+
                "   <body>" +
                "<html>"

        ));

        HttpContext aContext = server.createContext("/a.do");
        aContext.setHandler(new PageHttpHandler("" +
                "<html>" +
                "   <body>" +
                "      <p><a href =\"/b.do\">b.do</a></p>"+
                "   <body>" +
                "<html>"

        ));

        HttpContext bContext = server.createContext("/b.do");
        bContext.setHandler(new PageHttpHandler("" +
                "<html>" +
                "   <body>" +
                "      <p><a href =\"/a.do\">a.do</a></p>"+
                "   <body>" +
                "<html>"

        ));

        server.start();
    }
}
