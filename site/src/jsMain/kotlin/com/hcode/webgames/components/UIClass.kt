package com.hcode.webgames.components

import androidx.compose.runtime.*
import com.hcode.webgames.data.bestGames
import com.hcode.webgames.data.hotGames
import com.stevdza.san.kotlinbs.components.*
import com.stevdza.san.kotlinbs.models.offcanvas.OffcanvasPlacement
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.CSSImage
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.url
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.TextInput
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.breakpoint.ResponsiveValues
import com.varabyte.kobweb.silk.components.style.common.PlaceholderColor
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import io.ktor.util.*
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.Draggable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.INSTANT
import org.w3c.dom.ScrollBehavior
import org.w3c.dom.ScrollToOptions
import org.w3c.dom.get


@Composable
fun ThemeButton( dark : MutableState<Boolean>,modifier: Modifier){
    Image(
        src = "dark_mode_icon_moon.png",
        modifier = modifier
            .size(40.px)
            .margin(10.px)
            .onClick {
                dark.value = !dark.value
                localStorage.setItem("darkModeGames", "${dark.value}")

            }
    )
}

@Composable
fun GamesCatalogsItem(
    icon: String,
    title: String,
    onClick: () -> Unit,
    color: CSSColorValue,
    count : Int
){
    Box(
        modifier = itemStyle.toModifier()
            .height(50.px)
            .background(color)
            .onClick {
                onClick()
            }
            .margin(5.px)
            .cursor(Cursor.Pointer)
            .hideOffcanvasOnClick()
    ) {

        Image(
            src = icon,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(30.px)
                .margin(10.px)
        )

        SpanText(
            text = title,
            modifier = Modifier
                .align(Alignment.Center)
                .fontSize( 16.px)
                .fontWeight(FontWeight.Bold)
                .color(Color.black)
        )

        SpanText(
            text = count.toString(),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fontSize( 12.px)
                .fontWeight(FontWeight.Bold)
                .color(Color.black)
                .margin(right = 10.px)
        )

    }
}


@Composable
fun TopBar(
    dark : MutableState<Boolean>,
    modifier : Modifier,
    text : MutableState<String>
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            src =  "menu-bar.png",
            modifier = Modifier
                .showOffcanvasOnClick(id = "myOffCanvas")
                .size(40.px)
                .margin(10.px)
        )

        TextInput(
            text = text.value,
            onTextChanged = {text.value = it},
            placeholder = "enter game name, category, desc ...",
            placeholderColor = PlaceholderColor(Color.white),
            modifier = Modifier
                .transition(CSSTransition(property = "background" , duration = 1000.ms))
                .fillMaxWidth()
                .borderRadius(20.px)
                .background(Color.transparent)
                .fontWeight(FontWeight.Bold)
                .color(Color.white)
        )

        ThemeButton(
            dark,
            modifier = Modifier
        )
    }
}



@Composable
fun GamesLayout(
    listGames: List<FDG>,
    itemMargin: Int = 5,
    modifier: Modifier,
    dark: Boolean,
    index: MutableState<Int>,
    listSelected: MutableState<Int>,
    current: MutableState<FDG>,
    isPlay : MutableState<Boolean>,
    scale : MutableState<Float>
){
    val breakpoint = rememberBreakpoint()
    var itemW by remember { mutableStateOf(getItemSize(breakpoint) - itemMargin*2) }
    var repeat by remember { mutableStateOf(false) }
    LaunchedEffect(repeat){
        itemW = getItemSize(breakpoint) - itemMargin*2
        repeat = ! repeat
    }

    val multiList = mutableListOf<List<FDG>>()
    listGames.chunked(50).forEach {
        multiList.add(it)
    }
    val s = remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()


        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ){
            SimpleGrid(
                numColumns = ResponsiveValues(base = 3 , md = 5 , sm = 3 , lg = 8 , xl = 8),
                modifier = modifier
                    .margin(top = 80.px)
            ){

                multiList[index.value].forEach { game ->
                    GameItemDisplay(
                        game = game,
                        modifier = Modifier
                            .transition(CSSTransition(property = "opacity" , duration = 500.ms))
                            .margin(itemMargin.px)
                            .size(itemW.px)
                            .borderRadius(r = 20.percent)
                            .opacity(s.value)
                            .onClick {
                                current.value = game
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
                            }
                    )
                }
            }

            NextPage(
                index = index,
                indices = multiList.indices,
                dark = dark,
                s = s,
                listSelected = listSelected,
                modifier = Modifier,
            )

        }



    LaunchedEffect(s.value){
        delay(500)
        s.value = 1
    }
}

