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
	
	/**
	 * 是否在SQLCollection.selectAll时排序
	 */
	boolean needSorted() default false;

	/**
	 * 名称
	 * 默认为空字符串
	 */
	String value() default "";

	/**
	 * 描述信息
	 * 默认为空字符串
	 */
	String description() default "";
	
	/**
	 * 是否由外部POI导入
	 * 默认为false
	 */
	boolean needImport() default false;
}
