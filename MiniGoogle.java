/*
 * Purpose: A client program that uses the TermFrequencyTable
 * ArticleTable & Article classes, to allow a user to create, modify
 * and interact with a encyclopedia database.
 *
 */

import java.util.*;
import java.util.Arrays;

public class MiniGoogle {
    
    //The 'blacklist' or a list of the most common words in the engish language
    private static final String [] blackList = { "the", "of", "and", "a", "to", "in", "is", 
        "you", "that", "it", "he", "was", "for", "on", "are", "as", "with", 
        "his", "they", "i", "at", "be", "this", "have", "from", "or", "one", 
        "had", "by", "word", "but", "not", "what", "all", "were", "we", "when", 
        "your", "can", "said", "there", "use", "an", "each", "which", "she", 
        "do", "how", "their", "if", "will", "up", "other", "about", "out", "many", 
        "then", "them", "these", "so", "some", "her", "would", "make", "like", 
        "him", "into", "time", "has", "look", "two", "more", "write", "go", "see", 
        "number", "no", "way", "could", "people",  "my", "than", "first", "water", 
        "been", "call", "who", "oil", "its", "now", "find", "long", "down", "day", 
        "did", "get", "come", "made", "may", "part", "about" }; 
     
    //GET LIST OF ARTICLES 
    public static Article[] getArticleList(DatabaseIterator db) {
        
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
    
    //A helper method that replace symbols with whitespace - used in the preprocess method 
    private static String preReplace(String s) {
        if (s.contains("."))
            s = s.replace('.', ' ');
        if (s.contains("?"))
            s = s.replace('?', ' ');
        if (s.contains("!"))
            s = s.replace('!', ' ');
        if (s.contains("("))
            s = s.replace('(', ' ');
        if (s.contains(")"))
            s = s.replace(')', ' ');
        if (s.contains(","))
            s = s.replace(',', ' ');
        if (s.contains("-"))
            s = s.replace('-', ' ');
        if (s.contains("'"))
            s = s.replace('\'', ' ');
        if (s.contains("\""))
            s = s.replace('"', ' ');
        return s;
    }
    
    //This method takes a string, turn it into all lower case, and remove all characters except for letters, digits, and whitespace
    private static String preprocess(String s) {
        String s2 = preReplace(s);
        String x = "";
        for (int i = 0; i<s2.length(); ++i) {
            if (Character.isLetter(s2.charAt(i)) || Character.isDigit(s2.charAt(i)) || Character.isWhitespace(s2.charAt(i))) 
                x += s2.charAt(i);
            else 
                x += " ";
        }
        return x.toLowerCase();
    }
    
    //This method determines if the string s is a member of the blacklist (array at the top of the file)
    private static boolean blacklisted(String s) { 
        for (int i = 0; i<blackList.length; ++i) {
            if (s.equals(blackList[i]))
                return true;
        }
        return false;
    }
    
    // This method takes two strings (e.g., the search phrase and the body of an article) and
    // preprocess each to remove all but letters, digits, and whitespace, and then
    // splits the string to extract each of the individual terms; Then we create a TermFrequencyTable and 
    // insert each of the terms which is NOT in the blacklist into the table with its docNum 
    // (String s being document 0 and String t being document 1); 
    // finally extract the cosine similarity and return it.
    private static double getCosineSimilarity(String s, String t) {
        TermFrequencyTable FT = new TermFrequencyTable();
        String s2 = preprocess(s);
        String t2 = preprocess(t);
        String[] listS = s2.split("\\s+");
        String[] listT = t2.split("\\s+");
        for (int i = 0; i<listS.length; ++i) {
            if (blacklisted(listS[i]) == false)
                FT.insert(listS[i], 0);
        }
        for (int j = 0; j<listT.length; ++j) {
            if (blacklisted(listT[j]) == false)
                FT.insert(listT[j], 1);
        }
        return FT.cosineSimilarity();
    } 
    
    
    //OPTION 1
    //This method adds a new article and is option 1 for the user
    private static void addArticle(Scanner s, ArticleTable A) {
        System.out.println();
        System.out.println("Add an article");
        System.out.println("==============");
        
        System.out.print("Enter article title: ");
        String title = s.nextLine();
        
        System.out.println("You may now enter the body of the article.");
        System.out.println("Press return two times when you are done.");
        
        String body = "";
        String line = "";
        do {
            line = s.nextLine();
            body += line + "\n";
        } while (!line.equals(""));
        
        A.insert(new Article(title, body));
    }
    
    //OPTION 2
    //This method deletes an article and is option 2 for the user
    private static void removeArticle(Scanner s, ArticleTable A) {
        System.out.println();
        System.out.println("Remove an article");
        System.out.println("=================");
        
        System.out.print("Enter article title: ");
        String title = s.nextLine();

        A.delete(title);
    }
    
    //OPTION 3
    //This method searches and returns an article that the user is looking for 
    private static void titleSearch(Scanner s, ArticleTable A) {
        System.out.println();
        System.out.println("Search by article title");
        System.out.println("=======================");
        
        System.out.print("Enter article title: ");
        String title = s.nextLine();
        
        Article a = A.lookup(title);
        if(a != null)
            System.out.println(a);
        else {
            System.out.println("Article not found!"); 
            return; 
        }
        
        System.out.println("Press return when finished reading.");
        s.nextLine();
    } 
    
    //OPTION 4
    //This method takes an ArticleTable and search it for articles most similar to
    //the phrase; return a string response that includes the top three
    //as shown in the sample session shown below
    public static String  phraseSearch(String phrase, ArticleTable T) { 
        int hits = 0;
        int count = 0;
        maxHeap mh = new maxHeap();
        T.reset();
        while(T.hasNext()) {
            Article a = T.next(); 
            double cosineSim = getCosineSimilarity(phrase, a.getBody());
            if (cosineSim > 0.001) {
                ++hits;
                mh.insert(cosineSim, a);
            }
        }
        if (mh.size() == 0)
            System.out.println("There are no matching articles.");
        else {
            for (int i = 1; i<=hits; ++i) {
                ++count;
                Article ex1 = mh.getMax();
                System.out.println("Match " + i + " with cosine similarity of: " + MiniGoogle.getCosineSimilarity(phrase, ex1.getBody()));
                System.out.println(ex1.getTitle());
                System.out.println(ex1.getBody());
                System.out.println();
                if (count >= 3)
                    break;
            }
        }
        return "";
    }
    
    public static void main(String[] args) {
        
        MiniGoogle MG = new MiniGoogle();
//        System.out.println("Testing preprocess: Should be n  mp   0");
//        System.out.println(MG.preprocess("N!@#mp   0"));
//        System.out.println("Testing preprocess: Should be   meat eating       hey      job");
//        System.out.println(MG.preprocess("(meat-eating)     \"hey\"    'job' "));
//        System.out.println("Testing blacklisted: Should be true");
//        System.out.println(MG.blacklisted("these"));
//        System.out.println("Testing blacklisted: Should be false");
//        System.out.println(MG.blacklisted("Boston"));
//        System.out.println();
//        System.out.println("Testing getCosineSimilarity: ");
//        System.out.println("Should be 1.0:");
//        System.out.println(MG.getCosineSimilarity("A B", "A A B B"));
//        System.out.println("Should be 0.0:");
//        System.out.println(MG.getCosineSimilarity("A B", "C D"));
//        System.out.println("Should be 0.9486832980505138:");
//        System.out.println(MG.getCosineSimilarity("CS112 HW10", "CS112 HW10 HW10"));
//        System.out.println();

        
        Scanner user = new Scanner(System.in);
        
        String dbPath = "articles/";
        DatabaseIterator db = setupDatabase(dbPath);
        
        ArticleTable AT = new ArticleTable(); 
        Article[] A = getArticleList(db);
        AT.initialize(A);
        
        System.out.println("Read " + db.getNumArticles() + 
                           " articles from disk.");
        System.out.println("Created in-memory hash table of articles.");
        
        //METHOD TO PRINT OUT EVERY ARTICLE TITLE 
//        System.out.println("Master List:");
//        AT.reset();
//        while(AT.hasNext()) {
//            Article a = AT.next(); 
//            System.out.println(a.getTitle());
//        }
//        ATM.chainAlong();
        
        int choice = -1;
        do {
            System.out.println();
            System.out.println("Welcome to Mini-Google!");
            System.out.println("=====================");
            System.out.println("Make a selection from the " +
                               "following options:");
            System.out.println();
            System.out.println("    1. add a new article");
            System.out.println("    2. remove an article");
            System.out.println("    3. search by article title");
            System.out.println("    4. Search by phrase (list of keywords)");
            System.out.println();
            System.out.print("Enter a selection (1-4, or 0 to quit): ");
            
            
            choice = user.nextInt();
            user.nextLine();
            
            switch (choice) {
                case 0:
                    System.out.println("Bye!");
                    return;
                    
                case 1:
                    addArticle(user, AT);
                    break;
                    
                case 2:
                    removeArticle(user, AT);
                    break;
                    
                case 3:
                    titleSearch(user, AT);
                    break;
                    
                case 4:
                    phraseSearch(user.nextLine(), AT);
                    break;          
                    
                    
                default:
                    break;
            }
            
            choice = -1;
            
        } while (choice < 0 || choice > 4); 
        
    } //for main
    
} // for class
