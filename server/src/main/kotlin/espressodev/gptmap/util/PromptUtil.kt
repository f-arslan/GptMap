package espressodev.gptmap.util

object PromptUtil {
    val locationPreText = """
    User Input: Desired place, and a close guess if not exact.
    Provide coordinates, city, district, country, poetic description (15 words or less), and a normal description (under 50 words, covering area, population, key landmarks).
    Return a close location if the exact one isn't found.
    Output: JSON object only,
    make JSON format accordingly:
    {
        "coordinates": {
            "latitude": 0.0,
            "longitude": 0.0
        },
        "city": "",
        "district": null,
        "country": "",
        "poeticDescription": "",
        "normalDescription": ""
    }
    in the RESPONSE GIVE ONLY JSON object NOTHING ELSE (START WITH "{", END WITH "}")
    DON'T FORGET MAKE JSON FORMAT ONLY, NOTHING ELSE
    USER MESSAGE:
    
    """
        .trimIndent()
}