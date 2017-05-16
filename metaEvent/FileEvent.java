package metaEvent;

import java.io.Serializable;

public class FileEvent implements Serializable {

	public FileEvent() {
	}

	private static final long serialVersionUID = 1L;
	
	private String destDir;
	private String srcDir;
	private String filename;
	private long fileSize;
	private byte[] fileData;
	private String status;
    private long crcRes;
    private long time;
    private long fileByteLen;
    private long read;
    
    public void setLen(long fileByteLen){
    	this.fileByteLen = fileByteLen;
    }
    
    public long getLen() {
    	return fileByteLen;
    }
    
    public void setRead(long read) {
    	this.read = read;
    }
    
    public long getRead(){
    	return read;
    }
    
    public void settime(long time){
    	this.time = time;
    }
    public long gettime(){
    	return time;
    }
	public String getDestDir() {
		return destDir;
	}

	public void setDestDir(String destDir) {
		this.destDir = destDir;
	}

	public String getSrcDir() {
		return srcDir;
	}

	public void setSrcDir(String srcDir) {
		this.srcDir = srcDir;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}
	
	public void setCRC32Value(long crc) {
		this.crcRes = crc;
	}
	
	public long getCRC32Value() {
		return crcRes;
	}
}