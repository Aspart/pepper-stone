package my.com

/**
 * Handle header of event
 * @param name - commonly "Study Event Definition #"
 * @param description - description of event, "Screening", "Therapy", etc.
 * @param key - E# or E##, etc.
 */
class EventHeader(val name: String, val description: String, val key: String) {
}
