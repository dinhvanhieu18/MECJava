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
        // Info message
        double currentTime = message.currentTime;
        double tranferTimeRsuToGnb = message.size * Config.rsuGnbMeanTranfer;
        double tranferTimeGnbToCar = message.size * Config.gnbCarMeanTranfer;
        double processTimeRsu = message.cpuCycle * Config.rsuProcessPerSecond;
        double processTimeGnb = message.cpuCycle * Config.gnbProcessPerSecond;
        res[0] = message.size;
        res[1] = message.cpuCycle;
        res[2] = tranferTimeRsuToGnb;
        res[3] = tranferTimeGnbToCar;
        res[4] = processTimeRsu;
        res[5] = processTimeGnb;
        // Estimate time need process of rsu and gnb
        double timeNeedProcessRsu = rsu.sumCpuCycle / Config.rsuProcessPerSecond;
        double timeNeedProcessGnb = network.gnb.sumCpuCycle / Config.gnbProcessPerSecond;
        // Estimate receive time
        double timeGnbReceive = Math.max(currentTime + tranferTimeRsuToGnb, network.gnb.preReceiveFromCar); 
        // estimate time delay tranfer + receive
        res[6] = timeGnbReceive - currentTime;
        // estimate time delay process
        res[7] = Math.max(rsu.preProcess + timeNeedProcessRsu, currentTime) + processTimeRsu - currentTime;
        res[8] = Math.max(network.gnb.preProcess + timeNeedProcessGnb, timeGnbReceive) + processTimeGnb - timeGnbReceive;
        
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
