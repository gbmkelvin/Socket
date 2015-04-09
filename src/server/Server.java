package server;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Server extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JPanel panelMain, pnlInfo, pnlLeft;
	JLabel lblSts, lblPort, lblLog, lblPro;
	JTextField tfSts, tfPort, tfPro;
	JButton btnStart = new JButton("OPEN");
	JButton btnStop = new JButton("STOP");
	TextArea taLog;

	ButtonGroup group = new ButtonGroup();
	private JRadioButton tcp_rd = new JRadioButton("TCP");
	private JRadioButton udp_rd = new JRadioButton("UDP");

	boolean bStart = false;
	public boolean flag_tcp = false;
	public boolean flag_udp = false;
	public String number;
	private int port;
	private mThread sThread;
	private ServerSocket sSocket;
	private DatagramSocket dSocket;

	public Server() {
		super("Server");
		setSize(550, 490);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		panelMain = new JPanel();
		panelMain.setLayout(null);
		panelMain.setBackground(new Color(234, 66, 123));
		pnlInfo = new JPanel(new GridLayout(7, 1));
		pnlInfo.setBackground(new Color(66, 99, 222));
		pnlInfo.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(""),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		/**
		 * the left panel label info
		 */
		lblSts = new JLabel("Status:");
		lblSts.setForeground(Color.YELLOW);
		tfSts = new JTextField(10);
		tfSts.setEditable(false);

		lblPort = new JLabel("Port:");
		lblPort.setForeground(Color.YELLOW);
		tfPort = new JTextField("8000", 10);

		lblPro = new JLabel("Protocol:");
		lblPro.setForeground(Color.YELLOW);
		tfPro = new JTextField("8000", 10);

		btnStart = new JButton("Start Server");
		btnStop = new JButton("Stop Server");
		btnStop.setEnabled(false);

		/**
		 * the Server log panel
		 */
		lblLog = new JLabel("[Server Log]");
		lblLog.setForeground(Color.YELLOW);
		taLog = new TextArea(20, 50);

		// add the component
		pnlInfo.add(lblSts);
		pnlInfo.add(tfSts);
		pnlInfo.add(lblPort);
		pnlInfo.add(tfPort);
		pnlInfo.add(lblPro);
		pnlInfo.add(tcp_rd);
		pnlInfo.add(udp_rd);
		group.add(tcp_rd);
		group.add(udp_rd);
		tcp_rd.setSelected(true);

		pnlLeft = new JPanel(new GridLayout(2, 1));
		pnlLeft.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(""),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		pnlLeft.add(pnlInfo);

		pnlLeft.setBounds(5, 5, 130, 400);
		lblLog.setBounds(140, 5, 100, 30);
		taLog.setBounds(140, 35, 400, 370);
		btnStart.setBounds(140, 420, 110, 25);
		btnStop.setBounds(270, 420, 110, 25);

		panelMain.add(pnlLeft);
		panelMain.add(lblLog);
		panelMain.add(taLog);
		panelMain.add(btnStart);
		panelMain.add(btnStop);
		this.getContentPane().add(panelMain);
		setVisible(true);

		BtnAction act = new BtnAction();
		btnStart.addActionListener(act);
		btnStop.addActionListener(act);

	}

	// Buttons action listener
	private class BtnAction implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			taLog.setEditable(false);
			taLog.setForeground(Color.blue);
			Object source = ae.getSource();
			if (source.equals(btnStart)) {
				try {
					int port = Integer.valueOf(tfPort.getText());
					if (port <= 1024 || port > 65535) {
						fail("Please input port number (65535>port>1024)");
						return;
					} else {

						if (tcp_rd.isSelected()) {
							sSocket = new ServerSocket(port);
							flag_tcp = true;
							taLog.append("TCP Server start in port : " + port
									+ "\n");
						}
						if (udp_rd.isSelected()) {
							dSocket = new DatagramSocket(port);
							flag_udp = true;
							taLog.append("UDP Server start in port : " + port
									+ "\n");
						}
					}
					sThread = new mThread();
					sThread.start();
					btnStop.setEnabled(true);
					btnStart.setEnabled(false);
					tfPort.setEditable(false);
					tfSts.setText("Server Started");
					serverStartTime();
				} catch (IOException e) {
					fail("Server cannot be started! Because the port is already occupid! ");
				} catch (NumberFormatException e) {
					fail("The port number is not integer!");
				} catch (Exception e) {
					fail("Thread created failed!");
				}
			} else if (source.equals(btnStop)) {
				try {
					if (sSocket != null) {
						sSocket.close();
						flag_tcp = false;
						serverStopTime();
					}
					else if (dSocket != null) {
						dSocket.close();
						flag_udp = false;
						serverStopTime();
					}
					sThread.stop();
					btnStart.setEnabled(true);
					btnStop.setEnabled(false);
					tfPort.setEditable(true);
					tfSts.setText("Server Stoped");
				} catch (IOException e) {
					fail("cannot start!");
				}
			}
		}
	}

	public static void fail(String str) {
		JOptionPane.showMessageDialog(null, str, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	public void serverStartTime() {
		taLog.append("The Server Started Time:" + (new Date()).toLocaleString()
				+ "\n");

	}

	public void serverStopTime() {
		taLog.append("The Server stoped Time:" + (new Date()).toLocaleString()
				+ "\n");
		taLog.append("****************************\n");
	}

	// using the listener to start to listen the thread
	private class mThread extends Thread {
		public void run() {
			try {
				while (flag_tcp) {
					Socket connectionSocket = sSocket.accept();
					BufferedReader inputFromClient = 
							new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
					DataOutputStream outputToClient = 
							new DataOutputStream(connectionSocket.getOutputStream());
					number = inputFromClient.readLine();
					port = connectionSocket.getPort();
					taLog.append("The number is : "+ number +" from client: "+connectionSocket.getInetAddress()+" on port "+port+"\n");
					taLog.append("****************************\n");
					outputToClient.writeBytes("The server recevied the number :"+ number+" at the time : "+ new Date().toLocaleString()+"\n");
				}
				while(flag_udp){
					 byte[] receiveData = new byte[1024];
					 byte[] sendData = new byte[1024];
					 DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
					 dSocket.receive(receivePacket);
					 number = new String(receivePacket.getData());
					 InetAddress IPAddress = receivePacket.getAddress();
					 port = receivePacket.getPort();
					 taLog.append("The number is :" + number);
					 sendData = new String("Server received number :"+number).getBytes();
					 taLog.append("from Client" +IPAddress+" on port "+port+"\nAt the time : "+ new Date().toLocaleString()+ "\n");
					 taLog.append("****************************\n");
					 DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,IPAddress,port);
					 dSocket.send(sendPacket);
				}
			} catch (Exception e) {
				fail("The connection is disconnected!");
			}
		}
	}

}

