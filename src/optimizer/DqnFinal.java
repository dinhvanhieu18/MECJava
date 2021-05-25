package src.optimizer;

import src.helper.Config;
import src.helper.Memory;
import src.helper.NeuralNetwork;
import src.object.Message;
import src.object.Network;
import src.object.Object;
import src.optimizerMethod.DqnFinalMethod;
import src.optimizerMethod.DqnMethod;

public class DqnFinal extends Optimizer {
    public double alpha;
    public double gamma;
    public NeuralNetwork onlineModel;
    public NeuralNetwork targetModel;
    public Memory memory;
    public boolean stable = false;
    public Heristic heristic;

    public DqnFinal(String agentName, int nStates, int nActions) {
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
        this.heristic = new Heristic(agentName, nStates, nActions);
    }
    
    public void updateReward(Message message, double delay) {
        DqnFinalMethod.updateReward(this, message, delay);
    }

    public int getAction(Object object, Message message, Network network) {
        double[] state = object.getState(message, network);
        DqnFinalMethod.updateState(this, message, state);
        double[] allActionValues = onlineModel.predict(state);
        int actionByPolicy;
        if (Math.abs(allActionValues[0]-allActionValues[1]) < Config.minDelta || this.cnt < Config.thresholdStable) {
            actionByPolicy = policy.getAction(allActionValues);
        } 
        else {
            actionByPolicy = policy.getAction(heristic.values);
        }
        // actionByPolicy = policy.getAction(allActionValues);
        DqnFinalMethod.addToMemoryTmp(this, message, state, actionByPolicy);
        this.cnt ++;
        return actionByPolicy;
    }
}
