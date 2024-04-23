package com.japa.imageliteapi.application.images;

import com.japa.imageliteapi.domain.entity.Image;
import com.japa.imageliteapi.domain.enums.ImageExtension;
import com.japa.imageliteapi.domain.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/imagens")
@Slf4j
@RequiredArgsConstructor
public class ImagensController {

    private final ImageService service;
    private final ImageMapper mapper;
    @PostMapping
    public ResponseEntity save(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("tags") List<String> tags
    ) throws IOException {
        log.info("Imagem recebida: name: {}, size: {}", file.getOriginalFilename(), file.getSize());
//        log.info("Content Type: {}", file.getContentType());
//        log.info("Media Type: {}", MediaType.valueOf(file.getContentType()));
//        log.info("Nome definido para a imagem: {}", name);
//        log.info("Tags: {}", tags);

        Image image = mapper.mapToImage(file, name, tags);
        Image savedImage = service.save(image);

        // http://localhost:8080/v1/images/asjdasdlkajs
        URI imageUri = buildImageUrl(savedImage);

        return ResponseEntity.created(imageUri).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") String id){
        var possivelImage = service.getById(id);
        if(possivelImage.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        var image = possivelImage.get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(image.getExtension().getMediaType());
        headers.setContentLength(image.getSize());
        headers.setContentDispositionFormData("inline; filename=\"" + image.getFileName() + "\"", image.getFileName());

        return new ResponseEntity<>(image.getFile(), headers, HttpStatus.OK);
    }

    private URI buildImageUrl(Image image){
        String imagePath = "/" + image.getId();
        return ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path(imagePath)
                    .build()
                    .toUri();
    }
}
