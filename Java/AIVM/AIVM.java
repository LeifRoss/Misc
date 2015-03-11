package sri.vn.aivm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Artificial Intelligence Virtual Machine
 * @author LeifAndreas
 *
 */
public class AIVM {

	public static final byte BYTE=0, SHORT=1, CHAR=2, INT=3, LONG=4, FLOAT=5, DOUBLE=6, STRING=7;
	public static final byte R0 = 0, R1 = 1, R2 = 2, R3 = 3, R4 = 4, R5 = 5, R6 = 6, LR = 7;

	// functions
	public static final byte 
			RSETF = 0x0, RSET = 0x1, MEMSET = 0x2, MALLOC = 0x3, REALLOC = 0x4, FREE = 0x5, MEMCPY = 0x6, MEMCMP = 0x7,
			PUSH = 0x8, POP = 0x9, PUSHA = 0xa, POPA = 0xb, 
			JMP = 0xc, BRANCHE = 0xd, BRANCHNE = 0xe,
			EQ = 0xf, GT = 0x10, LT = 0x11,
			ADD = 0x12, MUL = 0x13, DIV = 0x14,
			SIZEOF = 0x15, SYS_WRITE = 0x16, SYS_READ = 0x17, SYS_OPEN = 0x18, SYS_CLOSE = 0x19, LOAD = 0x20, SAVE = 0x21;

	public static final int SYSTEM = 1;

	private ArrayList<byte[]> vMemory;
	private DynamicByteBuffer stack;
	private BinaryParser parser;
	private byte[][] register;
	private byte[] program;
	private int len, counter;


	private int errorPos;
	private boolean error;
	private HashMap<Integer,KernelModule> modules;

	public AIVM(byte[] program){

		this.program = program;
		this.len = this.program.length;
		this.error = false;
		this.stack = new DynamicByteBuffer(32);
		this.vMemory = new ArrayList<byte[]>();
		this.register = new byte[8][8];
		this.parser = new BinaryParser();
		this.modules = new HashMap<Integer,KernelModule>();

		// load std modules
		loadModule(new PrintModule());

	}


	public void loadModule(KernelModule module){
		this.modules.put(module.address(), module);
		module.onLoad();
	}

	public byte[] execute(byte[] data){

		vMemory.clear();
		vMemory.add(data);
		counter = 0;

		while(counter < len){
			instruction(data);
		}

		System.out.println(register[R0][7]);

		return data;
	}

	
	public byte[] executeDebug(byte[] data){

		vMemory.clear();
		//vMemory.add(data); // TODO add call to read from input data and write to output
		counter = 0;
		int instr = 0;
		
		try{
			while(counter < len){
				instr++;
				instruction(data);
			}
		}catch(Exception e){
			System.out.println("Encountered error at line "+instr);
			e.printStackTrace();
		}
		
		System.out.println(register[R0][7]);

		return data;
	}
	
	private void instruction(byte[] data) {

		byte instr = program[counter];

		switch(instr){
		case RSETF:
			rsetf();
			break;
		case RSET:
			rset();
			break;
		case LOAD:
			load();
			break;
		case SAVE:
			save();
			break;
		case MEMSET:
			memset();
			break;
		case MALLOC:
			malloc();
			break;
		case REALLOC:
			realloc();
			break;
		case MEMCPY:
			memcpy();
			break;
		case MEMCMP:
			memcmp();
			break;
		case SIZEOF:
			sizeof();
			break;
		case FREE:
			free();
			break;
		case PUSH:
			push();
			break;
		case POP:
			pop();
			break;
		case PUSHA:
			pushA();
			break;
		case POPA:
			popA();
			break;
		case JMP:
			jmp();
			break;
		case BRANCHE:
			branchE();
			break;
		case BRANCHNE:
			branchNE();
			break;
		case EQ:
			eq();
			break;
		case LT:
			lt();
			break;
		case GT:
			gt();
			break;
		case ADD:
			add();
			break;
		case MUL:
			mul();
			break;
		case DIV:
			div();
			break;
		case SYS_WRITE:
			sysWrite();
			break;
		case SYS_READ:
			sysRead();
			break;
		case SYS_OPEN:
			sysOpen();
			break;
		case SYS_CLOSE:
			sysClose();
			break;
		default: // invalid instruction error
			onError();
		}	
	}


	private void pushA(){

		stack.put(register[0]);
		stack.put(register[1]);
		stack.put(register[2]);
		stack.put(register[3]);
		stack.put(register[4]);
		stack.put(register[5]);
		stack.put(register[6]);
		stack.put(register[7]);
		counter++;
	}

	private void popA(){

		stack.flip();
		stack.get(register[7]);
		stack.get(register[6]);
		stack.get(register[5]);
		stack.get(register[4]);
		stack.get(register[3]);
		stack.get(register[2]);
		stack.get(register[1]);
		stack.get(register[0]);
		stack.reset();
		counter++;
	}

