package org.tuyen.unsplashspring;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.tuyen.unsplashspring.object.Links;
import org.tuyen.unsplashspring.object.PhotoURLs;
import org.tuyen.unsplashspring.service.Collections;
import org.tuyen.unsplashspring.service.Photos;
import org.tuyen.unsplashspring.service.Search;
import org.tuyen.unsplashspring.service.Utils;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("api")
public class UnsplashController {
    final private Collections collections;
    final private Photos photos;
    final private Search search;

    public UnsplashController() throws MalformedURLException {
        Utils utils = new Utils();
        String baseURL = UriComponentsBuilder
                .newInstance()
                .scheme(utils.getScheme())
                .host(utils.getHost())
                .build()
                .toUri()
                .toURL()
                .toString();

        collections = new Collections(baseURL);
        photos = new Photos(baseURL);
        search = new Search(baseURL);
    }

    @GetMapping("/collections")
    public ResponseEntity<Mono<List<Links>>> listCollections(@RequestHeader("Authorization") String accessKey) {
        return ResponseEntity.ok(collections.list(accessKey));
    }

    @GetMapping("/collection/photos/{id}")
    public ResponseEntity<Mono<List<PhotoURLs>>> getCollectionPhotos(@PathVariable String id, @RequestHeader("Authorization") String accessKey) {
        return ResponseEntity.ok(collections.getPhotos(id, accessKey));
    }

    @GetMapping("/photos")
    public ResponseEntity<Mono<List<PhotoURLs>>> listPhotos(@RequestHeader("Authorization") String accessKey) {
        return ResponseEntity.ok(photos.list(accessKey));
    }

    @GetMapping("/photo/{id}")
    public ResponseEntity<Mono<PhotoURLs>> getPhotos(@PathVariable String id, @RequestHeader("Authorization") String accessKey) {
        return ResponseEntity.ok(photos.get(id, accessKey));
    }

    @GetMapping("/photos/random/{count}")
    public ResponseEntity<Mono<List<PhotoURLs>>> getRandomPhotos(@PathVariable String count, @RequestHeader("Authorization") String accessKey) {
        return ResponseEntity.ok(photos.getRandoms(Integer.parseInt(count), accessKey));
    }

    @GetMapping("/search/photos/{query}")
    public ResponseEntity<Mono<List<PhotoURLs>>> searchPhotos(@PathVariable String query, @RequestHeader("Authorization") String accessKey) {
        return ResponseEntity.ok(search.photos(query, accessKey));
    }

    @GetMapping("/search/collections/{query}")
    public ResponseEntity<Mono<List<Links>>> searchCollections(@PathVariable String query, @RequestHeader("Authorization") String accessKey) {
        return ResponseEntity.ok(search.collections(query, accessKey));
    }
}
