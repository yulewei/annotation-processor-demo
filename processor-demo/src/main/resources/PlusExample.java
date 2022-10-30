import com.example.maker.PlusOne;

public class PlusExample {

    @PlusOne
    public int func(int x) {
        return x * x;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.exit(-1);
        }
        Integer arg = Integer.parseInt(args[0]);
        System.out.println(new PlusExample().func(arg));
    }
}