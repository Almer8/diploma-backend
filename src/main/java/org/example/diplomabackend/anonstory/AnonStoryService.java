package org.example.diplomabackend.anonstory;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.anonstory.entities.*;
import org.example.diplomabackend.anonstoryreport.AnonStoryReportService;
import org.example.diplomabackend.auth.security.CustomUserDetails;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnonStoryService {

    private final AnonStoryRepository anonStoryRepository;
    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;
    private final AnonStoryReportService reportService;

    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<?> getStories(String q, Integer page, Integer size, String sortDirection) {
        Sort sort;
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDirection.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }
        sort = Sort.by(direction,"id");
        PageRequest p = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(anonStoryRepository.findAllByQuery(q,p));
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getStory(Long id) {
        return ResponseEntity.ok(anonStoryRepository.findById(id));
    }
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<?> createStory(CreateStoryRequest r){

        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AnonStoryEntity story = AnonStoryEntity.create(r,user.getId(),getTagsByIds(r.getTags()));
        return ResponseEntity.ok(anonStoryRepository.save(story));
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateStory(Long id, UpdateStoryRequest r){
        AnonStoryEntity story = anonStoryRepository.findById(id).orElseThrow(()->new RuntimeException("AnonStory not found"));

        modelMapper.map(r,story);

        story.setTags(tagRepository.findAllById(r.getTags()));
        return ResponseEntity.ok(anonStoryRepository.save(story));
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteStory(Long id) {
        AnonStoryEntity story = anonStoryRepository.findById(id).orElseThrow(()->new RuntimeException("AnonStory not found"));

        anonStoryRepository.deleteById(id);
        for(TagEntity tag : story.getTags()){
            if(!anonStoryRepository.existsByTagsId(tag.getId())){
                tagRepository.delete(tag);
            }
        }
        reportService.deleteAnonStoryReportsByStoryId(id);

        return ResponseEntity.ok("AnonStory has been deleted");
    }

    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<?> getAllTags(){

        ArrayList<TagResponse> tags = tagRepository.findAll().stream()
                .map(e -> new TagResponse(e.getId(), e.getName())).collect(Collectors.toCollection(ArrayList::new));

        return ResponseEntity.ok(new TagsResponse(tags));
    }

    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<?> createTags(CreateTagsRequest r){

        ArrayList<TagResponse> createdTags = new ArrayList<>();
        for(String t: r.getTags()){
            TagEntity tag = tagRepository.save(new TagEntity(t));
            createdTags.add(new TagResponse(tag.getId(),tag.getName()));
        }
        return ResponseEntity.ok(new TagsResponse(createdTags));
    }

    public List<TagEntity> getTagsByIds(List<Long> ids) {

        return tagRepository.findAllById(ids);
    }

}
