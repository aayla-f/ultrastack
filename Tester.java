package comp2402a4;

import java.util.Iterator;
import java.util.Random;

public class Tester {
    /*
    Set useSeed to true if you want the same values to be
    used in your tests all the time..
    */
    static boolean useSeed = true;

    static void showContents(Iterable<Integer> ds) {
        System.out.print("[");
        Iterator<Integer> it = ds.iterator();
        while (it.hasNext()) {
            System.out.print(it.next());
            if (it.hasNext()) {
                System.out.print(",");
            }
        }
        System.out.println("]");
    }

    static void ultraTest(UltraStack css, int n) {
        System.out.println(css.getClass().getName());
        Random rand;
        if (useSeed)
            rand = new Random(2402);
        else
            rand = new Random();

        for (int i = 0; i < n; i++) {
            int x = rand.nextInt(3 * n / 2);
            System.out.println("push(" + x + ")");
            css.push(x);
            showContents(css);

            int k = rand.nextInt(css.size() + 1);
            System.out.println("topKodd(" + k + ") = " + css.topKodd(k));
            System.out.println("maxDiff() = " + css.maxDiff());
        }

        for (int i = 0; i < n; i++) {
            int ii = rand.nextInt(css.size());
            showContents(css);
            System.out.println("get("+ ii +") = " + css.get(ii));
            int xx = rand.nextInt(3 * n / 2);
            System.out.println("set("+ ii + ", " + xx +") = " + css.set(ii, xx));
            int k = rand.nextInt(css.size() + 1);
            System.out.println("topKodd(" + k + ") = " + css.topKodd(k));
            System.out.println("maxDiff() = " + css.maxDiff());
        }

        while (css.size() > 0) {
            System.out.println("pop() = " + css.pop());
            showContents(css);
            if (css.size() > 0) {
                int k = rand.nextInt(css.size() + 1);
                System.out.println("topKodd(" + k + ") = " + css.topKodd(k));
                System.out.println("maxDiff() = " + css.maxDiff());
            }
        }
    }

    public static void main(String[] args) {
        ultraTest(new UltraSlow(), 20);
        ultraTest(new UltraFast(), 20);
    }
}
