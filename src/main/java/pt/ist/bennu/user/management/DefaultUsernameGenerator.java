package pt.ist.bennu.user.management;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default implementation of {@link UsernameGenerator}.
 * 
 * In this implementation, all generated usernames are in the form {@code bennuXXXX} where {@code XXXX} is an increasing integer.
 * 
 * @author João Carvalho (joao.pedro.carvalho@ist.utl.pt)
 * 
 */
public class DefaultUsernameGenerator extends UsernameGenerator<Object> {

    private final AtomicInteger currentId = new AtomicInteger(0);

    @Override
    protected String doGenerate(Object ignored) {
        return "bennu" + currentId.getAndIncrement();
    }

}
