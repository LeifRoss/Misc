package sri.os.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class HTMLDocument {

	private HashMap<String, ArrayList<HTMLNode>> idNodes, classNodes;

	private HashMap<String,Boolean> blackList;


	private String innerHTML;
	private char[] rawData;
	private HTMLNode document;

	private Stack<HTMLNode> nodeStack;
	private HTMLNode currNode;


	private boolean prevClosing, prevSpacing;
	private int off, tagStart, tagStop;

	/**
	 * HTMLDocument
	 * @param data the raw html source code
	 */
	public HTMLDocument(String data){
		this(data, new String[]{"script","style"});
	}

	/**
	 * HTMLDocument
	 * @param data the raw html source code
	 * @param noparse a filter for tags to not parse as HTML
	 */
	public HTMLDocument(String data, String[] noparse){

		idNodes = new HashMap<String,ArrayList<HTMLNode>>();
		classNodes = new HashMap<String,ArrayList<HTMLNode>>();
		blackList = new HashMap<String,Boolean>();

		for(String s: noparse){
			blackList.put(s, true);
		}

		innerHTML = data;
		rawData = data.toCharArray();
		nodeStack = new Stack<HTMLNode>();

		document = new HTMLNode();
		parse();
	}


	public ArrayList<HTMLNode> getElementById(String id){

		if(idNodes.containsKey(id)){
			return idNodes.get(id);
		}

		return null;
	}


	public ArrayList<HTMLNode> getElementByClass(String cls){

		if(classNodes.containsKey(cls)){
			return classNodes.get(cls);
		}

		return null;
	}


	public ArrayList<HTMLNode> get(String query){
		return document.get(query);
	}


	private void parse(){

		currNode = document;
		nodeStack.clear();
		int length = rawData.length;

		prevClosing = false;
		boolean readAttr = false;

		for(off = 0; off < length; off++){

			char c = rawData[off];

			switch(c){

			case '<': 
				tagStart = off+1;
				readAttr = true;
				openNode();
				break;
			case '>':
				tagStop = off;
				readAttr = false;
				closeNode();
				break;
			default:

				if(!readAttr){

					if(c != '\n' && c != '\t' && (!prevSpacing || c != ' ')){
						currNode.appendInnerHTML(c);
					}

					prevSpacing = c == ' ';
				}
			}

			prevClosing = c == '/';
		}
	}


	private void openNode(){ // this is run at '<'

		prevClosing = rawData[off+1] == '/';

		if(prevClosing){ // '</' closing tag

			currNode = nodeStack.pop();
			off = innerHTML.indexOf('>', off);

		}else{ // '<' open new tag

			HTMLNode node = new HTMLNode();
			currNode.append(node);
			nodeStack.push(currNode);
			currNode = node;
		}
	}


	private void closeNode(){


		currNode.parse(rawData, tagStart, tagStop - (prevClosing ? 1 : 0));

		if(currNode.hasAttribute("class")){

			String ncls = currNode.getAttribute("class");

			if(!classNodes.containsKey(ncls)){
				classNodes.put(ncls, new ArrayList<HTMLNode>());
			}

			classNodes.get(ncls).add(currNode);
		}

		if(currNode.hasAttribute("id")){

			String nid = currNode.getAttribute("id");

			if(!idNodes.containsKey(nid)){
				idNodes.put(nid, new ArrayList<HTMLNode>());
			}

			idNodes.get(nid).add(currNode);
		}


		if(prevClosing || currNode.isSingle()){ // '/>' || '<br>'
			currNode = nodeStack.pop();	
		}


		if(blackList.containsKey(currNode.getType())){

			String reg = "</"+currNode.getType()+">";
			int nIndex = innerHTML.indexOf(reg, off);

			currNode.appendInnerHTML(innerHTML.substring(off+1, nIndex-1));
			off = nIndex + reg.length()-1;
		}

	}


	public void printDebug(){
		document.printDebug("");
	}


}
