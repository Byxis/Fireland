package fr.byxis.faction.essaim.enums;

/**
 * Enum representing the state of an expedition
 */
public enum ExpeditionState
{
    PREPARING,    // Group is forming, not started yet
    IN_PROGRESS,  // Expedition is active
    COMPLETED,    // Successfully finished
    FAILED,       // Failed (all members died/left)
    DISBANDED     // Group was disbanded
}