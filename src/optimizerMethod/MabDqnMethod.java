package src.optimizerMethod;

import src.behaviorPolicy.Policy;
import src.behaviorPolicy.SigmoidExplore;
import src.helper.Config;
import src.helper.Memory.MemoryTmpE;
import src.object.Message;
import src.object.Network;
import src.object.Object;
import src.optimizer.MabDqn;

public class MabDqnMethod {
    public static Policy getBehaviorPolicy() {
        Policy policy = new SigmoidExplore(Config.epsilon, Config.w);
        return policy;
    }

    public static void addToMemoryTmp(MabDqn mabDqn, Message message, double[] state, int action) {
        DqnMethod.addToMemoryTmp(mabDqn.dqn, message, state, action);
        if (!mabDqn.stable) {
            MabMethod.addToMemoryTmp(mabDqn.mab, message, action);
        }
    }

    public static void updateReward(MabDqn mabDqn, Message message, double delay) {
        double[] state = new double[mabDqn.nStates];
        // Update dqn
        for (int i = 0; i < mabDqn.dqn.memory.getMemoryTmpSize(); i ++) {
            MemoryTmpE memoryTmpE = mabDqn.dqn.memory.memoryTmp.get(i);
            if (message.stt != memoryTmpE.mesId) {
                continue;
            }
            double reward = - delay;
            memoryTmpE.experience.reward = reward;
            state = memoryTmpE.experience.state;
            if (memoryTmpE.experience.nextstate != null) {
                mabDqn.dqn.memory.addToMemory(memoryTmpE.experience);
                mabDqn.dqn.memory.memoryTmp.remove(i);
                mabDqn.dqn.cnt ++;
            }
            break;
        }
        if (mabDqn.stable) {
            DqnMethod.update(mabDqn.dqn);
        }
        else {
            // Update mab
            mabDqn.mab.updateReward(message, delay);
            // // update probChooseMab
            // if (mabDqn.probChooseMab > Config.minProbChooseMab) {
            //     mabDqn.probChooseMab *= Config.decayRateChooseMab;
            // } 
            // Update online model with groundtruth get from mab
            double[] target = mabDqn.mab.values;
            mabDqn.dqn.onlineModel.train(state, target);

            if (checkStable(mabDqn)) {
                mabDqn.stable = true;
                mabDqn.dqn.targetModel.setWeights(mabDqn.dqn.onlineModel.getWeights());
            }
        }
    }

    public static boolean checkStable(MabDqn mabDqn) {
        return mabDqn.cnt > Config.thresholdStable ? true : false;
    }

    public static int getAction(MabDqn mabDqn, Object object, Message message, Network network) {
        mabDqn.cnt ++;
        if (mabDqn.stable) {
            return mabDqn.dqn.getAction(object, message, network);
        }
        else {
            return mabDqn.mab.getAction(object, message, network);
        }
    }
}
