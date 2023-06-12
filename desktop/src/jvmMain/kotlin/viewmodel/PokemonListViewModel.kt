package viewmodel

import com.zyf.pokemon.utils.NetworkResource
import com.zyf.pokemon.utils.extractId
import http.PKRetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.PokemonResult
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import retrofit2.HttpException


class PokemonListViewModel : ViewModel(){

    // 宝可梦列表数据流  currentResult是为了保证从详情页面返回时不重新加载数据
    var currentResult = MutableStateFlow<List<PokemonResult>>(emptyList())




    var page = 0

    var limit = 20
    fun getPokemon(searchString: String?,page: Int) {
        viewModelScope.launch {
            if (page == 0){
                limit = 20
            }else {
                limit *= page
            }
            val data = PKRetrofitClient.getPokemonApi().getPokemons(limit,0)
            val filteredData = if (searchString != null) {
                data.results.filter { it.name.contains(searchString, true) }
            } else {
                data.results
            }

            currentResult.emit(filteredData)
        }
    }


    fun getSinglePokemon(url: String) = flow {
        val id = url.extractId()
        emit(NetworkResource.Loading)
        emit(safeApiCall {
            PKRetrofitClient.getPokemonApi().getSinglePokemon(id)
        })
    }

    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): NetworkResource<T> {
        return withContext(Dispatchers.IO) {
            try {
                NetworkResource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        NetworkResource.Failure(
                            false,
                            throwable.code(),
                            throwable.response()?.errorBody()
                        )
                    }
                    else -> {
                        NetworkResource.Failure(true, null, null)
                    }
                }
            }
        }
    }
}