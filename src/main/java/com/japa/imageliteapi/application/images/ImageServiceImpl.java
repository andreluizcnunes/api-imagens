package com.japa.imageliteapi.application.images;

import com.japa.imageliteapi.domain.entity.Image;
import com.japa.imageliteapi.domain.service.ImageService;
import com.japa.imageliteapi.infra.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository repository;
    @Override
    @Transactional
    public Image save(Image image) {
        return repository.save(image);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Image> getById(String id) {
        return repository.findById(id);
    }
}
