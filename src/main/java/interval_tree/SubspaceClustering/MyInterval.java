package interval_tree.SubspaceClustering;

public class MyInterval implements MyData{
    public int start;
    public int end;

    public MyInterval(int start, int end){
        this.start = start;
        this.end = end;
    }

    @Override
    public int getLow() {
        return start;
    }

    @Override
    public int getHigh() {
        return end;
    }
}