	// TODO: stack functions
	private void push(){

		byte rA = program[counter+1];
		stack.put(register[rA]);
		counter+=2;
	}

	private void pop(){

		stack.flip();
		byte rA = program[counter+1];
		stack.get(register[rA]);
		stack.reset();
		counter += 2;
	}

	private void rsetf(){		

		byte rA = program[counter+1];
		byte o = program[counter+2];
		byte l = program[counter+3];

		for(byte b = 0; b < l; b++){
			register[rA][o+b] = program[counter+4+b];
		}

		counter += 4 + l;
	}


	private void rset(){

		byte rA = program[counter+1];
		byte l = program[counter+2];

		for(byte b = 0; b < l && b < 8; b++){
			register[rA][b] = program[counter+3+b];
		}

		counter+= 3 + l;
	}

	private void load(){
		
		byte rA = program[counter+1];
		byte rB = program[counter+2];
		
		int index = parser.parseInt(register[rA]);
		byte[] data = vMemory.get(index);
		
		for(int i = 0; i < 8 && i < data.length; i++){
			register[rB][i] = data[i]; 
		}
		
		counter += 3;
	}
	
	private void save(){
		
		byte rA = program[counter+1];
		byte rB = program[counter+2];
		
		int index = parser.parseInt(register[rB]);
		byte[] data = vMemory.get(index);
		
		for(int i = 0; i < 8 && i < data.length; i++){
			data[i] = register[rA][i] ; 
		}
		
		counter += 3;
	}
	
	
	
	private void memset(){

		byte rA = program[counter+1];
		byte rB = program[counter+2];
		byte rC = program[counter+3];

		int idx = parser.parseInt(register[rA]);
		int offset = parser.parseInt(register[rB]);
		int length = parser.parseInt(register[rC]);

		byte[] arr = vMemory.get(idx);

		for(int i = 0; i < length; i++){
			arr[offset+i] = program[counter+i+4];
		}


		counter += 4 + length;
	}

	// memory functions, allocate a chunk of memory and add the index to the stack
	private void malloc(){

		byte rA = program[counter+1];
		byte rB = program[counter+2];

		int l = parser.parseInt(register[rA]); // size of memory to allocate
		parser.parseInt(vMemory.size(), register[rB]);

		vMemory.add(new byte[l]);
		counter+=3;
	}

	// reallocate a memory block
	private void realloc(){
		byte rA = program[counter+1];
		byte rB = program[counter+2];

		int idx = parser.parseInt(register[rA]);
		int l = parser.parseInt(register[rB]); // size of memory to allocate		
		vMemory.set(idx, new byte[l]);

		counter+=3;
	}

	private void sizeof(){

		byte rA = program[counter+1];
		byte rB = program[counter+2];

		int idx = parser.parseInt(register[rA]);
		parser.parseInt(vMemory.get(idx).length,register[rB]);	

		counter+=3;
	}

	private void free(){
		byte rA = program[counter+1];
		int idx = parser.parseInt(register[rA]);
		vMemory.set(idx, null);
		counter+=2;
	}

	// copy rC bytes from rA to rB
	private void memcpy(){

		byte rA = program[counter+1];
		byte rB = program[counter+2];
		byte rC = program[counter+3];

		int a = parser.parseInt(register[rA]);
		int b = parser.parseInt(register[rB]);
		int l = parser.parseInt(register[rC]);

		byte[] arr0 = vMemory.get(a);
		byte[] arr1 = vMemory.get(b);

		BufferUtility.fill(arr0, arr1, 0, 0, l);

		counter += 4;
	}

	// compare two chunks of stack memory, add result to stack
	private void memcmp(){

		byte rA = program[counter+1];
		byte rB = program[counter+2];
		byte rC = program[counter+3];

		int a = parser.parseInt(register[rA]);
		int b = parser.parseInt(register[rB]);

		register[rC][7] = Arrays.equals(vMemory.get(a), vMemory.get(b)) ? (byte)1 : (byte)0;
		counter += 4;
	}

