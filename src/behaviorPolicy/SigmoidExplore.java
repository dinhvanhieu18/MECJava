package src.behaviorPolicy;

import java.util.ArrayList;
import java.util.Random;

public class SigmoidExplore extends Policy{
    public double epsilon;
    public double w;

    public SigmoidExplore(double epsilon, double w) {
        this.epsilon = epsilon;
        this.w = w;
    }

    public int getAction(ArrayList<Double> values) {
        double varient = Math.abs(values.get(0) - values.get(1));
        epsilon = 1 - (1 / (1 + Math.exp(- w * varient)));
        Random random = new Random();
        double rand = random.nextDouble();
        if (rand < epsilon) {
            return random.nextInt(2);
        }
        else {
            return values.get(0) >= values.get(1) ? 0 : 1;
        }
    }
}
