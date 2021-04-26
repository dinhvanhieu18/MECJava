package src.behaviorPolicy;

import java.util.Random;

public class SigmoidExplore extends Policy{
    public double epsilon;
    public double w;

    public SigmoidExplore(double epsilon, double w) {
        this.epsilon = epsilon;
        this.w = w;
    }

    public int getAction(double[] values) {
        double varient = Math.abs(values[0] - values[1]);
        epsilon = 1 - (1 / (1 + Math.exp(- w * varient)));
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
