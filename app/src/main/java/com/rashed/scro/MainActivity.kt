package com.rashed.scro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rashed.scro.ui.theme.ScroTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel by viewModels<MainViewModel>()

        setContent {
            val state by viewModel.state.collectAsState()
            val keyboardController = LocalSoftwareKeyboardController.current
            val focusManager = LocalFocusManager.current

            ScroTheme {


                Scaffold(modifier = Modifier) { innerPadding ->
                    CompositionLocalProvider(
                        LocalLayoutDirection provides LayoutDirection.Ltr
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(top = 10.dp)
                                .padding(horizontal = 10.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                },
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                GradientButton(
                                    onClick = {
                                        keyboardController?.hide()
                                        viewModel.addEmptyCard()
                                        focusManager.clearFocus()
                                    },
                                    text = "Add Card",
                                    gradientColors = listOf(
                                        Color(0xff4E516F),
                                        Color(0xffC84B7F),
                                        Color(0xffE49773),
                                    )
                                )
                                GradientButton(
                                    onClick = {
                                        keyboardController?.hide()
                                        viewModel.resetCards()
                                        focusManager.clearFocus()
                                    },
                                    text = "Reset"
                                )

                            }
                            LazyVerticalGrid(
                                modifier = Modifier,
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                itemsIndexed(state.cards) { index, card ->
                                    Item(
                                        value = card.value,
                                        isSuspended = card.isSuspended,
                                        onGo = {
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                        },
                                        clear = {
                                            viewModel.updateCard(index, "")
                                        },
                                        valueChange = {
                                            viewModel.updateCard(index, it)
                                        },
                                        suspend = {
                                            viewModel.updateCardStatus(index, true)
                                        }
                                    )
                                }
                                item { }

                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Item(
    modifier: Modifier = Modifier,
    value: String,
    isSuspended: Boolean,
    onGo: () -> Unit,
    clear: () -> Unit,
    suspend: () -> Unit,
    valueChange: (String) -> Unit
) {
    var job: Job? = null
    var rotated by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val rotation by animateFloatAsState(
        targetValue = if (rotated) 180f else 0f,
        animationSpec = tween(500), label = ""
    )

    Card(
        modifier = modifier.alpha(if (isSuspended) .3f else 1f),
        onClick = {
            rotated = !rotated
            focusManager.clearFocus()
            job?.cancel()
            job = scope.launch {
                delay(30000)
                rotated = false
            }
        }) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = rotation
                    }
            )
            AnimatedVisibilityComponent(
                visible = rotated,
                modifier = Modifier.align(Alignment.BottomCenter),
                content = @Composable {
                    TextField(
                        enabled = !isSuspended,
                        value = value,
                        onValueChange = valueChange,
                        textStyle = TextStyle(
                            textDirection = TextDirection.Ltr,
                            fontSize = 60.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        ), modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(20.dp)),

                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(onGo = {
                            onGo()
                            job?.cancel()
                            scope.launch {
                                delay(2000)
                                rotated = false
                            }
                        }),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            disabledTextColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.White
                        )
                    )

                })

            AnimatedVisibilityComponent(
                visible = rotated,
                modifier = Modifier.align(Alignment.TopEnd),
                content = {
                    IconButton(
                        onClick = { clear() },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Gray.copy(
                                alpha = .5f
                            )
                        ),
                    ) {
                        Icon(imageVector = Icons.Outlined.Clear, contentDescription = "")
                    }
                })
            AnimatedVisibilityComponent(
                visible = value.isNotEmpty() && rotated,
                modifier = Modifier.align(Alignment.TopStart),
                content = {
                    IconButton(
                        onClick = {
                            suspend()
                            rotated = false
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Gray.copy(
                                alpha = .5f
                            )
                        ),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_remove_circle_outline_24),
                            contentDescription = ""
                        )
                    }
                })
        }
    }
}

@Composable
fun AnimatedVisibilityComponent(
    modifier: Modifier = Modifier,
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        exit = fadeOut(),
        enter = fadeIn(animationSpec = tween(delayMillis = 200, durationMillis = 100)),
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun GradientButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    gradientColors: List<Color> = listOf(
        Color(0xffE49773),
        Color(0xffC84B7F),
        Color(0xff4E516F),
    )
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent // Set to Transparent to apply gradient
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .drawWithCache {
                val brush = Brush.horizontalGradient(gradientColors)
                onDrawBehind {
                    drawRoundRect(
                        brush,
                        cornerRadius = CornerRadius(10.dp.toPx())
                    )
                }
            }
    ) {
        Text(text = text, color = Color.White)
    }
}