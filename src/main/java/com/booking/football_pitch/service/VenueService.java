package com.booking.football_pitch.service;


import com.booking.football_pitch.dto.response.VenueResponse;
import java.util.List;
public interface VenueService {
    List<VenueResponse> getAllVenues();
}
