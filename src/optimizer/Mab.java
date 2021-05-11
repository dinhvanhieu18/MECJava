package src.optimizer;

import java.util.Hashtable;

import src.object.Message;
import src.object.Network;
import src.object.Object;
import src.optimizerMethod.MabMethod;

public class Mab extends Optimizer{
    public double[] values;
    public int[] cntAction; 
    public Hashtable<Integer, Integer> memory = new Hashtable<>();

    public Mab(String agentName, int nStates, int nActions) {
        this.agentName = agentName;
        this.nStates = nStates;
        this.nActions = nActions;
        values = new double[nActions];
        cntAction = new int[nActions];
        this.policy = MabMethod.getBehaviorPolicy();
        this.cnt = 0;
    }

    public void updateReward(Message message, double delay) {
        MabMethod.updateReward(this, message, delay);
    }

    public int getAction(Object object, Message message, Network network) {
        int action = policy.getAction(values);
        MabMethod.addToMemoryTmp(this, message, action);
        this.cnt ++;
        return action;
    }
}
