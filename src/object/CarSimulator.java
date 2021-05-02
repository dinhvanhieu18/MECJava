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

    public void sendToCar(CarSimulator car, Message message, double currentTime, Network network) {
        message.indexCar.add(car.id);

        simulateTranferTime(car.preReceiveFromCar, Config.carCarMeanTranfer, message);
        message.locations.add(0);
        car.preReceiveFromCar = message.currentTime;

        addToNextPosition(car, message, currentTime, network);
    }

    public void sendToRsu(RsuSimulator rsu, Message message, double currentTime, Network network) {
        message.indexRsu.add(rsu.id);

        simulateTranferTime(rsu.preReceiveFromCar, Config.carRsuMeanTranfer, message);
        message.locations.add(1);
        rsu.preReceiveFromCar = message.currentTime;

        addToNextPosition(rsu, message, currentTime, network);
    }

    public void sendToGnb(GnbSimulator gnb, Message message, double currentTime, Network network) {
        simulateTranferTime(gnb.preReceiveFromCar, Config.carGnbMeanTranfer, message);
        message.locations.add(2);
        gnb.preReceiveFromCar = message.currentTime;

        addToNextPosition(gnb, message, currentTime, network);
    }

    public void process(Message message, double currentTime, Network network) {
        simulateProcessTime(Config.carProcessPerSecond, message);
        addToNextPosition(this, message, currentTime, network);
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
                sendToCar(startCar, message, currentTime, network);
            }
        }
        else {
            ResGetAction resGetAction = CarMethod.getAction(this, message, currentTime, network);
            if (resGetAction.action == 0) {
                sendToCar((CarSimulator)resGetAction.nextLocation, message, currentTime, network);
            }
            else if (resGetAction.action == 1) {
                sendToRsu((RsuSimulator)resGetAction.nextLocation, message, currentTime, network);
            }
            else if (resGetAction.action == 2) {
                sendToGnb((GnbSimulator)resGetAction.nextLocation, message, currentTime, network);
            }
            else {
                process(message, currentTime, network);
            }
            numTask -= 1;
            if (resGetAction.action != 3) {
                resGetAction.nextLocation.numTask += 1;
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
