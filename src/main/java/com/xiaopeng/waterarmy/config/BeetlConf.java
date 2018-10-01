package com.xiaopeng.waterarmy.config;

import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * * 功能描述：
 * <p> 版权所有：
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
@Configuration
public class BeetlConf {

    @Value("${project.version}")
    private String projectVersion;

    /**
     * 模板跟目录 ，比如 "templates"
     */
	private static final String TEMPLATE_PATH = "templates";

	@Bean(name = "beetlConfig")
	public BeetlGroupUtilConfiguration getBeetlGroupUtilConfiguration() {
		BeetlGroupUtilConfiguration beetlGroupUtilConfiguration = new BeetlGroupUtilConfiguration();
		// 获取Spring Boot 的ClassLoader
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = BeetlConf.class.getClassLoader();
		}
		Properties extProperties = new Properties();
		extProperties.setProperty("beetl.suffix", "html");
        // 额外的配置，可以覆盖默认配置，一般不需要
		beetlGroupUtilConfiguration.setConfigProperties(extProperties);
		ClasspathResourceLoader cploder = new ClasspathResourceLoader(loader, TEMPLATE_PATH);
		beetlGroupUtilConfiguration.setResourceLoader(cploder);
        Map<String,Object> vars = new HashMap<>();
        vars.put("PROJECT_VERSION", projectVersion);
        beetlGroupUtilConfiguration.setSharedVars(vars);
		beetlGroupUtilConfiguration.init();
		// 如果使用了优化编译器，涉及到字节码操作，需要添加ClassLoader
		beetlGroupUtilConfiguration.getGroupTemplate().setClassLoader(loader);
		return beetlGroupUtilConfiguration;

	}
	
	@Bean(name = "beetlViewResolver")
	public BeetlSpringViewResolver getBeetlSpringViewResolver(
			@Qualifier("beetlConfig") BeetlGroupUtilConfiguration beetlGroupUtilConfiguration) {
		BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
		beetlSpringViewResolver.setContentType("text/html;charset=UTF-8");
		beetlSpringViewResolver.setOrder(0);
		beetlSpringViewResolver.setConfig(beetlGroupUtilConfiguration);
		return beetlSpringViewResolver;
	}

}