@Composable
fun GameItemDisplay(
    game: FDG,
    modifier: Modifier,
    textW : Int? = 1
) {
    var showName by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .cursor(Cursor.Pointer)
            .onMouseEnter {
                showName = true
            }
            .onMouseLeave {
                showName = false
            }
            .borderRadius(r = 20.percent)
            .draggable(Draggable.True)



    ) {
        Image(
            src = getImageUrl(game.thumb),
            modifier = Modifier
                .transition(CSSTransition(property = "all" , duration = 500.ms))
                .fillMaxSize()
                .borderRadius(r = 20.percent)
                .filter(blur(if (showName) 3.px else 0.px))
                .draggable(draggable = false)

                
        )

        SpanText(
            modifier = Modifier
                .position(Position.Relative)
                .align(Alignment.Center)
                .transition(CSSTransition(property = "all" , duration = 500.ms))
                .opacity(if(showName) 100.percent else 0.percent)
                .color(Color.white)
                .fontSize(20.px)
                .fontWeight(FontWeight.Bold)
                .textAlign(TextAlign.Center)
                .padding(10.px)
                .textShadow(4.px, 4.px, 2.px, Color.black)
                .fontFamily("serif")
                .thenIf(
                    condition = textW != 1,
                    other = Modifier
                        .width(textW!!.px)
                ),
            text = game.title,

        )
        
       
        
        
        
    }
}


@Composable
fun NextPage(
    dark: Boolean,
    index : MutableState<Int>,
    s : MutableState<Int>,
    indices : IntRange,
    listSelected : MutableState<Int>,
    modifier: Modifier,
){
    val scope = rememberCoroutineScope()
    val itemsV = when(rememberBreakpoint()){
        Breakpoint.MD -> { 5 }
        Breakpoint.LG -> { 10 }
        Breakpoint.XL -> { 10 }
        Breakpoint.SM -> { 3 }
        else -> { 3 }

    }

    val listIndex = mutableListOf<Int>()
    for (i in indices){
        listIndex.add(i)
    }
    val chunkedList = listIndex.chunked(itemsV)

    Box (
        modifier = modifier
            .width(80.percent)
            .height(50.px)
            .borderRadius(r = 25.px)
            .margin(30.px)
            .background(if (dark) MyColor.SOCIAL_ICON_BACKGROUND_DARK.color else MyColor.SOCIAL_ICON_BACKGROUND_LIGHT.color)
    ) {
        Button(
            onClick = {
                listSelected.value -= if (listSelected.value - 1 in chunkedList.indices) 1 else 0
            },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(40.px)
                .margin(left = 10.px)
                .background(if (dark) MyColor.SOCIAL_ICON_BACKGROUND_DARK.color else MyColor.SOCIAL_ICON_BACKGROUND_LIGHT.color)
                .borderRadius(r = 20.px)
        ){
            Image(
                src = "arrow_l.png",
                modifier = Modifier
                    .size(40.px),
            )
        }

        Button(
            onClick = {
                listSelected.value += if (listSelected.value + 1 in chunkedList.indices) 1 else 0

            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(40.px)
                .margin(right = 10.px)
                .background(if (dark) MyColor.SOCIAL_ICON_BACKGROUND_DARK.color else MyColor.SOCIAL_ICON_BACKGROUND_LIGHT.color)
                .borderRadius(r = 20.px),

        ){
            Image(
                src =  "arrow_r.png",
                modifier = Modifier
                    .size(40.px),
            )
        }


        Row (
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(70.percent)
                .fillMaxHeight()
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){

            chunkedList[listSelected.value].forEach { ii ->
                Button(
                    onClick = {
                        scope.launch{
                            s.value = 0
                            delay(500)
                            index.value = ii
                        }



                    },
                    modifier = Modifier
                        .size(40.px)
                        .margin(left = 10.px, right = 10.px)
                        .borderRadius(r = 20.px)
                        .border(2.px , color = Color.black)
                        .background(if (ii==index.value) MyColor.BLUE_ICON.color else Color.transparent)
                ){
                    SpanText(
                        text = "${ii+1}"
                    )
                }
            }
        }

    }




}




@Composable
fun CategoryChooser(
    dark: Boolean,
    index: MutableState<Int>,
    indexPage: MutableState<Int>,
    listSelected: MutableState<Int>,
    pageSelected: MutableState<Int>,

    ){
    BSOffcanvas(
        id = "myOffCanvas",
        title = "Category",
        body = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ){
                GamesCatalogsItem(
                    icon = "best-seller.png",
                    title = "Best Games",
                    color = if (pageSelected.value == 0) MyColor.BLUE_ICON.color else Color.white,
                    onClick = {
                        pageSelected.value = 0
                    },
                    count = bestGames.size
                )
                GamesCatalogsItem(
                    icon = "hot-sale.png",
                    title = "Hot Games",
                    color = if (pageSelected.value == 1) MyColor.BLUE_ICON.color else Color.white,
                    onClick = {
                        pageSelected.value = 1
                    },
                    count = hotGames.size
                )
                listMenu.forEachIndexed { i, itemData ->
                    GamesCatalogsItem(
                        icon = itemData.icon,
                        title = itemData.title,
                        color = if (i == index.value && pageSelected.value == 2) MyColor.BLUE_ICON.color else Color.white,
                        onClick = {
                            indexPage.value = 0
                            listSelected.value = 0
                            index.value = i
                            pageSelected.value = 2

                        },
                        count = if (i < gamesListData.size) gamesListData[i].size else allGames.size
                    )
                }
            }
        },
        placement = OffcanvasPlacement.START,
        dark = dark,
        modifier = Modifier
            .background(if (dark) MyColor.DARK_BLACK.color else MyColor.DARK_BLUE.color)

    )


}





