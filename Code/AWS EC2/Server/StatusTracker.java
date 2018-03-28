import java.net.Socket;

/**
 * @author Md Arshad
 *
 */
public class StatusTracker {
	ServerSupport servSupp = null;//used for cancel op, when called by client servsupp.running is set to false and file transfer is terminated.
	Socket s =null;
	String Operation = null;
	long completeFileSize=0;
	String filePath=null;
	String file_Name = null;
	int currentFileSize=0;
	String Status;
	int percentageCompleted;
	public StatusTracker(ServerSupport servSupp, Socket s, String operation, long completeFileSize, String filePath,
			String file_Name, int currentFileSize, String status, int percentageCompleted) {
		super();
		this.servSupp = servSupp;
		this.s = s;
		this.Operation = operation;
		this.completeFileSize = completeFileSize;
		this.filePath = filePath;
		this.file_Name = file_Name;
		this.currentFileSize = currentFileSize;
		this.Status = status;
		this.percentageCompleted = percentageCompleted;
	}
	public ServerSupport getServSupp() {
		return servSupp;
	}
	public void setServSupp(ServerSupport servSupp) {
		this.servSupp = servSupp;
	}
	public Socket getS() {
		return s;
	}
	public void setS(Socket s) {
		this.s = s;
	}
	public String getOperation() {
		return Operation;
	}
	public void setOperation(String operation) {
		Operation = operation;
	}
	public long getCompleteFileSize() {
		return completeFileSize;
	}
	public void setCompleteFileSize(long completeFileSize) {
		this.completeFileSize = completeFileSize;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFile_Name() {
		return file_Name;
	}
	public void setFile_Name(String file_Name) {
		this.file_Name = file_Name;
	}
	public int getCurrentFileSize() {
		return currentFileSize;
	}
	public void setCurrentFileSize(int currentFileSize) {
		this.currentFileSize = currentFileSize;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public int getPercentageCompleted() {
		return percentageCompleted;
	}
	public void setPercentageCompleted(int percentageCompleted) {
		this.percentageCompleted = percentageCompleted;
	}
	@Override
	public String toString() {
		return "StatusTracker [servSupp=" + servSupp + ", s=" + s + ", Operation=" + Operation + ", completeFileSize="
				+ completeFileSize + ", filePath=" + filePath + ", file_Name=" + file_Name + ", currentFileSize="
				+ currentFileSize + ", Status=" + Status + ", percentageCompleted=" + percentageCompleted + "]";
	}
	
	
}