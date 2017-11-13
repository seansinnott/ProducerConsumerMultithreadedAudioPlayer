import javax.sound.sampled.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

class Player extends Panel implements Runnable {
    private static final long serialVersionUID = 1L;
    private TextField textfield;
    private static TextArea textarea;
    private Font font;
    private String filename;	
	private static boolean isHalted = false;
	private static boolean isPaused = false;
	private AudioInputStream s;
	private AudioFormat format;
	private DataLine.Info info;
	private SourceDataLine line;
	public FloatControl volume;
	

	public static void writeToTextArea(String s) {
		textarea.append(s + "\n");
	}
	
	public static boolean getStopValue() {
		return isHalted;
	}

	public static boolean getPauseValue() {
		return isPaused;
	}

	public Player(String filename){
	
		font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
		textfield = new TextField();
		textarea = new TextArea();
		textarea.setFont(font);
		textfield.setFont(font);
		setLayout(new BorderLayout());
		add(BorderLayout.SOUTH, textfield);
		add(BorderLayout.CENTER, textarea);
						
		
		textfield.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = textfield.getText();
				if (s.equals("x")) {				
					isHalted = true;
					textarea.append("Command received: halt playback" + "\n");
					textfield.setText("");
				}
				else if (s.equals("r")) {
					isPaused = false;
					textarea.append("Command received: resume playback" + "\n");
					textfield.setText("");
				}
				else if (s.equals("p")) {
					isPaused = true;
					textarea.append("Command received: pause playback" + "\n");
					textfield.setText("");
				} else if(s.equals("q")){
					if(line.isControlSupported(FloatControl.Type.MASTER_GAIN)){					
						volume = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
						volume.setValue(volume.getValue() + 10.0F);
						textarea.append("Command received: increase volume" + "\n");
					} else{
						textarea.append("Sorry this command is not available on this machine" + "\n");
					}					
					textfield.setText("");
						
				}else if(s.equals("a")){
					if(line.isControlSupported(FloatControl.Type.MASTER_GAIN)){
						volume = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
						volume.setValue(volume.getValue() - 10.0F);
						textarea.append("Command received: decrease volume" + "\n");
					} else{
						textarea.append("Sorry this command is not available on this machine" + "\n");
					} 
				}
				else if(s.equals("m")){
					if(line.isControlSupported(FloatControl.Type.MASTER_GAIN)){
						volume = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
						volume.setValue(-80.0F);
						textarea.append("Command received: mute volume" + "\n");
					} else{
						textarea.append("Sorry this command is not available on this machine" + "\n");
					} 
				}
				else if(s.equals("u")){
					if(line.isControlSupported(FloatControl.Type.MASTER_GAIN)){
						volume = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
						volume.setValue(0.0F);
						textarea.append("Command received: unmute volume" + "\n");
					} else{
						textarea.append("Sorry this command is not available on this machine" + "\n");
					} 
				}
					textfield.setText("");
				
				
			
			}
    	});

		this.filename = filename;
		new Thread(this).start();
    }

    public void run() {

		try {
				
s = AudioSystem.getAudioInputStream(new File(filename));
				format = s.getFormat();
    
				info = new DataLine.Info(SourceDataLine.class, format);
				if (!AudioSystem.isLineSupported(info)) {
					throw new UnsupportedAudioFileException();
				}

				line = (SourceDataLine) AudioSystem.getLine(info);
			
				BoundedBuffer bb = new BoundedBuffer();
				Thread prodThread = new Thread(new Producer(bb, s, format));
				Thread consThread = new Thread(new Consumer(bb, s, format, line));
		
				prodThread.start();
				consThread.start();

				prodThread.join();
				consThread.join();
			
				textarea.append("Main says: Playback complete" + "\n");

		} catch (InterruptedException e){
			e.printStackTrace();
			System.exit(1);
		} catch (UnsupportedAudioFileException e ) {
			System.out.println("Player initialisation failed");
			e.printStackTrace();
			System.exit(1);
		} catch (LineUnavailableException e) {
			System.out.println("Player initialisation failed");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Player initialisation failed");
			e.printStackTrace();
			System.exit(1);
		}
    }
}

public class StudentPlayerApplet extends Applet
{
	private static final long serialVersionUID = 1L;
	public void init() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, new Player(getParameter("file")));
	}
}
