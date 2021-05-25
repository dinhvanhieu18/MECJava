package src.optimizerMethod;

import src.behaviorPolicy.Policy;
import src.behaviorPolicy.SigmoidExplore;
import src.helper.Config;
import src.helper.Memory.MemoryE;
import src.helper.Memory.MemoryTmpE;
import src.object.Message;
import src.optimizer.DqnFinal;

public class DqnFinalMethod {
    public static Policy getBehaviorPolicy() {
        Policy policy = new SigmoidExplore(Config.epsilon, Config.w);
        return policy;
    }

    public static void addToMemoryTmp(DqnFinal dqnFinal, Message message, double[] state, int action) {
        MemoryE experience = new MemoryE(state, action);
        MemoryTmpE experienceTmp = new MemoryTmpE(experience, message.stt);
        dqnFinal.memory.addToMemoryTmp(experienceTmp);
    }

    public static void updateReward(DqnFinal dqnFinal, Message message, double delay) {
        double[] state = new double[dqnFinal.nStates];
        for (int i = 0; i < dqnFinal.memory.getMemoryTmpSize(); i ++) {
            MemoryTmpE memoryTmpE = dqnFinal.memory.memoryTmp.get(i);
            if (message.stt != memoryTmpE.mesId) {
                continue;
            }
            double reward = - delay;
            memoryTmpE.experience.reward = reward;
            state = memoryTmpE.experience.state;
            if (memoryTmpE.experience.nextstate != null) {
                dqnFinal.memory.addToMemory(memoryTmpE.experience);
                dqnFinal.memory.memoryTmp.remove(i);
            }
        }
        if (dqnFinal.stable) {
            DqnFinalMethod.update(dqnFinal);
        }
        else {
            
            double[] target = dqnFinal.heristic.values;
            dqnFinal.onlineModel.train(state, target);

            if (checkStable(dqnFinal)) {
                dqnFinal.stable = true;
                dqnFinal.targetModel.setWeights(dqnFinal.onlineModel.getWeights());
            }
        }
    }

    public static boolean checkStable(DqnFinal dqnFinal) {
        return dqnFinal.cnt > Config.thresholdStable ? true : false;
    }

    public static void updateState(DqnFinal dqnFinal, Message message, double[] state) {
        if (dqnFinal.memory.getMemoryTmpSize() == 0) {
            return;
        }
        MemoryTmpE preState = dqnFinal.memory.getLastMemoryTmp();
        preState.experience.nextstate = state;
        if (preState.experience.reward != 0.0) {
            dqnFinal.memory.addToMemory(preState.experience);
            dqnFinal.memory.removeLastMemoryTmp();
        }
    }

    public static void update(DqnFinal dqnFinal) {
        if (dqnFinal.cnt % Config.timeUpdateOnlineModel == 0) {
            replayExperienceFromMemory(dqnFinal);
        }
        if (dqnFinal.cnt % Config.timeUpdateTargetModel == 0) {
            dqnFinal.targetModel.setWeights(dqnFinal.onlineModel.getWeights());
        }
    }

    public static void replayExperienceFromMemory(DqnFinal dqnFinal) {
        if (dqnFinal.memory.getMemorySize() < Config.batchSize) {
            return;
        }
        MemoryE[] experienceBatch = dqnFinal.memory.getRandomBatch(Config.batchSize);
        for (MemoryE experience : experienceBatch) {
            updateOnlineModel(dqnFinal, experience);
        }
    }

    public static void updateOnlineModel(DqnFinal dqnFinal, MemoryE experience) {
        double[] currentState = experience.state;
        int action = experience.action;
        double reward = experience.reward;
        double[] nextState = experience.nextstate;
        double[] actionValuesForCurrentState = dqnFinal.onlineModel.predict(currentState);
        // if (dqnFinal.stable) {
        //     double[] actionValuesForNextState = dqnFinal.targetModel.predict(nextState);
        //     double maxValueNextState = Math.max(actionValuesForNextState[0], actionValuesForNextState[1]);
        //     double targetActionValue = reward + dqnFinal.gamma * maxValueNextState;
        //     actionValuesForCurrentState[action] = targetActionValue;
        // }
        // else {
        //     actionValuesForCurrentState[0] = dqnFinal.rewardGnb;
        //     actionValuesForCurrentState[1] = dqnFinal.rewardRsu;
        //     if (dqnFinal.cnt >= Config.thresholdStable) {
        //         dqnFinal.stable = true;
        //     }
        // }
        double[] actionValuesForNextState = dqnFinal.targetModel.predict(nextState);
        double maxValueNextState = Math.max(actionValuesForNextState[0], actionValuesForNextState[1]);
        double targetActionValue = reward + dqnFinal.gamma * maxValueNextState;
        actionValuesForCurrentState[action] = targetActionValue;
        dqnFinal.onlineModel.train(currentState, actionValuesForCurrentState);
    }
}
