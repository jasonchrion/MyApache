package lxw.apache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Processor implements Runnable {
	private Socket socket;
	private InputStream in;
	private PrintStream out;
	
	private final static String WEB_ROOT = "C:\\share";
	
	public Processor(Socket socket){
		this.socket = socket;
		try {
			in = socket.getInputStream();
			out = new PrintStream(socket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		String filename = parse(in);
		if (filename != null && !filename.isEmpty()) {
			sendFile(filename);
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String parse(InputStream in) {
		BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
		String filename = null; 
		try {
			String httpMessage = bReader.readLine();
			System.out.println(httpMessage);
			filename = httpMessage.split(" ")[1];
		} catch (Exception e) {
			e.printStackTrace();
			sendErrorMessage(400, "Client bad request...");
			return null;
		}
		return filename;
	}

	private void sendErrorMessage(int errorCode, String errorMessage) {
		out.println("HTTP/1.0 " + errorCode + " " + errorMessage);
		out.println("content-type: text/html");
		out.println();
		out.println("<html>");
		out.println("<title> Error Message");
		out.println("</title>");
		out.println("<body>");
		out.println("<h1>Error code:" + errorCode + " Error Message:" + errorMessage + "</h1>");
		out.println("</body>");
		out.println("</html>");
		out.flush();
		out.close();
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendFile(String filename){
		File file = new File(WEB_ROOT + filename);
		if (!file.exists()) {
			sendErrorMessage(404, "File not found...");
			return;
		}
		try {
			InputStream input  = new FileInputStream(file);
			byte[] content = new byte[(int)file.length()];
			input.read(content);
			out.println("HTTP/1.0 200 success");
			out.println("content-length:" + content.length);
			out.println();
			out.write(content);
			out.flush();
			out.close();
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}