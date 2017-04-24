package udp_tcp;

import java.io.*;
import java.net.*;

public class UdpServer {
	DatagramSocket dsock;
	DatagramPacket sPack, rPack;
	InetAddress client;
	int sport = 8001, cport;
	FileEvent fileEvent;
    CRC32get crc = new CRC32get();
	static double avgTime;
	
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
		
		
		/*
		 * 1. length receive
		 * 2. send continue and save file
		 * 3. end
		 */
		
		try{
			while (true) {
				
				byte[] inputData = new byte[1024 * 64];
				
				// 1. length receive
				
				DatagramPacket rPack = new DatagramPacket(inputData, inputData.length);
				dsock.receive(rPack);
				client = rPack.getAddress();
				cport = rPack.getPort();

				// 2. send continue and save file
				String length = new String(rPack.getData());
				char len = length.charAt(0);
				int cnt = Character.getNumericValue(len);
				long fileCnt = cnt;
				while(cnt > 0) {
					String strOut = "c";
					byte[] strOutByte = strOut.getBytes();
					
					sPack = new DatagramPacket(strOutByte, strOutByte.length, client, cport);
					dsock.send(sPack);
					

					dsock.receive(rPack);	

					byte[] data = rPack.getData();
					
					ByteArrayInputStream in = new ByteArrayInputStream(data);
					ObjectInputStream is = new ObjectInputStream(in);
					fileEvent = (FileEvent) is.readObject();
					
					long totaltime = System.currentTimeMillis() - fileEvent.gettime();
					long s = fileEvent.getFileSize();
					avgTime += (double)s/(totaltime * 1000);
					
					if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
						System.out.println("Errors happened! while data packing");
						System.exit(0);
					}
					createAndWriteFile();
					cnt = cnt - 1;
					
					if(checkCRCValue(fileEvent, crc.getCRC32(fileEvent.getDestDir() + fileEvent.getFilename(), fileEvent.getFileData())) == 0 ) {
						System.out.format("무결성 보장!\n");
					} else {
						System.out.format("무결성을 보장할 수 없습니다!\n");
					}
				}
				System.out.println("file 전송속도 : "+ 1000 * avgTime / (double)fileCnt  + "bps");
				System.out.println("file 전송속도 : "+ avgTime / (double)fileCnt  + "Mb/s");
				
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
		UdpServer server = new UdpServer(8001);
		server.createAndListenSocket();
	}
}
