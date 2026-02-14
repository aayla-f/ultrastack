package comp2402a4;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UltraFast implements UltraStack {
    List<Integer> ds;

    // Heaps store stack indices (indexes into ds)
    int[] minHeap;
    int[] maxHeap;
    int nMin;
    int nMax;

    // For each stack index i, minPos[i] is position of i inside minHeap (or -1)
    // same for maxPos
    int[] minPos;
    int[] maxPos;

    // partial odd-sum tree uses this
    // oddLeaves = num leaves in the segment tree (power of two)
    // oddTree = segment tree stored in array, indices going from [1 -> 2*oddLeaves-1]
    int oddLeaves;
    int[] oddTree;

    public UltraFast() {
        ds = new ArrayList<>();
        nMin = 0;
        nMax = 0;
        minHeap = new int[1];
        maxHeap = new int[1];
        minPos = new int[1];
        maxPos = new int[1];
        minPos[0] = -1;
        maxPos[0] = -1;

        oddLeaves = 1;
        oddTree = new int[2 * oddLeaves];
    }

    /** --------------------------- PUSH --------------------------- */
    public void push(Integer x) {
        int stackIndex = ds.size();
        ds.add(x);

        ensureCapacity(stackIndex);

        // maintain odd tree
        ensureOddTreeCapacity(ds.size());
        oddTreeSetLeaf(stackIndex, (x % 2 != 0) ? 1 : 0);

        // insert stackIndex into both heaps
        updateMinHeap(stackIndex);
        updateMaxHeap(stackIndex);
    }

    /** --------------------------- HEAP INSERT (MIN) --------------------------- */
    private void updateMinHeap(int stackIndex) {
        if (nMin == minHeap.length) minHeap = resize(minHeap);

        minHeap[nMin] = stackIndex;
        minPos[stackIndex] = nMin;
        bubbleUpMin(nMin);
        nMin++;
    }

    /** --------------------------- HEAP INSERT (MAX) --------------------------- */
    private void updateMaxHeap(int stackIndex) {
        if (nMax == maxHeap.length) maxHeap = resize(maxHeap);

        maxHeap[nMax] = stackIndex;
        maxPos[stackIndex] = nMax;
        bubbleUpMax(nMax);
        nMax++;
    }

    /** --------------------------- RESIZE HELPERS --------------------------- */
    private int[] resize(int[] heap) {
        int[] newArr = new int[heap.length * 2 + 1];
        System.arraycopy(heap, 0, newArr, 0, heap.length);
        return newArr;
    }

    // Ensure minPos/maxPos arrays can hold the index passed (inclusive)
    private void ensureCapacity(int cap) {
        if (cap < minPos.length) return;

        int newSize = Math.max(minPos.length * 2 + 1, cap + 1);

        int[] newMinPos = new int[newSize];
        int[] newMaxPos = new int[newSize];

        // copy old values and init new slots to -1
        System.arraycopy(minPos, 0, newMinPos, 0, minPos.length);
        System.arraycopy(maxPos, 0, newMaxPos, 0, maxPos.length);

        for (int i = minPos.length; i < newSize; i++) {
            newMinPos[i] = -1;
            newMaxPos[i] = -1;
        }

        // updating min and maxPos arrays
        minPos = newMinPos;
        maxPos = newMaxPos;
    }

    // returns the index of the parent of index i
    private int parent(int i) {
        return (i == 0) ? 0 : (i - 1) / 2;
    }

    /** --------------------------- SWAPS (keep pos arrays updated) --------------------------- */

    private void minSwap(int i, int j) {
        int si = minHeap[i];
        int sj = minHeap[j];

        minHeap[i] = sj;
        minHeap[j] = si;

        // update positions
        minPos[si] = j;
        minPos[sj] = i;
    }

    private void maxSwap(int i, int j) {
        int si = maxHeap[i];
        int sj = maxHeap[j];

        maxHeap[i] = sj;
        maxHeap[j] = si;

        // update positions
        maxPos[si] = j;
        maxPos[sj] = i;
    }

    /** --------------------------- BUBBLE (min) --------------------------- */

    private void bubbleUpMin(int i) {
        while (i > 0) {
            int p = parent(i);
            int si = minHeap[i];
            int sp = minHeap[p];
            if (ds.get(sp) <= ds.get(si)) break;
            minSwap(i, p);
            i = p;
        }
    }

    private void bubbleDownMin(int i) {
        while (true) {
            int left = 2 * i + 1;
            int right = left + 1;
            if (left >= nMin) break;
            int smallest = left;
            if (right < nMin && ds.get(minHeap[right]) < ds.get(minHeap[left])) smallest = right;
            if (ds.get(minHeap[i]) <= ds.get(minHeap[smallest])) break;
            minSwap(i, smallest);
            i = smallest;
        }
    }

    /** --------------------------- BUBBLE (max) --------------------------- */

    private void bubbleUpMax(int i) {
        while (i > 0) {
            int p = parent(i);
            int si = maxHeap[i];
            int sp = maxHeap[p];
            if (ds.get(sp) >= ds.get(si)) break;
            maxSwap(i, p);
            i = p;
        }
    }

    private void bubbleDownMax(int i) {
        while (true) {
            int left = 2 * i + 1;
            int right = left + 1;
            if (left >= nMax) break;
            int largest = left;
            if (right < nMax && ds.get(maxHeap[right]) > ds.get(maxHeap[left])) largest = right;
            if (ds.get(maxHeap[i]) >= ds.get(maxHeap[largest])) break;
            maxSwap(i, largest);
            i = largest;
        }
    }

    /** --------------------------- REMOVE BY STACK INDEX --------------------------- */

    // remove stackIndex from minHeap (wherever it sits), maintains minPos
    private void removeMinHeap(int stackIndex) {
        int idx = minPos[stackIndex];
        if (idx == -1) return; // not present (shouldn't happen normally)

        // move last into idx
        nMin--;
        minPos[stackIndex] = -1; // no longer in heap
        if (idx != nMin) {
            int movedStackIndex = minHeap[nMin];
            minHeap[idx] = movedStackIndex;
            minPos[movedStackIndex] = idx;
            // heapify both directions
            bubbleDownMin(idx);
            bubbleUpMin(idx);
        }
        // else we removed last, nothing else to do
    }

    private void removeMaxHeap(int stackIndex) {
        int idx = maxPos[stackIndex];
        if (idx == -1) return;

        nMax--;
        maxPos[stackIndex] = -1;
        if (idx != nMax) {
            int movedStackIndex = maxHeap[nMax];
            maxHeap[idx] = movedStackIndex;
            maxPos[movedStackIndex] = idx;
            bubbleDownMax(idx);
            bubbleUpMax(idx);
        }
    }

    /** --------------------------- POP --------------------------- */

    public Integer pop() {
        if (ds.size() == 0) return null;
        int lastIndex = ds.size() - 1;

        // update odd tree: set last leaf to 0
        if (lastIndex >= 0) {
            // ensure tree covers current size (it will), then zero the leaf
            ensureOddTreeCapacity(ds.size());
            oddTreeSetLeaf(lastIndex, 0);
        }

        // remove last from heaps using its stack index
        removeMinHeap(lastIndex);
        removeMaxHeap(lastIndex);

        // remove from ds
        return ds.remove(lastIndex);
    }

    /** --------------------------- GET / SET --------------------------- */

    public Integer get(int i) {
        if (i < 0 || i >= ds.size()) return null;
        return ds.get(i);
    }

    /**
     * set stack position i to x.
     * After changing the value, we must restore heap properties at the corresponding heap positions.
     *
     * Optimized: decide the single direction for each heap (up or down) based on old vs new value.
     * Also updates the odd tree leaf for index i.
     */
    public Integer set(int i, int x) {
        if (i < 0 || i >= ds.size()) return null;
        int old = ds.get(i);
        if (old == x) {
            // no change, nothing to do
            return old;
        }

        ds.set(i, x);

        // update odd tree leaf for position i
        ensureOddTreeCapacity(ds.size());
        oddTreeSetLeaf(i, (x % 2 != 0) ? 1 : 0);

        // MIN HEAP adjustment: if new value smaller -> bubble up; if larger -> bubble down
        int minHeapPos = minPos[i];
        if (minHeapPos != -1) {
            if (x < old) {
                bubbleUpMin(minHeapPos);
            } else { // x > old
                bubbleDownMin(minHeapPos);
            }
        }

        // MAX HEAP adjustment: if new value larger -> bubble up; if smaller -> bubble down
        int maxHeapPos = maxPos[i];
        if (maxHeapPos != -1) {
            if (x > old) {
                bubbleUpMax(maxHeapPos);
            } else { // x < old
                bubbleDownMax(maxHeapPos);
            }
        }

        return old;
    }

    /** --------------------------- partial-odd-sum tree helpers --------------------------- */

    // Ensure the oddTree has a power-of-two leaves >= n
    private void ensureOddTreeCapacity(int n) {
        if (n <= 0) {
            // reset to minimal tree
            if (oddLeaves != 1) {
                oddLeaves = 1;
                oddTree = new int[2 * oddLeaves];
            }
            return;
        }
        int L = 1;
        while (L < n) L <<= 1;
        if (L == oddLeaves) return;

        // need to grow (or shrink) the tree; rebuild from ds
        oddLeaves = L;
        oddTree = new int[2 * oddLeaves];
        buildOddTree();
    }

    // Build the odd tree from current ds (used when we (re)allocate)
    private void buildOddTree() {
        int n = ds.size();
        // leaves at indices oddLeaves .. oddLeaves + oddLeaves -1
        // fill leaf values for actual elements, zero for unused leaves
        for (int i = 0; i < oddLeaves; i++) {
            int leafIdx = oddLeaves + i;
            if (i < n) {
                oddTree[leafIdx] = (ds.get(i) % 2 != 0) ? 1 : 0;
            } else {
                oddTree[leafIdx] = 0;
            }
        }
        // build internal nodes
        for (int v = oddLeaves - 1; v >= 1; v--) {
            oddTree[v] = oddTree[2 * v] + oddTree[2 * v + 1];
        }
    }

    // set leaf at stack index (0-based) to val (0 or 1) and update parents
    private void oddTreeSetLeaf(int stackIndex, int val) {
        if (stackIndex < 0) return;
        // if tree doesn't cover this index, ensure capacity and rebuild (rare)
        if (stackIndex >= oddLeaves) {
            ensureOddTreeCapacity(stackIndex + 1);
        }
        int leaf = oddLeaves + stackIndex;
        oddTree[leaf] = val;
        leaf >>= 1;
        while (leaf >= 1) {
            oddTree[leaf] = oddTree[2 * leaf] + oddTree[2 * leaf + 1];
            leaf >>= 1;
        }
    }

    // Query number of odd values in index range [l..r] inclusive (0-based)
    private int oddTreeRangeSum(int l, int r) {
        if (l > r) return 0;
        int n = ds.size();
        if (n == 0) return 0;
        if (l < 0) l = 0;
        if (r >= n) r = n - 1;

        ensureOddTreeCapacity(n);

        int left = oddLeaves + l;
        int right = oddLeaves + r;
        int sum = 0;
        while (left <= right) {
            if ((left & 1) == 1) sum += oddTree[left++];
            if ((right & 1) == 0) sum += oddTree[right--];
            left >>= 1;
            right >>= 1;
        }
        return sum;
    }

    /** --------------------------- topKodd, maxDiff, size, iterator --------------------------- */

    public int topKodd(int k) {
        int n = ds.size();
        if (k <= 0 || n == 0) return 0;
        if (k > n) k = n;

        // query odd count in last k elements: indices [n-k .. n-1]
        return oddTreeRangeSum(n - k, n - 1);
    }

    public Integer maxDiff() {
        if (ds.size() < 2) return null;
        if (nMax == 0 || nMin == 0) return null;
        int maxVal = ds.get(maxHeap[0]);
        int minVal = ds.get(minHeap[0]);
        return maxVal - minVal;
    }

    public int size() {
        return ds.size();
    }

    public Iterator<Integer> iterator() {
        return ds.iterator();
    }
}
