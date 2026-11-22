package MathProject;
import java.util.*;
import java.util.function.Function;


public class RelationAnalyzer {
    static final int N = 5;

    static List<List<Integer>> emptyMatrix() {
        List<List<Integer>> m = new ArrayList<>();
        for (int i = 0; i < N; i++)
            m.add(new ArrayList<>(Collections.nCopies(N, 0)));
        return m;
    }

    static List<List<Integer>> copy(List<List<Integer>> m) {
        List<List<Integer>> result = new ArrayList<>();
        for (List<Integer> row : m) result.add(new ArrayList<>(row));
        return result;
    }

    // BEGIN input
    static List<List<Integer>> readMatrix(Scanner in) {
        List<List<Integer>> m = new ArrayList<>();
        while (m.size() < N) {
            System.out.print((m.size() + 1) + "행 (0/1 5개): ");
            if (!in.hasNextLine()) throw new IllegalArgumentException("입력 부족");
            String[] tokens = in.nextLine().trim().split("\\s+");
            List<Integer> row = new ArrayList<>();
            boolean valid = tokens.length == N;
            for (String token : tokens) {
                if (!token.equals("0") && !token.equals("1")) valid = false;
                else row.add(Integer.parseInt(token));
            }
            if (valid) m.add(row);
            else System.out.println("0 또는 1을 정확히 5개 입력하세요.");
        }
        return m;
    }
    // END input

