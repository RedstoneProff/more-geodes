package com.github.thedeathlycow.moregeodes.testmod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.TestReporter;

public final class GametestXmlReporter implements TestReporter {
	public static final String REPORT_PROPERTY = "geodes.gametest.report";

	private static final class Case {
		final String name;
		final double seconds;
		final String failure;
		final boolean required;

		Case(String name, double seconds, String failure, boolean required) {
			this.name = name;
			this.seconds = seconds;
			this.failure = failure;
			this.required = required;
		}
	}

	private final String classname = "com.github.thedeathlycow.moregeodes.testmod.MoreGeodesGameTests";
	private final List<Case> cases = new ArrayList<>();
	private final long startNanos = System.nanoTime();

	public GametestXmlReporter() {
	}

	@Override
	public synchronized void onTestSuccess(GameTestInfo info) {
		cases.add(caseOf(info, null));
	}

	@Override
	public synchronized void onTestFailed(GameTestInfo info) {
		Throwable error = info.getError();
		String message = error == null ? "unknown failure"
				: (error.getMessage() == null ? error.toString() : error.getMessage());
		cases.add(caseOf(info, message));
	}

	private Case caseOf(GameTestInfo info, String failure) {
		String method = info.id().getPath();
		return new Case(method, info.getRunTime() / 1000.0, failure, info.isRequired());
	}

	@Override
	public synchronized void finish() {
		String path = System.getProperty(REPORT_PROPERTY);
		if (path == null || path.isEmpty()) {
			return;
		}
		long failures = cases.stream().filter(c -> c.failure != null && c.required).count();
		long skipped = cases.stream().filter(c -> c.failure != null && !c.required).count();
		double seconds = (System.nanoTime() - startNanos) / 1.0e9;

		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xml.append("<testsuite name=\"geodes gametests\" tests=\"").append(cases.size())
			.append("\" skipped=\"").append(skipped)
			.append("\" failures=\"").append(failures)
			.append("\" errors=\"0\" time=\"").append(seconds).append("\">\n");
		for (Case c : cases) {
			xml.append("  <testcase name=\"").append(escape(c.name))
				.append("\" classname=\"").append(escape(classname))
				.append("\" time=\"").append(c.seconds).append("\"");
			if (c.failure == null) {
				xml.append("/>\n");
			} else {
				xml.append(">\n    <").append(c.required ? "failure" : "skipped")
					.append(" message=\"").append(escape(c.failure)).append("\"/>\n  </testcase>\n");
			}
		}
		xml.append("</testsuite>\n");

		try {
			Path target = Paths.get(path);
			if (target.getParent() != null) {
				Files.createDirectories(target.getParent());
			}
			Files.write(target, xml.toString().getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new IllegalStateException("Could not write the GameTest report to " + path, e);
		}
	}

	private static String escape(String s) {
		StringBuilder out = new StringBuilder(s.length());
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case '&' -> out.append("&amp;");
				case '<' -> out.append("&lt;");
				case '>' -> out.append("&gt;");
				case '"' -> out.append("&quot;");
				case '\n' -> out.append("&#10;");
				case '\r' -> out.append("&#13;");
				default -> out.append(c);
			}
		}
		return out.toString();
	}
}
