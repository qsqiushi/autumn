package com.autumn.core;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 加载自定yaml文件属性
 *
 * @author qiushi
 * @author qiushi
 * @since 2019-07-09 10:59
 */
public class YamlPropertyLoaderFactory extends DefaultPropertySourceFactory {
  @Override
  public PropertySource<?> createPropertySource(String name, EncodedResource resource)
      throws IOException {
    if (Objects.isNull(resource)) {
      return super.createPropertySource(name, resource);
    }

    List<PropertySource<?>> ps =
        new YamlPropertySourceLoader()
            .load(resource.getResource().getFilename(), resource.getResource());
    return ps.isEmpty() ? null : ps.get(0);
  }
}
