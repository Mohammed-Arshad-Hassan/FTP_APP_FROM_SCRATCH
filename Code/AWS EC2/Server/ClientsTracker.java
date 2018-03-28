

import java.net.Socket;

public class ClientsTracker {
	Socket socket ;
	String User_name;
	String path;
	String root;
	public ClientsTracker(Socket socket, String user_name, String path, String root) {
		super();
		this.socket = socket;
		User_name = user_name;
		this.path = path;
		this.root = root;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public String getUser_name() {
		return User_name;
	}
	public void setUser_name(String user_name) {
		User_name = user_name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getRoot() {
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}
	
}
