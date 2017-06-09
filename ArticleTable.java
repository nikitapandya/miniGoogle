/*
 * Author: Nikita Pandya
 * Prof. Snyder
 * CS112 - HW10 B.2
 * April 29, 2015
 * Purpose: This program develops a hash-table based version of the DumbList.java code.
 * The progrm inserts, deletes, and looks up articles. Also the program contains iterators 
 *  that provide a way of traversing all the entries in the table.
 *
 */

public class ArticleTable {
    
    //The node class
    public static class Node {
        String key;
        public Article data;
        public Node next;  // pointer to next node in bucket
        public Node next2; // pointer to global list of all nodes
        
        //Constructors 
        public Node(Article data, Node n) {
            this.data = data;
            this.next2 = n;
            this.next = n;
        }
        
        public Node(Article data) {
            this(data, null);
        }
    }
    
    public Node root = null;
    
    //GET LIST OF ARTICLES 
     private static Article[] getArticleList(DatabaseIterator db) {
        
        // count how many articles are in the directory
        int count = db.getNumArticles(); 
        
        // now create array
        Article[] list = new Article[count];
        for(int i = 0; i < count; ++i)
            list[i] = db.next();
        
        return list; 
    }
    
     private static DatabaseIterator setupDatabase(String path) {
        return new DatabaseIterator(path);
    }
    
    //SIZE AND ARRAY OF NODES
    private int size = 2503; //Size of the Hash table
    private Node[] T = new Node[size]; //Array of nodes/hash table
    
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
    
    //The initializer method that inserts all of the articles into the hash table 
    public void initialize(Article[] A) {
        for(int i = 0; i < A.length; ++i) {
            insert(A[i]); 
        }
    }
    
    //INSERT & INSERT HELPER
    // insert a into the table using the title of a as the hash key
    public void insert(Article a)  {                
        if (lookup(a.getTitle()) == null) {
            T[hash(a.getTitle())] = insertHelper(a, T[hash(a.getTitle())]);  
            root = new Node(a, root);              // add new article to head of list
                                                  // linked by next2 pointer 
        }
    } 
    
    //insert helper 
    private Node insertHelper(Article a, Node p) { 
        if (p == null) {
            return new Node(a);
        }
        else {
            p.next = insertHelper(a, p.next);
            return p;
        }
    }
    
    
    //DELETE & DELETE HELPER 
    public void delete(String title) {
        T[hash(title)] = deleteHelper(title, T[hash(title)]); 
        root = deleteHelper(title, root);
    }
    

     //delete helper
    private Node deleteHelper(String title, Node p) {   
        if (p == null)
            return p;
        else if (title.equals(p.data.getTitle())){
            return p.next;
        }
        else {
            p.next = deleteHelper(title, p.next );
            return p;
        }
    }
    
    //LOOKUP & LOOKUP HELPER 
    public Article lookup(String title) {
        Node n = lookupHelper(T[hash(title)],title); 
        if(n != null)
            return n.data; 
        return null; 
    }
    
    //helper for the lookup method
    private Node lookupHelper(Node p, String title) {
        if (p == null){
            return null;
        }
        else if (title.compareTo(p.data.getTitle()) == 0) {
            return p;
        } else 
            return lookupHelper(p.next,title); 
    }
    
    //ITERATORS
    
    //Resets the linked list back to the head
    Node q; //global node q
    public void reset() {      // initialize the iterator       
        q = root;
    }
    
    //This method returns true if there is next node, false otherwise 
    public boolean hasNext() {
        if (q == null) {
            return false;
        }
        else {
            return true;
        }
    }
    
    //Returns the next element in the node
    public Article next() {
        Article temp = q.data;
        q = q.next;
        return temp;
    }
    

    
    public static void main(String[] args) {
        
        String dbPath = "articles/";
        DatabaseIterator db = setupDatabase(dbPath);
        
        ArticleTable AT = new ArticleTable(); 
        Article[] A = getArticleList(db);
        //==AT.initialize(A);
        
        
        System.out.println("TESTING INSERT");
        
        System.out.println("Inserting: " + A[1500].getTitle());
        AT.insert(A[1500]);
        System.out.println("Inserting: " + A[1000].getTitle());
        AT.insert(A[1000]);
        System.out.println("Inserting: " + A[770].getTitle());
        AT.insert(A[770]);
        System.out.println("Inserting: " + A[230].getTitle());
        AT.insert(A[230]);
        System.out.println("Inserting: " + A[900].getTitle());
        AT.insert(A[900]);
        System.out.println("Inserting: " + A[2104].getTitle());
        AT.insert(A[2104]);
        System.out.println();
        
        System.out.println("TESTING LOOKUP");
        System.out.println("Should be Meal");
        System.out.println(AT.lookup("Meal"));
        System.out.println();
        
        System.out.println("Should be Hawaiian Islands");
        System.out.println(AT.lookup("Hawaiian Islands"));
         
        System.out.println("Should be null");
        System.out.println(AT.lookup("IDK?"));
        System.out.println();
        
        System.out.println("Testing Delete");
        AT.delete("Meal");
        System.out.println("Deleting Meal: Should be null");
        System.out.println(AT.lookup("Meal"));
        System.out.println();
        
        AT.delete("Gene");
        System.out.println("Deleting Gene: Should be null");
        System.out.println(AT.lookup("Gene"));
        System.out.println();
        
        //METHOD TO PRINT OUT EVERY ARTICLE TITLE 
       System.out.println("Master List:");
       AT.reset();
       while(AT.hasNext()) {
          Article a = AT.next(); 
          System.out.println(a.getTitle());
       }
        
    }
    
}
