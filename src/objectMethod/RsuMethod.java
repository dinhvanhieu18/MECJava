package src.objectMethod;

import java.util.Random;

import src.helper.Config;
import src.helper.Utils.ResGetAction;
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

    public static double[] getState(RsuSimulator rsu, Message message, Network network) {
        double[] res = new double[Config.nStatesRsu];
        res[0] = rsu.meanDelayProcess;
        res[1] = rsu.meanDelaySendToGnb;
        res[2] = rsu.sumCpuCycle / Config.rsuProcessPerSecond;
        res[3] = network.gnb.sumSize * Config.rsuGnbMeanTranfer;
        res[4] = network.gnb.sumCpuCycle / Config.gnbProcessPerSecond;
        res[5] = message.size;
        res[6] = message.cpuCycle;
        return res;
    }

    public static ResGetAction getAction(RsuSimulator rsu, Message message, double currentTime, Network network) {
        ResGetAction res;
        int actionByPolicy = 0;
        if (rsu.optimizer != null) {
            actionByPolicy = rsu.optimizer.getAction(rsu, message, network);
        }
        else {
            Random random = new Random();
            double rand = random.nextDouble();
            
            if (rand < Config.pR) {
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
            res = new ResGetAction(3, rsu);
        }
        return res;
    }
}
