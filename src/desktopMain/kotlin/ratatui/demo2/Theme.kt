// port-lint: source examples/apps/demo2/src/theme.rs
package ratatui.demo2

import ratatui.style.Color
import ratatui.style.Modifier
import ratatui.style.Style

private val DARK_BLUE: Color = Color.Rgb(16.toUByte(), 24.toUByte(), 48.toUByte())
private val LIGHT_BLUE: Color = Color.Rgb(64.toUByte(), 96.toUByte(), 192.toUByte())
private val LIGHT_YELLOW: Color = Color.Rgb(192.toUByte(), 192.toUByte(), 96.toUByte())
private val LIGHT_GREEN: Color = Color.Rgb(64.toUByte(), 192.toUByte(), 96.toUByte())
private val LIGHT_RED: Color = Color.Rgb(192.toUByte(), 96.toUByte(), 96.toUByte())
private val RED: Color = Color.Rgb(215.toUByte(), 0.toUByte(), 0.toUByte())
// Not really black, often #080808.
private val BLACK: Color = Color.Rgb(8.toUByte(), 8.toUByte(), 8.toUByte())
private val DARK_GRAY: Color = Color.Rgb(68.toUByte(), 68.toUByte(), 68.toUByte())
private val MID_GRAY: Color = Color.Rgb(128.toUByte(), 128.toUByte(), 128.toUByte())
private val LIGHT_GRAY: Color = Color.Rgb(188.toUByte(), 188.toUByte(), 188.toUByte())
// Not really white, often #eeeeee.
private val WHITE: Color = Color.Rgb(238.toUByte(), 238.toUByte(), 238.toUByte())

data class Theme(
    val root: Style,
    val content: Style,
    val appTitle: Style,
    val tabs: Style,
    val tabsSelected: Style,
    val borders: Style,
    val description: Style,
    val descriptionTitle: Style,
    val keyBinding: KeyBinding,
    val logo: Logo,
    val email: Email,
    val traceroute: Traceroute,
    val recipe: Recipe
)

data class KeyBinding(
    val key: Style,
    val description: Style
)

data class Logo(
    val ratEye: Color,
    val ratEyeAlt: Color
)

data class Email(
    val tabs: Style,
    val tabsSelected: Style,
    val inbox: Style,
    val item: Style,
    val selectedItem: Style,
    val header: Style,
    val headerValue: Style,
    val body: Style
)

data class Traceroute(
    val header: Style,
    val selected: Style,
    val ping: Style,
    val map: Map
)

data class Map(
    val style: Style,
    val color: Color,
    val path: Color,
    val source: Color,
    val destination: Color,
    val backgroundColor: Color
)

data class Recipe(
    val ingredients: Style,
    val ingredientsHeader: Style
)

val THEME: Theme = Theme(
    root = Style.new().bg(DARK_BLUE),
    content = Style.new().bg(DARK_BLUE).fg(LIGHT_GRAY),
    appTitle = Style.new()
        .fg(WHITE)
        .bg(DARK_BLUE)
        .addModifier(Modifier.BOLD),
    tabs = Style.new().fg(MID_GRAY).bg(DARK_BLUE),
    tabsSelected = Style.new()
        .fg(WHITE)
        .bg(DARK_BLUE)
        .addModifier(Modifier.BOLD)
        .addModifier(Modifier.REVERSED),
    borders = Style.new().fg(LIGHT_GRAY),
    description = Style.new().fg(LIGHT_GRAY).bg(DARK_BLUE),
    descriptionTitle = Style.new().fg(LIGHT_GRAY).addModifier(Modifier.BOLD),
    logo = Logo(
        ratEye = BLACK,
        ratEyeAlt = RED
    ),
    keyBinding = KeyBinding(
        key = Style.new().fg(BLACK).bg(DARK_GRAY),
        description = Style.new().fg(DARK_GRAY).bg(BLACK)
    ),
    email = Email(
        tabs = Style.new().fg(MID_GRAY).bg(DARK_BLUE),
        tabsSelected = Style.new()
            .fg(WHITE)
            .bg(DARK_BLUE)
            .addModifier(Modifier.BOLD),
        inbox = Style.new().bg(DARK_BLUE).fg(LIGHT_GRAY),
        item = Style.new().fg(LIGHT_GRAY),
        selectedItem = Style.new().fg(LIGHT_YELLOW),
        header = Style.new().addModifier(Modifier.BOLD),
        headerValue = Style.new().fg(LIGHT_GRAY),
        body = Style.new().bg(DARK_BLUE).fg(LIGHT_GRAY)
    ),
    traceroute = Traceroute(
        header = Style.new()
            .bg(DARK_BLUE)
            .addModifier(Modifier.BOLD)
            .addModifier(Modifier.UNDERLINED),
        selected = Style.new().fg(LIGHT_YELLOW),
        ping = Style.new().fg(WHITE),
        map = Map(
            style = Style.new().bg(DARK_BLUE),
            backgroundColor = DARK_BLUE,
            color = LIGHT_GRAY,
            path = LIGHT_BLUE,
            source = LIGHT_GREEN,
            destination = LIGHT_RED
        )
    ),
    recipe = Recipe(
        ingredients = Style.new().bg(DARK_BLUE).fg(LIGHT_GRAY),
        ingredientsHeader = Style.new()
            .addModifier(Modifier.BOLD)
            .addModifier(Modifier.UNDERLINED)
    )
)
