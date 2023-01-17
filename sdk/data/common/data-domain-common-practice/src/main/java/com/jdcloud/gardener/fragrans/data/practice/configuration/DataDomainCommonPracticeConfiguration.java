package com.jdcloud.gardener.fragrans.data.practice.configuration;

import com.jdcloud.gardener.fragrans.data.practice.DataDomainCommonPracticePackage;
import com.jdcloud.gardener.fragrans.data.practice.operation.CommonOperations;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangHan
 * @date 2022/6/17 1:39
 */
@Configuration
@ComponentScan(basePackageClasses = DataDomainCommonPracticePackage.class)
@MapperScan(basePackageClasses = CommonOperations.CommonMapper.class)
public class DataDomainCommonPracticeConfiguration {

}
