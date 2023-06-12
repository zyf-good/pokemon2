package ui.common

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp

import kotlinx.coroutines.delay

/**
 * @author zengyifeng
 * @date createDate:2023-04-08
 * @brief description
 */
@Composable
fun CommonCircularProgress(){
    CircularProgressIndicator(
        color = MaterialTheme.colors.secondary,
    )
}

@Composable
fun CommonAttributeCircularProgress(text:String,content:String,mProgress: Float,modifier: Modifier){
    Column(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val progress = remember {
            mutableStateOf(0.0f)
        }
        LaunchedEffect(true){
            var state = 0.0f
            while (state <= mProgress) {
                progress.value = state
                state += mProgress / 10f
                delay(50)
            }
        }

        Box(
            modifier = modifier
        ){
            Text(text = text,modifier = modifier.align(Alignment.Center))
            CircularProgressIndicator(progress = 1f,
                color = Color(0xFFffcba4),
                modifier = modifier
                    .align(Alignment.Center)
                    .size(150.dp, 150.dp)
            )
            CircularProgressIndicator(progress = progress.value,
                color = MaterialTheme.colors.secondary,
                modifier = modifier
                    .align(Alignment.Center)
                    .size(150.dp, 150.dp)
            )
        }
        Text(text = content, modifier = modifier.padding(top = 10.dp))
    }
}




@Composable
fun CommonLinearProgressIndicator(text:String,content:String,mProgress: Float,modifier: Modifier){
    Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
        Text("$content:$text",Modifier.width(150.dp))
        Spacer(Modifier.size(10.dp))
        LinearProgressIndicator(progress = mProgress, color = MaterialTheme.colors.secondary, backgroundColor = Color(0xFFffcba4), modifier = Modifier.padding(top = 7.dp))
    }

}



