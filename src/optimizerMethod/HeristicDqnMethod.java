package src.optimizerMethod;

import src.behaviorPolicy.Policy;
import src.behaviorPolicy.SigmoidExplore;
import src.helper.Config;
import src.helper.Memory.MemoryTmpE;
import src.object.Message;
import src.object.Network;
import src.object.Object;
import src.optimizer.HeristicDqn;

public class HeristicDqnMethod {
    public static Policy getBehaviorPolicy() {
        Policy policy = new SigmoidExplore(Config.epsilon, Config.w);
        return policy;
    }

    public static void updateReward(HeristicDqn heristicDqn, Message message, double delay) {
        double[] state = new double[heristicDqn.nStates];
        // Update dqn
        for (int i = 0; i < heristicDqn.dqn.memory.getMemoryTmpSize(); i ++) {
            MemoryTmpE memoryTmpE = heristicDqn.dqn.memory.memoryTmp.get(i);
            if (message.stt != memoryTmpE.mesId) {
                continue;
            }
            double reward = - delay;
            memoryTmpE.experience.reward = reward;
            state = memoryTmpE.experience.state;
            if (memoryTmpE.experience.nextstate != null) {
                heristicDqn.dqn.memory.addToMemory(memoryTmpE.experience);
                heristicDqn.dqn.memory.memoryTmp.remove(i);
                heristicDqn.dqn.cnt ++;
            }
            break;
        }
        if (heristicDqn.stable) {
            DqnMethod.update(heristicDqn.dqn);
        }
        else {
            double[] target = new double[heristicDqn.nActions];
            target[0] = - heristicDqn.heristic.values[0];
            target[1] = - heristicDqn.heristic.values[1];
            
            heristicDqn.dqn.onlineModel.train(state, target);

            if (checkStable(heristicDqn)) {
                heristicDqn.stable = true;
                heristicDqn.dqn.targetModel.setWeights(heristicDqn.dqn.onlineModel.getWeights());
            }
        }
    }

    public static boolean checkStable(HeristicDqn heristicDqn) {
        return heristicDqn.cnt > Config.thresholdStable ? true : false;
    }

    public static int getAction(HeristicDqn heristicDqn, Object object, Message message, Network network) {
        heristicDqn.cnt ++;
        if (heristicDqn.stable) {
            return heristicDqn.dqn.getAction(object, message, network);
        }
        else {
            return heristicDqn.heristic.getAction(object, message, network);
        }
    }
}