@Composable
fun GamePlace(
    isPlay : MutableState<Boolean>,
    currentGame : MutableState<FDG>,
    dark: Boolean,
    scale: MutableState<Float>
) {
    val breakpoint = rememberBreakpoint()
    val screenHeight = window.innerHeight
    val tag = remember { mutableStateOf("") }
    val show = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .position(Position.Absolute)
    ) {
        SimpleGrid(
            numColumns = ResponsiveValues(base = 1 , md = 1 , lg = 2 , xl = 2 , sm = 1),
            modifier = Modifier
                .fillMaxSize()

        ){
            LeftSide(
                modifier = Modifier
                    .fillMaxSize()
                    .thenIf(
                        condition = breakpoint <= Breakpoint.MD,
                        other = Modifier
                            .height(screenHeight.px)
                            .fillMaxWidth()
                    ),
                url = getGameUrl(currentGame.value.thumb) ,
                BP = breakpoint,
                dark = dark,
            )
            RightSide(
                modifier = Modifier
                    .fillMaxSize(),
                data = currentGame,
                dark = dark,
                tag = tag,
                show = show,
                scale = scale
            )

        }

        Image(
            src = "previous.png",
            modifier = goBackStyle.toModifier()
                .margin(10.px)
                .position(Position.Fixed)
                .size(35.px)
                .cursor(Cursor.Pointer)
                .onClick {
                    isPlay.value = false
                }
        )

        Image(
            src = "fullscreen.png",
            modifier = goBackStyle.toModifier()
                .translateY(45.px)
                .margin(10.px)
                .position(Position.Fixed)
                .size(35.px)
                .cursor(Cursor.Pointer)
                .onClick {
                    val iframe = document.getElementById("GameIframe")
                   iframe?.let {
                       if (document.fullscreen){
                           document.exitFullscreen()
                       }else{
                           window.scrollTo(options = ScrollToOptions(left = 0.0 , top = 0.0 , behavior = ScrollBehavior.INSTANT))
                           it.requestFullscreen()
                       }
                   }
                }


        )

        if (show.value){
            Dialog{
                DialogGamesTags(tag = tag.value , dark = dark , currentGame = currentGame , show = show, scale = scale)
            }
        }

        /*
        AddFav(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            scale = scale
        )

         */

    }


}

