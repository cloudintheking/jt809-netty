package io.github.cloudintheking.jt809.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author cloudintheking
 * @createTime 2019/9/29
 * @description 应用上下文提供器
 */
@Component
public final class ApplicationContextProvider implements ApplicationContextAware {
    private static ConfigurableApplicationContext applicationContext;

    public static ApplicationContextProvider instance() {
        return applicationContext.getBean(ApplicationContextProvider.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = (ConfigurableApplicationContext) context;
    }

    /**
     * 获取ApplicationContext对象
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 停止应用程序
     */
    public static void close() {
        if (applicationContext != null) {
            applicationContext.close();
        }
    }

    /**
     * 根据bean 类型获取bean
     *
     * @param requiredType
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }


    /**
     * 根据bean的名称获取bean
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 根据bean的名称和类型获取bean
     *
     * @param beanName
     * @param requiredType
     * @param <T>
     * @return
     */
    public static <T> T getBean(String beanName, Class<T> requiredType) {
        return applicationContext.getBean(beanName, requiredType);
    }

    /**
     * 根据bean的class来查找所有的对象（包括子类）
     */
    public static <T> Map<String, T> getBeansByClass(Class<T> c) {
        return applicationContext.getBeansOfType(c);
    }

}
