package sri.vn.aivm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import sri.wn.util.Utility;

public class FileModule implements KernelModule {

	private HashMap<Integer,File> handles;
	private int counter;

	public FileModule(){
		handles = new HashMap<Integer,File>();
		counter = 0;
	}


	@Override
	public int address() {
		return 3;
	}

	@Override
	public void onLoad() {

	}

	@Override
	public byte[] read(int handle, int count) {

		if(!handles.containsKey(handle)){
			return null;
		}

		File f = handles.get(handle);

		if(!f.exists()){
			return null;
		}
		
		FileInputStream stream = null;
		byte[] data = new byte[(int)f.length()];

		try {
			
			stream = new FileInputStream(f);
			stream.read(data);
			
		} catch ( IOException e) {
			e.printStackTrace();
		}finally{
			if(stream != null){
				try {
					stream.close();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		}

		return data;
	}

	@Override
	public int write(int handle, byte[] data) {

		if(!handles.containsKey(handle)){
			return -1;
		}

		File f = handles.get(handle);
		FileOutputStream stream = null;

		if(!f.exists()){
			f.mkdirs();
		}

		try {

			stream = new FileOutputStream(f);
			stream.write(data);

		} catch (IOException e) {
			e.printStackTrace();
		}finally{

			if(stream != null){
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return 0;
	}

	@Override
	public int open(byte[] address) {
		counter++;

		String path = new String(address);
		File f = new File(Utility.getAssetsLocation(),path);
		handles.put(counter, f);

		return counter;
	}

	@Override
	public int close(int handle) {

		if(handles.containsKey(handle)){
			handles.remove(handle);	
			return 0;
		}

		return -1;
	}

}
