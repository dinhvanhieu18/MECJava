package src.optimizer;

import src.behaviorPolicy.Policy;

import src.object.Message;

public class Optimizer {
    public String agentName;
    public int nStates;
    public int nActions;
    public Policy policy;

    public void addToMemoryTmp(Message message, double[] state, int action) {}

    public void updateReward(Message message, double delay) {}

    public void updateState(Message message, double[] state) {}

    public int getAction(double[] state) {
        int res = 0;
        return res;
    }
}
