package yatea_client.datamodel;

import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil.ToStringAdapter;

import talismane_client.datamodel.Token;

public class Term {
	
	private int start;
	private int end;
	private String text;
	private int sentence;
	ArrayList<Token> tokens;
	
	
	public Term(){
		this.tokens = new ArrayList<Token>();
	}


	public int getStart() {
		return start;
	}


	public void setStart(int start) {
		this.start = start;
	}


	public int getEnd() {
		return end;
	}


	public void setEnd(int end) {
		this.end = end;
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public int getSentence() {
		return sentence;
	}


	public void setSentence(int sentence) {
		this.sentence = sentence;
	}
	


	public ArrayList<Token> getTokens() {
		return tokens;
	}


	public void setTokens(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}
	
	public void addToken(Token currToken){
		this.tokens.add(currToken);
	}


	public String toString(){
		String toReturn ="";
		toReturn += this.getText() + "[" + this.getStart() + "," + this.getEnd() + "]";
		return toReturn;
	}
	
	

}
