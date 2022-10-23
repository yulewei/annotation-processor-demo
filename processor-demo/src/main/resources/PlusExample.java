import com.example.maker.PlusOne;

public class PlusExample {

    @PlusOne
    public String func(int x) {
        return "hello";
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.exit(-1);
        }
        Integer arg = Integer.parseInt(args[0]);
        System.out.println(new PlusExample().func(arg));
    }
}