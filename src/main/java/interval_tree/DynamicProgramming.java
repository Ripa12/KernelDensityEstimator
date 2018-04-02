package interval_tree;

import interval_tree.CandidateIndex;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

// A Dynamic Programming based solution for 0-1 Knapsack problem
public class DynamicProgramming {

    // A utility function that returns maximum of two integers
    static int max(int a, int b) {
        return (a > b) ? a : b;
    }
    // https://introcs.cs.princeton.edu/java/23recursion/Knapsack.java.html
    public static void calcSolution(List<Map.Entry<Integer, Integer>> data, int capacity){
        int N = data.size();   // number of items
        int W = capacity;   // maximum weight of knapsack

        int[] profit = new int[N+1];
        int[] weight = new int[N+1];

        // generate random instance, items 1..N
        for (int n = 1; n <= N; n++) {
            profit[n] = data.get(n-1).getKey();
            weight[n] = data.get(n-1).getValue();
        }

        // opt[n][w] = max profit of packing items 1..n with weight limit w
        // sol[n][w] = does opt solution to pack items 1..n with weight limit w include item n?
        int[][] opt = new int[N+1][W+1];
        boolean[][] sol = new boolean[N+1][W+1];

        for (int n = 1; n <= N; n++) {
            for (int w = 1; w <= W; w++) {

                // don't take item n
                int option1 = opt[n-1][w];

                // take item n
                int option2 = Integer.MIN_VALUE;
                if (weight[n] <= w) option2 = profit[n] + opt[n-1][w-weight[n]];

                // select better of two options
                opt[n][w] = Math.max(option1, option2);
                sol[n][w] = (option2 > option1);
            }
        }

        // determine which items to take
        boolean[] take = new boolean[N+1];
        for (int n = N, w = W; n > 0; n--) {
            if (sol[n][w]) {
                take[n] = true;
                w = w - weight[n];
            }
            else {
                take[n] = false;
            }
        }


        int totalProfit = 0;
        int totalWeight = 0;
        // print results
        System.out.println("item" + "\t" + "profit" + "\t" + "weight" + "\t" + "take");
        for (int n = 1; n <= N; n++) {
            totalProfit += take[n] ? profit[n] : 0;
            totalWeight += take[n] ? weight[n] : 0;
            System.out.println(n + "\t" + profit[n] + "\t" + weight[n] + "\t" + take[n]);
        }
        System.out.println("Total profit: " + totalProfit);
        System.out.println("Total weight: " + totalWeight);
    }

    // Returns the maximum value that can be put in a knapsack of capacity W
    static int knapSack(int W, int wt[], int val[], int n) {
        // val[] is for storing maximum profit for each weight
        // wt[] is for storing weights
        // n number of item
        // W maximum capacity of bag
        // dp[W+1] to store final result
        // array to store final result
        //dp[i] stores the profit with KnapSack capacity "i"
        int dp[] = new int[W + 1];

        //initially profit with 0 to W KnapSack capacity is 0
        //memset(dp, 0, sizeof(dp));
        Arrays.fill(dp, 0);


        // iterate through all items
        for (int i = 0; i < n; i++)
            //traverse dp array from right to left
            for (int j = W; j >= wt[i]; j--)
                dp[j] = max(dp[j], val[i] + dp[j - wt[i]]);
            /*above line finds out maximum of  dp[j](excluding ith element value)
            and val[i] + dp[j-wt[i]] (including ith element value and the
            profit with "KnapSack capacity - ith element weight") */
        return dp[W];


// This code is contributed by Gaurav Mamgain

//        int i, w;
//        int K[][] = new int[n+1][W+1];
//
//        // Build table K[][] in bottom up manner
//        for (i = 0; i <= n; i++)
//        {
//            for (w = 0; w <= W; w++)
//            {
//                if (i==0 || w==0)
//                    K[i][w] = 0;
//                else if (wt[i-1] <= w)
//                    K[i][w] = max(val[i-1] + K[i-1][w-wt[i-1]],  K[i-1][w]);
//                else
//                    K[i][w] = K[i-1][w];
//            }
//        }
//
//        return K[n][W];
    }


    // Driver program to test above function
    public static void solveKP(List<CandidateIndex> data, int capacity) {

        int n = data.size();
        int val[] = new int[n];
        int wt[] = new int[n];
        int W = capacity;


        for (int i = 0; i < n; i++) {
            val[i] = (int)(data.get(i).getValue());
            wt[i] = (int)(data.get(i).getWeight());
        }

        System.out.println(knapSack(W, wt, val, n));
    }
}
/*This code is contributed by Rajat Mishra */