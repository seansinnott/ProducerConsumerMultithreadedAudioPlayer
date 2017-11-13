import javax.sound.sampled.*;
import java.awt.*;
import java.io.*;

class Producer implements Runnable {
	public BoundedBuffer bb;
	AudioInputStream s;
	AudioFormat f;
	private TextArea textarea;

	public Producer (BoundedBuffer bb, AudioInputStream s, AudioFormat f) {
		this.bb = bb;
		this.s = s;
		this.f = f;
	}

	public void run() {
		try {			
			int oneSecond = (int) (f.getChannels() * f.getSampleRate() * f.getSampleSizeInBits() / 8);
			byte[] audioChunk = new byte[oneSecond];
			int bytesRead = s.read(audioChunk);
			while(bytesRead > 0 && !(Player.getStopValue())){
				while(Player.getPauseValue()){
					try{
						wait();
					}
					catch(Exception e) {}
				}
				bb.insertChunk(audioChunk, bytesRead);
				bytesRead = s.read(audioChunk);
				if (bytesRead == -1) {
					bb.insertChunk(audioChunk, bytesRead);
				}				
			}
			Player.writeToTextArea("Producer says: goodbye!");
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e){
			e.printStackTrace();
			System.exit(1);
		}
	}
}
