package task5.core;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public class Analyzer {
	private final JsxParser jsxParser;
	private final CssParser cssParser;

	public Analyzer() {
		this.jsxParser = new JsxParser();
		this.cssParser = new CssParser();
	}

	public Set<String> analyze(Path jsxDir, Path cssDir) throws IOException {
		Set<String> jsxSelectors = jsxParser.parseJsxFiles(jsxDir);
		Set<String> cssSelectors = cssParser.parseCssFiles(cssDir);
		cssSelectors.removeAll(jsxSelectors);
		return cssSelectors;
	}

	public void fixCss(Path cssDir, Set<String> unusedSelectors) throws IOException {
		cssParser.removeUnusedSelectors(cssDir, unusedSelectors);
	}
}