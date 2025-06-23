package task5.core;

import cz.vutbr.web.css.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class CssParser {

    public CssParser() {
    }


    public Set<String> parseCssFiles(Path cssDir) throws IOException {
        final Set<String> selectors = new HashSet<>();
        try (Stream<Path> paths = Files.walk(cssDir)) {
            paths
                    .filter(path -> path.toString().endsWith(".css"))
                    .forEach(path -> {
                        try {

                            StyleSheet sheet = CSSFactory.parse(path.toUri().toURL(), StandardCharsets.UTF_8.name());
                            if (sheet == null)
                                return;

                            for (Rule<?> rule : sheet) {
                                if (rule instanceof RuleSet ruleSet) {
                                    for (CombinedSelector selector : ruleSet.getSelectors()) {
                                        for (Selector part : selector) {
                                            if (part.getClassName() != null) {
                                                selectors.add("." + part.getClassName());
                                            } else if (part.getIDName() != null) {
                                                selectors.add("#" + part.getIDName());
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (IOException e) {
                            System.err.println("Error reading or parsing file: " + path + ": " + e.getMessage());
                        } catch (CSSException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        return selectors;
    }

    public void removeUnusedSelectors(Path cssDir, Set<String> unusedSelectors) throws IOException {
        try (Stream<Path> paths = Files.walk(cssDir)) {
            paths
                    .filter(path -> path.toString().endsWith(".css"))
                    .forEach(path -> {
                        try {
                            StyleSheet sheet = CSSFactory.parse(path.toUri().toURL(), StandardCharsets.UTF_8.name());

                            var rules = sheet.asList();
                            System.out.println(sheet.size());
                            for (int i = rules.size() - 1; i >= 0; i--) {
                                Rule<?> rule = rules.get(i);
                                if (shouldRemoveRule(rule, unusedSelectors)) {
                                    sheet.remove(i);
                                }
                            }
                            System.out.println("Removed unused rules" + sheet.size());
                            StringBuilder sb = new StringBuilder();
                            for (Rule<?> rule : sheet) {
                                sb.append(rule.toString()).append("\n");
                            }

                            Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);

                        } catch (IOException e) {
                            System.err.println("Error processing or writing file: " + path + ": " + e.getMessage());
                        } catch (CSSException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    private boolean shouldRemoveRule(Rule<?> rule, Set<String> unusedSelectors) {
        if (!(rule instanceof RuleSet ruleSet)) {
            return false;
        }

        if (ruleSet.getSelectors() == null || ruleSet.getSelectors().length == 0) {
            return false;
        }

        return Arrays.stream(ruleSet.getSelectors())
                .allMatch(combinedSelector -> isCombinedSelectorUnused(combinedSelector, unusedSelectors));
    }

    private boolean isCombinedSelectorUnused(CombinedSelector combinedSelector, Set<String> unusedSelectors) {
        boolean hasClassOrId = false;
        for (Selector simpleSelector : combinedSelector) {
            String className = simpleSelector.getClassName();
            if (className != null) {
                hasClassOrId = true;
                if (!unusedSelectors.contains("." + className)) {
                    return false;
                }
            }

            String idName = simpleSelector.getIDName();
            if (idName != null) {
                hasClassOrId = true;
                if (!unusedSelectors.contains("#" + idName)) {
                    return false;
                }
            }
        }
        return hasClassOrId;
    }
}