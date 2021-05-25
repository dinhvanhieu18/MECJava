package src.optimizer;

import src.helper.Config;
import src.helper.Memory;
import src.helper.NeuralNetwork;
import src.helper.Utils;
import src.object.CarSimulator;
import src.object.Message;
import src.object.Network;
import src.object.Object;
import src.object.RsuSimulator;
import src.optimizerMethod.DqnMethod;

public class Dqn extends Optimizer {
    public double alpha;
    public double gamma;
    public NeuralNetwork onlineModel;
    public NeuralNetwork targetModel;
    public Memory memory;
    public boolean stable = true;
    public double rewardRsu = 0.0;
    public double rewardGnb = 0.0;

    public Dqn(String agentName, int nStates, int nActions) {
        this.agentName = agentName;
        this.nStates = nStates;
        this.nActions = nActions;
        this.policy = DqnMethod.getBehaviorPolicy();
        this.alpha = Config.learningRate;
        this.gamma = Config.discountingFactor;
        this.onlineModel = new NeuralNetwork(nStates, Config.hiddenLayer, nActions, alpha);
        this.targetModel = new NeuralNetwork(nStates, Config.hiddenLayer, nActions, alpha);
        this.memory = new Memory(Config.memoryCapacity);
        this.cnt = 0;
    }
    
    public void updateReward(Message message, double delay) {
        DqnMethod.updateReward(this, message, delay);
    }

    public int getAction(Object object, Message message, Network network) {
        double[] state = object.getState(message, network);
        DqnMethod.updateState(this, message, state);
        double[] allActionValues = onlineModel.predict(state);
        int actionByPolicy;
        // if (Math.abs(allActionValues[0]-allActionValues[1]) < Config.minDelta) {
        //     Random random = new Random();
        //     actionByPolicy = random.nextInt(2);
        // } 
        // else {
        //     actionByPolicy = policy.getAction(allActionValues);
        // }
        if (Math.abs(allActionValues[0]-allActionValues[1]) < Config.minDelta) {
            if (object instanceof CarSimulator) {
                CarSimulator car = (CarSimulator) object;
                double tranferTime = Utils.getNext(1.0 / Config.carRsuMeanTranfer) * message.size;
                double selectedTime = Math.max(car.neighborRsu.preReceiveFromCar, message.currentTime);
                double receiveTime = tranferTime + selectedTime;
    
                double processTime = Utils.getNext(Config.rsuProcessPerSecond) * message.cpuCycle;
                selectedTime = Math.max(car.neighborRsu.preProcess, receiveTime);
                double processedTime = processTime + selectedTime;
    
                tranferTime = Utils.getNext(1.0 / Config.rsuCarMeanTranfer) * message.size;
                selectedTime = Math.max(car.preReceiveFromRsu, processedTime);
                receiveTime = tranferTime + selectedTime;
                double delaySendToRsu = receiveTime - message.currentTime;
                boolean dropRsu = false;
                if (car.getPosition(receiveTime) > Config.roadLength || (
                    car.neighborRsu.distanceToCar(car, receiveTime) > Config.rsuCoverRadius)) {
                        dropRsu = true;
                }
        
    
                tranferTime = Utils.getNext(1.0 / Config.carGnbMeanTranfer) * message.size;
                selectedTime = Math.max(network.gnb.preReceiveFromCar, message.currentTime);
                receiveTime = tranferTime + selectedTime;
    
                processTime = Utils.getNext(Config.gnbProcessPerSecond) * message.cpuCycle;
                selectedTime = Math.max(network.gnb.preProcess, receiveTime);
                processedTime = processTime + selectedTime;
    
                tranferTime = Utils.getNext(1.0 / Config.gnbCarMeanTranfer) * message.size;
                selectedTime = Math.max(car.preReceiveFromGnb, processedTime);
                receiveTime = tranferTime + selectedTime;
                double delaySendToGnb = receiveTime - message.currentTime;
                boolean dropGnb = false;
    
                if (car.getPosition(receiveTime) > Config.roadLength) {
                    dropGnb = true;
                }
    
                if ((!dropRsu && !dropGnb) || (dropRsu && dropGnb)) {
                    actionByPolicy = delaySendToRsu < delaySendToGnb ? 1 : 0;
                }
                else {
                    actionByPolicy = dropRsu ? 0 : 1;
                }
            }
    
            else {
                RsuSimulator rsu = (RsuSimulator) object;
                CarSimulator car = network.carList.get(message.indexCar.get(0));
    
                double processTime = Utils.getNext(Config.rsuProcessPerSecond) * message.cpuCycle;
                double selectedTime = Math.max(rsu.preProcess, message.currentTime);
                double processedTime = processTime + selectedTime;
    
                double tranferTime = Utils.getNext(1.0 / Config.rsuCarMeanTranfer) * message.size;
                selectedTime = Math.max(car.preReceiveFromRsu, processedTime);
                double receiveTime = tranferTime + selectedTime;
                double delayProcess = receiveTime - message.currentTime;
                boolean dropRsu = false;
                if (car.getPosition(receiveTime) > Config.roadLength || (
                    car.neighborRsu.distanceToCar(car, receiveTime) > Config.rsuCoverRadius)) {
                        dropRsu = true;
                }
    
    
                tranferTime = Utils.getNext(1.0 / Config.rsuGnbMeanTranfer) * message.size;
                selectedTime = Math.max(network.gnb.preReceiveFromRsu, message.currentTime);
                receiveTime = tranferTime + selectedTime;
    
                processTime = Utils.getNext(Config.gnbProcessPerSecond) * message.cpuCycle;
                selectedTime = Math.max(network.gnb.preProcess, receiveTime);
                processedTime = processTime + selectedTime;
    
                tranferTime = Utils.getNext(1.0 / Config.gnbCarMeanTranfer) * message.size;
                selectedTime = Math.max(car.preReceiveFromGnb, processedTime);
                receiveTime = tranferTime + selectedTime;
                double delaySendToGnb = receiveTime - message.currentTime;
                boolean dropGnb = false;
    
                if (car.getPosition(receiveTime) > Config.roadLength) {
                    dropGnb = true;
                };
    
                if ((!dropRsu && !dropGnb) || (dropRsu && dropGnb)) {
                    actionByPolicy = delayProcess < delaySendToGnb ? 1 : 0;
                }
                else {
                    actionByPolicy = dropRsu ? 0 : 1;
                }
    
            }
        }

        else {
            actionByPolicy = policy.getAction(allActionValues);
        }
        
        // actionByPolicy = policy.getAction(allActionValues);
        DqnMethod.addToMemoryTmp(this, message, state, actionByPolicy);
        this.cnt ++;
        return actionByPolicy;
    }
}
