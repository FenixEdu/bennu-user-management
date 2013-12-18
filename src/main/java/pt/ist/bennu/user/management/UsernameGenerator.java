package pt.ist.bennu.user.management;

import org.fenixedu.bennu.core.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for automatic username generation strategies.
 * 
 * If no generator is registered, {@link DefaultUsernameGenerator} is used.
 * 
 * @author João Carvalho (joao.pedro.carvalho@ist.utl.pt)
 * 
 */
public abstract class UsernameGenerator<T> {

    private static final Logger logger = LoggerFactory.getLogger(UsernameGenerator.class);

    /**
     * Configures the {@link UsernameGenerator} to be used for this application.
     */
    public static void setDefault(UsernameGenerator<?> generator) {
        logger.debug("Setting UsernameGenerator to: {}", generator);
        selected = generator;
    }

    protected abstract String doGenerate(T parameter);

    /*
     * Private API
     */

    private final String generateUsernameFor(T parameter) {
        while (true) {
            String username = doGenerate(parameter);
            if (User.findByUsername(username) == null) {
                logger.debug("Generated username {} for {}", username, parameter);
                return username;
            }
        }
    }

    private static UsernameGenerator<?> selected = new DefaultUsernameGenerator();

    @SuppressWarnings("unchecked")
    static <T> String generate(T parameter) {
        return ((UsernameGenerator<T>) selected).generateUsernameFor(parameter);
    }

}
