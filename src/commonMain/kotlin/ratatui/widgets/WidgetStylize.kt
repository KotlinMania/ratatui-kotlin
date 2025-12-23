package ratatui.widgets

import ratatui.style.Color
import ratatui.style.Modifier
import ratatui.style.Style
import ratatui.widgets.chart.Axis
import ratatui.widgets.chart.Dataset
import ratatui.widgets.sparkline.Sparkline

// ============================================================================
// Sparkline Stylize Extensions
// ============================================================================

fun Sparkline.fg(color: Color): Sparkline = style(getStyle().fg(color))
fun Sparkline.bg(color: Color): Sparkline = style(getStyle().bg(color))
fun Sparkline.addModifier(modifier: Modifier): Sparkline = style(getStyle().addModifier(modifier))
fun Sparkline.removeModifier(modifier: Modifier): Sparkline = style(getStyle().removeModifier(modifier))

// Foreground colors
fun Sparkline.black(): Sparkline = fg(Color.Black)
fun Sparkline.red(): Sparkline = fg(Color.Red)
fun Sparkline.green(): Sparkline = fg(Color.Green)
fun Sparkline.yellow(): Sparkline = fg(Color.Yellow)
fun Sparkline.blue(): Sparkline = fg(Color.Blue)
fun Sparkline.magenta(): Sparkline = fg(Color.Magenta)
fun Sparkline.cyan(): Sparkline = fg(Color.Cyan)
fun Sparkline.gray(): Sparkline = fg(Color.Gray)
fun Sparkline.darkGray(): Sparkline = fg(Color.DarkGray)
fun Sparkline.lightRed(): Sparkline = fg(Color.LightRed)
fun Sparkline.lightGreen(): Sparkline = fg(Color.LightGreen)
fun Sparkline.lightYellow(): Sparkline = fg(Color.LightYellow)
fun Sparkline.lightBlue(): Sparkline = fg(Color.LightBlue)
fun Sparkline.lightMagenta(): Sparkline = fg(Color.LightMagenta)
fun Sparkline.lightCyan(): Sparkline = fg(Color.LightCyan)
fun Sparkline.white(): Sparkline = fg(Color.White)

// Background colors
fun Sparkline.onBlack(): Sparkline = bg(Color.Black)
fun Sparkline.onRed(): Sparkline = bg(Color.Red)
fun Sparkline.onGreen(): Sparkline = bg(Color.Green)
fun Sparkline.onYellow(): Sparkline = bg(Color.Yellow)
fun Sparkline.onBlue(): Sparkline = bg(Color.Blue)
fun Sparkline.onMagenta(): Sparkline = bg(Color.Magenta)
fun Sparkline.onCyan(): Sparkline = bg(Color.Cyan)
fun Sparkline.onGray(): Sparkline = bg(Color.Gray)
fun Sparkline.onDarkGray(): Sparkline = bg(Color.DarkGray)
fun Sparkline.onLightRed(): Sparkline = bg(Color.LightRed)
fun Sparkline.onLightGreen(): Sparkline = bg(Color.LightGreen)
fun Sparkline.onLightYellow(): Sparkline = bg(Color.LightYellow)
fun Sparkline.onLightBlue(): Sparkline = bg(Color.LightBlue)
fun Sparkline.onLightMagenta(): Sparkline = bg(Color.LightMagenta)
fun Sparkline.onLightCyan(): Sparkline = bg(Color.LightCyan)
fun Sparkline.onWhite(): Sparkline = bg(Color.White)

// Modifiers
fun Sparkline.bold(): Sparkline = addModifier(Modifier.BOLD)
fun Sparkline.dim(): Sparkline = addModifier(Modifier.DIM)
fun Sparkline.italic(): Sparkline = addModifier(Modifier.ITALIC)
fun Sparkline.underlined(): Sparkline = addModifier(Modifier.UNDERLINED)
fun Sparkline.reversed(): Sparkline = addModifier(Modifier.REVERSED)

// ============================================================================
// Dataset Stylize Extensions
// ============================================================================

fun Dataset.fg(color: Color): Dataset = style(getStyle().fg(color))
fun Dataset.bg(color: Color): Dataset = style(getStyle().bg(color))
fun Dataset.addModifier(modifier: Modifier): Dataset = style(getStyle().addModifier(modifier))
fun Dataset.removeModifier(modifier: Modifier): Dataset = style(getStyle().removeModifier(modifier))

// Foreground colors
fun Dataset.black(): Dataset = fg(Color.Black)
fun Dataset.red(): Dataset = fg(Color.Red)
fun Dataset.green(): Dataset = fg(Color.Green)
fun Dataset.yellow(): Dataset = fg(Color.Yellow)
fun Dataset.blue(): Dataset = fg(Color.Blue)
fun Dataset.magenta(): Dataset = fg(Color.Magenta)
fun Dataset.cyan(): Dataset = fg(Color.Cyan)
fun Dataset.gray(): Dataset = fg(Color.Gray)
fun Dataset.darkGray(): Dataset = fg(Color.DarkGray)
fun Dataset.lightRed(): Dataset = fg(Color.LightRed)
fun Dataset.lightGreen(): Dataset = fg(Color.LightGreen)
fun Dataset.lightYellow(): Dataset = fg(Color.LightYellow)
fun Dataset.lightBlue(): Dataset = fg(Color.LightBlue)
fun Dataset.lightMagenta(): Dataset = fg(Color.LightMagenta)
fun Dataset.lightCyan(): Dataset = fg(Color.LightCyan)
fun Dataset.white(): Dataset = fg(Color.White)

