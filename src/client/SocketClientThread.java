package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class SocketClientThread extends Thread {

	private static final int TIMEOUT = 1500;
	private static final int MAXTRIES = 3;

	private String hostName;
	private int portNumber;
	private int number;
	private String protocol;
	private String sengMSG;

	private DataOutputStream outToServer;
	private BufferedReader inFromServer;
	private DatagramPacket receivePacket;
	private DatagramPacket sendPacket;
	private JTextArea taLog;

	public SocketClientThread(String hostname, int portNumber, int number,
			String protocol, JTextArea taLog) {
		this.hostName = hostname;
		this.portNumber = portNumber;
		this.number = number;
		this.protocol = protocol;
		this.taLog = taLog;
	}

	public void run() {
		try {
			if (protocol.equals("tcp")) {
				Socket socket = new Socket(hostName, portNumber);
				socket.setSoTimeout(TIMEOUT);
				outToServer = new DataOutputStream(socket.getOutputStream());
				inFromServer = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				outToServer.writeBytes(number + "\n");
				taLog.append("The Client Send number : " + number
						+ " to server IP: " + InetAddress.getByName(hostName)
						+ "\n");
				taLog.append("The port is: " + portNumber + " via " + protocol
						+ "\n");
				sengMSG = inFromServer.readLine();
				taLog.append(sengMSG);
				if (!sengMSG.isEmpty()) {
					taLog.append("\n" +  "Success" + "\n");
					taLog.append("****************************\n");
				}
				socket.close();
			}
			if (protocol.equals("udp")) {
				byte[] sendData = new byte[1024];
				byte[] receiveData = new byte[1024];
				DatagramSocket dataSocket = new DatagramSocket();
				InetAddress loc = InetAddress.getByName(hostName);
				sendData = new String(number + " ").getBytes();
				sendPacket = new DatagramPacket(sendData, sendData.length, loc,
						portNumber);
				receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				dataSocket.setSoTimeout(TIMEOUT);
				int tries = 0;
				boolean recevivedResponse = false;
				while (!recevivedResponse && tries < MAXTRIES) {
					dataSocket.send(sendPacket);
					try {
						dataSocket.receive(receivePacket);
						if (!receivePacket.getAddress().equals(loc)) {
							throw new IOException(
									"Received packet from an umknown source");
						}
						recevivedResponse = true;
					} catch (InterruptedIOException e) {
						tries += 1;
						taLog.append("Time out,"
								+ (MAXTRIES - tries)
								+ " more tries...waitting for connecting to the server"
								+ "\n");
					}
				}
				if (recevivedResponse) {
					taLog.append("The Client Send number: " + number
							+ " to server IP:"
							+ InetAddress.getByName(hostName) + "\n");
					taLog.append("The port is: " + portNumber + " via "
							+ protocol + "\n");

					sengMSG = new String(receivePacket.getData());
					taLog.append(sengMSG);
					if (!sengMSG.isEmpty()) {
						taLog.append("\nAt the time: " +new Date().toLocaleString()+ "\n"+"Success" + "\n");
						taLog.append("****************************\n");
					}
				} else {
					taLog.append("NO Response---give up!!" + "\n");
					taLog.append("****************************\n");
				}
				dataSocket.close();
			}
		} catch (IOException ex) {		
			taLog.append("The client can not connect to the server!" + "\n");
		} finally {
			try {
				if (outToServer != null)
					outToServer.close();
			} catch (IOException ex) {
				taLog.append(ex.getMessage()+"\n****The client can not connect to the server! 4" + "\n");
			}
		}
	}
	public static void fail(String str) {
		JOptionPane.showMessageDialog(null, str, "Error",
				JOptionPane.ERROR_MESSAGE);
	}
}

