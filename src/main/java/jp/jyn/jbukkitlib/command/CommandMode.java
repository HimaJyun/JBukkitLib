package jp.jyn.jbukkitlib.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify the command mode.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandMode {
    /**
     * true if this command is player only
     *
     * @return player only
     */
    boolean playerOnly() default false;

    /**
     * Minimum required arguments for this command
     *
     * @return number of minimum argument
     */
    int minimumArgs() default 0;

    /**
     * Required permission for the execute this command.
     *
     * @return permission
     */
    String permission() default "";
}
