package src.object;

import src.Config;
import src.Utils;
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

    public void collectMessage(double currentTime, Network network) {
        // @SuppressWarnings("unchecked") ArrayList<Message> tmp = (ArrayList<Message>)waitList.clone();
        // waitList.clear();
        // ArrayList<Message> res = new ArrayList<>();
        // for (Message mes : tmp) {
        //     if (mes.currentTime > currentTime + Config.cycleTime) {
        //         waitList.add(mes);
        //     }
        //     else {
        //         res.add(mes);
        //     }
        // }
        // return res;
        while (!waitList.isEmpty()) {
            Message mes = waitList.poll();
            if (mes.currentTime > currentTime + Config.cycleTime) {
                break;
            }
            else {
                network.queue.add(mes);
            }
        }
    }

    public void simulateTranferTime(double preReceive, double meanTranfer, Message message) {
        message.sendTime.add(message.currentTime);

        double tranferTime = Utils.getNext(1.0 / meanTranfer) * message.size;
        double selectedTime = Math.max(preReceive, message.currentTime);
        double receiveTime = tranferTime + selectedTime;

        message.receiveTime.add(receiveTime);
        message.currentTime = receiveTime;
    }

    public void simulateProcessTime(double processPerSecond, Message message) {
        double processTime = Utils.getNext(processPerSecond) * message.cpuCycle;
        double selectedTime = Math.max(preProcess, message.currentTime);
        double processedTime = processTime + selectedTime;

        message.currentTime = processedTime;
        message.isDone = true;

        preProcess = processedTime;
    }

    public void addToNextPosition(Object object, Message message, double currentTime, Network network) {
        if (message.currentTime > currentTime + Config.cycleTime) {
            object.waitList.add(message);
        }
        else {
            network.queue.add(message);
        }
    }
}