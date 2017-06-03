package Server;

import java.io.*;
import java.net.*;
import java.util.*;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;

import metaEvent.*;

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
				
				String strOut = "c";
				byte[] strOutByte = strOut.getBytes();
				
				sPack = new DatagramPacket(strOutByte, strOutByte.length, client, cport);
				dsock.send(sPack);
				
				while(cnt > 0) {				
					dsock.receive(rPack);	

					byte[] data = rPack.getData();
					
					ByteArrayInputStream in = new ByteArrayInputStream(data);
					ObjectInputStream is = new ObjectInputStream(in);
					fileEvent = (FileEvent) is.readObject();
					
					long totaltime = System.currentTimeMillis() - fileEvent.gettime();
					long s = fileEvent.getFileSize();
					
					avgTime += Math.round((double) s/(totaltime * 1000) * 100d );
					
					if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
						System.out.println("Errors happened! while data packing");
						System.exit(0);
					}
					createAndWriteFile();
					scanAndAnalysis();
					cnt = cnt - 1;
					if(checkCRCValue(fileEvent, crc.getCRC32(fileEvent.getDestDir() + fileEvent.getFilename(), fileEvent.getFileData())) == 0 ) {
						System.out.format("무결성 보장!\n");
					} else {
						System.out.format("무결성을 보장할 수 없습니다!\n");
					}
				}
				
				String speed = String.valueOf(avgTime);
				byte[] speedOutByte = speed.getBytes();

				sPack = new DatagramPacket(speedOutByte, speedOutByte.length, client, cport);
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
		String variable = headerScan.nextLine();
		
		System.out.print("Graphic : 1. histogram, 2. boxplot > ");
		int value = headerNumScan.nextInt();
		
		System.out.println("------ Regression ------");

		System.out.print(" 반응 변수는 무엇입니까?    > ");
		String response = headerScan.nextLine();

		System.out.print(" 몇개의 독립변수를 사용하시겠습니까?   > ");
		int indepNum = headerNumScan.nextInt();
		
		String indep[] = new String[indepNum];
		
		for(int i = 0 ; i < indepNum ; i ++){
			System.out.print((i+1) + "번째 독립 변수는 무엇입니까?    > ");
			indep[i] = headerScan.nextLine();
		}
		
		System.out.println("독립변수가 하나일 경우에는 linear model plot이 자동으로 그려집니다.");
		
		headerNumScan.close();
		headerScan.close();
		try {
			Rprocess(value, response, variable, indep);
		} catch (REXPMismatchException | REngineException e) {
			e.printStackTrace();
		}
	}
	
	public void Rprocess(int value, String response, String variable, String ...indep)  throws REXPMismatchException, REngineException {
	        Rserv Rserv = new Rserv();
	       
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

	public static void main(String[] args)  {
		UdpServer server = new UdpServer(8001);
		server.createAndListenSocket();
	}
}
