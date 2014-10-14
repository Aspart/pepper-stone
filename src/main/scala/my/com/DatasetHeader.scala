package my.com

/**
 * Handle header of OpenClinica data
 */
class DatasetHeader(val name: String, val description: String, val status: String,
                    val study: String, val ID: String, val date: String,
                    val subjects: Int, val events: Array[EventHeader]) {
}
