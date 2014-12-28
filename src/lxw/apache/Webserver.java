package lxw.apache;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Webserver {

	public static void webserverStart(int port){
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			while(true){
				Socket socket = serverSocket.accept();
				//每个线程创建各种的Processor，不是共享资源
				new Thread(new Processor(socket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(serverSocket != null){
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		int port = 80;
		if(args != null && args.length > 0){
			try {
				port = Integer.parseInt(args[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		webserverStart(port);
	}
}
