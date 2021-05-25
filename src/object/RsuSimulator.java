package src.object;

import src.helper.Config;
import src.helper.Utils;
import src.helper.Utils.ResGetAction;
import src.objectMethod.RsuMethod;
import src.optimizer.Optimizer;

public class RsuSimulator extends Object{
    public int id;
    public double xcord;
    public double ycord;
    public double zcord;
    public Optimizer optimizer;

    public RsuSimulator(int id, double xcord, double ycord, double zcord) {
        this.id = id;
        this.xcord = xcord;
        this.ycord = ycord;
        this.zcord = zcord;
        this.optimizer = Utils.getOptimizer("rsu_"+id, Config.nStatesRsu, Config.nActionsRsu);
    }

    public void working(Message message, double currentTime, Network network) {
        if (message.isDone) {
            int rsuId = message.indexRsu.get(0);
            if (rsuId != this.id) {
                sendToRsu(network.rsuList.get(rsuId), message, Config.rsuRsuMeanTranfer, currentTime, network);
                return;
            }
            CarSimulator startCar = network.carList.get(message.indexCar.get(0));
            if (startCar.getPosition(currentTime) > Config.roadLength || (
                distanceToCar(startCar, currentTime) > Config.rsuCoverRadius)
            ){
                message.isDrop = true;
                network.output.add(message);
                if (this.optimizer != null) {
                    Utils.update(message, network);
                }
            }
            else {
                sendToCar(startCar, message, Config.rsuCarMeanTranfer, currentTime, network);
            }
        }
        else {
            ResGetAction resGetAction = RsuMethod.getAction(this, message, currentTime, network);
            if (resGetAction.action == 1) {
                sendToRsu((RsuSimulator)resGetAction.nextLocation, message, Config.rsuRsuMeanTranfer, currentTime, network);
            }
            else if (resGetAction.action == 2) {
                sendToGnb((GnbSimulator)resGetAction.nextLocation, message, Config.rsuGnbMeanTranfer, currentTime, network);
            }
            else {
                process(message, Config.rsuProcessPerSecond, currentTime, network);
            }
            numTask -= 1;
            sumSize -= message.size;
            sumCpuCycle -= message.cpuCycle;
            if (resGetAction.action != 3) {
                resGetAction.nextLocation.numTask += 1;
                resGetAction.nextLocation.sumSize += message.size;
                resGetAction.nextLocation.sumCpuCycle += message.cpuCycle;
            }
        }
    }

    public double distanceToCar(CarSimulator car, double currentTime) {
        return RsuMethod.distanceToCar(this, car, currentTime);
    }

    public double distanceToRsu(RsuSimulator rsu) {
        return RsuMethod.distanceToRsu(this, rsu);
    }

    public double[] getState(Message message, Network network) {
        return RsuMethod.getState(this, message, network);
    }
}
