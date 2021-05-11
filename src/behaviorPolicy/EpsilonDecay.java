package src.behaviorPolicy;

import java.util.Random;

import src.helper.Config;

public class EpsilonDecay extends Policy{
    public double epsilon;

    public EpsilonDecay(double epsilon) {
        this.epsilon = epsilon;
    }

    public int getAction(double[] values) {
        if (epsilon > Config.minEpsilon) {
            epsilon *= Config.decayRateEpsilon;
        }
        Random random = new Random();
        double rand = random.nextDouble();
        if (rand < epsilon) {
            return random.nextInt(2);
        }
        else {
            return values[0] >= values[1] ? 0 : 1;
        }
    }
}
