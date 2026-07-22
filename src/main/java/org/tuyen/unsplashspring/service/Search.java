package org.tuyen.unsplashspring.service;

import org.springframework.web.reactive.function.client.WebClient;
import org.tuyen.unsplashspring.object.Links;
import org.tuyen.unsplashspring.object.PhotoURLs;
import org.tuyen.unsplashspring.object.SearchPhotosResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class Search {
    private final WebClient webClient;

    public Search(String baseURL) {
        this.webClient = WebClient.builder().baseUrl(baseURL).build();
    }

    // Get a single page of photo results for a query.
    public Mono<List<PhotoURLs>> photos(String query, String accessKey) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("search", "photos")
                        .queryParam("query", query)
                        .build())
                .header("Authorization", "Client-ID " + accessKey)
                .retrieve()
                .bodyToMono(SearchPhotosResponse.class)
                .map(searchPhotosResponse -> {
                    ArrayList<PhotoURLs> photoURLs = new ArrayList<>();

                    for (var result : searchPhotosResponse.results) {
                        photoURLs.add(result.urls);
                    }

                    return photoURLs;
                });
    }

    // Get a single page of collection results for a query.
    public Mono<List<Links>> collections(String query, String accessKey) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("search", "collections")
                        .queryParam("query", query)
                        .build())
                .header("Authorization", "Client-ID " + accessKey)
                .retrieve()
                .bodyToMono(SearchPhotosResponse.class)
                .map(searchPhotosResponse -> {
                    ArrayList<Links> links = new ArrayList<>();

                    for (var result : searchPhotosResponse.results) {
                        links.add(result.links);
                    }

                    return links;
                });
    }
}
