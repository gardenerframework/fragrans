package io.gardenerframework.fragrans.data.practice.configuration;

import io.gardenerframework.fragrans.data.practice.DataDomainCommonPracticePackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangHan
 * @date 2022/6/17 1:39
 */
@Configuration
@ComponentScan(basePackageClasses = DataDomainCommonPracticePackage.class, includeFilters = @ComponentScan.Filter(DataDomainCommonPracticeComponent.class))
public class DataDomainCommonPracticeConfiguration {

}
