package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import model.PokemonResult
import model.SinglePokemonResponse
import model.Sprites
import model.Stats
import moe.tlaster.precompose.ui.viewModel
import ui.common.AsyncImage
import ui.common.CommonCircularProgress
import ui.common.CommonLinearProgressIndicator
import ui.common.toast.ToastManager.showToast
import util.MAX_BASE_STATE
import util.NetworkResource
import util.getPicUrl
import viewmodel.PokemonListViewModel
import java.util.*


@Composable
fun FullScreenView(){
    val viewModel  = viewModel { PokemonListViewModel() }

    val listState by viewModel.currentResult.collectAsState()

    val state = rememberLazyListState()

    val scope = rememberCoroutineScope()

    var page by remember { mutableStateOf(1) }

    var searchString by rememberSaveable {
        mutableStateOf("")
    }

    LaunchedEffect(page,searchString) {
        viewModel.getPokemon(searchString,page)
    }
            Box (Modifier.fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 10.dp)){

                Column {
                    OutlinedTextField(value = searchString, onValueChange = {
                        searchString = it
                    }, placeholder = { Text(text = "Search Pokemon", color = Color.Gray) },
                        modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .padding(20.dp)
                        .focusTarget()
                        .background(
                            Color.White, RoundedCornerShape(16.dp)
                        ), singleLine = true, colors = LoginTextFieldColors()
                    )


                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(30.dp)
                        ,
                        state = state,
                        contentPadding = PaddingValues(14.dp, 0.dp, 14.dp, 80.dp),
                        verticalArrangement = Arrangement.spacedBy(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        items(listState.size){
                            ItemView(listState[it],viewModel)
                        }
                        item {
                            Button(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(30.dp),
                                onClick = {
                                    page+=1
                                },
                                shape = RoundedCornerShape(16.dp)
                            ){
                                Text("加载更多")
                            }
                        }
                    }
                }



                FloatingActionButton(
                    onClick = {
                        scope.launch {  state.scrollToItem(0,0) }
                    },
                    modifier = Modifier.padding(16.dp)              // Add some padding to the button
                    .align(Alignment.BottomEnd)  // Align the button to the bottom end of the screen
                ){
                  Text("Back to top",style = TextStyle(
                      fontSize = 10.sp,
                      color = Color.Black
                  ))
                }
            }


}


//总的item
@Composable
fun ItemView(item: PokemonResult,viewModel: PokemonListViewModel){
    Row (
        Modifier.fillMaxWidth()
            .height(200.dp)
            .background(
                Color.White, shape = RoundedCornerShape(16.dp)
            )
    ){
        PicAndName(item,Modifier
        .weight(1f)
        .fillMaxHeight())
        Attribute(item,Modifier.weight(3f)
            .fillMaxHeight(),viewModel)
    }



}

//图片和名称
@Composable
fun PicAndName(item: PokemonResult,modifier: Modifier){
    Column(
        modifier.padding(start = 10.dp)
    ) {
        Box(
            modifier = modifier
                .fillMaxHeight()
                .align(alignment = Alignment.CenterHorizontally)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ), contentAlignment = Alignment.Center
        ) {
            Spacer(modifier = modifier.height(10.dp))
            AsyncImage(
                modifier = modifier
                    .fillMaxHeight(),
                url = item.url.getPicUrl(),
                contentScale = ContentScale.Crop,
            )
        }
        Text(
            text = item.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            modifier = modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(top = 16.dp, bottom = 10.dp),
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

        )
    }
}

@Composable
fun Attribute(item: PokemonResult,modifier: Modifier,viewModel: PokemonListViewModel){
    val showLoading = remember {
        mutableStateOf(true)
    }
    var entity by remember {
        mutableStateOf(SinglePokemonResponse(Sprites(), emptyList(), 0, 0))
    }
    LaunchedEffect(key1 = item) {
        viewModel.getSinglePokemon(item.url).collect {
            when (it) {
                is NetworkResource.Success<*> -> {
                    showLoading.value = false
                    entity = it.value as SinglePokemonResponse
                }
                is NetworkResource.Failure -> {
                    showToast("There was an error loading the pokemon")
                }
                is NetworkResource.Loading -> {
                    showLoading.value = true
                }
            }
        }
    }

    if (showLoading.value) {
            CommonCircularProgress()
    }

    Row  (modifier = modifier.padding(top = 20.dp)){
        Column (){
            Text(text = entity.height.div(10.0).toString() + " metres",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ))
            Text(text = entity.weight.div(10.0).toString() + " kgs",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ))
        }
        LazyColumn (
            modifier = modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(14.dp, 0.dp, 14.dp),
        ){
            items(count = entity.stats.size) { count ->
                val item1 = entity.stats[count]
                AttributeDetailItemView(item1, modifier)
            }
        }


    }




}

@Composable
fun AttributeDetailItemView(stat: Stats, modifier: Modifier) {
    CommonLinearProgressIndicator(
        modifier = modifier.size(75.dp), content = stat.stat.name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }, mProgress =
        (stat.base_stat.toFloat() / MAX_BASE_STATE.toFloat()), text = stat.base_stat.toString()
    )


}


@Composable
private fun LoginTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.outlinedTextFieldColors(
        placeholderColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black,
        unfocusedBorderColor = Color.Black,
        focusedBorderColor = Color.Black,
        textColor = Color.Black,
        cursorColor = Color.Black,
    )
}