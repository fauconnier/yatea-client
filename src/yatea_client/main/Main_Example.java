package yatea_client.main;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.jdom2.JDOMException;

import talismane_client.core.TalismaneClient;
import talismane_client.datamodel.Structure;
import yatea_client.core.YateaClient;
import yatea_client.datamodel.Term;

public class Main_Example {

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException, JDOMException {
	
		/* pre-requisite : YaTea must be installed and can be reached with "yatea" command */
		YateaClient yatea_client = new YateaClient();
		TalismaneClient talismane_client = new TalismaneClient();
		
		
		/* A test String */
		String test = "Le calcul de la similarité sémantique entre les termes repose sur l'existence "
				+ "et l'utilisation de ressources sémantiques. "
				+ "Cependant de telles ressources, qui consistent souvent à proposer des équivalences "
				+ "entre entités, ou des synonymes, doivent elles-mêmes être d'abord analysées afin de "
				+ "définir des zones de fiabilité où la similarité sémantique est plus forte. "
				+ "Nous proposons une méthode d'acquisition de synonymes élémentaires grâce à l'exploitation "
				+ "des terminologies structurées au travers de l'analyse de la structure syntaxique des termes "
				+ "complexes et de leur compositionnalités. ";
		
				
		/* Analysing structure */
		Structure structure_test = talismane_client.analyse(test, false);
		ArrayList<Term> terms = yatea_client.analyse(structure_test, false);
		
		/* print terms */
		for(Term currTerm : terms){
			System.out.println(currTerm.toString());
		}
		

	}

}
