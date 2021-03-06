package src.object;

import src.helper.Config;
import src.helper.Utils;

import java.util.PriorityQueue;

public class Object {
    public PriorityQueue<Message> waitList = new PriorityQueue<>();
    public double preProcess = 0.0;
    public double preReceiveFromCar = 0.0;
    public double preReceiveFromRsu = 0.0;
    public double preReceiveFromGnb = 0.0;
    public double maxDelay = 0.0;
    public double meanDelay = 0.0;
    public double meanDelayProcess = 0.0;
    public double meanDelaySendToCar = 0.0;
    public double meanDelaySendToRsu = 0.0;
    public double meanDelaySendToGnb = 0.0;
    public int cnt = 0;
    public int cntProcess = 0;
    public int cntSendToCar = 0;
    public int cntSendToRsu = 0;
    public int cntSendToGnb = 0;
    public int cntDrop = 0;
    public int numTask = 0;
    public double sumSize = 0.0;
    public double sumCpuCycle = 0.0;

    public void collectMessage(double currentTime, Network network) {
        while (!waitList.isEmpty()) {
            Message mes = waitList.peek();
            if (mes.currentTime > currentTime + Config.cycleTime) {
                break;
            }
            else {
                network.queue.add(waitList.poll());
            }
        }
    }

    public void simulateTranferTime(double meanTranfer, Message message) {
        message.sendTime.add(message.currentTime);

        double tranferTime = Utils.getNext(1.0 / meanTranfer) * message.size;

        message.currentTime += tranferTime;
    }

    public double simulateReceiveTime(double preReceive, Message message) {
        double receiveTime = Math.max(preReceive, message.currentTime);

        message.receiveTime.add(receiveTime);

        message.currentTime = receiveTime;

        return receiveTime;
    }

    public void simulateProcessTime(double processPerSecond, Message message) {
        double selectedTime = Math.max(preProcess, message.currentTime);
        double processTime = Utils.getNext(processPerSecond) * message.cpuCycle;
        
        double processedTime = processTime + selectedTime;

        message.currentTime = processedTime;
        message.isDone = true;

        this.preProcess = processedTime;
    }

    public void sendToCar(CarSimulator car, Message message, double meanTranfer, double currentTime, Network network) {
        message.indexCar.add(car.id);

        simulateTranferTime(meanTranfer, message);
        message.locations.add(0);

        addToNextPosition(car, message, currentTime, network);
    }

    public void sendToRsu(RsuSimulator rsu, Message message, double meanTranfer, double currentTime, Network network) {
        message.indexRsu.add(rsu.id);

        simulateTranferTime(meanTranfer, message);
        message.locations.add(1);

        addToNextPosition(rsu, message, currentTime, network);
    }

    public void sendToGnb(GnbSimulator gnb, Message message, double meanTranfer, double currentTime, Network network) {
        simulateTranferTime(meanTranfer, message);
        message.locations.add(2);

        addToNextPosition(gnb, message, currentTime, network);
    }

    public void receiveFromCar(Message message, double currentTime, Network network) {
        this.preReceiveFromCar = simulateReceiveTime(this.preReceiveFromCar, message);
    
        addToNextPosition(this, message, currentTime, network);
    }

    public void receiveFromRsu(Message message, double currentTime, Network network) {
        this.preReceiveFromRsu = simulateReceiveTime(this.preReceiveFromRsu, message);
    
        addToNextPosition(this, message, currentTime, network);
    }

    public void receiveFromGnb(Message message, double currentTime, Network network) {
        this.preReceiveFromGnb = simulateReceiveTime(this.preReceiveFromGnb, message);
    
        addToNextPosition(this, message, currentTime, network);
    }

    public void receiveMessage(Message message, double currentTime, Network network) {
        int preLocation = message.locations.get(message.locations.size() - 2);
        if (preLocation == 0) {
            receiveFromCar(message, currentTime, network);
        }
        else if (preLocation == 1) {
            receiveFromRsu(message, currentTime, network);
        }
        else {
            receiveFromGnb(message, currentTime, network);
        }
    }

    public void process(Message message, double processPerSecond, double currentTime, Network network) {
        simulateProcessTime(processPerSecond, message);
        addToNextPosition(this, message, currentTime, network);
    }
    
    public void addToNextPosition(Object object, Message message, double currentTime, Network network) {
        if (message.currentTime > currentTime + Config.cycleTime) {
            object.waitList.add(message);
        }
        else {
            network.queue.add(message);
        }
    }

    public double[] getState(Message message, Network network) {
        return new double[0];
    }
}