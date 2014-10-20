package yatea_client.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.*;

import talismane_client.datamodel.Sentence;
import talismane_client.datamodel.Structure;
import talismane_client.datamodel.Token;
import yatea_client.datamodel.Term;

public class YateaClient {
	

	public YateaClient() {

	}

	public ArrayList<Term> analyse(Structure currStructure) throws UnknownHostException,
			IOException, InterruptedException {
		return this.analyse(currStructure, false);
	}
	
	public ArrayList<Term> analyse(Structure currStructure, boolean print) throws IOException, InterruptedException{
		
		String workingDir = System.getProperty("user.dir");
		/**
		 * Ecriture
		 */
		this.mappingToTreeTagger(currStructure, workingDir+"/tmp.ttg"); /* write */
		
		/**
		 *  Analyse avec YaTea
		 */
		Runtime runtime = Runtime.getRuntime();
		
		String [] cmdarray = new String [4];
		cmdarray [0] = "yatea";
		cmdarray [1] = "-rcfile";
		cmdarray [2] = "/home/jfaucon/Program/YateA/Lingua-YaTeA-0.622/etc/yatea/yatea-fr.rc";
		cmdarray [3] = workingDir+"/tmp.ttg";
		
		String [] envp = new String [0];
		File dir = new File( workingDir+"/");
		
		final Process process = runtime.exec(cmdarray, envp, dir);
		process.waitFor();
		
		/**
		 *  Récupération des éléments
		 */
		ArrayList<Term> candidate_terms = new ArrayList<Term>();
		
		// Remove doctype in xml file
//		removeDTD(workingDir+"/tmp/default/xml/candidates.xml");
		
		copyFile(new File("/home/jfaucon/Program/YateA/Lingua-YaTeA-0.622/share/doc/YaTeA/DTD/yatea.dtd"), new File(workingDir+"/tmp/default/xml/yatea.dtd"));
		
		SAXBuilder sxb = new SAXBuilder();
		String pathXml =  workingDir+"/tmp/default/xml/candidates.xml";
		
		
		Document document =  new Document();
		try {
			document =  sxb.build(new File(pathXml));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			System.err.println("Error in reading XML");
			e.printStackTrace();
		}


		Element root = document.getRootElement();
		
		Element listTerm_Candidate = root.getChildren("LIST_TERM_CANDIDATES").get(0);
		List candidates = listTerm_Candidate.getChildren("TERM_CANDIDATE");
		
		Iterator i = candidates.iterator();
		while (i.hasNext()) {
			Element courant = (Element) i.next();
			
			boolean MNP_status = false;
			
//			if(courant.getAttributeValue("MNP_STATUS").equals("1") ){
//				MNP_status = false;
//			}
//			
//			if(courant.getAttributeValue("MNP_STATUS").equals("1")){
//				Element syntactic_analysis = courant.getChild("SYNTACTIC_ANALYSIS");
//				if(syntactic_analysis != null){
//					System.out.println("TEST");
//				}
//			}
			
			if(courant.getChild("SYNTACTIC_ANALYSIS") != null){
				MNP_status = true;
			}
			
			
			
			if( MNP_status ){
				Element form = courant.getChild("FORM");
//				System.out.println(form.getText());
				
				
				Element listOccurences = courant.getChildren("LIST_OCCURRENCES").get(0);
				List occurrences = listOccurences.getChildren("OCCURRENCE");
				Iterator j = occurrences.iterator();
				
				while(j.hasNext()){
					Term newTerm = new Term();
					
					Element currOcc = (Element) j.next();
					Element sentence = currOcc.getChild("SENTENCE");
					Element start = currOcc.getChild("START_POSITION");
					Element end = currOcc.getChild("END_POSITION");

					newTerm.setSentence(Integer.parseInt(sentence.getText()));
					newTerm.setStart(Integer.parseInt(start.getText()));
					newTerm.setEnd(Integer.parseInt(end.getText()));
					newTerm.setText(form.getText());
				
					candidate_terms.add(newTerm);
				}
			}
		}
		
		
		// Le plus simple serait un mapping sur les tokens
		// ainsi :
		// - on reste en objet
		// - on peut calculer les caractères beaucoup plus tard.
		for(Term currTerm : candidate_terms){
			Sentence currSentence = currStructure.get(currTerm.getSentence());
			int length = 0;
			for(Token currToken : currSentence){
				if(length >= currTerm.getStart() && length <= currTerm.getEnd()){
					currTerm.addToken(currToken);
				}
				length += currToken.getForm().length() + 1;
			}
		}
		
		if(print){
			for(Term currTerm : candidate_terms){
				System.out.println(currTerm.toString());
			}
		}
		
		return candidate_terms;
	}
	
	
	
	
	private void mappingToTreeTagger(Structure currStructure, String path) throws IOException{
		
		File outFile = new File(path);
		Writer destFile = null;
		
		destFile = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outFile), "UTF-8"));
		
		String toWrite = "";
		for(Sentence currSentence : currStructure){
			for(Token currToken : currSentence){
				
				if(this.postagTreeTagger(currToken).getCppostag().equals("")){
					System.out.print(currToken.getForm());
					System.out.print("\t");
					System.out.print(currToken.getCppostag());
					System.out.print("\t");
					System.out.print(currToken.getFeats());
					System.out.print("\t");
					System.out.print(currToken.getLemma());
					System.out.print("\n");
					System.out.print(this.postagTreeTagger(currToken));
					System.out.println("");
					
					System.err.println("Erreur YaTeA : postag non reconnu");
				}
				
				if(currToken.getId_token() == currSentence.size()){
					if(currToken.getCppostag().equals("PUN")){
						toWrite += currToken.getForm() + "\t" + "SENT" + "\t" + currToken.getLemma() + "\n";
					}
					else{
						toWrite += currToken.getForm() + "\t" + this.postagTreeTagger(currToken).getCppostag() + "\t" + currToken.getLemma()+"\n";
					}
				
				}
				else{
					toWrite += currToken.getForm() + "\t" + this.postagTreeTagger(currToken).getCppostag() + "\t" + currToken.getLemma()+"\n";
				}
			}
		}
		
		destFile.write(toWrite);
		destFile.close();
		
	}
	
	private Token postagTreeTagger(Token currToken){
		
		String postag = currToken.getCppostag();
		String lemma = currToken.getLemma();
		String feats = currToken.getFeats();
		
		String postag_treeTagger = "";
				
		
		if(postag.equals("DET")) {
//			if(lemma.equals("le") || lemma.equals("la") || lemma.equals("les")  ){
//				postag_treeTagger = "DET:ART";
//			}
//			else 
			if( lemma.equals("leur") || lemma.equals("ton") || lemma.equals("ta") || lemma.equals("tes") || lemma.equals("leurs")
					|| lemma.equals("mon") || lemma.equals("ma") || lemma.equals("mes")){
				currToken.setCppostag("DET:POS");
			}
			else{
				
				if(currToken.getLemma().equals("l'")){
					currToken.setLemma("le");
				}
				currToken.setCppostag("DET:ART");
			}
		}
		else if(postag.equals("NC")){
			currToken.setCppostag("NOM");
		}
		else if(postag.equals("P")){
			currToken.setCppostag("PRP");
		}
		else if(postag.equals("CC")){
			currToken.setCppostag("KON");
		}
		else if(postag.equals("PONCT")){
			currToken.setCppostag("PUN");
		}
		else if(postag.equals("PRO")){
			currToken.setCppostag("PRO");
		}
		else if(postag.equals("P+D")){
			currToken.setCppostag("PRP:det");
		}
		else if(postag.equals("P+D")){
			currToken.setCppostag("PRP:det");
		}
		else if(postag.equals("ADV")){
			currToken.setCppostag("ADV");
		}
		else if(postag.equals("ADJ")){
			currToken.setCppostag("ADJ");
		}
		else if(postag.equals("VPP")){
			currToken.setCppostag("VER:pper");
		}
		else if(postag.equals("CLS")){
			currToken.setCppostag("PRO:PER");
		}
		else if(postag.equals("VINF")){
			currToken.setCppostag("VER:infi");
		}
		else if(postag.equals("NPP")){
			currToken.setCppostag("NAM");
		}
		else if(postag.equals("V")){
			if(feats.contains("|t=P")){
				currToken.setCppostag("VER:pres");
			}
		}
		else if(postag.equals("PROREL")){
			currToken.setCppostag("PRO:REL");
		}
		else{
//			System.err.println("Erreur dans TreeTaggerMapping pour " + currToken.getCppostag());
		}
		
		if(currToken.getLemma().equals("_")){
			currToken.setLemma(currToken.getForm());
		}
		
		
		return currToken;
	}
	
	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
	
	
//	public void removeDTD(String path) throws IOException{
//		File inputFile = new File(path);
//		File tempFile = new File(path+"_tmp");
//
//		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
//		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
//
//		String lineToRemove = "DOCTYPE";
//		String currentLine;
//
//		while((currentLine = reader.readLine()) != null) {
//		    // trim newline when comparing with lineToRemove
//		    String trimmedLine = currentLine.trim();
//		    if(trimmedLine.contains(lineToRemove)) continue;
//		    writer.write(currentLine+"\n");
//		}
//
//		
//		boolean successful = tempFile.renameTo(inputFile);
//	}
}
