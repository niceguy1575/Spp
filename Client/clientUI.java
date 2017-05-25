package Client;

import java.io.*;
import java.util.*;
import metaEvent.*;

public class clientUI {
			
	public static void main(String[] args) {
		
		KBArray KBArr = new KBArray();
		
		System.out.println("Welcome! We are Statistic Analysis Server!");

		System.out.println("------------ IP Address -----------------");
		System.out.println("-----------------------------------------");
		System.out.print("   > ");

		Scanner ipScan = new Scanner(System.in);
		String ipAdr = ipScan.nextLine();
		ipScan.close();

		System.out.println();
		System.out.println("-----------------------------------------");
		System.out.println("------------ 1. 분석할 파일의 경로 ------------");
		System.out.println("------ 분석파일이 저장된 경로를 입력해주세요. -------");
		System.out.println("-- 분석파일은 독립변수와 반응변수로 구성되어있어야 합니다.-");
		System.out.println("------- 또한 첫행은 각 변수의 이름을 넣어주세요 ------");
		System.out.println("-----------------------------------------");
		System.out.print("   > ");
		Scanner pathScan = new Scanner(System.in);
//		String srcPath = pathScan.nextLine();
		
		System.out.println("------------------------------------------");
		System.out.println("----------- 2. 분셕결과를 저장할 경로 ------------");
		System.out.println("------------------------------------------");
		System.out.print("   > ");
//		String destPath = pathScan.nextLine();
		pathScan.close();
		
		String srcPath = "C:\\prac\\";
		String destPath = "C:\\prac1\\";

		// Client Connect
		TcpClient TCPclient = new TcpClient(ipAdr, 8000, srcPath, destPath);
		UdpClient UDPclient = new UdpClient(ipAdr, 8001, srcPath, destPath);
		
		File[] fList = KBArr.revDir(srcPath);

		List<Double> kbArr = new ArrayList<Double>();
		kbArr = KBArr.KBArr(srcPath);
		Collections.sort(kbArr ,Collections.reverseOrder());
		
		int k = KBArr.idx_64(kbArr);
		int n = kbArr.size();
		
		
		try {
			
				if( k == n-1 ) {
					if ( kbArr.get(k) > 64 ) {
						// TCP n
						String goBack = null;
						TCPclient.send(String.valueOf( n ));
						for(int i = 0 ; i < n ; i ++) {
							goBack = TCPclient.receive();
							if(goBack.equals("continue")) {
								TCPclient.sendFile(fList[i].getAbsolutePath());
							}
						}
					} else{
						// TCP n-1, UDP 1
						String goBack = null;
						TCPclient.send(String.valueOf( n-1 ));
						for(int i = 0 ; i < n-1 ; i ++) {
							goBack = TCPclient.receive();
							if(goBack.equals("continue")) {
								TCPclient.sendFile(fList[i].getAbsolutePath());
							}
						}
						Thread.sleep(10000);
						String UDPSpeed = UDPclient.createConnection(1);
					}
				}
				
				else if( k == 0) {
					if( kbArr.get(k) < 64 ) {
						// do only UDP n
						String UDPSpeed = UDPclient.createConnection(n);
					}
					else {
						// TCP 1, UDP n-1
						String goBack = null;
						TCPclient.send(String.valueOf( 1 ));
						for(int i = 0 ; i < 1 ; i ++) {
							goBack = TCPclient.receive();
							if(goBack.equals("continue")) {
								TCPclient.sendFile(fList[i].getAbsolutePath());
							}
						}
						
						Thread.sleep(10000);
						String UDPSpeed = UDPclient.createConnection( n-1 );
					}
				}
				
				else {
					// TCP k-1, UDP n-k+1
					String goBack = null;
					TCPclient.send(String.valueOf( k ));
					for(int i = 0 ; i < k ; i ++) {
						goBack = TCPclient.receive();
						if(goBack.equals("continue")) {
							TCPclient.sendFile(fList[i].getAbsolutePath());
						}
					}
					
					String TCPSpeed = TCPclient.receive();
					TCPclient.close();
					
					Thread.sleep(10000);
		
					String UDPSpeed = UDPclient.createConnection( n - k );
				}
				
			}
		 catch (InterruptedException e) {
				e.printStackTrace();
		 }
		
		System.out.println("Client를 종료합니다.");
//		double finalSpeed = Double.parseDouble(TCPSpeed) + Double.parseDouble(UDPSpeed);
//		
//		finalSpeed = finalSpeed / fList.length;
//		System.out.println("file 전송속도 : "+ 1000 * Math.round(finalSpeed)  + "bps");
//		System.out.println("file 전송속도 : "+ Math.round(finalSpeed)  + "Mb/s");
		System.exit(0);
	}
}