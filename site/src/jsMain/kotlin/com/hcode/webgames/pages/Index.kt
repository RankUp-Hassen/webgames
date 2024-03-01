package com.hcode.webgames.pages


import androidx.compose.runtime.*
import com.hcode.webgames.components.*
import com.hcode.webgames.data.bestGames
import com.hcode.webgames.data.hotGames
import com.varabyte.kobweb.compose.css.CSSTransition
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.functions.CSSImage
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.url
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.px


@Page
@Composable
fun HomePage() {
    val dark = remember { mutableStateOf(localStorage.getItem("darkModeGames").toBoolean()) }

    var scrollUp by remember { mutableStateOf(true) }
    var last = window.pageYOffset



    document.addEventListener("scroll", callback = {
        scrollUp = window.pageYOffset < last
        last = window.pageYOffset
    })

    Box(
        modifier = Modifier
            .id("mainBox")
            .fillMaxSize()
            .transition(CSSTransition(property = "background", duration = 1000.ms))
            .background(if (dark.value) MyColor.DARK_BLUE.color else MyColor.LIGHT_BLUE.color)
            .backgroundImage(CSSImage.Companion.of(url("bg-diamante.svg")))
            .overflow(Overflow.Auto)

    ) {

        MainPage(
            scrollUp = scrollUp,
            dark = dark
        )


    }
}




@Composable
fun MainPage(
    dark : MutableState<Boolean>,
    scrollUp : Boolean
){

     val index = remember { mutableStateOf(0) }
     val text = remember { mutableStateOf("") }
     val indexPage = remember { mutableStateOf(0) }
     val listSelected = remember { mutableStateOf(0) }
     val currentGame = remember { mutableStateOf(FDG("","","","")) }
     val isPlay = remember { mutableStateOf(false) }
     val scale = remember { mutableStateOf(0f) }
     val pageSelected = remember { mutableStateOf(0) }


    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(org.jetbrains.compose.web.css.Color.transparent)
    ) {
        if (isPlay.value) {
            GamePlace(isPlay = isPlay,  currentGame = currentGame, dark = dark.value, scale = scale)
        } else  {

            if (text.value.isEmpty()){
                when(pageSelected.value){
                    0 -> {
                        HomeGames(
                            dark = dark,
                            currentGame = currentGame,
                            scale = scale,
                            data = bestGames,
                            isPlay = isPlay
                        )
                    }
                    1 -> {
                        HomeGames(
                            dark = dark,
                            currentGame = currentGame,
                            scale = scale,
                            data = hotGames,
                            isPlay = isPlay
                        )
                    }
                    2 -> {
                        GamesLayout(
                            listGames = if (index.value < gamesListData.size) gamesListData[index.value] else allGames,
                            modifier = Modifier,
                            dark = dark.value,
                            index = indexPage,
                            listSelected = listSelected,
                            current = currentGame,
                            isPlay = isPlay,
                            scale = scale
                        )
                    }
                }
            }else{

            }

            TopBar(
                dark = dark,
                modifier = Modifier
                    .transition(CSSTransition(property = "all" , duration = 500.ms))
                    .fillMaxWidth()
                    .translateY(if (scrollUp)  0.px else (-60).px)
                    .position(Position.Fixed)
                    .background(Color.argb(0.2f,255,255,255))
                    .backdropFilter(blur(15.px)),
                text = text
            )

        }


        CategoryChooser(
            dark = dark.value,
            index = index,
            indexPage = indexPage,
            listSelected = listSelected,
            pageSelected = pageSelected
        )

        AddFav(
            modifier = Modifier
                .position(Position.Fixed)
                .align(Alignment.BottomCenter)
                .margin(bottom = 100.px),
            scale = scale
        )

    }


 }