@Composable
private fun RightSide(
    modifier: Modifier ,
    data : MutableState<FDG> ,
    dark: Boolean,
    tag: MutableState<String>,
    show: MutableState<Boolean>,
    scale: MutableState<Float>
) {
    val isLoaded = remember { mutableStateOf(false) }
    var otherData by remember { mutableStateOf(Item("","","")) }
    LaunchedEffect(isLoaded.value){
        if (!isLoaded.value){
            otherData = getGameDataFromServer(data.value.id,isLoaded)
        }
    }
    Box(
        modifier = modifier
            .background(if (dark) MyColor.DARK_BLUE.color else MyColor.LIGHT_BLUE.color)
            .backgroundImage(CSSImage.Companion.of(url("bg-diamante.svg")))
            .padding(20.px),
        contentAlignment = Alignment.Center
    ) {


        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(com.varabyte.kobweb.compose.ui.graphics.Color.argb(0.1f,255,255,255))
                .backdropFilter(blur(15.px))
                .overflow(Overflow.Auto)
                .borderRadius(20.px)
                .padding(20.px),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            RSTitles(title = "Tags" , index = 1 , modifier = Modifier.margin(topBottom = 20.px), dark = dark)
            CategoryListForGamePlace(s = data.value.tags, dark = dark , tag = tag , show = show)
            RSTitles(title = "Description" , index = 2 , modifier = Modifier.margin(topBottom = 20.px), dark = dark)
            Description(desc = otherData.description)
            RSTitles(title = "Instructions" , index = 3 , modifier = Modifier.margin(topBottom = 20.px), dark = dark)
            Description(desc = otherData.instructions)
            RSTitles(title = "Video" , index = 4 , modifier = Modifier.margin(topBottom = 20.px), dark = dark)
            VideoGame(url = getVideoUrl(data.value.thumb))
            RSTitles(title = "Similar Games" , index = 5 , modifier = Modifier.margin(topBottom = 20.px), dark = dark)
            SimilarGames(id = data.value.id, currentGame = data , scale = scale )

        }


    }

}

@Composable
private fun LeftSide(
    modifier: Modifier ,
    url : String ,
    BP: Breakpoint,
    dark: Boolean,
){
    var y by remember { mutableStateOf(0.0) }
    val screenHeight = window.innerHeight
    document.addEventListener("scroll", callback = {
        y = window.pageYOffset
    })

    Box (
        modifier = modifier
            .background(if (dark) MyColor.DARK_BLUE.color else MyColor.LIGHT_BLUE.color)
            .backgroundImage(CSSImage.Companion.of(url("bg-diamante.svg")))
            .thenIf(
                condition = BP > Breakpoint.MD,
                other = Modifier
                    .padding(20.px)
            ),
        contentAlignment = Alignment.TopCenter
    ){

        Iframe(
            attrs = Modifier
                .id("GameIframe")
                .fillMaxWidth()
                .height(screenHeight.px)
                .thenIf(
                    condition = BP > Breakpoint.MD,
                    other = Modifier
                        .borderRadius(20.px)
                        .height((screenHeight-40).px)
                        .translateY(y.px)
                )
                .toAttrs{
                    attr("src", url)
                }
        )
    }

}

@Composable
private fun RSTitles(
    title: String,
    index: Int,
    modifier: Modifier,
    dark: Boolean
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.px)
            .borderRadius(20.px)
            .border(1.px, LineStyle.Solid, color = if (dark) MyColor.LIGHT_BLUE.color else MyColor.DARK_BLUE.color)
    ) {
        SpanText(
            text = "$index",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(35.px)
                .fontSize(22.px)
                .textAlign(TextAlign.Center)
                .fontWeight(FontWeight.Bold)
                .color(Color.white)
                .borderRadius(17.5.px)
                .background(if (dark) MyColor.LIGHT_BLUE.color else MyColor.DARK_BLUE.color)
                .margin(left = 2.5.px)
        )
        SpanText(
            text = title,
            modifier = Modifier
                .align(Alignment.Center)
                .fontWeight(FontWeight.Bold)
                .color(Color.white)
                .fontStyle(FontStyle.Oblique)
                .fontFamily("serif")
                .fontSize(32.px)
                .textShadow(2.px, 2.px, 2.px, Color.black)
        )
    }
}



