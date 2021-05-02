package src.optimizer;

import src.helper.Config;
import src.helper.Memory;
import src.helper.NeuralNetwork;
import src.object.Message;
import src.object.Network;
import src.object.Object;
import src.optimizerMethod.DqnMethod;

public class Dqn extends Optimizer {
    public double alpha;
    public double gamma;
    public NeuralNetwork onlineModel;
    public NeuralNetwork targetModel;
    public Memory memory;
    public int cnt;
    public boolean stable;

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
        int actionByPolicy = policy.getAction(allActionValues);
        DqnMethod.addToMemoryTmp(this, message, state, actionByPolicy);
        return actionByPolicy;
    }
}
