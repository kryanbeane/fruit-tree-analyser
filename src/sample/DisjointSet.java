package sample;

public class DisjointSet<N> {

    public static DsNode<?> find(DsNode<?> node) {
        return node.parent == null ? node : (node.parent = find(node.parent));
    }

    public static void unionBySize(DsNode<?> p, DsNode<?> q) {
        DsNode<?> rootp = find(p);
        DsNode<?> rootq = find(q);
        DsNode<?> biggerRoot = rootp.size >= rootq.size ? rootp : rootq;
        DsNode<?> smallerRoot = biggerRoot == rootp ? rootq : rootp;
        smallerRoot.parent = biggerRoot;
        biggerRoot.size += smallerRoot.size;
    }

    public static class DsNode<S> {
        public DsNode<?> parent = null;
        public S data;
        public int size = 1;
        public int height = 1;

        // Constructor
        public DsNode(DsNode<?> parent, S data) {
            this.parent = parent;
            this.data = data;

        }
    }
}