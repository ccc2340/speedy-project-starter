package org.speedy.common.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/09 22:56
 */
public abstract class ComponentManager<T> {

	private Class<T> baseClass;

	protected final Map<Class<?>, Object> data = new HashMap<>();

	public ComponentManager(Class<T> baseClass) {
		this.baseClass = baseClass;
	}

	/* 注册对象 */
	public void register(Object t) {
		Class<?>[] interfaces = t.getClass().getInterfaces();
		data.put(interfaces[0], t);
	}

	/* 使用基础的对象 */
	public T base() {
		return (T) data.get(baseClass);
	}

	/* 使用指定类型的对象 */
	public <SonType> SonType use(Class<SonType> clazz) {
		if (data.containsKey(clazz)) {
			return (SonType) data.get(clazz);
		}
		return null;
	}
}
