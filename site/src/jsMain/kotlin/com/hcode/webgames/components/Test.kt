package com.hcode.webgames.components

import androidx.compose.runtime.*
import com.hcode.webgames.data.action
import com.hcode.webgames.data.hotGames
import com.stevdza.san.kotlinbs.components.BSButton
import com.varabyte.kobweb.compose.css.CSSTransition
import com.varabyte.kobweb.compose.css.GridAuto
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.functions.CSSImage
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.url
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.INSTANT
import org.w3c.dom.ScrollBehavior
import org.w3c.dom.ScrollToOptions

import kotlin.random.Random


@Composable
fun HomeGames(
    dark: MutableState<Boolean>,
    currentGame: MutableState<FDG>,
    scale: MutableState<Float>,
    data: List<FDG>,
    isPlay: MutableState<Boolean>
){
    val scope = rememberCoroutineScope()
    var scrollUp by remember { mutableStateOf(true) }
    var part by remember { mutableStateOf(11) }
    var count = (((window.innerWidth-100)/150)*2)-3
    part = if (count%2 == 1) count else count - 1
    var more by remember { mutableStateOf(10) }
    var show by remember { mutableStateOf(false) }
    var last = window.pageYOffset
    document.addEventListener("scroll", callback = {
        scrollUp = window.pageYOffset < last
        last = window.pageYOffset
    })
    val dataGames = if (more*part < data.lastIndex) data.subList(0,more*part)else data
    var op by remember { mutableStateOf(0) }

    window.addEventListener("resize", callback = {
        count = (((window.innerWidth-100)/150)*2)-3
        part = if (count%2 == 1) count else count - 1
    })



    Box (
        modifier = Modifier
            .id("HomeGames")
            .fillMaxSize()
            .transition(CSSTransition(property = "background", duration = 1000.ms))
            .background(if (dark.value) MyColor.DARK_BLUE.color else MyColor.LIGHT_BLUE.color)
            .backgroundImage(CSSImage.Companion.of(url("bg-diamante.svg")))

    ){

        Column (
            modifier = Modifier
                .transition(CSSTransition(property = "opacity" , duration = 300.ms ))
                .opacity(op)
                .fillMaxSize()
                .overflow(Overflow.Auto)
                .padding(top = 80.px),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            if (show){
                dataGames.chunked(part).forEach{ games ->
                    CustomRow(
                        data = games,
                        size = 150,
                        currentGame = currentGame,
                        scale = scale,
                        isPlay = isPlay
                    )
                }

                Button(
                    onClick = {
                        if (more*part < data.size){
                            val y = window.pageYOffset
                            op = 0
                            scope.launch {
                                delay(300)
                                more+=10
                                delay(1000)
                                window.scrollTo(options = ScrollToOptions(left = 0.0 , top = y , behavior = ScrollBehavior.INSTANT))
                                op = 1
                            }
                        }
                    },
                    modifier = Modifier
                        .width(300.px)
                        .height(50.px)
                        .margin(20.px)
                        .background(Color.argb(0.2f,255,255,255))
                        .backdropFilter(blur(15.px))
                        .borderRadius(20.px),

                ){
                    Text("Show More")
                }

            }
        }


        AddFav(
            modifier = Modifier
                .position(Position.Fixed)
                .align(Alignment.TopCenter)
                .margin(top = 100.px),
            scale = scale
        )

    }

    scope.launch {
        show = true
        op = 1

    }



}




