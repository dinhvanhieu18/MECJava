package src.optimizerMethod;

// import src.behaviorPolicy.EpsilonDecay;
import src.behaviorPolicy.Policy;
import src.behaviorPolicy.SigmoidExplore;
import src.helper.Config;
import src.helper.Memory.MemoryE;
import src.helper.Memory.MemoryTmpE;
import src.object.Message;
import src.optimizer.Dqn;

public class DqnMethod {
    public static Policy getBehaviorPolicy() {
        Policy policy = new SigmoidExplore(Config.epsilon, Config.w);
        // Policy policy = new EpsilonDecay(Config.epsilon);
        return policy;
    }

    public static void addToMemoryTmp(Dqn dqn, Message message, double[] state, int action) {
        MemoryE experience = new MemoryE(state, action);
        MemoryTmpE experienceTmp = new MemoryTmpE(experience, message.stt);
        dqn.memory.addToMemoryTmp(experienceTmp);
    }

    public static void updateReward(Dqn dqn, Message message, double delay) {
        for (int i = 0; i < dqn.memory.getMemoryTmpSize(); i ++) {
            MemoryTmpE memoryTmpE = dqn.memory.memoryTmp.get(i);
            if (message.stt != memoryTmpE.mesId) {
                continue;
            }
            double reward = - delay;
            memoryTmpE.experience.reward = reward;
            if (memoryTmpE.experience.nextstate != null) {
                dqn.memory.addToMemory(memoryTmpE.experience);
                dqn.memory.memoryTmp.remove(i);
            }
        }
        update(dqn);
    }

    public static void updateState(Dqn dqn, Message message, double[] state) {
        if (dqn.memory.getMemoryTmpSize() == 0) {
            return;
        }
        MemoryTmpE preState = dqn.memory.getLastMemoryTmp();
        preState.experience.nextstate = state;
        if (preState.experience.reward != 0.0) {
            dqn.memory.addToMemory(preState.experience);
            dqn.memory.removeLastMemoryTmp();
        }
    }

    public static void update(Dqn dqn) {
        if (dqn.cnt % Config.timeUpdateOnlineModel == 0) {
            replayExperienceFromMemory(dqn);
        }
        if (dqn.cnt % Config.timeUpdateTargetModel == 0) {
            dqn.targetModel.setWeights(dqn.onlineModel.getWeights());
        }
    }

    public static void replayExperienceFromMemory(Dqn dqn) {
        if (dqn.memory.getMemorySize() < Config.batchSize) {
            return;
        }
        MemoryE[] experienceBatch = dqn.memory.getRandomBatch(Config.batchSize);
        for (MemoryE experience : experienceBatch) {
            updateOnlineModel(dqn, experience);
        }
    }

    public static void updateOnlineModel(Dqn dqn, MemoryE experience) {
        double[] currentState = experience.state;
        int action = experience.action;
        double reward = experience.reward;
        double[] nextState = experience.nextstate;
        double[] actionValuesForCurrentState = dqn.onlineModel.predict(currentState);
        // if (dqn.stable) {
        //     double[] actionValuesForNextState = dqn.targetModel.predict(nextState);
        //     double maxValueNextState = Math.max(actionValuesForNextState[0], actionValuesForNextState[1]);
        //     double targetActionValue = reward + dqn.gamma * maxValueNextState;
        //     actionValuesForCurrentState[action] = targetActionValue;
        // }
        // else {
        //     actionValuesForCurrentState[0] = dqn.rewardGnb;
        //     actionValuesForCurrentState[1] = dqn.rewardRsu;
        //     if (dqn.cnt >= Config.thresholdStable) {
        //         dqn.stable = true;
        //     }
        // }
        double[] actionValuesForNextState = dqn.targetModel.predict(nextState);
        double maxValueNextState = Math.max(actionValuesForNextState[0], actionValuesForNextState[1]);
        double targetActionValue = reward + dqn.gamma * maxValueNextState;
        actionValuesForCurrentState[action] = targetActionValue;
        dqn.onlineModel.train(currentState, actionValuesForCurrentState);
    }
}
