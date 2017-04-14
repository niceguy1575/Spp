package udp;

import java.io.*;
import java.net.*;

import tcp.CRC32get;

public class UdpClient {
	DatagramSocket dsock;
	DatagramPacket sPack, rPack;
	InetAddress server;
	int port = 8000;
	String srcPath, destPath;
	FileEvent event;
	CRC32get crc = new CRC32get();

	public UdpClient(String ip, int port, String srcPath, String destPath) {
		try{
			server = InetAddress.getByName(ip);
			this.port = port;
			this.dsock = new DatagramSocket();
			this.srcPath = srcPath;
			this.destPath = destPath;
			
			System.out.println("connecting to server...");
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void createConnection(){
		
		try{
			byte[] inputData = new byte[1024 * 64];
			event = getFileEvent();
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(outputStream);
			
			os.writeObject(event);
			
			byte[] data = outputStream.toByteArray();
			
			sPack = new DatagramPacket(data, data.length, server, port);
			
			dsock.send(sPack);
			System.out.println("File transfer from client");
			
			rPack = new DatagramPacket(inputData, inputData.length);
			dsock.receive(rPack);
			
			String response = new String(rPack.getData());
			System.out.println("Response from server:" + response);
			
			Thread.sleep(2000);
			System.out.println("UDP client를 종료합니다.");
			System.exit(0);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public FileEvent getFileEvent() {
		
		FileEvent fileEvent = new FileEvent();
		
		String fileName = srcPath.substring(srcPath.lastIndexOf("/") + 1, srcPath.length());
		//String path = srcPath.substring(0, srcPath.lastIndexOf("/") + 1);
		fileEvent.setDestDir(destPath);
		fileEvent.setFilename(fileName);
		fileEvent.setSrcDir(srcPath);
		
		File file = new File(srcPath);
		
		if (file.isFile()) {
			try {
				DataInputStream diStream = new DataInputStream(new FileInputStream(file));
				long len = (int) file.length();
				byte[] fileBytes = new byte[(int) len];
				
				int read = 0;
				int numRead = 0;
				while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
					read = read + numRead;
				}

				fileEvent.setFileSize(len);
				fileEvent.setFileData(fileBytes);
				fileEvent.setStatus("Success");
				fileEvent.setCRC32Value(crc.getCRC32(srcPath,fileBytes));

				diStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				fileEvent.setStatus("Error");
			}
		} else {
			System.out.println("path is not pointing to a file");
			fileEvent.setStatus("Error");
			System.out.println("UDP client를 종료합니다.");
			System.exit(0);
		}
		return fileEvent;
	}
	
	public static void main(String[] args) {
		UdpClient client = new UdpClient("127.0.0.1", 8000, "C:/prac/a.txt", "C:/prac/test/");
		client.createConnection();
	}
}
