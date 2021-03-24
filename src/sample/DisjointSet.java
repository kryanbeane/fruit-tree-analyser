package sample;

public class DisjointSet {

    public static void unionBySize(int[] array, int p, int q) {
        int rootp = find(array, p);
        int rootq = find(array, q);

        int biggerRoot = array[rootp] < array[rootq] ? rootp : rootq;
        int smallerRoot = biggerRoot == rootp ? rootq : rootp;
        int smallSize = array[smallerRoot];

        array[smallerRoot] = biggerRoot;
        array[biggerRoot] += smallSize;
    }

    public static void quickUnion(int[] a, int p, int q) {
        a[find(a,q)]=p; //The root of q is made reference p
    }

    public static void unionTwoSinglesBySize(int[] array, int p, int q) {

    }

    public static int find(int[] array, int id) {
        return array[id] < 0 ? id : find(array, array[id]);
    }

}