package Indexer;

public class Cleaner {

    public Cleaner(){

    }
    public String runCleaner(String page){
        // Removing anything except alphabet characters
        page = page.replaceAll("[^a-zA-Z]", " ");
//        page = page.replaceAll("<style([\\s\\S]+?)</style>", "");
//        page = page.replaceAll("<script([\\s\\S]+?)</script>", "");
//        page = page.replaceAll("<meta[^>]*>", "");
//        page = page.replaceAll("<link[^>]*>", "");
        return page;
    }

}
