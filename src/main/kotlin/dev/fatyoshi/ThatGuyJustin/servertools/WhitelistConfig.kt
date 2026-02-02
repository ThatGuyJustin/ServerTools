package dev.fatyoshi.ThatGuyJustin.servertools

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import net.neoforged.fml.loading.FMLPaths

@Serializable
data class WhitelistConfigObject (
    val users: MutableMap<String, MutableList<String>> = mutableMapOf()
)

class WhitelistConfig {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    private val filePath: Path
        get() = FMLPaths.GAMEDIR.get().resolve("servertools-whitelist.json")

    fun load(): WhitelistConfigObject {
        return try {
            if (!Files.exists(filePath)) {
                WhitelistConfigObject().also { save(it) }
            } else {
                val content = Files.readString(filePath)
                json.decodeFromString(WhitelistConfigObject.serializer(), content)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            WhitelistConfigObject()
        }
    }

    fun save(data: WhitelistConfigObject) {
        try {
            val content = json.encodeToString(WhitelistConfigObject.serializer(), data)
            Files.writeString(
                filePath,
                content,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}