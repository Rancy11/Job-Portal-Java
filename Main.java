import controller.JobPortalController;
import view.JobPortalView;

public class Main {
    public static void main(String[] args) {
        JobPortalView view = new JobPortalView();
        JobPortalController controller = new JobPortalController(view);
        controller.start();
    }
}