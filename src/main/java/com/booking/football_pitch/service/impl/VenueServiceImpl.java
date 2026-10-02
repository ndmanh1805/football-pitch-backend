package com.booking.football_pitch.service.impl;


import com.booking.football_pitch.dto.response.VenueResponse;
import com.booking.football_pitch.entity.Venue;
import com.booking.football_pitch.repository.VenueRepository;
import com.booking.football_pitch.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService{
    private final VenueRepository venueRepository;

    @Override
    public List<VenueResponse> getAllVenues() {
        // Lấy tất cả các cụm sân từ Database
        List<Venue> venues = venueRepository.findAll();
        
        // Map sang DTO
        return venues.stream().map(venue -> VenueResponse.builder()
                .id(venue.getId())
                .name(venue.getName())
                .address(venue.getAddress())
                // Giả định bảng Venue cũng dùng Enum Status giống Pitch
                .status(venue.getStatus().name()) 
                .build()).collect(Collectors.toList());
    }
}
