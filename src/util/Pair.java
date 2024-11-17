package util;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pair<First, Second> {
    private First first;
    private Second second;

    private Pair(First first, Second second){
        this.first = first;
        this.second = second;
    }

    public static<First, Second> Pair<First, Second> of(First first, Second second){
        return new Pair<>(first, second);
    }
}
