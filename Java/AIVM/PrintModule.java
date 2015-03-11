package sri.vn.aivm;

import java.util.Scanner;

public class PrintModule implements KernelModule {

	public int address(){
		return 1;
	}
	
	@Override
	public byte[] read(int handle, int count) {
		
		Scanner in = new Scanner(System.in);
		String data = in.nextLine();
		in.close();
		
		return data.getBytes();
	}

	@Override
	public int write(int handle, byte[] data) {
		
		System.out.println(new String(data));
		return 0;
	}

	@Override
	public int open(byte[] address) {
		return 0;
	}

	@Override
	public int close(int handle) {
		return 0;
	}

	@Override
	public void onLoad() {

	}

	
	
}
