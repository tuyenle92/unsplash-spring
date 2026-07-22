package org.tuyen.unsplashspring.service;

import org.springframework.web.reactive.function.client.WebClient;
import org.tuyen.unsplashspring.object.Links;
import org.tuyen.unsplashspring.object.PhotoURLs;
import org.tuyen.unsplashspring.object.SearchPhotosResult;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class Collections {
    private final WebClient webClient;

    public Collections(String baseURL) {
        webClient = WebClient.builder().baseUrl(baseURL).build();
    }

    // Get a single page from the list of all collections.
    public Mono<List<Links>> list(String accessKey) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("collections")
                        .build())
                .header("Authorization", "Client-ID " + accessKey)
                .retrieve()
                .bodyToMono(SearchPhotosResult[].class)
                .flatMap(results -> {
                    ArrayList<Links> links = new ArrayList<>();
                    for (var result : results) {
                       links.add(result.links);
                    }

                    return Mono.just(links);
                });
    }

    // Retrieve a collection’s photos.
    public Mono<List<PhotoURLs>> getPhotos(String id, String accessKey) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("collections", id, "photos")
                        .build())
                .header("Authorization", "Client-ID " + accessKey)
                .retrieve()
                .bodyToMono(SearchPhotosResult[].class)
                .flatMap(results -> {
                    ArrayList<PhotoURLs> photoURLs = new ArrayList<>();

                    for (var result : results) {
                        photoURLs.add(result.urls);
                    }

                    return Mono.just(photoURLs);
                });
    }
}
