package au.com.imagichine.ant;

/**
 * Created with IntelliJ IDEA.
 * User: marcin.szczepanski
 * Date: 5/12/2013
 * Time: 5:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestCaseResult {

    public TestCaseResult(String name, String failureMessage, String failureDetailMessage) {
        this.name = name;
        this.failureMessage = failureMessage;
        this.failureDetailMessage = failureDetailMessage;
    }

    private String name;
    private String failureMessage;
    private String failureDetailMessage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public boolean isPassed() {
        return failureMessage == null;
    }

    public String getFailureDetailMessage() {
        return failureDetailMessage;
    }

    public void setFailureDetailMessage(String failureDetailMessage) {
        this.failureDetailMessage = failureDetailMessage;
    }

}
