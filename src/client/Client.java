package client;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements ActionListener {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static final String Defaut_HOST = "127.0.0.1";
  private static final String Defaut_PORT = "8000";
  private static final String Defaut_NUM = "32";
  private static final String Defaut_COM ="-x 32 -t tcp -s localhost -p 8000";
  

  private JButton btn_Send, btn_Enter;
  private JLabel commandLab, portLab, hostLab, textLab, clientLog, helpLab,protocolLab,stsLab;
  private JTextField tfCom, tfPort, tfHost, tfTextnumber,tfSts;

  private JTextArea taLog = new JTextArea();
  private JScrollPane northPane = new JScrollPane(taLog);
  private JRadioButton tcp_rd = new JRadioButton("TCP");
  private JRadioButton udp_rd = new JRadioButton("UDP");

  ButtonGroup group = new ButtonGroup();
  private JPanel jPanel;
  private int Cknumber;
  private int CkportNumber;
  private String Ckprotocol;
  private String Ckaddress;

  public Client() {
    setTitle("Client");
    setSize(550, 490);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(null);
    setResizable(false);

    portLab = new JLabel("Port:");
    hostLab = new JLabel("Hostname/Ip:");
    textLab = new JLabel("Number: ");
    clientLog = new JLabel("[Client log]");
    helpLab = new JLabel("Tips: -x 32 -t tcp -s localhost -p 8000");
    commandLab = new JLabel("Command line: ");
    protocolLab = new JLabel("Protocol: ");
    stsLab = new JLabel("Status: ");
    
    tfCom = new JTextField(Defaut_COM);
    tfHost = new JTextField(Defaut_HOST);
    tfPort = new JTextField(Defaut_PORT);
    tfTextnumber = new JTextField(Defaut_NUM);
    tfSts = new JTextField(20);
    tfSts.setEditable(false);
    btn_Enter = new JButton("Enter");
    btn_Send = new JButton("Send");

    clientLog.setBounds(20, 0, 100, 30);
    getContentPane().add(clientLog);
    northPane.setBounds(20, 30, 500, 240);
    northPane.setEnabled(false);
    getContentPane().add(northPane);
    helpLab.setBounds(30, 270, 300, 30);
    getContentPane().add(helpLab);
    commandLab.setBounds(30, 300, 150, 30);
    getContentPane().add(commandLab);
    tfCom.setBounds(130, 300, 290, 30);
    getContentPane().add(tfCom);
    btn_Enter.setBounds(430, 300, 90, 30);
    portLab.setBounds(90, 340, 50, 30);
    tfPort.setBounds(130, 340, 60, 30);
    getContentPane().add(portLab);
    getContentPane().add(tfPort);
    hostLab.setBounds(200, 340, 100, 30);
    tfHost.setBounds(290, 340, 130, 30);
    getContentPane().add(hostLab);
    getContentPane().add(tfHost);

    jPanel = new JPanel(new GridLayout(1, 2));
    jPanel.add(tcp_rd);
    jPanel.add(udp_rd);
    group.add(tcp_rd);
    group.add(udp_rd);
    tcp_rd.setSelected(true);

    protocolLab.setBounds(60, 380, 70, 30);
    stsLab.setBounds(250,380,70,30);
    jPanel.setBounds(120, 380, 100, 30);
    getContentPane().add(protocolLab);
    getContentPane().add(stsLab);
    getContentPane().add(jPanel);
    tfSts.setBounds(300,380,100,30);
    textLab.setBounds(40, 410, 100, 30);
    tfTextnumber.setBounds(130, 410, 290, 30);
    getContentPane().add(tfSts);
    getContentPane().add(textLab);
    getContentPane().add(tfTextnumber);
    btn_Send.setBounds(430, 410, 90, 30);
    getContentPane().add(btn_Enter);
    getContentPane().add(btn_Send);

    tfCom.addActionListener(this);
    tfPort.addActionListener(this);
    tfTextnumber.addActionListener(this);
    tfHost.addActionListener(this);
    btn_Send.addActionListener(this);
    btn_Enter.addActionListener(this);
    setVisible(true);
  }

  public boolean checkInput(String text, String portText, String protocol,
      String address) throws ConnectException {
    taLog.setEditable(false);
    taLog.setForeground(Color.blue);
    try {
      if (protocol.isEmpty()) {
        taLog.append("The protocol is missing!" + "\n");
        return false;
      } else if (text.isEmpty()) {
        taLog.append("The text message is missing!" + "\n");
        return false;
      } else if (portText.isEmpty()) {
        taLog.append("The port number is missing!" + "\n");
        return false;
      } else if (address.isEmpty()) {
        taLog.append("The host name/IP is missing!" + "\n");
        return false;
      } else {
        Cknumber = Integer.parseInt(text);
        CkportNumber = Integer.parseInt(portText);
        InetAddress[] iPaddress = InetAddress.getAllByName(address);
        if (CkportNumber <= 1024 || CkportNumber > 65535) {
          fail("The port number is out of range, 1024<port number <65535"
              + "\n");
          return false;
        } else if (Cknumber < 0 || Cknumber > Integer.MAX_VALUE) {
          fail("The number is out of range, the range must be 0<number<Integer.MAX_VALUE,"
              + "\n");
          return false;
        } else if ((protocol.equals("tcp") || protocol.equals("udp")) != true) {
          fail("The protocol only provides TCP or UDP" + "\n");
          return false;
        } else if (iPaddress == null) {
          fail("The Ip address is empty!!" + "\n");
          return false;
        }
      }
    } catch (UnknownHostException e) {
      taLog.append("Unkonwn Hostname " + "\n");
      fail("The hostname is illegal!!( eg: loaclhost) " + "\n");
    } catch (NumberFormatException eu) {
      fail("Please check the number of textmessage or port number, try again!!!"
          + "\n");
    } catch (Exception e) {
      taLog.append("The client is not connect to the server!" + "\n");

    }
    Ckprotocol = protocol;
    Ckaddress = address;
    return true;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String number = null;
    String portNumber = null;
    String protocol = null;
    String IP = null;
    try {
      if (e.getSource() == btn_Enter) {
        StringTokenizer tokenizedLine = new StringTokenizer(
            tfCom.getText(), "- ");

        while (tokenizedLine.hasMoreTokens()) {
          String name = tokenizedLine.nextToken();
          String val = tokenizedLine.nextToken();
          switch (name) {
          case "x":
            number = val;
            taLog.append("The number is : " + number + "\n");
            break;
          case "t":
            protocol = val;
            taLog.append("The protocol is :" + protocol + "\n");
            break;
          case "s":
            IP = val;
            taLog.append("The hostname or IP is : " + IP + "\n");
            break;
          case "p":
            portNumber = val;
            taLog.append("The port number is :" + portNumber + "\n");
            break;
          }
        }
        if (checkInput(number, portNumber, protocol, IP) == true) {
          SocketClientThread Thread = new SocketClientThread(
              Ckaddress, CkportNumber, Cknumber, Ckprotocol,
              taLog);
          Thread.start();
      	  tfSts.setText("Client Startting.....");
        }
      }

      if (e.getSource() == btn_Send) {
        portNumber = tfPort.getText();
        number = tfTextnumber.getText();
        IP = tfHost.getText();
        if (tcp_rd.isSelected())
          protocol = "tcp";
        if (udp_rd.isSelected())
          protocol = "udp";
        if (checkInput(number, portNumber, protocol, IP) == true) {
          SocketClientThread Thread = new SocketClientThread(
              Ckaddress, CkportNumber, Cknumber, Ckprotocol,
              taLog);
          Thread.start();
          tfSts.setText("Client Startting.....");
        }
      }
      Cknumber = 0;
      CkportNumber = 0;
      Ckaddress = null;
      Ckprotocol = null;
    } catch (Exception e1) {      
      taLog.append("The client can not connect to the server!" + "\n");
    }

  }

  public static void fail(String str) {
    JOptionPane.showMessageDialog(null, str, "Error",
        JOptionPane.ERROR_MESSAGE);
  }

  /**
   * check ip address
   * 
   * @param address
   * @return
   */
  public static boolean isIpAddress(String address) {

    if (address == "localhost") {
      return true;
    } else {
      String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
          + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
          + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
          + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
      Pattern p = Pattern.compile(regex);
      Matcher m = p.matcher(address);
      return m.matches();
    }
  }
}
