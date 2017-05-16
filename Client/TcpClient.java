package Client;

import java.io.*;
import java.net.*;
import metaEvent.*;

public class TcpClient {
	Socket socket;
	ObjectOutputStream outputStream;
	boolean isConnected = false;
	FileEvent fileEvent;
	BufferedReader in;
	PrintWriter out;
	String srcPath, destPath, ip;
	int port = 8000;
	CRC32get crc = new CRC32get();
	
	public TcpClient(String ip, int port, String srcPath, String destPath) {
		
		this.ip = ip;
		this.port = port;
		this.srcPath = srcPath;
		this.destPath = destPath;
		
		while(!isConnected) {
			try {
				socket = new Socket(ip, port);	
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
				outputStream = new ObjectOutputStream(socket.getOutputStream());
				//서버 소켓에 스트림을 연결
				printInfo();
				isConnected = true;

			} catch (IOException e) {
				System.out.println(e.toString());			
			}
		}
	}
	
	public String receive() {
		String mesg = null;
		try {
			//서버 소켓으로부터 받은 메시지를 화면에 출력
//			System.out.println("[서버] "+ in.readLine());
			mesg = in.readLine();
			return mesg;
		} catch (IOException e) {
			System.out.println(e.toString());			
		}
		return mesg;
	}
	
	public void send(String msg) {
		//서버 소켓에 메시지 전송
		out.println(msg);
		out.flush();
		System.out.println("[클라이언트] " + msg);		
	}
	
	public void sendFile(String srcPath) {
		FileEvent fileEvent = new FileEvent();
		
		String fileName = srcPath.substring(srcPath.lastIndexOf("\\") + 1, srcPath.length());
		//String path = srcPath.substring(0, srcPath.lastIndexOf("/") + 1);
		fileEvent.setDestDir(destPath);
		fileEvent.setFilename(fileName);
		fileEvent.setSrcDir(srcPath);
		
		File file = new File(srcPath);

			if (file.isFile()) {
				try {
//					DataInputStream diStream = new DataInputStream(new FileInputStream(file));
					long len = (int) file.length();
					byte[] fileBytes = new byte[(int) len];

//					int read = 0;
//					int numRead = 0;
//					while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
//						read = read + numRead;
////					    if(){
////		                     System.exit(0);
////		                  }
//					}
//					
					long startTime = System.currentTimeMillis();
					fileEvent.settime(startTime);
					fileEvent.setFileSize(len);
					fileEvent.setFileData(fileBytes);
					fileEvent.setStatus("Success");
					fileEvent.setCRC32Value(crc.getCRC32(srcPath,fileBytes));
					
					System.out.println((int) fileBytes.length+"\n");

					for(int i=0; i<fileBytes.length; i++){
						System.out.println(fileBytes[i]);
					}
//					System.out.println();
//					for(int i=0; i<sendBytes.length; i++){
//					System.out.println(sendBytes[i]);
//			}
					
//					diStream.close();
				} catch (Exception e) {
					e.printStackTrace();
					fileEvent.setStatus("Error");
				}
			} else {
				System.out.println("path is not pointing to a file");
				fileEvent.setStatus("Error");
				System.out.println("TCP client를 종료합니다.");
				System.exit(0);
			}
			// write file
			try {
				outputStream.writeObject(fileEvent);
				System.out.println("파일 전송 완료");
				Thread.sleep(3000);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
}