package metaEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KBArray {
	public int idx_64(List<Double> kbArr) {
		int idx = 0;
		for(int i = 0 ; i < kbArr.size(); i ++) {
			if(kbArr.get(i) < 64 ) {
				idx = i;
				return idx;
			}
		}
		return kbArr.size() - 1;
	}

	public List<Double> KBArr(String srcPath) {
		File directory = new File(srcPath);
		File[] fList = directory.listFiles();
		double KB;

		List<Double> kbArr = new ArrayList<Double>();

		for(File file:fList) {
			KB = file.length()/1024;
			kbArr.add(KB);
		}
		return kbArr;
	}

	public File[] revDir(String srcPath) {
		File directory = new File(srcPath);
		File[] fList = directory.listFiles();
				
		List<Double> kbArr = new ArrayList<Double>();
		kbArr = KBArr(srcPath);

		List<Double> kbIdx = new ArrayList<Double>(kbArr);
		Collections.sort(kbArr ,Collections.reverseOrder());

		int[] indexes = new int[kbArr.size()];
		
		for(int i = 0 ; i < kbArr.size() ; i ++) {
			indexes[i] = kbArr.indexOf(kbIdx.get(i));
		}
		
		File[] tempList = directory.listFiles();
		
		for(int i = 0 ; i < fList.length; i++) {
			tempList[indexes[i]] = fList[i];
		}
		fList = tempList;
		
		return fList;
	}
}
