/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package im.dadoo.price.spider.manager.service;

import im.dadoo.price.core.domain.Link;
import im.dadoo.price.core.domain.Product;
import im.dadoo.price.core.domain.Record;
import im.dadoo.price.core.domain.Seller;
import im.dadoo.price.core.domain.util.Pair;
import im.dadoo.price.core.service.LinkService;
import im.dadoo.price.core.service.ProductService;
import im.dadoo.price.core.service.RecordService;
import im.dadoo.price.core.service.SellerService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 *
 * @author codekitten
 */
@Component
public class ManagerService {
  
  @Resource
  private LinkService linkService;
  
  @Resource
  private ProductService productService;
  
  @Resource
  private RecordService recordService;
  
  @Resource
  private SellerService sellerService;
  
  private Map<Integer, ConcurrentLinkedQueue<Link>> preMap;
  private Map<Integer, ConcurrentSkipListSet<Link>> proMap;
  
  @PostConstruct
  public void init() {
    this.preMap = new HashMap<>();
    this.proMap = new HashMap<>();
    List<Seller> sellers = this.sellerService.list();
    for (Seller seller : sellers) {
      ConcurrentLinkedQueue<Link> queue = new ConcurrentLinkedQueue<>();
      ConcurrentSkipListSet<Link> set = new ConcurrentSkipListSet<>();
      this.preMap.put(seller.getId(), queue);
      this.proMap.put(seller.getId(), set);
    }
  }
  
  public Pair<Link, Product> allocate(Integer sellerId) {
    Link link = null;
    ConcurrentLinkedQueue<Link> queue = this.preMap.get(sellerId);
    ConcurrentSkipListSet<Link> set = this.proMap.get(sellerId);
    //为了防止有新的seller加入
    if (queue == null) {
      List<Link> links = this.linkService.listBySellerId(sellerId);
      if (links != null && !links.isEmpty()) {
        queue = new ConcurrentLinkedQueue<>();
        set = new ConcurrentSkipListSet<>();
        queue.addAll(links);
        this.preMap.put(sellerId, queue);
        this.proMap.put(sellerId, set);
      } else {
        return null;
      }
    }
    if (!queue.isEmpty()) {
      link = queue.poll();
      set.add(link);
    } else {
      if (!set.isEmpty()) {
        link = set.first();
      } else {
        queue.addAll(this.linkService.listBySellerId(sellerId));
        link = queue.poll();
        set.add(link);
      }
    }
    if (link != null) {
      return new Pair(link, this.productService.findById(link.getProductId()));
    } else {
      return null;
    }
  }
  
  public Boolean handover(Integer sellerId, Record record) {
    ConcurrentSkipListSet<Link> set = this.proMap.get(sellerId);
    if (!set.isEmpty()) {
      for (Link link : set) {
        if (link.getId().equals(record.getLinkId())) {
          //避免易迅网页返回404
          if (record.getPrice() != null || record.getStock() == 0) {
            this.recordService.save(link, 
                    record.getPrice(), record.getStock(), record.getPromotion());
          }
          set.remove(link);
          return true;
        }
      }
      return false;
    } else {
      return false;
    }
  }
  
  public Integer getPreSize(Integer sellerId) {
    return this.preMap.get(sellerId).size();
  }
  
  public Integer getProSize(Integer sellerId) {
    return this.proMap.get(sellerId).size();
  }
}