@Composable
fun CustomRow(
    data: List<FDG> = action,
    size: Int,
    currentGame: MutableState<FDG>,
    scale: MutableState<Float>,
    padding : Int = 5,
    isPlay : MutableState<Boolean>
){
    val scope = rememberCoroutineScope()
    var randomIndex = Random.nextInt(0,data.size)
    while (randomIndex%2!=0){
        randomIndex = Random.nextInt(0,data.size)
    }

    val l1 = data.subList(0,randomIndex)
    val l2 = data.subList(randomIndex+1,data.size)

    Row{
        Column {
            Row {
                l1.forEachIndexed { index, game ->
                    if (index%2==0){
                        GameItemDisplay(
                            game = game,
                            modifier = Modifier
                                .size(size.px)
                                .padding(padding.px)
                                .borderRadius(20.px)
                                .onClick {
                                    window.scrollTo(options = ScrollToOptions(left = 0.0 , top = 0.0 , behavior = ScrollBehavior.INSTANT))
                                    currentGame.value = game
                                    isPlay.value = true
                                }
                                .onDragStart {
                                    it.dataTransfer!!.items.add(game.id ,"text/plain")
                                    scope.launch {
                                        scale.value=1.3f
                                        delay(300)
                                        scale.value=1f
                                    }

                                }
                                .onDragEnd {
                                    scale.value = 0f
                                },
                        )
                    }
                }
            }
            Row {
                l1.forEachIndexed { index, game ->
                    if (index%2==1){
                        GameItemDisplay(
                            game = game,
                            modifier = Modifier
                                .padding(padding.px)
                                .size(size.px)
                                .borderRadius(20.px)
                                .onClick {
                                    window.scrollTo(options = ScrollToOptions(left = 0.0 , top = 0.0 , behavior = ScrollBehavior.INSTANT))
                                    currentGame.value = game
                                    isPlay.value = true
                                }
                                .onDragStart {
                                    it.dataTransfer!!.items.add(game.id ,"text/plain")
                                    scope.launch {
                                        scale.value=1.3f
                                        delay(300)
                                        scale.value=1f
                                    }

                                }
                                .onDragEnd {
                                    scale.value = 0f
                                },
                        )
                    }
                }
            }
        }

        GameItemDisplay(
            game = data[randomIndex],
            modifier = Modifier
                .padding(padding.px)
                .size((size*2).px)
                .borderRadius(20.px)
                .onClick {
                    window.scrollTo(options = ScrollToOptions(left = 0.0 , top = 0.0 , behavior = ScrollBehavior.INSTANT))
                    currentGame.value = data[randomIndex]
                    isPlay.value = true
                }
                .onDragStart {
                    it.dataTransfer!!.items.add(data[randomIndex].id ,"text/plain")
                    scope.launch {
                        scale.value=1.3f
                        delay(300)
                        scale.value=1f
                    }
                }
                .onDragEnd {
                    scale.value = 0f
                },
        )

        Column {
            Row {
                l2.forEachIndexed { index, game ->
                    if (index%2==0){
                        GameItemDisplay(
                            game = game,
                            modifier = Modifier
                                .size(size.px)
                                .padding(padding.px)
                                .borderRadius(20.px)
                                .onClick {
                                    window.scrollTo(options = ScrollToOptions(left = 0.0 , top = 0.0 , behavior = ScrollBehavior.INSTANT))
                                    currentGame.value = game
                                    isPlay.value = true
                                }
                                .onDragStart {
                                    it.dataTransfer!!.items.add(game.id ,"text/plain")
                                    scope.launch {
                                        scale.value=1.3f
                                        delay(300)
                                        scale.value=1f
                                    }
                                }
                                .onDragEnd {
                                    scale.value = 0f
                                },
                        )
                    }
                }
            }
            Row {
                l2.forEachIndexed { index, game ->
                    if (index%2==1){
                        GameItemDisplay(
                            game = game,
                            modifier = Modifier
                                .size(size.px)
                                .padding(padding.px)
                                .borderRadius(20.px)
                                .onClick {
                                    window.scrollTo(options = ScrollToOptions(left = 0.0 , top = 0.0 , behavior = ScrollBehavior.INSTANT))
                                    currentGame.value = game
                                    isPlay.value = true
                                }
                                .onDragStart {
                                    it.dataTransfer!!.items.add(game.id ,"text/plain")
                                    scope.launch {
                                        scale.value=1.3f
                                        delay(300)
                                        scale.value=1f
                                    }
                                }
                                .onDragEnd {
                                    scale.value = 0f
                                },
                        )
                    }
                }
            }
        }
    }
}