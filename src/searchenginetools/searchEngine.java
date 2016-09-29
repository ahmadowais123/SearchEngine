package searchenginetools;

import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class searchEngine 
{

    public HashMap<String, LinkedList<String> > wordIndex; // this will contain a set of pairs (String, LinkedList of Strings)	
    public directedGraph internet; // this is our internet graph
    
    
    
    // Constructor initializes everything to empty data structures
    // It also sets the location of the internet files
    searchEngine() 
    {
	// Below is the directory that contains all the internet files
	htmlParsing.internetFilesLocation = "internetFiles";
	wordIndex = new HashMap<String, LinkedList<String> > ();		
	internet = new directedGraph();				
    } // end of constructor2015
    
    
    // Returns a String description of a searchEngine
    public String toString () 
    {
	return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
    }
    
    
    // This does a graph traversal of the internet, starting at the given url.
    // For each new vertex seen, it updates the wordIndex, the internet graph,
    // and the set of visited vertices.
    
    void traverseInternet(String url) throws Exception 
    {    	
    	//Add the present url into the graph
    	internet.addVertex(url);
    	
    	//Add edges to the present vertex (Add the neighbouring urls)
    	//Linked List used to store all the urls leading out of the present page
    	//which is then iterated over and the urls added as edges of the present page
    	LinkedList<String> neighbours = htmlParsing.getLinks(url);
    	Iterator<String> i = neighbours.iterator();
    	while(i.hasNext())
    	{
    		String s = i.next();
    		internet.addEdge(url,s);
    	}
    	
    	//Update word index
    	//Linked List used to store all the words on the present page
    	//Which is then iterated over to update the word index.
    	LinkedList<String> words = htmlParsing.getContent(url);
    	Iterator<String> j = words.iterator();
    	while(j.hasNext())
    	{
    		String s = j.next();
    		
    		//Checks if a word is already present in the index
    		//If present it adds the url to the list of urls containing that word
    		//Else adds the word to the index and then adds the url.
    		if(wordIndex.containsKey(s))
    		{
    			LinkedList temp = wordIndex.get(s);
    			temp.add(url);
    		}
    		else
    		{
    			wordIndex.put(s, new LinkedList<String>());
    			LinkedList temp = wordIndex.get(s);
    			temp.add(url);
    		}
    	}
    	
    	//Change label to visited
    	internet.setVisited(url,true);
    	
    	//This DFS makes sure that all the vertices are visited
    	LinkedList<String> edges = internet.getNeighbors(url);
    	Iterator<String> k = edges.iterator();
    	while(k.hasNext())
    	{
    		String s = k.next();
    		if(internet.getVisited(s)==false)
    		{
    			traverseInternet(s);
    		}
    	}
    } // end of traverseInternet
    
    
    /* This computes the pageRanks for every vertex in the internet graph.
       It will only be called after the internet graph has been constructed using 
       traverseInternet.
       Use the iterative procedure described in the text of the assignment to
       compute the pageRanks for every vertices in the graph. 
       
       This method will probably fit in about 30 lines.
    */
    void computePageRanks() 
    {
    	//Initializes all page ranks to 1.
    	LinkedList vertices = internet.getVertices();
    	Iterator<String> i = vertices.iterator();
    	while(i.hasNext())
    	{
    		String s = i.next();
    		internet.setPageRank(s,1.0);
    	}
    	
    	//Iterative formual as given is implemented.
    	//Iterates 100 times to ensure precision.
    	for(int j=0; j<100; j++)
    	{
    		//This iterates over all the vertices in the graph
    		Iterator<String> k = vertices.iterator();
    		while(k.hasNext())
    		{
    			String s = k.next();
    			double pageRank = 0.5;
    			
    			//This iterates over all the incident edges of the present vertex.
    			LinkedList<String> incidentEdges = internet.getEdgesInto(s);
    			Iterator<String> m = incidentEdges.iterator();
    			while(m.hasNext())
    			{
    				String t=m.next();
    				pageRank+=(0.5*((internet.getPageRank(t))/(internet.getOutDegree(t))));
    			}
    			//Sets page rank
    			internet.setPageRank(s,pageRank);
    		}
    	}
    } // end of computePageRanks
    
	
    /* Returns the URL of the page with the high page-rank containing the query word
       Returns the String "" if no web site contains the query.
       This method can only be called after the computePageRanks method has been executed.
       Start by obtaining the list of URLs containing the query word. Then return the URL 
       with the highest pageRank.
       This method should take about 25 lines of code.
    */
    String getBestURL(String query) 
    {
    	//Checks if query word is present in index
    	if(wordIndex.containsKey(query))
    	{
    		//Gets the list of URLs containign the query word
    		LinkedList<String> listURL = wordIndex.get(query);
    		double pageRank=0;
    		String bestURL="";
    		
    		//Iterates over the list and chooses the one with highest page rank.
    		Iterator<String> i = listURL.iterator();
    		while(i.hasNext())
    		{
    			String s = i.next();
    			if(internet.getPageRank(s)>pageRank)
    			{
    				pageRank=internet.getPageRank(s);
    				bestURL=s;
    			}
    		}
    		
    		//returns best URL
    		return bestURL;
    	}
    	else
    	{
    		return "";
    	}
    } // end of getBestURL
    
    
	
    public static void main(String args[]) throws Exception
    {		
	searchEngine mySearchEngine = new searchEngine();
	
	//It takes a little while for the program to run but it works. Please dont think it doesnt work.
	mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");
		
	mySearchEngine.computePageRanks();
	
	BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
	String query;
	do {
	    System.out.print("Enter query: ");
	    query = stndin.readLine();
	    if ( query != null && query.length() > 0 ) {
		System.out.println("Best site = " + mySearchEngine.getBestURL(query));
	    }
	} while (query!=null && query.length()>0);				
    } // end of main
}
