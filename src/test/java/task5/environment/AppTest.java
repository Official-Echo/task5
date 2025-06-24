package task5.environment;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import picocli.CommandLine;

@Tag("integration")
class AppTest {

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should print unused selectors when --fix is not used")
    void shouldPrintUnusedSelectors(@TempDir Path tempDir) throws IOException {
        Path jsxDir = Files.createDirectory(tempDir.resolve("jsx"));
        Path cssDir = Files.createDirectory(tempDir.resolve("css"));
        Files.writeString(jsxDir.resolve("component.jsx"), "<div className=\"used-class\"></div>");
        Files.writeString(cssDir.resolve("styles.css"), ".used-class { color: green; } .unused-class { color: red; }");

        new CommandLine(new App()).execute(jsxDir.toString(), cssDir.toString());

        assertTrue(outContent.toString().contains("Unused CSS selectors found:"));
        assertTrue(outContent.toString().contains(".unused-class"));
    }

    @Test
    @DisplayName("Should remove unused selectors when --fix is used")
    void shouldRemoveUnusedSelectorsWithFix(@TempDir Path tempDir) throws IOException {
        Path jsxDir = Files.createDirectory(tempDir.resolve("jsx"));
        Path cssDir = Files.createDirectory(tempDir.resolve("css"));
        Files.writeString(jsxDir.resolve("component.jsx"), "<div className=\"used-class\"></div>");
        Path cssFile = cssDir.resolve("styles.css");
        Files.writeString(cssFile, ".used-class { color: green; } .unused-class { color: red; }");

        new CommandLine(new App()).execute(jsxDir.toString(), cssDir.toString(), "--fix");

        String fixedContent = Files.readString(cssFile);
        assertTrue(fixedContent.contains(".used-class"));
        assertFalse(fixedContent.contains(".unused-class"));
        assertTrue(outContent.toString().contains("Unused CSS selectors have been removed."));
    }
}