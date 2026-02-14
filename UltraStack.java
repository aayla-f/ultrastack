package comp2402a4;

import java.util.Iterator;

public interface UltraStack extends Iterable<Integer> {
    public void push(Integer x);
    public Integer pop();
    public Integer get(int i);
    public Integer set(int i, int x);
    public int topKodd(int k);
    public Integer maxDiff();
    public int size();
    public Iterator<Integer> iterator();
}
