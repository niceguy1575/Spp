package tcp;

import java.io.*;
import java.net.*;

import udp.FileEvent;

public class TcpServer {
	int port = 8000;
	ServerSocket server;
	Socket socket;
	ObjectInputStream inputStream;
	FileEvent fileEvent;
	File dstFile;
	FileOutputStream fileOutputStream;
	BufferedReader in;
	PrintWriter out;

	public TcpServer (int port) {
		try {
			this.port = port;
			
			System.out.println(">> 서버를 시작합니다.");
			
			this.server = new ServerSocket(port);			
		} catch (IOException e) {
			System.out.println(e.toString());	
		}
	}
	
	public void waitForClient() {
		System.out.println(">> 클라이언트가 접속하길 기다리고 있습니다.");
		try {
			// 클라이언트 접속때까지 대기
			socket = server.accept(); 
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			inputStream = new ObjectInputStream(socket.getInputStream());

			printInfo();
			
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}

	public void receiveFile() {
		try {
			fileEvent = (FileEvent) inputStream.readObject();

			if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
				System.out.println("에러가 발생하였습니다. 종료합니다.");
				System.exit(0);
			}
			
			String outputFile = fileEvent.getDestDir() + fileEvent.getFilename();
			
			// 경로가 없으면 새로 만든다.
			if (!new File(fileEvent.getDestDir()).exists()) {
				new File(fileEvent.getDestDir()).mkdirs();
			}
			
			dstFile = new File(outputFile);
			
			fileOutputStream = new FileOutputStream(dstFile);
			fileOutputStream.write(fileEvent.getFileData());
			fileOutputStream.flush();
			fileOutputStream.close();
			
			System.out.println("Output file : " + outputFile + "is successfully saved");
			
			Thread.sleep(3000);
					
		} catch (IOException e) {
			System.out.println(e.toString());			
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void receive() {
		try {
			//서버 소켓으로부터 받은 메시지를 화면에 출력
			System.out.println("[서버] "+ in.readLine());		
		} catch (IOException e) {
			System.out.println(e.toString());			
		}
	}
	
	public void send(String msg) {
		// 클라이언트 소켓에 메시지 전송
		out.println(msg);
		out.flush();
		System.out.println("[서버] " + msg);		
	}
	
	public void close() {
		try {
			// 클라이언트 소켓 종료
			socket.close();		
		} catch(IOException e) {
			System.out.println(e.toString());
		}
	}

	public void printInfo() {
		System.out.println(">> 클라이언트가 접속에 성공했습니다.");
		//서비스 포트 번호와 클라이언트 주소와 포트번호 출력
		System.out.println("     서버 포트번호: " + socket.getLocalPort());
		System.out.println("     클라이언트 주소: " + socket.getInetAddress());
		System.out.println("     클라이언트 포트번호: " + socket.getPort() + '\n');
	}
	
	public static void main(String[] args) {
		TcpServer server = new TcpServer(8000);
		server.waitForClient();
		server.receiveFile();
		server.send("파일을 잘 받았습니다!");
		server.close();
	}
}