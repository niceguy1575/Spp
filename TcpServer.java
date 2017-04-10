package tcp;

import java.io.*;
import java.net.*;

public class TcpServer {
	int port = 7777;
	ServerSocket server;
	Socket socket;
	BufferedReader in;
	PrintWriter out;
	
	public TcpServer (int port) {
		this.port = port;
		System.out.println(">> 서버를 시작합니다.");
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println(e.toString());			
		}
	}
	
	public void waitForClient() {
		System.out.println(">> 클라이언트가 접속하길 기다리고 있습니다.");
		try {
			// 클라이언트 접속때까지 대기
			socket = server.accept(); 
			printInfo();
			//클라이언트 소켓에 스트림을 연결
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			System.out.println(e.toString());			
		}
	}
	
	public void receive() {
		try {
			//클라이언트 소켓으로부터 받은 메시지를 화면에 출력
			System.out.println("[클라이언트] "+ in.readLine());		
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
		int port = 7777;			
		TcpServer myServer = new TcpServer(port);
		myServer.waitForClient();
		myServer.receive();
		myServer.send("서버에 접속하신 것을 환영합니다!");
		myServer.close();
	}
}