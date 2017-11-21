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
	 * 描述信息、名称
	 * 默认为空字符串
	 */
	String value() default "";
	
	/**
	 * 排序价值
	 */
	int weight() default 0;

	/**
	 * 是否属于键值
	 * 如果是，则会被放在WHERE后面，否则放在SELECT处
	 * 默认为false
	 */
	boolean isKey() default false;

	/**
	 * NOT NULL
	 * 只在Base.create时检查
	 */
	boolean notNull() default false;
	
	/**
	 * 是否由外部POI导入
	 * 默认为false
	 */
	boolean autoInit() default false;
	
	/**
	 * Static Source的类名
	 */
	String source() default "";
	
	/**
	 * 注解
	 * 默认为空字符串
	 */
	String ps() default "";

}
