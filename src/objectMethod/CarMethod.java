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
        // Info message
        double currentTime = message.currentTime;
        double tranferTimeCarToRsu =  message.size * Config.carRsuMeanTranfer;
        double tranferTimeCarToGnb =  message.size * Config.carGnbMeanTranfer;
        double processTimeRsu = message.cpuCycle * Config.rsuProcessPerSecond;
        double processTimeGnb = message.cpuCycle * Config.gnbProcessPerSecond;
        res[0] = message.size;
        res[1] = message.cpuCycle;
        res[2] = tranferTimeCarToRsu;
        res[3] = tranferTimeCarToGnb;
        res[4] = processTimeRsu;
        res[5] = processTimeGnb;
        // Estimate time need process of rsu and gnb
        double timeNeedProcessRsu = car.neighborRsu.sumCpuCycle / Config.rsuProcessPerSecond;
        double timeNeedProcessGnb = network.gnb.sumCpuCycle / Config.gnbProcessPerSecond;
        // Estimate receive time
        double timeRsuReceive = Math.max(currentTime + tranferTimeCarToRsu, car.neighborRsu.preReceiveFromCar);
        double timeGnbReceive = Math.max(currentTime + tranferTimeCarToGnb, network.gnb.preReceiveFromCar);
        // estimate time delay tranfer + receive
        res[6] = timeRsuReceive - currentTime;
        res[7] = timeGnbReceive - currentTime;
        // estimate time delay process
        res[8] = Math.max(car.neighborRsu.preProcess + timeNeedProcessRsu, timeRsuReceive) + processTimeRsu - timeRsuReceive;
        res[9] = Math.max(network.gnb.preProcess + timeNeedProcessGnb, timeGnbReceive) + processTimeGnb - timeGnbReceive;
        
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
