package sri.vn.aivm;

import java.nio.ByteBuffer;

public class BinaryParser {

	private ByteBuffer buffer;
	
	public BinaryParser(){
		buffer = ByteBuffer.allocate(8);
		buffer.mark();
	}
	
	
	public int parseInt(byte[] data){
		
		buffer.put(data, data.length-4, 4);
		buffer.flip();
		int res = buffer.getInt();
		buffer.flip();
		
		return res;
	}
	
	public void parseInt(int c, byte[] arr){
		
		buffer.putInt(c);
		buffer.flip();
		buffer.get(arr, arr.length-4, 4);
		buffer.flip();
	}
	
	
	public short parseShort(byte[] data){
		
		buffer.put(data, data.length-2, 2);
		buffer.flip();
		short res = buffer.getShort();
		buffer.flip();
		
		return res;
	}
	
	public void parseShort(short c, byte[] arr){
		
		buffer.putShort(c);
		buffer.flip();
		buffer.get(arr, arr.length-2, 2);
		buffer.flip();
	}
	
	
	public long parseLong(byte[] data){
		
		buffer.put(data, data.length-8, 8);
		buffer.flip();
		long res = buffer.getLong();
		buffer.flip();
		
		return res;
	}
	
	public void parseLong(long c, byte[] arr){
		
		buffer.putLong(c);
		buffer.flip();
		buffer.get(arr, arr.length-8, 8);
		buffer.flip();
	}
	
	public float parseFloat(byte[] data){
		
		buffer.put(data, data.length-4, 4);
		buffer.flip();
		float res = buffer.getFloat();
		buffer.flip();
		
		return res;
	}
	
	public void parseFloat(float c, byte[] arr){
		
		buffer.putFloat(c);
		buffer.flip();
		buffer.get(arr, arr.length-4, 4);
		buffer.flip();
	}
	
	
	public double parseDouble(byte[] data){
		
		buffer.put(data, data.length-8, 8);
		buffer.flip();
		double res = buffer.getDouble();
		buffer.flip();
		
		return res;
	}
	
	public void parseDouble(double c, byte[] arr){
		
		buffer.putDouble(c);
		buffer.flip();
		buffer.get(arr, arr.length-8, 8);
		buffer.flip();
	}
	
	
	public char parseChar(byte[] data){
		
		buffer.put(data, data.length-2, 2);
		buffer.flip();
		char res = buffer.getChar();
		buffer.flip();
		
		return res;
	}
	
	public void parseChar(char c, byte[] arr){
		
		buffer.putChar(c);
		buffer.flip();
		buffer.get(arr, arr.length-2, 2);
		buffer.flip();
	}
	
	
}
