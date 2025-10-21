// package home.exercise.java_programming_demo;

// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.List;
// import java.util.Set;

// public class Vowels {
//     private static final Set<Character> VOWELS = Set.of('a', 'e', 'i', 'o', 'u');

//     public boolean doesAliceWin(String s) {
//         for (int i = 0; i < s.length(); i++) {
//             if (VOWELS.contains(s.charAt(i))) {
//                 return true;
//             }
//         }
//         return false;
//     }

//     public static String sortVowels(String s) {
//         // // this has a very big time complexity, for larger inputs it will take a lot of time O(n^3)
//         // for (int i = 0; i < s.length(); i++) {
//         //     if (VOWELS.contains(s.toLowerCase().charAt(i))) {
//         //         char firstVowel = s.charAt(i);
//         //         for (int j = i + 1; j < s.length(); j++) {
//         //             if (firstVowel != s.charAt(j)) {
//         //                 char tempVowel = s.charAt(j);
//         //                 if (VOWELS.contains(s.toLowerCase().charAt(j)) && firstVowel > tempVowel) {
//         //                     System.out.println(tempVowel + " " + firstVowel);
//         //                     char[] charArray = s.toCharArray();
//         //                     charArray[i] = tempVowel;
//         //                     charArray[j] = firstVowel;
//         //                     firstVowel = tempVowel;
//         //                     s = new String(charArray);
//         //                 }
//         //             }
//         //         }
//         //     }
//         // }
//         // return s;
//         List<Integer> vowelIndexesInString = new ArrayList<>();
//         List<Character> vowelsInString = new ArrayList<>();
//         for (int i = 0; i < s.length(); i++) {
//             if (VOWELS.contains(s.toLowerCase().charAt(i))) {
//                 vowelIndexesInString.add(i);
//                 vowelsInString.add(s.charAt(i));
//             }
//         }
//         Collections.sort(vowelsInString);
//         for (int i = 0; i < vowelIndexesInString.size(); i++) {
//             s = s.substring(0, vowelIndexesInString.get(i)) + vowelsInString.get(i) + s.substring(vowelIndexesInString.get(i) + 1);
//         }
//         return s;
//     }

//     public static void main(String[] args) {
//         String s = "bbcd";
//         boolean aliceWins = s.chars().anyMatch(
//                 ch -> "aeiou".chars().anyMatch(ch1 -> ch1 == ch));
//         System.out.println(aliceWins);
//         System.out.println(sortVowels("PineApple"));
//     }
// }
