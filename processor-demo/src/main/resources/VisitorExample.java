public class VisitorExample {

    private String foo = "bar";

    public int func(int x) {
        int y = x + 1;
        return y;
    }

    public static class InnerExample {
        private int innerFoo = 42;
    }
}