	// conditionals
	private void eq(){

		byte type = program[counter+1];
		byte rA = program[counter+2];
		byte rB = program[counter+3];
		byte rC = program[counter+4];

		boolean result = false;

		switch(type){
		case BYTE:
			result = register[rA][7] == register[rB][7];
			break;
		case SHORT:
			result = register[rA][7] == register[rB][7] && register[rA][6] == register[rB][6];
			break;
		case CHAR:
			result = register[rA][7] == register[rB][7] && register[rA][6] == register[rB][6];
			break;
		case INT:
			result = register[rA][7] == register[rB][7] && register[rA][6] == register[rB][6] &&
					 register[rA][5] == register[rB][5] && register[rA][4] == register[rB][4];
			break;
		case LONG:
			result = register[rA][7] == register[rB][7] && register[rA][6] == register[rB][6] &&
					 register[rA][5] == register[rB][5] && register[rA][4] == register[rB][4] &&
					 register[rA][3] == register[rB][3] && register[rA][2] == register[rB][2] &&
					 register[rA][1] == register[rB][1] && register[rA][0] == register[rB][0];
			break;
		case FLOAT:
			result = register[rA][7] == register[rB][7] && register[rA][6] == register[rB][6] &&
					 register[rA][5] == register[rB][5] && register[rA][4] == register[rB][4];
			break;
		case DOUBLE:
			result = register[rA][7] == register[rB][7] && register[rA][6] == register[rB][6] &&
					 register[rA][5] == register[rB][5] && register[rA][4] == register[rB][4] &&
					 register[rA][3] == register[rB][3] && register[rA][2] == register[rB][2] &&
					 register[rA][1] == register[rB][1] && register[rA][0] == register[rB][0];
			break;
		case STRING:
			result = vMemory.get(rA).toString().compareTo(vMemory.get(rB).toString()) == 0;
			break;
		}

		register[rC][7] = result ? (byte)1:(byte)0;

		counter+=5;
	}


	private void gt(){
		byte type = program[counter+1];
		byte rA = program[counter+2];
		byte rB = program[counter+3];
		byte rC = program[counter+4];

		boolean result = false;

		switch(type){
		case BYTE:
			result = register[rA][7] > register[rB][7];
			break;
		case SHORT:
			result = parser.parseShort(register[rA]) > parser.parseShort(register[rB]);
			break;
		case CHAR:
			result = parser.parseChar(register[rA]) > parser.parseChar(register[rB]);
			break;
		case INT:
			result = parser.parseInt(register[rA]) > parser.parseInt(register[rB]);
			break;
		case LONG:
			result = parser.parseLong(register[rA]) > parser.parseLong(register[rB]);
			break;
		case FLOAT:
			result = parser.parseFloat(register[rA]) > parser.parseFloat(register[rB]);
			break;
		case DOUBLE:
			result = parser.parseDouble(register[rA]) > parser.parseDouble(register[rB]);
			break;
		case STRING:
			result = vMemory.get(rA).toString().compareTo(vMemory.get(rB).toString()) > 0;
			break;
		}

		register[rC][7] = result ? (byte)1:(byte)0;

		counter+=5;
	}

	private void lt(){

		byte type = program[counter+1];
		byte rA = program[counter+2];
		byte rB = program[counter+3];
		byte rC = program[counter+4];

		boolean result = false;

		switch(type){
		case BYTE:
			result = register[rA][7] < register[rB][7];
			break;
		case SHORT:
			result = parser.parseShort(register[rA]) < parser.parseShort(register[rB]);
			break;
		case CHAR:
			result = parser.parseChar(register[rA]) < parser.parseChar(register[rB]);
			break;
		case INT:
			result = parser.parseInt(register[rA]) < parser.parseInt(register[rB]);
			break;
		case LONG:
			result = parser.parseLong(register[rA]) < parser.parseLong(register[rB]);
			break;
		case FLOAT:
			result = parser.parseFloat(register[rA]) < parser.parseFloat(register[rB]);
			break;
		case DOUBLE:
			result = parser.parseDouble(register[rA]) < parser.parseDouble(register[rB]);
			break;
		case STRING:
			result = vMemory.get(rA).toString().compareTo(vMemory.get(rB).toString()) < 0;
			break;
		}

		register[rC][7] = result ? (byte)1:(byte)0;
		counter+=5;
	}


	private void branchE(){

		byte rA = program[counter+1];
		byte rB = program[counter+2];

		if(register[rA][7] == 0){
			counter+=3;
		}else{
			counter = parser.parseInt(register[rB]);
		}
	}


	private void branchNE(){

		byte rA = program[counter+1];
		byte rB = program[counter+2];

		if(register[rA][7] != 0){
			counter+=3;
		}else{
			counter = parser.parseInt(register[rB]);
		}
	}

	private void jmp(){
		byte rA = program[counter+1];
		counter = parser.parseInt(register[rA]);
	}

	// arithmetic operations

	private void add(){

		byte type = program[counter+1];
		byte rA = program[counter+2];
		byte rB = program[counter+3];
		byte rC = program[counter+4];

		switch(type){
		case BYTE:
			register[rC][7] = (byte)(register[rA][7] + register[rB][7]);
			break;
		case SHORT:
			parser.parseShort((short)(parser.parseShort(register[rA])+parser.parseShort(register[rB])), register[rC]);
			break;
		case INT:
			parser.parseInt(parser.parseInt(register[rA])+parser.parseInt(register[rB]), register[rC]);
			break;
		case LONG:
			parser.parseLong(parser.parseLong(register[rA])+parser.parseLong(register[rB]), register[rC]);
			break;
		case FLOAT:
			parser.parseFloat(parser.parseFloat(register[rA])+parser.parseFloat(register[rB]), register[rC]);
			break;
		case DOUBLE:
			parser.parseDouble(parser.parseDouble(register[rA])+parser.parseDouble(register[rB]), register[rC]);
			break;
		}


		counter+=5;
	}

