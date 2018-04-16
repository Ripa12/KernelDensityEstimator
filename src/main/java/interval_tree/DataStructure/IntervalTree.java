package interval_tree.DataStructure;

import interval_tree.GUI.Histogram;
import interval_tree.GUI.StdDraw;
import interval_tree.PDF.UnivariateKernelEstimator;

import java.util.stream.IntStream;

// ToDO: Implement Red-Black tree self-balancing
public class IntervalTree {

    public class TreeNode{
        TreeNode left, right;
        NodeData data;

        int max;

        private TreeNode(NodeData data){
            this.data = data;
            this.max = data.getHigh();
            left = right = null;
        }

        @Override
        public String toString(){
            return String.format("Start: " + data.getLow() + " end: " + data.getHigh());
        }
    }

    public static abstract class NodeData{
        int weight;

        NodeData(){
            weight = 1;
        }

        private void incrementWeight(){
            weight += 1;
        }

        public abstract int getHigh();
        public abstract int getLow();
        abstract boolean equalTo(NodeData other);
        abstract void insertToKernel(UnivariateKernelEstimator e);
        abstract void insertToHistogram(Histogram e, int oldMin, int oldMax);

        abstract void scaleData(int oldMin, int oldMax);
        abstract void rescaleData(int oldMin, int oldMax);
    }

    public static class Point extends NodeData {
        public int data;

        public Point(int data){
            this.data = data;
        }

        public int getHigh() {
            return data;
        }

        public int getLow() {
            return data;
        }

        boolean equalTo(NodeData other) {
            return other.getLow() == data && other.getHigh() == data;
        }

        @Override
        void insertToKernel(UnivariateKernelEstimator e) {
            e.addValue(data, weight);
        }

        @Override
        void insertToHistogram(Histogram e, int oldMin, int oldMax) {
            e.addDataPoint(scale(data, oldMin, oldMax));
        }

        @Override
        void scaleData(int oldMin, int oldMax) {
            data = scale(data, oldMin, oldMax);
        }

        @Override
        void rescaleData(int oldMin, int oldMax) {
            data = rescale(data, oldMin, oldMax);
        }

    }

    public static class Interval extends NodeData {
        private static final int BinRange = 500;

        public int start;
        public int end;

        public Interval(int start, int end){
            this.start = start;
            this.end = end;
        }

        public int getHigh() {
            return end;
        }

        public int getLow() {
            return start;
        }

        boolean equalTo(NodeData other) {
            return other.getHigh() == end && other.getLow() == start;
        }

        @Override
        void insertToKernel(UnivariateKernelEstimator e) {
            int localBinRange = MaxNew/100;//Math.max((end - start) / 10, 1);
            IntStream.range(start, end).filter(x->x%localBinRange==0)
                    .mapToDouble(x->x)
                    .forEach(x->e.addValue(x, weight/1));
        }

        @Override
        void insertToHistogram(Histogram e, int oldMin, int oldMax) {
            int localBinRange = MaxNew/100;//Math.max((end - start) / 10, 1);
            IntStream.range(start, end).filter(x->x%localBinRange==0)
                    .forEach(x->e.addDataPoint(scale(x, oldMin, oldMax)));
        }

        @Override
        void scaleData(int oldMin, int oldMax) {
            start = scale(start, oldMin, oldMax);
            end = scale(end, oldMin, oldMax);
        }

        @Override
        void rescaleData(int oldMin, int oldMax) {
            start = rescale(start, oldMin, oldMax);
            end = rescale(end, oldMin, oldMax);
        }

    }

    // ToDo: Would it be possible for histogram and estimator to be static?
//    private Histogram histogram;
    private static UnivariateKernelEstimator estimator = null;

    private TreeNode root;
    private int minVal;
    private int maxVal;
    private int frequency;
    private String column;


    // ToDo: Could possibly change interval tree to an array of bins instead
    public IntervalTree(String column){
        frequency = 0;
//        histogram = null;

        //estimator = null;
        if(estimator == null){
            estimator = new UnivariateKernelEstimator();
        }

        root = null;
        minVal = Integer.MAX_VALUE;
        maxVal = Integer.MIN_VALUE;

        this.column = column;
    }

    public void setMinVal(int minVal){
        this.minVal = Math.min(minVal, this.minVal);
    }

    public void setMaxVal(int maxVal){
        this.maxVal = Math.max(maxVal, this.maxVal);
    }

    public String getColumn(){
        return this.column;
    }

    public int getFrequency(){
        return frequency;
    }

    public void insert(NodeData data){
        frequency++;

        data.scaleData(minVal, maxVal);

        minVal = Math.min(minVal, data.getLow());
        maxVal = Math.max(maxVal, data.getHigh());

        TreeNode node = insert(root, data);
        if(this.root == null)
            this.root = node;

    }

    private TreeNode insert(TreeNode root, NodeData data){

        if(root == null)
            return new TreeNode(data);

        int l = root.data.getLow();

        if(data.getLow() < l){
            root.left = insert(root.left, data);
        }
        else if(root.data.equalTo(data)){
            root.data.incrementWeight();
        }
        else{
            root.right = insert(root.right, data);
        }

        root.max = root.max < data.getHigh() ? data.getHigh() : root.max;

        return root;
    }

    public double[][] predictIntervals(double conf){
        return estimator.predictIntervals(conf, minVal, maxVal);
    }

    // ToDO: might be more efficient with an iterator and not recursion
    public void iterate(){
//        histogram = new Histogram(MaxNew+1);
//        estimator = new UnivariateKernelEstimator(); // ToDo: maybe check if null here to avoid creating a new instance every invocation

        estimator.reset();

        iterate(this.root);

        // display using standard draw
//        StdDraw.setCanvasSize(2500, 700);
//        histogram.draw(minVal, maxVal);

//        histogram = null;
//        estimator = null;
    }

    private void iterate(TreeNode root)
    {
        if (root == null)
            return;

        iterate(root.left);

//        System.out.println(root.toString());

        root.data.insertToKernel(estimator);
//        root.data.insertToHistogram(histogram, minVal, maxVal);

        iterate(root.right);
    }

    private final static int MaxNew = 100;
    private final static int MinNew = 0;

    public static int scale(double v, int oldMin, int oldMax){
        final double maxnew = MaxNew;
        final double minnew = MinNew;
        final double maxold = oldMax;
        final double minold = oldMin;

        return (int)(((maxnew-minnew)/(maxold-minold))*(v-maxold)+maxnew);
    }

    public static int rescale(double v, int oldMin, int oldMax){
        final double maxnew = oldMax;
        final double minnew = oldMin;
        final double maxold = MaxNew;
        final double minold = MinNew;

        return (int)(((maxnew-minnew)/(maxold-minold))*(v-maxold)+maxnew);
    }

}
