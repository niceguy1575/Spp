package tcp;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TcpClient {
	Socket socket;
	BufferedReader in;
	PrintWriter out;
	
	public TcpClient (String ip, int port) {
		try {
			socket = new Socket(ip, port);
			
			//서버 소켓에 스트림을 연결
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			printInfo();
		} catch (IOException e) {
			System.out.println(e.toString());			
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
		//서버 소켓에 메시지 전송
		out.println(msg);
		out.flush();
		System.out.println("[클라이언트] " + msg);		
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
		System.out.println(">> 서버 접속에 성공했습니다.");
		//서비스 포트 번호와 클라이언트 주소와 포트번호 출력
		System.out.println("     서버 주소: " + socket.getInetAddress());
		System.out.println("     서버 포트번호: " + socket.getPort());
		System.out.println("     클라이인트 포트번호: " + socket.getLocalPort() + '\n');
	}
	
	public static void main(String[] args) {
		//서버 주소와 포트번호를 지정하여 서버에 접속
		TcpClient client = new TcpClient("127.0.0.1", 7777); 
		
		System.out.print("서버에게 보낼 메시지 입력 >> ");
		Scanner s = new Scanner(System.in);
		String msg = s.nextLine();
		client.send(msg);
		client.receive();
		client.close();
		s.close();
	}
}