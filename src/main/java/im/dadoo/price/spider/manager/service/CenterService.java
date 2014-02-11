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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.random.RandomDataGenerator;
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
  
  private static final List<Link> preLinks = Collections.synchronizedList(new LinkedList<Link>());

  private static final Map<Integer, Link> proMap = 
          Collections.synchronizedMap(new HashMap<Integer, Link>());
  
  
  public Link select() {
    Link link = null;
    if (!CenterService.preLinks.isEmpty()) {
      link = CenterService.preLinks.get(0);
      CenterService.preLinks.remove(0);
      CenterService.proMap.put(link.getId(), link);
    } else {
      if (!CenterService.proMap.isEmpty()) {
        for (Map.Entry<Integer, Link> entry : CenterService.proMap.entrySet()) {
          link = entry.getValue();
          break;
        }
      } else {
        CenterService.preLinks.addAll(this.disorder(this.linkService.list()));
        CenterService.proMap.clear();
        link = CenterService.preLinks.get(0);
        CenterService.preLinks.remove(0);
      }
    }
    return link;
  }
  
  public Boolean save(Record record) {
    Link link = record.getLink();
    if (CenterService.proMap.containsKey(link.getId())) {
      this.recordService.save(link, record.getPrice(), record.getStock(), record.getPromotion());
      CenterService.proMap.remove(link.getId());
      return true;
    } else {
      return false;
    }
  }
  
  public Integer getPreLinksSize() {
    return CenterService.preLinks.size();
  }
  
  public Integer getProMapSize() {
    return CenterService.proMap.size();
  }
  
  public List<Link> disorder(List<Link> links) {
    RandomDataGenerator rdg = new RandomDataGenerator();
		List<Link> result = new ArrayList<>(links.size());
		int[] p = rdg.nextPermutation(links.size(), links.size());
		for (int i = 0; i < p.length; i++) {
			result.add(links.get(p[i]));
		}
		return result;
  }
}
