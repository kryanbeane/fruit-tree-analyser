package sample;

public class DisjointSet {

    public static void quickUnion(int[] a, int p, int q) {
        a[ find(a,q) ] = find(a,p);
    }

    public static int find(int[] a, int id) {
        return a[id] == id ? id : ( a[id] = find(a,a[id]) );
    }
}