package src;

import java.util.Random;
import src.object.Object;
import src.object.Message;
import src.object.Network;

public class Utils {
    public static double getNext(double x) {
        Random random = new Random();
        return - Math.log(1.0 - random.nextDouble()) / x;
    }

    public static class ResGetAction {
        public int action;
        public Object nextLocation;
        public ResGetAction(int action, Object nextLocation) {
            this.action = action;
            this.nextLocation = nextLocation;
        }
    }

    public static void update(Message message, Network network) {

    }
}