// Background colors
fun Dataset.onBlack(): Dataset = bg(Color.Black)
fun Dataset.onRed(): Dataset = bg(Color.Red)
fun Dataset.onGreen(): Dataset = bg(Color.Green)
fun Dataset.onYellow(): Dataset = bg(Color.Yellow)
fun Dataset.onBlue(): Dataset = bg(Color.Blue)
fun Dataset.onMagenta(): Dataset = bg(Color.Magenta)
fun Dataset.onCyan(): Dataset = bg(Color.Cyan)
fun Dataset.onGray(): Dataset = bg(Color.Gray)
fun Dataset.onDarkGray(): Dataset = bg(Color.DarkGray)
fun Dataset.onLightRed(): Dataset = bg(Color.LightRed)
fun Dataset.onLightGreen(): Dataset = bg(Color.LightGreen)
fun Dataset.onLightYellow(): Dataset = bg(Color.LightYellow)
fun Dataset.onLightBlue(): Dataset = bg(Color.LightBlue)
fun Dataset.onLightMagenta(): Dataset = bg(Color.LightMagenta)
fun Dataset.onLightCyan(): Dataset = bg(Color.LightCyan)
fun Dataset.onWhite(): Dataset = bg(Color.White)

// Modifiers
fun Dataset.bold(): Dataset = addModifier(Modifier.BOLD)
fun Dataset.dim(): Dataset = addModifier(Modifier.DIM)
fun Dataset.italic(): Dataset = addModifier(Modifier.ITALIC)
fun Dataset.underlined(): Dataset = addModifier(Modifier.UNDERLINED)
fun Dataset.reversed(): Dataset = addModifier(Modifier.REVERSED)

// ============================================================================
// Axis Stylize Extensions
// ============================================================================

fun Axis.fg(color: Color): Axis = style(getStyle().fg(color))
fun Axis.bg(color: Color): Axis = style(getStyle().bg(color))
fun Axis.addModifier(modifier: Modifier): Axis = style(getStyle().addModifier(modifier))
fun Axis.removeModifier(modifier: Modifier): Axis = style(getStyle().removeModifier(modifier))

// Foreground colors
fun Axis.black(): Axis = fg(Color.Black)
fun Axis.red(): Axis = fg(Color.Red)
fun Axis.green(): Axis = fg(Color.Green)
fun Axis.yellow(): Axis = fg(Color.Yellow)
fun Axis.blue(): Axis = fg(Color.Blue)
fun Axis.magenta(): Axis = fg(Color.Magenta)
fun Axis.cyan(): Axis = fg(Color.Cyan)
fun Axis.gray(): Axis = fg(Color.Gray)
fun Axis.darkGray(): Axis = fg(Color.DarkGray)
fun Axis.lightRed(): Axis = fg(Color.LightRed)
fun Axis.lightGreen(): Axis = fg(Color.LightGreen)
fun Axis.lightYellow(): Axis = fg(Color.LightYellow)
fun Axis.lightBlue(): Axis = fg(Color.LightBlue)
fun Axis.lightMagenta(): Axis = fg(Color.LightMagenta)
fun Axis.lightCyan(): Axis = fg(Color.LightCyan)
fun Axis.white(): Axis = fg(Color.White)

// Background colors
fun Axis.onBlack(): Axis = bg(Color.Black)
fun Axis.onRed(): Axis = bg(Color.Red)
fun Axis.onGreen(): Axis = bg(Color.Green)
fun Axis.onYellow(): Axis = bg(Color.Yellow)
fun Axis.onBlue(): Axis = bg(Color.Blue)
fun Axis.onMagenta(): Axis = bg(Color.Magenta)
fun Axis.onCyan(): Axis = bg(Color.Cyan)
fun Axis.onGray(): Axis = bg(Color.Gray)
fun Axis.onDarkGray(): Axis = bg(Color.DarkGray)
fun Axis.onLightRed(): Axis = bg(Color.LightRed)
fun Axis.onLightGreen(): Axis = bg(Color.LightGreen)
fun Axis.onLightYellow(): Axis = bg(Color.LightYellow)
fun Axis.onLightBlue(): Axis = bg(Color.LightBlue)
fun Axis.onLightMagenta(): Axis = bg(Color.LightMagenta)
fun Axis.onLightCyan(): Axis = bg(Color.LightCyan)
fun Axis.onWhite(): Axis = bg(Color.White)

// Modifiers
fun Axis.bold(): Axis = addModifier(Modifier.BOLD)
fun Axis.dim(): Axis = addModifier(Modifier.DIM)
fun Axis.italic(): Axis = addModifier(Modifier.ITALIC)
fun Axis.underlined(): Axis = addModifier(Modifier.UNDERLINED)
fun Axis.reversed(): Axis = addModifier(Modifier.REVERSED)
