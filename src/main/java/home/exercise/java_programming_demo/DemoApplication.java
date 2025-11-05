package home.exercise.java_programming_demo;

import org.springframework.boot.SpringApplication;  
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}


















// import java.util.Collection;
// import java.util.Collections;
// import java.util.HashSet;
// import java.util.LinkedList;
// import java.util.List;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
// import java.util.stream.Collectors;


// runHtmlElements("<div><p></div></p>");
	

// private static void runHtmlElements(String htmlTags) {

// 	// Compile patterns for start and end tags
// 	Pattern regexEndMatcher = Pattern.compile("</(\\w+)>");
// 	Matcher endMatch = regexEndMatcher.matcher(htmlTags);

// 	Pattern regexStartMatcher = Pattern.compile("<(\\w+)>");
// 	Matcher startMatch = regexStartMatcher.matcher(htmlTags);

// 	List<String> startAndEndTags = new LinkedList<>();

// 	startMatch.results().forEach(matchResult -> {
// 		startAndEndTags.add(matchResult.group(1));
// 	});
// 	endMatch.results().forEach(matchResult -> {
// 		startAndEndTags.add(matchResult.group(1));
// 	});

// 	startAndEndTags.stream().distinct().map(tag -> {
// 		long count = Collections.frequency(startAndEndTags, tag);
// 		if (count <= 1) {
// 			System.out.println(tag + "  has either an open or closing " + count + " tag");
// 		}
// 		return tag;
// 	}).collect(Collectors.toList());

// }