@Composable
private fun CategoryListForGamePlace(
    s :String,
    dark: Boolean,
    tag: MutableState<String>,
    show: MutableState<Boolean>
){
    val cats = s.split(",")

    Ul(
        attrs = Modifier
            .fillMaxWidth()
            .textAlign(TextAlign.Left)
            .padding(10.px)
            .toAttrs()
    ){
        cats.forEach { s ->
            Li(
                attrs = tagStyle.toModifier()
                    .display(DisplayStyle.InlineBlock)
                    .margin(2.px)
                    .toAttrs()
            ) {
                Button(
                    onClick = {
                        tag.value = s
                        show.value = true
                    },
                    modifier = Modifier
                        .background(if (dark) MyColor.LIGHT_BLUE.color else MyColor.DARK_BLUE.color)
                ){
                    SpanText(
                        text = s,
                        modifier = Modifier
                            .color(Color.white)
                    )
                }
            }
        }

    }



}


@Composable
private fun Description(
    desc : String
){
    P(
        attrs = Modifier
            .padding(10.px)
            .color(Color.white)
            .fontSize(FontSize.Larger)
            .textAlign(TextAlign.Center)
            .textShadow(2.px, 2.px, 2.px, Color.black)
            .toAttrs()
    ) {
        Text(desc)
    }

}

@Composable
private fun VideoGame(
    url: String
){
    val screenHeight = window.innerHeight/2f

    Iframe(
        attrs = Modifier
            .id("VideoIframe")
            .fillMaxWidth()
            .height(screenHeight.px)
            .borderRadius(20.px)
            .toAttrs{
                attr("src", url)
            }
    )

    Link(
        path = url
    ){
        Text("Original Video Source")
    }
}


