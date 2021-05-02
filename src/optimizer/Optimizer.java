package src.optimizer;

import src.behaviorPolicy.Policy;

import src.object.Message;
import src.object.Network;
import src.object.Object;

public class Optimizer {
    public String agentName;
    public int nStates;
    public int nActions;
    public Policy policy;

    public void updateReward(Message message, double delay) {}

    public int getAction(Object object, Message message, Network network) {
        return 0;
    }
}
