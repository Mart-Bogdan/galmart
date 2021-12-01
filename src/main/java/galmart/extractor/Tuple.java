package galmart.extractor;

public class Tuple<TFirst, TSecond> {
    public final TFirst First;
    public final TSecond Second;

    public Tuple(TFirst First, TSecond Second) {
        this.First = First;
        this.Second = Second;
    }
}
