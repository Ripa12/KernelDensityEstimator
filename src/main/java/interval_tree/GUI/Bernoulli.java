package interval_tree.GUI;

/******************************************************************************
 *  Compilation:  javac Bernoulli.java
 *  Execution:    java Bernoulli n trials
 *  Dependencies: StdDraw.java StdRandom.java Gaussian.java StdStats.java
 *
 *  Each experiment consists of flipping n fair coins trials times.
 *  Plots a histogram of the number of times i of the n coins are heads.
 *
 *  % java Bernoulli 32 1000
 *
 *  % java Bernoulli 64 1000
 *
 *  % java Bernoulli 128 1000
 *
 ******************************************************************************/



public class Bernoulli {

    // number of heads when flipping n biased-p coins
    public static int binomial(int n, double p) {
        int heads = 0;
        for (int i = 0; i < n; i++) {
            if (StdRandom.bernoulli(p)) {
                heads++;
            }
        }
        return heads;
    }

    // number of heads when flipping n fair coins
    // or call binomial(n, 0.5)
    public static int binomial(int n) {
        int heads = 0;
        for (int i = 0; i < n; i++) {
            if (StdRandom.bernoulli(0.5)) {
                heads++;
            }
        }
        return heads;
    }

}