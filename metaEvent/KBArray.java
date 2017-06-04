package metaEvent;

import java.io.File;
import java.util.*;

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
		double[] kbArrVal = new double[kbArr.size()];
		
		for(int i = 0 ; i < kbArr.size(); i++){
			kbArrVal[i] = kbArr.get(i);
		}
		
		indexes = indexSort(kbArrVal, false);
				
		File[] tempList = directory.listFiles();
				
		for(int i = 0 ; i < fList.length; i++) {
			tempList[i] = fList[getArrayIndex(indexes, i)];
		}
		fList = tempList;
		
		return fList;
	}
	
	 public int getArrayIndex(int[] arr,int value) {

	        int k=0;
	        for(int i=0;i<arr.length;i++){

	            if(arr[i]==value){
	                k=i;
	                break;
	            }
	        }
	    return k;
	}
	 
	public int[] indexSort(final double[] v, boolean keepUnsorted) {
	    final Integer[] II = new Integer[v.length];
	    for (int i = 0; i < v.length; i++) II[i] = i;
	    Arrays.sort(II, new Comparator<Integer>() {
	        @Override
	        public int compare(Integer o1, Integer o2) {
	            return Double.compare(v[o1],v[o2]);
	        }
	    });
	    int[] ii = new int[v.length];
	    for (int i = 0; i < v.length; i++) ii[i] = II[i];
	    if (!keepUnsorted) {
	        double[] clon = v.clone();
	        for (int i = 0; i < v.length; i++) v[i] = clon[II[i]];
	    }
	    return ii;
	}
}
