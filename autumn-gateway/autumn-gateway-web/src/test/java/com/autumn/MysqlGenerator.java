package com.autumn;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * mysql 代码生成器演示例子
 *
 * @author jobob
 * @since 2018-09-12
 */
public class MysqlGenerator {

  public static void main(String[] args) {

    List<String> tables = new ArrayList<>();
    tables.add("config");
    tables.add("config_param");
    tables.add("group");
    tables.add("resource");
    tables.add("resource_config");
    tables.add("resource_config_param");

    for (String tableName : tables) {
      generater(tableName);
    }
  }

  /** RUN THIS */
  public static void generater(String tableName) {
    // 代码生成器
    AutoGenerator mpg = new AutoGenerator();

    // 全局配置
    GlobalConfig gc = new GlobalConfig();
    String projectPath = System.getProperty("user.dir");
    System.out.println(projectPath);
    gc.setOutputDir(projectPath + "/src/main/java");
    gc.setAuthor("自动生成");
    gc.setOpen(false);
    mpg.setGlobalConfig(gc);

    // 数据源配置
    DataSourceConfig dsc = new DataSourceConfig();
    dsc.setUrl(
        "jdbc:mysql://pro.rdsmajl7aar4fdc.rds.bd.baidubce.com:3306/api_gateway?useUnicode=true&useSSL=false&characterEncoding=utf8");
    // dsc.setSchemaName("public");
    dsc.setDriverName("com.mysql.cj.jdbc.Driver");
    dsc.setUsername("rdsroot");
    dsc.setPassword("Dataearth2018");
    mpg.setDataSource(dsc);

    // 包配置
    PackageConfig pc = new PackageConfig();
    // pc.setModuleName(scanner("模块名"));
    pc.setParent("com.airlook.gateway");
    mpg.setPackageInfo(pc);

    // 自定义配置
    InjectionConfig cfg =
        new InjectionConfig() {
          @Override
          public void initMap() {
            // to do nothing
          }
        };
    List<FileOutConfig> focList = new ArrayList<>();
    focList.add(
        new FileOutConfig("/templates/mapper.xml.ftl") {
          @Override
          public String outputFile(TableInfo tableInfo) {
            // 自定义输入文件名称
            return projectPath
                + "/src/main/resources/mapper/"
                + (pc.getModuleName() == null ? "" : pc.getModuleName() + "/")
                + tableInfo.getEntityName()
                + "Mapper"
                + StringPool.DOT_XML;
          }
        });
    cfg.setFileOutConfigList(focList);
    mpg.setCfg(cfg);
    mpg.setTemplate(new TemplateConfig().setXml(null));

    // 策略配置
    StrategyConfig strategy = new StrategyConfig();
    strategy.setNaming(NamingStrategy.underline_to_camel);
    strategy.setColumnNaming(NamingStrategy.underline_to_camel);
    // strategy.setSuperEntityClass("com.airlook.ceph.base.BasePO");
    strategy.setEntityLombokModel(true);
    // strategy.setSuperControllerClass("com.airlook.map.iserver.common.BaseController");
    strategy.setInclude(tableName);
    strategy.setSuperEntityColumns("id");
    strategy.setControllerMappingHyphenStyle(true);
    strategy.setTablePrefix(pc.getModuleName() + "_");
    mpg.setStrategy(strategy);
    // 选择 freemarker 引擎需要指定如下加，注意 pom 依赖必须有！
    mpg.setTemplateEngine(new FreemarkerTemplateEngine());
    mpg.execute();
  }
}
