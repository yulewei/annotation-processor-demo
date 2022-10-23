import com.example.maker.PlusOne;

public class MakerExample {

    @PlusOne
    public int func(int x) {
        return x;
    }

    public static void main(String[] args) {
        System.out.println(new MakerExample().func(42));
    }
}