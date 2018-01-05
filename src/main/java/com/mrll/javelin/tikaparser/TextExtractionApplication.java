package com.mrll.javelin.tikaparser;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import com.google.common.base.Predicates;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Tesseract1;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @description The Application class is basic configuration class for
 *              springboot application to set starting level of application.
 * 
 * @SpringBootApplication is a convenience annotation that adds all of the
 *                        following:
 */
@SpringBootApplication
@EnableSwagger2
//@EnableDiscoveryClient
public class TextExtractionApplication {
	/**
	 * Method provides functionality to launch MicroService .
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(TextExtractionApplication.class, args);
	}
	
	@Bean
    public Docket newsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
      
                .groupName("Tika_OCR_SERVICE")
                .apiInfo(apiInfo())
                .select()
                .paths(Predicates.in(Arrays.asList("/api/ocr")))
                .build();
    }
     
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Tika OCR service")
                .description("Tika service using tesseract")
                .build();
    }

    @Bean
    public Tesseract tesseract(){
    	return new Tesseract();
    }
}
