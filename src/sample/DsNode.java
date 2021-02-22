package sample;

public class DsNode<S> {
    public DsNode<?> parent = null;
    public S data;

    // Constructor
    public DsNode(DsNode<?> parent, S data) {
        this.parent = parent;
        this.data = data;
    }

    // Getters
    public DsNode<?> getParent() {
        return parent;
    }

    // Setters
    public void setParent(DsNode<?> parent) {
        this.parent = parent;
    }

    public S getData() {
        return data;
    }

    public void setData(S data) {
        this.data = data;
    }

    // Find method
    public DsNode<?> find(DsNode<?> node) {
        return node.parent == null ? node : find(node.parent);
    }

    // Union two nodes method
    public void union(DsNode<?> p, DsNode<?> q) {
        find(q).parent = p;
    }

}
