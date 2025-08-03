package cn.nefu.ccec.graduaterecommendationevaluation.service;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.Category;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.College;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.Major;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.WeightedScore;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.CategoryDTO;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.CollegeDTO;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final MajorRepository majorRepository;
    private final CollegeRepository collegeRepository;


    @Transactional
    @CacheEvict(value = "category", allEntries = true)
    public Mono<Void> addCategory(Category category) {
        return categoryRepository.save(category)
                .then();
    }

    public Mono<List<Long>> listCatIds(long collid) {
        return categoryRepository.findCatIdsByCollId(collid)
                .collectList();
    }

    /*public Mono<List<Long>> listCatIdsByUid(long uid) {
        return categoryRepository.findCatIdsByUid(uid)
                .collectList();
    }*/

    public Mono<Long> getCatIdByUid(long uid) {
        return categoryRepository.findCatIdByUid(uid);
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

    public Mono<List<CategoryDTO>> listCategories(long collid) {
        return categoryRepository.findCatIdsByCollId(collid)
                .collectList()
                .flatMap(this::listByCatids);
    }

    public Mono<List<CategoryDTO>> listCategoriesByuid(long uid) {
        return categoryRepository.findCatIdsByUid(uid)
                .collectList()
                .flatMap(this::listByCatids);
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

    @CacheEvict(value = "category", allEntries = true)
    @Transactional
    public Mono<Void> addMajor(Major major) {
        return majorRepository.save(major)
                .then();
    }



}
