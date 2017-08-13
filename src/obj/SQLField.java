package obj;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SQLField {
	/**
	 * 是否属于键值
	 * 如果是，则会被放在WHERE后面，否则放在SELECT处
	 * 默认为false
	 */
	boolean isKey() default false;
	

}
