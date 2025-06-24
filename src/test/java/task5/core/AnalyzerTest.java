package task5.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class AnalyzerTest {

    private final Analyzer analyzer = new Analyzer();

    @Test
    @DisplayName("Should identify unused selectors and then remove them on fix")
    void shouldIdentifyAndRemoveUnusedSelectors(@TempDir Path tempDir) throws IOException {
        Path jsxDir = Files.createDirectory(tempDir.resolve("jsx"));
        Path cssDir = Files.createDirectory(tempDir.resolve("css"));

        String jsxContent = "<div className=\"used-class\" id=\"used-id\"></div>";
        Files.writeString(jsxDir.resolve("component.jsx"), jsxContent);

        String cssContent = ".used-class { color: green; }\n" +
                            "#used-id { font-weight: bold; }\n" +
                            ".unused-class { color: red; }\n" +
                            "#unused-id { display: none; }";
        Path cssFile = cssDir.resolve("styles.css");
        Files.writeString(cssFile, cssContent);

        Set<String> unusedSelectors = analyzer.analyze(jsxDir, cssDir);

        assertEquals(2, unusedSelectors.size());
        assertTrue(unusedSelectors.contains(".unused-class"));
        assertTrue(unusedSelectors.contains("#unused-id"));

        analyzer.fixCss(cssDir, unusedSelectors);

        String fixedCssContent = Files.readString(cssFile);
        assertTrue(fixedCssContent.contains(".used-class"));
        assertTrue(fixedCssContent.contains("#used-id"));
        assertFalse(fixedCssContent.contains(".unused-class"));
        assertFalse(fixedCssContent.contains("#unused-id"));
    }
}