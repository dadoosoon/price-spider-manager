/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package im.dadoo.price.spider.manager.controller;

import im.dadoo.price.core.domain.Link;
import im.dadoo.price.core.domain.Record;
import im.dadoo.price.spider.manager.service.CenterService;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author codekitten
 */
@Controller
public class CenterController {
  
  private static final Logger logger = LoggerFactory.getLogger(CenterController.class);
  
  @Autowired
  private CenterService centerService;
  
  @Autowired
  private ObjectMapper mapper;
  
  @RequestMapping(value = "/center", method = RequestMethod.GET)
  @ResponseBody
  public Link select() {
    Link link = this.centerService.select();
    logger.info(String.format("请求:未发送队列还剩%d,未处理队列还剩%d", 
            this.centerService.getPreLinksSize(), 
            this.centerService.getProMapSize()));
    return link;
  }
  
  @RequestMapping(value = "/center", method = RequestMethod.POST)
  @ResponseBody
  public Boolean save(@RequestParam String record) {
    Boolean b = false;
    try {
      Record r = this.mapper.readValue(record, Record.class);
      b = this.centerService.save(r);
      logger.info(String.format("保存:未发送队列还剩%d,未处理队列还剩%d", 
            this.centerService.getPreLinksSize(), 
            this.centerService.getProMapSize()));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return b;
  }
}
