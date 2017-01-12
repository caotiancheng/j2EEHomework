import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class TCPClient {

    public static void main(String[] args) {

        try {
            Socket socket = new Socket("127.0.0.1",3333);
            //2、获取输出流,向服务器发送信息
            OutputStream os = socket.getOutputStream(); 
            PrintWriter pw  = new PrintWriter(os); 
            Scanner sc=new Scanner(System.in);
            String str=null;
            System.out.print("You can send a message to the server such as:");
            //str=sc.nextLine();
            str=getRandomString(5);
            System.out.println(str);
            pw.write(str);
            pw.flush();
            socket.shutdownOutput(); //关闭输出流
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String info = null;
            //循环读取
            while ((info = br.readLine()) != null){
                System.out.println("the server sends back a message:" + info);
            }
            br.close();
            is.close();
            isr.close();
            pw.close();
            os.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getRandomString(int length) { 
        String base = "abcdefghijklmnopqrstuvwxyz";   
        Random random = new Random();   
        StringBuffer sb = new StringBuffer();   
        for (int i = 0; i < length; i++) {   
            int number = random.nextInt(base.length());   
            sb.append(base.charAt(number));   
        }   
        return sb.toString();   
     }  
}