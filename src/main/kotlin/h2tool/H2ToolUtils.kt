import java.util.*

inline fun <reified T> props(): Map<String, String?> {
    val resId = T::class.java.run { "$simpleName.properties" }
    val res = T::class.java.getResource(resId)
        ?.openStream()
        ?.reader()
        ?.use { Properties().apply { load(it) } } ?: return emptyMap()
    return buildMap(res.size) {
        res.entries.forEach { (k, v) -> put(k as String, v as String?) }
    }
}
