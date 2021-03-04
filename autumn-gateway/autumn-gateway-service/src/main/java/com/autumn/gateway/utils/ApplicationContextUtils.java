package com.autumn.gateway.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * <spring 上下文工具类>
 *
 * @author qiushi
 * @since 2021/3/3 17:24
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {

  private static ApplicationContext applicationContext = null;

  /**
   * <获取applicationContext>
   *
   * @param
   * @return : org.springframework.context.ApplicationContext
   * @author qiushi
   * @updator qiushi
   * @since 2021/3/3 17:24
   */
  public static ApplicationContext getApplicationContext() {

    return applicationContext;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    if (ApplicationContextUtils.applicationContext == null) {
      ApplicationContextUtils.applicationContext = applicationContext;
    }
  }

  /**
   * <通过name获取 Bean.>
   *
   * @param name
   * @return : java.lang.Object
   * @author qiushi
   * @updator qiushi
   * @since 2021/3/3 17:25
   */
  public static Object getBean(String name) {

    return getApplicationContext().getBean(name);
  }

  /**
   * <通过class获取Bean.>
   *
   * @param clazz
   * @return : T
   * @author qiushi
   * @updator qiushi
   * @since 2021/3/3 17:25
   */
  public static <T> T getBean(Class<T> clazz) {

    return getApplicationContext().getBean(clazz);
  }

  /**
   * <通过name,以及Clazz返回指定的Bean>
   *
   * @param name
   * @param clazz
   * @return : T
   * @author qiushi
   * @updator qiushi
   * @since 2021/3/3 17:25
   */
  public static <T> T getBean(String name, Class<T> clazz) {

    return getApplicationContext().getBean(name, clazz);
  }
}
