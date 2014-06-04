/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package im.dadoo.price.spider.manager.controller;

import im.dadoo.price.core.domain.Link;
import im.dadoo.price.core.domain.Product;
import im.dadoo.price.core.domain.Record;
import im.dadoo.price.core.domain.Seller;
import im.dadoo.price.core.domain.util.Pair;
import im.dadoo.price.core.service.SellerService;
import im.dadoo.price.spider.manager.service.ManagerService;
import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author codekitten
 */
@Controller
public class ManagerController {
  
  private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);
  
  @Resource
  private ManagerService centerService;
  
  @Resource
  private SellerService sellerService;
  
  @RequestMapping(value = "/sellers", method = RequestMethod.GET)
  @ResponseBody
  public List<Seller> list() {
    return this.sellerService.list();
  }
  
  @RequestMapping(value = "/seller/{sellerId}", method = RequestMethod.GET)
  @ResponseBody
  public Pair<Link, Product> allocate(@PathVariable Integer sellerId) {
    Pair<Link, Product> pair = this.centerService.allocate(sellerId);
    logger.info(String.format("请求:电商%d,未发送队列还剩%d,未处理队列还剩%d", 
            sellerId,
            this.centerService.getPreSize(sellerId), 
            this.centerService.getProSize(sellerId)));
    return pair;
  }
  
  @RequestMapping(value = "/seller/{sellerId}", method = RequestMethod.POST)
  @ResponseBody
  public Boolean handover(@PathVariable Integer sellerId, @RequestBody Record record) {
    Boolean b = false;
    b = this.centerService.handover(sellerId, record);
    logger.info(String.format("保存:电商%d,未发送队列还剩%d,未处理队列还剩%d", 
          sellerId,
          this.centerService.getPreSize(sellerId), 
          this.centerService.getProSize(sellerId)));
    return b;
  }
}
