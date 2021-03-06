package src.object;

import java.util.ArrayList;

import src.helper.Config;
import src.helper.Utils;
import src.helper.Utils.ResGetAction;
import src.objectMethod.CarMethod;
import src.optimizer.Optimizer;

public class CarSimulator extends Object {
    public int id;
    public double startTime;
    public Optimizer optimizer;
    public ArrayList<CarSimulator> neighborCars = new ArrayList<>();
    public RsuSimulator neighborRsu;

    public CarSimulator(int id, double startTime) {
        this.id = id;
        this.startTime = startTime;
        this.optimizer = Utils.getOptimizer("car_"+id, Config.nStatesCar, Config.nActionsCar);
    }

    public void collectMessage(double currentTime, Network network) {
        super.collectMessage(currentTime, network);
        CarMethod.generateMessage(this, currentTime, network);
    }    

    public void working(Message message, double currentTime, Network network) {
        if (message.isDone) {
            CarSimulator startCar = network.carList.get(message.indexCar.get(0));
            if (startCar.getPosition(currentTime) > Config.roadLength || (
                distanceToCar(startCar, currentTime) > Config.carCoverRadius)
            ){
                message.isDrop = true;
            }
            if (message.isDrop || startCar.id == this.id) {
                network.output.add(message);
                if (this.optimizer != null) {
                    Utils.update(message, network);
                }
            }
            else {
                sendToCar(startCar, message, Config.carCarMeanTranfer, currentTime, network);
            }
        }
        else if (message.sendTime.size() > message.receiveTime.size()) {
            receiveMessage(message, currentTime, network);
        }
        else {
            ResGetAction resGetAction = CarMethod.getAction(this, message, currentTime, network);
            if (resGetAction.action == 0) {
                sendToCar((CarSimulator)resGetAction.nextLocation, message, Config.carCarMeanTranfer, currentTime, network);
            }
            else if (resGetAction.action == 1) {
                sendToRsu((RsuSimulator)resGetAction.nextLocation, message, Config.carRsuMeanTranfer, currentTime, network);
            }
            else if (resGetAction.action == 2) {
                sendToGnb((GnbSimulator)resGetAction.nextLocation, message, Config.carGnbMeanTranfer, currentTime, network);
            }
            else {
                process(message, Config.carProcessPerSecond, currentTime, network);
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

    public double getPosition(double currentTime) {
        return CarMethod.getPosition(this, currentTime);
    }

    public double distanceToCar(CarSimulator car, double currentTime) {
        return CarMethod.distanceToCar(this, car, currentTime);
    }

    public double distanceToRsu(RsuSimulator rsu, double currentTime) {
        return CarMethod.distanceToRsu(this, rsu, currentTime);
    }

    public double[] getState(Message message, Network network) {
        return CarMethod.getState(this, message, network);
    }
}
