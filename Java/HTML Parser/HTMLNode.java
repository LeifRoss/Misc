package sri.os.dom;

import java.util.ArrayList;
import java.util.HashMap;

public class HTMLNode {

	private HashMap<String,String> attributes;
	private StringBuilder innerHTML;
	private ArrayList<HTMLNode> children;
	
	private boolean isSingleTag;
	private String type;

	public HTMLNode(){
		type = "doc";
		attributes = new HashMap<String,String>();
		children = new ArrayList<HTMLNode>();
		innerHTML = new StringBuilder();
	}

	public void parse(char[] data, int from, int to){

		boolean hasType = false;
		boolean quote = false;

		String key = null;
		String val = null;

		char last = 0;

		int idx0 = from;
		int idx1 = from;
		int len = 0;

		boolean hasVal = false;
		boolean hasStr = false;
		
		for(int i = from; i < to; i++){

			char c = data[i];
			len = idx1-idx0;

			switch(c){

			case ' ':

				if(!quote && hasStr){

					if(!hasVal){

						if(key != null){
							attributes.put(key, "");
						}

						key = String.copyValueOf(data, idx0, len);
						
						if(!hasType){
							type = key;
							hasType = true;
							key = null;
						}
						
					}else{
						
						val = String.copyValueOf(data, idx0, len);
						
						attributes.put(key, val);
						key = val = null;
						hasVal = false;
					}
					
					hasStr = false;
				}

				break;
			case '"':
			case '\'':

				if(quote && last == c){	
					quote = false;
				}else if(!quote){
					last = c;
					quote = true;
				}

				break;
			case '=':
				
				if(!quote){
					hasVal = true;
					key = String.copyValueOf(data, idx0, len);
					idx0 = i+1;
					hasStr = false;
				}
				
				break;
			default:
				if(!hasStr){
					hasStr = true;
					idx0 = i;
				}
				
				idx1 = i+1;
				
			}

		}

		idx1 = to-1;
		len = idx1-idx0;
		
		if(!hasVal){

			if(key != null){
				attributes.put(key, "");
			}

			key = String.copyValueOf(data, idx0, len+1);
			
			if(!hasType){
				type = key;
				hasType = true;
			}
			
		}else{
			
			val = String.copyValueOf(data, idx0, len);
			attributes.put(key, val);
			key = val = null;
			hasVal = false;
		}
		
		
		isSingleTag = type.equals("br") || type.equals("!--") || type.equals("wbr");
	}

	
	public String getType(){
		return this.type;
	}

	
	public void append(HTMLNode node){
		children.add(node);
	}

	public HTMLNode get(int index){
		return children.get(index);
	}
	
	
	public ArrayList<HTMLNode> get(String query){
		
		ArrayList<HTMLNode> result = new ArrayList<HTMLNode>();
		this.get(query,result);
		
		return result;
	}
	
	public void get(String query, ArrayList<HTMLNode> result){
		
		int idx = query.indexOf(' ');
		int len = children.size();
		
		if(idx < 0){
			
			for(int i = 0; i < len; i++){
				
				HTMLNode child = children.get(i);
				
				if(query.equals(child.getType())){
					result.add(child);
				}
			}
			
		}else{
		
			String ntype = query.substring(0, idx);
			String nquery = query.substring(idx+1);

			for(int i = 0; i < len; i++){
				
				HTMLNode child = children.get(i);
				
				if(ntype.equals(child.getType())){
					child.get(nquery, result);
				}
			}
		}
	}
	
	
	public boolean isSingle(){
		return isSingleTag;
	}

	
	public void appendInnerHTML(String c){
		this.innerHTML.append(c);
	}
	
	
	public void appendInnerHTML(char c){
		this.innerHTML.append(c);
	}
	
	
	public void setInnerHTML(String text){
		this.innerHTML = new StringBuilder(text);
	}

	
	public boolean hasAttribute(String key){
		return this.attributes.containsKey(key);
	}
	
	
	public String getAttribute(String key){
		
		if(this.hasAttribute(key)){
			return this.attributes.get(key);
		}
		
		return null;
	}
	
	
	public void printDebug(String offset){

		StringBuilder str = new StringBuilder();

		for(String key: attributes.keySet()){
			String val = attributes.get(key);
			str.append("["+key+"="+val+"]");
		}

		System.out.println(offset+">"+type+" "+str.toString()+"("+innerHTML.toString()+")");

		for(int i = 0; i < children.size(); i++){
			children.get(i).printDebug(offset+"-");
		}
	}

	
	@Override
	public String toString(){
		// reconstruct document
		return null;
	}

}
