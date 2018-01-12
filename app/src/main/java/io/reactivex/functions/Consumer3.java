package io.reactivex.functions;

public interface Consumer3<First, Second, Third> {
    void apply(First first, Second second, Third third) throws Exception;
}
