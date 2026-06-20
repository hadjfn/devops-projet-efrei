package efrei.devops.stats.data;

import jakarta.validation.constraints.NotNull;

public record ApprentiDto(
        Long id,
        String nom,
        String prenom,
        String programme,
        String majeure,
        @NotNull Integer annee,
        boolean archived
) {
}
