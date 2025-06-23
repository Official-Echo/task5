package task5.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JsxParser {
    private static final Pattern CLASS_PATTERN = Pattern.compile("(?:className|class)=\"([^\"]+)\"");
    private static final Pattern ID_PATTERN = Pattern.compile("id=\"([^\"]+)\"");

    public Set<String> parseJsxFiles(Path jsxDir) throws IOException {
        Set<String> selectors = new HashSet<>();
        try (Stream<Path> stream = Files.walk(jsxDir)) {
            stream
                    .filter(path -> path.toString().endsWith(".jsx"))
                    .forEach(path -> {
                        try {
                            String content = Files.readString(path);
                            extractSelectors(content, selectors);
                        } catch (IOException e) {
                            System.err.println("Error reading file: " + path);
                        }
                    });
        }
        return selectors;
    }

    private void extractSelectors(String content, Set<String> selectors) {
        Matcher classMatcher = CLASS_PATTERN.matcher(content);
        while (classMatcher.find()) {
            String[] classes = classMatcher.group(1).split("\\s+");
            for (String cls : classes) {
                if (!cls.isEmpty()) {
                    selectors.add("." + cls);
                }
            }
        }

        Matcher idMatcher = ID_PATTERN.matcher(content);
        while (idMatcher.find()) {
            selectors.add("#" + idMatcher.group(1));
        }
    }
}