package cn.nefu.ccec.graduaterecommendationevaluation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    @Value("${my.upload}")
    private String rootDirectory;

    private final StudentItemService studentItemService;

    // 创建并获取：`学院/类别/专业/姓名-学号`，目录
    public Mono<Path> createAndGetRelativePath(long majorid, long uid) {
        return studentItemService.getMajorDirectoryName(majorid)
                .flatMap(majorD -> studentItemService.getUserFileDirectoryName(uid)
                        .flatMap(userD -> Mono.fromCallable(() ->
                                        Files.createDirectories(Path.of(rootDirectory, majorD, userD))
                                ).subscribeOn(Schedulers.boundedElastic())
                                .thenReturn(Path.of(majorD, userD)))
                );
    }

    public Mono<Path> saveFile(Path relativePath, String fileName, Flux<DataBuffer> content) {
        var index = fileName.lastIndexOf(".");
        var name = fileName.substring(0, index) + "-" +System.currentTimeMillis() +  fileName.substring(index);
        var file = relativePath.resolve(name);
        return DataBufferUtils.write(content, Path.of(rootDirectory).resolve(file))
                .thenReturn(file);
    }

    public Mono<Boolean> removeFile(Path filePath) {
        var fp = Path.of(rootDirectory).resolve(filePath);
        return Mono.fromCallable(() -> Files.deleteIfExists(fp))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private final DataBufferFactory factory = new DefaultDataBufferFactory();

    public Flux<DataBuffer> downloadFile(Path filePath) {
        Path path = Path.of(rootDirectory).resolve(filePath);
        return DataBufferUtils.read(path, factory, 1024 * 8);
    }

    public Mono<Long> getSize(Path filePath) {
        return Mono.fromCallable(() ->
                        Files.size(Path.of(rootDirectory).resolve(filePath)))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
