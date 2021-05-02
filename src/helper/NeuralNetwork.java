package src.helper;

public class NeuralNetwork {
    public Matrix weights_ih, weights_ho, bias_h, bias_o;
    public double lr;
    
    public NeuralNetwork(int i, int h, int o, double lr) {
        this.weights_ih = new Matrix(h, i);
        this.weights_ho = new Matrix(o, h);
        this.bias_h = new Matrix(h, 1);
        this.bias_o = new Matrix(o, 1);
        this.lr = lr;
    }

    public double[] predict(double[] x) {
        Matrix input = Matrix.fromArray(x);
        Matrix hidden = Matrix.multiply(weights_ih, input);
        hidden.add(bias_h);
        hidden.sigmoid();

        Matrix output = Matrix.multiply(weights_ho, hidden);
        output.add(bias_o);
        output.sigmoid();

        return output.toArray();
    }

    public void train(double[] x, double[] y) {
        Matrix input = Matrix.fromArray(x);
        Matrix hidden = Matrix.multiply(weights_ih, input);
        hidden.add(bias_h);
        hidden.sigmoid();

        Matrix output = Matrix.multiply(weights_ho, hidden);
        output.add(bias_o);
        output.sigmoid();

        Matrix target = Matrix.fromArray(y);
        Matrix error = Matrix.subtract(target, output);
        Matrix gradient = output.dsigmoid();
        gradient.multiply(error);
        gradient.multiply(lr);

        Matrix hidden_T = Matrix.transpose(hidden);
        Matrix who_delta = Matrix.multiply(gradient, hidden_T);

        weights_ho.add(who_delta);
        bias_o.add(gradient);

        Matrix who_T = Matrix.transpose(weights_ho);
        Matrix hidden_errors = Matrix.multiply(who_T, error);
        Matrix h_gradient = hidden.dsigmoid();
        h_gradient.multiply(hidden_errors);
        h_gradient.multiply(lr);

        Matrix i_T = Matrix.transpose(input);
        Matrix wih_delta = Matrix.multiply(h_gradient, i_T);

        weights_ih.add(wih_delta);
        bias_h.add(h_gradient);
    }

    public void fit(double[][] X, double[][] Y) {
        for (int i = 0; i < X.length; i++) {
            train(X[i], Y[i]);
        }
    }

    public Matrix[] getWeights() {
        Matrix[] res = {weights_ih, weights_ho, bias_h, bias_o};
        return res;
    }

    public void setWeights(Matrix[] weights) {
        weights_ih = weights[0];
        weights_ho = weights[1];
        bias_h = weights[2];
        bias_o = weights[3];
    }

    public static void main(String[] args) {
        NeuralNetwork nn = new NeuralNetwork(4, 8, 2, Config.learningRate);
        double[][] X = {{0, 0, 0, 0}, {1, 2, 3, 4}, {3, 4, 5, 6}, {1, 3, 4, 6}};
        double[][] Y = {{0, 1}, {0, 1}, {1, 0}, {1, 0}};
        nn.fit(X, Y);
    }
}
