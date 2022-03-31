package HTTPExample.a.little.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

/**
 * Это маленький сервер
 * Чтобы проверить его работу: запускаем его и в браузере набираем localhost c портом который здесь задали
 * Разобраться почему после того как отправили ответ на страницу, то к нам еще раз приходит ответ со всей информацией
 */

public class StartServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(80);
        while (true){
            System.out.println("wait for TCP-connectoin...");
            Socket socket = serverSocket.accept();
            System.out.println("get one!");
            try(InputStream in = socket.getInputStream(); //вытаскиваем то, что пришло
                OutputStream out = socket.getOutputStream())  //отправим обратно
            {
                byte[] request = readRequestFully(in);
                System.out.println("----------");
                System.out.println(new String(request, StandardCharsets.US_ASCII));
                System.out.println("----------");
                byte[] response = new Date().toString().getBytes(StandardCharsets.US_ASCII);
                out.write(response);

            }
            finally {
                socket.close();
            }
        }

    }

    //метод чтения
    public static byte[] readRequestFully(InputStream in) throws IOException {
        byte[] buff = new byte[8192];
        int headerLen=0;
        while (true){
            int count = in.read(buff, headerLen, buff.length-headerLen);
            if(count<0){
                throw new RuntimeException("Incoming connection close");
            } else {
                headerLen +=count;
                if(isRequestEnd(buff,headerLen)){
                    return Arrays.copyOfRange(buff,0,headerLen);
                }
                if(headerLen== buff.length){
                    throw new RuntimeException("Too big HTTP header");
                }
            }
        }
    }

    public static boolean isRequestEnd(byte[] request,int length){
        if(length<4) {
            return false;
        } else {
            return request[length-4]== '\r' &&
                    request[length-3]== '\n' &&
                    request[length-2]== '\r' &&
                    request[length-1]== '\n';
        }

    }
}
