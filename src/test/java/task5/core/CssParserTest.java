package task5.core;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("analyzer")
class CssParserTest {

	private final CssParser cssParser = new CssParser();

	@Test
	@DisplayName("Should parse selectors from a CSS file")
	void testParseCssFile(@TempDir Path tempDir) throws IOException {
		String cssContent = ".class1 { color: red; } #id1 { font-size: 12px; }";
		Path cssFile = tempDir.resolve("style.css");
		Files.writeString(cssFile, cssContent);

		Assumptions.assumeTrue(Files.isWritable(tempDir), "Skipping test: Temp directory is not writable.");

		Set<String> selectors = cssParser.parseCssFiles(tempDir);

		assertEquals(2, selectors.size());
		assertTrue(selectors.contains(".class1"));
		assertTrue(selectors.contains("#id1"));
		System.out.println("CSS Parsing test completed successfully.");
	}
}