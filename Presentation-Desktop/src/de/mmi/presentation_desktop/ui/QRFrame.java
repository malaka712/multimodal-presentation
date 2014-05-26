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
	private JPanel contentPane;
	private JComboBox<String> interfaceChooser;
	private DefaultComboBoxModel<String> interfaceModel;
	
	private List<NetworkInterface> netList;
	private JTextPane ipInfoLabel;
	private ImagePanel qrPanel;
	
	private final static Pattern IP_PATTERN = Pattern.compile("/(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");
	private final static int QR_CODE_SIZE = 500;
	
	/**
	 * Create the frame.
	 */
	public QRFrame() {
		super("Choose IP");
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
	
	public void init(){		
		try {
			netList = Collections.list(NetworkInterface.getNetworkInterfaces());
			
			for(NetworkInterface ni : netList){
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
	
	private String getIpv4Address(NetworkInterface netInt){
		String retVal = "No valid ip found";
		
		Matcher m;
		
		Enumeration<InetAddress> inetAddresses = netInt.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
        	m = IP_PATTERN.matcher(inetAddress.toString());
        	if(m.matches()){
        		//System.out.println("found match with " + inetAddress.toString().substring(1));
        		return inetAddress.toString().substring(1);
        	}
        }
		
		return retVal;
	}

}
