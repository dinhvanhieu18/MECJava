package src.optimizer;

import java.security.Policy;
import java.util.ArrayList;

import src.object.Message;

public class Optimizer {
    public String agentName;
    public int nStates;
    public int nActions;
    public Policy policy;

    public Optimizer(String agentName, int nStates, int nActions) {
        this.agentName = agentName;
        this.nStates = nStates;
        this.nActions = nActions;
    }

    public void addToMemoryTmp(Message message) {}

    public void updateReward(Message message, double delay) {}

    public void updateState(Message message, ArrayList<Double> state) {}

    public ArrayList<Double> getAllActionValues(ArrayList<Double> state) {
        return new ArrayList<Double>();
    }
}
