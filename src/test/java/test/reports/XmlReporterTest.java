package test.reports;

import org.testng.TestNG;
import org.testng.annotations.Test;
import org.testng.reporters.RuntimeBehavior;
import org.w3c.dom.Document;
import test.SimpleBaseTest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;
import test.reports.issue2171.TestClassExample;

import static org.assertj.core.api.Assertions.assertThat;

public class XmlReporterTest extends SimpleBaseTest {
    @Test(description = "GITHUB-1566")
    public void testMethod() throws IOException {
        File file = runTest(Issue1566Sample.class);
        boolean flag = false;
        Pattern pattern = Pattern.compile("\\p{Cc}");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (pattern.matcher(line).find()) {
                    flag = true;
                }
            }
        }
        assertThat(flag).as("Should not have found a control character").isFalse();
    }

    @Test(description = "GITHUB1659")
    public void ensureStackTraceHasLineFeedsTest() throws Exception {
        File file = runTest(Issue1659Sample.class);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "//full-stacktrace/text()";
        String data = (String) xPath.compile(expression).evaluate(doc, XPathConstants.STRING);
        data = data.trim();
        assertThat(data.split("\n").length)
                .as("Line feeds and carriage returns should not have been removed")
                .isGreaterThan(1);
    }

    @Test(description = "GITHUB-2171")
    public void ensureCustomisationOfReportIsSupported() throws Exception {
        File file = runTest(TestClassExample.class, "issue_2171.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "//test/class/test-method/file/@path";
        String data = (String) xPath.compile(expression).evaluate(doc, XPathConstants.STRING);
        assertThat(data.trim()).isEqualTo("issue2171.html");
    }

    private static File runTest(Class<?> clazz) {
        return runTest(clazz, RuntimeBehavior.FILE_NAME);
    }

    private static File runTest(Class<?> clazz, String fileName) {
        String suiteName = UUID.randomUUID().toString();
        File fileLocation = createDirInTempDir(suiteName);
        TestNG testng = create(fileLocation.toPath(), clazz);
        testng.setUseDefaultListeners(true);
        testng.run();
        return new File(fileLocation, fileName);

    }
}
