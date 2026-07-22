package org.tuyen.unsplashspring.service;

import org.springframework.web.reactive.function.client.WebClient;
import org.tuyen.unsplashspring.object.PhotoURLs;
import org.tuyen.unsplashspring.object.SearchPhotosResult;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class Photos {
    private final WebClient webClient;

    public Photos(String baseURL) {
        this.webClient = WebClient.builder().baseUrl(baseURL).build();
    }

    // Get a single page from the Editorial feed.
    public Mono<List<PhotoURLs>> list(String accessKey) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("photos")
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

    // Get a single page of collection results for a query.
    public Mono<PhotoURLs> get(String id, String accessKey) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("photo", id)
                        .build())
                .header("Authorization", "Client-ID " + accessKey)
                .retrieve()
                .bodyToMono(SearchPhotosResult.class)
                .map(result -> result.urls);
    }

    // Retrieve a collection’s photos.
    public Mono<List<PhotoURLs>> getRandoms(int count, String accessKey) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("photos", "random")
                        .queryParam("count", count)
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
