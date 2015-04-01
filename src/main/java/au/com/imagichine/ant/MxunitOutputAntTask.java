package au.com.imagichine.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: marcin.szczepanski
 * Date: 28/11/2013
 * Time: 7:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class MxunitOutputAntTask extends Task {

    public static String TICK = "✓";
    public static String CROSS = "✕";

    public String getResultsDirectory() {
        return resultsDirectory;
    }

    public void setResultsDirectory(String resultsDirectory) {
        this.resultsDirectory = resultsDirectory;
    }

    private String resultsDirectory;
    private boolean showPassingCases = false;

    public MxunitOutputAntTask() {
    }

    public void execute() throws BuildException {
        File dir = new File(getResultsDirectory());

        if (!dir.exists()) {
            throw new BuildException("Results directory does not exist: " + getResultsDirectory());
        }

        /**
         * rough outline:
         * - for each XML file in the results directory
         * - parse it and log the test results to the console
         */


        TestSuiteResult testResults = new TestSuiteResult();
        processDirectory(dir, testResults);

        if (testResults.getTestCount() == 0) {
            throw new BuildException("No tests were run");
        }

        String testOutput = testResultsToString(testResults);

        // We cheat and use the Echo task to get UTF-8 encoding
        Echo echo = new Echo();
        echo.setMessage(testOutput);
        echo.setEncoding("utf-8");
        echo.execute();

        if (testResults.getFailCount() > 0) {
            throw new BuildException("At least one test failed");
        }

    }

    public String getTickCross(boolean check) {
        return (check ? TICK : CROSS);
    }

    private String testResultsToString(TestSuiteResult testResults) {
        StringBuilder testOutput = new StringBuilder();

        for (Map.Entry<String, TestResult> entry : testResults.getTestResults()) {
            TestResult result = entry.getValue();

            testOutput.append(String.format("%s %s: %d tests, %d passed, %d failed\n",
                    getTickCross(result.isPassed()),
                    entry.getKey(),
                    result.getTotalTests(),
                    result.getPasses(),
                    result.getFails()));

            if (!result.isPassed() || showPassingCases) {
                for (TestCaseResult testCaseResult : result.getTestCases()) {
                    testOutput.append(String.format("  %s %s", getTickCross(testCaseResult.isPassed()), testCaseResult.getName()));
                    if (!testCaseResult.isPassed()) {
                        testOutput.append(String.format(": %s\n", testCaseResult.getFailureMessage()));
                        testOutput.append("    ");
                        testOutput.append(testCaseResult.getFailureDetailMessage());
                    }
                    testOutput.append("\n");
                }
                testOutput.append("\n");
            }
        }

        testOutput.insert(0, String.format("Total tests: %d (%d failures)\n", testResults.getTestCount(), testResults.getFailCount()));

        return testOutput.toString();
    }

    private void processDirectory(File dir, TestSuiteResult testResults) {
        for ( File file : dir.listFiles()) {
            if (!file.getName().endsWith("xml")) {
                continue;
            }

            try {
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.parse(file);

                Node testSuite = doc.getFirstChild();
                //tests += Integer.parseInt(testSuite.getAttributes().getNamedItem("tests").getNodeValue());
                //failures += Integer.parseInt(testSuite.getAttributes().getNamedItem("failures").getNodeValue());

                XPath xpath = XPathFactory.newInstance().newXPath();
                NodeList testCases = (NodeList)xpath.compile("//testcase").evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < testCases.getLength(); i++) {
                    Node testCase = testCases.item(i);
                    String testClassName = testCase.getAttributes().getNamedItem("classname").getNodeValue();

                    String testName = testCase.getAttributes().getNamedItem("name").getNodeValue();
                    if (testCase.hasChildNodes()) {
                        String failureMessage = testCase.getFirstChild().getAttributes().getNamedItem("message").getNodeValue();
                        String failureDetailMessage = testCase.getFirstChild().getFirstChild().getNodeValue();
                        testResults.addTest(testClassName, testName, failureMessage, failureDetailMessage);
                    } else {
                        testResults.addTest(testClassName, testName);
                    }
                }
            } catch(Exception e) {
                System.out.println(e);
            }
        }
    }



}
