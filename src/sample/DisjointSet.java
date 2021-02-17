package sample;

public class DisjointSet<N> {

    private int setSize;

    public void addNode(DsNode<N> n) {
        if (this.isEmpty()) {
            n.parent = n;
            setSize++;
        } else {
            setSize++;
        }
    }

    public boolean isEmpty() {
        return setSize == 0;
    }


}
