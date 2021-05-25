package src.optimizer;

import src.object.Message;
import src.object.Network;
import src.object.Object;
import src.optimizerMethod.HeristicDqnMethod;

public class HeristicDqn extends Optimizer {
    public Heristic heristic;
    public Dqn dqn;
    public boolean stable;

    public HeristicDqn(String agentName, int nStates, int nActions) {
        this.agentName = agentName;
        this.nStates = nStates;
        this.nActions = nActions;
        this.policy = HeristicDqnMethod.getBehaviorPolicy();
        this.heristic = new Heristic(agentName, nStates, nActions);
        this.dqn = new Dqn(agentName, nStates, nActions);
        this.cnt = 0;
        this.stable = false;
    }

    public void updateReward(Message message, double delay) {
        HeristicDqnMethod.updateReward(this, message, delay);
    }

    public int getAction(Object object, Message message, Network network) {
        return HeristicDqnMethod.getAction(this, object, message, network);
    }
}
