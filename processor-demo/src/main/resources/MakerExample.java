import com.example.maker.PlusOne;

public class MakerExample {

    @PlusOne
    public String func(int x) {
        return "hello";
    }

    public static void main(String[] args) {
        System.out.println(new MakerExample().func(42));
    }
}