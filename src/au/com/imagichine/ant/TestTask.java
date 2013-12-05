package au.com.imagichine.ant;

/**
 * Created with IntelliJ IDEA.
 * User: marcin.szczepanski
 * Date: 28/11/2013
 * Time: 7:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestTask {
    public static void main(String[] args) {
        MxunitOutputAntTask task = new MxunitOutputAntTask();
        task.setResultsDirectory(args[0]);
        task.execute();
    }
}
