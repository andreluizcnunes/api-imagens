package com.japa.imageliteapi.infra.repository;

import com.japa.imageliteapi.domain.entity.Image;
import com.japa.imageliteapi.domain.enums.ImageExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.StringUtils;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, String>, JpaSpecificationExecutor<Image> {

    /**
     * @param extension
     * @param query
     * @return
     *
     *  SELECT * FROM IMAGE 1 = 1 AND EXTENSION = 'PNG' AND ( NAME LIKE 'QUERY' OR TAGS LIKE 'QUERY' )
     * */

    default List<Image> findByExtensionAndNameOrTagsLike(ImageExtension extension, String query){
        // SELECT * FROM IMAGE 1 = 1
        Specification<Image> conjuntion = (root, q, criteriaBuilder) -> criteriaBuilder.conjunction();
        Specification<Image> spec = Specification.where(conjuntion);

        if(extension != null){
            //AND EXTENSION = 'PNG'
            Specification<Image> extensionEqual = (root, q, criteriaBuilder) -> criteriaBuilder.equal(
                    root.get("extension"), extension
            );
            spec = spec.and(extensionEqual);
        }

        if(StringUtils.hasText(query)){
            //AND ( NAME LIKE 'QUERY' OR TAGS LIKE 'QUERY' )
            // river => RIVER
            Specification<Image> nameLike = (root, q, criteriaBuilder) -> criteriaBuilder
                    .like(criteriaBuilder.upper(root.get("name")),
                            "%" + query.toUpperCase() + "%");

            Specification<Image> tagsLike = (root, q, criteriaBuilder) -> criteriaBuilder
                    .like(criteriaBuilder.upper(root.get("tags")),
                            "%" + query.toUpperCase() + "%");;

            Specification<Image> nameOrTagsLike = Specification.anyOf(nameLike, tagsLike);

            spec = spec.and(nameOrTagsLike);
        }

        return findAll(spec);
    }

}