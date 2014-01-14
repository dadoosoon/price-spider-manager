/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package im.dadoo.price.spider.manager.service;

import im.dadoo.price.core.domain.Link;
import im.dadoo.price.core.domain.Record;
import im.dadoo.price.core.service.LinkService;
import im.dadoo.price.core.service.RecordService;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author codekitten
 */
@Component
public class CenterService {
  
  @Autowired
  private LinkService linkService;
  
  @Autowired
  private RecordService recordService;
  
  //初始未被发送的linkid队列
  private static final SortedSet<Link> selectSet = 
          (SortedSet<Link>)Collections.synchronizedSortedSet(new TreeSet<Link>());
  
  //已经被发送但采集尚未返回的set
  private static final SortedSet<Link> saveSet = 
          (SortedSet<Link>)Collections.synchronizedSortedSet(new TreeSet<Link>());
  
  public Link select() {
    if (!CenterService.selectSet.isEmpty()) {
      Link item = CenterService.selectSet.last();
      CenterService.selectSet.remove(item);
      CenterService.saveSet.add(item);
      return item;
    } else {
      if (!CenterService.saveSet.isEmpty()) {
        Link item = CenterService.saveSet.first();
        return item;
      } else {
        CenterService.selectSet.addAll(this.linkService.list());
        Link item = CenterService.selectSet.last();
        CenterService.selectSet.remove(item);
        CenterService.saveSet.add(item);
        return item;
      }
    }
  }
  
  public Boolean save(Record record) {
    Link link = record.getLink();
    if (CenterService.saveSet.contains(link)) {
      this.recordService.save(record.getLink(), record.getPrice(), record.getStock());
      CenterService.saveSet.remove(link);
      return true;
    } else {
      return false;
    }
  }
  
  public Integer getSelectSetSize() {
    return CenterService.selectSet.size();
  }
  
  public Integer getSaveSetSize() {
    return CenterService.saveSet.size();
  }
}
