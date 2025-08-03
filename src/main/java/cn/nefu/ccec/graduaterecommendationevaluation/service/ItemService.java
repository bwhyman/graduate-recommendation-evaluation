package cn.nefu.ccec.graduaterecommendationevaluation.service;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.Item;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @CacheEvict(value = "items", allEntries = true)
    @Transactional
    public Mono<Void> addItem(Item item) {
        return itemRepository.save(item).then();
    }

    //@Cacheable(value = "items", key = "#catid")
    public Mono<List<Item>> listTopItems(long catid) {
        return itemRepository.findTopByCatId(catid).collectList();
    }

    //@Cacheable(value = "items", key = "#parentid")
    public Mono<List<Item>> listItems(long catid, long parentid) {
        return itemRepository.findByCatIdAndParentId(catid, parentid).collectList().cache();
    }

    //@Cacheable(value = "items", key = "#catid")
    public Mono<List<Item>> listItems(long catid) {
        return itemRepository.findByCatId(catid).collectList().cache();
    }

    public Mono<Item> getItem(long id, long catid) {
        return itemRepository.findByIdAndCatId(id, catid);
    }

    public Mono<Item> getItem(long id) {
        return itemRepository.findById(id);
    }
}
