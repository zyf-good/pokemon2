package ui.common

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import org.succlz123.lib.imageloader.ImageAsyncImageFile
import org.succlz123.lib.imageloader.ImageAsyncImageUrl
import org.succlz123.lib.imageloader.ImageRes
import org.succlz123.lib.imageloader.core.ImageCallback


@Composable
fun AsyncImage(
    modifier: Modifier,
    url: String?,
    placeHolderUrl: String? = "image/ic_pokmon.jpeg",
    errorUrl: String? = "image/ic_pokmon.jpeg",
    contentScale: ContentScale = ContentScale.Crop
) {
    val imgUrl = url ?: "image/ic_pokmon.jpeg"
    if (imgUrl.startsWith("http")) {
        ImageAsyncImageUrl(url = imgUrl, imageCallback = ImageCallback(placeHolderView = {
            placeHolderUrl?.let {
                Image(
                    painter = painterResource(placeHolderUrl),
                    contentDescription = imgUrl,
                    modifier = modifier,
                    contentScale = contentScale
                )
            }
        }, errorView = {
            errorUrl?.let {
                Image(
                    painter = painterResource(errorUrl),
                    contentDescription = imgUrl,
                    modifier = modifier,
                    contentScale = contentScale
                )
            }
        }) {
            Image(
                painter = it, contentDescription = imgUrl, modifier = modifier, contentScale = contentScale
            )
        })
    } else if (imgUrl.startsWith("/") || imgUrl.contains(":\\")) {
        ImageAsyncImageFile(filePath = imgUrl, imageCallback = ImageCallback(placeHolderView = {
            placeHolderUrl?.let {
                Image(
                    painter = painterResource(placeHolderUrl),
                    contentDescription = imgUrl,
                    modifier = modifier,
                    contentScale = contentScale
                )
            }
        }, errorView = {
            errorUrl?.let {
                Image(
                    painter = painterResource(errorUrl),
                    contentDescription = imgUrl,
                    modifier = modifier,
                    contentScale = contentScale
                )
            }
        }) {
            Image(
                painter = it, contentDescription = imgUrl, modifier = modifier, contentScale = contentScale
            )
        })
    } else {
        ImageRes(resName = imgUrl, imageCallback = ImageCallback(placeHolderView = {
            placeHolderUrl?.let {
                Image(
                    painter = painterResource(placeHolderUrl),
                    contentDescription = imgUrl,
                    modifier = modifier,
                    contentScale = contentScale
                )
            }
        }, errorView = {
            errorUrl?.let {
                Image(
                    painter = painterResource(errorUrl),
                    contentDescription = imgUrl,
                    modifier = modifier,
                    contentScale = contentScale
                )
            }
        }) {
            Image(
                painter = it, contentDescription = imgUrl, modifier = modifier, contentScale = contentScale
            )
        })
    }
}