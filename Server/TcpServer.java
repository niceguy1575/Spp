package Server;

import java.io.*;
import java.net.*;
import java.util.*;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RserveException;

import metaEvent.*;

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
	Rserv Rserv = new Rserv();

	public TcpServer (int port)throws RserveException {
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
			
			// 파일 저장
			fileOutputStream = new FileOutputStream(dstFile);
			fileOutputStream.write(fileEvent.getFileData());
			fileOutputStream.flush();
			fileOutputStream.close();
			
			// Rprocess
			scanAndAnalysis();
			
			long totaltime = System.currentTimeMillis() - fileEvent.gettime();
			long s = fileEvent.getFileSize();
			avgTime += Math.round((double) s/(totaltime * 1000) * 100d);

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
	
	public void scanAndAnalysis(){
		fileReader fr = new fileReader();
		Scanner headerScan = new Scanner(System.in);
		Scanner headerNumScan = new Scanner(System.in);
		List<String> header= new ArrayList<String>();
		
		header = fr.read_head(fileEvent.getDestDir() + fileEvent.getFilename());
		
		System.out.println("------------------------------ ");
		System.out.println(" header 출력");
		
		for(String val : header) {
			System.out.print(val + "\t");
		}
		System.out.println();
	
		System.out.println("------ Summary ------");
		System.out.print(" 살펴볼 변수는 무엇입니까?    > ");
		boolean scanNext = headerScan.hasNext();
		if( scanNext ) {

			String variable = headerScan.nextLine();
			
			System.out.print("Graphic : 1. histogram, 2. boxplot > ");
			int value = headerNumScan.nextInt();
			headerNumScan.nextLine();
			
			System.out.println("------ Regression ------");
	
			System.out.print(" 반응 변수는 무엇입니까?    > ");
			String response = headerScan.nextLine();
	
			System.out.print(" 몇개의 독립변수를 사용하시겠습니까?   > ");
			int indepNum = headerNumScan.nextInt();
			headerNumScan.nextLine();
			
			String indep[] = new String[indepNum];
			
			System.out.print("Press Enter");
			headerScan.nextLine();

			for(int i = 0 ; i < indepNum ; i ++){
				System.out.print((i+1) + "번째 독립 변수는 무엇입니까?    > ");
				indep[i] = headerScan.next();
				headerScan.nextLine();
			}
			
			System.out.println("독립변수가 하나일 경우에는 linear model plot이 자동으로 그려집니다.");
			
//			headerNumScan.close();
//			headerScan.close();
			
			try {
				Rprocess(value, response, variable, indep);
			} catch (REXPMismatchException | REngineException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void Rprocess(int value, String response, String variable, String ...indep)  throws REXPMismatchException, REngineException {
        			
			String resDir = fileEvent.getDestDir() + "Results\\";
			
			if (!new File(resDir).exists()) {
				new File(resDir).mkdirs();
			}
			
	       Rserv.read_file( fileEvent.getDestDir() + fileEvent.getFilename() );
	       Rserv.linearModel(resDir, response, indep);
	       Rserv.summary(resDir, variable);
	       Rserv.summary_plot(resDir, variable, value);
	       if(indep.length == 1) {
		       Rserv.lm_plot(resDir, response, indep);
	       }
	}
	
	public static void main(String[] args)  throws RserveException{
		
		try {
			
			String metaData;
			TcpServer server = new TcpServer(8000);
			server.waitForClient();
			metaData = server.receive();
			
			if(metaData.equals("0")){
				System.exit(0);
			}
			
			server.send("continue");
					
			for(int i = 0 ; i < Integer.parseInt(metaData) ; i ++) {
				server.receiveFile();
				if( i == Integer.parseInt(metaData) - 1) {
					server.send(String.valueOf(avgTime));
				}
				else {
					server.send("continue");
				}
			}
			
			server.close();
		} catch(Exception e) {
			System.exit(0);
		}
	}
}