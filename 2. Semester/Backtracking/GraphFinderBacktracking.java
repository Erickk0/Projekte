package Backtracking;
public class GraphFinderBacktracking {


    public static int countErrors(int[] solution, int[][] graph1, int[][] graph2) {
        int errors = 0;

        for (int i = 0; i < solution.length; i++) {
            int node1 = solution[i];
            if(node1==-1) break;
            for (int j = 0; j < i; j++) {
                int node2 = solution[j];
                if(node2==-1) break;
                if (graph1[i][j] != graph2[node1][node2]) {
                    errors++;
                }
            }
        }

        return errors;
    }

    
    public static int[] findBacktracking(int[][] graph1, int[][] graph2) {
        int[] solution = new int[graph1.length];
        for (int i = 0; i < solution.length; i++) {
            solution[i] = -1; // Empty solution initialization
        }

        int[] result = backtracking(graph1, graph2, solution);
        if (result == null) {
            return new int[0]; // Return an empty array if no solution found
        }
        return result;
    }

    private static int[] backtracking(int[][] graph1, int[][] graph2, int[] solution) {
        if (isSolutionComplete(solution)) {
            return solution;
        }

        int nextNodeIndex = getNextUnassignedNodeIndex(solution);

        int[] bestSolution = null;
        for (int node : graph2[nextNodeIndex]) {
            if (!isNodeUsed(node, solution)) {
                solution[nextNodeIndex] = node;
                int[] currentSolution = backtracking(graph1, graph2, solution);
                if (currentSolution != null && (bestSolution == null || countErrors(currentSolution, graph1, graph2) < countErrors(bestSolution, graph1, graph2))) {
                    bestSolution = currentSolution.clone();
                }
                solution[nextNodeIndex] = -1;
            }
        }

        return bestSolution;
    }

    private static boolean isSolutionComplete(int[] solution) {
        for (int node : solution) {
            if (node == -1) {
                return false;
            }
        }
        return true;
    }

    private static int getNextUnassignedNodeIndex(int[] solution) {
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] == -1) {
                return i;
            }
        }
        return -1; // Kein unzugewiesener Knoten gefunden
    }

    private static boolean isNodeUsed(int node, int[] solution) {
        for (int assignedNode : solution) {
            if (assignedNode == node) {
                return true;
            }
        }
        return false;
    }
}



