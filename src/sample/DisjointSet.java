package sample;

public class DisjointSet<N> {

    public static int find(int[] a, int id) {
        return a[id] < 0 ? id : find(a, a[id]);
    }

    public static void unionBySize(int[] a, int p, int q) {
        int rootp = find(a, p);
        int rootq = find(a, q);
        int biggerRoot = a[rootp] < a[rootq] ? rootp : rootq;
        int smallerRoot = biggerRoot == rootp ? rootq : rootp;
        int smallSize = a[smallerRoot];
        a[smallerRoot] = biggerRoot;
        a[biggerRoot] += smallSize;
    }

    public static class DsNode<S> {
        public DsNode<?> parent = null;
        public S data;

        // Constructor
        public DsNode(DsNode<?> parent, S data) {
            this.parent = parent;
            this.data = data;
        }
    }
}