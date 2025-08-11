package cn.nefu.ccec.graduaterecommendationevaluation.service;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.Item;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.ItemDTO;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final TransactionalOperator transactionalOperator;

    @CacheEvict(value = "items", allEntries = true)
    public Mono<Void> addItem(Item item) {
        return itemRepository.save(item).then()
                .as(transactionalOperator::transactional);
    }

    @Cacheable(value = "items", key = "#catid")
    public Mono<List<Item>> listTopItems(long catid) {
        return itemRepository.findTopByCatId(catid).collectList();
    }

    /**
     * ，不直接返回，按子节点封装
     *
     * @param catid
     * @param parentid
     * @return
     */
   // @Cacheable(value = "items", key = "#parentid")
    public Mono<ItemDTO> listItems(long catid, long parentid) {
        return itemRepository.findByCatIdAndParentId(catid, parentid)
                .collectList()
                .map(items -> convertToItemDTO(items, parentid))
                .cache();
    }

    private ItemDTO convertToItemDTO(List<Item> items, long parentid) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        // 建立 id -> DTO 映射
        Map<Long, ItemDTO> itemMap = items.stream()
                .map(item -> {
                    ItemDTO dto = new ItemDTO();
                    BeanUtils.copyProperties(item, dto);
                    return dto;
                })
                .collect(Collectors.toMap(ItemDTO::getId, dto -> dto));

        // 分组：parentId -> 所有子节点列表
        Map<Long, List<ItemDTO>> childrenMap = itemMap
                .values()
                .stream()
                .filter(dto -> dto.getParentId() != null)
                .collect(Collectors.groupingBy(ItemDTO::getParentId));

        // 将子节点挂到对应的父节点上
        childrenMap.forEach((id, children) -> {
            ItemDTO parent = itemMap.get(id);
            if (parent != null) {
                children.sort(Comparator.comparing(ItemDTO::getId));
                parent.setItems(children);
            }
        });
        // 返回根节点
        return itemMap
                .values()
                .stream()
                .filter(dto -> dto.getId() == parentid)
                .findFirst()
                .orElse(null);
    }

    private List<ItemDTO> convertToItemDTO(List<Item> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        // 建立 id -> DTO 映射
        Map<Long, ItemDTO> itemMap = items.stream()
                .map(item -> {
                    ItemDTO dto = new ItemDTO();
                    BeanUtils.copyProperties(item, dto);
                    return dto;
                })
                .collect(Collectors.toMap(ItemDTO::getId, dto -> dto));

        // 分组：parentId -> 所有子节点列表
        Map<Long, List<ItemDTO>> childrenMap = itemMap
                .values()
                .stream()
                .filter(dto -> dto.getParentId() != null)
                .collect(Collectors.groupingBy(ItemDTO::getParentId));

        // 将子节点挂到对应的父节点上
        childrenMap.forEach((id, children) -> {
            ItemDTO parent = itemMap.get(id);
            if (parent != null) {
                children.sort(Comparator.comparing(ItemDTO::getId));
                parent.setItems(children);
            }
        });
        // 4. 根节点：parentId 为 null 或父节点不在 map 中
        return itemMap
                .values()
                .stream()
                .filter(dto -> dto.getParentId() == null || !itemMap.containsKey(dto.getParentId()))
                .sorted(Comparator.comparing(ItemDTO::getId))
                .collect(Collectors.toList());
    }

    //@Cacheable(value = "items", key = "#catid")
    public Mono<List<ItemDTO>> listItems(long catid) {
        return itemRepository.findByCatId(catid)
                .collectList()
                .map(this::convertToItemDTO)
                .cache();
    }

    public Mono<Item> getItem(long id, long catid) {
        return itemRepository.findByIdAndCatId(id, catid);
    }
}
