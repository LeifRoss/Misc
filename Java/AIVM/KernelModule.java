package sri.vn.aivm;

public interface KernelModule {

	public int address();
	
	public void onLoad();
	
	public byte[] read(int handle, int count);
	
	public int write(int handle, byte[] data);
	
	public int open(byte[] address);
	
	public int close(int handle);
	
	
}
