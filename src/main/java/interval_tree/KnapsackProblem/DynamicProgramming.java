package interval_tree.KnapsackProblem;

import interval_tree.CandidateIndex.AbstractIndex;
import interval_tree.CandidateIndex.IIndex;

import java.util.List;


/**
 * Not my code!
 */
public class DynamicProgramming {
    public boolean[] getKnapsackSolution(
            int[] weight, int[] value, int capcity) {
        int n = weight.length;
        int[][] cost = new int[n][capcity + 1];

        for ( int i = 0; i <= capcity; ++ i ) {
            int currentWeight = weight[n - 1],
                    currentValue = value[n - 1];
            cost[n - 1][i] = i < currentWeight ? 0 : currentValue;
        }
        for ( int i = n - 2; i >= 0; -- i ) {
            int currentWeight = weight[i],
                    currentValue = value[i];
            for ( int j = 0; j <= capcity; ++ j ) {
                cost[i][j] = j < currentWeight ? cost[i + 1][j] :
                        Math.max(cost[i + 1][j],
                                cost[i + 1][j - currentWeight] + currentValue);
            }
        }
        return getPickedItem(cost, weight, capcity);
    }

    public boolean[] getPickedItem(int[][] cost, int[] weight, int capcity) {
        int n = cost.length;
        int j = capcity;
        boolean[] isPicked = new boolean[n];

        for ( int i = 0; i < n - 1; ++ i ) {
            if ( cost[i][j] != cost[i + 1][j] ) {
                isPicked[i] = true;
                j -= weight[i];
            }
        }
        isPicked[n - 1] = cost[n - 1][j] != 0;
        return isPicked;
    }

    public static int solveKP(List<? extends IIndex> idxs, int cap) {
        if(idxs.isEmpty())
            return 0;

        int n = idxs.size();
        int capcity = cap;
        int[] weight = new int[n];
        int[] value = new int[n];

        for ( int i = 0; i < n; ++ i ) {
            weight[i] = (int)idxs.get(i).getWeight();
            value[i] = (int)idxs.get(i).getValue();
        }

        DynamicProgramming k = new DynamicProgramming();
        long startTime = System.currentTimeMillis();
        boolean[] isPicked = k.getKnapsackSolution(weight, value, capcity);
        long endTime = System.currentTimeMillis();

        int totalValue = 0;
        int totalWeight = 0;
        for ( int i = n-1; i >= 0; i-- ) {
            if ( isPicked[i] ) {
                totalValue += value[i];
                totalWeight += weight[i];
            }
            else{
                idxs.remove(i);
            }
            System.out.print((isPicked[i] ? 1 : 0) + " ");
        }
        System.out.println();
        System.out.println("Max Value = " + totalValue);
        System.out.println("Total Time: " + (endTime - startTime) + " ms");

        return totalWeight;
    }
}