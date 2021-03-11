package com.autumn.gateway.loadbalancer.model;

import com.autumn.gateway.enums.LoadBalancerEnum;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: autumn
 * @description: 策略
 * @author: qiushi
 * @create: 2021-03-10:17:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Strategy {

  /** 负载均衡方式 版本 */
  private static final String MODE_VERSION = "version";

  /** 负载均衡方式 权重 */
  private static final String MODE_WEIGHT = "weight";

  /** 模式 */
  private LoadBalancerEnum loadBalancerEnum;

  /** 参数 */
  private Map<String, String> params;

  public Strategy(String mode, List<String> strategies) {

    this.params = new HashMap<>();

    if (StringUtils.equals(MODE_VERSION, mode)) {
      this.loadBalancerEnum = LoadBalancerEnum.VERSION;
      for (String str : strategies) {
        String[] paramArray = str.split("-");
        // version-serverUrl
        this.params.put(paramArray[1], paramArray[0]);
      }

    } else if (StringUtils.equals(MODE_WEIGHT, mode)) {
      this.loadBalancerEnum = LoadBalancerEnum.WEIGHT;
      for (String str : strategies) {
        String[] paramArray = str.split("-");
        // serverUrl-weight
        this.params.put(paramArray[0], paramArray[1]);
      }
    }
  }
}
