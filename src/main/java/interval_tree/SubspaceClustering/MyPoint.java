package interval_tree.SubspaceClustering;

public class MyPoint implements MyData{
    public int data;

    public MyPoint(int data){
        this.data = data;
    }

    @Override
    public int getLow() {
        return data;
    }

    @Override
    public int getHigh() {
        return data;
    }
}
