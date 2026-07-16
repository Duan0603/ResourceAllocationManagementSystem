package com.company.resourcealloc.controller;

import com.company.resourcealloc.dto.AllocationRequest;
import com.company.resourcealloc.dto.AllocationResponse;
import com.company.resourcealloc.service.AllocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/allocations")
@RequiredArgsConstructor
public class AllocationController {

    private final AllocationService allocationService;

    @PostMapping
    public ResponseEntity<AllocationResponse> createAllocation(@Valid @RequestBody AllocationRequest request) {
        AllocationResponse response = allocationService.createAllocation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AllocationResponse> updateAllocation(@PathVariable Long id, @Valid @RequestBody AllocationRequest request) {
        AllocationResponse response = allocationService.updateAllocation(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAllocation(@PathVariable Long id) {
        allocationService.deleteAllocation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AllocationResponse>> getAllAllocations() {
        return ResponseEntity.ok(allocationService.getAllAllocations());
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<AllocationResponse> activateAllocation(@PathVariable Long id) {
        AllocationResponse response = allocationService.activateAllocation(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<AllocationResponse> endAllocation(@PathVariable Long id) {
        AllocationResponse response = allocationService.endAllocation(id);
        return ResponseEntity.ok(response);
    }
}
