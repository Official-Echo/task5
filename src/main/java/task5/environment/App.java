package task5.environment;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import task5.core.Analyzer;

@Command(name = "task5", mixinStandardHelpOptions = true, version = "1.0",
        description = "Finds and optionally removes unused CSS selectors by analyzing JSX and CSS files.")
public class App implements Callable<Integer> {

    @Parameters(index = "0", description = "The directory containing your JSX files.")
    private Path jsxDir;

    @Parameters(index = "1", description = "The directory containing your CSS files.")
    private Path cssDir;

    @Option(names = {"--fix"}, description = "If specified, unused CSS rules that do not contain used ones will be removed from the files.")
    private boolean fix;

    @Override
    public Integer call() throws IOException {
        Analyzer analyzer = new Analyzer();
        Set<String> unusedSelectors = analyzer.analyze(jsxDir, cssDir);

        if (fix) {
            if (unusedSelectors.isEmpty()) {
                System.out.println("No unused CSS selectors to remove.");
            } else {
                analyzer.fixCss(cssDir, unusedSelectors);
                System.out.println("Unused CSS selectors have been removed.");
            }
        } else {
            if (unusedSelectors.isEmpty()) {
                System.out.println("No unused CSS selectors found.");
            } else {
                System.out.println("Unused CSS selectors found:");
                unusedSelectors.forEach(System.out::println);
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}