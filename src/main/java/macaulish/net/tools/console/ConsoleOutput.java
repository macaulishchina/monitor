package macaulish.net.tools.console;

import macaulish.net.test.DefaultSocketMonitor;

import java.util.Scanner;

public class ConsoleOutput {

    public static void main(String args[]){
        Scanner scanner = new Scanner(System.in);
        int port;
        System.out.println("端口监听程序by-MacAulish\n\n");
        System.out.println("请输入监听的端口:");
        while (true){
            try {
                port = Integer.parseInt(scanner.nextLine());
                break;
            }catch (Exception e){
                System.out.println("请输入1-65535之间的整数！");
            }
        }
        System.out.println("******正在监听本机端口:"+port+"******");
        DefaultSocketMonitor monitor = new DefaultSocketMonitor(port);
        ConsoleMessageHandler handler = new ConsoleMessageHandler(monitor);
        handler.work();
    }
}
