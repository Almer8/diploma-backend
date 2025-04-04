package org.example.diplomabackend.anonstory;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.anonstory.entities.CreateStoryRequest;
import org.example.diplomabackend.anonstory.entities.CreateTagsRequest;
import org.example.diplomabackend.anonstory.entities.UpdateStoryRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/story")
public class AnonStoryController {

    private final AnonStoryService anonStoryService;

    @GetMapping
    public ResponseEntity<?> getStories(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "size") Integer size,
            @RequestParam(name = "sortDirection") String sortDirection) {

        return anonStoryService.getStories(q, page, size, sortDirection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStory(@PathVariable("id") Long id) {
        return anonStoryService.getStory(id);
    }

    @PostMapping
    public ResponseEntity<?> createStory(@RequestBody CreateStoryRequest r){
        return anonStoryService.createStory(r);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStory(@RequestBody UpdateStoryRequest r, @PathVariable("id") Long id) {
        return anonStoryService.updateStory(id,r);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStory(@PathVariable("id") Long id) {
        return anonStoryService.deleteStory(id);
    }
    @PostMapping("/tags")
    public ResponseEntity<?> createTags(@RequestBody CreateTagsRequest r){
        return anonStoryService.createTags(r);
    }
    @GetMapping("/tags")
    public ResponseEntity<?> getTags() {
        return anonStoryService.getAllTags();
    }



}
