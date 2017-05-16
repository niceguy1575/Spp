package Client;

import java.io.File;
import java.util.*;

public class clientUI {
	public static void main(String[] args) {

		System.out.println("------------ 접속할 IP주소를 입력해주세요 -------------------");
		System.out.println("----------------------------------------------------");
		System.out.println("   > ");

		Scanner ipScan = new Scanner(System.in);
		String ipAdr = ipScan.nextLine();
		System.out.println("ip address : " + ipAdr);
				
		System.out.println("------------------------------------------");
		System.out.println("------------ 경로를 입력해 주세요 ------------");
		System.out.println("------------------------------------------");
		System.out.println("------------ 1. SrcPath ---------------");
		System.out.println("------------------------------------------");
		System.out.println("   > ");
		Scanner pathScan = new Scanner(System.in);
		String srcPath = pathScan.nextLine();
		
		System.out.println("------------------------------------------");
		System.out.println("------------ 2. destPath ---------------");
		System.out.println("------------------------------------------");
		System.out.println("   > ");
		String destPath = pathScan.nextLine();

		
		srcPath = "C:\\prac\\";
		destPath = "C:\\prac1\\";

		pathScan.close();
		ipScan.close();
		
		File directory = new File(srcPath);
		File[] fList = directory.listFiles();	
		
		double KB;
		int i;
		
		List<Double> kbArr = new ArrayList<Double>();

		for(File file:fList) {
			KB = file.length()/1024;
			kbArr.add(KB);
		}

		List<Double> kbIdx = new ArrayList<Double>(kbArr);

//		Collections.sort(kbArr);
		Collections.sort(kbArr ,Collections.reverseOrder());
		int[] indexes = new int[kbArr.size()];
		
		for(i = 0 ; i < kbArr.size() ; i ++) {
			indexes[i] = kbArr.indexOf(kbIdx.get(i));
		}
		
		File[] tempList = directory.listFiles();	

		for(i = 0 ; i < fList.length; i++) {
//			System.out.println("path : " + tempList[i]);
			tempList[indexes[i]] = fList[i];
//			System.out.println("changed : " + tempList[indexes[i]]);
		}
		fList = tempList;
		
		TcpClient TCPclient = new TcpClient(ipAdr, 8000, srcPath, destPath);
		UdpClient UDPclient = new UdpClient(ipAdr, 8001, srcPath, destPath);
	
		int idx = 0;
		for(i = 0 ; i < kbArr.size(); i ++) {
			if(kbArr.get(i) < 64 ) {
				idx = i;
				break;
			}
		}

		// 1. TCP
		String goBack = null;
		TCPclient.send(String.valueOf(idx));
		for(i = 0 ; i < idx ; i ++) {
			goBack = TCPclient.receive();
			if(goBack.equals("continue")) {
				TCPclient.sendFile(fList[i].getAbsolutePath());
			}
		}
		
		String TCPSpeed = TCPclient.receive();
		System.out.println(TCPSpeed);
		TCPclient.close();

		String UDPSpeed = UDPclient.createConnection(idx);
		System.out.println(UDPSpeed);
				
		double finalSpeed = Double.parseDouble(TCPSpeed) + Double.parseDouble(UDPSpeed);
		
		finalSpeed = finalSpeed / fList.length;
		System.out.println("file 전송속도 : "+ 1000 * Math.round(finalSpeed)  + "bps");
		System.out.println("file 전송속도 : "+ Math.round(finalSpeed)  + "Mb/s");
		System.exit(0);
	}
}