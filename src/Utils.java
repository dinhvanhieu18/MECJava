package src;

import java.util.Random;
import src.object.Object;
import src.object.RsuSimulator;
import src.optimizer.Mab;
import src.optimizer.Optimizer;
import src.object.CarSimulator;
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

    public static Optimizer getOptimizer(String agentName, int nStates, int nActions) {
        Optimizer optimizer = null;
        if (Config.optimizer.equals("MAB")) {
            optimizer = new Mab(agentName, nStates, nActions);
        }
        return optimizer;
    }

    public static void update(Message message, Network network) {
        double delay , delayForCar, delayForRsu;
        delay = delayForCar = delayForRsu = message.currentTime - message.startTime;
        int carId = message.indexCar.get(0);
        CarSimulator car = network.carList.get(carId);
        RsuSimulator rsu = car.neighborRsu;
        if (message.isDrop) {
            delayForCar = Math.max(delay, car.maxDelay);
            delayForRsu = Math.max(delay, rsu.maxDelay);
        }
        car.maxDelay = Math.max(car.maxDelay, delayForCar);
        car.optimizer.updateReward(message, delayForCar);
        int typeMessage;
        if (message.indexRsu.size() == 0) {
            typeMessage = 1;
        }
        else {
            if (message.locations.contains(2)) {
                typeMessage = 3;
            }
            else typeMessage = 2;
            if (message.indexRsu.get(0) == rsu.id) {
                rsu.maxDelay = Math.max(rsu.maxDelay, delayForRsu);
                rsu.optimizer.updateReward(message, delayForRsu);
            } 
        }

        // Update meanDelay
        double a = Config.decayRateMean;
        if (typeMessage == 1) {
            car.meanDelay = car.meanDelay > 0 ? a * car.meanDelay + (1-a) * delayForCar : delayForCar;
            car.meanDelaySendToGnb = car.meanDelaySendToGnb > 0 ? a * car.meanDelaySendToGnb + (1-a) * delayForCar : delayForCar;
        }
        else {
            car.meanDelaySendToRsu = car.meanDelaySendToRsu > 0 ? a * car.meanDelaySendToRsu + (1-a) * delayForRsu : delayForRsu;
            if (message.indexRsu.get(0) != rsu.id) {
                return;
            }
            rsu.meanDelay = rsu.meanDelay > 0 ? a * rsu.meanDelay + (1-a) * delayForRsu : delayForRsu;
            if (typeMessage == 2) {
                rsu.meanDelayProcess = rsu.meanDelayProcess > 0 ? a * rsu.meanDelayProcess + (1-a) * delayForRsu : delayForRsu;
            }
            else {
                rsu.meanDelaySendToGnb = rsu.meanDelaySendToGnb > 0 ? a * rsu.meanDelaySendToGnb + (1-a) * delayForRsu : delayForRsu;
            }
        }
    }
}
