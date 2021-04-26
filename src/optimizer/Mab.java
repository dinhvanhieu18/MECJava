package src.optimizer;

import java.util.Hashtable;

import src.object.Message;
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
    }

    public void addToMemoryTmp(Message message, double[] state, int action) {
        MabMethod.addToMemoryTmp(this, message, action);
    }

    public void updateReward(Message message, double delay) {
        MabMethod.updateReward(this, message, delay);
    }

    public int getAction(double[] state) {
        return policy.getAction(values);
    }
}
