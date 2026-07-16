package com.company.resourcealloc.service;

import com.company.resourcealloc.dto.AllocationRequest;
import com.company.resourcealloc.dto.AllocationResponse;

import java.util.List;

public interface AllocationService {
    AllocationResponse createAllocation(AllocationRequest request);
    AllocationResponse updateAllocation(Long id, AllocationRequest request);
    void deleteAllocation(Long id);
    List<AllocationResponse> getAllAllocations();
    AllocationResponse activateAllocation(Long id);
    AllocationResponse endAllocation(Long id);
}
