package sri.vn.aivm;

import static sri.vn.aivm.AIVM.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Assembler {

	private String data;
	private DynamicByteBuffer buffer, linkbuffer;
	private HashMap<String,Integer> linkVars;
	private HashMap<String, ArrayList<Integer>> jumpFlags;
	private HashMap<String,Byte> registers;
	private BinaryParser parser;


	public Assembler(){
		// TODO integer replace on assembler calls
		
		linkVars = new HashMap<String,Integer>();
		jumpFlags = new HashMap<String,ArrayList<Integer>>();
		registers = new HashMap<String,Byte>();
		buffer = new DynamicByteBuffer(128);
		linkbuffer = new DynamicByteBuffer(128);
		parser = new BinaryParser();

		registers.put("r0", R0);
		registers.put("r1", R1);
		registers.put("r2", R2);
		registers.put("r3", R3);
		registers.put("r4", R4);
		registers.put("r5", R5);
		registers.put("r6", R6);
		registers.put("lr", LR);

		registers.put("byte", BYTE);
		registers.put("short", SHORT);
		registers.put("int", INT);
		registers.put("long", LONG);
		registers.put("float", FLOAT);
		registers.put("double", DOUBLE);
		registers.put("char", CHAR);
		registers.put("string", STRING);
		
		linkVars.put("sys", SYSTEM);
	}

	public byte[] assemble(String d){

		this.data = d;
		String[] lines = this.data.split("\n");

		for(int i = 0; i < lines.length; i++){

			if(lines[i].isEmpty() || lines[i].startsWith("//")){
				continue;
			}

			parseLine(lines[i].trim());
		}


		int l = buffer.position();
		buffer.flip();
		byte[] program = new byte[l];
		buffer.get(program);

		return program;
	}

	private void parseLine(String line){

		if(line.charAt(0) == '.'){
			// add branch point
			String point = line.substring(1);
			int bpos = buffer.position();
			linkVars.put(point, bpos);
			
			// replace preset positions
			if(jumpFlags.containsKey(point)){
			
				byte[] parsed = new byte[4];
				parser.parseInt(bpos, parsed);
				byte[] arr = buffer.array();
				
				for(Integer io: jumpFlags.get(point)){
				
					int pos = io.intValue();

					for(int i = 0; i < 4; i++){
						arr[pos+i] = parsed[i];
					}
				}
			}
			
			return;
		}

		// function
		String func = line.substring(0, line.indexOf(' '));
		line = line.substring(line.indexOf(' ')+1);

		switch(func){
		
		case "set":
			setFunc(line);
			break;

		default:
			stdFunc(func, line);
		}


		int l = linkbuffer.position();
		linkbuffer.flip();
		byte[] data = new byte[l];
		linkbuffer.get(data);
		linkbuffer.flip();

		buffer.put(data);

	}

	
	private void setFunc(String line){
		
		String[] expl = line.split(" ");
		
		String reg = expl[0];
		String val = expl[1];
		byte r = registers.get(reg).byteValue();
		
		boolean isNum = val.charAt(0) == '#';
		boolean isStr = val.charAt(0) == '"';
		
		if(isNum){
			
			buffer.put(RSETF);
			buffer.put(r);
			parseNumReg(val);
			
		}else if(isStr){
			
			int idx0 = line.indexOf('"')+1;
			int idx1 = line.indexOf('"', idx0);
			
			val = line.substring(idx0, idx1);
			byte[] data = val.getBytes();
			
			buffer.put(PUSH);
			buffer.put(R5);
			
			buffer.put(PUSH);
			buffer.put(R6);
			
			// allocate offset
			buffer.put(RSETF);
			buffer.put(R5);
			buffer.put((byte)0x4);
			buffer.put((byte)0x4);
			buffer.putInt(0x0);
			
			// allocate length
			buffer.put(RSETF);
			buffer.put(R6);
			buffer.put((byte)0x4);
			buffer.put((byte)0x4);
			buffer.putInt(data.length);
			
			
			// malloc
			buffer.put(MALLOC);
			buffer.put(R6);
			buffer.put(r);
			
			// memset
			buffer.put(MEMSET);
			buffer.put(r);
			
			//offset
			buffer.put(R5);
			// length
			buffer.put(R6);
			// data
			buffer.put(data);
			
			buffer.put(POP);
			buffer.put(R6);
			buffer.put(POP);
			buffer.put(R5);
			
		}else{
			
			buffer.put(RSETF);
			buffer.put(r);
			buffer.put((byte)0x4);
			buffer.put((byte)0x4);
			int var = 0;
			int pos = buffer.position();
			
			if(linkVars.containsKey(val)){
				// System address
				var = linkVars.get(val).intValue();
			}else{
				
				if(jumpFlags.containsKey(val)){
					jumpFlags.put(val, new ArrayList<Integer>());
				}
				
				jumpFlags.get(val).add(pos);
			}
			
			buffer.putInt(var);	
		}
	}
	
	private void stdFunc(String func, String line){
		switch(func){

		case "rsetf":
			linkbuffer.put(RSETF);
			break;
		case "rset":
			linkbuffer.put(RSET);
			break;
		case "save":
			linkbuffer.put(SAVE);
			break;
		case "load":
			linkbuffer.put(LOAD);
			break;
		case "pop":
			linkbuffer.put(POP);
			break;
		case "push":
			linkbuffer.put(PUSH);
			break;
		case "popa":
			linkbuffer.put(POPA);
			break;
		case "pusha":
			linkbuffer.put(PUSHA);
			break;
		case "malloc":
			linkbuffer.put(MALLOC);
			break;
		case "memset":
			linkbuffer.put(MEMSET);
			break;
		case "memcpy":
			linkbuffer.put(MEMCPY);
			break;
		case "memcmp":
			linkbuffer.put(MEMCMP);
			break;
		case "sizeof":
			linkbuffer.put(SIZEOF);
			break;
		case "free":
			linkbuffer.put(FREE);
			break;
		case "add":
			linkbuffer.put(ADD);
			break;
		case "mul":
			linkbuffer.put(MUL);
			break;
		case "div":
			linkbuffer.put(DIV);
			break;
		case "eq":
			linkbuffer.put(EQ);
			break;
		case "gt":
			linkbuffer.put(GT);
			break;
		case "lt":
			linkbuffer.put(LT);
			break;
		case "branche":
			linkbuffer.put(BRANCHE);
			break;
		case "branchne":
			linkbuffer.put(BRANCHNE);
			break;
		case "write":
			linkbuffer.put(SYS_WRITE);
			break;
		case "read":
			linkbuffer.put(SYS_READ);
			break;
		case "open":
			linkbuffer.put(SYS_OPEN);
			break;
		case "close":
			linkbuffer.put(SYS_CLOSE);
			break;
		}

		replaceLink(line);
	}

	private void replaceLink(String in){


		String[] expl = in.split(" ");

		for(int i = 0; i < expl.length; i++){

			String s = expl[i];

			if(s.isEmpty()){
				continue;
			}

			if(registers.containsKey(s)){
				linkbuffer.put(registers.get(s).byteValue());
			}else if(s.charAt(0) == '#'){
				parseNum(s);
			}else{

				int var = 0;
				buffer.put(RSETF);
				buffer.put(R6);
				buffer.put((byte)0x4);
				buffer.put((byte)0x4);
				
				if(linkVars.containsKey(s)){
					var = linkVars.get(s).intValue();
				}else{					
					
					if(!jumpFlags.containsKey(s)){
						jumpFlags.put(s, new ArrayList<Integer>());
					}
					
					jumpFlags.get(s).add(buffer.position());
				}

				byte[] arr = new byte[4];
				parser.parseInt(var, arr);
				buffer.put(arr);
				
				linkbuffer.put(R6);
			}
		}
	}
	
	private byte parseNum(String val){
		
		String num = val.substring(2);
		char type = val.charAt(1);
		
		switch(type){
		case 'b':
			linkbuffer.put(Byte.parseByte(num));
			return BYTE;
		case 's':
			linkbuffer.putShort(Short.parseShort(num));
			return SHORT;
		case 'i':
			linkbuffer.putInt(Integer.parseInt(num));
			return INT;
		case 'l':
			linkbuffer.putLong(Long.parseLong(num));
			return LONG;
		case 'f':
			linkbuffer.putFloat(Float.parseFloat(num));
			return FLOAT;
		case 'd':
			linkbuffer.putDouble(Double.parseDouble(num));
			return DOUBLE;
		}
		
		return -1;
	}

private byte parseNumReg(String val){
		
		String num = val.substring(2);
		char type = val.charAt(1);
		
		switch(type){
		case 'b':
			linkbuffer.put((byte)0x7);
			linkbuffer.put((byte)0x1);
			linkbuffer.put(Byte.parseByte(num));
			return BYTE;
		case 's':
			linkbuffer.put((byte)0x6);
			linkbuffer.put((byte)0x2);
			linkbuffer.putShort(Short.parseShort(num));
			return SHORT;
		case 'i':
			linkbuffer.put((byte)0x4);
			linkbuffer.put((byte)0x4);
			linkbuffer.putInt(Integer.parseInt(num));
			return INT;
		case 'l':
			linkbuffer.put((byte)0x0);
			linkbuffer.put((byte)0x8);
			linkbuffer.putLong(Long.parseLong(num));
			return LONG;
		case 'f':
			linkbuffer.put((byte)0x4);
			linkbuffer.put((byte)0x4);
			linkbuffer.putFloat(Float.parseFloat(num));
			return FLOAT;
		case 'd':
			linkbuffer.put((byte)0x0);
			linkbuffer.put((byte)0x8);
			linkbuffer.putDouble(Double.parseDouble(num));
			return DOUBLE;
		}
		
		return -1;
	}
	
}
