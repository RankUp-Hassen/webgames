package com.hcode.webgames.components

import androidx.compose.runtime.MutableState
import com.hcode.webgames.data.action
import com.hcode.webgames.data.adventure
import com.hcode.webgames.data.arcade
import com.hcode.webgames.data.babyHazel
import com.hcode.webgames.data.bejeweled
import com.hcode.webgames.data.boys
import com.hcode.webgames.data.clicker
import com.hcode.webgames.data.cooking
import com.hcode.webgames.data.girls
import com.hcode.webgames.data.hyperCasual
import com.hcode.webgames.data.ioo
import com.hcode.webgames.data.multiPlayer
import com.hcode.webgames.data.puzzle
import com.hcode.webgames.data.racing
import com.hcode.webgames.data.shooting
import com.hcode.webgames.data.soccer
import com.hcode.webgames.data.sport
import com.hcode.webgames.data.stickMan
import com.hcode.webgames.data.threeD
import com.hcode.webgames.data.twoPlayer
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.util.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.random.Random


data class ItemData(val icon : String , val title : String )
data class FDG(val thumb : String , val title : String , val id: String ,val tags: String)




fun getItemSize(breakpoint: Breakpoint): Float{
    val w = window.innerWidth - 140
    return when(breakpoint){
        Breakpoint.MD -> { w / 5f }
        Breakpoint.LG -> { w / 8f }
        Breakpoint.XL -> { w / 8f }
        Breakpoint.SM -> { w / 3f }
        else -> { 100f }
    }
}





val listMenu = listOf(
    ItemData("action.png","Action"),
    ItemData("moughamara.png","Adventure"),
    ItemData("startup.png","Arcade"),
    ItemData("stickman.png","StickMan"),
    ItemData("click.png","Clicker"),
    ItemData("io_game.png",".IO"),
    ItemData("3d.png","3D"),
    ItemData("two-player.png","2-Player"),
    ItemData("puzzle.png", "Puzzle"),
    ItemData("hyper_casual.png","Hyper Casual"),
    ItemData("shooting.png","Shooting"),
    ItemData("gamer_boy.png","Baby Hazel"),
    ItemData("diamond.png","Bejeweled"),
    ItemData("gamer_boy.png","Boys"),
    ItemData("gamer.png", "Girls"),
    ItemData("chef.png","Cooking"),
    ItemData("tyre.png","Racing"),
    ItemData("multiplayer.png","MultiPlayers"),
    ItemData("football.png","Sport"),
    ItemData("soccer.png","Soccer"),
    ItemData("all.png","All")
)


val gamesListData = listOf(
    action,
    adventure,
    arcade,
    stickMan,
    clicker,
    ioo,
    threeD,
    twoPlayer,
    puzzle,
    hyperCasual,
    shooting,
    babyHazel,
    bejeweled,
    boys,
    girls,
    cooking,
    racing,
    multiPlayer,
    sport,
    soccer,
)



fun getAllGames(): MutableList<FDG> {
    val all = mutableListOf<FDG>()
    gamesListData.forEach {
        all.addAll(it)
    }
    return all
}

val allGames = getAllGames()







@Serializable
data class Item(
    @SerialName("description") val description: String,
    @SerialName("instructions") val instructions: String,
    @SerialName("category") val category: String,
)


suspend fun getGameDataFromServer(id : String , task : MutableState<Boolean>): Item {
    val client = HttpClient()
    try {
        val json = Json { ignoreUnknownKeys = true }
        val item = json.decodeFromString<List<Item>>(client.get("https://gamemonetize.com/feed.php?format=0&id=$id").bodyAsText()).first()
        task.value = true
        client.close()
        return item
    } catch (e: Exception) {
        println("Failed to fetch data: ${e.message}")
        task.value = false
        return Item("No Description","Control Mouse","")
    }
}


fun getVideoUrl(id: String): String {
    return "https://gamemonetize.video/index.php?domain=gamemonetize.com&gameid=$id&getads=false"
}

fun getGameUrl(id: String): String {
    return "https://html5.gamemonetize.co/$id"
}


fun getImageUrl(id: String): String {
    return "https://img.gamemonetize.com/$id/512x384.jpg"
}


fun getGamesWithTag(tag: String): MutableList<FDG> {
    val listGames = mutableListOf<FDG>()
    allGames.forEachIndexed{ i , game ->
        if ( tag.toLowerCasePreservingASCIIRules() in game.tags.toLowerCasePreservingASCIIRules()){
            listGames.add(game)
        }
        println(i == allGames.lastIndex)
    }
    return listGames

}



fun getIndexCatFromId(id : String): Int {
    gamesListData.forEachIndexed { i , l ->
        if(l.firstOrNull{ it.id == id } != null){
            return i
        }
    }
    return 0

}


fun getRandomGame(i : Int): List<FDG> {
    val games = gamesListData[i]
    if (games.size<=20){
        if (games.size<=10){
            return games
        }
        return games.subList(0,10)
    }
    val start = Random.nextInt(0,games.size-10)
    return games.subList(start,start+10)
}

fun clenIframe(){
    val game = document.getElementById("GameIframe")
    game?.setAttribute("src","")
    val video = document.getElementById("VideoIframe")
    video?.setAttribute("src","")
}

