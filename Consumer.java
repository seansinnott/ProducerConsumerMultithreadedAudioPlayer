import javax.sound.sampled.*;
import java.awt.*;
import java.io.*;


class Consumer implements Runnable {
	BoundedBuffer bb;
	AudioInputStream s;
	AudioFormat f;
	SourceDataLine l;
	private TextArea textarea;

	public Consumer (BoundedBuffer bb, AudioInputStream s, AudioFormat f, SourceDataLine l) {
		this.bb = bb;
		this.s = s;
		this.f = f;
		this.l = l;
	}

	public void run() {
		try {				
				l.open(f);
				l.start();
			
			int oneSecond = (int) (f.getChannels() * f.getSampleRate() * f.getSampleSizeInBits() / 8);
			byte [] chunk = bb.removeChunk();
			while(chunk.length != 0 && !(Player.getStopValue())){
				while(Player.getPauseValue()){
					try{
						wait();
					}
					catch(Exception e) {}
				}
				
				l.write(chunk, 0, oneSecond);
				chunk = bb.removeChunk();
			}
			l.drain();
			l.stop();
			s.close();

			Player.writeToTextArea("Consumer says: goodbye!");
			
		}	
		catch (IOException e) {
			e.printStackTrace();
    			System.exit(1);
		}
		catch (InterruptedException e){
			e.printStackTrace();
    			System.exit(1);
		} 	catch (LineUnavailableException d) {
    			d.printStackTrace();
    			System.exit(1);
		}	
		
	}
}
