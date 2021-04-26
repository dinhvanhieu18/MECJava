package src.optimizerMethod;

import src.Config;
import src.behaviorPolicy.Policy;
import src.behaviorPolicy.SigmoidExplore;
import src.object.Message;
import src.optimizer.Mab;

public class MabMethod {

    public static Policy getBehaviorPolicy() {
        Policy policy = new SigmoidExplore(Config.epsilon, Config.w);
        return policy;
    }

    public static void addToMemoryTmp(Mab mab, Message message, int action) {
        mab.memory.put(message.stt, action);
    }

    public static void updateReward(Mab mab, Message message, double delay) {
        int action = mab.memory.remove(message.stt);
        mab.cntAction[action] ++;
        double reward = -delay;
        double lr = 1.0 / mab.cntAction[action];
        mab.values[action] = (1 - lr) * mab.values[action] + lr * reward;
    }

}
