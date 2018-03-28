

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ControllerClient implements Runnable{
	Scanner sc;
	Socket server;
	DataOutputStream out;
	OutputStream outToServer;
	InputStream inFromServer;
	DataInputStream in;
	boolean userFlag,passFlag;
	String user,serverIP;
	int port;
	Thread t;

	@Override
	public void run(){
		try{
		sc = new Scanner(System.in); 
		//Reading server's ip address
		System.out.println("Enter Server's IP address: ");
		serverIP = sc.nextLine();
		//Reading port number
		System.out.println("Enter the port: ");
		port = (Integer.parseInt(sc.nextLine()));
		//establishing command/control connection
		server = new Socket(serverIP, port);
		outToServer = server.getOutputStream();
	    out = new DataOutputStream(outToServer);
		inFromServer = server.getInputStream();
	    in = new DataInputStream(inFromServer);
	    
	    while(true){
	    if(server.isConnected()){
			System.out.println("Connected to server");
			break;
	    }
	    }
	    
	    //Receive welcome message from server.
	    System.out.println("Message from server: \n"+ in.readUTF());
	    
		//user authentication
		System.out.println("Use USER and PASS commands to login");
		do{
			String username = sc.nextLine();//read string entered by user
			String bits[]=username.split(" ");//split it on <space>
			if(!bits[0].equalsIgnoreCase("user")||bits.length!=2){
				System.out.println("Use USER command to login"); //if first token is not USER, ask user to enter again
				continue;
			}
			out.writeUTF(username);//sending to server
			String responseCode = in.readUTF().split("= ")[1].split(" ")[0];//receive server response and search for "response code" in it.
			
			if(responseCode.equals("331")){
				userFlag=true;
				System.out.println("It is a valid username, now use PASS command to enter password");
				user=username.split(" ")[1];
			}else{
				System.out.println("Invalid username, try again");
			}
		}while(!userFlag);
		if(userFlag){
			do{
				String password = sc.nextLine();//read string entered by user
				String bits[]=password.split(" ");
				if(!bits[0].equalsIgnoreCase("pass")||bits.length!=2){
					System.out.println("Use PASS command to login");//if first token is not PASS, ask user to enter again
					continue;
				}
				out.writeUTF(password);//write to server
				String responseCode = in.readUTF().split("= ")[1].split(" ")[0];//receive message from server and search for "response code"
				
				if(responseCode.equals("230")){
					passFlag=true;
					System.out.println("Logged in successfully");
				}else{
					System.out.println("Invalid password, try again");
				}
			}while(!passFlag);
		}
		if(userFlag&&passFlag){   //if both username and password exist and match
			commandHandler();  // start the command handler module which manages the commands entered by user.
		}
		
		
		
		}catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
		
		
		
	}

	//Default contructor
	ControllerClient(){
		t= new Thread(this,"control"); //creates the control thread immediately this class is instantiated.
		t.start();			//start the thread
		System.out.println("Control Thread started");
	}
	
	//command handler module
	@SuppressWarnings("unchecked")
	public void commandHandler() throws IOException, ClassNotFoundException{
		while(true){
			System.out.print("Command>");
			String input=sc.nextLine();	//read command entered by user.
			String commands[]=input.split(" "); //split the command to 
			String command=commands[0];	    // see which operation it is.
			switch(command){
				case "STOR":
					if(commands.length>2||commands.length<=1){   //if STOR is not followed by one filename
						System.out.println("Try STOR [filepath]");	//show correct format for using the command
						break;
					}
					if(new File(commands[1]).isFile()){
						commands[1]=commands[1].replaceAll("\\\\", "\\\\\\\\"); //replace the single backslashes with double backslashes.
						String path[]=commands[1].split("\\\\"); 
						//--------------------
						out.writeUTF("LIST"); //receive a list of files on server
						ArrayList<String> list1=new ArrayList<String>();
						ObjectInputStream ois1 = new ObjectInputStream(inFromServer);
						list1=(ArrayList<String>)ois1.readObject();
						String fileName;
						boolean noFlag=false;
						for(String s:list1){
							String tokens[]=s.split("/");
							fileName=tokens[tokens.length-1];
							if(fileName.equals(path[path.length-1])){    //check if file to be STOR is already existing on server
							System.out.println("File with same name exists.It will be deleted and replaced(yes/no):");
							String op1=sc.nextLine();
							if(!op1.equalsIgnoreCase("yes")){	//if user enters no/NO
								noFlag=true;			//cancel the STOR operation
								break;
							}
							}
						}
						//--------------------
						if(!noFlag){					//if user hasn't entered no 
						out.writeUTF("STOR "+path[path.length-1]);	//send server the STOR command and filename
						out.writeLong(new File(commands[1]).length());	//send the filesize to server
						String responseCode = in.readUTF().split("= ")[1].split(" ")[0]; //receive response code from server
						if(responseCode.equals("200")){
							//create a sending thread
							new ClientSupport("STOR", new File(commands[1]), serverIP, port, "stor_"+path[path.length-1]);
						}else{
							System.out.println("Error sending file, Try again.");
						}}
					}else{
						System.out.println("Invalid file name");
					}
					break;
				case "RETR":
					if(commands.length>2||commands.length<=1){ 		//checking for right number of arguements
						System.out.println("Try RETR [filename]");
						break;
					}
					out.writeUTF("RETR "+commands[1]);			//sending RETR command and filename to server
					String serverResponse=in.readUTF();			//read response code
					String responseCode=serverResponse.split("= ")[1].split(" ")[0];	
					if(responseCode.equals("200")){
						long fileSize=Long.parseLong(serverResponse.split("size   =")[1]);
						boolean flag=false;
						while(!flag){
						//read location to save file
						System.out.println("Enter a location to store file(or NO to exit): ");
						String path=sc.nextLine();
						if(path.equals("NO")||path.equals("no")){
							out.writeBoolean(false);
							break;
						}
						path=path.replaceAll("\\\\", "\\\\\\\\");
						File f =new File(path);
						if(f.exists()&&f.isDirectory()){
							File f1=new File(path+commands[1]);
							if(f1.exists()&&!f1.isDirectory()){	//if a file with same name exists in given directory
								System.out.println("File with same name exists.It will be deleted and replaced(yes/no):");
								String op=sc.nextLine();
								if(!op.equals("yes")&&!op.equals("YES")){
									continue;
								}
							}
							out.writeBoolean(true);
							flag=true;
							//start a RETR thread
							new ClientSupport("RETR", serverIP, port, "retr_"+commands[1], path+commands[1], fileSize);
						}else{
							System.out.println("Folder doesn't exist");
						}
						}
						
					}else{
						System.out.println("Retrieve cannot be done");
					}
					//operation
					break;
				case "PORT":
					//checking for right number of arguements
					if(commands.length>2||commands.length<=1){
						System.out.println("Try PORT [port_number]");
						break;
					}
					out.writeUTF("PORT " +commands[1]); //sending PORT command and port number to server.
					String serverResponse2=in.readUTF();
					String responseCode2=serverResponse2.split("= ")[1].split(" ")[0];
					if(responseCode2.equals("200")){		//checking the response code from server
					port=Integer.parseInt(commands[1]);
					System.out.println("port changed to :"+port);
					}else {
						System.out.println("Invalid Port number");
					}
					
					break;
				case "QUIT":
					out.writeUTF("QUIT");		//sending QUIT command to server
					String res=in.readUTF().split("= ")[1].split(" ")[0];
					//checking for response from server	
					if(res.equals("226")){					
					System.out.println("Logged out. All running transactions will be completed");
					//close all connections
					inFromServer.close();
					in.close();
					outToServer.close();
					out.close();
					if(server.isConnected())
						server.close();
					sc.close();
					System.exit(0);
					break;
					}
				case "NOOP":
					if(commands.length!=1){			//checking for number of arguements
						System.out.println("Try NOOP");
						break;
					}
					out.writeUTF("NOOP");			//writing NOOP command to server
					String response=in.readUTF().split("= ")[1].split(" ")[0];
					if(response.equals("200")){
						System.out.println("Command OK");
					}else{
						System.out.println("Error in doing NOOP, try again");
					}
					break;
				case "LIST":
					if(commands.length!=1){				//checking for right number of arguements	
						System.out.println("Try LIST");
						break;
					}
					out.writeUTF("LIST");				//writing LIST command to server
					ArrayList<String> list=new ArrayList<String>();
					ObjectInputStream ois = new ObjectInputStream(inFromServer);
					//read the list of filenames from server
					list=(ArrayList<String>)ois.readObject();
					System.out.println("=======================YOUR FILES=====================");
					for(String s:list){
						String tokens[]=s.split("/");
						System.out.println(tokens[tokens.length-1]); //print filename one-by-one
					}
					System.out.println("======================================================");
					break;
				case "MKDIR":
					if(commands.length>2||commands.length<=1){
						System.out.println("Try MKDIR [folder name]");
						break;
					}
					out.writeUTF("MKDIR "+commands[1]);
					String response1=in.readUTF().split("= ")[1].split(" ")[0];
					if(response1.equals("257")){
						System.out.println("Folder Created");
					}else{
						System.out.println("Error in doing MKDIR, try again");
					}
					break;
				case "STAT":
					if(commands.length!=1){
						System.out.println("Try STAT");
						break;
					}
					out.writeUTF("STAT");
					String resp=in.readUTF().split("= ")[1].split(" ")[0];
					if(resp.equals("200")){
					ArrayList<String> operations=new ArrayList<String>();
					ObjectInputStream os = new ObjectInputStream(inFromServer);
					operations=(ArrayList<String>)os.readObject();
					System.out.println("======================STATUS=====================");
					for(String s:operations){
						System.out.println(s);
					}
					System.out.println("======================================================");
					}else{
						System.out.println("Operation cannot be completed.Please try again.");
					}
					break;
				case "CANCEL":
					if(commands.length!=1){
						System.out.println("Try CANCEL");
						break;
					}
					out.writeUTF("CANCEL");
					String response2=in.readUTF().split("= ")[1].split(" ")[0];
					if(response2.equals("426")){
						System.out.println("Cancelled all running operations");
					}else{
						System.out.println("Error in cancelling, try again");
					}
					break;
				case "PWD":
					if(commands.length!=1){
						System.out.println("Try PWD");
						break;
					}
					out.writeUTF("PWD");
					String pwdResp=in.readUTF().split("= ")[1].split(" ")[0];
					if(pwdResp.equals("200")){
						System.out.println(in.readUTF());
					}
					break;
				case "CWD":
					if(commands.length>2||commands.length<=1){
						System.out.println("Try CWD [Directory]");
						break;
					}
					out.writeUTF("CWD "+commands[1]);
					String cwdResp=in.readUTF().split("= ")[1].split(" ")[0];
					if(cwdResp.equals("200")){
						System.out.println("Working directory changed.");
					}else if(cwdResp.equals("550")){
						System.out.println("Directory doesn't exit.");
					}
					break;
				case "CDUP":
					if(commands.length!=1){
						System.out.println("Try CDUP");
						break;
					}
					out.writeUTF("CDUP");
					String cdupResp=in.readUTF().split("= ")[1].split(" ")[0];
					if(cdupResp.equals("200")){
						System.out.println("Changed to parent directory");
					}
					break;
				case "DEL":
					if(commands.length>2||commands.length<=1){
						System.out.println("Try DEL [filename]");
						break;
					}
					out.writeUTF("DEL "+commands[1]);
					String resp1=in.readUTF().split("= ")[1].split(" ")[0];
					if(resp1.equals("200")){
						System.out.println("File deleted successfully.");
					}else if(resp1.equals("450")){
						System.out.println("File busy, operation aborted.");
					}else if(resp1.equals("550")){
						System.out.println("File not found.");
					}
					break;
				default:
					System.out.println("Invalid Command");
					break;
				}
			}
		}
		
	
	//-----------
	
}
