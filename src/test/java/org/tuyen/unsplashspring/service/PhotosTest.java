package org.tuyen.unsplashspring.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.tuyen.unsplashspring.object.PhotoURLs;
import org.tuyen.unsplashspring.object.SearchPhotosResult;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhotosTest {
    private static MockWebServer mockWebServer;
    ObjectMapper mapper = new ObjectMapper();
    private static final SearchPhotosResult[] searchPhotosResults = new SearchPhotosResult[1];

    @BeforeAll
    public static void setup() throws IOException {
        mockWebServer = new MockWebServer();

        searchPhotosResults[0] = new SearchPhotosResult();
        searchPhotosResults[0].urls = new PhotoURLs();

        searchPhotosResults[0].urls.raw = "https://images.unsplash.com/photo-1449614115178-cb924f730780";
        searchPhotosResults[0].urls.full = "https://images.unsplash.com/photo-1449614115178-cb924f730780?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy";
        searchPhotosResults[0].urls.regular = "https://images.unsplash.com/photo-1449614115178-cb924f730780?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=1080&fit=max";
        searchPhotosResults[0].urls.small = "https://images.unsplash.com/photo-1449614115178-cb924f730780?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=400&fit=max";
        searchPhotosResults[0].urls.thumb = "https://images.unsplash.com/photo-1449614115178-cb924f730780?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=400&fit=max";
    }

    @AfterAll
    public static void teardown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void list() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(searchPhotosResults)));

        Photos photos = new Photos(mockWebServer.url("/").toString());
        Mono<List<PhotoURLs>> photoURLs = photos.list("test");

        StepVerifier
                .create(photoURLs)
                .expectNextMatches(urls -> urls.size() == 1 &&
                        urls.get(0).raw.equals(searchPhotosResults[0].urls.raw) &&
                        urls.get(0).full.equals(searchPhotosResults[0].urls.full) &&
                        urls.get(0).regular.equals(searchPhotosResults[0].urls.regular) &&
                        urls.get(0).small.equals(searchPhotosResults[0].urls.small) &&
                        urls.get(0).thumb.equals(searchPhotosResults[0].urls.thumb)
                )
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/photos", recordedRequest.getPath());
    }

    @Test
    public void get() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(searchPhotosResults[0])));

        Photos photos = new Photos(mockWebServer.url("/").toString());
        Mono<PhotoURLs> photoURL = photos.get("test", "test");

        StepVerifier
                .create(photoURL)
                .expectNextMatches(url ->
                        url.raw.equals(searchPhotosResults[0].urls.raw) &&
                        url.full.equals(searchPhotosResults[0].urls.full) &&
                        url.regular.equals(searchPhotosResults[0].urls.regular) &&
                        url.small.equals(searchPhotosResults[0].urls.small) &&
                        url.thumb.equals(searchPhotosResults[0].urls.thumb)
                )
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/photo/test", recordedRequest.getPath());
    }

    @Test
    public void getRandom() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(searchPhotosResults)));

        Photos photos = new Photos(mockWebServer.url("/").toString());
        Mono<List<PhotoURLs>> photoURLs = photos.getRandoms(2, "test");

        StepVerifier
                .create(photoURLs)
                .expectNextMatches(urls -> urls.size() == 1 &&
                        urls.get(0).raw.equals(searchPhotosResults[0].urls.raw) &&
                        urls.get(0).full.equals(searchPhotosResults[0].urls.full) &&
                        urls.get(0).regular.equals(searchPhotosResults[0].urls.regular) &&
                        urls.get(0).small.equals(searchPhotosResults[0].urls.small) &&
                        urls.get(0).thumb.equals(searchPhotosResults[0].urls.thumb)
                )
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/photos/random?count=2", recordedRequest.getPath());
    }
}
