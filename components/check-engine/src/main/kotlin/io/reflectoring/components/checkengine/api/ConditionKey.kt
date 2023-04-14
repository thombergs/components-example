package io.reflectoring.components.checkengine.api

/**
 * Unique identifier for a condition of a check that may fail. Should be something like "SOCIAL-1-2", where
 * "SOCIAL-1" is the check that was executed and "2" is the condition in that check that failed.
 */
data class ConditionKey(
    val key: String
) {
    override fun toString(): String {
        return this.key
    }
}
