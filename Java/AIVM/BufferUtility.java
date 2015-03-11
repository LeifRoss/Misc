package sri.vn.aivm;

import java.nio.ByteBuffer;

public class BufferUtility {

	/**
	 * Write a string to the buffer
	 * @param s
	 * @param buffer
	 */
	public static void writeString(String s, ByteBuffer buffer){
		
		if(s == null){
			s = "";
		}
		
		int length = s.length();
		buffer.putInt(length);
		
		for(int i = 0; i < length; i++){
			buffer.putChar(s.charAt(i));
		}
	}
	
	/**
	 * Read a string from the buffer
	 * @param buffer
	 * @return
	 */
	public static String readString(ByteBuffer buffer){
		
		int length = buffer.getInt();
		
		StringBuilder str = new StringBuilder(length);
		
		for(int i = 0; i < length; i++){
			str.append(buffer.getChar());
		}
		
		return str.toString();
	}
	
	/**
	 * Return the size of a string inside a buffer
	 * @param s
	 * @return
	 */
	public static int getLength(String s){
		
		if(s == null){
			return Integer.SIZE/8;
		}
		
		return (Integer.SIZE/8) + s.length()*(Character.SIZE/8);
	}
	
	
	
	public static ByteBuffer ensureRemainingCapacity(ByteBuffer buffer, int minRemaining){
		
		if(buffer.remaining() < minRemaining){
			
			ByteBuffer next = ByteBuffer.allocate(buffer.capacity()+minRemaining);
			next.put(buffer.array());
			buffer.clear();
			buffer = next;
		}
		
		return buffer;
	}
	
	
	public static void fill(byte[] a, byte[] b, int offsetA, int offsetB, int length){
		for(int i = 0; i < length; i++){
			b[offsetB+i] = a[offsetB+i];
		}
	}
	
}
