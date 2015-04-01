package au.com.imagichine.ant;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: marcin.szczepanski
 * Date: 6/12/2013
 * Time: 10:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestSuiteResult {
    private int testCount;

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public int getTestCount() {
        return testCount;
    }

    public void setTestCount(int testCount) {
        this.testCount = testCount;
    }

    private int failCount;

    private Map<String, TestResult> testResults;

    public TestSuiteResult() {
        testCount = 0;
        failCount = 0;
        testResults = new  HashMap<String, TestResult>();
    }

    public TestResult getTestResultForClassName(String testClassName) {
        TestResult testResult;
        if (testResults.containsKey(testClassName)) {
            testResult = testResults.get(testClassName);
        } else {
            testResult = new TestResult();
            testResult.setName(testClassName);
            testResults.put(testClassName, testResult);
        }
        return testResult;
    }

    public void addTest(String testClassName, String testName) {
        addTest(testClassName, testName, null, null);
    }

    public void addTest(String testClassName, String testName, String failureMessage, String failureDetailMessage) {
        testCount++;

        if (failureMessage != null) {
            failCount++;
        }

        TestResult testResult = getTestResultForClassName(testClassName);
        testResult.addTest(testName, failureMessage, failureDetailMessage);
    }

    public Set<Map.Entry<String, TestResult>> getTestResults()
    {
        return testResults.entrySet();
    }

}
