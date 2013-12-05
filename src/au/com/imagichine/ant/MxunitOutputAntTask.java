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

        /**
         * rough outline:
         * - for each XML file in the results directory
         * - parse it and log the test results to the console
         */
        boolean allPassed = true;
        boolean noTestsRun = true;
        for ( File file : dir.listFiles()) {
            if (!file.getName().endsWith("xml")) {
                continue;
            }

            noTestsRun = false;

            Map<String, TestResult> testResults = new HashMap<String, TestResult>();

            try {
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.parse(file);
                XPath xpath = XPathFactory.newInstance().newXPath();
                NodeList testCases = (NodeList)xpath.compile("//testcase").evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < testCases.getLength(); i++) {
                    Node testCase = testCases.item(i);
                    String testClassName = testCase.getAttributes().getNamedItem("classname").getNodeValue();
                    TestResult testResult;
                    if (testResults.containsKey(testClassName)) {
                        testResult = testResults.get(testClassName);
                    } else {
                        testResult = new TestResult();
                        testResult.setName(testClassName);
                        testResults.put(testClassName, testResult);
                    }

                    String testName = testCase.getAttributes().getNamedItem("name").getNodeValue();
                    if (testCase.hasChildNodes()) {
                        String failureMessage = testCase.getFirstChild().getAttributes().getNamedItem("message").getNodeValue();
                        String failureDetailMessage = testCase.getFirstChild().getFirstChild().getNodeValue();
                        testResult.addTest(testName, failureMessage, failureDetailMessage);
                        allPassed = false;
                    } else {
                        testResult.addTest(testName, null, null);
                    }
                }
            } catch(Exception e) {
                System.out.println(e);
            }

            if (testResults.size() == 0) {
                throw new BuildException("No tests were run!");
            }

            StringBuilder testOutput = new StringBuilder();
            for (String key : testResults.keySet()) {
                TestResult result = testResults.get(key);

                testOutput.append(String.format("%s %s: %d tests, %d passed, %d failed\n",
                        getTickCross(result.isPassed()),
                        key,
                        result.getTotalTests(),
                        result.getPasses(),
                        result.getFails()));

                if (!result.isPassed() || showPassingCases) {
                    for (TestCaseResult testCaseResult : result.getTestCases()) {
                        testOutput.append(String.format("\t%s %s", getTickCross(testCaseResult.isPassed()), testCaseResult.getName()));
                        if (!testCaseResult.isPassed()) {
                            testOutput.append(String.format(": %s\n", testCaseResult.getFailureMessage()));
                            testOutput.append("\t\t");
                            testOutput.append(testCaseResult.getFailureDetailMessage());
                        }
                        testOutput.append("\n");
                    }
                    testOutput.append("\n");
                }
            }

            // We cheat and use the Echo task to get UTF-8 encoding
            Echo echo = new Echo();
            echo.setMessage(testOutput.toString());
            echo.setEncoding("utf-8");
            echo.execute();
        }

        if (noTestsRun) {
            throw new BuildException("No tests were run");
        }

        if (!allPassed) {
            throw new BuildException("At least one test failed");
        }
    }

    public String getTickCross(boolean check) {
        return (check ? TICK : CROSS);
    }

}
