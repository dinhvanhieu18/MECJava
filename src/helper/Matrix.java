package src.helper;

public class Matrix {
    double[][] data;
    int rows, cols;

    public Matrix(int rows, int cols) {
        data = new double[rows][cols];
        this.rows = rows;
        this.cols = cols;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = Math.random() * 2 - 1;
                // data[i][j] = 0.0;
            }
        }
    }

    public void add(Matrix m) {
        if (cols != m.cols || rows != m.rows) {
            System.out.println("Shape mismatch");
            return;
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.data[i][j] += m.data[i][j];
            }
        }
    }

    public static Matrix subtract(Matrix a, Matrix b) {
        Matrix res = new Matrix(a.rows, a.cols);
        for (int i = 0; i < a.rows; i++) {
            for (int j = 0; j < a.cols; j++) {
                res.data[i][j] = a.data[i][j] - b.data[i][j];
            }
        }
        return res;
    }

    public static Matrix transpose(Matrix a) {
        Matrix res = new Matrix(a.cols, a.rows);
        for (int i = 0; i < a.rows; i++) {
            for (int j = 0; j < a.cols; j++) {
                res.data[j][i] = a.data[i][j];
            }
        }
        return res;
    }

    public static Matrix multiply(Matrix a, Matrix b) {
        Matrix res = new Matrix(a.rows, b.cols);
        for (int i = 0; i < res.rows; i++) {
            for (int j = 0; j < res.cols; j++) {
                double sum = 0;
                for (int k = 0; k < a.cols ; k++) {
                    sum += a.data[i][k] * b.data[k][j];
                }
                res.data[i][j] = sum;
            }
        }
        return res;
    }

    public void multiply(Matrix a) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.data[i][j] *= a.data[i][j];
            }
        }
    }

    public void multiply(double a) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.data[i][j] *= a;
            }
        }
    }

    public void sigmoid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.data[i][j] = 1 / (1 + Math.exp(-this.data[i][j]));
            }
        }
    }

    public Matrix dsigmoid() {
        Matrix res = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res.data[i][j] = this.data[i][j] * (1 - this.data[i][j]);
            }
        }
        return res;
    }

    public void linear() {
        return;
    }

    public Matrix dlinear() {
        Matrix res = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res.data[i][j] = 1;
            }
        }
        return res;
    }

    public static Matrix fromArray(double[] x) {
        Matrix res = new Matrix(x.length, 1);
        for (int i = 0; i < x.length; i++) {
            res.data[i][0] = x[i];
        }
        return res;
    }

    public static Matrix fromArray(double[] x, int rows, int cols) {
        Matrix res = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res.data[i][j] = x[i * cols + j];
            }
        }
        return res;
    }

    public double[] toArray() {
        double[] res = new double[rows * cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res[i * cols + j] = data[i][j];
            }
        }
        return res;
    }

    public void print() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(data[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Matrix a = new Matrix(2, 3);
        Matrix b = new Matrix(2, 3);
        Matrix c = new Matrix(3, 2);
        a.print();
        b.print();
        c.print();
        a.add(b);
        a.print();
        Matrix d = Matrix.subtract(a, b);
        d.print();
        Matrix e = Matrix.transpose(d);
        e.print();
        double[] arr = e.toArray();
        for (double i : arr) {
            System.out.print(i + " ");
        } 
        Matrix f = Matrix.fromArray(arr, 3, 2);
        System.out.println();
        f.print();

    }
}
