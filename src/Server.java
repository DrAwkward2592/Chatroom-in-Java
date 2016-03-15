import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public Server(){
		super("Adityan's instant messenger!");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(e.getActionCommand());
				userText.setText("");
				
			}
		});
		add(userText,BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(500, 300);
		setVisible(true);
	}
	
	public void startRunning(){
		try {
			server = new ServerSocket(6789, 100);
			while(true){
				try {
					waitForConnection();
					setupStreams();
					whileChatting();
				} catch (EOFException e) {
					showMessage("\n Server ended the connection!");
				}finally {
					closeConnection();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	private void closeConnection() {
		showMessage("\n Closing connections \n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void whileChatting() throws IOException {
		String message = "You are now connected";
		sendMessage(message);
		ableToType(true);
		
		do{
			
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			} catch (ClassNotFoundException e) {
				showMessage("Class not found exception");	
			}
		}while(!message.equals("CLIENT - END"));
		
	}



	//send message to client
	private void sendMessage(String message) {
		try {
			output.writeObject("SEVER - " + message);
			output.flush();
			showMessage("\n SERVER - " + message);
		} catch (IOException e) {
			chatWindow.append("I cannot send that message!");
		}
		
	}

	private void showMessage(final String text) {
		SwingUtilities.invokeLater(
				new Runnable() {
					
					@Override
					public void run() {
						chatWindow.append(text);
					}
				}

				);
		
		
	}

	//Wait for connection then display connection information
	private void waitForConnection() {
		showMessage("\n Waiting for someone to connect...");
		try {
			connection = server.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showMessage("\n Now connected to " + connection.getInetAddress().getHostAddress());
	}
	
	//get stream to send and receive data
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n The streams are now setup!");
	}
	
	private void ableToType(final boolean b) {
		SwingUtilities.invokeLater(
				new Runnable() {
					
					@Override
					public void run() {
						userText.setEditable(b);
					}
				}

				);
	}
}
