package task5.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("parser")
class JsxParserTest {

    private final JsxParser jsxParser = new JsxParser();

    @Test
    @DisplayName("Should extract single class and ID from JSX content")
    void testSimpleExtraction(@TempDir Path tempDir) throws IOException {
        String content = "<div className=\"my-class\" id=\"my-id\"></div>";
        Path tempFile = tempDir.resolve("test.jsx");
        Files.writeString(tempFile, content);

        Set<String> selectors = jsxParser.parseJsxFiles(tempDir);

        assertEquals(2, selectors.size());
        assertTrue(selectors.contains(".my-class"));
        assertTrue(selectors.contains("#my-id"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "<div className=\"class1 class2\"></div>",
        "<div class=\"class1 class2\"></div>"
    })
    @DisplayName("Should extract multiple space-separated classes")
    void testMultipleClassesExtraction(String content, @TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("test.jsx");
        Files.writeString(tempFile, content);

        Set<String> selectors = jsxParser.parseJsxFiles(tempDir);

        assertEquals(2, selectors.size());
        assertTrue(selectors.contains(".class1"));
        assertTrue(selectors.contains(".class2"));
    }
}