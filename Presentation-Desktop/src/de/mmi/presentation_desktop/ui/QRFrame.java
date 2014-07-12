package de.mmi.presentation_desktop.ui;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9153059369012127544L;
	
	/**
	 * Main Panel. Is used with {@link java.awt.BorderLayout BorderLayout}
	 */
	private JPanel contentPane;
	private JComboBox<String> interfaceChooser;
	private DefaultComboBoxModel<String> interfaceModel;
	
	/**
	 * If true, {@link #LOOPBACK} will be considered a valid IPv4 address.
	 */
	private final boolean loopbackValid;
	
	private List<NetworkInterface> netList;
	private JTextPane ipInfoLabel;
	private ImagePanel qrPanel;

	private static final String NOT_VALID = "No valid ip found";
	/**
	 * The Loopback address: 127.0.0.1
	 */
	public static final String LOOPBACK = "127.0.0.1";
	
	/**
	 * If an IP matches this pattern, it is considered as valid (except for loopback-address)
	 */
	private final static Pattern IP_PATTERN = Pattern.compile("/(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");
	private final static int QR_CODE_SIZE = 500;
	
	/**
	 * Creates a frame displaying the IPv4-Addresses of the available interfaces in a QR-Code
	 * By default, loopback is not seen as valid IP-Address. See {@link #QRFrame(boolean)} if loopback should be included.
	 */
	public QRFrame() {
		this(false);
	}
	
	/**
	 * Creates a frame displaying the IPv4-Addresses of the available interfaces in a QR-Code
	 * @param loopbackValid If false, the loopback address {@link #LOOPBACK} will not be displayed as a valid ipv4-address.
	 */
	public QRFrame(boolean loopbackValid){
		super("Choose IP");
		
		this.loopbackValid = loopbackValid;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		qrPanel = new ImagePanel();
		contentPane.add(qrPanel, BorderLayout.CENTER);
		
		interfaceChooser = new JComboBox<String>();
		interfaceModel = new DefaultComboBoxModel<>();
		interfaceChooser.setModel(interfaceModel);
		interfaceChooser.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
					choiceChanged((String)e.getItem());
			}
		});
		
		contentPane.add(interfaceChooser, BorderLayout.NORTH);
		
		ipInfoLabel = new JTextPane();
		contentPane.add(ipInfoLabel, BorderLayout.SOUTH);
	}
	
	/**
	 * This method prepares the frame. It reads all valid interfaces and puts them in a list for user to choose to which one to use.
	 */
	public void init(){		
		try {
			netList = Collections.list(NetworkInterface.getNetworkInterfaces());
			
			for(NetworkInterface ni : netList){
				if(isInterfaceValid(ni))
					interfaceModel.addElement(ni.getName() + " - " + getIpv4Address(ni));
			}
			this.repaint();
			
		} catch (SocketException e) {
			e.printStackTrace();
		}		
		
		if(netList == null || netList.isEmpty()){
			ipInfoLabel.setText("No network interfaces found");
		}
	}
	
	/**
	 * Creates a new QR-Code-Image for the given item.
	 * And updates the text with detailed info.
	 * @param item The Item chosen (from JComboBox)
	 */
	private void choiceChanged(String item){
		item = item.split("-")[0].trim();
		NetworkInterface ni = null;
		for(NetworkInterface n : netList){
			if(n.getName().equals(item)){
				ni = n;
				break;
			}
		}
		
		if(ni == null){
			ipInfoLabel.setText("Sorry, unknown interface");
		}else{
			ipInfoLabel.setText(getInterfaceInformation(ni));
			
			try {
				BitMatrix bm = new QRCodeWriter().encode(getIpv4Address(ni), BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
				BufferedImage bi = MatrixToImageWriter.toBufferedImage(bm);
				qrPanel.setImage(bi);
				pack();
				repaint();
			} catch (WriterException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * Checks if the given interface is considered a valid IPv4 address
	 * @param netint The interface
	 * @return false if {@link #getIpv4Address(NetworkInterface)} returns {@link #NOT_VALID}. true otherwize.
	 */
	private boolean isInterfaceValid(NetworkInterface netint){
		return !getIpv4Address(netint).equals(NOT_VALID);
	}
	
	/**
	 * Puts the Interface Information in a multi-lined human readable String.
	 * @param netint The network interface to be displayed
	 * @return The readable multi-lined String holding Information about the Interface
	 */
	private String getInterfaceInformation(NetworkInterface netint) {
		String str = "";
		str += "Display name: " + netint.getDisplayName() + "\n";
		str += "Name: " + netint.getName() + "\n";
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
        	str += "InetAddress: " + inetAddress + "\n";
        }
        return str.substring(0, str.length()-1);
     }
	
	/**
	 * Extracts the IPV4 address of the given interface. <br>The Loopback address ({@link #LOOPBACK}) is only considered as valid if Constructor {@link #QRFrame(boolean) QRFrame(true)} was used.
	 * @param netInt The interface
	 * @return The IP as a String or {@link #NOT_VALID} if no valid IP was found.
	 */
	private String getIpv4Address(NetworkInterface netInt){
		String retVal = NOT_VALID;
		
		Matcher m;		
		Enumeration<InetAddress> inetAddresses = netInt.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
        	m = IP_PATTERN.matcher(inetAddress.toString());
        	if(m.matches() && (loopbackValid || !inetAddress.toString().contains(LOOPBACK))){
        		//System.out.println("found match with " + inetAddress.toString().substring(1));
        		return inetAddress.toString().substring(1);
        	}
        }
		
		return retVal;
	}

}
