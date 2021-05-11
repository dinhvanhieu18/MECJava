package src.object;

import src.helper.Config;
import src.helper.Utils;

public class GnbSimulator extends Object {
    public void sendToCar(CarSimulator car, Message message, double currentTime, Network network) {
        message.indexCar.add(car.id);

        simulateTranferTime(car.preReceiveFromGnb, Config.gnbCarMeanTranfer, message);
        message.locations.add(0);
        car.preReceiveFromGnb = message.currentTime;

        addToNextPosition(car, message, currentTime, network);
    }

    public void process(Message message, double currentTime, Network network) {
        simulateProcessTime(Config.gnbProcessPerSecond, message);
        addToNextPosition(this, message, currentTime, network);
    }

    public void working(Message message, double currentTime, Network network) {
        if (message.isDone) {
            CarSimulator startCar = network.carList.get(message.indexCar.get(0));
            if (startCar.getPosition(currentTime) > Config.roadLength) {
                message.isDrop = true;
                network.output.add(message);
                if (startCar.optimizer != null) {
                    Utils.update(message, network);
                }
            }
            else {
                sendToCar(startCar, message, currentTime, network);
            }
        }
        else {
            process(message, currentTime, network);
            numTask -= 1;
            sumSize -= message.size;
            sumCpuCycle -= message.cpuCycle;
        }
    }
}