    // BEGIN properties
    static boolean isReflexive(List<List<Integer>> m) {
        for (int i = 0; i < N; i++) if (m.get(i).get(i) != 1) return false;
        return true;
    }
    static boolean isSymmetric(List<List<Integer>> m) {
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++)
            if (!m.get(i).get(j).equals(m.get(j).get(i))) return false;
        return true;
    }
    static boolean isTransitive(List<List<Integer>> m) {
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++)
            for (int k = 0; k < N; k++)
                if (m.get(i).get(j) == 1 && m.get(j).get(k) == 1
                        && m.get(i).get(k) == 0) return false;
        return true;
    }
    // END properties

    static boolean isEquivalence(List<List<Integer>> m) {
        return isReflexive(m) && isSymmetric(m) && isTransitive(m);
    }

    // BEGIN classes
    static List<Set<Integer>> equivalenceClasses(List<List<Integer>> m) {
        if (!isEquivalence(m)) throw new IllegalArgumentException("동치 관계 아님");
        List<Set<Integer>> classes = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            Set<Integer> group = new LinkedHashSet<>();
            for (int j = 0; j < N; j++)
                if (m.get(i).get(j) == 1) group.add(j + 1);
            classes.add(group);
        }
        return classes;
    }
    // END classes

    // BEGIN closures
    static List<List<Integer>> reflexiveClosure(List<List<Integer>> m) {
        List<List<Integer>> c = copy(m);
        for (int i = 0; i < N; i++) c.get(i).set(i, 1);
        return c;
    }
    static List<List<Integer>> symmetricClosure(List<List<Integer>> m) {
        List<List<Integer>> c = copy(m);
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++)
            if (m.get(i).get(j) == 1) c.get(j).set(i, 1);
        return c;
    }
    static List<List<Integer>> transitiveClosure(List<List<Integer>> m) {
        List<List<Integer>> c = copy(m);
        for (int k = 0; k < N; k++) for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (c.get(i).get(k) == 1 && c.get(k).get(j) == 1)
                    c.get(i).set(j, 1);
        return c;
    }
    // END closures

    // BEGIN compose
    // compose(R, S): first R, then S. Result is S composed with R.
    static List<List<Integer>> compose(List<List<Integer>> r,
                                        List<List<Integer>> s) {
        List<List<Integer>> result = emptyMatrix();
        for (int i = 0; i < N; i++) for (int k = 0; k < N; k++)
            for (int j = 0; j < N; j++)
                if (r.get(i).get(j) == 1 && s.get(j).get(k) == 1) {
                    result.get(i).set(k, 1);
                    break;
                }
        return result;
    }
    // END compose

    // BEGIN power
    static List<List<Integer>> relationPower(List<List<Integer>> r, int n) {
        if (n < 0) throw new IllegalArgumentException("지수는 0 이상");
        List<List<Integer>> result = reflexiveClosure(emptyMatrix());
        for (int count = 0; count < n; count++) result = compose(result, r);
        return result;
    }
    // END power

    static String pairsText(List<List<Integer>> m) {
        StringJoiner joiner = new StringJoiner(", ", "{", "}");
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++)
            if (m.get(i).get(j) == 1) joiner.add("(" + (i+1) + "," + (j+1) + ")");
        return joiner.toString();
    }

    static void printMatrix(List<List<Integer>> m) {
        for (List<Integer> row : m) {
            StringJoiner joiner = new StringJoiner(" ");
            for (int value : row) joiner.add(String.valueOf(value));
            System.out.println(joiner);
        }
    }

    static void showStatus(List<List<Integer>> m) {
        System.out.printf("반사 / 대칭 / 추이: %s / %s / %s%n",
                isReflexive(m) ? "O" : "X", isSymmetric(m) ? "O" : "X",
                isTransitive(m) ? "O" : "X");
        System.out.println(isEquivalence(m) ? "동치 관계입니다." : "동치 관계가 아닙니다.");
        if (isEquivalence(m)) {
            List<Set<Integer>> classes = equivalenceClasses(m);
            for (int i = 0; i < N; i++)
                System.out.println("[" + (i + 1) + "] = " + classes.get(i));
        }
    }

    // BEGIN analyze
    static void analyze(List<List<Integer>> m) {
        System.out.println("[원본 행렬]");
        printMatrix(m);
        showStatus(m);
        if (isEquivalence(m)) return;
        if (!isReflexive(m))
            reportClosure("반사", m, reflexiveClosure(m));
        if (!isSymmetric(m))
            reportClosure("대칭", m, symmetricClosure(m));
        if (!isTransitive(m))
            reportClosure("추이", m, transitiveClosure(m));
    }
    // END analyze

    static void reportClosure(String name, List<List<Integer>> original,
                              List<List<Integer>> closed) {
        System.out.println("\n[" + name + " 폐포: 원본]");
        printMatrix(original);
        System.out.println("[" + name + " 폐포: 변환 후]");
        printMatrix(closed);
        showStatus(closed);
    }

    static void extraMenu(Scanner in, List<List<Integer>> r) {
        while (true) {
            System.out.println("\n추가 기능: 1 관계 합성 / 2 거듭제곱 / 0 종료");
            if (!in.hasNextLine()) return;
            String choice = in.nextLine().trim();
            switch (choice) {
                case "0": return;
                case "1":
                    System.out.println("두 번째 관계 S를 입력하세요.");
                    List<List<Integer>> s = readMatrix(in);
                    System.out.println("[S composed with R: R 다음 S]");
                    printMatrix(compose(r, s));
                    System.out.println(pairsText(compose(r, s)));
                    break;
                case "2":
                    System.out.print("지수 n (0~20): ");
                    if (!in.hasNextLine()) return;
                    try {
                        int n = Integer.parseInt(in.nextLine().trim());
                        if (n < 0 || n > 20) throw new IllegalArgumentException();
                        printMatrix(relationPower(r, n));
                    } catch (IllegalArgumentException e) {
                        System.out.println("0~20 사이의 정수를 입력하세요.");
                    }
                    break;
                default: System.out.println("메뉴 번호를 확인하세요.");
            }
        }
    }

    static List<List<Integer>> demoMatrix(String name) {
        List<List<Integer>> m = emptyMatrix();
        if (name.equals("combined")) {
            m.get(0).set(1,1); m.get(1).set(2,1); m.get(3).set(4,1);
            return m;
        }
        for (int i = 0; i < N; i++) m.get(i).set(i, 1);
        if (name.equals("symmetric")) m.get(0).set(1, 1);
        else if (name.equals("transitive")) {
            m.get(0).set(1, 1); m.get(1).set(0, 1);
            m.get(1).set(2, 1); m.get(2).set(1, 1);
        } else {
            m.get(0).set(1, 1); m.get(1).set(0, 1);
            m.get(2).set(3, 1); m.get(3).set(2, 1);
            if (name.equals("reflexive")) m.get(4).set(4, 0);
            else if (!name.equals("equivalence")) throw new IllegalArgumentException("예제 이름 오류");
        }
        return m;
    }

    static void demoOperations() {
        List<List<Integer>> r = emptyMatrix();
        r.get(0).set(1,1); r.get(1).set(2,1);
        List<List<Integer>> s = emptyMatrix();
        s.get(1).set(3,1); s.get(2).set(4,1);
        System.out.println("R = " + pairsText(r));
        System.out.println("S = " + pairsText(s));
        System.out.println("[관계 합성: R 다음 S]");
        printMatrix(compose(r,s));
        System.out.println("S composed with R = " + pairsText(compose(r,s)));
        System.out.println("[거듭제곱 R^0]"); printMatrix(relationPower(r,0));
        System.out.println("[거듭제곱 R^2]"); printMatrix(relationPower(r,2));
        System.out.println("R^2 = " + pairsText(relationPower(r,2)));
        System.out.println("[거듭제곱 R^3]"); printMatrix(relationPower(r,3));
        System.out.println("R^3 = " + pairsText(relationPower(r,3)));
    }

    public static void main(String[] args) {
        if (args.length == 2 && args[0].equals("--demo")) {
            if (args[1].equals("operations")) demoOperations();
            else analyze(demoMatrix(args[1]));
            return;
        }
        try (Scanner in = new Scanner(System.in, "UTF-8")) {
            System.out.println("집합 A = {1, 2, 3, 4, 5}, 5×5 관계 행렬 입력");
            List<List<Integer>> r = readMatrix(in);
            analyze(r);
            extraMenu(in, r);
        }
    }
}
