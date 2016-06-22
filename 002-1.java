package src.ding.show;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

class RunWith {
	public static void main(String[] args) throws IOException {
		try {
			System.out.println("Show argument and run(V1.0),console based.All rights resvered.\n");
			String sstr = args[0];
			String eqs = "--mc";
			System.out.println("Now start output the arguments.");
			for(int i=0;i<args.length;i++) {
				System.out.println("The " + i + " of the string is:" + args[i]);
			}
			System.out.println("End output.");
			if (args[0].equals(eqs)) {
				sstr = args[1];
				System.out.println("MC Crack mode used.");
				int lgh = args.length - 1;
				int ne = 2;
				for(int c=2;c<lgh;c++) {
					if(args[c].equals("--username")) {
						ne++;
						break;
					}
					ne++;
				}
				for(int a=2;a<ne;a++) {
					sstr += " " + args[a];
			 	}
			 	String nme;
			 	try {
			 		FileReader fr = new FileReader("Username.txt");
			 		BufferedReader br = new BufferedReader(fr);
			 		nme = br.readLine();
			 		for(int n=0;n<2;n++) {
			 			nme = br.readLine();
			 		}
			 		System.out.println("Username has setted to " + nme);
			 		br.close();
			 	} 
			 	catch(FileNotFoundException e) {
			 		System.out.println("Username.txt not found. Creating new file...\nChange your MC's username in it.");
			 		BufferedWriter bw = new BufferedWriter(new FileWriter("Username.txt"));
			 		bw.write("#This file is used to crack MC's Username.");
			 		bw.newLine();
			 		bw.write("#By changing the name next line ,you change your name.");
			 		bw.newLine();
			 		bw.write("Player");
			 		bw.close();
			 		nme = "Player";
			 		System.exit(1);
			 	}
			 	sstr += " " + nme;
			 	for(int a=(++ne);a<lgh;a++) {
					sstr += " " + args[a];
			 	}
			} else {				
				for(int p=1;p<args.length;p++) {
					sstr += " " + args[p];
			 	}
			}
			System.out.println("Now execute " + sstr);
			Execute exc = new Execute();
			exc.CommandExection(sstr);
		}
		catch (ArrayIndexOutOfBoundsException e){
			System.out.println("No arguments found.\nUsage:RunCon.jar program args...");	
			System.exit(1);
		}	
	}
}

class Execute {
	public void CommandExection(String commandline) {
		try {
  		  System.out.println("Starting Program...");
    		Process par = Runtime.getRuntime().exec(commandline);
    		System.out.println("Now start output.\n");
    		InputStream[] inStreams = new InputStream[] {par.getInputStream(),par.getErrorStream()};
    	  new ConsoleTextArea(inStreams);
   	 		int vl = par.waitFor();
    		System.out.println("");
    		System.out.println("End output.");
    		System.out.println("Program exited with exitcode " + vl);
    		System.exit(vl);
		} catch (Exception e) {
			  System.out.println("Exception happened.");
  		  e.printStackTrace();
		}
	}
}

class ConsoleTextArea {
	
	public ConsoleTextArea(InputStream[] inStreams) throws InterruptedException{
	for(int i = 0; i < inStreams.length; ++i)
		startConsoleReaderThread(inStreams[i]);
	} // ConsoleTextArea()
	
	public ConsoleTextArea() throws IOException,InterruptedException{
		final LoopedStreams ls = new LoopedStreams();
		//relocale System.out and stem.err
		PrintStream ps = new PrintStream(ls.getOutputStream());
		System.setOut(ps);
		System.setErr(ps);
		startConsoleReaderThread(ls.getInputStream());
	} // ConsoleTextArea()

	private void startConsoleReaderThread(InputStream inStream) throws InterruptedException{
		final BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
		Thread ts = new Thread(new Runnable() {
			public void run() {
				StringBuffer sb = new StringBuffer();
				try {
					String s;
					while((s = br.readLine()) != null) {
						System.out.println(s);
					}					
				}
				catch(IOException e) {
					System.out.println("Read Error message from BufferedReader" + e);
					System.exit(1);
				}
			}
		});
		ts.start();	
		ts.join();
	} // startConsoleReaderThread()
}

class LoopedStreams {
	private PipedOutputStream pipedOS = new PipedOutputStream();
	private boolean keepRunning = true;
	private ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream() {
		public void close() {
			keepRunning = false;
			try {
				super.close();
				pipedOS.close();
			}
			catch(IOException e) {
				System.out.println(e);
				System.exit(1);
			}
	 }
	};
	
	private PipedInputStream pipedIS = new PipedInputStream() {
		public void close() {
			keepRunning = false;
			try {
				super.close();
			}
			catch(IOException e) {
		  	System.out.println(e);
				System.exit(1);
			}
		}
	};
	
	public LoopedStreams() throws IOException {
		pipedOS.connect(pipedIS);
		startByteArrayReaderThread();
	} // LoopedStreams()
	
	public InputStream getInputStream() {
		return pipedIS;
	} // getInputStream()
	
	public OutputStream getOutputStream() {
		return byteArrayOS;
	} // getOutputStream()
	
	public void startByteArrayReaderThread() {
		new Thread(new Runnable() {
			public void run() {
				while(keepRunning) {
					// Check bytes in it
					if(byteArrayOS.size() > 0) {
						byte[] buffer = null;
						synchronized(byteArrayOS) {
							buffer = byteArrayOS.toByteArray();
							byteArrayOS.reset(); // Clear buffer
						}
						try {
							// Send the info. to PipedOutputStream
							pipedOS.write(buffer, 0, buffer.length);
						}
						catch(IOException e) {
							System.out.println(e);
							System.exit(1);
						}
					}
					else { // No info. can be used. Thread went into sleep
						try {
							// Check for info. per second in ByteArrayOutputStream
							Thread.sleep(1000);
						}
						catch(InterruptedException e) {}
					}
				}
			}
		}).start();
	} // startByteArrayReaderThread()
}   // LoopedStreams