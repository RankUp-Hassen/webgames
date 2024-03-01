package com.hcode.webgames.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.varabyte.kobweb.compose.css.CSSTransition
import com.varabyte.kobweb.compose.css.Content
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.style.*
import org.jetbrains.compose.web.css.*

enum class MyColor(val color: Color) {
    BLUE(color = Color.rgb(r = 33, g = 150, b = 243)),
    LIGHT_BLUE(color = Color.rgb(153, 204, 255)),
    DARK_BLUE(color = Color.rgb(r = 34, g = 63, b = 94)),
    SOCIAL_ICON_BACKGROUND_LIGHT(color = Color.rgb(r = 230, g = 230, b = 230)),
    SOCIAL_ICON_BACKGROUND_DARK(color = Color.rgb(r = 48, g = 82, b = 118)),
    GRADIENT_ONE(color = Color.rgb(r = 161, g = 196, b = 253)),
    GRADIENT_ONE_DARK(color = Color.rgb(r = 19, g = 38, b = 58)),
    GRADIENT_TWO(color = Color.rgb(r = 194, g = 233, b = 251)),
    GRADIENT_TWO_DARK(color = Color.rgb(r = 20, g = 46, b = 73)),
    BLUE_ICON(color = Color.rgb(r = 0, g = 255, b = 255)),
    DARK_BLACK(color = Color.rgb(r = 30, g = 30, b = 30))

}


val itemStyle by ComponentStyle {
    base {
        Modifier
            .height(50.px)
            .fillMaxWidth()
            .borderRadius(25.px )
            .transition(CSSTransition(property = "width" , duration = 300.ms))
    }
}



 val titleStyle by ComponentStyle {
     before {
         Modifier
             .color(org.jetbrains.compose.web.css.Color.transparent)
             .styleModifier { property("-webkit-text-stroke", "1px #fff") }
             .position(Position.Absolute)
             .content("kobweb")
             .top((-3).px)
             .right((-4).px)
             .opacity(0.8)
     }
 }


val tagStyle by ComponentStyle {
    base {
        Modifier
            .rotate(0.deg)
            .transition(CSSTransition(property = "rotate", duration = 300.ms))
    }
    hover {
        Modifier
            .rotate(10.deg)

    }
}


val goBackStyle by ComponentStyle {
    base {
        Modifier
            .opacity(0.4)
            .transition(CSSTransition(property = "opacity", duration = 300.ms))
    }
    hover {
        Modifier
            .opacity(1)

    }
}



val MyDialogStyle by ComponentStyle.base {
    Modifier
        .display(DisplayStyle.Block)
        .position(Position.Fixed)
        .top(50.percent)
        .left(50.percent)
        .transform { translate((-50).percent, (-50).percent) }
        .backgroundColor(org.jetbrains.compose.web.css.Color.transparent)
        .zIndex(1000)
}
val OverlayStyle by ComponentStyle.base {
    Modifier
        .display(DisplayStyle.Block)
        .position(Position.Fixed)
        .top(0.px)
        .left(0.px)
        .backgroundColor(Color.rgba(0, 0, 0, 0.5f))
        .zIndex(999)
        .fillMaxSize()

}
