package Solvers;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class AStar_Solver {
    public static void main(String[] args) {
        for (int test = 1 ; test <= 7; test++) {
            Scanner scanner;
            String fileName = "Inputs/input" + test + ".txt";
            try { 
                scanner = new Scanner(new File(fileName));
            } catch (Exception e) {
                scanner = new Scanner(System.in);
            }
            int nrCities = scanner.nextInt();
            int start = scanner.nextInt();
            int dist[][] = new int[nrCities][nrCities];
            for (int i = 0; i < nrCities; i++)
                for (int j = 0; j < nrCities; j++)
                    dist[i][j] = scanner.nextInt();
            AStar_Solver solver = new AStar_Solver(dist, nrCities, start);
            long startTime = System.currentTimeMillis();
            Path bestPath = solver.solve();
            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("Testul " + test + ":");
            System.out.println(bestPath);
            System.out.println("Timp total in ms:");
            System.out.println(totalTime);
        }
    }
    public int dist[][];
    public int nrCities;
    public int start;
    public int averageDistance = -1;
    public PriorityQueue<Path> queue = new PriorityQueue<>();
    public Path bestPath;
    public static int INF = 999999999;
    public AStar_Solver () {}
    public AStar_Solver (int _dist[][], int _nrCities, int _start) {
        dist = _dist; nrCities = _nrCities; start = _start; 
    }
    public Path solve() {
        Path initialPath = new Path(this);
        initialPath.add(start, 0); 
        Path bestPath = new Path(this); 
        bestPath.cost = INF;
        queue.add(initialPath);
        while (!queue.isEmpty()) {
            Path path = queue.poll();
            if (path.cost > bestPath.cost)
                continue;
            if (path.getLength() == nrCities) {
                path.add(start, dist[path.getLast()][start]);
                if (path.cost < bestPath.cost)
                    bestPath = path;
            } else {
                for (int i = 0; i < nrCities; i++) {
                    if (path.notVisited(i)) {
                        Path newPath = new Path(path);
                        newPath.add(i, dist[path.getLast()][i]);
                        queue.add(newPath);
                    }
                }
            }
        }
        return bestPath;
    }
    public int getAverageDist() {
        if (averageDistance < 0) {
            int sum = 0;
            for (int i = 0; i < nrCities; i++)
                for (int j = i+1; j < nrCities; j++)
                    sum += dist[i][j];
            averageDistance = sum /((nrCities * (nrCities-1)) / 2);
        }
        return averageDistance;
    }
    private class Path implements Comparable<Path> {
        public AStar_Solver solver;
        public List <Integer> cities = new ArrayList<>();
        public int cost = 0;
        public int heuristic() {
            int n = solver.nrCities;
            int avg = solver.getAverageDist();
            return (n-this.getLength()) * avg;
        }
        public Path (Path copy) {
            ///constructor de copiere: cand fac NewPath = [path|newCity]
            ///intai copiez Path in Newpath si dupa adaug newCity
            cities = new ArrayList<Integer>(copy.cities);
            cost = copy.cost;
            solver = copy.solver;
        }
        public Path(AStar_Solver solver) {
            this.solver = solver;
        }
        public int getLast () {
            ///ultimul oras vizitat
            ///merge sa il gasesc asa fiindca e ArrayList
            return cities.get(cities.size()-1).intValue();
        }
        public int getLength() {return cities.size();}

        public void add(int newCity, int dist) {
            cities.add(Integer.valueOf(newCity));
            cost += dist;
        }
        public Boolean notVisited(int i) {
            for (Integer city: cities)
                if (city.intValue() == i) 
                    return false;
            return true;
        } 
        public String toString() {
            String str = "";
            str += "Drumul este:\n";
            for (Integer city: cities) str += city + " ";
            str += "\nCostul este:\n"; 
            str += cost;
            return str;
        }
        @Override
        public int compareTo(Path path) {
           if (this.cost + this.heuristic() == path.cost + path.heuristic()) return 0;
           return (this.cost + this.heuristic() < path.cost + path.heuristic()) ? -1 : 1;
        }
    }
}