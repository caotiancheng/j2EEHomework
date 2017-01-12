import java.io.*;
import java.net.Socket;


public class ServerThread extends Thread {

    Socket socket = null;
    public ServerThread(Socket socket){
        this.socket = socket;
    }
    public static String reverse(String str){  
        return new StringBuilder(str).reverse().toString();  
    }  
  
    public void run(){

        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        OutputStream os = null;
        PrintWriter pw = null;
        try {
            is = socket.getInputStream();
            isr = new InputStreamReader(is); 
            br = new BufferedReader(isr);
            String info = null;
            while ((info = br.readLine()) != null){
                System.out.println("The client sends a message: " +info);
                socket.shutdownInput(); 
                os = socket.getOutputStream();
                pw = new PrintWriter(os); 
                pw.write(reverse(info));
                pw.flush();  
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
                try {
                    if (pw != null)
                        pw.close();
                    if (os != null)
                        os.close();
                    if (is != null)
                        is.close();
                    if (isr != null)
                        isr.close();
                    if (br != null)
                        br.close();
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }

        }
    }
}
