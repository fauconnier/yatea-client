# YaTeA-client

[YaTeA-client](https://github.com/jfaucon/yatea-client) is a simple client for [YaTeA](http://search.cpan.org/~thhamon/Lingua-YaTeA/), a terminology extractor. YaTeA-Client allows to treat texts with [Talismane](https://github.com/urieli/talismane) instead of TreeTagger.

YaTeA-client has the same licence as YaTeA (GPL).


## Talismane
### Install Talismane and Talismane-Client

1. Clone [Talismane](https://github.com/urieli/talismane) project
```bash
 git clone https://github.com/urieli/talismane.git
 cd talismane
```
    
2. Build Talismane with Ant
```bash
 ant jar
```

3. Clone [Talismane-client](https://github.com/jfaucon/talismane-client) in a new directory
```bash
 git clone https://github.com/jfaucon/talismane-client.git
 cd talismane_client
```  
        
4. Build Talismane-client with Ant
```bash
 ant jar
``` 

### Run server

* Run standard server for English :
```bash 
 cd talismane/dist/
 java -Xmx2G -jar talismane-en-2.x.x-allDeps.jar command=analyse mode=server
```     
* Or for French :
```bash
 java -Xmx2G -jar talismane-fr-2.x.x-allDeps.jar command=analyse mode=server
```
              
* It is possible to have a verbose output with a different log4j configuration. An example is proposed in _/talismane_client/external/_ directory. 
```bash
 cp /talismane-client/external/log4j.properties /talismane/dist/
 java -Xmx2G -jar talismane-fr-2.x.x-allDeps.jar  ... logConfigFile=log4j.properties
```
* By default, Talismane runs on port 7272 and can be reached on "localhost" (in a local use). Also, Talismane can be configured with others several parameters. See [Talismane users's manual](http://urieli.github.io/talismane/).



## YaTeA
### Install YaTeA

1. See [here](http://taln09.blogspot.be/2009/03/description-lextracteur-de-termes-yatea.html)


### Install YaTeA-client

1. Clone [YaTeA-client](https://github.com/jfaucon/yatea-client) project
```bash
 git clone https://github.com/jfaucon/yatea-client
 cd talismane
```





## Usage in a Java environment


* Import YaTeA-client project in a new project.

    
* You can call YaTeA like this:
```java
		/* pre-requisite : YaTea must be installed and can be reached with "yatea" command */
		YateaClient yatea_client = new YateaClient();
		TalismaneClient talismane_client = new TalismaneClient();
		
```
* A simple test: 

```java
		/* A test String */
		String test = "Le calcul de la similarité sémantique entre les termes repose sur l'existence "
				+ "et l'utilisation de ressources sémantiques. "
				+ "Cependant de telles ressources, qui consistent souvent à proposer des équivalences "
				+ "entre entités, ou des synonymes, doivent elles-mêmes être d'abord analysées afin de "
				+ "définir des zones de fiabilité où la similarité sémantique est plus forte. "
				+ "Nous proposons une méthode d'acquisition de synonymes élémentaires grâce à l'exploitation "
				+ "des terminologies structurées au travers de l'analyse de la structure syntaxique des termes "
				+ "complexes et de leur compositionnalités. ";
		
				
		/* Analysis of the syntactic structure */
		Structure structure_test = talismane_client.analyse(test);
		ArrayList<Term> terms = yatea_client.analyse(structure_test);
		
		/* Print candidate terms */
		for(Term currTerm : terms){
			System.out.println(currTerm.toString());
		}
```



## Warning
* YaTeA-client is currently very simple and the implementation is naive. There is still very much to be done ...

