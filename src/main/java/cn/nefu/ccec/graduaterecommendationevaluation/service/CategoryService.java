package cn.nefu.ccec.graduaterecommendationevaluation.service;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.Category;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.Major;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.CategoryDTO;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.CollegeDTO;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final MajorRepository majorRepository;
    private final CollegeRepository collegeRepository;
    private final TransactionalOperator transactionalOperator;

    @CacheEvict(value = "category", allEntries = true)
    public Mono<Void> addCategory(Category category) {
        return categoryRepository.save(category)
                .then()
                .as(transactionalOperator::transactional);
    }

    @Cacheable(value = "category", key = "#catid")
    public Mono<CategoryDTO> getCategoryAndMajors(long catid) {
        Mono<Category> catM = categoryRepository.findById(catid);
        Mono<List<Major>> majorsM = majorRepository.findByCatId(catid).collectList();
        return Mono.zip(catM, majorsM)
                .map(r -> CategoryDTO
                        .builder()
                        .category(r.getT1())
                        .majors(r.getT2())
                        .build()
                ).cache();
    }

    public Mono<List<CategoryDTO>> listCategoryDTOs(long collid) {
        return categoryRepository.findCatIdsByCollId(collid)
                .collectList()
                .flatMap(this::listByCatids);
    }

    public Mono<List<Category>> listCategories(long uid) {
        return categoryRepository.findByUid(uid)
                .collectList();
    }

    private Mono<List<CategoryDTO>> listByCatids(List<Long> catids) {
        return Flux.fromIterable(catids)
                .flatMap(this::getCategoryAndMajors)
                .collectList();
    }

    public Mono<List<CollegeDTO>> listCollegesAndMajors() {
        return collegeRepository.findAll()
                .collectList()
                .flatMap(colleges -> Flux.fromIterable(colleges)
                        .flatMap(college -> majorRepository.findByCollId(college.getId())
                                .collectList()
                                .map(majors -> CollegeDTO.builder()
                                        .college(college)
                                        .majors(majors)
                                        .build()))
                        .collectList()
                );
    }

    @CacheEvict(value = {"category", "majors"}, allEntries = true)
    public Mono<Void> addMajor(Major major) {
        return majorRepository.save(major)
                .then()
                .as(transactionalOperator::transactional);
    }

    @Cacheable(value = "majors", key = "#catid")
    public Mono<List<Major>> listMajors(long catid) {
        return majorRepository.findByCatId(catid)
                .collectList();
    }
    // 检测用户是否在类别下
    public Mono<Boolean> checkInCateory(long uid,long catid) {
        return userCategoryRepository.checkInCategory(uid, catid)
                .hasElement();
    }
}
