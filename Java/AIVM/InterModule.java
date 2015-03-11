package sri.vn.aivm;

import java.util.HashMap;

import sri.wn.ai.Neuron;

public class InterModule implements KernelModule {

	private HashMap<Integer,Neuron> map;
	
	
	public InterModule(){
		map = new HashMap<Integer,Neuron>();
	}
	
	@Override
	public int address() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] read(int handle, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int write(int handle, byte[] data) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int open(byte[] address) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int close(int handle) {
		// TODO Auto-generated method stub
		return 0;
	}

}
