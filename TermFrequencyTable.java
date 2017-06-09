/*
 * Author: Nikita Pandya
 * Prof. Snyder
 * CS112 - HW10 B.3
 * April 29, 2015
 * Purpose: This program stores the words from two Strings (i.e., documents) 
 * in such a way that we can calculate the "cosine" similarity of the two.
 *
 * To calculate the similarity of the two documents, we must take the union of the two word 
 * lists A and B (total vocabulary minus any "black-listed words") and then calculate the new 
 * term frequency vector of each document with respect to this total list of words. 
 * 
 */

public class TermFrequencyTable {
    
    private final int size = 101; //Size of the Hash table
    Node[] T = new Node[size]; //Array of nodes/hash table
    public Node head = null; 
    
    //HASH METHOD & HELPER
    int hash(String s) {
        return sfold(s, size);
    }
    //helper method that actually does the hashing
    int sfold(String s, int M) {
        int intLength = s.length() / 4;
        int sum = 0;
        for (int j = 0; j < intLength; j++) {
            char c[] = s.substring(j * 4, (j * 4) + 4).toCharArray();
            int mult = 1;
            for (int k = 0; k < c.length; k++) {
                sum += c[k] * mult;
                mult *= 256;
            }
        }
        
        char c[] = s.substring(intLength * 4).toCharArray();
        long mult = 1;
        for (int k = 0; k < c.length; k++) {
            sum += c[k] * mult;
            mult *= 256;
        }
        
        return(Math.abs(sum) % M);
    }
    
    //The toString method & its helper that will be called if we need to print the hash table
    public String toString() {
        return toStringHelper(T);
    }
    //the helper method for toString that returns the string representation of the has table 
    private String toStringHelper(Node[] T) {
        String s = "";
        for (int i = 0; i<T.length; ++i) {
            if (T[i] != null)
                s += T[i].term  + " " ;//+ T[i].termFreq[0] + T[i].termFreq[1] + " ";
            else 
                s += T[i] + " ";
        }
        return s;
    }
    
    
    // bucket node class 
    private class Node{
        String term;
        int[] termFreq = new int[2];       // this gives the term frequency in each of two documents for this term
        Node next; 
        
        //The contructors 
        public Node(String key, Node n) {
            this.term = key;
            this.next = n;
        }
        
        public Node(String key) {
            this.term = key;
            this.next = null;
        }
    }
    
    
    //Insert method & helper
    //insert a term from a document docNum (= 0 or 1) into the table; if the term is not already present, add it
    //to the table with a termFreq of 1 for docNum. If the term IS already there, just increment the appropriate termFreq value. 
    public void insert(String term, int docNum) {
        String[] list = term.split("\\s+");
        for (int i = 0; i<list.length; ++i) {
                String var = list[i];//.toLowerCase();
                   T[hash(var)] = insertHelper(var, docNum, T[hash(var)]);  
        }
    }
    
    //The helper method for insert
    private Node insertHelper(String term, int docNum, Node p) {
        if (p == null) {
            Node r = new Node(term);
            r.termFreq[docNum] = 1;
            return r;
        }
        else if (term.equals(p.term)) {
            ++p.termFreq[docNum];
            return p;
        }
        else {
            p.next = insertHelper(term, docNum, p.next);
            return p;
        }
    }
    
    
    //This method returns the cosine similarity of the terms for the two documents stored in this table; 
    public double cosineSimilarity() { 
        return cosineHelper(T);
    }  
    
    //helper to calculate the cosine
    private double cosineHelper(Node[] T) {
        double numerator = 0.0;
        double countA = 0.0;
        double countB = 0.0;
        for (int i = 0; i<T.length; ++i) {
            if (T[i] != null) {
                //System.out.println(T[i].term + "    " + T[i].termFreq[0] + "    " + T[i].termFreq[1]);
                numerator += (T[i].termFreq[0] * T[i].termFreq[1]);
                countA += Math.pow(T[i].termFreq[0],2);
                countB += Math.pow(T[i].termFreq[1],2);
            }
        }
        return (numerator/(Math.sqrt(countA) * Math.sqrt(countB)));
    }
    
    
//    
//    private boolean member(String term, String[] A) {
//        for (int i = 0; i<A.length; ++i) {
//            if (term.equals(A[i]))
//                return true;
//        }
//        return false;
//    }
    
    
    public static void main(String[] args) {
        
        TermFrequencyTable FT = new TermFrequencyTable();
        TermFrequencyTable FT2 = new TermFrequencyTable();
        TermFrequencyTable FT3 = new TermFrequencyTable();
        TermFrequencyTable FT4 = new TermFrequencyTable();
        
        System.out.println("Inserting A B");
        FT.insert("A B", 0); 
        System.out.println("Inserting A A B B");
        FT.insert("A A B B", 1); 
        System.out.println("Cosine Similarity: Should be 1.0");
        System.out.println(FT.cosineSimilarity());
        System.out.println();
        
        System.out.println("Inserting A B");
        FT2.insert("A B", 0); 
        System.out.println("Inserting C D");
        FT2.insert("C D", 1); 
        System.out.println("Cosine Similarity: Should be 0.0");
        System.out.println(FT2.cosineSimilarity());
        System.out.println();
        
        System.out.println("Inserting CS112 HW10");
        FT3.insert("CS112 HW10", 0);
        System.out.println("Inserting CS112 HW10 HW10");
        FT3.insert("CS112 HW10 HW10", 1);
        System.out.println("Cosine Similarity: Should be 0.9487");
        System.out.println(FT3.cosineSimilarity());
        System.out.println();
        
        //FT4.insert("dogs cats", 0);
        //FT4.insert("A pet is a domesticated animal that lives with people, but is not forced to  work and is not eaten, in most instances. In most cases, a pet is kept to  entertain people or for companionship Some pets such as dogs and cats are  placed in an animal shelter if there is no one willing to take care of it. If  no one adopts it or the pet is too old/sick the pet may be killed  dogs, cats, fish, birds are the most common pets in North America horses, elephants, oxen, and donkeys are usually made to work, so they are not usually called pets. Some dogs also do work for people, and it was once common for some birds (like falcons and carrier pigeons) to work for humans.  Rodents are also very popular pets. The most common are guinea pigs, rabbits, hamsters (especially Syrians and Dwarfs), mice and rats.  The cap'tchi tribe in Sudan is known for the ritual burning of domesticated animals that are considered too sacred to eat.", 1); 
        //System.out.println("CosineSim:  " + FT4.cosineSimilarity());
        
        //FT.insert("the man with the hat ran up to the man with the dog", 0);
        //FT.insert("a man with a hat approached a dog and a man", 1);
        
        //System.out.println("The Hash Table");
        //System.out.println(FT);

    }
    
}