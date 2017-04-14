package udp;

import java.io.*;
import java.net.*;

public class UdpServer {
	DatagramSocket dsock;
	DatagramPacket sPack, rPack;
	InetAddress client;
	int sport = 8000, cport;
	FileEvent fileEvent;
	
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
	
	public void createAndListenSocket(){
		
		try{
			byte[] inputData = new byte[1024 * 64];

			while (true) {
				
				DatagramPacket rPack = new DatagramPacket(inputData, inputData.length);
				dsock.receive(rPack);			
				byte[] data = rPack.getData();
				
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				
				fileEvent = (FileEvent) is.readObject();
				
				if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
					System.out.println("Errors happened! while data packing");
					System.exit(0);
				}
				
				createAndWriteFile();
				
				client = rPack.getAddress();
				cport = rPack.getPort();
							
				String strOut = "File Received";
				byte[] strOutByte = strOut.getBytes();
				
				sPack = new DatagramPacket(strOutByte, strOutByte.length, client, cport);
				dsock.send(sPack);
				Thread.sleep(3000);
				System.out.println("UDP 서버를 종료합니다.");
				System.exit(0);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void createAndWriteFile() {
		String outputFile = fileEvent.getDestDir() + fileEvent.getFilename();
		if (!new File(fileEvent.getDestDir()).exists()) {
			new File(fileEvent.getDestDir()).mkdirs();
		}
		
		File dstFile = new File(outputFile);
		FileOutputStream fileOutputStream = null;
		
		try {
			fileOutputStream = new FileOutputStream(dstFile);
			fileOutputStream.write(fileEvent.getFileData());
			fileOutputStream.flush();
			fileOutputStream.close();
			System.out.println("Output file : " + outputFile + " is successfully saved ");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		UdpServer server = new UdpServer(8000);
		server.createAndListenSocket();
	}
}
