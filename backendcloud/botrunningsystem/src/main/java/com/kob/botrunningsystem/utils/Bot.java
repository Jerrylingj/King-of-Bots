package com.kob.botrunningsystem.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Bot implements java.util.function.Supplier<Integer> {
    static class Cell {
        public int x, y;
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cell cell = (Cell) o;
            return x == cell.x && y == cell.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }

    // 核心优化参数
    private static final int[][] DIRS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    private static final int MAX_DEPTH = 6;
    private static final long TIME_LIMIT = 95; // 毫秒
    private static final Map<Long, Double> TRANSPOSITION_TABLE = new ConcurrentHashMap<>();
    private static final ZobristHasher ZOBRIST = new ZobristHasher();

    // 历史启发表
    private static final Map<String, Integer> HISTORY_HEURISTIC = new HashMap<>();

    private boolean checkTailIncreasing(int step) {
        if (step <= 10) return true;
        return step % 3 == 1;
    }

    public List<Cell> getCells(int sx, int sy, String steps) {
        steps = steps.substring(1, steps.length() - 1);
        List<Cell> res = new ArrayList<>();
        int x = sx, y = sy;
        res.add(new Cell(x, y));
        int step = 0;

        for (int i = 0; i < steps.length(); i++) {
            int d = steps.charAt(i) - '0';
            x += DIRS[d][0];
            y += DIRS[d][1];
            res.add(new Cell(x, y));
            if (!checkTailIncreasing(++step)) res.remove(0);
        }
        return res;
    }

    public Integer nextMove(String input) {
        long startTime = System.currentTimeMillis();
        String[] strs = input.split("#");

        // 解析游戏状态
        GameState state = parseGameState(strs);
        int bestDir = iterativeDeepeningSearch(state, startTime);

        return bestDir != -1 ? bestDir : findAnyValidMove(state);
    }

    private GameState parseGameState(String[] strs) {
        int[][] staticObstacles = new int[13][14];
        for (int i = 0, k = 0; i < 13; i++)
            for (int j = 0; j < 14; j++, k++)
                staticObstacles[i][j] = strs[0].charAt(k) == '1' ? 1 : 0;

        List<Cell> aCells = getCells(Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), strs[3]);
        List<Cell> bCells = getCells(Integer.parseInt(strs[4]), Integer.parseInt(strs[5]), strs[6]);
        int globalStep = (strs[3].length()-2) + (strs[6].length()-2);

        return new GameState(aCells, bCells, globalStep, staticObstacles, true);
    }

    private int iterativeDeepeningSearch(GameState state, long startTime) {
        int bestDir = -1;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (int depth = 3; depth <= MAX_DEPTH; depth++) {
            long remainingTime = TIME_LIMIT - (System.currentTimeMillis() - startTime);
            if (remainingTime < 10) break;

            SearchResult result = alphaBetaSearch(state, depth, remainingTime);
            if (result.score > bestScore) {
                bestScore = result.score;
                bestDir = result.dir;
            }
        }
        return bestDir;
    }

    private SearchResult alphaBetaSearch(GameState state, int depth, long remainingTime) {
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        int bestDir = -1;
        double maxScore = Double.NEGATIVE_INFINITY;

        List<Integer> legalMoves = orderMoves(state.getLegalMoves(true));
        for (int dir : legalMoves) {
            long hash = ZOBRIST.hash(state);
            if (TRANSPOSITION_TABLE.containsKey(hash)) {
                double score = TRANSPOSITION_TABLE.get(hash);
                if (score > maxScore) {
                    maxScore = score;
                    bestDir = dir;
                }
                continue;
            }

            GameState nextState = state.generateNextState(dir, true);
            double score = minimax(nextState, depth-1, alpha, beta, false,
                    System.currentTimeMillis(), remainingTime);

            TRANSPOSITION_TABLE.put(hash, score);
            if (score > maxScore) {
                maxScore = score;
                bestDir = dir;
            }
            alpha = Math.max(alpha, score);
        }
        return new SearchResult(bestDir, maxScore);
    }

    private double minimax(GameState state, int depth, double alpha, double beta,
                           boolean isMax, long startTime, long timeLimit) {
        if (System.currentTimeMillis() - startTime > timeLimit)
            return isMax ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

        if (depth == 0 || state.isTerminal())
            return evaluateState(state);

        long hash = ZOBRIST.hash(state);
        if (TRANSPOSITION_TABLE.containsKey(hash))
            return TRANSPOSITION_TABLE.get(hash);

        List<Integer> legalMoves = orderMoves(state.getLegalMoves(isMax));
        if (legalMoves.isEmpty())
            return isMax ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

        double value = isMax ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        for (int dir : legalMoves) {
            GameState child = state.generateNextState(dir, isMax);
            double eval = minimax(child, depth-1, alpha, beta, !isMax, startTime, timeLimit);

            if (isMax) {
                value = Math.max(value, eval);
                alpha = Math.max(alpha, value);
            } else {
                value = Math.min(value, eval);
                beta = Math.min(beta, value);
            }

            if (beta <= alpha) break;
        }
        TRANSPOSITION_TABLE.put(hash, value);
        return value;
    }

    private List<Integer> orderMoves(List<Integer> moves) {
        moves.sort((a, b) ->
                HISTORY_HEURISTIC.getOrDefault(b, 0) - HISTORY_HEURISTIC.getOrDefault(a, 0));
        return moves;
    }

    private double evaluateState(GameState state) {
        if (state.aCells.isEmpty()) return -10000;
        if (state.bCells.isEmpty()) return 10000;

        // Voronoi空间划分
        int mySpace = floodFill(state, state.aCells.getLast(), true);
        int enemySpace = floodFill(state, state.bCells.getLast(), false);

        // 动态威胁评估
        int lethalSteps = calculateLethalSteps(state);

        // 位置优势
        int distance = manhattanDistance(state.aCells.getLast(), state.bCells.getLast());

        return 1.5 * (mySpace - enemySpace)
                + 0.8 * (100 - enemySpace)
                - 0.3 * lethalSteps
                + 0.2 * (14 - distance);
    }

    private int floodFill(GameState state, Cell start, boolean isMine) {
        boolean[][] visited = new boolean[13][14];
        Queue<Cell> queue = new LinkedList<>();
        queue.add(start);
        visited[start.x][start.y] = true;
        int count = 0;

        while (!queue.isEmpty()) {
            Cell cell = queue.poll();
            count++;

            for (int[] dir : DIRS) {
                int nx = cell.x + dir[0];
                int ny = cell.y + dir[1];

                if (isValid(nx, ny) && !visited[nx][ny] && !isOccupied(state, nx, ny)) {
                    visited[nx][ny] = true;
                    queue.add(new Cell(nx, ny));
                }
            }
        }
        return count;
    }

    private int calculateLethalSteps(GameState state) {
        int[][] dist = new int[13][14];
        Arrays.stream(dist).forEach(arr -> Arrays.fill(arr, -1));
        Queue<Cell> q = new LinkedList<>();

        Cell enemyHead = state.bCells.getLast();
        q.add(enemyHead);
        dist[enemyHead.x][enemyHead.y] = 0;

        while (!q.isEmpty()) {
            Cell c = q.poll();
            for (int[] dir : DIRS) {
                int nx = c.x + dir[0], ny = c.y + dir[1];
                if (isValid(nx, ny) && dist[nx][ny] == -1) {
                    dist[nx][ny] = dist[c.x][c.y] + 1;
                    q.add(new Cell(nx, ny));
                }
            }
        }

        return Arrays.stream(dist)
                .flatMapToInt(Arrays::stream)
                .filter(d -> d > 0)
                .min().orElse(Integer.MAX_VALUE);
    }

    private int findAnyValidMove(GameState state) {
        Cell head = state.aCells.getLast();
        for (int d = 0; d < 4; d++) {
            int nx = head.x + DIRS[d][0];
            int ny = head.y + DIRS[d][1];
            if (isValid(nx, ny) && !isOccupied(state, nx, ny)) {
                return d;
            }
        }
        return 0;
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < 13 && y >= 0 && y < 14;
    }

    private boolean isOccupied(GameState state, int x, int y) {
        return state.staticObstacles[x][y] == 1 ||
                state.aCells.contains(new Cell(x, y)) ||
                state.bCells.contains(new Cell(x, y));
    }

    private int manhattanDistance(Cell a, Cell b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    static class SearchResult {
        int dir;
        double score;
        SearchResult(int dir, double score) {
            this.dir = dir;
            this.score = score;
        }
    }

    static class ZobristHasher {
        private final long[][][] table = new long[13][14][2];
        public ZobristHasher() {
            Random rand = new Random(0xDEADBEEF);
            for (int i = 0; i < 13; i++)
                for (int j = 0; j < 14; j++)
                    for (int k = 0; k < 2; k++)
                        table[i][j][k] = rand.nextLong();
        }

        long hash(GameState state) {
            long h = 0;
            for (Cell c : state.aCells) h ^= table[c.x][c.y][0];
            for (Cell c : state.bCells) h ^= table[c.x][c.y][1];
            return h;
        }
    }

    class GameState {
        LinkedList<Cell> aCells;
        LinkedList<Cell> bCells;
        int globalStep;
        int[][] staticObstacles;
        boolean isMaxTurn;

        public GameState(List<Cell> aCells, List<Cell> bCells, int globalStep,
                         int[][] staticObstacles, boolean isMaxTurn) {
            this.aCells = new LinkedList<>(aCells);
            this.bCells = new LinkedList<>(bCells);
            this.globalStep = globalStep;
            this.staticObstacles = staticObstacles;
            this.isMaxTurn = isMaxTurn;
        }

        List<Integer> getLegalMoves(boolean forMax) {
            LinkedList<Cell> current = forMax ? aCells : bCells;
            Cell head = current.getLast();
            List<Integer> moves = new ArrayList<>();

            for (int d = 0; d < 4; d++) {
                int nx = head.x + DIRS[d][0];
                int ny = head.y + DIRS[d][1];

                if (isValid(nx, ny) && !isOccupied(nx, ny)) {
                    moves.add(d);
                }
            }
            return moves;
        }

        GameState generateNextState(int dir, boolean isMaxMove) {
            LinkedList<Cell> newA = new LinkedList<>(aCells);
            LinkedList<Cell> newB = new LinkedList<>(bCells);

            if (isMaxMove) {
                Cell newHead = moveHead(newA.getLast(), dir);
                newA.add(newHead);
                if (!checkTailIncreasing(globalStep + 1)) newA.removeFirst();
            } else {
                Cell newHead = moveHead(newB.getLast(), dir);
                newB.add(newHead);
                if (!checkTailIncreasing(globalStep + 1)) newB.removeFirst();
            }

            return new GameState(newA, newB, globalStep + 1, staticObstacles, !isMaxTurn);
        }

        boolean isTerminal() {
            return aCells.isEmpty() || bCells.isEmpty();
        }

        private Cell moveHead(Cell head, int dir) {
            return new Cell(head.x + DIRS[dir][0], head.y + DIRS[dir][1]);
        }

        private boolean isValid(int x, int y) {
            return x >= 0 && x < 13 && y >= 0 && y < 14;
        }

        private boolean isOccupied(int x, int y) {
            return staticObstacles[x][y] == 1 ||
                    aCells.contains(new Cell(x, y)) ||
                    bCells.contains(new Cell(x, y));
        }
    }

    @Override
    public Integer get() {
        try {
            return nextMove(new Scanner(new File("input.txt")).next());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}