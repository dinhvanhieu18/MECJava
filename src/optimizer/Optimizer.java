package src.optimizer;

import src.behaviorPolicy.Policy;

import src.object.Message;
import src.object.Network;
import src.object.Object;

public abstract class Optimizer {
    public String agentName;
    public int nStates;
    public int nActions;
    public Policy policy;
    public int cnt;

    public void updateReward(Message message, double delay) {}

    public abstract int getAction(Object object, Message message, Network network);
}
