package comp2402a4;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class UltraSlow implements UltraStack {
    List<Integer> ds;

    public UltraSlow() {
        ds = new ArrayList<Integer>();
    }

    public void push(Integer x) {
        ds.add(x);
    }

    public Integer pop() {
        if (ds.size() == 0)
            return null;
        return ds.remove(ds.size() - 1);
    }

	public Integer get(int i) {
		if (i < 0 || i >= ds.size())
			return null;
		return ds.get(i);
	}

	public Integer set(int i, int x) {
		if (i < 0 || i >= ds.size())
			return null;
		return ds.set(i, x);
	}

    public int topKodd(int k) {
        int num_odd = 0;
        for (int i = 0; i < k && i < ds.size(); i++)
            if (ds.get(ds.size() - 1 - i) % 2 != 0)
                num_odd++;
        return num_odd;
    }

    public Integer maxDiff() {
        if(size() < 2)
            return null;
		int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
		for (int i =  0; i < ds.size(); i++) {
			Integer value =  ds.get(i);
			if (value > max)
				max = value;
			if (value < min)
				min = value;
		}
		return max - min;
    }

    public int size() {
        return ds.size();
    }

    public Iterator<Integer> iterator() {
        return ds.iterator();
    }
}
