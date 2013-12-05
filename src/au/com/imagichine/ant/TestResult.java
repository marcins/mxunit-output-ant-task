package au.com.imagichine.ant;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: marcin.szczepanski
 * Date: 5/12/2013
 * Time: 5:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestResult {

    public TestResult() {
        testCases = new ArrayList<TestCaseResult>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPasses() {
        return passes;
    }

    public void setPasses(int passes) {
        this.passes = passes;
    }

    public int getFails() {
        return fails;
    }

    public void setFails(int fails) {
        this.fails = fails;
    }

    public ArrayList<TestCaseResult> getTestCases() {
        return testCases;
    }

    public void setTestCases(ArrayList<TestCaseResult> testCases) {
        this.testCases = testCases;
    }

    private String name;
    private int passes;
    private int fails;
    private ArrayList<TestCaseResult> testCases;

    public void addTest(String name, String failureMessage, String failureDetailMessage) {
        TestCaseResult result = new TestCaseResult(name, failureMessage, failureDetailMessage);
        testCases.add(result);
        if (failureMessage != null) {
            fails++;
        } else {
            passes++;
        }
    }

    public int getTotalTests() {
        return passes + fails;
    }

    public boolean isPassed() {
        return fails == 0;
    }
}