	private void mul(){
		byte type = program[counter+1];
		byte rA = program[counter+2];
		byte rB = program[counter+3];
		byte rC = program[counter+4];

		switch(type){
		case BYTE:
			register[rC][7] = (byte)(register[rA][7] * register[rB][7]);
			break;
		case SHORT:
			parser.parseShort((short)(parser.parseShort(register[rA])*parser.parseShort(register[rB])), register[rC]);
			break;
		case INT:
			parser.parseInt(parser.parseInt(register[rA])*parser.parseInt(register[rB]), register[rC]);
			break;
		case LONG:
			parser.parseLong(parser.parseLong(register[rA])*parser.parseLong(register[rB]), register[rC]);
			break;
		case FLOAT:
			parser.parseFloat(parser.parseFloat(register[rA])*parser.parseFloat(register[rB]), register[rC]);
			break;
		case DOUBLE:
			parser.parseDouble(parser.parseDouble(register[rA])*parser.parseDouble(register[rB]), register[rC]);
			break;
		}

		counter+=5;
	}


	private void div(){
		byte type = program[counter+1];
		byte rA = program[counter+2];
		byte rB = program[counter+3];
		byte rC = program[counter+4];

		switch(type){
		case BYTE:
			register[rC][7] = (byte)(register[rA][7] / register[rB][7]);
			break;
		case SHORT:
			parser.parseShort((short)(parser.parseShort(register[rA])/parser.parseShort(register[rB])), register[rC]);
			break;
		case INT:
			parser.parseInt(parser.parseInt(register[rA])/parser.parseInt(register[rB]), register[rC]);
			break;
		case LONG:
			parser.parseLong(parser.parseLong(register[rA])/parser.parseLong(register[rB]), register[rC]);
			break;
		case FLOAT:
			parser.parseFloat(parser.parseFloat(register[rA])/parser.parseFloat(register[rB]), register[rC]);
			break;
		case DOUBLE:
			parser.parseDouble(parser.parseDouble(register[rA])/parser.parseDouble(register[rB]), register[rC]);
			break;
		}

		counter+=5;
	}

	// utility operations

	private void sysWrite(){

		byte rA = program[counter+1]; // handle
		byte rB = program[counter+2]; // write from address
		byte rC = program[counter+3]; // write to address
		byte rD = program[counter+4]; // result

		int handle = parser.parseInt(register[rA]);
		int idx0 = parser.parseInt(register[rB]);
		int idx1 = parser.parseInt(register[rC]);

		byte[] data = vMemory.get(idx0);

		if(modules.containsKey(idx1)){
			parser.parseInt(modules.get(idx1).write(handle, data), register[rD]);	
		}

		counter+=5;
	}


	private void sysRead(){

		byte rA = program[counter+1]; // handle
		byte rB = program[counter+2]; // number of bytes to read
		byte rC = program[counter+3]; // read from address
		byte rD = program[counter+4]; // result

		int handle = parser.parseInt(register[rA]);
		
		int idx0 = parser.parseInt(register[rB]);
		int idx1 = parser.parseInt(register[rC]);
		int idx2 = parser.parseInt(register[rD]);	

		if(modules.containsKey(idx1)){
			byte[] data = modules.get(idx1).read(handle,idx0);
			vMemory.set(idx2, data);
		}

		counter+=5;
	}

	private void sysOpen(){

		byte rA = program[counter+1]; // module
		byte rB = program[counter+2]; // address
		byte rC = program[counter+3]; // handle result

		int module = parser.parseInt(register[rA]);
		int address = parser.parseInt(register[rB]);

		byte[] path = vMemory.get(address);

		if(modules.containsKey(module)){
			int handle = modules.get(module).open(path);
			parser.parseInt(handle, register[rC]);
		}

		counter+=4;
	}

	private void sysClose(){

		byte rA = program[counter+1]; // module
		byte rB = program[counter+2]; // handle
		byte rC = program[counter+3]; // result

		int module = parser.parseInt(register[rA]);
		int handle = parser.parseInt(register[rB]);

		if(modules.containsKey(module)){
			int result = modules.get(module).close(handle);
			parser.parseInt(result, register[rC]);
		}

		counter+=4;
	}

	public void onError(){
		this.error = true;
		errorPos = counter;
		counter = len;
	}


}
