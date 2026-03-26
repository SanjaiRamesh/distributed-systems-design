package normalzename;

import java.text.Normalizer;
import java.util.*;

public class NormalizeCompanyName {

    private static List<String> suffix =  List.of("inc","incorporated", "llc", "com", "co");
    public static void main(String[] args) {
        List<String> input = List.of(
                "Stripe Inc.",
                "Stripe, Inc",
                "STRIPE INC",
                "Stripe Incorporated",
                "Stripe",
                "Amazon LLC",
                "Amazon.com",
                "Amazon Co"
        );

        Map<String, List<String>> resultSet = new HashMap<>();

        for(String str: input){
            String result = notmalize(str);
            resultSet.computeIfAbsent(result,  k -> new ArrayList<>()).add(str);
        }
        for(Map.Entry<String, List<String>> entry: resultSet.entrySet()){
            System.out.println("Normalized: "+ entry.getKey());
            System.out.println("Original: "+entry.getValue());

        }

    }
    public static String notmalize(String str){

        if(str == null || str.isBlank()){
            return str;
        }
        str = str.toLowerCase();
        str = str.replaceAll("[^a-zA-Z0-9]", " ");
        str = str.replaceAll("\\s+"," ");

        List<String> filtered = new ArrayList<>();
        String[] words = str.split(" ");
        for(String word: words){
            if(!suffix.contains(word)){
                filtered.add(word);
            }
        }
        return String.join(" ", filtered);
    }
}
