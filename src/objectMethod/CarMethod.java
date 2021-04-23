package src.objectMethod;

import java.util.Random;

import src.Utils;
import src.Utils.ResGetAction;
import src.Config;
import src.object.CarSimulator;
import src.object.Message;
import src.object.Network;
import src.object.RsuSimulator;

public class CarMethod {
    public static void generateMessage(CarSimulator car, double currentTime, Network network) {
        double preTime = currentTime;
        while (true) {
            double nextTime = preTime + Utils.getNext(Config.numMessagePerSecond);
            if (nextTime > currentTime + Config.cycleTime) {
                return;
            }
            else {
                Message mes = new Message(car.id, nextTime);
                network.queue.add(mes);
                car.numTask ++;
                preTime = nextTime;
            }
        }
    }

    public static double getPosition(CarSimulator car, double currentTime) {
        return Config.carSpeed * (currentTime - car.startTime);
    }

    public static double distanceToCar(CarSimulator car1, CarSimulator car2, double currentTime) {
        return Math.abs(getPosition(car1, currentTime) - getPosition(car2, currentTime));
    }

    public static double distanceToRsu(CarSimulator car, RsuSimulator rsu, double currentTime) {
        double position = getPosition(car, currentTime);
        return Math.sqrt(
            Math.pow(position - rsu.xcord, 2) + Math.pow(rsu.ycord, 2) + Math.pow(rsu.zcord, 2)
        );
    }

    public static ResGetAction getAction(CarSimulator car, Message message, double currentTime, Network network) {
        ResGetAction res;
        Random random = new Random();
        double rand = random.nextDouble();
        int actionByPolicy;
        if (rand < Config.pL) {
            actionByPolicy = 0;
        }
        else {
            actionByPolicy = 1;
        }
        if (actionByPolicy == 0) {
            res = new ResGetAction(2, network.gnb);
        } 
        else {
            res = new ResGetAction(1, car.neighborRsu);
        }
        return res;
    }
}
