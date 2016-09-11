import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ameyapandilwar on 8/6/16.
 */
public class Subsets {

    public static void main(String[] args) {

        System.out.println(subsets(new int[] {1, 2, 3}));
    }

    private static List<List<Integer>> subsets(int[] set) {
        List<List<Integer>> subsets = new ArrayList<>();
        subsets.add(new ArrayList<>());

        Arrays.sort(set);

        for (int i = 0; i < set.length; i++) {
            int size = subsets.size();

            for (int j = 0; j < size; j++) {
                List<Integer> subset = new ArrayList<>(subsets.get(j));
                subset.add(set[i]);
                subsets.add(subset);
            }
        }

        return subsets;
    }
}