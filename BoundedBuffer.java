public class BoundedBuffer{

	
	public int nextIn, nextOut, size, occupied, ins, outs;
	private boolean dataAvailable, roomAvailable;

	private byte [][] buffArray;
	
	public BoundedBuffer(){
		nextIn = 0;
		nextOut = 0;
		size = 10;
		occupied = 0;
		outs = 0;
		dataAvailable = false;
		roomAvailable = true;
		buffArray = new byte[size][];
	}



	public synchronized void insertChunk(byte [] audio, int audioChunk) throws InterruptedException{		
		while (!roomAvailable) {
			wait();
		}
		if(audioChunk == -1){
			byte [] finishChunk = {};
			occupied--;
			buffArray[nextIn] = finishChunk;
			
		}
		else {

			byte [] size = new byte [audioChunk];
			for(int i = 0; i < audioChunk; i++){
				size[i] = audio[i];
			}
			buffArray[nextIn]= size;
			if(nextIn == 9){
				nextIn = 0;
			}
			else{
				nextIn++;
			}
			occupied++;
			if(occupied == 9){
				roomAvailable = false;
			}
			dataAvailable = true;			
			ins++;

			notifyAll();
		}
		
	}
	public synchronized byte [] removeChunk() throws InterruptedException{
		byte [] nextChunk = new byte[1];
		if(!dataAvailable && occupied == 0){
			return buffArray[nextIn];
		}

		while (!dataAvailable){
			wait();
		}

		nextChunk = buffArray[nextOut];
		if(nextOut == 9){
			nextOut = 0;
		}
		else{
			nextOut++;
		}
		
		occupied--;
		if(occupied == 0){
			dataAvailable = false;
		}
		roomAvailable = true;
		outs++;
		notifyAll();
		return nextChunk;
	}
}
