package Server;

import java.io.*;
import java.net.*;

import metaEvent.*;
import javax.swing.JOptionPane;


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
    CRC32get crc = new CRC32get();
    static double avgTime;
    
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
			long existFileSize = 0;
			int result = 1; // ConfirmDialog 반환값.
			   if(!dstFile.exists()){
			    // 없으면,
			   }else{
			    // 잇으면,
			    result = JOptionPane.showConfirmDialog(null, "' 다음 경로에 \n" + fileEvent.getDestDir()+ "\n"+fileEvent.getFilename()+"가 이미 존재합니다. 이어받기 하시겠습니까?");
			    if(result == 0){    // 예
			     existFileSize = dstFile.length();
			    }else if(result == 1){  // 아니오.
			     existFileSize = 0;
			    }
			   }

			fileOutputStream = new FileOutputStream(dstFile,true);
			long len = existFileSize;
			byte[] fileBytes = new byte[(int) len];
			System.out.format("dstFile %d \n",fileBytes.length);
			System.out.format("getFileData %d\n",fileEvent.getFileData().length);
			fileOutputStream.write(fileEvent.getFileData(),fileBytes.length,fileEvent.getFileData().length-fileBytes.length);
			fileOutputStream.flush();
			fileOutputStream.close();
	

			long totaltime = System.currentTimeMillis() - fileEvent.gettime();
			long s = fileEvent.getFileSize();
			avgTime += (double) s/(totaltime * 1000);
			
			if(checkCRCValue(fileEvent, crc.getCRC32(outputFile,fileEvent.getFileData())) == 0 ) {
				System.out.format("무결성 보장!\n");
			} else {
				System.out.format("무결성을 보장할 수 없습니다!\n");
			}
			
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
		String metaData;
		TcpServer server = new TcpServer(8000);
		server.waitForClient();
		metaData = server.receive();
		server.send("continue");
		
		for(int i = 0 ; i < Integer.parseInt(metaData) ; i ++) {
			server.receiveFile();
			server.send("continue");
		}
		
		System.out.println("file 전송속도 : "+ 1000 * avgTime / (double) Integer.parseInt(metaData)  + "bps");
		System.out.println("file 전송속도 : "+ avgTime / (double) Integer.parseInt(metaData)  + "Mb/s");
		
		server.send("파일을 잘 받았습니다!");
		server.close();
	}
}