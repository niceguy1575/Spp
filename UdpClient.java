package udp;

import java.io.*;
import java.net.*;

public class UdpClient {
	DatagramSocket dsock;
	DatagramPacket sPack, rPack;
	InetAddress server;
	int port = 8888;
	
	public UdpClient(String ip, int port) {
		try{
			server = InetAddress.getByName(ip);
			this.port = port;
			this.dsock = new DatagramSocket();
			
			System.out.println("connecting to server...");
			System.out.println("msg to server : ");
			System.out.println("write 'quit' to quit : ");
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void communicate(){

		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String strOut = null;
			
			while( (strOut = br.readLine()) != null ) {
				
				sPack = new DatagramPacket(strOut.getBytes(), strOut.getBytes().length, server, port);
				dsock.send(sPack);
				
				if(strOut.trim().equals("quit")) break;
				
				byte[] buffer = new byte[1024];
				
				rPack = new DatagramPacket(buffer, buffer.length);
				dsock.receive(rPack);
				
				String strIn = new String(rPack.getData(), 0, rPack.getData().length);
				
				System.out.println("[server" + server + " : " + port + "]" + strIn);
			}
			System.out.println("UDP client를 종료합니다.");
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public static void main(String[] args) {
		UdpClient client = new UdpClient("127.0.0.1", 7777);
		client.communicate();
	}
	
}
