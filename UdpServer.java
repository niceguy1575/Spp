package udp;

import java.io.*;
import java.net.*;

public class UdpServer {
	DatagramSocket dsock;
	DatagramPacket sPack, rPack;
	InetAddress client;
	int sport = 7777, cport;
	
	public UdpServer(int sport) {
		try{
			this.sport = sport;
			
			System.out.println("server start...");
			System.out.println("Waiting for client..." + "\n");
			
			this.dsock = new DatagramSocket(sport);
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void communicate(){
		try{

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
			while(true) {
				byte[] buffer = new byte[1024];
				
				rPack = new DatagramPacket(buffer, buffer.length);
				dsock.receive(rPack);
				
				String strIn = new String(rPack.getData(), 0, rPack.getData().length);
				
				client = rPack.getAddress();
				cport = rPack.getPort();
				
				System.out.println("client" + client + " : " + cport + "]" + strIn);
				
				if(strIn.trim().equals("quit")) break;
				
				String strOut = br.readLine();
				sPack = new DatagramPacket(strOut.getBytes(), strOut.getBytes().length, client, cport);
				dsock.send(sPack);
			}
			System.out.println("UDP 서버를 종료합니다.");
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public static void main(String[] args) {
		UdpServer client = new UdpServer(7777);
		client.communicate();
	}
	
}
