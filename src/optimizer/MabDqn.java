package src.optimizer;

import src.object.Message;
import src.object.Network;
import src.object.Object;
import src.optimizerMethod.MabDqnMethod;

public class MabDqn extends Optimizer{
    public Mab mab;
    public Dqn dqn;
    public double probChooseMab;
    public boolean stable;

    public MabDqn(String agentName, int nStates, int nActions) {
        this.agentName = agentName;
        this.nStates = nStates;
        this.nActions = nActions;
        this.policy = MabDqnMethod.getBehaviorPolicy();
        this.mab = new Mab(agentName, nStates, nActions);
        this.dqn = new Dqn(agentName, nStates, nActions);
        this.cnt = 0;
        this.stable = false;
    }

    public void updateReward(Message message, double delay) {
        MabDqnMethod.updateReward(this, message, delay);
    }

    public int getAction(Object object, Message message, Network network) {
        return MabDqnMethod.getAction(this, object, message, network);
    }
}
