package udp;

import java.io.*;
import java.net.*;

import tcp.CRC32get;
import udp.FileEvent;

public class UdpServer {
	DatagramSocket dsock;
	DatagramPacket sPack, rPack;
	InetAddress client;
	int sport = 8000, cport;
	FileEvent fileEvent;
    CRC32get crc = new CRC32get();

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
			
			if(checkCRCValue(fileEvent, crc.getCRC32(fileEvent.getSrcDir(),fileEvent.getFileData())) == 0 ) {
				System.out.format("보낸 파일의 CRC32값은 %08X 입니다.\n",fileEvent.getCRC32Value());
				System.out.format("무결성 보장!\n");
			} else {
				System.out.format("무결성을 보장할 수 없습니다!\n");
			}
			
			System.out.println("Output file : " + outputFile + " is successfully saved ");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public long checkCRCValue(FileEvent event, long crcValue) {
		// 다르면 -1 return
		// 같으면 0 return
		if(event.getCRC32Value() == crcValue) {
			return 0; 
		}
		else {
			return -1;
		}
	}
	public static void main(String[] args) {
		UdpServer server = new UdpServer(8000);
		server.createAndListenSocket();
	}
}
