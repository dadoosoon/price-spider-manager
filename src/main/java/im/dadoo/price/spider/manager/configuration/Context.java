/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package im.dadoo.price.spider.manager.configuration;

import im.dadoo.logger.client.LoggerClient;
import im.dadoo.logger.client.impl.DefaultLoggerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 *
 * @author codekitten
 */
@Configuration
@EnableWebMvc
@ComponentScan("im.dadoo.price.spider.manager")
public class Context {
  
  @Bean
  public LoggerClient loggerClient() {
    return new DefaultLoggerClient("http://log.dadoo.im/logger");
  }
}