@Composable
private fun SimilarGames(
    id : String,
    currentGame: MutableState<FDG>,
    scale: MutableState<Float>
){
    val scope = rememberCoroutineScope()
    val similar = getRandomGame(getIndexCatFromId(id))
    var itemWidth by remember { mutableStateOf(200f) }
    Ul(
        attrs = Modifier
            .id("SimilarGames")
            .fillMaxWidth()
            .textAlign(TextAlign.Center)
            .padding(10.px)
            .toAttrs()
    ){
        similar.forEach {game ->
            Li(
                attrs = Modifier
                    .display(DisplayStyle.InlineBlock)
                    .margin(5.px)
                    .toAttrs()
            ) {
                GameItemDisplay(
                    game = game,
                    modifier = Modifier
                        .size(itemWidth.px)
                        .borderRadius(20.px)
                        .onClick {
                            scope.launch {
                                clenIframe()
                                window.scrollTo(y = 0.0 , x = 0.0)
                                delay(300)
                                currentGame.value = game

                            }

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
                    textW = itemWidth.toInt()
                )
            }
        }

    }
    LaunchedEffect(Unit) {
        val element = document.getElementById("SimilarGames")
        element?.let {
            itemWidth = (it.clientWidth-70)/4f
        }
    }

}


@Composable
fun DialogGamesTags(
    tag : String,
    dark: Boolean,
    currentGame: MutableState<FDG>,
    show : MutableState<Boolean>,
    scale: MutableState<Float>
){
    val scope = rememberCoroutineScope()
    var chunkedIndex by remember { mutableIntStateOf(0) }
    val gameLists = getGamesWithTag(tag).chunked(18)
    var s by remember { mutableStateOf(0) }



    Box(
        modifier = Modifier
            .width((window.innerWidth).px)
            .height((window.innerHeight).px)
            .background(Color.transparent),
        contentAlignment = Alignment.Center

    ) {
        Column (
            modifier = Modifier
                .width((window.innerWidth-50).px)
                .height((window.innerHeight-50).px)
                .borderRadius(10.px)
                .background(if (dark) MyColor.DARK_BLUE.color else MyColor.LIGHT_BLUE.color)
                .backgroundImage(CSSImage.Companion.of(url("bg-diamante.svg")))


        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                SpanText(
                    text = tag,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fontWeight(FontWeight.Bold)
                        .color(Color.white)
                        .fontStyle(FontStyle.Oblique)
                        .fontFamily("serif")
                        .fontSize(40.px)
                        .textShadow(2.px, 2.px, 2.px, Color.black)
                        .margin(10.px)
                )

                Image(
                    src = "close_dialog.png",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(35.px)
                        .margin(5.px)
                        .cursor(Cursor.Pointer)
                        .onClick {
                            show.value = false

                        }

                )
            }

            Ul(
                attrs = Modifier
                    .id("GamesFromTag")
                    .fillMaxSize()
                    .textAlign(TextAlign.Center)
                    .padding(10.px)
                    .overflow(Overflow.Auto)
                    .toAttrs()
            ){

                gameLists[chunkedIndex].forEach {game ->
                    Li(
                        attrs = Modifier
                            .display(DisplayStyle.InlineBlock)
                            .margin(5.px)
                            .toAttrs()
                    ) {
                        GameItemDisplay(
                            game = game,
                            modifier = Modifier
                                .transition(CSSTransition(property = "opacity" , duration = 500.ms))
                                .size(150.px)
                                .borderRadius(20.px)
                                .opacity(s)
                                .onClick {
                                    scope.launch {
                                        clenIframe()
                                        window.scrollTo(y = 0.0 , x = 0.0)
                                        delay(300)
                                        currentGame.value = game
                                        show.value = false

                                    }
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
                            textW = 140
                        )
                    }
                }
            }
        }

        Image(
            src = "arrow_l.png",
            modifier = Modifier
                .position(Position.Fixed)
                .cursor(Cursor.Pointer)
                .align(Alignment.CenterStart)
                .size(50.px)
                .borderRadius(20.px)
                .margin(left = 50.px)
                .onClick {
                    s = 0
                    scope.launch {
                        delay(500)
                        chunkedIndex -= if (chunkedIndex-1  in gameLists.indices ) 1 else 0
                    }
                }
        )

        Image(
            src = "arrow_r.png",
            modifier = Modifier
                .position(Position.Fixed)
                .cursor(Cursor.Pointer)
                .align(Alignment.CenterEnd)
                .size(40.px)
                .borderRadius(20.px)
                .margin(right = 50.px)
                .onClick {
                    s = 0
                    scope.launch {
                        delay(500)
                        chunkedIndex += if (chunkedIndex+1  in gameLists.indices ) 1 else 0
                    }
                }

        )
        AddFav(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .margin(bottom = 100.px)
            ,
            scale = scale
        )
    }

    LaunchedEffect(s){
        delay(500)
        s = 1
    }
}

@Composable
fun Dialog(
    modifier: Modifier = Modifier,
    content : @Composable () -> Unit
){
    Div(attrs = OverlayStyle.toModifier().fillMaxSize().id("overlay").toAttrs())
    Div(
        attrs = MyDialogStyle.toModifier().id("myDialog").then(other = modifier).toAttrs()
    ) {
        content()
    }

}

@Composable
fun AddFav(
    modifier: Modifier,
    scale : MutableState<Float>
){


    Image(
        src = "fav_icon.png",
        modifier = modifier
            .transition(CSSTransition(property = "scale" , duration = 300.ms))
            .size(200.px)
            .borderRadius(50.percent)
            .boxShadow(blurRadius = 3.px, color = com.varabyte.kobweb.compose.ui.graphics.Color.argb(1f ,237, 77, 100) , spreadRadius = 10.px )
            .scale(scale.value)
            .background(com.varabyte.kobweb.compose.ui.graphics.Color.argb(1f ,237, 77, 100))
            .onDragOver {
                it.preventDefault()
                scale.value = 1.3f
            }
            .onDragLeave {
                scale.value = 1f
            }
            .onDrop { event ->
                event.preventDefault()
                event.dataTransfer?.items?.get(0)?.getAsString {
                    println(it)
                }
                scale.value = 0f
            }
    )


}


