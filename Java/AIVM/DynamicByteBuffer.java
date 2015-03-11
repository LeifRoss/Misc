package sri.vn.aivm;

import java.nio.ByteBuffer;

public class DynamicByteBuffer {

	
	private ByteBuffer buffer;
	
	public DynamicByteBuffer(int capacity){
		buffer = ByteBuffer.allocate(capacity);
	}
	
	public DynamicByteBuffer(){
		this(16);
	}
	
	public void ensureRemainingCapacity(int minRemaining){
		
		if(buffer.remaining() < minRemaining){
			
			ByteBuffer next = ByteBuffer.allocate(buffer.capacity()+minRemaining);
			buffer.flip();
			next.put(buffer.array(), 0, buffer.position());
			buffer.clear();
			buffer = next;
		}
	}
	
	
	private void ensureCapacity(int minRemaining){
		
		if(buffer.remaining() < minRemaining){
			
			ByteBuffer next = ByteBuffer.allocate((buffer.capacity()+minRemaining)*2);
			buffer.flip();
			next.put(buffer.array(), 0, buffer.remaining());
			buffer.clear();
			buffer = next;
		}
	}
	
	
	public void compactCapacity(){
			
		if(buffer.remaining() > buffer.capacity()/2){
			
			buffer.compact();
			ByteBuffer next = ByteBuffer.allocate(buffer.capacity()/2);
			buffer.flip();
			next.put(buffer.array(), 0, buffer.position());
			buffer.clear();
			buffer = next;
		}
	}
	
	
	public ByteBuffer getBuffer(){
		return buffer;
	}
	
	
	public void clear(){
		buffer.clear();
	}
	
	public byte[] array(){
		return buffer.array();
	}
	
	public byte get(){
		return buffer.get();
	}
	
	public void put(byte in){
		ensureCapacity(1);
		buffer.put(in);
	}
	
	public void get(byte[] in){
		buffer.get(in);
	}
	
	public void put(byte[] in){
		ensureCapacity(in.length);
		buffer.put(in);
	}
	
	public void put(byte[] in, int offset, int length){
		ensureCapacity(in.length);
		buffer.put(in,offset,length);
	}
	
	public float getFloat(){
		return buffer.getFloat();
	}
	
	public void putFloat(float in){
		ensureCapacity(4);
		buffer.putFloat(in);
	}
	
	public double getDouble(){
		return buffer.getDouble();
	}
	
	public void putDouble(double in){
		ensureCapacity(8);
		buffer.putDouble(in);
	}
	
	public short getShort(){
		return buffer.getShort();
	}
	
	public void putShort(short in){
		ensureCapacity(2);
		buffer.putShort(in);
	}
	
	public int getInt(){
		return buffer.getInt();
	}
	
	public void putInt(int in){
		ensureCapacity(4);
		buffer.putInt(in);
	}
	
	
	public void putInt(int index, int in){
		ensureCapacity(4);
		buffer.putInt(index, in);
	}
	
	public long getLong(){
		return buffer.getLong();
	}
	
	public void putLong(long in){
		ensureCapacity(8);
		buffer.putLong(in);
	}
	
	public char getChar(){
		return buffer.getChar();
	}
	
	public void putChar(char c){
		ensureCapacity(2);
		buffer.putChar(c);
	}
	
	public String getString(){
		return BufferUtility.readString(buffer);
	}
	
	public void putString(String in){
		ensureCapacity(4 + in.length()*2);
		BufferUtility.writeString(in, buffer);
	}
	
	public byte[] getByteArray(){
		
		int length = buffer.getInt();
		byte[] data = new byte[length];
		buffer.get(data);
		
		return data;
	}
	
	public void flip(){
		buffer.flip();
	}
	
	public void reset(){
		buffer.mark();
		buffer.reset();
	}
	
	public int position(){
		return buffer.position();
	}
	
	/**
	 * Put a byte array in the buffer
	 * @param in
	 */
	public void putByteArray(byte[] in){
		ensureCapacity(in.length + 4);
		buffer.putInt(in.length);
		buffer.put(in);
	}
	
	
	
}
