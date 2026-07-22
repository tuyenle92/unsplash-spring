package org.tuyen.unsplashspring.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.tuyen.unsplashspring.object.Links;
import org.tuyen.unsplashspring.object.PhotoURLs;
import org.tuyen.unsplashspring.object.SearchPhotosResponse;
import org.tuyen.unsplashspring.object.SearchPhotosResult;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SearchTest {
    private static MockWebServer mockWebServer;
    ObjectMapper mapper = new ObjectMapper();
    private static final SearchPhotosResponse searchPhotosResponse = new SearchPhotosResponse();


    @BeforeAll
    public static void setup() throws IOException {
        mockWebServer = new MockWebServer();

        SearchPhotosResult[] searchPhotosResults = new SearchPhotosResult[1];
        searchPhotosResults[0] = new SearchPhotosResult();
        searchPhotosResults[0].urls = new PhotoURLs();
        searchPhotosResults[0].links = new Links();

        searchPhotosResults[0].links.self = "https://api.unsplash.com/collections/296";
        searchPhotosResults[0].links.html = "https://unsplash.com/collections/296";
        searchPhotosResults[0].links.photos = "https://api.unsplash.com/collections/296/photos";
        searchPhotosResults[0].links.related = "https://api.unsplash.com/collections/296/related";

        searchPhotosResults[0].urls.raw = "https://images.unsplash.com/photo-1449614115178-cb924f730780";
        searchPhotosResults[0].urls.full = "https://images.unsplash.com/photo-1449614115178-cb924f730780?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy";
        searchPhotosResults[0].urls.regular = "https://images.unsplash.com/photo-1449614115178-cb924f730780?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=1080&fit=max";
        searchPhotosResults[0].urls.small = "https://images.unsplash.com/photo-1449614115178-cb924f730780?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=400&fit=max";
        searchPhotosResults[0].urls.thumb = "https://images.unsplash.com/photo-1449614115178-cb924f730780?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=400&fit=max";

        searchPhotosResponse.results = searchPhotosResults;
    }

    @AfterAll
    public static void teardown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void photos() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(searchPhotosResponse)));

        Search search = new Search(mockWebServer.url("/").toString());
        Mono<List<PhotoURLs>> photoURLs = search.photos("cat", "test");

        StepVerifier
                .create(photoURLs)
                .expectNextMatches(urls -> urls.size() == 1 &&
                        urls.get(0).raw.equals(searchPhotosResponse.results[0].urls.raw) &&
                        urls.get(0).full.equals(searchPhotosResponse.results[0].urls.full) &&
                        urls.get(0).regular.equals(searchPhotosResponse.results[0].urls.regular) &&
                        urls.get(0).small.equals(searchPhotosResponse.results[0].urls.small) &&
                        urls.get(0).thumb.equals(searchPhotosResponse.results[0].urls.thumb)
                )
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/search/photos?query=cat", recordedRequest.getPath());
    }

    @Test
    public void collections() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(searchPhotosResponse)));

        Search search = new Search(mockWebServer.url("/").toString());
        Mono<List<Links>> links = search.collections("cat", "test");

        StepVerifier
                .create(links)
                .expectNextMatches(link -> link.size() == 1 &&
                        link.get(0).self.equals(searchPhotosResponse.results[0].links.self) &&
                        link.get(0).html.equals(searchPhotosResponse.results[0].links.html) &&
                        link.get(0).photos.equals(searchPhotosResponse.results[0].links.photos) &&
                        link.get(0).related.equals(searchPhotosResponse.results[0].links.related)
                )
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/search/collections?query=cat", recordedRequest.getPath());
    }
}
