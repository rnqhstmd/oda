package com.oda.interfaces.api.simulation;

import com.oda.interfaces.api.ApiResponse;
import com.oda.interfaces.api.simulation.dto.SimulateCareerRequest;
import com.oda.interfaces.api.simulation.dto.SimulationResponse;
import com.oda.application.simulation.CareerSimulationService;
import com.oda.domain.simulation.SimulationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/simulation")
public class SimulationController {

    private final CareerSimulationService careerSimulationService;

    public SimulationController(CareerSimulationService careerSimulationService) {
        this.careerSimulationService = careerSimulationService;
    }

    @PostMapping("/career")
    public ResponseEntity<ApiResponse<SimulationResponse>> simulateCareer(
            @AuthenticationPrincipal Long userId,
            @RequestBody SimulateCareerRequest request) {
        SimulationResult result = careerSimulationService.simulate(userId, request.targetJobCategory());
        return ResponseEntity.ok(ApiResponse.success(SimulationResponse.from(result)));
    }
}
