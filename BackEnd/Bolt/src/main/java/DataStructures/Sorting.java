package DataStructures;

import java.util.*;

public class Sorting {

    public static List<String> sortByValue(java.util.HashMap<String, Double> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Double> > list =
                new LinkedList<Map.Entry<String, Double> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2)
            {
                return (o2.getValue().compareTo(o1.getValue()));
            }
        });

        // put data from sorted list to hashmap
        List<String> strings = new ArrayList<>();
        for (Map.Entry<String, Double> element : list) {
            strings.add(element.getKey());
        }
        return strings;
    }
}
