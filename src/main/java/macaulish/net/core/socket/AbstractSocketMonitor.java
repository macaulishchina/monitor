package macaulish.net.core.socket;

import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 基于TCP协议的Socket监听器实现
 * 包含数据接受和发送功能
 */
public abstract class AbstractSocketMonitor extends Thread implements _HardWareMonitor {

    private _MessageHandler mMessageHandler;
    private int mPort;
    private ExecutorService mExecutorService;// 创建一个线程池

    /**
     * 指定绑定的端口，并监听此端口
     *
     * @param port 本地网络端口
     */
    public AbstractSocketMonitor(int port) {
        mPort = port;
        mExecutorService = Executors.newCachedThreadPool();
    }

    /**
     * 将硬件端发送过来的一条消息转化成JSON格式
     * @param message 从数据采集端获得的一条数据
     * @return 包含数据采集端消息的JSON对象
     */
    abstract protected JSONObject translate(String message) throws RuntimeException;

    /**
     * 发送一条消息到硬件端
     * @param message JSON格式的消息
     * @return 转换后的字符串格式数据
     */
    abstract protected String translate(JSONObject message) throws RuntimeException;

    public void run(){
        try {
            ServerSocket mServerSocket = new ServerSocket(mPort);
            Socket socket;
            while (true) {
                //调用accept()方法开始监听，等待客户端的连接
                socket = mServerSocket.accept();
                //创建一个新的线程
                AcceptThread serverThread = new AcceptThread(socket);
                //启动线程
                mExecutorService.execute(serverThread);
                InetAddress address = socket.getInetAddress();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onAcceptMessage(JSONObject message, _MessageHandler messageHandler) {
         messageHandler.onAcceptMessage(message);
    }

    public void onDeliverMessage(JSONObject message) throws Exception {

        String IP = (String) message.get("IP");
        int port = message.getInt("port");
        //创建客户端Socket，指定服务器地址和端口
        Socket socket = new Socket(IP,port);
        //新建发送线程
        DeliverThread deliverThread = new DeliverThread(socket,translate(message));
        //启动线程
        mExecutorService.execute(deliverThread);

    }

    /**
     * 接受线程
     */
    private class AcceptThread extends Thread {

        // 和本线程相关的Socket
        private Socket mSocket;

        public AcceptThread(Socket socket) {
            this.mSocket = socket;
        }

        //线程执行的操作，响应客户端的请求
        public void run() {
            InputStream is = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            try {
                //获取输入流，并读取客户端信息
                is = mSocket.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                String info;
                while ((info = br.readLine()) != null) {//循环读取客户端的信息
                    JSONObject json = null;
                    try {
                        json = translate(info);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    json.put("IP", mSocket.getInetAddress().getHostAddress());
                    json.put("port",mSocket.getPort());
                    onAcceptMessage(json, mMessageHandler);
                }
                mSocket.shutdownInput();//关闭输入流
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //关闭资源
                try {
                    if (br != null)
                        br.close();
                    if (isr != null)
                        isr.close();
                    if (is != null)
                        is.close();
                    if (mSocket != null)
                        mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 发送线程
     */
    private class DeliverThread extends Thread {

        // 和本线程相关的Socket
        private Socket mSocket;
        private String mMessage;

        public DeliverThread(Socket socket,String message) {
            mSocket = socket;
            mMessage = message;
        }

        //线程执行的操作，发送数据到客户端
        public void run() {
            OutputStream os = null;//字节输出流
            PrintWriter pw = null; //为打印流
            try {
                os = mSocket.getOutputStream();
                pw=new PrintWriter(os);//将输出流包装为打印流
                pw.write(mMessage);
                pw.flush();
                mSocket.shutdownOutput();//关闭输出流
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                //关闭资源
                try {
                    if (pw != null)
                        pw.close();
                    if (os != null)
                        os.close();
                    if (mSocket != null)
                        mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void setMessageHandler(_MessageHandler messageHandler){
        mMessageHandler = messageHandler;
    }
}
