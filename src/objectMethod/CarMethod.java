package src.objectMethod;

import java.util.Random;

import src.helper.Config;
import src.helper.Utils;
import src.helper.Utils.ResGetAction;
import src.object.CarSimulator;
import src.object.Message;
import src.object.Network;
import src.object.RsuSimulator;

public class CarMethod {
    public static void generateMessage(CarSimulator car, double currentTime, Network network) {
        double preTime = currentTime;
        while (true) {
            double nextTime = preTime + Utils.getNext(Config.numMessagePerSecond);
            double size = Utils.getNext(Config.messageSize);
            double cpuCycle = Utils.getNext(Config.messageCpuCycle);
            if (nextTime > currentTime + Config.cycleTime) {
                return;
            }
            else {
                Message mes = new Message(car.id, nextTime, size, cpuCycle);
                network.queue.add(mes);
                car.numTask ++;
                car.sumSize += size;
                car.sumCpuCycle += cpuCycle;
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

    public static double[] getState(CarSimulator car, Message message, Network network) {
        double[] res = new double[Config.nStatesCar];
        // res[0] = car.meanDelaySendToRsu;
        // res[1] = car.meanDelaySendToGnb;
        // res[2] = car.neighborRsu.sumSize * Config.carRsuMeanTranfer;
        res[0] = car.neighborRsu.sumCpuCycle / Config.rsuProcessPerSecond;
        // res[4] = network.gnb.sumSize * Config.carGnbMeanTranfer;
        res[1] = network.gnb.sumCpuCycle / Config.gnbProcessPerSecond;
        res[2] = message.size;
        res[3] = message.cpuCycle;
        res[4] = car.neighborRsu.preReceiveFromCar;
        res[5] = car.neighborRsu.preProcess;
        res[6] = network.gnb.preReceiveFromCar;
        res[7] = network.gnb.preProcess;
        return res;
    }

    public static ResGetAction getAction(CarSimulator car, Message message, double currentTime, Network network) {
        ResGetAction res;
        int actionByPolicy = 0;
        if (car.optimizer != null) {
            actionByPolicy = car.optimizer.getAction(car, message, network);
        }
        else {
            Random random = new Random();
            double rand = random.nextDouble();
            
            if (rand < Config.pL) {
                actionByPolicy = 0;
            }
            else {
                actionByPolicy = 1;
            }
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
