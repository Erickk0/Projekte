package Backtracking;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class GraphFinderBacktrackingTest {

    static void runTestBT(int[][] graph1, int[][] graph2, int[] expected) {
        int[] result = GraphFinderBacktracking.findBacktracking(graph1, graph2);
        assertArrayEquals( expected, result );
    }


    // Test Case 1: simple toy example matching 4 nodes to 6 nodes.
    // see visualization in the exercise letter.
    @Test
    public void test1() {
        int[][] graph1 = new int[][] {
                {0,1,0,0},
                {1,0,1,1},
                {0,1,0,0},
                {0,1,0,0}
        };
        int[][] graph2 =  new int[][] {
                {0,1,0,0,0,0},
                {1,0,1,1,0,0},
                {0,1,0,0,1,0},
                {0,1,0,0,0,1},
                {0,0,1,0,0,0},
                {0,0,0,1,0,0}
        };
        int[] expected = new int[] {0,1,2,3};
        runTestBT(graph1, graph2, expected);
    }


    // Test Case 2: like Test Case 1, but node order swapped
    // see visualization in the exercise letter.
    @Test
    public void test2() {
        int[][] graph1 = new int[][] {
                {0,1,0,0},
                {1,0,1,1},
                {0,1,0,0},
                {0,1,0,0}
        };
        int[][] graph2 = new int[][] {
                {0,0,1,0,0,0},
                {0,0,1,1,0,0},
                {1,1,0,0,1,0},
                {0,1,0,0,0,0},
                {0,0,1,0,0,1},
                {0,0,0,0,1,0}
        };
        int[] expected = new int[] {0,2,1,4};
        runTestBT(graph1, graph2, expected);
    }


    // Test Case 3: Greedy not optimal
    // see visualization in the exercise letter.
    @Test
    public void test3() {
        int[][] graph1 = new int[][] {
                {0,1,1,0,0,0,0,0},
                {1,0,0,0,0,0,0,0},
                {1,0,0,1,1,1,1,1},
                {0,0,1,0,0,0,0,0},
                {0,0,1,0,0,0,0,0},
                {0,0,1,0,0,0,0,0},
                {0,0,1,0,0,0,0,0},
                {0,0,1,0,0,0,0,0}
        };
        int[][] graph2 = new int[][] {
                {0,1,1,0,0,0,0,0,0},
                {1,0,0,1,0,0,0,0,0},
                {1,0,0,0,0,0,0,0,0},
                {0,1,0,0,1,1,1,1,1},
                {0,0,0,1,0,0,0,0,0},
                {0,0,0,1,0,0,0,0,0},
                {0,0,0,1,0,0,0,0,0},
                {0,0,0,1,0,0,0,0,0},
                {0,0,0,1,0,0,0,0,0}
        };
        int[] expectedBT = new int[] {1,0,3,4,5,6,7,8};
        runTestBT(graph1, graph2, expectedBT);
    }


    // Test Case 4: "Haus vom Nikolaus"
    // see visualization in the exercise letter.
    @Test
    public void test4() {
        int[][] graph1 = new int[][] {
                {0,1,1,1,1},
                {1,0,1,0,0},
                {1,1,0,1,1},
                {1,0,1,0,1},
                {1,0,1,1,0},
        };
        int[][] graph2 = new int[][] {
                // "Haus 1"
                {0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {1,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                // "Haus 2"
                {0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                // "Haus 3"
                {0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,1,0,1,1,0,0,0,0,0,0},
                // "Haus 4"
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,1},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0},
        };
        int[] expectedBT = new int[] {10,11,12,13,14};
        runTestBT(graph1, graph2, expectedBT);
    }

}
