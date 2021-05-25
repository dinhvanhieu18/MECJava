package src.object;

import src.helper.Config;
import src.helper.Utils;

public class GnbSimulator extends Object {
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
                sendToCar(startCar, message, Config.gnbCarMeanTranfer, currentTime, network);
            }
        }
        else {
            process(message, Config.gnbProcessPerSecond, currentTime, network);
            numTask -= 1;
            sumSize -= message.size;
            sumCpuCycle -= message.cpuCycle;
        }
    }
}
