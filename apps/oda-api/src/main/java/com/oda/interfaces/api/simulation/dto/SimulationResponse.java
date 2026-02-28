package com.oda.interfaces.api.simulation.dto;

import com.oda.domain.simulation.CareerPath;
import com.oda.domain.simulation.SimulationResult;

import java.util.List;

public record SimulationResponse(
        String currentPosition,
        String targetPosition,
        CareerPathResponse careerPath,
        List<String> gapItems
) {
    public static SimulationResponse from(SimulationResult result) {
        return new SimulationResponse(
                result.currentPosition(),
                result.targetPosition(),
                CareerPathResponse.from(result.careerPath()),
                result.gapItems()
        );
    }

    public record CareerPathResponse(
            List<CareerStepResponse> steps,
            int totalEstimatedMonths,
            List<String> requiredCertifications
    ) {
        public static CareerPathResponse from(CareerPath careerPath) {
            return new CareerPathResponse(
                    careerPath.steps().stream().map(CareerStepResponse::from).toList(),
                    careerPath.totalEstimatedMonths(),
                    careerPath.requiredCertifications()
            );
        }
    }

    public record CareerStepResponse(
            String title,
            String description,
            int estimatedMonths,
            List<String> requiredSkills
    ) {
        public static CareerStepResponse from(com.oda.domain.simulation.CareerStep step) {
            return new CareerStepResponse(
                    step.title(),
                    step.description(),
                    step.estimatedMonths(),
                    step.requiredSkills()
            );
        }
    }
}
