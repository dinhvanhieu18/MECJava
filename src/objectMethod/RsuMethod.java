package src.objectMethod;

import java.util.Random;

import src.Config;
import src.Utils.ResGetAction;
import src.object.CarSimulator;
import src.object.Message;
import src.object.Network;
import src.object.RsuSimulator;

public class RsuMethod {
    public static double distanceToCar(RsuSimulator rsu, CarSimulator car, double currentTime) {
        double position = car.getPosition(currentTime);
        return Math.sqrt(
            Math.pow(position - rsu.xcord, 2) + Math.pow(rsu.ycord, 2) + Math.pow(rsu.zcord, 2)
        );
    }

    public static double distanceToRsu(RsuSimulator rsu1, RsuSimulator rsu2) {
        return Math.sqrt(
            Math.pow(rsu1.xcord - rsu2.xcord, 2) + 
            Math.pow(rsu1.ycord - rsu2.ycord, 2) + 
            Math.pow(rsu1.zcord - rsu2.zcord, 2)
        );
    }

    public static ResGetAction getAction(RsuSimulator rsu, Message message, double currentTime, Network network) {
        ResGetAction res;
        Random random = new Random();
        double rand = random.nextDouble();
        int actionByPolicy;
        if (rand < Config.pR) {
            actionByPolicy = 0;
        }
        else {
            actionByPolicy = 1;
        }
        if (actionByPolicy == 0) {
            res = new ResGetAction(2, network.gnb);
        } 
        else {
            res = new ResGetAction(3, rsu);
        }
        return res;
    }
}
